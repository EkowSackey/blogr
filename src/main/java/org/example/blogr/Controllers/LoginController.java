package org.example.blogr.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import org.bson.types.ObjectId;
import org.controlsfx.validation.ValidationSupport;
import org.example.blogr.Utils.AlertErrorDisplay;
import org.example.blogr.Utils.ContextUtil;
import org.example.blogr.Utils.ErrorDisplay;
import org.example.blogr.Utils.ValidationUtils;
import org.example.blogr.domain.Post;
import org.example.blogr.domain.User;
import org.example.blogr.exceptions.InvalidCredentialsException;
import org.example.blogr.services.PostService;
import org.example.blogr.services.UserService;

import java.io.IOException;
import java.util.List;


public class LoginController {
    @FXML private Hyperlink registerLink;
    @FXML private TextField usernameOrEmailField;
    @FXML private TextField passwordField;
    @FXML private Button loginButton;

    private final SceneController sc = new SceneController();
    private final ValidationSupport vs = new ValidationSupport();
    private ErrorDisplay strategy;

    ContextUtil context = ContextUtil.getInstance();

    public void switchToRegister(ActionEvent actionEvent) throws IOException {
        sc.switchToRegister(actionEvent);
    }

    public void initialize(){
        ValidationUtils.init(vs);

        ValidationUtils.registerRequired(vs, usernameOrEmailField, "Username is required");
        ValidationUtils.registerMinLength(vs, passwordField, 8, "Password must be at least 8 characters");
        ValidationUtils.bindDisableOnInvalid(loginButton, vs);

        strategy = new AlertErrorDisplay();
    }

    public void submitData(ActionEvent actionEvent) {

        usernameOrEmailField.setText(usernameOrEmailField.getText() != null ? usernameOrEmailField.getText().trim() :"" );

        if (!ValidationUtils.ensureValidOrShow(vs, strategy)){
            return;
        }

        UserService userService = new UserService();
        PostService postService = new PostService();
        try {
            ObjectId userId = userService.login(usernameOrEmailField.getText(), passwordField.getText());
            User currentUser = userService.getMyProfile(userId);
            List<Post> userPosts = postService.getUserPosts(userId);

            context.setCurrentUserId(userId);
            context.setCurrentUser(currentUser);
            context.setUserPosts(userPosts);
            sc.switchToHome(actionEvent);
        } catch ( InvalidCredentialsException e) {
            ValidationUtils.showServerError(strategy, e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
