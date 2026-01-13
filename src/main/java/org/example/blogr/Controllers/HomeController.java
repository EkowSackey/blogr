package org.example.blogr.Controllers;

import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.example.blogr.Utils.ContextUtil;
import org.example.blogr.Utils.Switcher;
import org.example.blogr.domain.Post;
import org.example.blogr.services.PostService;
import org.example.blogr.services.UserService;

import java.util.List;

public class HomeController {

    public ListView<VBox> postList;

    List<Post> allPosts;
    private final PostService postService = new PostService();
    private final UserService userService = new UserService();

    ContextUtil context = ContextUtil.getInstance();


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
            postList.getItems().add(pane);
        }

    }
}
