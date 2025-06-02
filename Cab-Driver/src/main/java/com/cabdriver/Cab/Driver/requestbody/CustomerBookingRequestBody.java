package com.cabdriver.Cab.Driver.requestbody;


public class CustomerBookingRequestBody {
    private String email;
    private String password;
    private String startingLocation;
    private String endingLocation;
    private int billAmount;
    private String state;
    private int distance;

    public String getStartingLocation() {
        return startingLocation;
    }

    public void setStartingLocation(String startingLocation) {
        this.startingLocation = startingLocation;
    }

    public String getEndingLocation() {
        return endingLocation;
    }

    public void setEndingLocation(String endingLocation) {
        this.endingLocation = endingLocation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(int billAmount) {
        this.billAmount = billAmount;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public CustomerBookingRequestBody() {
    }

    public CustomerBookingRequestBody(String email, String password, String startingLocation, String endingLocation, int billAmount, int distance,String state) {
        this.email = email;
        this.password = password;
        this.startingLocation = startingLocation;
        this.endingLocation = endingLocation;
        this.billAmount = billAmount;
        this.state = state;
        this.distance = distance;
    }
}
