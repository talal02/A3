package com.example.assignment3;

public class User {
    private String name, email, phno, photo;
    private long lastSeen;
    private String lastMessage = "";

    public User(String name, String email, String phno, String photo, long lastSeen) {
        this.name = name;
        this.email = email;
        this.phno = phno;
        this.photo = photo;
        this.lastSeen = lastSeen;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhno() {
        return phno;
    }

    public String getPhoto() {
        return photo;
    }
}
