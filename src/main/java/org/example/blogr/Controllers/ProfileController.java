package org.example.blogr.Controllers;

import javafx.event.Event;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.example.blogr.Utils.ContextUtil;
import org.example.blogr.Utils.Switcher;
import org.example.blogr.domain.Post;
import org.example.blogr.services.UserService;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;

public class ProfileController {
    public FontIcon homeButton;
    public FontIcon searchButton;
    public Text username;
    public Text numberOfUserPosts;
    public ListView<VBox> userPostsList;

    ContextUtil context = ContextUtil.getInstance();
    private final UserService userService = new UserService();
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
            pane.setOnMouseClicked(mouseEvent -> System.out.println("CLicked"));

            userPostsList.getItems().add(pane);
        }

    }
}
