package org.example.blogr.domain;

import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

public record Post(
                   ObjectId postId,
                   String title,
                   String content,
                   Date dateCreated,
                   Date lastUpdate,
                   ObjectId authorId,
                   List<Comment> comments,
                   List<Tag> tags,
                   List<Review> reviews)
{
    public Post{
        // Handle null comments list
        if (comments == null){
            comments = List.of();
        }

        // Handle null reviews list
        if (reviews == null){
            reviews = List.of();
        }
    }

    public int commentCount() {
        return comments.size();
    }

    public double avgRating() {
        if (reviews.isEmpty()){
            return 0.0;
        }
        double total = 0.0;
        for (Review r: reviews){
            total += r.stars();
        }
        return total/reviews.size();
    }
}
