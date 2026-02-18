package org.example.blogr.Controllers;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.bson.types.ObjectId;
import org.controlsfx.validation.ValidationSupport;
import org.example.blogr.Utils.*;
import org.example.blogr.components.PostListCell;
import org.example.blogr.domain.Post;
import org.example.blogr.domain.User;
import org.example.blogr.services.PostService;
import org.example.blogr.services.UserService;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchController {
    public FontIcon homeButton;
    public FontIcon searchButton;
    public ComboBox<String> comboBox;
    public TextField searchField;

    private final List<Post> posts = new ArrayList<>();
    private final PostService postService = ServiceLocator.getPostService();
    private final UserService userService = ServiceLocator.getUserService();

    private final ValidationSupport vs = new ValidationSupport();
    public ListView<Post> resultList;
    private ErrorDisplay strategy;


    ContextUtil context = ContextUtil.getInstance();
    public void initialize(){

        comboBox.getItems().addAll("Users", "Posts", "Tags");
        comboBox.setValue("Posts"); // Set default value
        
        ValidationUtils.init(vs);
        ValidationUtils.registerRequired(vs, searchField, "Search term is required" );

        strategy = new AlertErrorDisplay();
        
        // Use custom cell factory for better display
        resultList.setCellFactory(param -> {
            PostListCell cell = new PostListCell();
            cell.setOnMouseClicked(event -> {
                if (!cell.isEmpty() && cell.getItem() != null) {
                    context.setCurrentPost(cell.getItem());
                    Switcher.switchScreen(event, Screen.DETAIL);
                }
            });
            return cell;
        });
        
        resultList.setPlaceholder(new Label("No results found"));
    }

    public void switchToHome(MouseEvent mouseEvent) {
        Switcher.switchScreen(mouseEvent, Screen.HOME);
    }

    public void switchToSearch(MouseEvent mouseEvent) {
        Switcher.switchScreen(mouseEvent, Screen.SEARCH);
    }

    public void switchToAdd(MouseEvent mouseEvent) {
        Switcher.switchScreen(mouseEvent, Screen.ADD);
    }

    public void switchToProfile(MouseEvent mouseEvent) {
        Switcher.switchScreen(mouseEvent, Screen.PROFILE);
    }

    public void search(ActionEvent actionEvent) {

        if (!ValidationUtils.ensureValidOrShow(vs, strategy)){
            return;
        }
        
        String searchCategory = comboBox.getValue();
        String searchTerm = searchField.getText();
        
        // Clear previous results
        posts.clear();
        resultList.getItems().clear();
        resultList.setPlaceholder(new Label("Searching..."));
        
        Task<List<Post>> searchTask = new Task<>() {
            @Override
            protected List<Post> call() throws Exception {
                Instant start = Instant.now();
                List<Post> results = new ArrayList<>();
                
                String cacheKey = searchTerm + "_" + searchCategory;
                if (CacheUtil.contains(cacheKey)){
                    System.out.println("Cache hit!");
                    return (List<Post>) CacheUtil.get(cacheKey);
                }

                switch (searchCategory){
                    case "Posts" -> {
                        results.addAll(postService.getPostsByTitle(searchTerm));
                    }
                    case  "Tags" -> {
                        results.addAll(postService.getPostsByTag(searchTerm));
                    }
                    case "Users" -> {
                        List<User> users = userService.findUsersByUsername(searchTerm);
                        for (User u : users){
                            ObjectId userId = userService.findUserByUsername(u.username());
                            var userPosts = postService.getUserPosts(userId);
                            results.addAll(userPosts);
                        }
                    }
                }
                
                CacheUtil.put(cacheKey, results);
                
                Instant end = Instant.now();
                Duration timeElapsed = Duration.between(start,end);
                System.out.println("Query time: " + timeElapsed);
                
                return results;
            }
        };

        searchTask.setOnSucceeded(event -> {
            List<Post> results = searchTask.getValue();
            if (results != null && !results.isEmpty()) {
                posts.addAll(results);
                resultList.getItems().addAll(results);
            } else {
                resultList.setPlaceholder(new Label("No results found"));
            }
        });

        searchTask.setOnFailed(event -> {
            Throwable e = searchTask.getException();
            // ValidationUtils.showServerError(strategy, e.getMessage()); // Don't show alert for not found
            resultList.setPlaceholder(new Label("Error: " + e.getMessage()));
            e.printStackTrace();
        });

        new Thread(searchTask).start();
    }
}
