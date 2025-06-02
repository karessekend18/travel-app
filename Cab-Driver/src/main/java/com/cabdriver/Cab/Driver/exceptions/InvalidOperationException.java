package com.cabdriver.Cab.Driver.exceptions;

public class InvalidOperationException extends RuntimeException{
    public InvalidOperationException(String mssg){
        super(mssg);
    }
}
