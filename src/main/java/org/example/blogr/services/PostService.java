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
        List<Post> posts = postRepository.getPostsByTitle(title);

        if (posts.isEmpty()){
            throw new PostNotFoundException("Your search term didn't match any posts");
        }

        return posts;
    }

    public  List<Post> getPostsByTag(String tag){
        Tag searchTag = new Tag(tag);
        List<Post> posts = postRepository.getPostsByTag(searchTag);

        if (posts.isEmpty()){
            throw new PostNotFoundException("No post with this tag.");
        }
        return posts;
    }

    public Post createPost(String title, String content, Date created,
                           Date updated, ObjectId author, List<Comment> comments,
                           List<Tag> tags, List<Review> reviews){
        Post post = Post.createWithCalculatedFields(null, title, content, created, updated, author, comments, tags, reviews);
        postRepository.createPost(post);
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
        List<Post> userPosts = postRepository.getPostsByAuthor(userId);

        if (userPosts != null && !userPosts.isEmpty()){
            return userPosts;
        }

        throw new PostNotFoundException("User has no Posts");
    }

    public void addReview(double stars, String comment, ObjectId userId, ObjectId postId){
        Comment c = new Comment(null, comment, userId, postId, new Date() );
        Review r = new Review(stars, userId, postId);

        postRepository.addPostReview(postId, r);
        postRepository.addComment(c);
    }

    public void deleteComment(ObjectId postId, ObjectId commentId){
        postRepository.deleteCommentById(postId, commentId);
    }
}
