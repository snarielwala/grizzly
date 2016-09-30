package com.grizzly.services.external;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.grizzly.exceptions.GrizzlyException;
import com.grizzly.helpers.DateFormatHelper;
import com.grizzly.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Samarth 9/25/16
 */

@Service
@Qualifier("gmailAPIClient")
public class GmailAPIClient implements EmailAPIClient {


    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final String QUERY_PARAMETERS="in:inbox after:%s";
    private static final String HEADER_FIELD="payload/headers";
    private static final String FROM = "From";

    //Pattern and Regex for extracting email from the "From" field
    private static final String EMAIL_REGEX="<(.*\\d*@.*\\d*)>";
    private static final Pattern PATTERN = Pattern.compile(EMAIL_REGEX);

    /** User Id = "me" for already authorized users*/
    private static final String ME = "me";

    /** Application name. */
    private static final String APPLICATION_NAME = "Zugata Interview";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".credentials/grizzly");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/gmail-java-quickstart
     */
    private static final List<String> SCOPES = Arrays.asList(GmailScopes.MAIL_GOOGLE_COM,GmailScopes.GMAIL_READONLY,GmailScopes.GMAIL_MODIFY);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public Credential authorize(String accessToken) throws IOException {
        // Load client secrets.
        InputStream in = GmailAPIClient.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
        credential.createScoped(SCOPES);
        return credential;
    }

    /**
     * Build and return an authorized Gmail client service.
     * @return an authorized Gmail client service
     * @throws IOException
     */
    public Gmail getGmailService(String accessToken) throws IOException {
        Credential credential = authorize(accessToken);

        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /** This method first fetches all the message/email Ids for the emails received in the past two months.
     * It then gets the headers (which contain the "From" email address) by making a batch/individual request to the Gmail API.
     *
     * @param service
     * @param userId
     * @param query
     * @return list of distinct email Ids
     * @throws IOException
     */
    public Set<String> listMessagesMatchingQuery(Gmail service, String userId, String query) throws IOException {

        ListMessagesResponse response = service.users().messages().list(userId).setQ(query).execute();
        Set<String> emailIds=new HashSet<String>();

        //going through all the pages and fetching all message ids
        List<Message> messages = new ArrayList<Message>();
        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = service.users().messages().list(userId).setQ(query)
                        .setPageToken(pageToken).execute();
            } else {
                break;
            }
        }

        // Get all message ids from the list of messages
        List<String> ids = messages
                        .stream()
                        .map(Message :: getId)
                        .collect(Collectors.toList());

        /* Iterating through every id and making a request for every message
        for (String id : ids) {
            Message message = service.users().messages().get(ME, id).setFields(HEADER_FIELD).execute();
            String emailId = message.
                                    getPayload().
                                    getHeaders().
                                    stream().
                                    filter(messagePartHeader -> messagePartHeader.getName().equals(FROM)).
                                    findFirst().
                                    get().getValue();
            emailIds.add(emailId);
        }
        */

        //Creating a batch request for all the message ids and making the get message information call through a batch request
        BatchRequest b = service.batch();
        JsonBatchCallback<Message> batchCallback = new JsonBatchCallback<Message>() {

            @Override
            public void onSuccess(Message message, HttpHeaders responseHeaders)
                    throws IOException {
                String fromField = message.
                        getPayload().
                        getHeaders().
                        stream().
                        filter(messagePartHeader -> messagePartHeader.getName().equals(FROM)).
                        findFirst().
                        get().getValue();

                //extract emailID and store
                Matcher matcher = PATTERN.matcher(fromField.trim());
                if(matcher.find())
                    emailIds.add(matcher.group(1));

            }

            @Override
            public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders)
                    throws IOException {
                throw new GrizzlyException();
            }
        };

        //queuing all the individual requests in a batch, requesting for only the header fields (lesser data transfer over the network)
        for(String id:ids){
            service.users().messages().get(ME,id).setFields(HEADER_FIELD).queue(b,batchCallback);
        }

        b.execute();
        return emailIds;
    }

    public Set<String> getDistinctEmailIds(String accessToken) {
        Set<String> response = null;
        try {
            Gmail service = getGmailService(accessToken);
            response = listMessagesMatchingQuery(service,ME,String.format(QUERY_PARAMETERS, DateFormatHelper.getDateBeforeTwoMonths()));
        } catch (IOException e) {
            log.info("Call to GMAIL API Failed");
            throw new GrizzlyException();
        }
        return response;
    }

}
