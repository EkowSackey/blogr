package org.example.blogr.Controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.controlsfx.validation.ValidationSupport;
import org.example.blogr.Utils.AlertErrorDisplay;
import org.example.blogr.Utils.ErrorDisplay;
import org.example.blogr.Utils.Switcher;
import org.example.blogr.Utils.ValidationUtils;
import org.example.blogr.domain.Post;
import org.example.blogr.domain.User;
import org.example.blogr.services.PostService;
import org.example.blogr.services.UserService;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;
import java.util.List;

public class SearchController {
    public FontIcon homeButton;
    public FontIcon searchButton;
    public Text displayName;
    public ComboBox<String> comboBox;
    public TextField searchField;

    private final ValidationSupport vs = new ValidationSupport();
    private ErrorDisplay strategy;

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

        if (!ValidationUtils.ensureValidOrShow(vs, strategy)){
            return;
        }
        String searchCategory = comboBox.getValue();
        String searchTerm = searchField.getText();

        PostService postService = new PostService();
        UserService userService = new UserService();
        List<Post> posts = new ArrayList<>();
        List<User> users = new ArrayList<>();

        switch (searchCategory){
            case "Posts" -> {
                posts.addAll(postService.getPostsByTitle(searchTerm));
                System.out.println(posts);
            }
            case  "Tags" -> {
                posts.addAll(postService.getPostsByTag(searchTerm));
                System.out.println(posts);
            }
            case "Users" -> {
                users.addAll(userService.findUsersByUsername(searchTerm));
                System.out.println(users);
            }

        }
        
    }
}
