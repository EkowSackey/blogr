package org.example.blogr.Controllers;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.bson.types.ObjectId;
import org.example.blogr.Utils.ContextUtil;
import org.example.blogr.Utils.Switcher;
import org.example.blogr.domain.User;
import org.example.blogr.services.UserService;
import org.kordamp.ikonli.javafx.FontIcon;

public class HomeController {

    public ImageView logo;
    public FontIcon homeButton;
    public FontIcon searchButton;
    public FontIcon addButton;
    public FontIcon profileButton;
    private ObjectId currentUserId;
    private User currentUser;
    @FXML public Text displayName;

    private final UserService uservice = new UserService();

    ContextUtil context = ContextUtil.getInstance();

    public void initialize(){
        setCurrentUserId();
        setCurrentUser();
        setDisplayName();
    }

    public ObjectId getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId() {
        this.currentUserId = context.getCurrentUserId();
    }

    public void setCurrentUser(){
        currentUser = uservice.getMyProfile(currentUserId);

    }

    public void setDisplayName(){
        displayName.setText(currentUser.username());
    }

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
