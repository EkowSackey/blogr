
package org.example.blogr.Controllers;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * The type Scene controller.
 */
public class SceneController {

    /**
     * Switch to login.
     *
     * @param event the event
     * @throws IOException the io exception
     */
    @FXML
    public void switchToLogin(ActionEvent event) throws IOException {
        switchRoot(event, "/org/example/blogr/login-view.fxml");
    }

    /**
     * Switch to register.
     *
     * @param event the event
     * @throws IOException the io exception
     */
    @FXML
    public void switchToRegister(ActionEvent event) throws IOException {
        switchRoot(event, "/org/example/blogr/register-view.fxml");
    }

    /**
     * Switch to home.
     *
     * @param event the event
     * @throws IOException the io exception
     */
    @FXML
    public void switchToHome(ActionEvent event) throws IOException {
        switchRoot(event, "/org/example/blogr/home-view.fxml");
    }

    /**
     * Switch to home.
     *
     * @param event the event
     * @throws IOException the io exception
     */
    @FXML
    public void switchToHome(Event event) throws IOException {
        switchRoot(event, "/org/example/blogr/home-view.fxml");
    }

    /**
     * Switch to search.
     *
     * @param event the event
     * @throws IOException the io exception
     */
    @FXML
    public void switchToSearch(Event event) throws IOException {
        switchRoot(event, "/org/example/blogr/search-view.fxml");
    }

    /**
     * Switch to add.
     *
     * @param event the event
     * @throws IOException the io exception
     */
    @FXML
    public void switchToAdd(Event event) throws IOException {
        switchRoot(event, "/org/example/blogr/addPost-view.fxml");
    }

    /**
     * Switch to profile.
     *
     * @param event the event
     * @throws IOException the io exception
     */
    @FXML
    public void switchToProfile(Event event) throws IOException {
        switchRoot(event, "/org/example/blogr/profile-view.fxml");
    }

    @FXML
    public void switchToDetails(Event event) throws IOException {
        switchRoot(event, "/org/example/blogr/postDetail-view.fxml");
    }

    private void switchRoot(Event event, String fxmlPath) throws IOException {
        URL url = SceneController.class.getResource(fxmlPath);
        Objects.requireNonNull(url, "FXML not found: " + fxmlPath);

        Parent newRoot = FXMLLoader.load(url);

        Scene scene = resolveSceneFromEvent(event);
        Objects.requireNonNull(scene, "Cannot resolve Scene from event");

        scene.setRoot(newRoot);
    }

    private Scene resolveSceneFromEvent(Event event) {
        Object src = event.getSource();

        if (src instanceof Node node) {
            return node.getScene();
        }

        if (src instanceof MenuItem mi) {
            Window owner = null;
            if (mi.getParentPopup() != null) {
                owner = mi.getParentPopup().getOwnerWindow();
            }
            if (owner == null) {
                owner = Stage.getWindows().stream()
                        .filter(Window::isFocused)
                        .findFirst()
                        .orElse(null);
            }
            return (owner != null) ? owner.getScene() : null;
        }

        Window focused = Stage.getWindows().stream()
                .filter(Window::isFocused)
                .findFirst()
                .orElse(null);

        return (focused != null) ? focused.getScene() : null;
    }
}

