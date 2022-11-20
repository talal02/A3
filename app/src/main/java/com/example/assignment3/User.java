package com.example.assignment3;

public class User {
    private String name, email, password, phno, photo;

    public User(String name, String email, String password, String phno, String photo) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phno = phno;
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhno() {
        return phno;
    }

    public String getPhoto() {
        return photo;
    }
}
