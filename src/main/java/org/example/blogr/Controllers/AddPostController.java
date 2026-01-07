package org.example.blogr.Controllers;

import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.example.blogr.Utils.Switcher;
import org.kordamp.ikonli.javafx.FontIcon;

public class AddPostController {
    public FontIcon homeButton;
    public FontIcon searchButton;
    public Text displayName;

    public void switchToHome(MouseEvent mouseEvent) {
        Switcher.switchScreen(mouseEvent, Screen.HOME);
    }

    public void switchToSearch(MouseEvent mouseEvent) {
        Switcher.switchScreen(mouseEvent, Screen.SEARCH);
    }

    public void switchToAdd(MouseEvent mouseEvent) {
        Switcher.switchScreen(mouseEvent, Screen.ADD);
    }

    public void switchToProfile(MouseEvent mouseEvent) {
        Switcher.switchScreen(mouseEvent, Screen.PROFILE);
    }


}
