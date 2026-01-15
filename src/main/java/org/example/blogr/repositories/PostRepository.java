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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Updates.push;

public class PostRepository {
    private final MongoCollection<Document> collection;

    public PostRepository(MongoClient client){
        this.collection = client.getDatabase("blogrdb").getCollection("posts");
    }


    private Document toDoc(Post post) {
        Document d = new Document()
                .append("postId", post.postId())
                .append("title", post.title())
                .append("content", post.content())
                .append("dateCreated", post.dateCreated())
                .append("lastUpdate", post.lastUpdate())
                .append("authorId", post.authorId())
                .append("commentCount", post.commentCount())
                .append("avgRating", post.avgRating());

        List<Document> tagDocs = post.tags() == null ? List.of() :
                post.tags().stream()
                        .filter(Objects::nonNull)
                        .map(tag -> new Document("name", tag.name()))
                        .toList();
        d.append("tags", tagDocs);

        List<Document> reviewDocs = post.reviews() == null ? List.of() :
                post.reviews().stream()
                        .filter(Objects::nonNull)
                        .map(review -> {
                            Document rd = new Document("stars", review.stars());
                            ObjectId userId = review.userId();
                            ObjectId postId = review.postId();

                            rd.append("userId", userId).append("postId", postId);

                            return rd;
                        })
                        .toList();
        d.append("reviews", reviewDocs);

        List<Document> commentDocs = post.comments() == null ? List.of() :
                post.comments().stream()
                        .filter(Objects::nonNull)
                        .map(c -> new Document()
                                .append("content", c.content())
                                .append("authorId", c.authorId())
                                .append("parentId", c.parentId())
                                .append("createdAt", c.createdAt()))
                        .toList();
        d.append("comments", commentDocs);

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
                                doc.getObjectId("commentId"),
                                doc.getString("content"),
                                doc.getObjectId("authorId"),
                                doc.getObjectId("parentId"),
                                doc.getDate("createdAt")
                        )
                ).toList();
        return new Post(
                d.getObjectId("postId"),
                d.getString("title"),
                d.getString("content"),
                d.getDate("dateCreated"),
                d.getDate("lastUpdate"),
                d.getObjectId("authorId"),
                comments,
                comments.size(),
                tags,
                reviews,
                0
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

    public List<Post> getPostsByAuthor(ObjectId authorId){
        Bson filter = Filters.eq("authorId", authorId);
        FindIterable<Document> docs = collection.find(filter);
        List<Post> posts = new ArrayList<>();
        docs.forEach( doc -> posts.add(toDomain(doc)));
        return posts;
    }

    public void updatePost(ObjectId id, String field, Object value){
        Bson filter = Filters.eq("postId", id);
        Bson updates  = Updates.set(field, value);

        UpdateResult result = collection.updateOne(filter, updates);
        System.out.println("Modified fields:" + result.getModifiedCount());
    }


    public void addPostReview(ObjectId postId, Review review) {
        Document newReview = new Document()
                .append("stars", review.stars())
                .append("postId", postId)
                .append("userId", review.userId());

        Bson filter = Filters.eq("postId", postId);


        List<Bson> pipeline = Arrays.asList(
                new Document("$set", new Document("reviewsNew",
                        new Document("$concatArrays", Arrays.asList("$reviews", Arrays.asList(newReview)))
                )),
                new Document("$set", new Document("reviews", "$reviewsNew")),
                new Document("$set", new Document("avgRating",
                        new Document("$cond", Arrays.asList(
                                new Document("$gt", Arrays.asList(
                                        new Document("$size", "$reviewsNew"), 0
                                )),
                                new Document("$avg", "$reviewsNew.stars"),
                                0
                        ))
                )),
                new Document("$unset", "reviewsNew")
        );

        UpdateResult result = collection.updateOne(filter, pipeline);
        System.out.println("modified docs: " + result.getModifiedCount());
    }



    public void addComment(Comment comment) {
        ObjectId postId = comment.parentId();
        ObjectId commentId = new ObjectId();

        Document newComment = new Document("commentId", commentId)
                .append("content", comment.content())
                .append("authorId", comment.authorId())
                .append("parentId", postId)
                .append("createdAt", comment.createdAt());

        Bson filter = Filters.eq("postId", postId);

        List<Bson> pipeline = Arrays.asList(
                new Document("$set", new Document("commentsNew",
                        new Document("$concatArrays", Arrays.asList("$comments", Arrays.asList(newComment)))
                )),
                new Document("$set", new Document("comments", "$commentsNew")),
                new Document("$set", new Document("commentCount",
                        new Document("$size", "$commentsNew")
                )),
                new Document("$unset", "commentsNew")
        );

        UpdateResult result = collection.updateOne(filter, pipeline);
        if (result.getModifiedCount() == 0) {
            throw new IllegalStateException("Post not found or comment not added");
        }
        System.out.println("added comment with id: " + commentId + " to post with id: " + postId);
    }




    public void deletePost(ObjectId id){
        Bson filter = Filters.eq("postId", id);
        collection.findOneAndDelete(filter);
        System.out.println("Post deleted with id: " + id);
    }

    public void deleteCommentById(ObjectId postId, ObjectId commentId){
        Bson filter = Filters.eq("postId", postId);
        Bson update = Updates.pull("comments", new Document("commentId", commentId));

        UpdateResult result = collection.updateOne(filter, update);
        System.out.println("deleted: " + result.getModifiedCount());
    }
}
