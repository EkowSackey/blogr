package org.example.blogr.Controllers;

import javafx.event.Event;
import javafx.scene.text.Text;
import org.example.blogr.Utils.ContextUtil;
import org.example.blogr.Utils.Switcher;
import org.example.blogr.domain.Post;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;

public class ProfileController {
    public FontIcon homeButton;
    public FontIcon searchButton;
    public Text username;
    public Text numberOfUserPosts;

    ContextUtil context = ContextUtil.getInstance();
    private List<Post> userPosts;

    public void initialize(){
        username.setText(context.getCurrentUser().username());
        userPosts = context.getUserPosts();
        numberOfUserPosts.setText(String.valueOf(userPosts.size()));
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
        for (Post p: userPosts){
            System.out.println(p);
        }
    }
}
