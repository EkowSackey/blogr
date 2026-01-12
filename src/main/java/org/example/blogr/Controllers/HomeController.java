package org.example.blogr.Controllers;

import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import org.example.blogr.Utils.Switcher;
import org.example.blogr.domain.Post;
import org.example.blogr.services.PostService;
import org.example.blogr.services.UserService;

import java.util.List;

public class HomeController {

    public ListView<String> postList;

    List<Post> allPosts;
    private final PostService postService = new PostService();
    private final UserService userService = new UserService();



    public void initialize(){

        allPosts = postService.getPosts();
        displayPosts();
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
            String title = p.title();
            String author = userService.getMyProfile(p.authorId()).username();
            String item = String.format("Title: %s. Author: %s", title, author);

            postList.getItems().add(item);
        }

    }
}
