package org.example.blogr.Controllers;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import org.bson.types.ObjectId;
import org.controlsfx.validation.ValidationSupport;
import org.example.blogr.Utils.*;
import org.example.blogr.domain.Comment;
import org.example.blogr.domain.Post;
import org.example.blogr.services.PostService;
import org.example.blogr.services.UserService;

import java.util.List;

public class PostDetailController {
    public Button backButton;
    public Button editButton;
    public Label postTitle;
    public Text authorName;
    public Text dateCreated;
    public Text lastUpdate;
    public WebView webView;
    public Button deleteButton;
    public AnchorPane reviewPane;
    public Slider ratingSlider;
    public TextArea commentArea;
    public Text avgRating;
    public Text commentCount;
    public ListView<Object> commentList;
    public Button sendReview;

    ContextUtil context = ContextUtil.getInstance();
    private final Post post = context.getCurrentPost();
    private final UserService userService = new UserService();
    private final PostService postService = new PostService();

    private ErrorDisplay strategy;
    private final ValidationSupport vs = new ValidationSupport();

    private final BooleanProperty isOwner = new SimpleBooleanProperty(false);

    public void initialize(){
        updateOwnership();
        editButton.setVisible(post.authorId().equals(context.getCurrentUserId()));
        deleteButton.setVisible(post.authorId().equals(context.getCurrentUserId()));

        reviewPane.managedProperty().bind(reviewPane.visibleProperty());
        reviewPane.visibleProperty().bind(isOwner.not());

        postTitle.setText(post.title());
        authorName.setText(userService.getMyProfile(post.authorId()).username());
        dateCreated.setText(String.valueOf(post.dateCreated()));
        lastUpdate.setText(String.valueOf(post.lastUpdate()));
        webView.getEngine().loadContent(post.content());
        commentCount.setText(String.valueOf(post.commentCount()));
        avgRating.setText(String.valueOf(post.avgRating()));

        displayComments();

        ValidationUtils.init(vs);
        ValidationUtils.registerRequired(vs, commentArea, "You can't send an empty review.");
        ValidationUtils.bindDisableOnInvalid(sendReview, vs);

        strategy = new AlertErrorDisplay();
    }

    public void updateOwnership(){
        isOwner.set(post.authorId().equals(context.getCurrentUserId()));
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


    public void deletePost(ActionEvent actionEvent) {
        postService.deletePost(context.getCurrentPost().postId());
        switchToHome(actionEvent);
    }

    public void addReview(ActionEvent actionEvent) {

        if (!ValidationUtils.ensureValidOrShow(vs, strategy)){
            return;
        }

        double stars = ratingSlider.getValue();
        String comment = commentArea.getText();
        ObjectId userId = context.getCurrentUserId();
        ObjectId postId = context.getCurrentPost().postId();

        postService.addReview(stars, comment, userId, postId);
        initialize();

    }

    public void displayComments(){
        List<Comment> comments = post.comments();

        for (Comment c : comments){
            Text content = new Text(c.content());
            content.setFont(Font.font("Monospaced", 24));
            Text author = new Text(userService.getMyProfile(c.authorId()).username());
            author.setFont(Font.font("Monospaced", 10));
            Text madeAt = new Text(String.valueOf(c.createdAt()));
            madeAt.setFont(Font.font("Monospaced", 8));

            Button deleteButton = new Button("Delete");
            deleteButton.setVisible(c.authorId().equals(context.getCurrentUserId()));
            deleteButton.setOnMouseClicked( mouseEvent -> {
                deleteComment(mouseEvent, context.getCurrentPost().postId(), c.commentId());
                refresh(mouseEvent);
            });

            VBox pane = new VBox();
            pane.setSpacing(5);
            pane.getChildren().add(author);
            pane.getChildren().add(madeAt);
            pane.getChildren().add(content);
            pane.getChildren().add(deleteButton);

            commentList.getItems().add(pane);
        }
    }

    private void refresh(Event event){
        Switcher.switchScreen(event, Screen.HOME);
        Switcher.switchScreen(event, Screen.DETAIL);
    }

    public void deleteComment(Event event, ObjectId postId, ObjectId commentId){
        postService.deleteComment(postId, commentId);
        refresh(event);
    }
}
