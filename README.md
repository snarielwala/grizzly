#Grizzly
Grizzly is a REST-ful web-service with two different endpoints.
[The endpoints have been described in detail section 2]

1. Overview & Tech-Stack
Grizzly is a web-service built using the Spring-Boot Framework (Java Programming Language, Java 8).
It uses as gradle as the build tool and Redis for persistence. Grizzly uses logback for logging.


2. APIs and Descriptions
    A. POST /emails
    The POST email call passes the Oauth token in the request body.
    All the emails received in the inbox for that user in the past 2 months are fetched using the GMAIL API through the following steps:
        a. Checks if a request for the same was received in the last 24 hours and if GMAIL was already quried for the list of mails.
            If yes, returns the RequestId from cache.
            If not, "list" messages API is used to fetch the message ids of all the messages from GMAIL.
        b. For every message id, the get message call is made to fetch the message information from GMAIL.
           (message header in our case that contains the email of the sender). The code attempts
            to do this in two different ways: one individual request for every message, making a
            batch request for all the message ids together.
        c. Generate a unique Request Id (uuid) for this request
        d. Store the (Request Id, List of Email Ids) in Redis
        e. Return the Request Id in response to the client
        f. Store the (Oauth,RequestId) in the cache for the next 24 hours

    Note: The batch request method is much more performant as compared to making one request each for every message id.(30-40% improvement in time)
          The procedure of fetching email Ids from the GMAIL API is time-consuming and hence this request is a long running request.


    B. GET /emails/{id} request
    This request accepts a Request Id or a List of Request Ids
    and returns distinct email Ids associated with each valid Request Id or an appropriate message if invalid.
    This request goes through the following processing steps:
    a. Parses the request Id/List of Request Ids
    b. Queries Redis for the List of Email Ids associated with each Request Id
    c. Returns this list in a JSON format

3. How to RUN ?
   a. Please make sure you have Java 8 and Redis Installed on your machine.
   b. Using gradle wrapper to run the project:
        1. CLEAN: ./gradlew clean
        2. BUILD: ./gradlew build
        3. RUN:   ./gradlew bootRun

4. At this point of time, basic tests for testing the working of both of APIs
    and their response formats+contents have been added to the project.




