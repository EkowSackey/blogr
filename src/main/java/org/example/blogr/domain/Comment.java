package org.example.blogr.domain;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;

public record Comment(
        String content,
        ObjectId authorId,
        ObjectId parentId,
        ArrayList<Comment> subComments,
        Date createdAt
) {
    public Comment {
        if(createdAt == null){
            createdAt = new Date();
        }

        if (subComments == null){
            subComments = new ArrayList<>();
        }
    }
}
