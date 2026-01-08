package org.example.blogr.customcomponents;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;


public class PostComponent extends VBox {
    private final StringProperty title = new SimpleStringProperty("default title");
    private final StringProperty author = new SimpleStringProperty("default author");
    private final StringProperty dateCreated = new SimpleStringProperty("default date");
    private final StringProperty averageRating = new SimpleStringProperty("6");
    private final StringProperty numberOfComments = new SimpleStringProperty("0");

    @FXML private Text titleText;
    @FXML private Text authorText;
    @FXML private Text dateCreatedText;
    @FXML private Text averageRatingText;
    @FXML private Text numberOfCommentsText;

    public PostComponent(){
        URL fxml = Objects.requireNonNull(PostComponent.class.getResource("org/example/blogr/post-component.fxml"), "NOT FOUND");
        FXMLLoader loader = new FXMLLoader(fxml);
        loader.setRoot(this);

        try{
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /*
        bind text nodes to component properties
        **/
        titleText.textProperty().bind(title);
        authorText.textProperty().bind(author);
        dateCreatedText.textProperty().bind(dateCreated);
        averageRatingText.textProperty().bind(averageRating);
        numberOfCommentsText.textProperty().bind(numberOfComments);
    }


    public String getTitle() { return title.get(); }
    public void setTitle(String value) { title.set(value); }
    public StringProperty titleProperty() { return title; }

    public String getAuthor() { return author.get(); }
    public void setAuthor(String value) { author.set(value); }
    public StringProperty authorProperty() { return author; }

    public String getDateCreated() { return dateCreated.get(); }
    public void setDateCreated(String value) { dateCreated.set(value); }
    public StringProperty dateCreatedProperty() { return dateCreated; }

    public String getAverageRating() { return averageRating.get(); }
    public void setAverageRating(String value) { averageRating.set(value); }
    public StringProperty averageRatingProperty() { return averageRating; }

    public String getNumberOfComments() { return numberOfComments.get(); }
    public void setNumberOfComments(String value) { numberOfComments.set(value); }
    public StringProperty numberOfCommentsProperty() { return numberOfComments; }

}
