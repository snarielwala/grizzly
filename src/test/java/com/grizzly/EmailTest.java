package com.grizzly;

import com.grizzly.dto.OAuthTokenDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Samarth on 9/25/16.
 */

//@RunWith(SpringJUnit4ClassRunner.class)
public class EmailTest extends BaseTest {

    private final String REQUEST_IDS="";
    private final String TOKEN="";


    /*
    This test case tests the post email API
    It creates a dummy request body and tests
    the format of the response body.
     */
    @Test
    public void testGetEmailsApi() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/v1/emails/"+REQUEST_IDS))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(REQUEST_IDS.split("//").length)))
                .andExpect(jsonPath("$[0].total").exists())
                .andExpect(jsonPath("$[0].values").exists());
    }

    /*
    This test case tests the post email API
    It creates a dummy request body and tests
    the format of the response body.
     */

    @Test
    public void testPostEmailsApi() throws Exception {

        OAuthTokenDto dummyRequestBody = new OAuthTokenDto();
        dummyRequestBody.setToken("");

        String requestBody = json(dummyRequestBody);

        mockMvc.perform(post("http://localhost:8080/api/v1/emails/")
                .contentType(contentType)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.requestID").exists());
    }

}
