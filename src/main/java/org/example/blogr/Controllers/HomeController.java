package org.example.blogr.Controllers;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class HomeController {

    @FXML public Text displayName;

    public void setDisplayName(String name){
        displayName.setText(name);
    }
}
