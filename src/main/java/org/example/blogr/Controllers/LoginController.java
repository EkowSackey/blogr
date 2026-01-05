package org.example.blogr.Controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.io.IOException;

public class LoginController {
    public Hyperlink registerLink;
    public TextField usernameOrEmailField;
    public TextField passwordField;
    public Button loginButton;

    private final SceneController sc = new SceneController();
    private final ValidationSupport vs = new ValidationSupport();
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";


    public void switchToRegister(ActionEvent actionEvent) throws IOException, IOException {
        sc.switchToRegister(actionEvent);
    }

    public void submitData(ActionEvent actionEvent) {
        vs.registerValidator(usernameOrEmailField, Validator.createEmptyValidator("Cannot be empty"));
        if (usernameOrEmailField.getText().contains("@")){
            vs.registerValidator(usernameOrEmailField, Validator.createPredicateValidator(
                    v -> v.toString().matches(EMAIL_REGEX),
                    "Input a valid email address"
            ));
        }
        vs.registerValidator(passwordField, Validator.createPredicateValidator(
                v -> v != null && v.toString().length() >=8, "Password must be at least 8 characters"
        ));
        loginButton.disableProperty().bind(vs.invalidProperty());

        System.out.println(usernameOrEmailField.getText());
        System.out.println(passwordField.getText());
    }
}
