module org.example.blogr {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires eu.hansolo.tilesfx;

    opens org.example.blogr to javafx.fxml;
    exports org.example.blogr;
    exports org.example.blogr.Controllers;
    opens org.example.blogr.Controllers to javafx.fxml;
}