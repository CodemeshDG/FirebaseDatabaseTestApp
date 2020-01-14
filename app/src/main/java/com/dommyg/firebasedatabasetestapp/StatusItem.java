package com.dommyg.firebasedatabasetestapp;

/**
 * Contains information for a user's status.
 */
class StatusItem {

    private String username;
    private String feeling;
    private String location;
    private boolean isBusy;

    public StatusItem() {
        // Empty constructor for Firestore RecyclerView use
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFeeling() {
        return feeling;
    }

    public void setFeeling(String feeling) {
        this.feeling = feeling;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean getIsBusy() {
        return isBusy;
    }

    public void setIsBusy(boolean busy) {
        isBusy = busy;
    }

    /**
     * Returns a String which reports a user's status. If the user has just joined the room, they
     * will not have a feeling set, and thus will return a string stating they have not updated their
     * status yet.
     */
    String getStatus() {
        if (feeling != null) {
            return createStatus(username, Integer.valueOf(feeling), location, isBusy);
        }
        return username + " - I have not updated my status yet.";
    }

    /**
     * Constructs and returns a String based on the user status variables.
     */
    private String createStatus(String username, int feeling, String location, boolean isBusy) {
        StringBuilder sb = new StringBuilder();
        sb.append(username).append(" - ");
        switch (feeling) {
            case 1:
                sb.append("I am happy ");
                break;

            case 2:
                sb.append("I am indifferent ");
                break;

            case 3:
                sb.append("I am sad ");
                break;
        }
        if (isBusy) {
            sb.append("and currently busy");
        } else {
            sb.append("and currently not busy");
        }
        if (location == null) {
            sb.append(".");
            return sb.toString();
        } else {
            sb.append(" @ ")
                    .append(location)
                    .append(".");
            return sb.toString();
        }
    }
}
