
package org.example.blogr.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class SceneController {

    @FXML
    public void switchToLogin(ActionEvent event) throws IOException {
        switchScene(event, "/org/example/blogr/login-view.fxml");
    }

    @FXML
    public void switchToRegister(ActionEvent event) throws IOException {
        switchScene(event, "/org/example/blogr/register-view.fxml");
    }

    @FXML
    public void switchToHome(ActionEvent event, String name) throws IOException {
//       todo: display username
        switchScene(event, "/org/example/blogr/home-view.fxml" );
    }

    private void switchScene(ActionEvent event, String fxmlPath) throws IOException {
        URL url = SceneController.class.getResource(fxmlPath);

        Objects.requireNonNull(url, "FXML not found: " + fxmlPath);

        FXMLLoader loader = new FXMLLoader(url);
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
