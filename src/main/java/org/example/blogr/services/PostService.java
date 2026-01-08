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

    public void createPost(String title, String content, Date created,
                           Date updated, ObjectId author, List<Comment> comments,
                           List<Tag> tags, List<Review> reviews){
        Post post = new Post(title, content, created, updated, author, comments, tags, reviews);
        prepo.createPost(post);
    }
}
