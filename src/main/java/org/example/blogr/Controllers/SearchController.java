package org.example.blogr.Controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.bson.types.ObjectId;
import org.controlsfx.validation.ValidationSupport;
import org.example.blogr.Utils.*;
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
    private final PostService postService = new PostService();
    private final UserService userService = new UserService();

    private final ValidationSupport vs = new ValidationSupport();
    public ListView<Object> resultList;
    private ErrorDisplay strategy;


    ContextUtil context = ContextUtil.getInstance();
    public void initialize(){

        comboBox.getItems().add("Users");
        comboBox.getItems().add("Posts");
        comboBox.getItems().add("Tags");
        ValidationUtils.init(vs);
        ValidationUtils.registerRequired(vs, searchField, "Search term is required" );

        strategy = new AlertErrorDisplay();
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

        Instant start = Instant.now();

        if (!ValidationUtils.ensureValidOrShow(vs, strategy)){
            return;
        }
        String searchCategory = comboBox.getValue();
        String searchTerm = searchField.getText();

        switch (searchCategory){
            case "Posts" -> {
                String cacheKey = searchTerm + "_" + searchCategory;
                if (CacheUtil.contains(cacheKey)){

                    System.out.println("Cache hit!");
                    posts.addAll(Objects.requireNonNull(CacheUtil.get(cacheKey)));
                    break;
                }
                posts.addAll(postService.getPostsByTitle(searchTerm));
                CacheUtil.put(searchTerm + "_" + searchCategory, posts);
            }
            case  "Tags" -> {
                String cacheKey = searchTerm + "_" + searchCategory;
                if (CacheUtil.contains(cacheKey)){

                    System.out.println("Cache hit!");
                    posts.addAll(Objects.requireNonNull(CacheUtil.get(cacheKey)));
                    break;
                }
                posts.addAll(postService.getPostsByTag(searchTerm));
                CacheUtil.put(searchTerm + "_" + searchCategory, posts);
            }
            case "Users" -> {
                String cacheKey = searchTerm + "_" + searchCategory;
                if (CacheUtil.contains(cacheKey)){

                    System.out.println("Cache hit!");
                    posts.addAll(Objects.requireNonNull(CacheUtil.get(cacheKey)));
                    break;
                }

                List<User> users = userService.findUsersByUsername(searchTerm);
                for (User u : users){
                    ObjectId userId = userService.findUserByUsername(u.username());
                    var userPosts = postService.getUserPosts(userId);
                    posts.addAll(userPosts);
                }
                CacheUtil.put(searchTerm + "_" + searchCategory, posts);
            }
        }

        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start,end);
        System.out.println("Query time after Optimization: " + timeElapsed);

        displayResults();
    }

    public void displayResults(){

        for (Post p: posts){
            Text title = new Text(String.format("Title: %s",p.title()));
            title.setFont(Font.font("Chiller", FontWeight.BOLD, 26));
            Text author = new Text(String.format("Author: %s",userService.getMyProfile(p.authorId()).username()));
            author.setFont(Font.font("Monospaced", 12));
            Text dateCreated = new Text(String.format("Date Created: %s", p.dateCreated()));
            dateCreated.setFont(Font.font("Monospaced", 12));

            VBox pane = new VBox();
            pane.setSpacing(10);
            pane.getChildren().add(title);
            pane.getChildren().add(author);
            pane.getChildren().add(dateCreated);
            pane.setOnMouseClicked(mouseEvent -> {
                context.setCurrentPost(p);
                Switcher.switchScreen(mouseEvent, Screen.DETAIL);
            });

            resultList.getItems().add(pane);
        }
    }
}
