module org.example.blogr {
    requires javafx.controls;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;

    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires eu.hansolo.tilesfx;
    requires org.mongodb.bson;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.driver.core;
    requires jbcrypt;
    requires javafx.graphics;




    opens org.example.blogr to javafx.fxml;
    exports org.example.blogr;
    exports org.example.blogr.Controllers;
    opens org.example.blogr.Controllers to javafx.fxml;
    exports org.example.blogr.customcomponents;
    opens org.example.blogr.customcomponents to javafx.fxml;
}