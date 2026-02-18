package org.example.blogr.services;

import com.mongodb.client.MongoClient;
import org.bson.types.ObjectId;
import org.example.blogr.Config.MongoConfig;
import org.example.blogr.domain.Comment;
import org.example.blogr.domain.Post;
import org.example.blogr.domain.Review;
import org.example.blogr.domain.Tag;
import org.example.blogr.exceptions.PostNotFoundException;
import org.example.blogr.repositories.PostRepository;

import java.util.Date;
import java.util.List;

public class PostService {
    private final PostRepository prepo;

    /**
     * Default constructor that uses the production MongoDB configuration.
     * Used by the application in production.
     */
    public PostService() {
        MongoClient client = MongoConfig.getClient();
        this.prepo = new PostRepository(client);
    }

    /**
     * Constructor that allows specifying a database name (useful for testing).
     * @param databaseName the database name to use
     */
    public PostService(String databaseName) {
        MongoClient client = MongoConfig.getClient();
        this.prepo = new PostRepository(client, databaseName);
    }

    /**
     * Constructor injection for dependency injection (useful for testing with mocks).
     * @param prepo the post repository to use
     */
    public PostService(PostRepository prepo) {
        this.prepo = prepo;
    }

    public List<Post> getPosts(){
        return prepo.getAllPosts();
    }

    public List<Post> getPostsByTitle(String title){
        List<Post> posts = prepo.getPostsByTitle(title);
        return posts != null ? posts : List.of();
    }

    public  List<Post> getPostsByTag(String tag){
        Tag searchTag = new Tag(tag);
        List<Post> posts = prepo.getPostsByTag(searchTag);
        return posts != null ? posts : List.of();
    }

    public Post createPost(String title, String content, Date created,
                           Date updated, ObjectId author, List<Comment> comments,
                           List<Tag> tags, List<Review> reviews){
        ObjectId postId = new ObjectId();
        Post post = new Post(postId, title, content, created, updated, author, comments, tags, reviews);
        prepo.createPost(post);
        return post;
    }

    public void updatePost(ObjectId postId, Post newPost){
        prepo.updatePost(postId, "title", newPost.title());
        prepo.updatePost(postId, "content", newPost.content());
        prepo.updatePost(postId, "lastUpdate", newPost.lastUpdate());
    }

    public void deletePost(ObjectId postId){
        prepo.deletePost(postId);
    }

    public List<Post> getUserPosts(ObjectId userId){
        List<Post> userPosts = prepo.getPostsByAuthor(userId);
        return userPosts != null ? userPosts : List.of();
    }

    public void addReview(double stars, String comment, ObjectId userId, ObjectId postId){
        ObjectId commentId = new ObjectId();
        Comment c = new Comment(commentId, comment, userId, postId, new Date() );
        Review r = new Review(stars, userId, postId);

        prepo.addPostReview(postId, r);
        prepo.addComment(c);
    }

    public void deleteComment(ObjectId postId, ObjectId commentId){
        prepo.deleteCommentById(postId, commentId);
    }

    public Post getPostById(ObjectId id){
        return prepo.getPostById(id.toHexString());
    }
}
