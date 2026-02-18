package org.example.blogr;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.example.blogr.Config.MongoConfig;
import org.example.blogr.Utils.CacheUtil;

import java.io.IOException;
import java.util.Objects;

public class BlogrApplication extends Application {

    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 700;
    private static final int MIN_WINDOW_WIDTH = 1000;
    private static final int MIN_WINDOW_HEIGHT = 600;

    @Override
    public void start(Stage stage) throws IOException {
        CacheUtil.invalidateAll();
        FXMLLoader fxmlLoader = new FXMLLoader(BlogrApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), WINDOW_WIDTH, WINDOW_HEIGHT);
        String css = Objects.requireNonNull(getClass().getResource("application.css")).toExternalForm();
        scene.getStylesheets().add(css);
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/r.png")));
        stage.getIcons().add(icon);
        stage.setTitle("blogr");
        stage.setMinWidth(MIN_WINDOW_WIDTH);
        stage.setMinHeight(MIN_WINDOW_HEIGHT);
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        MongoConfig.close();
        super.stop();
    }
}
