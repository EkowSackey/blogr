package org.example.blogr.Utils;

import javafx.event.Event;
import org.example.blogr.Controllers.SceneController;
import org.example.blogr.Controllers.Screen;

import java.io.IOException;

public class Switcher {
    private static final SceneController sc = new SceneController();

    public static void switchScreen(Event event, Screen screen) {
        switch (screen){
            case Screen.ADD -> {
                try {
                    sc.switchToAdd(event);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case Screen.SEARCH -> {
                try {
                    sc.switchToSearch(event);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case Screen.HOME -> {
                try {
                    sc.switchToHome(event);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case Screen.PROFILE -> {
                try {
                    sc.switchToProfile(event);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            case Screen.DETAIL -> {
                try {
                    sc.switchToDetails(event);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
