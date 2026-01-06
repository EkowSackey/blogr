package org.example.blogr.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import org.controlsfx.validation.ValidationSupport;
import org.example.blogr.Utils.AlertErrorDisplay;
import org.example.blogr.Utils.ErrorDisplay;
import org.example.blogr.Utils.ValidationUtils;
import org.example.blogr.exceptions.InvalidCredentialsException;
import org.example.blogr.services.UserService;

import java.io.IOException;
import java.util.regex.Pattern;

public class LoginController {
    @FXML private Hyperlink registerLink;
    @FXML private TextField usernameOrEmailField;
    @FXML private TextField passwordField;
    @FXML private Button loginButton;

    private final SceneController sc = new SceneController();
    private final ValidationSupport vs = new ValidationSupport();
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private ErrorDisplay strategy;

    public void switchToRegister(ActionEvent actionEvent) throws IOException {
        sc.switchToRegister(actionEvent);
    }

    public void switchToHome(ActionEvent actionEvent){

    }

    public void initialize(){
        ValidationUtils.init(vs);

        ValidationUtils.registerRequired(vs, usernameOrEmailField, "Username is required");
        ValidationUtils.registerRegex(vs, usernameOrEmailField, Pattern.compile(EMAIL_REGEX), "Please input a valid email address");
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
        try {
            userService.login(usernameOrEmailField.getText(), passwordField.getText());
            sc.switchToHome(actionEvent, usernameOrEmailField.getText());
        } catch ( InvalidCredentialsException e) {
            ValidationUtils.showServerError(strategy, e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
