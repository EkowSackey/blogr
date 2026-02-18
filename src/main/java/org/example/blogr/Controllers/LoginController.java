package org.example.blogr.Controllers;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
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
    private static final String REQUIRED_USERNAME_MSG = "Username is required";
    private static final String MIN_PASSWORD_MSG = "Password must be at least 8 characters";
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final String HOME_SCREEN_ERROR_MSG = "Failed to load the home screen.";
    private static final String UNEXPECTED_ERROR_MSG = "An unexpected error occurred.";

    @FXML private Hyperlink registerLink;
    @FXML private TextField usernameOrEmailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;

    private final SceneController sc = new SceneController();
    private final ValidationSupport vs = new ValidationSupport();
    private ErrorDisplay strategy;
    private final UserService userService;
    private final PostService postService;

    private final ContextUtil context = ContextUtil.getInstance();
    private final BooleanProperty isProcessing = new SimpleBooleanProperty(false);

    public LoginController() {
        this(new UserService(), new PostService());
    }

    public LoginController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    public void switchToRegister(ActionEvent actionEvent) throws IOException {
        sc.switchToRegister(actionEvent);
    }

    public void initialize(){
        ValidationUtils.init(vs);

        ValidationUtils.registerRequired(vs, usernameOrEmailField, REQUIRED_USERNAME_MSG);
        ValidationUtils.registerMinLength(vs, passwordField, MIN_PASSWORD_LENGTH, MIN_PASSWORD_MSG);
        
        // Bind disable property to validation status OR processing status
        loginButton.disableProperty().bind(vs.invalidProperty().or(isProcessing));

        strategy = new AlertErrorDisplay();
    }

    public void submitData(ActionEvent actionEvent) {

        String usernameInput = usernameOrEmailField.getText() != null ? usernameOrEmailField.getText().trim() : "";
        usernameOrEmailField.setText(usernameInput);
        String passwordInput = passwordField.getText();

        if (!ValidationUtils.ensureValidOrShow(vs, strategy)){
            return;
        }

        isProcessing.set(true);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                ObjectId userId = userService.login(usernameInput, passwordInput);
                User currentUser = userService.getMyProfile(userId);
                List<Post> userPosts = postService.getUserPosts(userId);

                context.setCurrentUserId(userId);
                context.setCurrentUser(currentUser);
                context.setUserPosts(userPosts);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            try {
                sc.switchToHome(actionEvent);
            } catch (IOException e) {
                isProcessing.set(false);
                ValidationUtils.showServerError(strategy, HOME_SCREEN_ERROR_MSG);
                e.printStackTrace();
            }
        });

        task.setOnFailed(event -> {
            isProcessing.set(false);
            Throwable e = task.getException();
            if (e instanceof InvalidCredentialsException) {
                ValidationUtils.showServerError(strategy, e.getMessage());
            } else {
                ValidationUtils.showServerError(strategy, UNEXPECTED_ERROR_MSG);
                e.printStackTrace();
            }
        });

        new Thread(task).start();
    }
}
