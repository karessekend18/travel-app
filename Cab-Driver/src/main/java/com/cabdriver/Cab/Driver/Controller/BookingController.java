package com.cabdriver.Cab.Driver.Controller;

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

    @Autowired
    BookingService bookingService;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    DriverRepository driverRepository;

    @Autowired
    BookingRepository bookingRepository;


    @PostMapping("/request")
    public String createCustomerBooking(@RequestBody CustomerBookingRequestBody customerBookingRequestBody, @RequestParam int customerId){

        String startingLocation = customerBookingRequestBody.getStartingLocation();
        String endingLocation = customerBookingRequestBody.getEndingLocation();
        String state = customerBookingRequestBody.getState();//
        int billAmount = customerBookingRequestBody.getBillAmount();//

        try{
            bookingService.handleCustomerRequest(startingLocation, endingLocation, state, billAmount ,customerId);
            if (state.equals("ACCEPTED")){ //when driver accepted the ride
                return "Diver accepted the booking";
            } else if (state.equals("CANCELLED")) {//when ride is cancelled
                return "booking was cancelled";
            } //
            return "Waiting for the driver to accept the booking"; //"Waiting for driver to accept"
        }catch(UserNotFound userNotFound){
            return userNotFound.getMessage();

        }
    }
    @PutMapping("/updating")  //here we are updating(mapping) customerId to bookingId and driverId to booking Id
    public void updating(@RequestParam int customerId, @RequestParam int driverId, @RequestParam int bookingId){
        Customer customer = customerRepository.findById(customerId).get();
        Driver driver = driverRepository.findById(driverId).get();
        Booking booking = bookingRepository.findById(bookingId).get();

        List<Booking> driverBooking = driver.getBookings();
        driverBooking.add(booking);
        driver.setBookings(driverBooking);
        List<Booking> customerBooking = customer.getBookings();
        customerBooking.add(booking);
        customer.setBookings(customerBooking);

        booking.setDriver(driver);
        booking.setCustomer(customer);

        customerRepository.save(customer);
        driverRepository.save(driver);
        bookingRepository.save(booking);



    }

    @GetMapping("/all")  //here we can see the list of bookings of different state
    public List<BookingResponseBody> getBookingByStatus(@RequestParam String state){
        return bookingService.getBookingStatus(state);

    }
    @PutMapping("/update")//here with this we can update the status
    public ResponseEntity updateBookingStatus(@RequestParam String oper, @RequestParam String email, @RequestParam Integer bookingId){

        try{
            String response = bookingService.updateBooking(oper,email ,bookingId);
            return new ResponseEntity(response, HttpStatus.CREATED);//CREATED
        }catch (UserNotFound userNotFound){
            return new ResponseEntity(userNotFound.getMessage(), HttpStatus.NOT_FOUND);

        }catch (InvalidOperationException invalidOperationException){
            return new ResponseEntity(invalidOperationException.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
        }catch (ResoursceDoesNotException resoursceDoesNotException){
            return new ResponseEntity(resoursceDoesNotException.getMessage(), HttpStatus.NOT_FOUND);
        }

    }

}
