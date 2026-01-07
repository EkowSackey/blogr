package org.example.blogr.Utils;

import org.bson.types.ObjectId;

public class ContextUtil {
    private static final ContextUtil instance = new ContextUtil();

    private ObjectId currentUserId;

    private ContextUtil(){}

    public static ContextUtil getInstance(){
        return instance;
    }

    public ObjectId getCurrentUserId(){
        return currentUserId;
    }

    public void setCurrentUserId(ObjectId currentUserId) {
        this.currentUserId = currentUserId;
    }
}
