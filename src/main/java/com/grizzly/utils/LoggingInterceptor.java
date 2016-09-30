package com.grizzly.utils;

import com.grizzly.helpers.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.UUID;

import static net.logstash.logback.marker.Markers.append;


/**
 * Created by Samarth
 * Description: This class LoggingInterceptor intercepts all the requests
 * and logs important metrics before and after the request is processed.
 */
@Component
@Qualifier("loggingInterceptor")
public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("preHandle Start");
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);
        request.setAttribute(Constants.REQUEST_TAG, UUID.randomUUID()+"_"+new Date());
        log.info("startTime="+startTime);
        log.info("preHandle End");
        return true;
    }

    /**
     * This method is executed after the request is processed.
     * It logs the request end time for every incoming request.
     * Also calculates and logs the request processing time for every request.
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHandle Start");
        long startTime = (Long)request.getAttribute("startTime");
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        log.info(String.valueOf(append("endTime",endTime)));
        log.info(String.valueOf(append("executionTime",executionTime)));
        log.info(String.valueOf(append("requestURI", request.getMethod()+" "+request.getRequestURI())));
        log.info("postHandle End");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("Request Completed");
    }


}
