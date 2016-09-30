package com.grizzly.services.external;

import java.util.Set;

/**
 * Created by Samarth 9/25/16
 */

public interface EmailAPIClient {

    public Set<String> getDistinctEmailIds(String accessToken) throws Exception;
}
