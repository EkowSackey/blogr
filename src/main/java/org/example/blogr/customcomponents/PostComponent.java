package org.example.blogr.customcomponents;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class PostComponent extends AnchorPane {

    @FXML private Text title;
    @FXML private Text author;
    @FXML private Text dateCreated;

    public PostComponent(String title, String author, String dateCreated){
        this.title = new Text(title);
        this.author= new Text(author);
        this.dateCreated = new Text(dateCreated);
    }
}
