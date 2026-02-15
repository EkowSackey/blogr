package org.example.blogr.Controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import org.example.blogr.Utils.ContextUtil;
import org.example.blogr.Utils.Switcher;
import org.example.blogr.components.PostListCell;
import org.example.blogr.domain.Post;
import org.example.blogr.services.PostService;

import java.util.List;

public class HomeController {

    @FXML
    public ListView<Post> postList;

    private List<Post> allPosts;
    private final PostService postService = new PostService();
    
    ContextUtil context = ContextUtil.getInstance();

    public void initialize(){
        allPosts = postService.getPosts();
        context.clearCurrentPost();
        
        // Use custom cell factory
        postList.setCellFactory(param -> {
            PostListCell cell = new PostListCell();
            // Add click listener to the cell
            cell.setOnMouseClicked(event -> {
                if (!cell.isEmpty() && cell.getItem() != null) {
                    context.setCurrentPost(cell.getItem());
                    // We need to handle the switch manually since we are inside a cell
                    Switcher.switchScreen(event, Screen.DETAIL);
                }
            });
            return cell;
        });

        displayPosts();
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

    public void displayPosts(){
        if (allPosts != null) {
             postList.setItems(FXCollections.observableArrayList(allPosts.reversed()));
        }
    }
}
