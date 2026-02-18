package org.example.blogr.Utils;

import com.mongodb.client.MongoClient;
import org.example.blogr.Config.MongoConfig;
import org.example.blogr.repositories.PostRepository;
import org.example.blogr.repositories.UserRepository;
import org.example.blogr.services.PostService;
import org.example.blogr.services.UserService;

public class ServiceLocator {

    private static UserService userService;
    private static PostService postService;

    private ServiceLocator() {
        // Private constructor to prevent instantiation
    }

    public static synchronized UserService getUserService() {
        if (userService == null) {
            MongoClient client = MongoConfig.getClient();
            String databaseName = MongoConfig.getDatabaseName();
            UserRepository userRepository = new UserRepository(client, databaseName);
            userService = new UserService(userRepository);
        }
        return userService;
    }

    public static synchronized PostService getPostService() {
        if (postService == null) {
            MongoClient client = MongoConfig.getClient();
            String databaseName = MongoConfig.getDatabaseName();
            PostRepository postRepository = new PostRepository(client, databaseName);
            postService = new PostService(postRepository);
        }
        return postService;
    }
}
