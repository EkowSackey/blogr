package org.example.blogr.Utils;

import org.bson.types.ObjectId;
import org.example.blogr.domain.Post;
import org.example.blogr.domain.User;

import java.util.List;

public class ContextUtil {
    private static final ContextUtil instance = new ContextUtil();

    private ObjectId currentUserId;
    private User currentUser;
    private List<Post> userPosts;
    private Post currentPost;
    private boolean editMode;

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

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public List<Post> getUserPosts() {
        return userPosts;
    }

    public void setUserPosts(List<Post> userPosts) {
        this.userPosts = userPosts;
    }

    public void addUserPost(Post post){
        this.userPosts.add(post);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public Post getCurrentPost() {
        return currentPost;
    }

    public void setCurrentPost(Post currentPost) {
        this.currentPost = currentPost;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public boolean isEditMode() {
        return editMode;
    }
}
