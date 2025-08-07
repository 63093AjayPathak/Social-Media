package com.sm.identity;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

import com.sm.identity.dto.AuthRequest;
import com.sm.identity.service.LoggingService;

@SpringBootTest
public class LoggingDemoTest {

    @Autowired
    private LoggingService loggingService;

    @Test
    public void demoStructuredLogging() {
        System.out.println("=== DEMO: Structured Logging Output ===\n");

        // Demo 1: User Registration
        demoUserRegistration();

        // Demo 2: User Login Success
        demoUserLoginSuccess();

        // Demo 3: User Login Failure
        demoUserLoginFailure();

        // Demo 4: Email Verification
        demoEmailVerification();

        // Demo 5: Password Change
        demoPasswordChange();

        // Demo 6: Error Scenario
        demoErrorScenario();

        System.out.println("\n=== DEMO COMPLETE ===");
    }

    private void demoUserRegistration() {
        System.out.println("1. USER REGISTRATION LOG:");
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/auth/register");
        request.setRemoteAddr("192.168.1.100");
        request.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        request.addHeader("X-Corelation-ID", "reg-123456-abc-def");

        Map<String, Object> params = new HashMap<>();
        params.put("email", "john.doe@example.com");

        loggingService.logInfo("Received registration request", "reg-123456-abc-def", request, 
                             "AuthController", "addNewUser", params, null, "john.doe@example.com", null);

        loggingService.logInfo("Registration process completed", "reg-123456-abc-def", request, 
                             "AuthController", "addNewUser", params, "user-001", "john.doe@example.com", "PENDING");
        System.out.println();
    }

    private void demoUserLoginSuccess() {
        System.out.println("2. USER LOGIN SUCCESS LOG:");
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/auth/token");
        request.setRemoteAddr("192.168.1.101");
        request.addHeader("User-Agent", "PostmanRuntime/7.32.3");
        request.addHeader("X-Corelation-ID", "login-789012-xyz-uvw");

        Map<String, Object> params = new HashMap<>();
        params.put("email", "jane.smith@example.com");

        loggingService.logInfo("Login/token request received", "login-789012-xyz-uvw", request, 
                             "AuthController", "getToken", params, null, "jane.smith@example.com", null);

        loggingService.logInfo("User authenticated and authorized", "login-789012-xyz-uvw", request, 
                             "AuthController", "getToken", params, "user-002", "jane.smith@example.com", "USER");
        System.out.println();
    }

    private void demoUserLoginFailure() {
        System.out.println("3. USER LOGIN FAILURE LOG:");
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/auth/token");
        request.setRemoteAddr("192.168.1.102");
        request.addHeader("User-Agent", "curl/7.68.0");
        request.addHeader("X-Corelation-ID", "login-fail-345678-ghi-jkl");

        Map<String, Object> params = new HashMap<>();
        params.put("email", "invalid.user@example.com");

        loggingService.logWarn("Failed login attempt", "login-fail-345678-ghi-jkl", request, 
                             "AuthController", "getToken", params, null, "invalid.user@example.com", null);
        System.out.println();
    }

    private void demoEmailVerification() {
        System.out.println("4. EMAIL VERIFICATION LOG:");
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/auth/verify/eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...");
        request.setRemoteAddr("192.168.1.103");
        request.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)");
        request.addHeader("X-Corelation-ID", "verify-901234-mno-pqr");

        Map<String, Object> params = new HashMap<>();
        params.put("token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...");

        loggingService.logInfo("Email verification request received", "verify-901234-mno-pqr", request, 
                             "AuthController", "verifyEmail", params, null, null, null);

        loggingService.logInfo("User creation verified in User-Service", "verify-901234-mno-pqr", request, 
                             "AuthServiceImpl", "authorizeUser", params, "user-003", "john.doe@example.com", "AUTHORIZED");
        System.out.println();
    }

    private void demoPasswordChange() {
        System.out.println("5. PASSWORD CHANGE LOG:");
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/auth/change_password/eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...");
        request.setRemoteAddr("192.168.1.104");
        request.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36");
        request.addHeader("X-Corelation-ID", "pwd-change-567890-stu-vwx");

        Map<String, Object> params = new HashMap<>();
        params.put("token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...");

        loggingService.logInfo("Password change request received", "pwd-change-567890-stu-vwx", request, 
                             "AuthController", "changePassword", params, null, null, null);

        loggingService.logInfo("Password updated", "pwd-change-567890-stu-vwx", request, 
                             "AuthServiceImpl", "changePassword", params, "user-004", "jane.smith@example.com", "USER");
        System.out.println();
    }

    private void demoErrorScenario() {
        System.out.println("6. ERROR SCENARIO LOG:");
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/auth/register");
        request.setRemoteAddr("192.168.1.105");
        request.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
        request.addHeader("X-Corelation-ID", "error-123456-abc-def");

        Map<String, Object> params = new HashMap<>();
        params.put("email", "duplicate@example.com");

        Exception emailException = new RuntimeException("Failed to send email: Connection timeout");

        loggingService.logError("Failed to send registration email", "error-123456-abc-def", request, 
                              "AuthServiceImpl", "saveUser", params, "user-005", "duplicate@example.com", "PENDING", emailException);
        System.out.println();
    }
} 