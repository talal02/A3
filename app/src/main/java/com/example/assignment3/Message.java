package com.example.assignment3;

public class Message {
    String message;
    User sender;
    long createdAt;
    boolean isPhoto, isVoice;

    public Message(String message, User sender, long createdAt, boolean isPhoto, boolean isVoice) {
        this.message = message;
        this.sender = sender;
        this.createdAt = createdAt;
        this.isPhoto = isPhoto;
        this.isVoice = isVoice;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }


    public boolean getIsPhoto() {
        return isPhoto;
    }

    public void setPhoto(boolean photo) {
        isPhoto = photo;
    }

    public boolean getIsVoice() {
        return isVoice;
    }

    public void setVoice(boolean voice) {
        isVoice = voice;
    }


}
