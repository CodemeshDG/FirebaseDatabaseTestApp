package com.dommyg.firebasedatabasetestapp;

public class StatusItem {

    private String status;

    public StatusItem() {
        // Empty constructor required.
    }

    public StatusItem(String username) {
        this.status = username + " - I have not updated my status yet.";
    }

    public StatusItem(String username, int feeling, String location, boolean isBusy) {
        this.status = createStatus(username, feeling, location, isBusy);
    }

    public String getStatus() {
        return status;
    }

    private String createStatus(String username, int feeling, String location, boolean isBusy) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(username).append(" - ");
        switch (feeling) {
            case R.id.radioButtonHappy:
                stringBuilder.append("I am happy ");
                break;

            case R.id.radioButtonIndifferent:
                stringBuilder.append("I am indifferent ");
                break;

            case R.id.radioButtonSad:
                stringBuilder.append("I am sad ");
                break;
        }
        if (isBusy) {
            stringBuilder.append("and currently busy");
        } else {
            stringBuilder.append("and currently not busy");
        }
        if (location == null) {
            stringBuilder.append(".");
            return stringBuilder.toString();
        } else {
            stringBuilder.append(" @ ")
                    .append(location)
                    .append(".");
            return stringBuilder.toString();
        }
    }
}
