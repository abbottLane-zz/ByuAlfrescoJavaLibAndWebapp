package com.springapp.mvc.models;

public class LoginModel {
    private String user;
    private String password;
    private boolean invalidLogin;

    public void setInvalidLogin(boolean invalidLogin) {
        this.invalidLogin = invalidLogin;
    }

    public boolean isInvalidLogin() {
        return invalidLogin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
