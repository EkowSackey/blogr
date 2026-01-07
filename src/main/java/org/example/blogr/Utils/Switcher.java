package org.example.blogr.Utils;

import javafx.scene.input.MouseEvent;
import org.example.blogr.Controllers.SceneController;
import org.example.blogr.Controllers.Screen;

import java.io.IOException;

public class Switcher {
    private static final SceneController sc = new SceneController();

    public static void switchScreen(MouseEvent mouseEvent, Screen screen) {
        switch (screen){
            case Screen.ADD -> {
                try {
                    sc.switchToAdd(mouseEvent);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case Screen.SEARCH -> {
                try {
                    sc.switchToSearch(mouseEvent);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case Screen.HOME -> {
                try {
                    sc.switchToHome(mouseEvent);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case Screen.PROFILE -> {
                try {
                    sc.switchToProfile(mouseEvent);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
