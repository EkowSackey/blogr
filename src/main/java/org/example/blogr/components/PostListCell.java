package org.example.blogr.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.example.blogr.domain.Post;
import org.example.blogr.services.UserService;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PostListCell extends ListCell<Post> {

    private final VBox root;
    private final Label titleLabel;
    private final Label authorLabel;
    private final Label dateLabel;
    private final Text snippetText;
    private final UserService userService;

    public PostListCell() {
        userService = new UserService();
        
        root = new VBox(5);
        root.getStyleClass().add("post-card");
        root.setMaxWidth(Double.MAX_VALUE);

        titleLabel = new Label();
        titleLabel.getStyleClass().add("post-title");
        titleLabel.setWrapText(true);

        HBox metaBox = new HBox(10);
        metaBox.setAlignment(Pos.CENTER_LEFT);
        
        authorLabel = new Label();
        authorLabel.getStyleClass().add("post-meta");
        
        dateLabel = new Label();
        dateLabel.getStyleClass().add("post-meta");
        
        metaBox.getChildren().addAll(authorLabel, new Label("|"), dateLabel);

        snippetText = new Text();
        snippetText.getStyleClass().add("post-snippet");
        snippetText.setWrappingWidth(500); // Initial width, will bind to property if needed

        root.getChildren().addAll(titleLabel, metaBox, snippetText);
    }

    @Override
    protected void updateItem(Post post, boolean empty) {
        super.updateItem(post, empty);

        if (empty || post == null) {
            setText(null);
            setGraphic(null);
        } else {
            titleLabel.setText(post.title());
            try {
                // Fetch author name safely
                // Note: This might be blocking if DB is slow, but acceptable for this scale.
                // Ideally, author name should be part of Post projection or cached.
                 var author = userService.getMyProfile(post.authorId());
                 authorLabel.setText("By " + (author != null ? author.username() : "Unknown"));
            } catch (Exception e) {
                authorLabel.setText("By Unknown");
            }
            
            dateLabel.setText(post.dateCreated() != null ? post.dateCreated().toString() : "");
            
            // Create a snippet from content (stripping HTML if possible, or just raw)
            // For now, just show "Read more..." or first few chars if plain text
            snippetText.setText("Click to read more..."); 
            
            // Bind wrapping width to cell width to ensure responsiveness
            snippetText.wrappingWidthProperty().bind(getListView().widthProperty().subtract(40));

            setGraphic(root);
        }
    }
}
