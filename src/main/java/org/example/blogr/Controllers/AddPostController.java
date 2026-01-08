package org.example.blogr.Controllers;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.web.HTMLEditor;
import org.bson.types.ObjectId;
import org.controlsfx.validation.ValidationSupport;
import org.example.blogr.Utils.*;
import org.example.blogr.domain.Comment;
import org.example.blogr.domain.Post;
import org.example.blogr.domain.Review;
import org.example.blogr.domain.Tag;
import org.example.blogr.services.PostService;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AddPostController {
    public FontIcon homeButton;
    public FontIcon searchButton;
    public TextField postTitle;
    public Button postButton;
    public TextField addTagField;
    public Button addTagButton;
    public HTMLEditor contentField;

    private final List<Tag> tags = new ArrayList<>();

    private ErrorDisplay strategy;
    private final ValidationSupport vs = new ValidationSupport();
    private final PostService postService = new PostService();

    ContextUtil context = ContextUtil.getInstance();

    public void initialize(){
        ValidationUtils.init(vs);

        ValidationUtils.registerRequired(vs, postTitle, "Title is required");
        ValidationUtils.bindDisableOnInvalid(postButton, vs);

        strategy = new AlertErrorDisplay();
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


    public void createPost(ActionEvent actionEvent) {
        postTitle.setText(postTitle.getText() != null ? postTitle.getText().trim() : "");

        if (!ValidationUtils.ensureValidOrShow(vs, strategy)){
            return;
        }

        String title   = postTitle.getText();
        String content = contentField.getHtmlText();
        Date dateCreated      = new Date();
        Date lastUpdate= new Date();
        ObjectId authorId = context.getCurrentUserId();
        List<Comment> comments = new ArrayList<>();
        List<Review> reviews = new ArrayList<>();

        Post post = postService.createPost(title, content, dateCreated, lastUpdate, authorId, comments, tags, reviews);
        context.addUserPost(post);
        switchToHome(actionEvent);

    }

    public void addTag(ActionEvent actionEvent) {
        ValidationUtils.registerRequired(vs, addTagField, "Tag is required");
        ValidationUtils.bindDisableOnInvalid(addTagButton, vs);

        if(!ValidationUtils.ensureValidOrShow(vs, strategy)){
            return;
        }
        String tagName = addTagField.getText();
        Tag tag = new Tag(tagName);
        tags.add(tag);
    }
}
