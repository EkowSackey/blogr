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
                   int commentCount,
                   List<Tag> tags,
                   List<Review> reviews,
                   double avgRating)
{
    public Post {
        if (postId == null){
            postId = new ObjectId();
        }

        // Handle null comments list
        if (comments == null){
            comments = List.of();
        }

        // Handle null reviews list
        if (reviews == null){
            reviews = List.of();
        }
    }

    public static Post createWithCalculatedFields(ObjectId postId, String title, String content, Date dateCreated,
                                                  Date lastUpdate, ObjectId authorId, List<Comment> comments,
                                                  List<Tag> tags, List<Review> reviews) {
        
        List<Comment> safeComments = comments == null ? List.of() : comments;
        List<Review> safeReviews = reviews == null ? List.of() : reviews;
        
        int commentCount = safeComments.size();
        
        double avgRating = 0.0;
        if (!safeReviews.isEmpty()){
            double total = 0.0;
            for (Review r: safeReviews){
                total += r.stars();
            }
            avgRating = total / safeReviews.size();
        }

        return new Post(postId, title, content, dateCreated, lastUpdate, authorId, safeComments, commentCount, tags, safeReviews, avgRating);
    }
}
