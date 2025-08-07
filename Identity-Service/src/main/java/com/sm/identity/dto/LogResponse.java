package com.sm.identity.dto;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogResponse {
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    @JsonProperty("level")
    private String level;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("application")
    private String application;
    
    @JsonProperty("environment")
    private String environment;
    
    @JsonProperty("service")
    private String service;
    
    @JsonProperty("logger_name")
    private String loggerName;
    
    @JsonProperty("thread_name")
    private String threadName;
    
    @JsonProperty("correlation_id")
    private String correlationId;
    
    @JsonProperty("http")
    private HttpInfo http;
    
    @JsonProperty("controller")
    private ControllerInfo controller;
    
    @JsonProperty("user")
    private UserInfo user;
    
    @JsonProperty("error")
    private ErrorInfo error;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class HttpInfo {
        @JsonProperty("method")
        private String method;
        
        @JsonProperty("path")
        private String path;
        
        @JsonProperty("status_code")
        private Integer statusCode;
        
        @JsonProperty("request_id")
        private String requestId;
        
        @JsonProperty("client_ip")
        private String clientIp;
        
        @JsonProperty("user_agent")
        private String userAgent;
        
        @JsonProperty("response_time_ms")
        private Long responseTimeMs;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ControllerInfo {
        @JsonProperty("name")
        private String name;
        
        @JsonProperty("action")
        private String action;
        
        @JsonProperty("params")
        private Map<String, Object> params;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UserInfo {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("email")
        private String email;
        
        @JsonProperty("role")
        private String role;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorInfo {
        @JsonProperty("message")
        private String message;
        
        @JsonProperty("stack_trace")
        private String stackTrace;
    }
} 