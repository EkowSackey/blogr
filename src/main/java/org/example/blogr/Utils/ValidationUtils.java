package org.example.blogr.Utils;

import javafx.scene.control.ButtonBase;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextInputControl;
import org.controlsfx.validation.ValidationMessage;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.controlsfx.validation.decoration.StyleClassValidationDecoration;


import java.util.List;
import java.util.regex.Pattern;

public class ValidationUtils {

    private ValidationUtils(){}

    public static void init(ValidationSupport vs){
        vs.setValidationDecorator(new StyleClassValidationDecoration());
    }

    public static void registerRequired(ValidationSupport vs, TextInputControl field, String message){
        vs.registerValidator(field, Validator.createEmptyValidator(message));
    }

    public static void registerRegex(ValidationSupport vs, TextInputControl field, Pattern pattern, String message){
        vs.registerValidator(field, Validator.createPredicateValidator(
                v-> v != null && pattern.matcher(v.toString()).matches(),
                message
        ));
    }

    public static void registerMinLength(ValidationSupport vs, TextInputControl field, int min, String message){
        vs.registerValidator(field, Validator.createPredicateValidator(
                v -> v != null && v.toString().length() >= min, message
        ));
    }

    public static void registerConfirmPassword(ValidationSupport vs, PasswordField passwordField, PasswordField confirmField, String message){
        vs.registerValidator(confirmField, (control, value)->
                ValidationResult.fromErrorIf(control, message, value==null || !value.equals(passwordField.getText())));

        passwordField.textProperty().addListener((obs, oldV, newV)-> vs.revalidate());
    }

    public static void bindDisableOnInvalid(ButtonBase button, ValidationSupport vs){
        button.disableProperty().bind(vs.invalidProperty());
    }

    public static boolean ensureValidOrShow(ValidationSupport vs, ErrorDisplay strategy){
        vs.revalidate();
        if (vs.isInvalid()){
            ValidationResult result = vs.getValidationResult();
            List<ValidationMessage> errors = result.getErrors().stream().toList();
            strategy.showValidationErrors(errors);
            return false;
        }
        strategy.clear();
        return true;
    }

    public static void showServerError(ErrorDisplay strategy, String message){
        strategy.showServerError(message);
    }
}
