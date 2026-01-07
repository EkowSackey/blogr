package org.example.blogr.services;

import com.mongodb.client.MongoClient;
import org.example.blogr.Config.MongoConfig;
import org.example.blogr.domain.Post;
import org.example.blogr.domain.Tag;
import org.example.blogr.exceptions.PostNotFoundException;
import org.example.blogr.repositories.PostRepository;

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
}
