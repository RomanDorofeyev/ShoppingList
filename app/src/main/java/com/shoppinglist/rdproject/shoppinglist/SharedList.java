package com.shoppinglist.rdproject.shoppinglist;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.Objects;
@IgnoreExtraProperties
public class SharedList implements Serializable {
    public static final int ALREADY_SHOWED = 1;
    public static final int NOT_YET_SHOWED = 0;
    private String sharedListName;
    private String fromUserName;
    private String fromUserEmail;
    private String fromUserId;
    private int showedToUser;

    public SharedList() {
    }

    public SharedList(String sharedListName, String fromUserName, String fromUserEmail, String fromUserId) {
        this.sharedListName = sharedListName;
        this.fromUserName = fromUserName;
        this.fromUserEmail = fromUserEmail;
        this.fromUserId = fromUserId;
    }

    public int getShowedToUser() {
        return showedToUser;
    }

    public void setShowedToUser(int showedToUser) {
        this.showedToUser = showedToUser;
    }

    public String getSharedListName() {
        return sharedListName;
    }

    public void setSharedListName(String sharedListName) {
        this.sharedListName = sharedListName;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getFromUserEmail() {
        return fromUserEmail;
    }

    public void setFromUserEmail(String fromUserEmail) {
        this.fromUserEmail = fromUserEmail;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SharedList that = (SharedList) o;
        return Objects.equals(sharedListName, that.sharedListName) &&
                Objects.equals(fromUserName, that.fromUserName) &&
                Objects.equals(fromUserEmail, that.fromUserEmail) &&
                Objects.equals(fromUserId, that.fromUserId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(sharedListName, fromUserName, fromUserEmail, fromUserId);
    }

}
