package com.cabdriver.Cab.Driver.Controller;

import com.cabdriver.Cab.Driver.exceptions.UserNotFound;
import com.cabdriver.Cab.Driver.models.Customer;
import com.cabdriver.Cab.Driver.requestbody.UserCredentialsRequestBody;
import com.cabdriver.Cab.Driver.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController{

    @Autowired
    CustomerService customerService;

    @PostMapping("/api/customer/register") //here the customers are getting registerd with all the given details
    public String createAccount(@RequestBody Customer customer) {
        customerService.registerAccount(customer);
        return "Account created successfully";
    }

    @GetMapping("/api/customer/authenticate") //for the further process we need to authenticate the customer do they get registerd or not
    public String loginCustomer(@RequestBody UserCredentialsRequestBody userCredentialsRequestBody){
        String email = userCredentialsRequestBody.getEmail();
        String password = userCredentialsRequestBody.getPassword();
        try{
            String authenticationDetails = customerService.authenticateCustomer(email, password);
            return authenticationDetails;
        }catch (UserNotFound userNotFound){
            return userNotFound.getMessage();

        }
    }

}
