package com.example.mycooking.Model;



public class Users {

    private String device_token;
    private String user_id;
    private String email;
    private String username;
    private String profile_photo;

    public Users() {
    }

    public Users(String user_id, String email, String username) {
        this.user_id = user_id;
        this.email = email;
        this.username = username;
    }

    public Users(String device_token, String user_id, String email, String username, String profile_photo) {
        this.device_token = device_token;
        this.user_id = user_id;
        this.email = email;
        this.username = username;
        this.profile_photo = profile_photo;
    }

    public Users(String user_id, String email, String username, String profile_photo) {
        this.user_id = user_id;
        this.email = email;
        this.username = username;
        this.profile_photo = profile_photo;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }
}
