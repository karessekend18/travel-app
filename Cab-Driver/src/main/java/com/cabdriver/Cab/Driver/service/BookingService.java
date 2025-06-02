package com.cabdriver.Cab.Driver.service;

import com.cabdriver.Cab.Driver.Repositary.CustomerRepository;
import com.cabdriver.Cab.Driver.Repositary.DriverRepository;
import com.cabdriver.Cab.Driver.exceptions.InvalidOperationException;
import com.cabdriver.Cab.Driver.exceptions.ResoursceDoesNotException;
import com.cabdriver.Cab.Driver.models.Driver;

import com.cabdriver.Cab.Driver.Repositary.BookingRepository;
import com.cabdriver.Cab.Driver.exceptions.UserNotFound;
import com.cabdriver.Cab.Driver.models.AppUser;
import com.cabdriver.Cab.Driver.models.Booking;
import com.cabdriver.Cab.Driver.models.Customer;
import com.cabdriver.Cab.Driver.responsebody.BookingResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import com.cabdriver.Cab.Driver.requestbody.UserCredentialsRequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@Service
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);//bridge it

    @Autowired
    CustomerService customerService;

    @Autowired
    DriverService driverService;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    DriverRepository driverRepository;

    public void handleCustomerRequest(String startingLocation, String endingLocation, String state, int billAmount,  int customerId){
        //we need to validate as they are valid or invalid
        //if customer id is there we will validate or else not
        Customer customer = customerService.getCustomerById(customerId);
        if(customer == null){
            throw new UserNotFound(String.format("User with id %d does not exist", customerId));
        }

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setStatus(state);//"DRAFT"
        booking.setBillAmount(billAmount);//0
        booking.setStartingLocation(startingLocation);
        booking.setEndingLocation(endingLocation);
        bookingRepository.save(booking);


        //we need to create booking

    }

    public List<BookingResponseBody> getBookingStatus(String state) {
        logger.info("Getting bookings with status: {}", state);
        List<Booking> bookingList = bookingRepository.getBookingByStatus(state);
        logger.info("Number of bookings found: {}", bookingList.size());



        //public List<BookingResponseBody> getBookingStatus(String sate){
    //    List<Booking> bookingList = bookingRepository.getBookingByStatus(sate);
        List<BookingResponseBody> bookingResponseBodyList = new ArrayList<>();
        for(Booking booking: bookingList){
            BookingResponseBody bookingResponseBody = new BookingResponseBody();
            bookingResponseBody.setBookingID(booking.getId());
            bookingResponseBody.setCustomerID(booking.getCustomer().getId());
            bookingResponseBody.setStartingLocation(booking.getStartingLocation());
            bookingResponseBody.setEndingLocation(booking.getEndingLocation());
            bookingResponseBody.setCustomerName(booking.getCustomer().getFirstName());
            bookingResponseBody.setBillingAmount(booking.getBillAmount());
            bookingResponseBody.setStatus(booking.getStatus());
            bookingResponseBodyList.add(bookingResponseBody);

        }
        return bookingResponseBodyList;
    }

    public String updateBooking(String operation, String email, Integer bookingId){
        //we need to identify is this customer email or is this a driver email

        Customer customer = customerService.getCustomerByEmail(email);//customerRepository.findById(userId).get();
        Driver driver = driverService.getDriverByEmail(email);
        String userType = "";
        Integer userId = -1;
        AppUser user = null;

        if(customer != null){
            userId = customer.getId();
            userType = "CUSTOMER";
            user = customer;
        } else if (driver != null) {
            userId = driver.getId();
            userType = "DRIVER";
            user = driver;

        }else{
            throw new UserNotFound(String.format("User with id %d does not exist",userId ));
        }

        Booking booking = bookingRepository.findById(bookingId).orElse(null);
            if(booking == null){
                throw new ResoursceDoesNotException(String.format("Booking with id %d does not exist in system", bookingId));
        }

        if(operation.equals("ACCEPT")){
            if(userType.equals("CUSTOMER")){
                throw new InvalidOperationException(String.format("Customer can't take rides"));
            }


            booking.setDriver(driver);
            booking.setStatus("ACCEPTED");
            bookingRepository.save(booking);
            //booking.setBillAmount(100);
            return String.format("Driver with id %d accepted booking with id %d", userId, bookingId);

        } else if (operation.equals("CANCELS")) {
            if(userType.equals("CUSTOMER")){
                if(booking.getCustomer().getId() == userId){
                booking.setStatus("CANCELLED");
                bookingRepository.save(booking);
                return String.format("Customer with id %d cancelled ride with booking id %d", userId, bookingId);
            }else{
                    throw new InvalidOperationException(String.format("Customer with id %d is not allowed to cancel booking with id %d", userId, bookingId));
                }

        } else if (userType.equals("DRIVER")) {
                if(booking.getDriver().getId() == userId){
                    booking.setStatus("CANCELLED");
                    bookingRepository.save(booking);
                    return String.format("Driver with id % cancelled booking with id %d", userId,bookingId);
                }else {
                    throw new InvalidOperationException(String.format("Driver with id %d is not allowed to cancel booking with id %d", userId, bookingId));
                }
            }
        }
        return "";
    }
}
