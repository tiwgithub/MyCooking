package com.example.mycooking.notifications;

public class Data {
    private String user , title , sent ,body;
    private Integer icon;

    public Data() {
    }

    public Data(String user, String title, String send, String bode, Integer icon) {
        this.user = user;
        this.title = title;
        this.sent = send;
        this.body = bode;
        this.icon = icon;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSend() {
        return sent;
    }

    public void setSend(String send) {
        this.sent = send;
    }

    public String getBode() {
        return body;
    }

    public void setBode(String bode) {
        this.body = bode;
    }

    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }
}
