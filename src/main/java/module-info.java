module org.example.blogr {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires eu.hansolo.tilesfx;
    requires org.mongodb.bson;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.driver.core;
    requires jbcrypt;


    opens org.example.blogr to javafx.fxml;
    exports org.example.blogr;
    exports org.example.blogr.Controllers;
    opens org.example.blogr.Controllers to javafx.fxml;
}