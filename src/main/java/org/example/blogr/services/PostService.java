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
    private final MongoClient client = MongoConfig.getClient();
    private final PostRepository prepo = new PostRepository(client);

    public List<Post> getPosts(){
        return prepo.getAllPosts();
    }

    public List<Post> getPostsByTitle(String title){
        List<Post> posts = prepo.getPostsByTitle(title);

        if (posts == null){
            throw new PostNotFoundException("Your search term didn't match any posts");
        }

        return posts;
    }

    public  List<Post> getPostsByTag(String tag){
        Tag searchTag = new Tag(tag);
        List<Post> posts = prepo.getPostsByTag(searchTag);

        if (posts == null){
            throw new PostNotFoundException("No post with this tag.");
        }
        return posts;
    }

    public Post createPost(String title, String content, Date created,
                           Date updated, ObjectId author, List<Comment> comments,
                           List<Tag> tags, List<Review> reviews){
        Post post = new Post(null, title, content, created, updated, author, comments, comments.size(), tags, reviews, 0);
        prepo.createPost(post);
        return post;
    }

    public void updatePost(ObjectId postId, Post newPost){
        prepo.updatePost(postId, "title", String.format("%s (Edit)",newPost.title()));
        prepo.updatePost(postId, "content", String.format("%s %n%n <span>This post was last edited on %s </span> %n ", newPost.content(), newPost.lastUpdate()));
        prepo.updatePost(postId, "lastUpdate", newPost.lastUpdate());
    }

    public void deletePost(ObjectId postId){
        prepo.deletePost(postId);
    }

    public List<Post> getUserPosts(ObjectId userId){
        List<Post> userPosts = prepo.getPostsByAuthor(userId);

        if (userPosts != null){
            return userPosts;
        }

        throw new PostNotFoundException("User has no Posts");
    }

    public void addReview(double stars, String comment, ObjectId userId, ObjectId postId){
        Comment c = new Comment(null, comment, userId, postId, new Date() );
        Review r = new Review(stars, userId, postId);

        prepo.addPostReview(postId, r);
        prepo.addComment(c);
    }

    public void deleteComment(ObjectId postId, ObjectId commentId){
        prepo.deleteCommentById(postId, commentId);
    }
}
