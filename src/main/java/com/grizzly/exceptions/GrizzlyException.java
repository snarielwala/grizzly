package com.grizzly.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Samarth on 9/25/16.
 */

/**This is custom exception class */
@ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR,reason="Request Failed. Try Again")
public class GrizzlyException extends RuntimeException {
    private static final long serialVersionUID = 100L;

}
