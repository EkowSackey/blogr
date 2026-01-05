package org.example.blogr.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.io.IOException;

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button registerButton;

    private final SceneController sc = new SceneController();
    private final ValidationSupport vs = new ValidationSupport();

    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    public Hyperlink loginLink;

    public void switchToLogin(ActionEvent actionEvent) throws IOException {
        sc.switchToLogin(actionEvent);
    }

    public void submitData(ActionEvent actionEvent) {
        vs.registerValidator(usernameField, Validator.createEmptyValidator("Username is required"));
        vs.registerValidator(emailField, Validator.createPredicateValidator(
                v -> v != null && v.toString().matches(EMAIL_REGEX), 
                "Please input a valid email address"
        ));
        vs.registerValidator(passwordField, Validator.createPredicateValidator(
                v -> v != null && v.toString().length() >=8, "Password must be at least 8 characters"
        ));
        vs.registerValidator(confirmPasswordField, (control, value) ->
                        ValidationResult.fromErrorIf(control,
                                "Passwords do not match!",
                                    value == null || !value.equals(passwordField.getText())
                        ));

        registerButton.disableProperty().bind(vs.invalidProperty());
        System.out.println(usernameField.getText());
        System.out.println(emailField.getText());
        System.out.println(passwordField.getText());
        System.out.println(confirmPasswordField.getText());
    }
}
