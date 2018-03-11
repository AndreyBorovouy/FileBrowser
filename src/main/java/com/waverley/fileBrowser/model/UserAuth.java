package com.waverley.fileBrowser.model;

import org.springframework.stereotype.Component;


public class UserAuth {

    private String userLogin;
    private String userPassword;
    private long timeChanging;
    private boolean isSuperAdmin;

    @Override
    public String toString() {
        return userLogin;
    }



    public UserAuth(String userLogin, String userPassword, long timeChanging) {
        this.userLogin = userLogin;
        this.userPassword = userPassword;
        this.timeChanging = timeChanging;
    }

    public long getTimeChanging() {
        return timeChanging;
    }

    public void setTimeChanging(long timeChanging) {
        this.timeChanging = timeChanging;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public boolean isSuperAdmin() {
        return isSuperAdmin;
    }

    public void setIsSuperAdmin(boolean isSuperAdmin) {
        this.isSuperAdmin = isSuperAdmin;
    }
}
