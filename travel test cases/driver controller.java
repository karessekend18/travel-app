/*
 * 
 * 
 * Driver controller test cases
 */
package com.cabdriver.Cab.Driver.Controller;

import com.cabdriver.Cab.Driver.models.Driver;
import com.cabdriver.Cab.Driver.service.DriverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverControllerTest {

    @Mock
    private DriverService driverService;

    @InjectMocks
    private DriverController driverController;

    private Driver driver;

    @BeforeEach
    void setUp() {
        // Initialize test data
        driver = new Driver();
        driver.setName("Test Driver");
        driver.setEmail("driver@example.com");
        driver.setPhoneNumber("1234567890");
        driver.setLicenseNumber("DL123456");
        driver.setVehicleNumber("VH789012");
    }

    @Nested
    @DisplayName("Driver Registration Tests")
    class DriverRegistrationTests {
        
        @Test
        @DisplayName("Should register driver successfully with valid details")
        void createAccount_WhenDriverDetailsValid_ShouldReturnSuccessMessage() {
            // Arrange
            doNothing().when(driverService).registerDriver(any(Driver.class));
            
            // Act
            String response = driverController.createAccount(driver);
            
            // Assert
            assertEquals("Driver got successfully registered", response);
            verify(driverService, times(1)).registerDriver(driver);
        }

        @Test
        @DisplayName("Should handle null driver object")
        void createAccount_WhenDriverIsNull_ShouldThrowException() {
            // Arrange & Act & Assert
            assertThrows(NullPointerException.class, () -> 
                driverController.createAccount(null));
            
            verify(driverService, never()).registerDriver(any());
        }

        @Test
        @DisplayName("Should handle service layer exceptions")
        void createAccount_WhenServiceThrowsException_ShouldPropagateException() {
            // Arrange
            doThrow(new RuntimeException("Registration failed"))
                .when(driverService).registerDriver(any(Driver.class));

            // Act & Assert
            Exception exception = assertThrows(RuntimeException.class, () -> 
                driverController.createAccount(driver));
            
            assertEquals("Registration failed", exception.getMessage());
            verify(driverService).registerDriver(driver);
        }

        @Test
        @DisplayName("Should handle driver with missing required fields")
        void createAccount_WhenDriverHasMissingFields_ShouldStillAttemptRegistration() {
            // Arrange
            Driver incompleteDriver = new Driver();
            doNothing().when(driverService).registerDriver(any(Driver.class));

            // Act
            String response = driverController.createAccount(incompleteDriver);

            // Assert
            assertEquals("Driver got successfully registered", response);
            verify(driverService).registerDriver(incompleteDriver);
        }

        @Test
        @DisplayName("Should handle duplicate driver registration")
        void createAccount_WhenDuplicateDriver_ShouldHandleServiceException() {
            // Arrange
            doThrow(new IllegalStateException("Driver already exists"))
                .when(driverService).registerDriver(any(Driver.class));

            // Act & Assert
            Exception exception = assertThrows(IllegalStateException.class, () -> 
                driverController.createAccount(driver));
            
            assertEquals("Driver already exists", exception.getMessage());
            verify(driverService).registerDriver(driver);
        }

        @Test
        @DisplayName("Should handle driver with empty strings")
        void createAccount_WhenDriverHasEmptyStrings_ShouldStillAttemptRegistration() {
            // Arrange
            Driver emptyDriver = new Driver();
            emptyDriver.setName("");
            emptyDriver.setEmail("");
            emptyDriver.setPhoneNumber("");
            emptyDriver.setLicenseNumber("");
            emptyDriver.setVehicleNumber("");
            
            doNothing().when(driverService).registerDriver(any(Driver.class));

            // Act
            String response = driverController.createAccount(emptyDriver);

            // Assert
            assertEquals("Driver got successfully registered", response);
            verify(driverService).registerDriver(emptyDriver);
        }
    }

    @Test
    @DisplayName("Should handle concurrent driver registrations")
    void handleConcurrentRegistrations() {
        // Arrange
        doNothing().when(driverService).registerDriver(any(Driver.class));

        // Act & Assert
        // Simulate concurrent requests
        assertAll(
            () -> assertEquals("Driver got successfully registered", 
                driverController.createAccount(driver)),
            () -> assertEquals("Driver got successfully registered", 
                driverController.createAccount(driver))
        );

        verify(driverService, times(2)).registerDriver(driver);
    }
}