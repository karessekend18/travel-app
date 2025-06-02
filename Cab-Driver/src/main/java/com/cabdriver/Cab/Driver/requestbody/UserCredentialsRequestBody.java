package com.cabdriver.Cab.Driver.requestbody;


public class UserCredentialsRequestBody {
    private String email;
    private String password;

    public UserCredentialsRequestBody() {
    }

    public UserCredentialsRequestBody(String email, String password) {
        this.email = email;
        this.password = password;
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
}
