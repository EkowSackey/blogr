package org.example.blogr.Controllers;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.example.blogr.Utils.ContextUtil;
import org.example.blogr.Utils.Switcher;
import org.example.blogr.domain.Post;
import org.example.blogr.services.PostService;


import java.util.List;


public class HomeController {

    @FXML public Text displayName;

    ContextUtil context = ContextUtil.getInstance();

    List<Post> allPosts;
    private final PostService postService = new PostService();

    public void initialize(){

        setDisplayName();
        allPosts = postService.getPosts();
        displayPosts();
    }

    public void setDisplayName(){
        displayName.setText(context.getCurrentUser().username());
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

    public void displayPosts(){
        for (Post p: allPosts){
            System.out.println(p);
        }

    }
}
