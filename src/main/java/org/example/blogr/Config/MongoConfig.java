package org.example.blogr.Config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MongoConfig {

    private static MongoClient client;
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = MongoConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find application.properties");
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Error loading application.properties", ex);
        }
    }

    public static synchronized MongoClient getClient() {
        if (client == null) {
            String uri = properties.getProperty("mongodb.uri");
            if (uri == null) {
                throw new RuntimeException("mongodb.uri not specified in application.properties");
            }
            client = MongoClients.create(uri);
        }
        return client;
    }

    public static String getDatabaseName() {
        return properties.getProperty("mongodb.database", "lab4");
    }

    public static synchronized void close() {
        if (client != null) {
            client.close();
            client = null;
        }
    }
}
