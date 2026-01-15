package org.example.blogr.domain;

import org.bson.types.ObjectId;

import java.util.Date;

public record Comment(
        ObjectId commentId,
        String content,
        ObjectId authorId,
        ObjectId parentId,
        Date createdAt
) {
    public Comment {
        if(createdAt == null){
            createdAt = new Date();
        }

        if (commentId == null){
            commentId = new ObjectId();
        }

    }
}
