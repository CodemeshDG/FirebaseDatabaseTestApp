package com.dommyg.firebasedatabasetestapp;

public class StatusItem {

    private String status;

    public StatusItem(String username) {
        this.status = username + " - I have not updated my status yet.";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
