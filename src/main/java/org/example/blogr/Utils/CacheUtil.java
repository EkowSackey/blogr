package org.example.blogr.Utils;

import org.example.blogr.domain.Post;

import java.util.HashMap;
import java.util.List;

public class CacheUtil {
    private static final HashMap<String, List<Post>> searchResults = new HashMap<>();

    public static void put(String searchTerm, List<Post> posts){
        searchResults.put(searchTerm, posts);
    }

    public static List<Post> get(String query){
        return searchResults.get(query.toLowerCase());
    }

    public static boolean contains(String query){
        return searchResults.containsKey(query.toLowerCase());
    }

}

