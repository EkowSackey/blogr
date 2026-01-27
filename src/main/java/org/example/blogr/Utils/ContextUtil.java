package org.example.blogr.Utils;

import org.bson.types.ObjectId;
import org.example.blogr.domain.Post;
import org.example.blogr.domain.User;

import java.util.ArrayList;
import java.util.List;

public class ContextUtil {
    private static final ThreadLocal<ContextUtil> instance = ThreadLocal.withInitial(ContextUtil::new);

    private ObjectId currentUserId;
    private User currentUser;
    private List<Post> userPosts;
    private Post currentPost;
    private boolean editMode;

    private ContextUtil(){}

    public static ContextUtil getInstance(){
        return instance.get();
    }

    public static void clear(){
        instance.remove();
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
        this.userPosts = userPosts != null? new ArrayList<>(userPosts) : null;
    }

    public void addUserPost(Post post){
        if (this.userPosts == null){
            this.userPosts = new ArrayList<>();
        }

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

    public void clearCurrentPost(){
        this.currentPost = null;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public boolean isEditMode() {
        return editMode;
    }
}
