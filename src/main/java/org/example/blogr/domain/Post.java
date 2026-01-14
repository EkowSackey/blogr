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
        if (postId == null){
            postId = new ObjectId();
        }
    }
}
