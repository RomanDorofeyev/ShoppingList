package com.rdproject.shoppinglist.login;

import java.util.Objects;

public class User {
    private String userId;
    private String name;
    private String email;
    private String  picUrl;

    public User() {
    }

    public User(String name, String email, String picUrl) {
        this.name = name;
        this.email = email;
        this.picUrl = picUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(userId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}


//{
//        "rules": {
//        ".read": "auth != null",
//        ".write": "auth != null"
//
//        }
//        }
