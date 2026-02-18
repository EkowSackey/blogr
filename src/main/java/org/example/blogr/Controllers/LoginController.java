package org.example.blogr.Controllers;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.bson.types.ObjectId;
import org.controlsfx.validation.ValidationSupport;
import org.example.blogr.Utils.*;
import org.example.blogr.domain.Post;
import org.example.blogr.domain.User;
import org.example.blogr.exceptions.InvalidCredentialsException;
import org.example.blogr.services.PostService;
import org.example.blogr.services.UserService;

import java.io.IOException;
import java.util.List;


public class LoginController {
    @FXML private TextField usernameOrEmailField;
    @FXML private TextField passwordField;
    @FXML private Button loginButton;

    private final SceneController sc = new SceneController();
    private final ValidationSupport vs = new ValidationSupport();
    private ErrorDisplay strategy;

    private final BooleanProperty isSubmitting = new SimpleBooleanProperty(false);
    ContextUtil context = ContextUtil.getInstance();

    public void switchToRegister(ActionEvent actionEvent) throws IOException {
        sc.switchToRegister(actionEvent);
    }

    public void initialize(){
        ValidationUtils.init(vs);

        ValidationUtils.registerRequired(vs, usernameOrEmailField, "Username is required");
        ValidationUtils.registerMinLength(vs, passwordField, 8, "Password must be at least 8 characters");
        loginButton.disableProperty().bind(vs.invalidProperty().or(isSubmitting));

        strategy = new AlertErrorDisplay();
    }

    public void submitData(ActionEvent actionEvent) {

        usernameOrEmailField.setText(usernameOrEmailField.getText() != null ? usernameOrEmailField.getText().trim() :"" );

        if (!ValidationUtils.ensureValidOrShow(vs, strategy)){
            return;
        }

        isSubmitting.set(true);

        Task<Void> loginTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                UserService userService = ServiceLocator.getUserService();
                PostService postService = ServiceLocator.getPostService();

                ObjectId userId = userService.login(usernameOrEmailField.getText(), passwordField.getText());
                User currentUser = userService.getMyProfile(userId);
                List<Post> userPosts = postService.getUserPosts(userId);

                context.setCurrentUserId(userId);
                context.setCurrentUser(currentUser);
                context.setUserPosts(userPosts);
                return null;
            }
        };

        loginTask.setOnSucceeded(e -> {
            isSubmitting.set(false);
            try {
                sc.switchToHome(actionEvent);
            } catch (IOException ex) {
                ValidationUtils.showServerError(strategy, "Error loading home screen: " + ex.getMessage());
            }
        });

        loginTask.setOnFailed(e -> {
            isSubmitting.set(false);
            Throwable exception = loginTask.getException();
            if (exception instanceof InvalidCredentialsException) {
                ValidationUtils.showServerError(strategy, exception.getMessage());
            } else {
                ValidationUtils.showServerError(strategy, "Login failed: " + exception.getMessage());
                exception.printStackTrace();
            }
        });

        TaskRunner.run(loginTask);
    }
}
