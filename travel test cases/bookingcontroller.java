

/* 
              Test cases for Booking controller
              
              
              */




package com.cabdriver.Cab.Driver.Controller;

import com.cabdriver.Cab.Driver.Repositary.BookingRepository;
import com.cabdriver.Cab.Driver.Repositary.CustomerRepository;
import com.cabdriver.Cab.Driver.Repositary.DriverRepository;
import com.cabdriver.Cab.Driver.exceptions.InvalidOperationException;
import com.cabdriver.Cab.Driver.exceptions.ResoursceDoesNotException;
import com.cabdriver.Cab.Driver.exceptions.UserNotFound;
import com.cabdriver.Cab.Driver.models.Booking;
import com.cabdriver.Cab.Driver.models.Customer;
import com.cabdriver.Cab.Driver.models.Driver;
import com.cabdriver.Cab.Driver.requestbody.CustomerBookingRequestBody;
import com.cabdriver.Cab.Driver.responsebody.BookingResponseBody;
import com.cabdriver.Cab.Driver.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingController bookingController;

    private CustomerBookingRequestBody bookingRequestBody;
    private Customer customer;
    private Driver driver;
    private Booking booking;

    @BeforeEach
    void setUp() {
        bookingRequestBody = new CustomerBookingRequestBody();
        bookingRequestBody.setStartingLocation("Start Location");
        bookingRequestBody.setEndingLocation("End Location");
        bookingRequestBody.setState("ACCEPTED");
        bookingRequestBody.setBillAmount(100);

        customer = new Customer();
        customer.setId(1);
        customer.setBookings(new ArrayList<>());

        driver = new Driver();
        driver.setId(1);
        driver.setBookings(new ArrayList<>());

        booking = new Booking();
        booking.setId(1);
    }

    @Test
    void createCustomerBooking_WhenStateIsAccepted_ShouldReturnAcceptedMessage() {
        when(bookingService.handleCustomerRequest(
            anyString(), anyString(), anyString(), anyInt(), anyInt()))
            .thenReturn("Success");

        String response = bookingController.createCustomerBooking(bookingRequestBody, 1);

        assertEquals("Diver accepted the booking", response);
        verify(bookingService).handleCustomerRequest(
            "Start Location", "End Location", "ACCEPTED", 100, 1);
    }

    @Test
    void createCustomerBooking_WhenStateIsCancelled_ShouldReturnCancelledMessage() {
        bookingRequestBody.setState("CANCELLED");
        
        when(bookingService.handleCustomerRequest(
            anyString(), anyString(), anyString(), anyInt(), anyInt()))
            .thenReturn("Success");

        String response = bookingController.createCustomerBooking(bookingRequestBody, 1);

        assertEquals("booking was cancelled", response);
    }

    @Test
    void createCustomerBooking_WhenUserNotFound_ShouldReturnErrorMessage() {
        when(bookingService.handleCustomerRequest(
            anyString(), anyString(), anyString(), anyInt(), anyInt()))
            .thenThrow(new UserNotFound("User not found"));

        String response = bookingController.createCustomerBooking(bookingRequestBody, 1);

        assertEquals("User not found", response);
    }

    @Test
    void updating_ShouldUpdateRelationships() {
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));

        bookingController.updating(1, 1, 1);

        verify(customerRepository).save(customer);
        verify(driverRepository).save(driver);
        verify(bookingRepository).save(booking);
        
        assertEquals(1, customer.getBookings().size());
        assertEquals(1, driver.getBookings().size());
        assertEquals(driver, booking.getDriver());
        assertEquals(customer, booking.getCustomer());
    }

    @Test
    void getBookingByStatus_ShouldReturnBookingList() {
        List<BookingResponseBody> expectedResponse = new ArrayList<>();
        when(bookingService.getBookingStatus(anyString())).thenReturn(expectedResponse);

        List<BookingResponseBody> response = bookingController.getBookingByStatus("ACCEPTED");

        assertEquals(expectedResponse, response);
        verify(bookingService).getBookingStatus("ACCEPTED");
    }

    @Test
    void updateBookingStatus_WhenSuccessful_ShouldReturnCreatedStatus() {
        when(bookingService.updateBooking(anyString(), anyString(), anyInt()))
            .thenReturn("Updated successfully");

        ResponseEntity response = bookingController.updateBookingStatus("ACCEPT", "test@email.com", 1);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Updated successfully", response.getBody());
    }

    @Test
    void updateBookingStatus_WhenUserNotFound_ShouldReturnNotFound() {
        when(bookingService.updateBooking(anyString(), anyString(), anyInt()))
            .thenThrow(new UserNotFound("User not found"));

        ResponseEntity response = bookingController.updateBookingStatus("ACCEPT", "test@email.com", 1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }

    @Test
    void updateBookingStatus_WhenInvalidOperation_ShouldReturnMethodNotAllowed() {
        when(bookingService.updateBooking(anyString(), anyString(), anyInt()))
            .thenThrow(new InvalidOperationException("Invalid operation"));

        ResponseEntity response = bookingController.updateBookingStatus("INVALID", "test@email.com", 1);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertEquals("Invalid operation", response.getBody());
    }

    @Test
    void updateBookingStatus_WhenResourceNotFound_ShouldReturnNotFound() {
        when(bookingService.updateBooking(anyString(), anyString(), anyInt()))
            .thenThrow(new ResoursceDoesNotException("Resource not found"));

        ResponseEntity response = bookingController.updateBookingStatus("ACCEPT", "test@email.com", 1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Resource not found", response.getBody());
    }
}