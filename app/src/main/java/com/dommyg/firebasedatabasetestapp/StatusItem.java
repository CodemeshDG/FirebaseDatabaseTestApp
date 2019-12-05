package com.dommyg.firebasedatabasetestapp;

class StatusItem {

    private String status;

    StatusItem(String username) {
        this.status = username + " - I have not updated my status yet.";
    }

    StatusItem(String username, int feeling, String location, boolean isBusy) {
        this.status = createStatus(username, feeling, location, isBusy);
    }

    String getStatus() {
        return status;
    }

    private String createStatus(String username, int feeling, String location, boolean isBusy) {
        StringBuilder sb = new StringBuilder();
        sb.append(username).append(" - ");
        switch (feeling) {
            case R.id.radioButtonHappy:
                sb.append("I am happy ");
                break;

            case R.id.radioButtonIndifferent:
                sb.append("I am indifferent ");
                break;

            case R.id.radioButtonSad:
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
