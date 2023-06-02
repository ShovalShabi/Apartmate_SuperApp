package superapp.logic;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import superapp.boundaries.command.MiniAppCommandBoundary;
import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.boundaries.user.UserBoundary;
import superapp.boundaries.user.UserIdBoundary;
import superapp.utils.Invokers.ObjectIdInvoker;
import superapp.utils.Invokers.UserIdInvoker;
import superapp.utils.TestDBHelpFunc;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertEquals;

/**
 * This class contains integration tests for the MiniAppCommandsServiceDB class.
 * It focuses on testing the CRUD operations of the users service using a database.
 * In addition, this class is testing the permissions of different users.
 */
@Tag("MiniAppCommandsServiceDBTest")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MiniAppCommandsServiceDBTest {

    private int port;
    private String usersUrl; // Users url as simple base url
    private String objectsUrl; // Users url as simple base url
    private String commandsUrl; // Mini app commands url as simple base url
    private String adminUsersUrl; // Admin url for users API as simple base url
    private String adminCommandsUrl;// Admin url for mini app command API as simple base url
    private String adminObjectsUrl;// Admin url for objects API as simple base url
    private UserBoundary miniAppUser; // MiniApp user boundary object
    private UserBoundary superAppUser; // SuperApp user boundary object
    private SuperAppObjectBoundary superAppObjectBoundary;// The target invoker of the command
    private RestTemplate restTemplate; // REST template object
    private TestDBHelpFunc testDBHelpFunc; //Helper class object


    @LocalServerPort
    public void setPort(int port) {
        this.commandsUrl = "http://localhost:" + port + "/superapp/miniapp";
        this.objectsUrl = "http://localhost:" + port + "/superapp/objects";
        this.usersUrl = "http://localhost:" + port + "/superapp/users";
        this.adminUsersUrl = "http://localhost:" + port + "/superapp/admin/users";
        this.adminCommandsUrl = "http://localhost:" + port + "/superapp/admin/miniapp";
        this.adminObjectsUrl = "http://localhost:" + port + "/superapp/admin/objects";
        this.restTemplate = new RestTemplate();
        this.port = port;
        this.testDBHelpFunc = new TestDBHelpFunc();
    }

    /**
     * Cleans up the database after each test by sending a delete request to the delete URL to each service.
     */
    @BeforeEach
    public void setupTest() {
        this.miniAppUser = this.restTemplate.postForObject(this.usersUrl,
                this.testDBHelpFunc.createDummyMiniappUser(),
                UserBoundary.class);
        assert this.miniAppUser != null;

        this.superAppUser = this.restTemplate.postForObject(this.usersUrl,
                this.testDBHelpFunc.createDummySuperappUser(),
                UserBoundary.class);
        assert this.superAppUser != null;

        this.superAppObjectBoundary = this.restTemplate.postForObject(String.format(this.objectsUrl +
                                "?userSuperapp=%s&userEmail=%s", this.superAppUser.getUserId().getSuperapp(),
                        this.superAppUser.getUserId().getEmail()),
                this.testDBHelpFunc.createDummyObject(),
                SuperAppObjectBoundary.class);
        assert this.superAppObjectBoundary != null;
    }

    /**
     * Cleans up the database after each test by sending a delete request to the delete URL to each service.
     */
    @AfterEach
    public void teardown() {
        UserBoundary admin = this.restTemplate.postForObject(this.usersUrl,
                this.testDBHelpFunc.createDummyAdmin(),
                UserBoundary.class);
        assert admin != null;


        this.restTemplate.delete(String.format(this.adminCommandsUrl + "?userSuperapp=%s&userEmail=%s",
                admin.getUserId().getSuperapp(),
                admin.getUserId().getEmail()));

        this.restTemplate.delete(String.format(this.adminObjectsUrl + "?userSuperapp=%s&userEmail=%s",
                admin.getUserId().getSuperapp(),
                admin.getUserId().getEmail()));

        this.restTemplate.delete(String.format(this.adminUsersUrl + "?userSuperapp=%s&userEmail=%s",
                admin.getUserId().getSuperapp(),
                admin.getUserId().getEmail()));
    }

    /**
     * Test case for invoking a MiniApp command when the async flag is false.
     */
    @Test
    public void invokeMiniAppCommandAsyncFalseTest() {
        // GIVEN the server is up
        // AND the user who invoke this command is authorized
        // AND The async flag is set to false
        // WHEN I POST /superapp/miniapp/miniAppName?async=false using the JSON request
        MiniAppCommandBoundary miniAppCommandBoundary = this.testDBHelpFunc.createCommandDummy();
        ObjectIdInvoker objectIdInvoker = new ObjectIdInvoker();
        objectIdInvoker.setObjectId(this.superAppObjectBoundary.getObjectId());
        miniAppCommandBoundary.setTargetObject(objectIdInvoker);
        miniAppCommandBoundary.setInvokedBy(new UserIdInvoker(this.miniAppUser.getUserId()));

        // Set the request headers to accept JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MiniAppCommandBoundary> requestEntity = new HttpEntity<>(miniAppCommandBoundary, headers);

        // WHEN I POST /superapp/miniapp/miniAppName?async=false
        ResponseEntity<String> responseEntity = this.restTemplate.exchange(this.commandsUrl + "/test?async=false", HttpMethod.POST, requestEntity, String.class);

        // THEN the MiniAppCommandsServiceRdb has successfully stored the MiniAppCommandBoundary as MiniAppCommandEntity in the database
        // AND the response status is 2XX OK
        // AND the response body is a JSON formatted that contains the sentence "We executed your command" because "test"
        // miniapp is not define as an actual miniapp
        assertThat(responseEntity.getBody())
                .isNotNull()
                .contains("We executed your command");
    }

    /**
     * Test case for invoking a MiniApp command when the async flag is true.
     */
    @Test
    public void invokeMiniAppCommandAsyncTrueTest() throws InterruptedException {
        // GIVEN the server is up
        // AND the user who invoke this command is authorized
        // AND The async flag is set to true
        // WHEN I POST /superapp/miniapp/miniAppName?async=false using the JSON request
        MiniAppCommandBoundary miniAppCommandBoundary = this.testDBHelpFunc.createCommandDummy();
        ObjectIdInvoker objectIdInvoker = new ObjectIdInvoker();
        objectIdInvoker.setObjectId(this.superAppObjectBoundary.getObjectId());
        miniAppCommandBoundary.setTargetObject(objectIdInvoker);
        miniAppCommandBoundary.setInvokedBy(new UserIdInvoker(this.miniAppUser.getUserId()));

        // Set the request headers to accept JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MiniAppCommandBoundary> requestEntity = new HttpEntity<>(miniAppCommandBoundary, headers);

        // WHEN I POST /superapp/miniapp/miniAppName?async=true
        ResponseEntity<MiniAppCommandBoundary> responseEntity = this.restTemplate.exchange(this.commandsUrl + "/test?async=true", HttpMethod.POST, requestEntity, MiniAppCommandBoundary.class);
        Thread.sleep(5000);// Waiting for asynchronous command to be executed

        // THEN the MiniAppCommandsServiceRdb has successfully stored the MiniAppCommandBoundary as MiniAppCommandEntity in the database
        // AND  the response status is 2XX OK
        // AND the response body is a JSON formatted that contains the sentence "We executed your command" because "test"
        // miniapp is not define as an actual miniapp
        assertThat(responseEntity.getBody())
                .isNotNull().
                extracting("commandId").
                extracting("miniapp").toString()
                .equals("test");
    }

    /**
     * Test case for invoking a MiniApp command with not existent user.
     */
    @Test
    public void invokeMiniAppCommandUserNotExistTest() {
        // GIVEN the server is up
        // AND the user who try to invoke the command does not exist
        MiniAppCommandBoundary miniAppCommandBoundary = this.testDBHelpFunc.createCommandDummy();
        UserIdBoundary userIdBoundary = new UserIdBoundary();
        userIdBoundary.setEmail("test@gmail.org");
        userIdBoundary.setSuperapp("META_SUPERAPP");
        miniAppCommandBoundary.setInvokedBy(new UserIdInvoker(userIdBoundary));

        // Set the request headers to accept JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MiniAppCommandBoundary> requestEntity = new HttpEntity<>(miniAppCommandBoundary, headers);

        // WHEN I POST /superapp/miniapp/miniAppName?async=false
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<String> responseEntity = restTemplate.exchange(this.commandsUrl +
                    "/test?async=false", HttpMethod.POST, requestEntity, String.class);
            // THEN the server responds with status 404 NOT FOUND
            // AND the response body is empty
            assertEquals("Expected to get " + HttpStatus.NOT_FOUND + " but got " + responseEntity.getStatusCode() +
                    " instead ", HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        });
        assertEquals("Expected to get " + HttpStatus.NOT_FOUND + " but got " + exception.getStatusCode() +
                " instead ", HttpStatus.NOT_FOUND, exception.getStatusCode());
    }


    /**
     * Test case for invoking a MiniApp command with unauthorized user.
     */
    @Test
    public void invokeMiniAppCommandUserNotAllowedTest() {
        // GIVEN the server is up
        // AND the user who invoke this command is not authorized
        // AND The async flag is set to false
        MiniAppCommandBoundary miniAppCommandBoundary = this.testDBHelpFunc.createCommandDummy();
        miniAppCommandBoundary.setInvokedBy(new UserIdInvoker(this.superAppUser.getUserId()));

        // Set the request headers to accept JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MiniAppCommandBoundary> requestEntity = new HttpEntity<>(miniAppCommandBoundary, headers);

        // WHEN I POST /superapp/miniapp/miniAppName?async=false
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<String> responseEntity = restTemplate.exchange(this.commandsUrl +
                    "/test?async=false", HttpMethod.POST, requestEntity, String.class);
            // THEN the server responds with status 401 UNAUTHORIZED
            // AND the response body is empty
            assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + responseEntity.getStatusCode() +
                    " instead ", HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        });
        assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + exception.getStatusCode() +
                " instead ", HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }


    /**
     * Test case for invoking a MiniApp command with invoked by set to null.
     */
    @Test
    public void invokeMiniAppCommandInvokedByIsNullTest() {
        // GIVEN the server is up
        // AND the user who invoke this command is authorized
        // AND The async flag is set to false
        // AND invoked by is null
        MiniAppCommandBoundary miniAppCommandBoundary = this.testDBHelpFunc.createCommandDummy();
        miniAppCommandBoundary.setInvokedBy(null);

        // Set the request headers to accept JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MiniAppCommandBoundary> requestEntity = new HttpEntity<>(miniAppCommandBoundary, headers);

        // WHEN I POST /superapp/miniapp/miniAppName?async=false
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<String> responseEntity = restTemplate.exchange(this.commandsUrl +
                    "/test?async=false", HttpMethod.POST, requestEntity, String.class);
            // THEN the server responds with status 400 BAD REQUEST
            // AND the response body is empty
            assertEquals("Expected to get " + HttpStatus.BAD_REQUEST + " but got " + responseEntity.getStatusCode() +
                    " instead ", HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        });
        assertEquals("Expected to get " + HttpStatus.BAD_REQUEST + " but got " + exception.getStatusCode() +
                " instead ", HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }


    /**
     * Test case for invoking a MiniApp command with null target object.
     */
    @Test
    public void invokeMiniAppCommandTargetObjectIsNullTest() {
        // GIVEN the server is up
        // AND the user who invoke this command is authorized
        // AND The async flag is set to false
        // AND target object is null
        MiniAppCommandBoundary miniAppCommandBoundary = this.testDBHelpFunc.createCommandDummy();
        miniAppCommandBoundary.setTargetObject(null);
        miniAppCommandBoundary.setInvokedBy(new UserIdInvoker(this.miniAppUser.getUserId()));

        // Set the request headers to accept JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MiniAppCommandBoundary> requestEntity = new HttpEntity<>(miniAppCommandBoundary, headers);

        // WHEN I POST /superapp/miniapp/miniAppName?async=false
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<String> responseEntity = restTemplate.exchange(this.commandsUrl +
                    "/test?async=false", HttpMethod.POST, requestEntity, String.class);
            // THEN the server responds with status 400 BAD REQUEST
            // AND the response body is empty
            assertEquals("Expected to get " + HttpStatus.BAD_REQUEST + " but got " + responseEntity.getStatusCode() +
                    " instead ", HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        });
        assertEquals("Expected to get " + HttpStatus.BAD_REQUEST + " but got " + exception.getStatusCode() +
                " instead ", HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }


    /**
     * Test case for invoking a MiniApp command with inactive target object.
     */
    @Test
    public void invokeMiniAppCommandTargetObjectIsInactiveTest() {
        // GIVEN the server is up
        // AND the user who invoke this command is authorized
        // AND The async flag is set to false
        // AND target object is inactive
        MiniAppCommandBoundary miniAppCommandBoundary = this.testDBHelpFunc.createCommandDummy();
        ObjectIdInvoker objectIdInvoker = new ObjectIdInvoker();
        objectIdInvoker.setObjectId(this.superAppObjectBoundary.getObjectId());
        miniAppCommandBoundary.setTargetObject(objectIdInvoker);
        miniAppCommandBoundary.setInvokedBy(new UserIdInvoker(this.miniAppUser.getUserId()));
        this.superAppObjectBoundary.setActive(false);
        this.restTemplate.put(String.format(this.objectsUrl +
                                "/%s/%s?userSuperapp=%s&userEmail=%s", this.superAppObjectBoundary.getObjectId().getSuperapp(),
                        this.superAppObjectBoundary.getObjectId().getInternalObjectId(),
                        this.superAppUser.getUserId().getSuperapp(),
                        this.superAppUser.getUserId().getEmail()),
                this.superAppObjectBoundary);

        // Set the request headers to accept JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MiniAppCommandBoundary> requestEntity = new HttpEntity<>(miniAppCommandBoundary, headers);

        // WHEN I POST /superapp/miniapp/miniAppName?async=false
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<String> responseEntity = restTemplate.exchange(this.commandsUrl +
                    "/test?async=false", HttpMethod.POST, requestEntity, String.class);
            // THEN the server responds with status 400 BAD REQUEST
            // AND the response body is empty
            assertEquals("Expected to get " + HttpStatus.BAD_REQUEST + " but got " + responseEntity.getStatusCode() +
                    " instead ", HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        });
        assertEquals("Expected to get " + HttpStatus.BAD_REQUEST + " but got " + exception.getStatusCode() +
                " instead ", HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }


    /**
     * Test case for invoking a MiniApp command with command description is empty.
     */
    @Test
    public void invokeMiniAppCommandIsEmptyTest() {
        // GIVEN the server is up
        // AND the user who invoke this command is authorized
        // AND The async flag is set to false
        // AND the command description
        MiniAppCommandBoundary miniAppCommandBoundary = this.testDBHelpFunc.createCommandDummy();
        ObjectIdInvoker objectIdInvoker = new ObjectIdInvoker();
        objectIdInvoker.setObjectId(this.superAppObjectBoundary.getObjectId());
        miniAppCommandBoundary.setTargetObject(objectIdInvoker);
        miniAppCommandBoundary.setInvokedBy(new UserIdInvoker(this.miniAppUser.getUserId()));
        miniAppCommandBoundary.setCommand("");

        // Set the request headers to accept JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MiniAppCommandBoundary> requestEntity = new HttpEntity<>(miniAppCommandBoundary, headers);

        // WHEN I POST /superapp/miniapp/miniAppName?async=false
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<String> responseEntity = restTemplate.exchange(this.commandsUrl +
                    "/test?async=false", HttpMethod.POST, requestEntity, String.class);
            // THEN the server responds with status 400 BAD REQUEST
            // AND the response body is empty
            assertEquals("Expected to get " + HttpStatus.BAD_REQUEST + " but got " + responseEntity.getStatusCode() +
                    " instead ", HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        });
        assertEquals("Expected to get " + HttpStatus.BAD_REQUEST + " but got " + exception.getStatusCode() +
                " instead ", HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }


    /**
     * Test case for getting all the commands of all miniapps as authorized user.
     */
    @Test
    public void getAllCommandsTest() {
        // GIVEN the server is up
        // AND there is several MiniAppCommandBoundaries objects within it
        // AND the user who try to get all the commands is an ADMIN
        UserBoundary admin = this.restTemplate.postForObject(this.usersUrl,
                this.testDBHelpFunc.createDummyAdmin(),
                UserBoundary.class);
        assert admin != null;
        IntStream.range(0, 4)
                .forEach(i -> {
                    MiniAppCommandBoundary miniAppCommandBoundary = this.testDBHelpFunc.createCommandDummy();
                    ObjectIdInvoker objectIdInvoker = new ObjectIdInvoker();
                    objectIdInvoker.setObjectId(this.superAppObjectBoundary.getObjectId());
                    miniAppCommandBoundary.setTargetObject(objectIdInvoker);
                    miniAppCommandBoundary.setInvokedBy(new UserIdInvoker(this.miniAppUser.getUserId()));

                    // Set the request headers to accept JSON
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<MiniAppCommandBoundary> requestEntity = new HttpEntity<>(miniAppCommandBoundary, headers);

                    restTemplate.exchange(this.commandsUrl +
                            "/test?async=false", HttpMethod.POST, requestEntity, String.class);
                });
        // WHEN I GET /superapp/admin/miniapp?userSuperApp={superapp}&userEmail={email}
        MiniAppCommandBoundary[] miniAppCommandBoundaries = this.restTemplate.getForObject(String.format(this.adminCommandsUrl +
                        "?userSuperapp=%s&userEmail=%s&size=2&page=1", admin.getUserId().getSuperapp(),
                admin.getUserId().getEmail()), MiniAppCommandBoundary[].class);
        assert miniAppCommandBoundaries != null;
        assertThat(miniAppCommandBoundaries).hasSize(2);

        this.restTemplate.delete(String.format(this.adminUsersUrl +
                        "?userSuperapp=%s&userEmail=%s",
                admin.getUserId().getSuperapp(),
                admin.getUserId().getEmail()));

    }

    /**
     * Test case for getting all the commands of all miniapps with unauthorized user.
     */
    @Test
    public void getAllCommandsUnauthorizedTest() {
        // GIVEN the server is up
        // AND there is several MiniAppCommandBoundaries objects within it
        // AND the user who try to get all the commands is not an ADMIN
        IntStream.range(0, 4)
                .forEach(i -> {
                    MiniAppCommandBoundary miniAppCommandBoundary = this.testDBHelpFunc.createCommandDummy();
                    ObjectIdInvoker objectIdInvoker = new ObjectIdInvoker();
                    objectIdInvoker.setObjectId(this.superAppObjectBoundary.getObjectId());
                    miniAppCommandBoundary.setTargetObject(objectIdInvoker);
                    miniAppCommandBoundary.setInvokedBy(new UserIdInvoker(this.miniAppUser.getUserId()));

                    // Set the request headers to accept JSON
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<MiniAppCommandBoundary> requestEntity = new HttpEntity<>(miniAppCommandBoundary, headers);

                    restTemplate.exchange(this.commandsUrl +
                            "/test?async=false", HttpMethod.POST, requestEntity, String.class);
                });

        // WHEN I GET /superapp/admin/miniapp?userSuperApp={superapp}&userEmail={email}
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<MiniAppCommandBoundary[]> responseEntity = restTemplate.getForEntity(String.format(this.adminCommandsUrl +
                            "?userSuperapp=%s&userEmail=%s&size=2&page=1", miniAppUser.getUserId().getSuperapp(),
                    miniAppUser.getUserId().getEmail()), null, MiniAppCommandBoundary[].class);

            // THEN the server responds with status 401 UNAUTHORIZED
            // AND the response body is empty
            assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + responseEntity.getStatusCode() +
                    " instead ", HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        });
        assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + exception.getStatusCode() +
                " instead ", HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }


    /**
     * Test case for getting all the commands of specific miniapp as authorized user.
     */
    @Test
    public void getAllCommandsInMiniAppTest() {
        // GIVEN the server is up
        // AND there is several MiniAppCommandBoundaries objects within it
        // AND the user who try to get all the commands from the miniapp is an ADMIN
        UserBoundary admin = this.restTemplate.postForObject(this.usersUrl,
                this.testDBHelpFunc.createDummyAdmin(),
                UserBoundary.class);
        assert admin != null;
        IntStream.range(0, 4)
                .forEach(i -> {
                    MiniAppCommandBoundary miniAppCommandBoundary = this.testDBHelpFunc.createCommandDummy();
                    ObjectIdInvoker objectIdInvoker = new ObjectIdInvoker();
                    objectIdInvoker.setObjectId(this.superAppObjectBoundary.getObjectId());
                    miniAppCommandBoundary.setTargetObject(objectIdInvoker);
                    miniAppCommandBoundary.setInvokedBy(new UserIdInvoker(this.miniAppUser.getUserId()));
                    String name;
                    if (i % 2 == 0)
                        name = "name";
                    else
                        name = "test";

                    // Set the request headers to accept JSON
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<MiniAppCommandBoundary> requestEntity = new HttpEntity<>(miniAppCommandBoundary, headers);

                    this.restTemplate.exchange(String.format(this.commandsUrl +
                            "/%s?async=false", name), HttpMethod.POST, requestEntity, String.class);
                });

        // WHEN I GET /superapp/admin/miniapp?userSuperApp={superapp}&userEmail={email}
        MiniAppCommandBoundary[] miniAppCommandBoundaries = this.restTemplate.getForObject(String.format(this.adminCommandsUrl +
                        "/test?userSuperapp=%s&userEmail=%s", admin.getUserId().getSuperapp(),
                admin.getUserId().getEmail()), MiniAppCommandBoundary[].class);

        // THEN the response statues is 2XX OK
        assert miniAppCommandBoundaries != null;
        assertThat(miniAppCommandBoundaries).hasSize(2);

        this.restTemplate.delete(String.format(this.adminUsersUrl +
                        "?userSuperapp=%s&userEmail=%s",
                admin.getUserId().getSuperapp(),
                admin.getUserId().getEmail()));

    }


    /**
     * Test case for getting all the commands from specific mini app command by unauthorized user.
     */
    @Test
    public void getAllCommandsFromMiniAppUnauthorizedTest() {
        // GIVEN the server is up
        // AND there is several MiniAppCommandBoundaries objects within it
        // AND the user who try to get all the commands is not an ADMIN
        IntStream.range(0, 4)
                .forEach(i -> {
                    MiniAppCommandBoundary miniAppCommandBoundary = this.testDBHelpFunc.createCommandDummy();
                    ObjectIdInvoker objectIdInvoker = new ObjectIdInvoker();
                    objectIdInvoker.setObjectId(this.superAppObjectBoundary.getObjectId());
                    miniAppCommandBoundary.setTargetObject(objectIdInvoker);
                    miniAppCommandBoundary.setInvokedBy(new UserIdInvoker(this.miniAppUser.getUserId()));

                    // Set the request headers to accept JSON
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<MiniAppCommandBoundary> requestEntity = new HttpEntity<>(miniAppCommandBoundary, headers);

                    restTemplate.exchange(this.commandsUrl +
                            "/test?async=false", HttpMethod.POST, requestEntity, String.class);
                });

        // WHEN I GET /superapp/admin/miniapp/{MiniAppName}?userSuperApp={superapp}&userEmail={email}
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<MiniAppCommandBoundary[]> responseEntity = restTemplate.getForEntity(String.format(this.adminCommandsUrl +
                            "/test?userSuperapp=%s&userEmail=%s&size=2&page=1", miniAppUser.getUserId().getSuperapp(),
                    miniAppUser.getUserId().getEmail()), null, MiniAppCommandBoundary[].class);

            // THEN the server responds with status 401 UNAUTHORIZED
            // AND the response body is empty
            assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + responseEntity.getStatusCode() +
                    " instead ", HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        });
        assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + exception.getStatusCode() +
                " instead ", HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }


    /**
     * Test case for deleting all the commands by authorized user.
     */
    @Test
    public void deleteAllCommandsTest() {
        // GIVEN the server is up
        // AND there is several MiniAppCommandBoundaries objects within it
        // AND the user who try to delete all the commands is an ADMIN
        IntStream.range(0, 4)
                .forEach(i -> {
                    MiniAppCommandBoundary miniAppCommandBoundary = this.testDBHelpFunc.createCommandDummy();
                    ObjectIdInvoker objectIdInvoker = new ObjectIdInvoker();
                    objectIdInvoker.setObjectId(this.superAppObjectBoundary.getObjectId());
                    miniAppCommandBoundary.setTargetObject(objectIdInvoker);
                    miniAppCommandBoundary.setInvokedBy(new UserIdInvoker(this.miniAppUser.getUserId()));

                    // Set the request headers to accept JSON
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<MiniAppCommandBoundary> requestEntity = new HttpEntity<>(miniAppCommandBoundary, headers);

                    restTemplate.exchange(this.commandsUrl +
                            "/test?async=false", HttpMethod.POST, requestEntity, String.class);
                });
        //Setting up the admin
        UserBoundary admin = this.restTemplate.postForObject(this.usersUrl,
                this.testDBHelpFunc.createDummyAdmin(),
                UserBoundary.class);
        assert admin != null;

        // WHEN I DELETE /superapp/admin/miniapp/{MiniAppName}?userSuperApp={superapp}&userEmail={email}

        restTemplate.delete(String.format(this.adminCommandsUrl +
                        "?userSuperapp=%s&userEmail=%s", admin.getUserId().getSuperapp(),
                admin.getUserId().getEmail()));


        // THEN the server responds with status 2XX OK
        // AND the response body is empty
        MiniAppCommandBoundary[] superAppObjectBoundaries = this.restTemplate.getForObject(String.format(this.adminCommandsUrl + "?userSuperapp=%s&userEmail=%s",
                admin.getUserId().getSuperapp(),
                admin.getUserId().getEmail()), MiniAppCommandBoundary[].class);
        assertThat(superAppObjectBoundaries).
                isNotNull().hasSize(0);

        this.restTemplate.delete(String.format(this.adminUsersUrl +
                        "?userSuperapp=%s&userEmail=%s&size=2&page=1", admin.getUserId().getSuperapp(),
                admin.getUserId().getEmail()));
    }


    /**
     * Test case for deleting all commands by unauthorized user.
     */
    @Test
    public void deleteAllCommandsFromMiniAppUnauthorizedTest() {
        // GIVEN the server is up
        // AND there is several MiniAppCommandBoundaries objects within it
        // AND the user who try to get all the commands is not an ADMIN
        IntStream.range(0, 4)
                .forEach(i -> {
                    MiniAppCommandBoundary miniAppCommandBoundary = this.testDBHelpFunc.createCommandDummy();
                    ObjectIdInvoker objectIdInvoker = new ObjectIdInvoker();
                    objectIdInvoker.setObjectId(this.superAppObjectBoundary.getObjectId());
                    miniAppCommandBoundary.setTargetObject(objectIdInvoker);
                    miniAppCommandBoundary.setInvokedBy(new UserIdInvoker(this.miniAppUser.getUserId()));

                    // Set the request headers to accept JSON
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<MiniAppCommandBoundary> requestEntity = new HttpEntity<>(miniAppCommandBoundary, headers);

                    restTemplate.exchange(this.commandsUrl +
                            "/test?async=false", HttpMethod.POST, requestEntity, String.class);
                });


        // WHEN I GET /superapp/admin/miniapp/{MiniAppName}?userSuperApp={superapp}&userEmail={email}
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            restTemplate.delete(String.format(this.adminCommandsUrl +
                            "?userSuperapp=%s&userEmail=%s", superAppUser.getUserId().getSuperapp(),
                    superAppUser.getUserId().getEmail()));
        });

        // THEN the server responds with status 401 UNAUTHORIZED
        // AND the response body is empty
        assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + exception.getStatusCode() +
                " instead ", HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }
}

