package org.example.blogr.domain;

import org.bson.types.ObjectId;

public record Review(
        double stars,
        ObjectId userId,
        ObjectId postId
) {}
