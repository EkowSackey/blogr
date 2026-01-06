package org.example.blogr.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.controlsfx.validation.ValidationSupport;
import org.example.blogr.Utils.AlertErrorDisplay;
import org.example.blogr.Utils.ErrorDisplay;
import org.example.blogr.Utils.ValidationUtils;
import org.example.blogr.exceptions.DuplicateEmailException;
import org.example.blogr.exceptions.DuplicateUsernameException;
import org.example.blogr.services.UserService;

import java.io.IOException;
import java.util.regex.Pattern;

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button registerButton;


    private final SceneController sc = new SceneController();
    private final ValidationSupport vs = new ValidationSupport();
    private ErrorDisplay strategy;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    public Hyperlink loginLink;


    public void switchToLogin(ActionEvent actionEvent) throws IOException {
        sc.switchToLogin(actionEvent);
    }

    public void initialize(){
        ValidationUtils.init(vs);

        ValidationUtils.registerRequired(vs, usernameField, "Username is required");
        ValidationUtils.registerRegex(vs, emailField, Pattern.compile(EMAIL_REGEX), "Please input a valid email address");
        ValidationUtils.registerMinLength(vs, passwordField, 8, "Password must be at least 8 characters");
        ValidationUtils.registerConfirmPassword(vs, confirmPasswordField, passwordField, "Passwords do not match");
        ValidationUtils.bindDisableOnInvalid(registerButton, vs);

        strategy = new AlertErrorDisplay();
    }

    public void submitData(ActionEvent actionEvent) {

        usernameField.setText(usernameField.getText() != null ? usernameField.getText().trim() :"" );
        emailField.setText(emailField.getText() != null ? emailField.getText().trim() :"" );

        if (!ValidationUtils.ensureValidOrShow(vs, strategy)){
            return;
        }

        UserService userService = new UserService();
        try{
            userService.register(usernameField.getText(), emailField.getText(), passwordField.getText());
            switchToLogin(actionEvent);
        }catch (DuplicateUsernameException | DuplicateEmailException e){
            ValidationUtils.showServerError(strategy, e.getMessage());
        } catch (IOException e) {
            ValidationUtils.showServerError(strategy, "An Unexpected error occurred. It's not you, It's me.");
        }
    }
}
