package com.sm.identity.service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sm.identity.dto.LogResponse;
import com.sm.identity.dto.LogResponse.ControllerInfo;
import com.sm.identity.dto.LogResponse.ErrorInfo;
import com.sm.identity.dto.LogResponse.HttpInfo;
import com.sm.identity.dto.LogResponse.UserInfo;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class LoggingService {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${spring.application.name:identity-service}")
    private String applicationName;
    
    @Value("${spring.profiles.active:development}")
    private String environment;
    
    public void logInfo(String message, String correlationId, HttpServletRequest request, 
                       String controllerName, String action, Map<String, Object> params, 
                       String userId, String userEmail, String userRole) {
        log("INFO", message, correlationId, request, controllerName, action, params, userId, userEmail, userRole, null);
    }
    
    public void logWarn(String message, String correlationId, HttpServletRequest request, 
                       String controllerName, String action, Map<String, Object> params, 
                       String userId, String userEmail, String userRole) {
        log("WARN", message, correlationId, request, controllerName, action, params, userId, userEmail, userRole, null);
    }
    
    public void logError(String message, String correlationId, HttpServletRequest request, 
                        String controllerName, String action, Map<String, Object> params, 
                        String userId, String userEmail, String userRole, Exception exception) {
        log("ERROR", message, correlationId, request, controllerName, action, params, userId, userEmail, userRole, exception);
    }
    
    private void log(String level, String message, String correlationId, HttpServletRequest request, 
                    String controllerName, String action, Map<String, Object> params, 
                    String userId, String userEmail, String userRole, Exception exception) {
        
        try {
            LogResponse logResponse = LogResponse.builder()
                    .timestamp(Instant.now().toString())
                    .level(level)
                    .message(message)
                    .application(applicationName)
                    .environment(environment)
                    .service("identity-service")
                    .loggerName(controllerName)
                    .threadName(Thread.currentThread().getName())
                    .correlationId(correlationId != null ? correlationId : generateCorrelationId())
                    .http(buildHttpInfo(request))
                    .controller(buildControllerInfo(controllerName, action, params))
                    .user(buildUserInfo(userId, userEmail, userRole))
                    .error(buildErrorInfo(exception))
                    .build();
            
            String jsonLog = objectMapper.writeValueAsString(logResponse);
            logger.info(jsonLog);
            
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize log response", e);
        }
    }
    
    private String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }
    
    private HttpInfo buildHttpInfo(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        
        return HttpInfo.builder()
                .method(request.getMethod())
                .path(request.getRequestURI())
                .clientIp(getClientIp(request))
                .userAgent(request.getHeader("User-Agent"))
                .build();
    }
    
    private ControllerInfo buildControllerInfo(String controllerName, String action, Map<String, Object> params) {
        return ControllerInfo.builder()
                .name(controllerName)
                .action(action)
                .params(params)
                .build();
    }
    
    private UserInfo buildUserInfo(String userId, String userEmail, String userRole) {
        if (userId == null && userEmail == null && userRole == null) {
            return null;
        }
        
        return UserInfo.builder()
                .id(userId)
                .email(userEmail)
                .role(userRole)
                .build();
    }
    
    private ErrorInfo buildErrorInfo(Exception exception) {
        if (exception == null) {
            return ErrorInfo.builder()
                    .message(null)
                    .stackTrace(null)
                    .build();
        }
        
        return ErrorInfo.builder()
                .message(exception.getMessage())
                .stackTrace(getStackTraceAsString(exception))
                .build();
    }
    
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    private String getStackTraceAsString(Exception exception) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : exception.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
} 