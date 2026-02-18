package org.example.blogr.services;

import org.bson.types.ObjectId;
import org.example.blogr.domain.Comment;
import org.example.blogr.domain.Post;
import org.example.blogr.domain.Review;
import org.example.blogr.domain.Tag;
import org.example.blogr.exceptions.PostNotFoundException;
import org.example.blogr.repositories.PostRepository;

import java.util.Date;
import java.util.List;

public class PostService {
    private final PostRepository postRepository;

    /**
     * Constructor injection for dependency injection.
     * @param postRepository the post repository to use
     */
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> getPosts(){
        return postRepository.getAllPosts();
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
        postRepository.updatePost(postId, "title", newPost.title());
        postRepository.updatePost(postId, "content", newPost.content());
        postRepository.updatePost(postId, "lastUpdate", newPost.lastUpdate());
    }

    public void deletePost(ObjectId postId){
        postRepository.deletePost(postId);
    }

    public List<Post> getUserPosts(ObjectId userId){
        List<Post> userPosts = prepo.getPostsByAuthor(userId);
        return userPosts != null ? userPosts : List.of();
    }

    public void addReview(double stars, String comment, ObjectId userId, ObjectId postId){
        ObjectId commentId = new ObjectId();
        Comment c = new Comment(commentId, comment, userId, postId, new Date() );
        Review r = new Review(stars, userId, postId);

        postRepository.addPostReview(postId, r);
        postRepository.addComment(c);
    }

    public void deleteComment(ObjectId postId, ObjectId commentId){
        postRepository.deleteCommentById(postId, commentId);
    }

    public Post getPostById(ObjectId id){
        return prepo.getPostById(id.toHexString());
    }
}
