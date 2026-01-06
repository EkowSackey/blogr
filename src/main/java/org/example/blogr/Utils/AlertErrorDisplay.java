package org.example.blogr.Utils;

import javafx.scene.control.Alert;
import org.controlsfx.validation.ValidationMessage;

import java.util.List;

public class AlertErrorDisplay implements ErrorDisplay {

    @Override
    public void showValidationErrors(List<ValidationMessage> errors) {
        StringBuilder sb = new StringBuilder();
        for (ValidationMessage msg : errors) {
            sb.append("• ").append(msg.getText()).append("\n");
        }
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Please fix the following:");
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }

    @Override
    public void showServerError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Failed");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
