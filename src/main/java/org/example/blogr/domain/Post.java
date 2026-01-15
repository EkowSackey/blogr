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
    public Post{
        if (postId == null){
            postId = new ObjectId();
        }

        commentCount = comments.size();


        if (!reviews.isEmpty()){
            int count = 0;
            double total = 0.0;

            for (Review r: reviews){
                count += 1;
                total += r.stars();
            }

            avgRating = total/count;

        }
    }
}
