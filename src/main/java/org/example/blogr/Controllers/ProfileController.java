package org.example.blogr.Controllers;

import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.example.blogr.Utils.CacheUtil;
import org.example.blogr.Utils.ContextUtil;
import org.example.blogr.Utils.ServiceLocator;
import org.example.blogr.Utils.Switcher;
import org.example.blogr.components.PostListCell;
import org.example.blogr.domain.Post;
import org.example.blogr.services.UserService;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;

public class ProfileController {
    public FontIcon homeButton;
    public FontIcon searchButton;
    public Text username;
    public Text numberOfUserPosts;
    public ListView<Post> userPostsList;

    @FXML public Button logoutButton;

    ContextUtil context = ContextUtil.getInstance();
    private final UserService userService = ServiceLocator.getUserService();
    private List<Post> userPosts;

    public void initialize(){
        username.setText(context.getCurrentUser().username());
        userPosts = context.getUserPosts();
        numberOfUserPosts.setText(String.valueOf(userPosts.size()));
        
        // Use PostListCell
        userPostsList.setCellFactory(param -> {
            PostListCell cell = new PostListCell();
            cell.setOnMouseClicked(event -> {
                if (!cell.isEmpty() && cell.getItem() != null) {
                    context.setCurrentPost(cell.getItem());
                    Switcher.switchScreen(event, Screen.DETAIL);
                }
            });
            return cell;
        });

        displayPosts();
    }
    public void switchToHome(Event event) {
        Switcher.switchScreen(event, Screen.HOME);
    }

    public void switchToSearch(Event event) {
        Switcher.switchScreen(event, Screen.SEARCH);
    }

    public void switchToAdd(Event event) {
        Switcher.switchScreen(event, Screen.ADD);
    }

    public void switchToProfile(Event event) {
        Switcher.switchScreen(event, Screen.PROFILE);
    }

    public void displayPosts(){
        if (userPosts != null) {
            userPostsList.setItems(FXCollections.observableArrayList(userPosts.reversed()));
        }
    }

    public void logout(Event event){
        context.clearUserData();
        CacheUtil.invalidateAll();
        Switcher.switchScreen(event, Screen.LOGIN);
    }
}
