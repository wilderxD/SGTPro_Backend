package com.example.sgtpro.SGTPRO.exception;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponse {
    
    private LocalDateTime timestamp;
    
    private Integer status;
    
    private String error;
    
    private String message;
    
    private String path;
    
    private List<String> details;

    public ErrorResponse() {
    }

    public ErrorResponse(LocalDateTime timestamp, Integer status, String error, String message, String path, List<String> details) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.details = details;
    }

    public List<String> getDetails() {
        return details;
    }
 
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }
    
    public static ErrorResponseBuilder builder(){
        return new ErrorResponseBuilder();
    }
    
    public static class ErrorResponseBuilder{
        private LocalDateTime timestamp;    
        private Integer status;    
        private String error;    
        private String message;    
        private String path;
        private List<String> details;
        
        public ErrorResponseBuilder timestamp(LocalDateTime timestamp){
            this.timestamp = timestamp;
            return this;
        }
        
        public ErrorResponseBuilder status(Integer status){
            this.status = status;
            return this;
        }
        
        public ErrorResponseBuilder error(String error){
            this.error = error;
            return this;
        }
        
        public ErrorResponseBuilder message(String message){
            this.message = message;
            return this;
        }
        
        public ErrorResponseBuilder path(String path){
            this.path = path;
            return this;
        }
        
        public ErrorResponseBuilder details(List<String> details){
            this.details = details;
            return this;
        }
        
        public ErrorResponse build(){
            return new ErrorResponse(timestamp, status, error, message, path, details);
        }
    }
    
}
