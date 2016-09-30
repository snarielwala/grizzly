package com.grizzly.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;
import com.lambdaworks.redis.RedisURI;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by Samarth on 9/25/16.
 */

@Service
@Qualifier("redisService")
public class RedisService {


    private static Logger log = LoggerFactory.getLogger(RedisService.class);
    private static RedisClient redisClient;
    private static RedisConnection redisConnection;


    @Value("${redis.hostname}")
    public String redisHost;

    @Value("${redis.port}")
    public String redisPort;

    @PostConstruct
    public void init() throws InterruptedException{

        log.info("Redis Service Initializing");
        redisClient=new RedisClient(RedisURI.create("redis://"+redisHost+":"+redisPort));
        redisConnection=redisClient.connect();

        log.info("Redis Service Initialization Successful");
    }

    public static RedisConnection getRedisConnection(){
        return redisConnection;
    }

    @PreDestroy
    public void closeConnection(){
        redisConnection.close();
        log.info("Redis Connection Closed");
    }
}
