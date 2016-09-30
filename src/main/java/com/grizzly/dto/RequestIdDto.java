package com.grizzly.dto;

/**
 * Created by Samarth 9/25/16
 */

/*
    DTOs are used to exchange data with your client
    DTOs can process data from multiple entities and present it to the client in the format they want
    DTOs are like wrappers or an abstraction layer around the database models/entity classes
    This class defines the response body format for the POST /emails API */

public class RequestIdDto {

    public RequestIdDto(String requestId){
        this.requestID=requestId;
    }
    private String requestID;
    public String getRequestID() {
        return requestID;
    }
    public void setRequestID(String requestId) {
        this.requestID = requestId;
    }
}
