package com.grizzly.controllers;


import com.grizzly.dto.EmailResponseDto;
import com.grizzly.dto.OAuthTokenDto;
import com.grizzly.dto.RequestIdDto;
import com.grizzly.exceptions.GrizzlyException;
import com.grizzly.helpers.Constants;
import com.grizzly.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * Created by Samarth 9/25/16
 */

/*
This class is an example of a controller class for your REST APIs
Every incoming request is mapped to a particular controller's method
 */
@RestController
@RequestMapping(value = Constants.API_BASE_URL + "emails")
public class EmailController {

    @Resource
    EmailService emailService;

    private static final Logger log = LoggerFactory.getLogger(EmailController.class);

    /*
     GET /emails Request method for the email controller.
     It accepts requestIds and returns a list of all the distinct email addresses
     as well as the total count of distinct email addresses for each requestID.
     The method throws an illegal arguement exception if the request parameter is null or missing
     */
    @RequestMapping(value = "/{ids}", method = RequestMethod.GET)
    public List<EmailResponseDto> getOffer(@PathVariable String ids) {

        if(ids==null||ids.isEmpty()) {
            throw new IllegalArgumentException("Parameter ids should not be 'null' or empty");
        }

        List<EmailResponseDto> emailResponseDtos = emailService.handleDistinctEmailRequest(ids.split(","));
        return emailResponseDtos;
    }

    /*
    POST /emails Request method for the email controller. Accepts a Google oAUTH token,
    and counts the number of distinct email addresses he/she has received emails from over the last 2 months.
    Return a status code 201 on success, and a requestID that can be used in the GET request to retrieve the results of this request.
    The method throws an illegal arguement exception if the request parameter is null or missing.
    The method throws a custom exception if anything goes wrong with the service calls made to the GMAIL API
    */
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public RequestIdDto postOffer(@RequestBody OAuthTokenDto oAuthTokenDto) {

        if(oAuthTokenDto.getToken().isEmpty() || oAuthTokenDto.getToken() == null)
            throw new IllegalArgumentException("Parameter token should not be 'null' or empty");

        try {
            return emailService.getDistinctEmailAddresses(oAuthTokenDto.getToken());
        } catch (IOException e) {
            log.info("Get distinct email call failed! ");
            throw new GrizzlyException();
        }
    }

}
