package com.grizzly.dto;

import java.util.Set;

/**
 * Created by Samarth 9/25/16
 */
/*
    DTOs are used to exchange data with your client
    DTOs can process data from multiple entities and present it to the client in the format they want
    DTOs are like wrappers or an abstraction layer around the database models/entity classes
    This class defines the response format for the GET /emails API */

public class EmailResponseDto {
    private int total;
    Set<String> values;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Set<String> getValues() {
        return values;
    }

    public void setValues(Set<String> values) {
        this.values = values;
    }

    public EmailResponseDto(Set<String> values, int total){
        this.values = values;
        this.total = values.size();

    }


}
