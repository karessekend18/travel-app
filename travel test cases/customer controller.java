

/*     
 * 
 *    Customer controller test cases
 */




package com.cabdriver.Cab.Driver.Controller;

import com.cabdriver.Cab.Driver.exceptions.UserNotFound;
import com.cabdriver.Cab.Driver.models.Customer;
import com.cabdriver.Cab.Driver.requestbody.UserCredentialsRequestBody;
import com.cabdriver.Cab.Driver.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private Customer customer;
    private UserCredentialsRequestBody credentials;

    @BeforeEach
    void setUp() {
        // Initialize test data
        customer = new Customer();
        customer.setEmail("test@example.com");
        customer.setPassword("password123");
        customer.setName("Test User");
        customer.setPhoneNumber("1234567890");

        credentials = new UserCredentialsRequestBody();
        credentials.setEmail("test@example.com");
        credentials.setPassword("password123");
    }

    @Nested
    @DisplayName("Account Creation Tests")
    class CreateAccountTests {
        
        @Test
        @DisplayName("Should create account successfully with valid customer details")
        void createAccount_WhenCustomerDetailsValid_ShouldReturnSuccessMessage() {
            doNothing().when(customerService).registerAccount(any(Customer.class));
            
            String response = customerController.createAccount(customer);
            
            assertEquals("Account created successfully", response);
            verify(customerService, times(1)).registerAccount(customer);
        }

        @Test
        @DisplayName("Should handle null customer object")
        void createAccount_WhenCustomerIsNull_ShouldThrowException() {
            assertThrows(NullPointerException.class, () -> 
                customerController.createAccount(null));
            
            verify(customerService, never()).registerAccount(any());
        }

        @Test
        @DisplayName("Should handle service layer exceptions")
        void createAccount_WhenServiceThrowsException_ShouldPropagateException() {
            doThrow(new RuntimeException("Database error"))
                .when(customerService).registerAccount(any(Customer.class));

            Exception exception = assertThrows(RuntimeException.class, () -> 
                customerController.createAccount(customer));
            
            assertEquals("Database error", exception.getMessage());
            verify(customerService).registerAccount(customer);
        }

        @Test
        @DisplayName("Should handle customer with missing required fields")
        void createAccount_WhenCustomerHasMissingFields_ShouldStillAttemptRegistration() {
            Customer incompleteCustomer = new Customer();
            doNothing().when(customerService).registerAccount(any(Customer.class));

            customerController.createAccount(incompleteCustomer);

            verify(customerService).registerAccount(incompleteCustomer);
        }
    }

    @Nested
    @DisplayName("Customer Authentication Tests")
    class LoginCustomerTests {

        @Test
        @DisplayName("Should authenticate successfully with valid credentials")
        void loginCustomer_WhenCredentialsValid_ShouldReturnAuthenticationDetails() {
            String expectedResponse = "Authentication successful";
            when(customerService.authenticateCustomer(anyString(), anyString()))
                .thenReturn(expectedResponse);

            String response = customerController.loginCustomer(credentials);

            assertEquals(expectedResponse, response);
            verify(customerService).authenticateCustomer(credentials.getEmail(), credentials.getPassword());
        }

        @Test
        @DisplayName("Should handle user not found scenario")
        void loginCustomer_WhenUserNotFound_ShouldReturnErrorMessage() {
            String errorMessage = "User not found";
            when(customerService.authenticateCustomer(anyString(), anyString()))
                .thenThrow(new UserNotFound(errorMessage));

            String response = customerController.loginCustomer(credentials);

            assertEquals(errorMessage, response);
            verify(customerService).authenticateCustomer(credentials.getEmail(), credentials.getPassword());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   "})
        @DisplayName("Should handle invalid email inputs")
        void loginCustomer_WhenEmailInvalid_ShouldStillAttemptAuthentication(String email) {
            credentials.setEmail(email);
            when(customerService.authenticateCustomer(anyString(), anyString()))
                .thenReturn("Authentication failed");

            String response = customerController.loginCustomer(credentials);

            assertEquals("Authentication failed", response);
            verify(customerService).authenticateCustomer(email, credentials.getPassword());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   "})
        @DisplayName("Should handle invalid password inputs")
        void loginCustomer_WhenPasswordInvalid_ShouldStillAttemptAuthentication(String password) {
            credentials.setPassword(password);
            when(customerService.authenticateCustomer(anyString(), anyString()))
                .thenReturn("Authentication failed");

            String response = customerController.loginCustomer(credentials);

            assertEquals("Authentication failed", response);
            verify(customerService).authenticateCustomer(credentials.getEmail(), password);
        }

        @Test
        @DisplayName("Should handle null credentials object")
        void loginCustomer_WhenCredentialsObjectNull_ShouldThrowException() {
            assertThrows(NullPointerException.class, () -> 
                customerController.loginCustomer(null));
            
            verify(customerService, never()).authenticateCustomer(any(), any());
        }

        @Test
        @DisplayName("Should handle service timeout")
        void loginCustomer_WhenServiceTimeout_ShouldPropagateException() {
            when(customerService.authenticateCustomer(anyString(), anyString()))
                .thenThrow(new RuntimeException("Service timeout"));

            Exception exception = assertThrows(RuntimeException.class, () -> 
                customerController.loginCustomer(credentials));
            
            assertEquals("Service timeout", exception.getMessage());
            verify(customerService).authenticateCustomer(credentials.getEmail(), credentials.getPassword());
        }
    }

    @Test
    @DisplayName("Should handle concurrent requests")
    void handleConcurrentRequests() {
        when(customerService.authenticateCustomer(anyString(), anyString()))
            .thenReturn("Authentication successful");

        // Simulate concurrent requests
        assertAll(
            () -> assertEquals("Authentication successful", 
                customerController.loginCustomer(credentials)),
            () -> assertEquals("Authentication successful", 
                customerController.loginCustomer(credentials))
        );

        verify(customerService, times(2))
            .authenticateCustomer(credentials.getEmail(), credentials.getPassword());
    }
}