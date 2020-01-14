package com.dommyg.firebasedatabasetestapp;

class JoinedRoomItem {
    private String roomName;
    private String password;
    private boolean isOwner;

    public JoinedRoomItem() {
        // Empty constructor for Firestore RecyclerView use
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(boolean owner) {
        isOwner = owner;
    }
}
