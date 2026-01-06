package org.example.blogr.Config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class MongoConfig {

    public static MongoClient getClient(){
        String uri = "mongodb://localhost:27017";
        try {
            return MongoClients.create(uri);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
