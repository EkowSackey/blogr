package org.example.blogr;

import com.mongodb.client.MongoClient;
import javafx.application.Application;
import org.example.blogr.Config.MongoConfig;

public class Launcher {
    public static void main(String[] args) {

        Application.launch(BlogrApplication.class, args);
    }
}
