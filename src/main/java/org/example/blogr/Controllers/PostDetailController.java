package org.example.blogr.Controllers;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import org.bson.types.ObjectId;
import org.example.blogr.Utils.ContextUtil;
import org.example.blogr.Utils.Switcher;
import org.example.blogr.domain.Post;
import org.example.blogr.services.PostService;
import org.example.blogr.services.UserService;

public class PostDetailController {
    public Button backButton;
    public Button editButton;
    public Button reviewButton;
    public Label postTitle;
    public Text authorName;
    public Text dateCreated;
    public Text lastUpdate;
    public WebView webView;
    public Button deleteButton;

    ContextUtil context = ContextUtil.getInstance();
    private final Post post = context.getCurrentPost();
    private final UserService userService = new UserService();
    private final PostService postService = new PostService();

    public void initialize(){
        editButton.setVisible(post.authorId().equals(context.getCurrentUserId()));
        deleteButton.setVisible(post.authorId().equals(context.getCurrentUserId()));


        postTitle.setText(post.title());
        authorName.setText(userService.getMyProfile(post.authorId()).username());
        dateCreated.setText(String.valueOf(post.dateCreated()));
        lastUpdate.setText(String.valueOf(post.lastUpdate()));
        webView.getEngine().loadContent(post.content());
    }

    public void switchToHome(Event mouseEvent) {
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

    public void switchToEdit(ActionEvent actionEvent) {
        context.setEditMode(true);
        Switcher.switchScreen(actionEvent, Screen.ADD);
    }

    public void displayDialog(ActionEvent actionEvent) {
    }

    public void deletePost(ActionEvent actionEvent) {
        postService.deletePost(new ObjectId());
    }
}
