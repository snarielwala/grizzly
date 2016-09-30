package com.grizzly.services;

import com.grizzly.dto.EmailResponseDto;
import com.grizzly.dto.RequestIdDto;
import com.grizzly.services.external.GmailAPIClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Samarth 9/25/16
 */

/*
  Service classes are annotated with the @Service Notation
  The email service class contains all the business logic to
  the APIs defined in the controller classes. It is also responsible
  for interacting with the database/storage and other internal/external services.
     */
@Service
@Qualifier("emailService")
public class EmailService {

    @Resource
    private GmailAPIClient gmailAPIClient;

    @Resource
    private EmailService emailService;

    @Resource
    private RedisService redisService;

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final String INVALID_REQUEST_ID="[ Invalid request, No emails found for this Request ID]";
    private static final int SECONDS_IN_A_DAY=86400;

    /**
     * Service method corresponding to the get request adding a new entity to the database
     */
    public RequestIdDto getDistinctEmailAddresses(String accessToken) throws IOException {

        Set<String> emailIds = null;

        //if accessToken<-->RequestId exists, return the existing request Id
        if(redisService.getRedisConnection().exists(accessToken))
            return new RequestIdDto(redisService.getRedisConnection().get(accessToken).toString());

        String requestId = UUID.randomUUID().toString();
        log.info("RequestId Generated:" + requestId);

        //Call GMAIL API if you encounter a cache miss
        emailIds = gmailAPIClient.getDistinctEmailIds(accessToken);

        log.info("Number of Emails Found:"+emailIds.size());

        for (String email : emailIds)
            redisService.getRedisConnection().sadd(requestId, email);

        //store accessToken<-->requestId in cache for a day
        redisService.getRedisConnection().setex(accessToken,SECONDS_IN_A_DAY,requestId);

        return new RequestIdDto(requestId);
    }

    /**
     * Service method corresponding to the get request requesting emailIds+count for a given requestId
     */
    public List<EmailResponseDto> handleDistinctEmailRequest(String[] requestIds) {

        List<EmailResponseDto> emailResponseDtos = new ArrayList<EmailResponseDto>();
        for (String requestId : requestIds) {

            Set<String> emailIds = redisService.getRedisConnection().smembers(requestId);
            if(emailIds.isEmpty())emailIds.add(INVALID_REQUEST_ID);
            emailResponseDtos.add(new EmailResponseDto(emailIds, emailIds.size()));

        }
        return emailResponseDtos;

    }

}
