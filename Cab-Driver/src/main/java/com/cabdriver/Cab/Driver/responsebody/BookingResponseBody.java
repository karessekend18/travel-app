package com.cabdriver.Cab.Driver.responsebody;

public class BookingResponseBody {

    private int bookingID;
    private int customerID;
    private String CustomerName;
    private String startingLocation;
    private String endingLocation;
    private int billingAmount;
    private String status;

    public BookingResponseBody() {
    }

    public BookingResponseBody(int bookingID, int customerID, String CustomerName, String startingLocation, String endingLocation, int billingAmount, String status) {
        this.bookingID = bookingID;
        this.customerID = customerID;
        this.CustomerName = CustomerName;
        this.startingLocation = startingLocation;
        this.endingLocation = endingLocation;
        this.billingAmount = billingAmount;
        this.status = status;
    }
    public int getBookingID() {
        return bookingID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public String getStartingLocation() {
        return startingLocation;
    }

    public String getEndingLocation() {
        return endingLocation;
    }

    public int getBillingAmount() {
        return billingAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setBookingID(int bookingID) {
        this.bookingID = bookingID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public void setStartingLocation(String startingLocation) {
        this.startingLocation = startingLocation;
    }

    public void setEndingLocation(String endingLocation) {
        this.endingLocation = endingLocation;
    }

    public void setBillingAmount(int billingAmount) {
        this.billingAmount = billingAmount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
