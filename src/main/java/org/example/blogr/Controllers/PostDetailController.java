package org.example.blogr.Controllers;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
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
    public Label authorName;
    public Label dateCreated;
    public Label lastUpdate;
    public WebView webView;
    public Button deleteButton;
    public VBox reviewPane;
    public Slider ratingSlider;
    public Label ratingValue;
    public TextArea commentArea;
    public Label avgRating;
    public Label commentCount;
    public ListView<VBox> commentList;
    public Button sendReview;

    ContextUtil context = ContextUtil.getInstance();
    private final Post post = context.getCurrentPost();
    private final UserService userService = ServiceLocator.getUserService();
    private final PostService postService = ServiceLocator.getPostService();

    private ErrorDisplay strategy;
    private final ValidationSupport vs = new ValidationSupport();

    private final BooleanProperty isOwner = new SimpleBooleanProperty(false);

    public void initialize(){
        if (post == null) return;
        
        updateOwnership();
        editButton.setVisible(post.authorId().equals(context.getCurrentUserId()));
        deleteButton.setVisible(post.authorId().equals(context.getCurrentUserId()));
        
        // Bind review pane visibility
        reviewPane.managedProperty().bind(reviewPane.visibleProperty());
        reviewPane.visibleProperty().bind(isOwner.not());
        
        // Bind slider value to label
        ratingValue.textProperty().bind(ratingSlider.valueProperty().asString("%.1f"));

        postTitle.setText(post.title());
        try {
             var author = userService.getMyProfile(post.authorId());
             authorName.setText(author != null ? author.username() : "Unknown");
        } catch (Exception e) {
             authorName.setText("Unknown");
        }
        
        dateCreated.setText(String.valueOf(post.dateCreated()));
        lastUpdate.setText(String.valueOf(post.lastUpdate()));
        webView.getEngine().loadContent(post.content());
        commentCount.setText("(" + post.commentCount() + ")");
        avgRating.setText(String.format("%.1f", post.avgRating()));

        displayComments();

        ValidationUtils.init(vs);
        ValidationUtils.registerRequired(vs, commentArea, "You can't send an empty review.");
        ValidationUtils.bindDisableOnInvalid(sendReview, vs);

        strategy = new AlertErrorDisplay();

    }

    public void updateOwnership(){
        if (post != null) {
            isOwner.set(post.authorId().equals(context.getCurrentUserId()));
        }
    }

    public void switchToHome(Event mouseEvent) {
        context.clearCurrentPost();
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
        // Refresh by switching back and forth or just re-initializing if possible
        // Ideally should just reload data, but for now switch is safe
        refresh(actionEvent);
    }

    public void displayComments(){
        List<Comment> comments = post.comments();
        commentList.getItems().clear();

        for (Comment c : comments){
            Label content = new Label(c.content());
            content.getStyleClass().add("text");
            content.setWrapText(true);
            
            Label author = new Label(userService.getMyProfile(c.authorId()).username());
            author.getStyleClass().add("text");
            author.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
            
            Label madeAt = new Label(String.valueOf(c.createdAt()));
            madeAt.getStyleClass().add("text");
            madeAt.setStyle("-fx-font-size: 10px; -fx-text-fill: #888888;");

            Button deleteButton = new Button("Delete");
            deleteButton.getStyleClass().add("button");
            deleteButton.setStyle("-fx-background-color: #D32F2F; -fx-font-size: 10px; -fx-padding: 2 5 2 5;");
            deleteButton.setVisible(c.authorId().equals(context.getCurrentUserId()));
            deleteButton.setOnMouseClicked( mouseEvent -> {
                deleteComment(mouseEvent, context.getCurrentPost().postId(), c.commentId());
            });

            VBox pane = new VBox(5);
            pane.setStyle("-fx-background-color: #333333; -fx-padding: 10; -fx-background-radius: 5;");
            pane.getChildren().add(author);
            pane.getChildren().add(madeAt);
            pane.getChildren().add(content);
            
            if (deleteButton.isVisible()) {
                pane.getChildren().add(deleteButton);
            }

            commentList.getItems().add(pane);
        }
    }

    private void refresh(Event event){
        Switcher.switchScreen(event, Screen.HOME);
        // Note: This double switch might be jarring but it's how the original code worked to refresh.
        // Better: reload data in place. But I'll stick to legacy behavior for now to minimize logic bugs.
        // Actually, let's just go home for simpler UX or try to reload.
        // context.setCurrentPost(postService.getPost(post.postId())); 
        // initialize();
        // Since I don't want to change too much logic, I'll keep the switch to Detail.
        // But Screen.DETAIL logic in Switcher might need context set.
        Switcher.switchScreen(event, Screen.DETAIL);
    }

    public void deleteComment(Event event, ObjectId postId, ObjectId commentId){
        postService.deleteComment(postId, commentId);
        refresh(event);
    }
}
