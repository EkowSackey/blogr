package org.example.blogr.repositories;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.example.blogr.domain.*;
import org.example.blogr.exceptions.PostNotFoundException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Updates.push;

public class PostRepository {
    private final MongoCollection<Document> collection;

    public PostRepository(MongoClient client){
        this.collection = client.getDatabase("blogrdb").getCollection("posts");
    }

    private Document toDoc(Post post){
        var d = new Document();
        d.put("title", post.title());
        d.put("content", post.content());
        d.put("dateCreated", post.dateCreated());
        d.put("lastUpdate", post.lastUpdate());
        d.put("authorId", post.authorId());
        d.put("comments", post.comments());
        d.put("tags", post.tags());
        d.put("reviews", post.reviews());

        return d;
    }

    private Post toDomain(Document d){
        List<Document> tagDocs = d.getList("tags", Document.class);
        List<Tag> tags = tagDocs.stream()
                .map(doc -> new Tag(doc.getString("name")))
                .toList();

        List<Document> reviewsDocs = d.getList("reviews", Document.class);
        List<Review> reviews = reviewsDocs.stream()
                .map(doc-> new Review(
                        doc.getDouble("stars"),
                        doc.getObjectId("userId"),
                        doc.getObjectId("postId")
                        )

                ).toList();

        List<Document> commentDocs = d.getList("comments", Document.class);
        List<Comment> comments = commentDocs.stream()
                .map(
                        doc -> new Comment(
                                doc.getString("content"),
                                doc.getObjectId("authorId"),
                                doc.getObjectId("parentId"),
                                new ArrayList<>(),
                                doc.getDate("createdAt")
                        )
                ).toList();
        return new Post(

                d.getString("title"),
                d.getString("content"),
                d.getDate("dateCreated"),
                d.getDate("lastUpdate"),
                d.getObjectId("authorId"),
                comments,
                tags,
                reviews
        );
    }

    public void createPost(Post post){
        var doc = toDoc(post);
        InsertOneResult result = collection.insertOne(doc);
        System.out.println("Inserted a document with id: "
                + Objects.requireNonNull(result.getInsertedId())
                .asObjectId().getValue());
    }

    public List<Post> getAllPosts(){
        FindIterable<Document> docs = collection.find();
        List<Post> allPosts = new ArrayList<>();
        docs.forEach( doc -> allPosts.add(toDomain(doc)));
        return allPosts;
    }

    public Post getPostById(String id){
        if (!ObjectId.isValid(id)) throw new IllegalArgumentException("Invalid ObjectId format");

        Bson filter = Filters.eq("_id", new ObjectId(id));
        Document doc = collection.find(filter).first();
        if (doc != null)
            return toDomain(doc);

        else throw new PostNotFoundException("Post with ID: " + id + " does not exist!");
    }

    public List<Post> getPostsByTag(Tag tag){
        Bson filter = Filters.eq("tags.name", tag.name());
        FindIterable<Document> docs = collection.find(filter);
        List<Post> postsByTag = new ArrayList<>();
        docs.forEach( doc -> postsByTag.add(toDomain(doc)));
        return postsByTag;
    }

    public List<Post> getPostsByTitle(String title){
        Bson filter = Filters.eq("title", Pattern.compile(Pattern.quote(title), Pattern.CASE_INSENSITIVE));
        FindIterable<Document> docs = collection.find(filter);
        List<Post> posts = new ArrayList<>();
        docs.forEach( doc -> posts.add(toDomain(doc)));
        return posts;
    }

    public List<Post> getPostsByAuthor(String authorId){
        Bson filter = Filters.eq("authorId", new ObjectId(authorId));
        FindIterable<Document> docs = collection.find(filter);
        List<Post> posts = new ArrayList<>();
        docs.forEach( doc -> posts.add(toDomain(doc)));
        return posts;
    }

    public void updatePost(String id, String field, String value){
        Bson filter = Filters.eq("_id", new ObjectId(id));
        Bson updates  = Updates.set(field, value);

        UpdateResult result = collection.updateOne(filter, updates);
        System.out.println("Modified fields:" + result.getModifiedCount());
    }

    public void addPostReview(String postId, Review review){
        Bson filter = Filters.eq("_id", new ObjectId(postId));
        UpdateResult result = collection.updateOne(filter, push("reviews", review));

    }

    public void addComment(Comment comment){
        ObjectId commentId = new ObjectId();

        Document commentDoc = new Document("_id", commentId)
                .append("content", comment.content() )
                .append("authorId", comment.authorId())
                .append("parentId", comment.parentId())
                .append("subComments", new ArrayList<Comment>())
                .append("createdAt", comment.createdAt());

        Bson filter = Filters.eq("_id", comment.parentId());
        UpdateResult result = collection.updateOne(filter, Updates.push("comments", commentDoc));

        if (result.getModifiedCount()==0){
            throw new IllegalStateException("Post not found or comment not added");
        }
        System.out.println("added comment with id: " + commentId);
    }

    public void addSubComment(String postId, Comment comment){
        ObjectId commentId = new ObjectId();

        Document commentDoc = new Document("_id", commentId)
                .append("content", comment.content() )
                .append("authorId", comment.authorId())
                .append("parentId", comment.parentId())
                .append("subComments", new ArrayList<Comment>())
                .append("createdAt", comment.createdAt());

        Bson filter = Filters.eq("_id", new ObjectId(postId));
        Bson update = Updates.push("comments.$[parent].subComments", commentDoc );
        UpdateOptions options = new UpdateOptions().arrayFilters(
                List.of(new Document("parent._id", comment.parentId()))
        );
        UpdateResult result = collection.updateOne(filter, update, options);


        if (result.getMatchedCount() == 0) {
            throw new IllegalStateException("Post not found: " + postId);
        }

        if (result.getModifiedCount()==0){
            throw new IllegalStateException("Parent comment not found at top level: " + comment.parentId());
        }
        System.out.println("added comment with id: " + commentId);
    }

    public void deletePost(String id){
        if (!ObjectId.isValid(id)) throw new IllegalArgumentException("Invalid ObjectId format");

        Bson filter = Filters.eq("_id", new ObjectId(id));
        collection.findOneAndDelete(filter);
    }

    public void deleteCommentById(String postId, String commentId){
        Bson filter = Filters.eq("_id", new ObjectId(postId));
        Bson update = Updates.pull("comments", new Document("_id", new ObjectId(commentId)));

        UpdateResult result = collection.updateOne(filter, update);
        System.out.println("deleted: " + result.getModifiedCount());
    }
}
