package com.cabdriver.Cab.Driver.service;


import com.cabdriver.Cab.Driver.Repositary.DriverRepository;
import com.cabdriver.Cab.Driver.models.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DriverService {

    @Autowired
    DriverRepository driverRepository;
    public void registerDriver(Driver driver){
        driverRepository.save(driver);
    }
    public Driver getDriverByEmail(String emailID){
        return driverRepository.findByEmailID(emailID);
    }
}
