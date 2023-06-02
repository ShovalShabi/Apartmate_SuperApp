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
import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.boundaries.object.SuperAppObjectIdBoundary;
import superapp.boundaries.user.UserBoundary;
import superapp.boundaries.user.UserIdBoundary;
import superapp.utils.Invokers.UserIdInvoker;
import superapp.utils.Location;
import superapp.utils.TestDBHelpFunc;

import java.util.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertEquals;


/**
 * This class contains integration tests for the ObjectsServiceDB class.
 * It focuses on testing the functionality of the ObjectsServiceDB class
 * when interacting with the database.
 */
@Tag("ObjectsServiceDBTest")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ObjectsServiceDBTest {
    private int port;
    private String usersUrl; // Users url as simple base url
    private String objectsUrl; // Objects url as simple base url
    private String adminObjectsUrl; // Admin url for object API as simple base url
    private String adminUsersUrl; // Admin url for users API as simple base url
    private UserBoundary miniAppUser; // MiniApp user boundary object
    private UserBoundary superAppUser; // SuperApp user boundary object
    private RestTemplate restTemplate; // REST template object
    private TestDBHelpFunc testDBHelpFunc; //Helper class object


    /**
     * Sets up the necessary configurations before each test.
     * It initializes the base URL, delete URL, and the RestTemplate.
     *
     * @param port The randomly assigned local server port.
     */
    @LocalServerPort
    public void setup(int port) {
        this.objectsUrl = "http://localhost:" + port + "/superapp/objects";
        this.usersUrl = "http://localhost:" + port + "/superapp/users";
        this.adminObjectsUrl = "http://localhost:" + port + "/superapp/admin/objects";
        this.adminUsersUrl = "http://localhost:" + port + "/superapp/admin/users";
        this.restTemplate = new RestTemplate();
        this.port = port;
        this.testDBHelpFunc = new TestDBHelpFunc();

    }


    /**
     * Cleans up the database after each test by sending a delete request to the delete URL to eac service.
     */
    @BeforeEach
    public void setupTest() {
        this.superAppUser = this.restTemplate.postForObject(this.usersUrl,
                this.testDBHelpFunc.createDummySuperappUser(),
                UserBoundary.class);
        assert this.superAppUser != null;

        this.miniAppUser = this.restTemplate.postForObject(this.usersUrl,
                this.testDBHelpFunc.createDummyMiniappUser(),
                UserBoundary.class);
        assert this.miniAppUser != null;
    }

    /**
     * Cleans up the database after each test by sending a delete request to the delete URL to eac service.
     */
    @AfterEach
    public void teardown() {
        UserBoundary admin = this.restTemplate.postForObject(this.usersUrl,
                this.testDBHelpFunc.createDummyAdmin(),
                UserBoundary.class);
        assert admin != null;

        this.restTemplate.delete(String.format(this.adminObjectsUrl + "?userSuperapp=%s&userEmail=%s",
                admin.getUserId().getSuperapp(),
                admin.getUserId().getEmail()));

        this.restTemplate.delete(String.format(this.adminUsersUrl + "?userSuperapp=%s&userEmail=%s",
                admin.getUserId().getSuperapp(),
                admin.getUserId().getEmail()));
    }

    /**
     * Test creation of a new SuperAppObjectBoundary object which has to pass, the user is
     * authorized to create object.
     *
     * @throws Exception if the return objet is null or some fields are wrong according to the users' request
     */
    @Test
    public void createObjectAsSuperAppUserTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty
        // AND the database contains the SUPER_APP user which created the SuperappObjectBoundary
        SuperAppObjectBoundary superAppObjectBoundary = this.testDBHelpFunc.createDummyObject();


        // WHEN I POST /superapp/objects
        // AND the request body has SuperAppObjectBoundary as JSON format with no objectId
        // AND has details on the invoker
        // AND the invoker has the permission to create the object
        superAppObjectBoundary = this.restTemplate.postForObject(this.objectsUrl, superAppObjectBoundary, SuperAppObjectBoundary.class);
        assert superAppObjectBoundary != null;

        // THEN the server responds with status 2xx
        // AND the response body is the same SuperAppObjectBoundary that sent to the ObjectsServiceDB but now with objectId
        SuperAppObjectBoundary returnObj = this.restTemplate.getForObject(String.format(
                        this.objectsUrl + "/%s/%s?userSuperapp=%s&userEmail=%s", this.superAppUser.getUserId().getSuperapp(),
                        superAppObjectBoundary.getObjectId().getInternalObjectId(),
                        this.superAppUser.getUserId().getSuperapp(),
                        this.superAppUser.getUserId().getEmail()),
                SuperAppObjectBoundary.class);

        if (returnObj == null || returnObj.getObjectId() == null ||
                returnObj.getObjectId().getInternalObjectId() == null ||
                returnObj.getObjectId().getSuperapp() == null) {
            throw new Exception("Invalid data from server, expected an existent object, but received field with null instead");
        }

        if (returnObj.getCreationTimestamp() == null)
            throw new Exception("Invalid data from server, the return object should hold creation time stamp ");

        if (!this.compareResponseToRequest(superAppObjectBoundary, returnObj))
            throw new Exception("Some fields except objectId are different.\n" +
                    "expected the object: " + superAppObjectBoundary + "\nbut instead got the object " + returnObj);

    }

    /**
     * Test creation of  a new SuperAppObjectBoundary object which has to throw exception, the user is authorized to create object.
     * The alias is not valid
     *
     * @throws Exception if the SuperAppObjectBoundary cannot set alias to an empty string
     */
    @Test
    public void createObjectInvalidAliasTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty
        // AND the database contains the SUPERAPP_USER who is trying to create the SuperappObjectBoundary

        // WHEN I POST /superapp/objects
        // AND the request body has SuperAppObjectBoundary as JSON format with no objectId
        // AND has details on the invoker
        // AND the alias is an empty string
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        SuperAppObjectBoundary superAppObjectBoundary = this.testDBHelpFunc.createDummyObject();
        superAppObjectBoundary.getCreatedBy().setUserId(this.superAppUser.getUserId());
        superAppObjectBoundary.setAlias("");
        HttpEntity<SuperAppObjectBoundary> requestEntity = new HttpEntity<>(superAppObjectBoundary, headers);

        // THEN the server responds with status 400 BAD REQUEST
        // AND the database does not store the object
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<SuperAppObjectBoundary> responseEntity = restTemplate.exchange(objectsUrl, HttpMethod.POST, requestEntity, SuperAppObjectBoundary.class);
            assertEquals("Expected to get " + HttpStatus.BAD_REQUEST + " but got " + responseEntity.getStatusCode() +
                    " instead", HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        });

        // Additional checks for the exception
        assertEquals("Expected to get " + HttpStatus.BAD_REQUEST + " but got " + exception.getStatusCode() +
                " instead", HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }


    /**
     * Test creation of  a new SuperAppObjectBoundary object which has to throw exception, the user is authorized to create object.
     * The type is not valid
     *
     * @throws Exception if the SuperAppObjectBoundary cannot set type to an empty string
     */
    @Test
    public void createObjectInvalidTypeTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty
        // AND the database contains the SUPERAPP_USER who is trying to create the SuperappObjectBoundary

        // WHEN I POST /superapp/objects
        // AND the request body has SuperAppObjectBoundary as JSON format with no objectId
        // AND has details on the invoker
        // AND the type is an empty string
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        SuperAppObjectBoundary superAppObjectBoundary = this.testDBHelpFunc.createDummyObject();
        superAppObjectBoundary.getCreatedBy().setUserId(this.superAppUser.getUserId());
        superAppObjectBoundary.setType("");
        HttpEntity<SuperAppObjectBoundary> requestEntity = new HttpEntity<>(superAppObjectBoundary, headers);

        // THEN the server responds with status 400 BAD REQUEST
        // AND the database does not store the object
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<SuperAppObjectBoundary> responseEntity = restTemplate.exchange(objectsUrl, HttpMethod.POST, requestEntity, SuperAppObjectBoundary.class);
            assertEquals("Expected to get " + HttpStatus.BAD_REQUEST + " but got " + responseEntity.getStatusCode() +
                    " instead", HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        });

        // Additional checks for the exception
        assertEquals("Expected to get " + HttpStatus.BAD_REQUEST + " but got " + exception.getStatusCode() +
                " instead", HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    /**
     * Test creation of  a new SuperAppObjectBoundary object which has to throw exception, the user is not
     * authorized to create object.
     *
     * @throws Exception if the user is not authorized to create the object
     */
    @Test
    public void createObjectAsMiniAppUserTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty
        // AND the database contains the MINIAPP_USER who is trying to create the SuperappObjectBoundary

        // WHEN I POST /superapp/objects
        // AND the request body has SuperAppObjectBoundary as JSON format with no objectId
        // AND has details on the invoker
        // AND the invoker does not have the permission to create the object
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        SuperAppObjectBoundary superAppObjectBoundary = this.testDBHelpFunc.createDummyObject();
        superAppObjectBoundary.getCreatedBy().setUserId(this.miniAppUser.getUserId());
        HttpEntity<SuperAppObjectBoundary> requestEntity = new HttpEntity<>(superAppObjectBoundary, headers);

        // THEN the server responds with status 401 UNAUTHORIZED
        // AND the database does not store the object
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<SuperAppObjectBoundary> responseEntity = restTemplate.exchange(objectsUrl, HttpMethod.POST, requestEntity, SuperAppObjectBoundary.class);
            assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + responseEntity.getStatusCode() +
                    " instead", HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        });

        // Additional checks for the exception
        assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + exception.getStatusCode() +
                " instead", HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    /**
     * Test update of a SuperAppObjectBoundary object , the user have the permission to update object.
     *
     * @throws Exception if the SuperAppObjectBoundary cannot be found by the given ID, or some fields are different
     *                   from the user request.
     */
    @Test
    public void updateExistsObjectTest() throws Exception {
        /*
        GIVEN the server is up
        AND the database is not empty and has specific SuperAppObjectBoundary within it
        AND the given internalObjectId exists in the database
        AND the user who invoked this command is a SUPERAPP_USER
         */
        SuperAppObjectBoundary testObj = this.testDBHelpFunc.createDummyObject();
        testObj = this.restTemplate.postForObject(this.objectsUrl, testObj, SuperAppObjectBoundary.class);

        /*
        WHEN I PUT /superapp/objects/{superapp}/{internalObjectId}?userSuperapp={superapp}&userEmail={userEmail}
        AND some fields are set to other legal values
        */
        assert testObj != null;
        testObj.setActive(false);
        testObj.setType("ahabalType");
        testObj.setAlias("Ahabal");

        this.restTemplate.put(String.format(this.objectsUrl + "/%s/%s?userSuperapp=%s&userEmail=%s",
                this.superAppUser.getUserId().getSuperapp(),
                testObj.getObjectId().getInternalObjectId(),
                this.superAppUser.getUserId().getSuperapp(),
                this.superAppUser.getUserId().getEmail()), testObj);
        /*
        THEN the superapp object boundary has been updated successfully
        AND ObjectsServiceDB has stored the object in the database
        AND the response status is 2XX OK
         */
        SuperAppObjectBoundary returnObj = this.restTemplate.getForObject(String.format(this.objectsUrl + "/%s/%s?userSuperapp=%s&userEmail=%s",
                this.superAppUser.getUserId().getSuperapp(),
                testObj.getObjectId().getInternalObjectId(),
                this.superAppUser.getUserId().getSuperapp(),
                this.superAppUser.getUserId().getEmail()), SuperAppObjectBoundary.class);

        if (returnObj == null || returnObj.getObjectId() == null ||
                returnObj.getObjectId().getInternalObjectId() == null ||
                returnObj.getObjectId().getSuperapp() == null) {
            throw new Exception("Invalid data from server, expected an existent object, but received field with null instead");
        }
        if (!returnObj.getObjectId().getInternalObjectId().equals(testObj.getObjectId().getInternalObjectId()))
            throw new Exception(String.format(
                    "Invalid data from server, expected an existent SuperAppObjectBoundary with id:%s, but received SuperAppObjectBoundary with id:%s instead",
                    testObj.getObjectId().getInternalObjectId(), returnObj.getObjectId().getInternalObjectId()));
        if (returnObj.getCreationTimestamp() == null)
            throw new Exception("Invalid data from server, the return object should hold creation time stamp ");

        if (!returnObj.getCreationTimestamp().equals(testObj.getCreationTimestamp()))
            throw new Exception("Invalid data from server, the return object should hold the same creation time stamp ");

        if (!this.compareResponseToRequest(testObj, returnObj)) {
            throw new Exception("Expected difference between the objects, expected to: " + returnObj + "\nbut got: " + testObj + " instead");
        }
    }

    /**
     * Test update of a SuperAppObjectBoundary object which not found in the database has to throw exception, the user have the
     * permission to update object.
     *
     * @throws Exception if the SuperAppObjectBoundary cannot be found by the given ID
     */
    @Test
    public void updateObjectNotExistsTest() throws Exception {
        /*
        GIVEN the server is up
        AND the given internalObjectId is not exists in the database
        AND the user who invoked this command is a SUPERAPP_USER
         */
        SuperAppObjectBoundary testObj = testDBHelpFunc.createDummyObject();
        testObj.setActive(false);
        /*
        WHEN I PUT /superapp/objects/{superapp}/{internalObjectId}?userSuperapp={superapp}&userEmail={userEmail}
        */
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SuperAppObjectBoundary> requestEntity = new HttpEntity<>(testObj, headers);

        // THEN the server responds with status 404 NOT FOUND
        // AND nothing has been stored in the database
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<SuperAppObjectBoundary> responseEntity = restTemplate.exchange(
                    String.format(this.objectsUrl + "/%s/%s?userSuperapp=%s&userEmail=%s",
                            this.superAppUser.getUserId().getSuperapp(),
                            "123",
                            this.superAppUser.getUserId().getSuperapp(),
                            this.superAppUser.getUserId().getEmail()), HttpMethod.PUT, requestEntity, SuperAppObjectBoundary.class);
            assertEquals("Expected to get " + HttpStatus.NOT_FOUND + " but got " + responseEntity.getStatusCode() +
                    " instead", HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        });
        // Additional checks for the exception
        assertEquals("Expected to get " + HttpStatus.NOT_FOUND + " but got " + exception.getStatusCode() +
                " instead", HttpStatus.NOT_FOUND, exception.getStatusCode());
    }


    /**
     * Test update of a SuperAppObjectBoundary object which has to throw exception, the user does not
     * have the permission to change the object.
     *
     * @throws Exception if the user is unauthorized to change the object
     */
    @Test
    public void updateObjectAsMiniAppUserTest() throws Exception {
        /*
        GIVEN the server is up
        AND the database is not empty and has specific SuperAppObjectBoundary within it
        AND the given internalObjectId exists in the database
         */
        SuperAppObjectBoundary testObj = this.injectObjectToDB();
        testObj = this.restTemplate.postForObject(this.objectsUrl, testObj, SuperAppObjectBoundary.class);

        /*
        WHEN I PUT /superapp/objects/{superapp}/{internalObjectId}?userSuperapp={superapp}&userEmail={userEmail}
        AND the user who invoked this command is a MINIAPP_USER
        */
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SuperAppObjectBoundary> requestEntity = new HttpEntity<>(testObj, headers);

        // THEN the server responds with status 401 UNAUTHORIZED
        // AND nothing has been stored in the database
        SuperAppObjectBoundary finalTestObj = testObj;
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            assert finalTestObj != null;
            ResponseEntity<SuperAppObjectBoundary> responseEntity = restTemplate.exchange(
                    String.format(this.objectsUrl + "/%s/%s?userSuperapp=%s&userEmail=%s",
                            this.miniAppUser.getUserId().getSuperapp(),
                            finalTestObj.getObjectId().getInternalObjectId(),
                            this.miniAppUser.getUserId().getSuperapp(),
                            this.miniAppUser.getUserId().getEmail()), HttpMethod.PUT, requestEntity, SuperAppObjectBoundary.class);
            assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + responseEntity.getStatusCode() +
                    " instead", HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        });
        // Additional checks for the exception
        assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + exception.getStatusCode() +
                " instead", HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    /**
     * Test update of a SuperAppObjectBoundary object which has to throw exception, the user have been tried to
     * change the invocation timestamp.
     *
     * @throws Exception if the user tried to change the invocation timestamp
     */
    @Test
    public void updateObjectCreationTimestampTest() throws Exception {
        /*
        GIVEN the server is up
        AND the database is not empty and has specific SuperAppObjectBoundary within it
        AND the given internalObjectId exists in the database
        AND the user who invoked this command is a SUPERAPP_USER
         */
        SuperAppObjectBoundary testObj = this.injectObjectToDB();
        testObj = this.restTemplate.postForObject(this.objectsUrl, testObj, SuperAppObjectBoundary.class);
        assert testObj != null;

        /*
        WHEN I PUT /superapp/objects/{superapp}/{internalObjectId}?userSuperapp={superapp}&userEmail={userEmail}
        AND the creation timestamp has been changed
        */
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        testObj.setCreationTimestamp(new Date());

        HttpEntity<SuperAppObjectBoundary> requestEntity = new HttpEntity<>(testObj, headers);

        // THEN the server responds with status 401 UNAUTHORIZED
        // AND nothing updates in the database
        SuperAppObjectBoundary finalTestObj = testObj;
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<SuperAppObjectBoundary> responseEntity = restTemplate.exchange(
                    String.format(this.objectsUrl + "/%s/%s?userSuperapp=%s&userEmail=%s",
                            this.superAppUser.getUserId().getSuperapp(),
                            finalTestObj.getObjectId().getInternalObjectId(),
                            this.superAppUser.getUserId().getSuperapp(),
                            this.superAppUser.getUserId().getEmail()), HttpMethod.PUT, requestEntity, SuperAppObjectBoundary.class);
            assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + responseEntity.getStatusCode() +
                    " instead", HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        });
        // Additional checks for the exception
        assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + exception.getStatusCode() +
                " instead", HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    /**
     * Test update of a SuperAppObjectBoundary object which has to throw exception, the user have been tried to
     * change the internal object ID.
     *
     * @throws Exception if the user tried to change the internal object ID
     */
    @Test
    public void updateInternalObjectIdTest() throws Exception {
        /*
        GIVEN the server is up
        AND the database is not empty and has specific SuperAppObjectBoundary within it
        AND the given internalObjectId exists in the database
        AND the user who invoked this command is a SUPERAPP_USER
         */
        SuperAppObjectBoundary testObj = this.injectObjectToDB();
        testObj = this.restTemplate.postForObject(this.objectsUrl, testObj, SuperAppObjectBoundary.class);
        assert testObj != null;

        /*
        WHEN I PUT /superapp/objects/{superapp}/{internalObjectId}?userSuperapp={superapp}&userEmail={userEmail}
        AND the internal object ID has been changed
        */
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        SuperAppObjectIdBoundary superAppObjectIdBoundary = new SuperAppObjectIdBoundary();
        superAppObjectIdBoundary.setInternalObjectId(UUID.randomUUID().toString());
        superAppObjectIdBoundary.setSuperapp(this.superAppUser.getUserId().getSuperapp());
        testObj.setObjectId(superAppObjectIdBoundary);

        HttpEntity<SuperAppObjectBoundary> requestEntity = new HttpEntity<>(testObj, headers);

        // THEN the server responds with status 404 NOT FOUND
        // AND the response status is 404 NOT FOUND
        SuperAppObjectBoundary finalTestObj = testObj;
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<SuperAppObjectBoundary> responseEntity = restTemplate.exchange(
                    String.format(this.objectsUrl + "/%s/%s?userSuperapp=%s&userEmail=%s",
                            this.superAppUser.getUserId().getSuperapp(),
                            finalTestObj.getObjectId().getInternalObjectId(),
                            this.superAppUser.getUserId().getSuperapp(),
                            this.superAppUser.getUserId().getEmail()), HttpMethod.PUT, requestEntity, SuperAppObjectBoundary.class);
            assertEquals("Expected to get " + HttpStatus.NOT_FOUND + " but got " + responseEntity.getStatusCode() +
                    " instead", HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        });
        // Additional checks for the exception
        assertEquals("Expected to get " + HttpStatus.NOT_FOUND + " but got " + exception.getStatusCode() +
                " instead", HttpStatus.NOT_FOUND, exception.getStatusCode());
    }


    /**
     * Test update of a SuperAppObjectBoundary object which has to throw exception, the user have been tried to
     * change the invoker of the object.
     *
     * @throws Exception if the user have been tried to change the invoker of the object.
     */
    @Test
    public void updateInvokerTest() throws Exception {
        /*
        GIVEN the server is up
        AND the database is not empty and has specific SuperAppObjectBoundary within it
        AND the given internalObjectId exists in the database
        AND the user who invoked this command is a MINIAPP_USER
         */
        SuperAppObjectBoundary testObj = this.injectObjectToDB();
        testObj = this.restTemplate.postForObject(this.objectsUrl, testObj, SuperAppObjectBoundary.class);
        assert testObj != null;

        /*
        WHEN I PUT /superapp/objects/{superapp}/{internalObjectId}?userSuperapp={superapp}&userEmail={userEmail}
        AND the user invoker has been changed
        */
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        UserIdBoundary userIdBoundary = new UserIdBoundary();
        userIdBoundary.setSuperapp("META_CORPORATION");
        userIdBoundary.setEmail("stam@gmail.uk");
        UserIdInvoker userIdInvoker = new UserIdInvoker();
        userIdInvoker.setUserId(userIdBoundary);
        testObj.setCreatedBy(userIdInvoker);

        HttpEntity<SuperAppObjectBoundary> requestEntity = new HttpEntity<>(testObj, headers);

        // THEN the server responds with status 401 UNAUTHORIZED
        // AND nothing updates in the database
        SuperAppObjectBoundary finalTestObj = testObj;
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<SuperAppObjectBoundary> responseEntity = restTemplate.exchange(
                    String.format(this.objectsUrl + "/%s/%s?userSuperapp=%s&userEmail=%s",
                            this.superAppUser.getUserId().getSuperapp(),
                            finalTestObj.getObjectId().getInternalObjectId(),
                            this.superAppUser.getUserId().getSuperapp(),
                            this.superAppUser.getUserId().getEmail()), HttpMethod.PUT, requestEntity, SuperAppObjectBoundary.class);
            assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + responseEntity.getStatusCode() +
                    " instead", HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        });
        // Additional checks for the exception
        assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + exception.getStatusCode() +
                " instead", HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    /**
     * Test update of a SuperAppObjectBoundary object which has to throw exception, the user have been tried to
     * change the alias to an empty string.
     *
     * @throws Exception if the user have been tried to change the alias to an empty string.
     */
    @Test
    public void updateAliasAsEmptyStringTest() throws Exception {
        /*
        GIVEN the server is up
        AND the database is not empty and has specific SuperAppObjectBoundary within it
        AND the given internalObjectId exists in the database
        AND the user who invoked this command is a USERAPP_USER
         */
        SuperAppObjectBoundary testObj = this.injectObjectToDB();
        testObj = this.restTemplate.postForObject(this.objectsUrl, testObj, SuperAppObjectBoundary.class);
        assert testObj != null;

        /*
        WHEN I PUT /superapp/objects/{superapp}/{internalObjectId}?userSuperapp={superapp}&userEmail={userEmail}
        AND the alias has been set to an empty string
        */
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        testObj.setAlias("");

        HttpEntity<SuperAppObjectBoundary> requestEntity = new HttpEntity<>(testObj, headers);

        // THEN the server responds with status 400 BAD REQUEST
        // AND nothing updates in the database
        SuperAppObjectBoundary finalTestObj = testObj;
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<SuperAppObjectBoundary> responseEntity = restTemplate.exchange(
                    String.format(this.objectsUrl + "/%s/%s?userSuperapp=%s&userEmail=%s",
                            this.superAppUser.getUserId().getSuperapp(),
                            finalTestObj.getObjectId().getInternalObjectId(),
                            this.superAppUser.getUserId().getSuperapp(),
                            this.superAppUser.getUserId().getEmail()), HttpMethod.PUT, requestEntity, SuperAppObjectBoundary.class);
            assertEquals("Expected to get " + HttpStatus.BAD_REQUEST + " but got " + responseEntity.getStatusCode() +
                    " instead", HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        });
        // Additional checks for the exception
        assertEquals("Expected to get " + HttpStatus.BAD_REQUEST + " but got " + exception.getStatusCode() +
                " instead", HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    /**
     * Test update of a SuperAppObjectBoundary object which has to throw exception, the user have been tried to
     * change the type od the object to an empty string.
     *
     * @throws Exception if the user have been tried to change the type od the object to an empty string.
     */
    @Test
    public void updateTypeAsEmptyStringTest() throws Exception {
        /*
        GIVEN the server is up
        AND the database is not empty and has specific SuperAppObjectBoundary within it
        AND the given internalObjectId exists in the database
        AND the user who invoked this command is a SUPERAPP_USER
         */
        SuperAppObjectBoundary testObj = this.injectObjectToDB();
        testObj = this.restTemplate.postForObject(this.objectsUrl, testObj, SuperAppObjectBoundary.class);
        assert testObj != null;

        /*
        WHEN I PUT /superapp/objects/{superapp}/{internalObjectId}?userSuperapp={superapp}&userEmail={userEmail}
        AND the type has been set to an e,pty string
        */
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        testObj.setType("");

        HttpEntity<SuperAppObjectBoundary> requestEntity = new HttpEntity<>(testObj, headers);

        // THEN the server responds with status 400 BAD REQUEST
        // AND nothing updates in the database
        SuperAppObjectBoundary finalTestObj = testObj;
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<SuperAppObjectBoundary> responseEntity = restTemplate.exchange(
                    String.format(this.objectsUrl + "/%s/%s?userSuperapp=%s&userEmail=%s",
                            this.superAppUser.getUserId().getSuperapp(),
                            finalTestObj.getObjectId().getInternalObjectId(),
                            this.superAppUser.getUserId().getSuperapp(),
                            this.superAppUser.getUserId().getEmail()), HttpMethod.PUT, requestEntity, SuperAppObjectBoundary.class);
            assertEquals("Expected to get " + HttpStatus.BAD_REQUEST + " but got " + responseEntity.getStatusCode() +
                    " instead", HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        });
        // Additional checks for the exception
        assertEquals("Expected to get " + HttpStatus.BAD_REQUEST + " but got " + exception.getStatusCode() +
                " instead", HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    /**
     * Test a retrieve of a specific SuperAppObject from the database with the given objectSuperApp and internalObjectId.
     *
     * @throws Exception if no matching object is found
     */
    @Test
    public void getSpecificObjectTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has specific SuperAppObjectBoundary within it
        // AND the user who invoked this command is a USERAPP_USER
        SuperAppObjectBoundary testObj = this.restTemplate.postForObject(this.objectsUrl,
                testDBHelpFunc.createDummyObject(),
                SuperAppObjectBoundary.class);
        assert testObj != null;


        // WHEN I GET /superapp/objects/{superapp}/{internalObjectId}?userSuperapp={superapp}&userEmail={userEmail}
        // AND there is no request body
        // AND the object hasn't been changed in the meanwhile
        SuperAppObjectBoundary returnObj = this.restTemplate.getForObject(String.format(this.objectsUrl + "/%s/%s?userSuperapp=%s&userEmail=%s",
                this.superAppUser.getUserId().getSuperapp(),
                testObj.getObjectId().getInternalObjectId(),
                this.superAppUser.getUserId().getSuperapp(),
                this.superAppUser.getUserId().getEmail()), SuperAppObjectBoundary.class);

        // THEN the server responds with status 2XX
        // AND the response body is the same SuperAppObjectBoundary that sent to the ObjectsServiceDB with the matching objectId
        if (returnObj == null || returnObj.getObjectId() == null ||
                returnObj.getObjectId().getInternalObjectId() == null ||
                returnObj.getObjectId().getSuperapp() == null) {
            throw new Exception("Invalid data from server, expected an existent object, but received field with null instead");
        }

        if (!returnObj.getObjectId().getInternalObjectId().equals(testObj.getObjectId().getInternalObjectId()))
            throw new Exception(String.format(
                    "Invalid data from server, expected an existent SuperAppObjectBoundary with id:%s, but received SuperAppObjectBoundary with id:%s instead",
                    testObj.getObjectId().getInternalObjectId(), returnObj.getObjectId().getInternalObjectId()));

        if (returnObj.getCreationTimestamp() == null)
            throw new Exception("Invalid data from server, the return object should hold creation time stamp ");

        if (!returnObj.getCreationTimestamp().equals(testObj.getCreationTimestamp()))
            throw new Exception("Invalid data from server, the return object should hold the same creation time stamp ");

        if (!this.compareResponseToRequest(testObj, returnObj))
            throw new Exception("Some fields except objectId are different.\n" +
                    "expected the object: " + testObj + "\nbut instead got the object " + returnObj);

    }

    /**
     * Test a retrieve of a specific SuperAppObject from the database that does not exist with the given objectSuperApp
     * and internalObjectId.
     *
     * @throws Exception if the matching object is found
     */
    @Test
    public void getSpecificObjectNotExistTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has no specific SuperAppObjectBoundary within it
        // AND the user who invoked this command is a USERAPP_USER
        SuperAppObjectBoundary testObj = this.testDBHelpFunc.createDummyObject();


        // WHEN I GET /superapp/objects/{superapp}/{internalObjectId}?userSuperapp={superapp}&userEmail={userEmail}
        // AND there is no request body
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SuperAppObjectBoundary> requestEntity = new HttpEntity<>(testObj, headers);

        // THEN the server responds with status 404 NOT FOUND
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<SuperAppObjectBoundary> responseEntity = restTemplate.exchange(
                    String.format(this.objectsUrl + "/%s/%s?userSuperapp=%s&userEmail=%s",
                            this.superAppUser.getUserId().getSuperapp(),
                            "123",
                            this.superAppUser.getUserId().getSuperapp(),
                            this.superAppUser.getUserId().getEmail()), HttpMethod.GET, requestEntity, SuperAppObjectBoundary.class);
            assertEquals("Expected to get " + HttpStatus.NOT_FOUND + " but got " + responseEntity.getStatusCode() +
                    " instead", HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        });
        // Additional checks for the exception
        assertEquals("Expected to get " + HttpStatus.NOT_FOUND + " but got " + exception.getStatusCode() +
                " instead", HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    /**
     * Test a retrieve of a specific SuperAppObject from the database as miniapp user
     * and internalObjectId.
     *
     * @throws Exception if the user who tried to invoke the command is a miniapp user
     */
    @Test
    public void getSpecificAsMiniAppUserWhenActiveIsFalseTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has no specific SuperAppObjectBoundary within it
        // AND the user who invoked this command is a MINIAPP_USER
        SuperAppObjectBoundary testObj = this.testDBHelpFunc.createDummyObject();
        testObj.setActive(false);

        testObj = this.restTemplate.postForObject(this.objectsUrl, testObj, SuperAppObjectBoundary.class);
        assert testObj != null;
        this.restTemplate.put(String.format(this.objectsUrl + "/%s/%s?userSuperapp=%s&userEmail=%s",
                this.superAppUser.getUserId().getSuperapp(),
                testObj.getObjectId().getInternalObjectId(),
                this.superAppUser.getUserId().getSuperapp(),
                this.superAppUser.getUserId().getEmail()), testObj);


        // WHEN I GET /superapp/objects/{superapp}/{internalObjectId}?userSuperapp={superapp}&userEmail={userEmail}
        // AND there is no request body
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SuperAppObjectBoundary> requestEntity = new HttpEntity<>(testObj, headers);

        // THEN the server responds with status 404 NOT FOUND
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<SuperAppObjectBoundary> responseEntity = restTemplate.exchange(
                    String.format(this.objectsUrl + "/%s/%s?userSuperapp=%s&userEmail=%s",
                            this.miniAppUser.getUserId().getSuperapp(),
                            Objects.requireNonNull(requestEntity.getBody()).getObjectId().getInternalObjectId(),
                            this.miniAppUser.getUserId().getSuperapp(),
                            this.miniAppUser.getUserId().getEmail()), HttpMethod.GET, requestEntity, SuperAppObjectBoundary.class);
            assertEquals("Expected to get " + HttpStatus.NOT_FOUND + " but got " + responseEntity.getStatusCode() +
                    " instead", HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        });
        // Additional checks for the exception
        assertEquals("Expected to get " + HttpStatus.NOT_FOUND + " but got " + exception.getStatusCode() +
                " instead", HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    /**
     * Test a retrieve of all SuperAppObjects from the database.
     *
     * @throws Exception if there are no objects at all or the specific amount of objects
     */
    @Test
    public void getAllObjectsAsSuperAppUserTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has several SuperAppObjectBoundaries within it
        // AND the user who invoked this command is a USERAPP_USER
        IntStream.range(0, 4)
                .forEach(i -> {
                    SuperAppObjectBoundary testObj = this.testDBHelpFunc.createDummyObject();
                    if (i % 2 == 0)
                        testObj.setActive(false);
                    this.restTemplate.postForObject(this.objectsUrl, testObj, SuperAppObjectBoundary.class);
                });


        // WHEN I GET /superapp/objects?userSuperapp={superapp}&userEmail={userEmail}
        // AND there is no request body
        SuperAppObjectBoundary[] superAppObjectBoundaries = this.restTemplate.getForObject(String.format(this.objectsUrl + "?userSuperapp=%s&userEmail=%s",
                this.superAppUser.getUserId().getSuperapp(),
                this.superAppUser.getUserId().getEmail()), SuperAppObjectBoundary[].class);

        // THEN the server responds with status 2XX
        // AND the response body is an array of SuperAppObjectBoundaries that is not empty

        if (superAppObjectBoundaries == null || superAppObjectBoundaries.length == 0) {
            throw new Exception("Invalid data from server, expected an existent array of SuperAppObjectBoundaries with overall array length greater than 0");
        }

        if (superAppObjectBoundaries.length != 4) {
            throw new Exception("Invalid data from server, expected an existent array of " +
                    "SuperAppObjectBoundaries with overall array length equal to 4 (contains active objects and not active objects)");
        }
    }

    /**
     * Test a retrieve of all SuperAppObjects from the database as miniapp user.
     *
     * @throws Exception if there are no objects at all or the specific amount of objects
     */
    @Test
    public void getAllObjectsAsMiniAppUserTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has several SuperAppObjectBoundaries within it
        // AND the user who invoked this command is a MINIAPP_USER
        IntStream.range(0, 4)
                .forEach(i -> {
                    SuperAppObjectBoundary testObj = this.testDBHelpFunc.createDummyObject();
                    if (i % 2 == 0)
                        testObj.setActive(false);
                    this.restTemplate.postForObject(this.objectsUrl, testObj, SuperAppObjectBoundary.class);
                });


        // WHEN I GET /superapp/objects/{superapp}/{internalObjectId}?userSuperapp={superapp}&userEmail={userEmail}
        // AND there is no request body
        SuperAppObjectBoundary[] superAppObjectBoundaries = this.restTemplate.getForObject(String.format(this.objectsUrl + "?userSuperapp=%s&userEmail=%s",
                this.miniAppUser.getUserId().getSuperapp(),
                this.miniAppUser.getUserId().getEmail()), SuperAppObjectBoundary[].class);

        // THEN the server responds with status 2XX
        // AND the response body is an array of SuperAppObjectBoundaries that is not empty
        if (superAppObjectBoundaries == null || superAppObjectBoundaries.length == 0) {
            throw new Exception("Invalid data from server, expected an existent array of SuperAppObjectBoundaries with overall array length greater than 0");
        }

        if (superAppObjectBoundaries.length != 2) {
            throw new Exception("Invalid data from server, expected an existent array of SuperAppObjectBoundaries with overall array length equal to 2 (contains only active objects)");
        }
    }


    /**
     * Test a retrieve of all SuperAppObjects from the database as admin.
     *
     * @throws Exception if the user in unauthorized to retrieve all the objects
     */
    @Test
    public void getAllObjectsAsAdminTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has several SuperAppObjectBoundaries within it
        // AND the user who invoked this command is an ADMIN
        IntStream.range(0, 4)
                .forEach(i -> {
                    SuperAppObjectBoundary testObj = this.testDBHelpFunc.createDummyObject();
                    if (i % 2 == 0)
                        testObj.setActive(false);
                    this.restTemplate.postForObject(this.objectsUrl, testObj, SuperAppObjectBoundary.class);
                });

        UserBoundary admin = this.restTemplate.postForObject(this.usersUrl, this.testDBHelpFunc.createDummyAdmin(), UserBoundary.class);
        assert admin != null;

        // THEN the server responds with status 401 UNAUTHORIZED
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            // WHEN I GET /superapp/objects/{superapp}/{internalObjectId}?userSuperapp={superapp}&userEmail={userEmail}
            // AND there is no request body
            ResponseEntity<SuperAppObjectBoundary[]> responseEntity = this.restTemplate.exchange(String.format(this.objectsUrl + "?userSuperapp=%s&userEmail=%s",
                    admin.getUserId().getSuperapp(),
                    admin.getUserId().getEmail()), HttpMethod.GET, null, SuperAppObjectBoundary[].class);
            assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + responseEntity.getStatusCode() +
                    " instead", HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        });
        // Additional checks for the exception
        assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + exception.getStatusCode() +
                " instead", HttpStatus.UNAUTHORIZED, exception.getStatusCode());

        //deleting the admin for avoiding conflict with the teardown function
        this.restTemplate.delete(String.format(this.adminUsersUrl + "?userSuperapp=%s&userEmail=%s",
                admin.getUserId().getSuperapp(),
                admin.getUserId().getEmail()));
    }

    /**
     * Test deletion of all SuperAppObjects from the database as admin.
     *
     * @throws Exception if the user unauthorized to delete the objects
     */
    @Test
    public void deleteAllObjectsTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has several SuperAppObjectBoundary within it
        // AND the user who invoked this command is an ADMIN
        this.injectObjectToDB();
        UserBoundary admin = this.restTemplate.postForObject(this.usersUrl,
                this.testDBHelpFunc.createDummyAdmin(),
                UserBoundary.class);
        assert admin != null;


        // WHEN I DELETE /superapp/admin/objects?userSuperapp={superapp}&userEmail={userEmail}
        // AND there is no request body
        this.restTemplate.delete(String.format(this.adminObjectsUrl + "?userSuperapp=%s&userEmail=%s",
                admin.getUserId().getSuperapp(),
                admin.getUserId().getEmail()));


        // THEN the server responds with status 2XX
        // AND the response body is empty
        SuperAppObjectBoundary[] superAppObjectBoundaries = this.restTemplate.getForObject(
                String.format(this.objectsUrl + "?userSuperapp=%s&userEmail=%s",
                        this.superAppUser.getUserId().getSuperapp(),
                        this.superAppUser.getUserId().getEmail()), SuperAppObjectBoundary[].class);

        //deleting the admin for avoiding conflict with the teardown function
        this.restTemplate.delete(String.format(this.adminUsersUrl + "?userSuperapp=%s&userEmail=%s",
                admin.getUserId().getSuperapp(),
                admin.getUserId().getEmail()));

        if (superAppObjectBoundaries == null) {
            throw new Exception("Invalid data from server, expected an existent array of SuperAppObjectBoundaries with overall array length equal to 0 but got null instead");
        }

        if (superAppObjectBoundaries.length > 0) {
            throw new Exception("Invalid data from server, expected an existent array of SuperAppObjectBoundaries with overall array equal to 0 but got non empty array");
        }
    }

    /**
     * Test deletion of all SuperAppObjects from the database as superapp user.
     *
     * @throws Exception if the user in unauthorized to delete objects
     */
    @Test
    public void deleteAllObjectsAsSuperAppUserTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has several SuperAppObjectBoundary within it
        // AND the user who invoked this command is a SUPERAPP_USER
        this.injectObjectToDB();


        // WHEN I DELETE /superapp/admin/objects?userSuperapp={superapp}&userEmail={userEmail}
        // AND there is no request body

        // THEN the server responds with status 401 UNAUTHORIZED
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<SuperAppObjectBoundary> responseEntity = restTemplate.exchange(
                    String.format(this.adminObjectsUrl + "?userSuperapp=%s&userEmail=%s",
                            this.superAppUser.getUserId().getSuperapp(),
                            this.superAppUser.getUserId().getEmail()), HttpMethod.DELETE, null, SuperAppObjectBoundary.class);
            assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + responseEntity.getStatusCode() +
                    " instead", HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        });
        // Additional checks for the exception
        assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + exception.getStatusCode() +
                " instead", HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    /**
     * Test a binding of a child object to a parent object, both parent and child must exist and be different entities.
     *
     * @throws Exception if something goes wrong while saving the objects.
     */
    @Test
    public void bindChildObjectTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has existing SuperAppObjectBoundary parent and target children within it
        // AND the user who invoked this command is a SUPERAPP_USER
        SuperAppObjectBoundary parentObj = this.injectObjectToDB();
        SuperAppObjectBoundary childObj = this.injectObjectToDB();


        // WHEN I PUT /superapp/objects/{superapp}/{internalObjectId}/children?userSuperapp={superapp}&userEmail={userEmail}
        // AND the request body has SuperAppObjectIdBoundary as JSON format
        this.restTemplate.put(String.format(this.objectsUrl + "/%s/%s/children?userSuperapp=%s&userEmail=%s", parentObj.getObjectId().getSuperapp(),
                parentObj.getObjectId().getInternalObjectId(), this.superAppUser.getUserId().getSuperapp(),
                this.superAppUser.getUserId().getEmail()), childObj.getObjectId());

        // THEN the server responds with status 2XX
        // AND the response body is empty
        SuperAppObjectBoundary[] childrenArr = this.restTemplate.getForObject(
                String.format(this.objectsUrl + "/%s/%s/children?userSuperapp=%s&userEmail=%s", parentObj.getObjectId().getSuperapp(),
                        parentObj.getObjectId().getInternalObjectId(), this.superAppUser.getUserId().getSuperapp(),
                        this.superAppUser.getUserId().getEmail()), SuperAppObjectBoundary[].class);

        if (childrenArr == null || childrenArr.length == 0) {
            throw new Exception("Invalid data from server, expected an existent array of SuperAppObjectBoundaries with overall array length greater than 0");
        }

        Optional<SuperAppObjectBoundary> matchingObjs = Arrays.stream(childrenArr)
                .filter(obj -> obj.equals(childObj))  // Filter the stream to find children matching to child object
                .findFirst();  // Get the first matching fruit

        if (matchingObjs.isEmpty())
            throw new Exception("Binding did not work well");
    }


    /**
     * Test a binding of a child object to a parent object, as the parent object is the child as well.
     *
     * @throws Exception if the object cannot bind to itself.
     */
    @Test
    public void bindChildObjectToItselfTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has existing SuperAppObjectBoundary parent and target children within it
        // AND the user who invoked this command is a SUPERAPP_USER
        SuperAppObjectBoundary parentObj = this.restTemplate.postForObject(this.objectsUrl,
                this.testDBHelpFunc.createDummyObject(),
                SuperAppObjectBoundary.class);
        assert parentObj != null;


        // WHEN I PUT /superapp/objects/{superapp}/{internalObjectId}/children?userSuperapp={superapp}&userEmail={userEmail}
        // AND the child object is the parent
        // AND the request body has SuperAppObjectIdBoundary as JSON format
        this.restTemplate.put(String.format(this.objectsUrl + "/%s/%s/children?userSuperapp=%s&userEmail=%s", parentObj.getObjectId().getSuperapp(),
                parentObj.getObjectId().getInternalObjectId(), this.superAppUser.getUserId().getSuperapp(),
                this.superAppUser.getUserId().getEmail()), parentObj.getObjectId());

        // THEN the server responds with status 2XX
        // AND the response body is empty
        SuperAppObjectBoundary[] childrenArr = this.restTemplate.getForObject(
                String.format(this.objectsUrl + "/%s/%s/children?userSuperapp=%s&userEmail=%s", parentObj.getObjectId().getSuperapp(),
                        parentObj.getObjectId().getInternalObjectId(), this.superAppUser.getUserId().getSuperapp(),
                        this.superAppUser.getUserId().getEmail()), SuperAppObjectBoundary[].class);

        if (childrenArr == null || childrenArr.length == 0) {
            throw new Exception("Invalid data from server, expected an existent array of SuperAppObjectBoundaries with overall array length greater than 0");
        }

        Optional<SuperAppObjectBoundary> matchingObjs = Arrays.stream(childrenArr)
                .filter(obj -> obj.equals(parentObj))  // Filter the stream to find children matching to child object
                .findFirst();  // Get the first matching fruit

        if (matchingObjs.isEmpty())
            throw new Exception("Binding did not work well");
    }

    /**
     * Test a binding of a child object to a parent object, as the user who request to bind them is not authorized.
     *
     * @throws Exception if the user is not authorized to bind objects.
     */
    @Test
    public void bindObjectsUnauthorizedUserTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has existing SuperAppObjectBoundary parent and target children within it
        // AND the user who invoked this command is a MINIAPP_USER
        SuperAppObjectBoundary parentObj = this.restTemplate.postForObject(this.objectsUrl,
                this.testDBHelpFunc.createDummyObject(),
                SuperAppObjectBoundary.class);
        assert parentObj != null;
        SuperAppObjectBoundary childObj = this.restTemplate.postForObject(this.objectsUrl,
                this.testDBHelpFunc.createDummyObject(),
                SuperAppObjectBoundary.class);
        assert childObj != null;


        // WHEN I PUT /superapp/objects/{superapp}/{internalObjectId}/children?userSuperapp={superapp}&userEmail={userEmail}
        // AND the request body has SuperAppObjectIdBoundary as JSON format
        SuperAppObjectIdBoundary superAppObjectIdBoundary = new SuperAppObjectIdBoundary(
                parentObj.getObjectId().getSuperapp(),
                "123");
        childObj.setObjectId(superAppObjectIdBoundary);

        // THEN the server responds with status 401 UNAUTHORIZED
        // AND nothing updates in the database
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            this.restTemplate.put(String.format(this.objectsUrl + "/%s/%s/children?userSuperapp=%s&userEmail=%s", parentObj.getObjectId().getSuperapp(),
                    parentObj.getObjectId().getInternalObjectId(), this.miniAppUser.getUserId().getSuperapp(),
                    this.miniAppUser.getUserId().getEmail()), childObj.getObjectId());
        });
        // Additional checks for the exception
        assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + exception.getStatusCode() +
                " instead", HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }


    /**
     * Test a binding of a child object to a parent object,the parent object cannot be found.
     *
     * @throws Exception if the parent object cannot be found.
     */
    @Test
    public void bindChildObjectsParentNotFoundTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has existing SuperAppObjectBoundary parent and target children within it
        // AND the user who invoked this command is a SUPERAPP_USER
        SuperAppObjectBoundary parentObj = this.restTemplate.postForObject(this.objectsUrl,
                this.testDBHelpFunc.createDummyObject(),
                SuperAppObjectBoundary.class);
        assert parentObj != null;
        SuperAppObjectBoundary childObj = this.restTemplate.postForObject(this.objectsUrl,
                this.testDBHelpFunc.createDummyObject(),
                SuperAppObjectBoundary.class);
        assert childObj != null;


        // WHEN I PUT /superapp/objects/{superapp}/{internalObjectId}/children?userSuperapp={superapp}&userEmail={userEmail}
        // AND the request body has SuperAppObjectIdBoundary as JSON format
        SuperAppObjectIdBoundary superAppObjectIdBoundary = new SuperAppObjectIdBoundary(
                parentObj.getObjectId().getSuperapp(),
                "123");
        childObj.setObjectId(superAppObjectIdBoundary);

        // THEN the server responds with status 404 NOT FOUND
        // AND nothing updates in the database
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            this.restTemplate.put(String.format(this.objectsUrl + "/%s/%s/children?userSuperapp=%s&userEmail=%s", parentObj.getObjectId().getSuperapp(),
                    "123", this.superAppUser.getUserId().getSuperapp(),
                    this.superAppUser.getUserId().getEmail()), childObj.getObjectId());
        });
        // Additional checks for the exception
        assertEquals("Expected to get " + HttpStatus.NOT_FOUND + " but got " + exception.getStatusCode() +
                " instead", HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    /**
     * Test a binding of a child object to a parent object, the child cannot be found.
     *
     * @throws Exception if the child object cannot be found.
     */
    @Test
    public void bindChildObjectsChildNotFoundTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has existing SuperAppObjectBoundary parent and target children within it
        // AND the user who invoked this command is a SUPERAPP_USER
        SuperAppObjectBoundary parentObj = this.restTemplate.postForObject(this.objectsUrl,
                this.testDBHelpFunc.createDummyObject(),
                SuperAppObjectBoundary.class);
        assert parentObj != null;
        SuperAppObjectBoundary childObj = this.restTemplate.postForObject(this.objectsUrl,
                this.testDBHelpFunc.createDummyObject(),
                SuperAppObjectBoundary.class);
        assert childObj != null;


        // WHEN I PUT /superapp/objects/{superapp}/{internalObjectId}/children?userSuperapp={superapp}&userEmail={userEmail}
        // AND the request body has SuperAppObjectIdBoundary as JSON format
        SuperAppObjectIdBoundary superAppObjectIdBoundary = new SuperAppObjectIdBoundary(
                parentObj.getObjectId().getSuperapp(),
                "123");
        childObj.setObjectId(superAppObjectIdBoundary);

        // THEN the server responds with status 404 NOT FOUND
        // AND nothing updates in the database
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            this.restTemplate.put(String.format(this.objectsUrl + "/%s/%s/children?userSuperapp=%s&userEmail=%s", parentObj.getObjectId().getSuperapp(),
                    parentObj.getObjectId().getInternalObjectId(), this.superAppUser.getUserId().getSuperapp(),
                    this.superAppUser.getUserId().getEmail()), childObj.getObjectId());
        });
        // Additional checks for the exception
        assertEquals("Expected to get " + HttpStatus.NOT_FOUND + " but got " + exception.getStatusCode() +
                " instead", HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    /**
     * Test a binding of a child object to a parent object, the parent object already contains the child object.
     *
     * @throws Exception if the parent object already hold the child object.
     */
    @Test
    public void bindObjectsAlreadyBoundTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has existing SuperAppObjectBoundary parent and target children within it
        // AND the user who invoked this command is a SUPERAPP_USER
        SuperAppObjectBoundary parentObj = this.restTemplate.postForObject(this.objectsUrl,
                this.testDBHelpFunc.createDummyObject(),
                SuperAppObjectBoundary.class);
        assert parentObj != null;
        SuperAppObjectBoundary childObj = this.restTemplate.postForObject(this.objectsUrl,
                this.testDBHelpFunc.createDummyObject(),
                SuperAppObjectBoundary.class);
        assert childObj != null;


        // WHEN I PUT /superapp/objects/{superapp}/{internalObjectId}/children?userSuperapp={superapp}&userEmail={userEmail}
        // AND the request body has SuperAppObjectIdBoundary as JSON format
        this.restTemplate.put(String.format(this.objectsUrl + "/%s/%s/children?userSuperapp=%s&userEmail=%s", parentObj.getObjectId().getSuperapp(),
                parentObj.getObjectId().getInternalObjectId(), this.superAppUser.getUserId().getSuperapp(),
                this.superAppUser.getUserId().getEmail()), childObj.getObjectId());

        // THEN the server responds with status 400 BAD REQUEST
        // AND nothing updates in the database
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            this.restTemplate.put(String.format(this.objectsUrl + "/%s/%s/children?userSuperapp=%s&userEmail=%s", parentObj.getObjectId().getSuperapp(),
                    parentObj.getObjectId().getInternalObjectId(), this.superAppUser.getUserId().getSuperapp(),
                    this.superAppUser.getUserId().getEmail()), childObj.getObjectId());
        });
        // Additional checks for the exception
        assertEquals("Expected to get " + HttpStatus.BAD_REQUEST + " but got " + exception.getStatusCode() +
                " instead", HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    /**
     * Test a fetch of all the children of a parent object.
     *
     * @throws Exception if the parent object does not exist or the array of children is empty.
     */
    @Test
    public void getAllObjectChildrenTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has existing SuperAppObjectBoundary parent and some children within it
        // AND the user who invoked this command is a SUPERAPP_USER
        SuperAppObjectBoundary parentObj = this.injectObjectToDB();
        SuperAppObjectBoundary childObj = this.injectObjectToDB();
        this.restTemplate.put(String.format(this.objectsUrl + "/%s/%s/children?" + testDBHelpFunc.superappAuthUtl, parentObj.getObjectId().getSuperapp(),
                parentObj.getObjectId().getInternalObjectId()), childObj.getObjectId());

        // WHEN I GET /superapp/objects/{superapp}/{internalObjectId}/children?userSuperapp={superapp}&userEmail={userEmail}
        // AND the request body is empty
        // THEN the server responds with status 2XX
        // AND the response body is an array of SuperAppObjectsFormatted as JSON
        SuperAppObjectBoundary[] objectBoundaries = this.restTemplate.getForObject(
                String.format(this.objectsUrl + "/%s/%s/children?" + testDBHelpFunc.superappAuthUtl, parentObj.getObjectId().getSuperapp(),
                        parentObj.getObjectId().getInternalObjectId()), SuperAppObjectBoundary[].class);

        if (objectBoundaries == null || objectBoundaries.length == 0) {
            throw new Exception("Invalid data from server, expected an existent array of SuperAppObjectBoundaries with overall array length greater than 0");
        }
    }

    /**
     * Test a fetch of all the parents of a child object.
     *
     * @throws Exception if the child object does not exist or the array of parents is empy.
     */
    @Test
    public void getAllObjectChildrenAsSuperAppUserTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has several SuperAppObjectBoundaries within it
        // AND the user who invoked this command is a SUPERAPP_USER
        SuperAppObjectBoundary parent = this.restTemplate.postForObject(this.objectsUrl,
                this.testDBHelpFunc.createDummyObject(), SuperAppObjectBoundary.class);
        assert parent != null;
        IntStream.range(0, 4)
                .forEach(i -> {
                    SuperAppObjectBoundary child = this.testDBHelpFunc.createDummyObject();
                    if (i % 2 == 0)
                        child.setActive(false);

                    //Creating the child object
                    child = this.restTemplate.postForObject(this.objectsUrl, child, SuperAppObjectBoundary.class);
                    assert child != null;

                    //Binding the objects
                    this.restTemplate.put(String.format(this.objectsUrl + "/%s/%s/children?userSuperapp=%s&userEmail=%s",
                                    parent.getObjectId().getSuperapp(), parent.getObjectId().getInternalObjectId(),
                                    this.superAppUser.getUserId().getSuperapp(), this.superAppUser.getUserId().getEmail()),
                            child.getObjectId());
                });

        // WHEN I GET /superapp/objects/{superapp}/{internalObjectId}/children?userSuperapp={superapp}&userEmail={userEmail}
        // AND the request body is empty
        // THEN the server responds with status 2XX
        // AND the response body is an array of SuperAppObjectsFormatted as JSON
        SuperAppObjectBoundary[] children = this.restTemplate.getForObject(
                String.format(this.objectsUrl + "/%s/%s/children?userSuperapp=%s&userEmail=%s", parent.getObjectId().getSuperapp(),
                        parent.getObjectId().getInternalObjectId(), this.superAppUser.getUserId().getSuperapp(),
                        this.superAppUser.getUserId().getEmail()), SuperAppObjectBoundary[].class);

        if (children == null || children.length == 0) {
            throw new Exception("Invalid data from server, expected an existent array of SuperAppObjectBoundaries with overall array length greater than 0");
        }

        assertThat(children).
                isNotNull().
                hasSize(4);
    }

    /**
     * Test a fetch of all the children of a parent object as miniapp user.
     *
     * @throws Exception if the parent object does not exist or the array is empty.
     */
    @Test
    public void getAllObjectChildrenAsMiniAppUserTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has several SuperAppObjectBoundaries within it
        // AND the user who invoked this command is a MINIAPP_USER
        SuperAppObjectBoundary parent = this.restTemplate.postForObject(this.objectsUrl,
                this.testDBHelpFunc.createDummyObject(), SuperAppObjectBoundary.class);
        assert parent != null;
        IntStream.range(0, 4)
                .forEach(i -> {
                    SuperAppObjectBoundary child = this.testDBHelpFunc.createDummyObject();
                    if (i % 2 == 0)
                        child.setActive(false);

                    //Creating the child object
                    child = this.restTemplate.postForObject(this.objectsUrl, child, SuperAppObjectBoundary.class);
                    assert child != null;

                    //Binding the objects
                    this.restTemplate.put(String.format(this.objectsUrl + "/%s/%s/children?userSuperapp=%s&userEmail=%s",
                                    parent.getObjectId().getSuperapp(), parent.getObjectId().getInternalObjectId(),
                                    this.superAppUser.getUserId().getSuperapp(), this.superAppUser.getUserId().getEmail()),
                            child.getObjectId());
                });

        // WHEN I GET /superapp/objects/{superapp}/{internalObjectId}/children?userSuperapp={superapp}&userEmail={userEmail}
        // AND the request body is empty
        // THEN the server responds with status 2xx
        // AND the response body is an array of SuperAppObjectsFormatted as JSON
        SuperAppObjectBoundary[] children = this.restTemplate.getForObject(
                String.format(this.objectsUrl + "/%s/%s/children?userSuperapp=%s&userEmail=%s", parent.getObjectId().getSuperapp(),
                        parent.getObjectId().getInternalObjectId(), this.miniAppUser.getUserId().getSuperapp(),
                        this.miniAppUser.getUserId().getEmail()), SuperAppObjectBoundary[].class);

        if (children == null || children.length == 0) {
            throw new Exception("Invalid data from server, expected an existent array of SuperAppObjectBoundaries with overall array length greater than 0");
        }

        assertThat(children).
                isNotNull().
                hasSize(2);
    }


    /**
     * Test a fetch of all the children of a parent object as admin.
     *
     * @throws Exception if the child object does not exist.
     */
    @Test
    public void getAllObjectChildrenAsAdminTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has several SuperAppObjectBoundaries within it
        // AND the user who invoked this command is an ADMIN
        SuperAppObjectBoundary parent = this.restTemplate.postForObject(this.objectsUrl,
                this.testDBHelpFunc.createDummyObject(), SuperAppObjectBoundary.class);
        assert parent != null;
        IntStream.range(0, 4)
                .forEach(i -> {
                    SuperAppObjectBoundary child = this.testDBHelpFunc.createDummyObject();
                    if (i % 2 == 0)
                        child.setActive(false);

                    //Creating the child object
                    child = this.restTemplate.postForObject(this.objectsUrl, child, SuperAppObjectBoundary.class);
                    assert child != null;

                    //Binding the objects
                    this.restTemplate.put(String.format(this.objectsUrl + "/%s/%s/children?userSuperapp=%s&userEmail=%s",
                                    parent.getObjectId().getSuperapp(), parent.getObjectId().getInternalObjectId(),
                                    this.superAppUser.getUserId().getSuperapp(), this.superAppUser.getUserId().getEmail()),
                            child.getObjectId());
                });
        UserBoundary admin = this.restTemplate.postForObject(this.usersUrl, this.testDBHelpFunc.createDummyAdmin(), UserBoundary.class);
        assert admin != null;

        // WHEN I GET /superapp/objects/{superapp}/{internalObjectId}/children?userSuperapp={superapp}&userEmail={userEmail}
        // AND the request body is empty
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<SuperAppObjectBoundary[]> responseEntity = this.restTemplate.exchange(String.format(this.objectsUrl + "/%s/%s/children?userSuperapp=%s&userEmail=%s", parent.getObjectId().getSuperapp(),
                    parent.getObjectId().getInternalObjectId(), admin.getUserId().getSuperapp(),
                    admin.getUserId().getEmail()), HttpMethod.GET, null, SuperAppObjectBoundary[].class);
            assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + responseEntity.getStatusCode() +
                    " instead", HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        });
        // THEN the server responds with status 401 UNAUTHORIZED
        // Additional checks for the exception
        assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + exception.getStatusCode() +
                " instead", HttpStatus.UNAUTHORIZED, exception.getStatusCode());

        this.restTemplate.delete(String.format(this.adminUsersUrl + "?userSuperapp=%s&userEmail=%s",
                admin.getUserId().getSuperapp(),
                admin.getUserId().getEmail()));
    }


    /**
     * Test a fetch of all the parents of a child object as superapp user.
     *
     * @throws Exception if the child object does not exist.
     */
    @Test
    public void getAllObjectParentsAsSuperAppUserTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has several SuperAppObjectBoundaries within it
        // AND the user who invoked this command is a SUPERAPP_USER

        SuperAppObjectBoundary child = this.restTemplate.postForObject(this.objectsUrl,
                this.testDBHelpFunc.createDummyObject(), SuperAppObjectBoundary.class);
        assert child != null;
        IntStream.range(0, 4)
                .forEach(i -> {
                    SuperAppObjectBoundary parent = this.testDBHelpFunc.createDummyObject();
                    if (i % 2 == 0)
                        parent.setActive(false);

                    //Creating the child object
                    parent = this.restTemplate.postForObject(this.objectsUrl, parent, SuperAppObjectBoundary.class);
                    assert parent != null;

                    //Binding the objects
                    this.restTemplate.put(String.format(this.objectsUrl + "/%s/%s/children?userSuperapp=%s&userEmail=%s",
                                    parent.getObjectId().getSuperapp(), parent.getObjectId().getInternalObjectId(),
                                    this.superAppUser.getUserId().getSuperapp(), this.superAppUser.getUserId().getEmail()),
                            child.getObjectId());
                });

        // WHEN I GET /superapp/objects/{superapp}/{internalObjectId}/children?userSuperapp={superapp}&userEmail={userEmail}
        // AND the request body is empty
        // THEN the server responds with status 2XX
        // AND the response body is an array of SuperAppObjectsFormatted as JSON
        SuperAppObjectBoundary[] children = this.restTemplate.getForObject(
                String.format(this.objectsUrl + "/%s/%s/parents?userSuperapp=%s&userEmail=%s", child.getObjectId().getSuperapp(),
                        child.getObjectId().getInternalObjectId(), this.superAppUser.getUserId().getSuperapp(),
                        this.superAppUser.getUserId().getEmail()), SuperAppObjectBoundary[].class);

        if (children == null || children.length == 0) {
            throw new Exception("Invalid data from server, expected an existent array of SuperAppObjectBoundaries with overall array length greater than 0");
        }

        assertThat(children).
                isNotNull().
                hasSize(4);
    }

    /**
     * Test a fetch of all the parents of a child object as mini app user.
     *
     * @throws Exception if the child object does not exist.
     */
    @Test
    public void getAllObjectParentsAsMiniAppUserTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has several SuperAppObjectBoundaries within it
        // AND the user who invoked this command is a MINIAPP_USER

        SuperAppObjectBoundary child = this.restTemplate.postForObject(this.objectsUrl,
                this.testDBHelpFunc.createDummyObject(), SuperAppObjectBoundary.class);
        assert child != null;
        IntStream.range(0, 4)
                .forEach(i -> {
                    SuperAppObjectBoundary parent = this.testDBHelpFunc.createDummyObject();
                    if (i % 2 == 0)
                        parent.setActive(false);

                    //Creating the child object
                    parent = this.restTemplate.postForObject(this.objectsUrl, parent, SuperAppObjectBoundary.class);
                    assert parent != null;

                    //Binding the objects
                    this.restTemplate.put(String.format(this.objectsUrl + "/%s/%s/children?userSuperapp=%s&userEmail=%s",
                                    parent.getObjectId().getSuperapp(), parent.getObjectId().getInternalObjectId(),
                                    this.superAppUser.getUserId().getSuperapp(), this.superAppUser.getUserId().getEmail()),
                            child.getObjectId());
                });

        // WHEN I GET /superapp/objects/{superapp}/{internalObjectId}/children?userSuperapp={superapp}&userEmail={userEmail}
        // AND the request body is empty
        // THEN the server responds with status 2XX
        // AND the response body is an array of SuperAppObjectsFormatted as JSON
        SuperAppObjectBoundary[] children = this.restTemplate.getForObject(
                String.format(this.objectsUrl + "/%s/%s/parents?userSuperapp=%s&userEmail=%s", child.getObjectId().getSuperapp(),
                        child.getObjectId().getInternalObjectId(), this.miniAppUser.getUserId().getSuperapp(),
                        this.miniAppUser.getUserId().getEmail()), SuperAppObjectBoundary[].class);

        if (children == null || children.length == 0) {
            throw new Exception("Invalid data from server, expected an existent array of SuperAppObjectBoundaries with overall array length greater than 0");
        }

        assertThat(children).
                isNotNull().
                hasSize(2);
    }


    /**
     * Test a fetch of all the parents of a child object as admin.
     *
     * @throws Exception if the child object does not exist.
     */
    @Test
    public void getAllObjectParentsAsAdminTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has several SuperAppObjectBoundaries within it
        // AND the user who invoked this command is an ADMIN
        SuperAppObjectBoundary child = this.restTemplate.postForObject(this.objectsUrl,
                this.testDBHelpFunc.createDummyObject(), SuperAppObjectBoundary.class);
        assert child != null;
        IntStream.range(0, 4)
                .forEach(i -> {
                    SuperAppObjectBoundary parent = this.testDBHelpFunc.createDummyObject();
                    if (i % 2 == 0)
                        parent.setActive(false);

                    //Creating the child object
                    parent = this.restTemplate.postForObject(this.objectsUrl, parent, SuperAppObjectBoundary.class);
                    assert parent != null;

                    //Binding the objects
                    this.restTemplate.put(String.format(this.objectsUrl + "/%s/%s/children?userSuperapp=%s&userEmail=%s",
                                    parent.getObjectId().getSuperapp(), parent.getObjectId().getInternalObjectId(),
                                    this.superAppUser.getUserId().getSuperapp(), this.superAppUser.getUserId().getEmail()),
                            child.getObjectId());
                });
        UserBoundary admin = this.restTemplate.postForObject(this.usersUrl, this.testDBHelpFunc.createDummyAdmin(), UserBoundary.class);
        assert admin != null;

        // WHEN I GET /superapp/objects/{superapp}/{internalObjectId}/children?userSuperapp={superapp}&userEmail={userEmail}
        // THEN the server responds with status 401 UNAUTHORIZED
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<SuperAppObjectBoundary[]> responseEntity = this.restTemplate.exchange(String.format(this.objectsUrl + "/%s/%s/children?userSuperapp=%s&userEmail=%s", child.getObjectId().getSuperapp(),
                    child.getObjectId().getInternalObjectId(), admin.getUserId().getSuperapp(),
                    admin.getUserId().getEmail()), HttpMethod.GET, null, SuperAppObjectBoundary[].class);
            assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + responseEntity.getStatusCode() +
                    " instead", HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        });
        // Additional checks for the exception
        assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + exception.getStatusCode() +
                " instead", HttpStatus.UNAUTHORIZED, exception.getStatusCode());

        this.restTemplate.delete(String.format(this.adminUsersUrl + "?userSuperapp=%s&userEmail=%s",
                admin.getUserId().getSuperapp(),
                admin.getUserId().getEmail()));
    }


    /**
     * Test a fetch of objects by type as superapp user.
     *
     * @throws Exception if the nothing has return.
     */
    @Test
    public void getAllObjectsByTypeAsSuperAppUserTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has several SuperAppObjectBoundaries within it
        // AND the user who invoked this command is a SUPERAPP_USER
        IntStream.range(0, 4)
                .forEach(i -> {
                    SuperAppObjectBoundary testObj = this.testDBHelpFunc.createDummyObject();
                    if (i % 2 == 0)
                        testObj.setActive(false);

                    //Setting the type of the object
                    testObj.setType("test");

                    //Creating the object
                    this.restTemplate.postForObject(String.format(this.objectsUrl + "?userSuperapp=%s&userEmail=%s",
                                    this.superAppUser.getUserId().getSuperapp(), this.superAppUser.getUserId().getEmail()),
                            testObj, SuperAppObjectBoundary.class);


                });

        // WHEN I GET /superapp/objects/search/byType/test?userSuperapp={superapp}&userEmail={userEmail}
        // AND the request body is empty
        // THEN the server responds with status 2XX
        // AND the response body is an array of SuperAppObjectsFormatted as JSON
        SuperAppObjectBoundary[] children = this.restTemplate.getForObject(
                String.format(this.objectsUrl + "/search/byType/test?userSuperapp=%s&userEmail=%s",
                        this.superAppUser.getUserId().getSuperapp(),
                        this.superAppUser.getUserId().getEmail()), SuperAppObjectBoundary[].class);

        if (children == null || children.length == 0) {
            throw new Exception("Invalid data from server, expected an existent array of SuperAppObjectBoundaries with overall array length greater than 0");
        }

        assertThat(children).
                isNotNull().
                hasSize(4);
    }

    /**
     * Test a fetch objects by type as miniapp user.
     *
     * @throws Exception if nothing has return.
     */
    @Test
    public void getAllObjectsByTypeAsMiniAppUserTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has several SuperAppObjectBoundaries within it
        // AND the user who invoked this command is a MINIAPP_USER
        IntStream.range(0, 4)
                .forEach(i -> {
                    SuperAppObjectBoundary testObj = this.testDBHelpFunc.createDummyObject();
                    if (i % 2 == 0)
                        testObj.setActive(false);

                    //Setting the type of the object
                    testObj.setType("test");

                    //Creating the object
                    this.restTemplate.postForObject(String.format(this.objectsUrl + "?userSuperapp=%s&userEmail=%s",
                                    this.superAppUser.getUserId().getSuperapp(), this.superAppUser.getUserId().getEmail()),
                            testObj, SuperAppObjectBoundary.class);


                });

        // WHEN I GET /superapp/objects/search/byType/test?userSuperapp={superapp}&userEmail={userEmail}
        // AND the request body is empty
        // THEN the server responds with status 2XX
        // AND the response body is an array of SuperAppObjectsFormatted as JSON
        SuperAppObjectBoundary[] children = this.restTemplate.getForObject(
                String.format(this.objectsUrl + "/search/byType/test?userSuperapp=%s&userEmail=%s",
                        this.miniAppUser.getUserId().getSuperapp(),
                        this.miniAppUser.getUserId().getEmail()), SuperAppObjectBoundary[].class);

        if (children == null || children.length == 0) {
            throw new Exception("Invalid data from server, expected an existent array of SuperAppObjectBoundaries with overall array length greater than 0");
        }

        assertThat(children).
                isNotNull().
                hasSize(2);
    }


    /**
     * Test a fetch objects by type as admin.
     *
     * @throws Exception if the user in unauthorized.
     */
    @Test
    public void getAllObjectsByTypeAsAdminTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has several SuperAppObjectBoundaries within it
        // AND the user who invoked this command is an ADMIN
        IntStream.range(0, 4)
                .forEach(i -> {
                    SuperAppObjectBoundary testObj = this.testDBHelpFunc.createDummyObject();
                    if (i % 2 == 0)
                        testObj.setActive(false);

                    //Setting the type of the object
                    testObj.setType("test");

                    //Creating the object
                    this.restTemplate.postForObject(String.format(this.objectsUrl + "?userSuperapp=%s&userEmail=%s",
                                    this.superAppUser.getUserId().getSuperapp(), this.superAppUser.getUserId().getEmail()),
                            testObj, SuperAppObjectBoundary.class);


                });
        UserBoundary admin = this.restTemplate.postForObject(this.usersUrl, this.testDBHelpFunc.createDummyAdmin(), UserBoundary.class);
        assert admin != null;

        // WHEN I GET /superapp/objects/search/byType/test?userSuperapp={superapp}&userEmail={userEmail}
        // THEN the server responds with status 401 UNAUTHORIZED
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<SuperAppObjectBoundary[]> responseEntity = this.restTemplate.exchange(String.format(
                    this.objectsUrl + "/search/byType/test?userSuperapp=%s&userEmail=%s", admin.getUserId().getSuperapp(),
                    admin.getUserId().getEmail()), HttpMethod.GET, null, SuperAppObjectBoundary[].class);
            assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + responseEntity.getStatusCode() +
                    " instead", HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        });
        // Additional checks for the exception
        assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + exception.getStatusCode() +
                " instead", HttpStatus.UNAUTHORIZED, exception.getStatusCode());

        this.restTemplate.delete(String.format(this.adminUsersUrl + "?userSuperapp=%s&userEmail=%s",
                admin.getUserId().getSuperapp(),
                admin.getUserId().getEmail()));
    }


    /**
     * Test a fetch of objects by alias as superapp user.
     *
     * @throws Exception if nothing has returned.
     */
    @Test
    public void getAllObjectsByAliasAsSuperAppUserTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has several SuperAppObjectBoundaries within it
        // AND the user who invoked this command is a SUPERAPP_USER
        IntStream.range(0, 4)
                .forEach(i -> {
                    SuperAppObjectBoundary testObj = this.testDBHelpFunc.createDummyObject();
                    if (i % 2 == 0)
                        testObj.setActive(false);

                    //Setting the type of the object
                    testObj.setAlias("test");

                    //Creating the object
                    this.restTemplate.postForObject(String.format(this.objectsUrl + "?userSuperapp=%s&userEmail=%s",
                                    this.superAppUser.getUserId().getSuperapp(), this.superAppUser.getUserId().getEmail()),
                            testObj, SuperAppObjectBoundary.class);


                });

        // WHEN I GET /superapp/objects/search/byAlias/test?userSuperapp={superapp}&userEmail={userEmail}
        // AND the request body is empty
        // THEN the server responds with status 2XX
        // AND the response body is an array of SuperAppObjectsFormatted as JSON
        SuperAppObjectBoundary[] children = this.restTemplate.getForObject(
                String.format(this.objectsUrl + "/search/byAlias/test?userSuperapp=%s&userEmail=%s",
                        this.superAppUser.getUserId().getSuperapp(),
                        this.superAppUser.getUserId().getEmail()), SuperAppObjectBoundary[].class);

        if (children == null || children.length == 0) {
            throw new Exception("Invalid data from server, expected an existent array of SuperAppObjectBoundaries with overall array length greater than 0");
        }

        assertThat(children).
                isNotNull().
                hasSize(4);
    }

    /**
     * Test a fetch of objects by alias as miniapp user.
     *
     * @throws Exception if nothing has returned.
     */
    @Test
    public void getAllObjectsByAliasAsMiniAppUserTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has several SuperAppObjectBoundaries within it
        // AND the user who invoked this command is a MINIAPP_USER
        IntStream.range(0, 4)
                .forEach(i -> {
                    SuperAppObjectBoundary testObj = this.testDBHelpFunc.createDummyObject();
                    if (i % 2 == 0)
                        testObj.setActive(false);

                    //Setting the type of the object
                    testObj.setAlias("test");

                    //Creating the object
                    this.restTemplate.postForObject(String.format(this.objectsUrl + "?userSuperapp=%s&userEmail=%s",
                                    this.superAppUser.getUserId().getSuperapp(), this.superAppUser.getUserId().getEmail()),
                            testObj, SuperAppObjectBoundary.class);


                });

        // WHEN I GET /superapp/objects/search/byAlias/test?userSuperapp={superapp}&userEmail={userEmail}
        // AND the request body is empty
        // THEN the server responds with status 2XX
        // AND the response body is an array of SuperAppObjectsFormatted as JSON
        SuperAppObjectBoundary[] children = this.restTemplate.getForObject(
                String.format(this.objectsUrl + "/search/byAlias/test?userSuperapp=%s&userEmail=%s",
                        this.miniAppUser.getUserId().getSuperapp(),
                        this.miniAppUser.getUserId().getEmail()), SuperAppObjectBoundary[].class);

        if (children == null || children.length == 0) {
            throw new Exception("Invalid data from server, expected an existent array of SuperAppObjectBoundaries with overall array length greater than 0");
        }

        assertThat(children).
                isNotNull().
                hasSize(2);
    }


    /**
     * Test a fetch objects by type as admin.
     *
     * @throws Exception if the user in unauthorized.
     */
    @Test
    public void getAllObjectsByAliasAsAdminTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has several SuperAppObjectBoundaries within it
        // AND the user who invoked this command is an ADMIN
        IntStream.range(0, 4)
                .forEach(i -> {
                    SuperAppObjectBoundary testObj = this.testDBHelpFunc.createDummyObject();
                    if (i % 2 == 0)
                        testObj.setActive(false);

                    //Setting the type of the object
                    testObj.setAlias("test");

                    //Creating the object
                    this.restTemplate.postForObject(String.format(this.objectsUrl + "?userSuperapp=%s&userEmail=%s",
                                    this.superAppUser.getUserId().getSuperapp(), this.superAppUser.getUserId().getEmail()),
                            testObj, SuperAppObjectBoundary.class);


                });
        UserBoundary admin = this.restTemplate.postForObject(this.usersUrl, this.testDBHelpFunc.createDummyAdmin(), UserBoundary.class);
        assert admin != null;

        // WHEN I GET /superapp/objects/search/byAlias/test?userSuperapp={superapp}&userEmail={userEmail}
        // THEN the server responds with status 401 UNAUTHORIZED
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<SuperAppObjectBoundary[]> responseEntity = this.restTemplate.exchange(
                    String.format(this.objectsUrl + "/search/byAlias/test?userSuperapp=%s&userEmail=%s", admin.getUserId().getSuperapp(),
                            admin.getUserId().getEmail()), HttpMethod.GET, null, SuperAppObjectBoundary[].class);
            assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + responseEntity.getStatusCode() +
                    " instead", HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        });
        // Additional checks for the exception
        assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + exception.getStatusCode() +
                " instead", HttpStatus.UNAUTHORIZED, exception.getStatusCode());

        this.restTemplate.delete(String.format(this.adminUsersUrl + "?userSuperapp=%s&userEmail=%s",
                admin.getUserId().getSuperapp(),
                admin.getUserId().getEmail()));
    }


    /**
     * Test a fetch objects by location in by miles as units as super app user
     *
     * @throws Exception the number of objects is less than excepted.
     */
    @Test
    public void getAllObjectsByLocationAsMilesAsSuperAppUserTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has several SuperAppObjectBoundaries within it
        // AND the user who invoked this command is a SUPERAPP_USER
        IntStream.range(0, 4)
                .forEach(i -> {
                    SuperAppObjectBoundary testObj = this.testDBHelpFunc.createDummyObject();
                    Location location = new Location();
                    if (i % 2 == 0) {
                        testObj.setActive(false);
                        location.setLat(35.0);
                        location.setLng(28.0);
                    } else {
                        location.setLat(32.0);
                        location.setLng(30.0);
                    }
                    //Setting the location of the object
                    testObj.setLocation(location);

                    //Creating the object
                    this.restTemplate.postForObject(String.format(this.objectsUrl + "?userSuperapp=%s&userEmail=%s",
                                    this.superAppUser.getUserId().getSuperapp(), this.superAppUser.getUserId().getEmail()),
                            testObj, SuperAppObjectBoundary.class);


                });

        // WHEN I GET /superapp/objects/search/byLocation/{lat}/{lng}/{distance}?units=MILES&userSuperapp={superapp}&userEmail={userEmail}
        // AND the request body is empty
        // THEN the server responds with status 2XX
        // AND the response body is an array of SuperAppObjectsFormatted as JSON
        SuperAppObjectBoundary[] objects = this.restTemplate.getForObject(
                String.format(this.objectsUrl + "/search/byLocation/35/28/300?units=MILES&userSuperapp=%s&userEmail=%s",
                        this.superAppUser.getUserId().getSuperapp(),
                        this.superAppUser.getUserId().getEmail()), SuperAppObjectBoundary[].class);

        if (objects == null || objects.length == 0) {
            throw new Exception("Invalid data from server, expected an existent array of SuperAppObjectBoundaries with overall array length greater than 0");
        }

        assertThat(objects).
                isNotNull().
                hasSize(4);
    }

    /**
     * Test a fetch objects by location in by kilometers as units as super app user
     *
     * @throws Exception the number of objects is less than excepted.
     */
    @Test
    public void getAllObjectsByLocationAsKilometersAsSuperAppUserTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has several SuperAppObjectBoundaries within it
        // AND the user who invoked this command is a SUPERAPP_USER
        IntStream.range(0, 4)
                .forEach(i -> {
                    SuperAppObjectBoundary testObj = this.testDBHelpFunc.createDummyObject();
                    Location location = new Location();
                    if (i % 2 == 0) {
                        testObj.setActive(false);
                        location.setLat(35.0);
                        location.setLng(28.0);
                    } else {
                        location.setLat(32.0);
                        location.setLng(30.0);
                    }
                    //Setting the location of the object
                    testObj.setLocation(location);

                    //Creating the object
                    this.restTemplate.postForObject(String.format(this.objectsUrl + "?userSuperapp=%s&userEmail=%s",
                                    this.superAppUser.getUserId().getSuperapp(), this.superAppUser.getUserId().getEmail()),
                            testObj, SuperAppObjectBoundary.class);


                });

        // WHEN I GET /superapp/objects/search/byLocation/{lat}/{lng}/{distance}?units=KILOMETERS&userSuperapp={superapp}&userEmail={userEmail}
        // AND the request body is empty
        // THEN the server responds with status 2XX
        // AND the response body is an array of SuperAppObjectsFormatted as JSON
        SuperAppObjectBoundary[] objects = this.restTemplate.getForObject(
                String.format(this.objectsUrl + "/search/byLocation/35/28/300?units=KILOMETERS&userSuperapp=%s&userEmail=%s",
                        this.superAppUser.getUserId().getSuperapp(),
                        this.superAppUser.getUserId().getEmail()), SuperAppObjectBoundary[].class);

        if (objects == null || objects.length == 0) {
            throw new Exception("Invalid data from server, expected an existent array of SuperAppObjectBoundaries with overall array length greater than 0");
        }

        assertThat(objects).
                isNotNull().
                hasSize(2);
    }

    /**
     * Test a fetch objects by location as miniapp user
     *
     * @throws Exception the number of objects is more than excepted.
     */
    @Test
    public void getAllObjectsByLocationAsMiniAppUserTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has several SuperAppObjectBoundaries within it
        // AND the user who invoked this command is a MINIAPP_USER
        IntStream.range(0, 4)
                .forEach(i -> {
                    SuperAppObjectBoundary testObj = this.testDBHelpFunc.createDummyObject();
                    Location location = new Location();
                    if (i % 2 == 0) {
                        testObj.setActive(false);
                        location.setLat(35.0);
                        location.setLng(28.0);
                    } else {
                        location.setLat(32.0);
                        location.setLng(30.0);
                    }
                    //Setting the location of the object
                    testObj.setLocation(location);

                    //Creating the object
                    this.restTemplate.postForObject(String.format(this.objectsUrl + "?userSuperapp=%s&userEmail=%s",
                                    this.superAppUser.getUserId().getSuperapp(), this.superAppUser.getUserId().getEmail()),
                            testObj, SuperAppObjectBoundary.class);


                });

        // WHEN I GET /superapp/objects/search/byLocation/{lat}/{lng}/{distance}?units=MILES&userSuperapp={superapp}&userEmail={userEmail}
        // AND the request body is empty
        // THEN the server responds with status 2XX
        // AND the response body is an array of SuperAppObjectsFormatted as JSON
        SuperAppObjectBoundary[] objects = this.restTemplate.getForObject(
                String.format(this.objectsUrl + "/search/byLocation/35/28/300?units=MILES&userSuperapp=%s&userEmail=%s",
                        this.miniAppUser.getUserId().getSuperapp(),
                        this.miniAppUser.getUserId().getEmail()), SuperAppObjectBoundary[].class);

        if (objects == null || objects.length == 0) {
            throw new Exception("Invalid data from server, expected an existent array of SuperAppObjectBoundaries with overall array length greater than 0");
        }

        assertThat(objects).
                isNotNull().
                hasSize(2);
    }

    /**
     * Test a fetch objects by location by invalid units method as superapp user
     *
     * @throws Exception the number of objects is more than excepted.
     */
    @Test
    public void getAllObjectsByLocationInvalidUnitTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has several SuperAppObjectBoundaries within it
        // AND the user who invoked this command is a SUPERAPP_USER
        IntStream.range(0, 4)
                .forEach(i -> {
                    SuperAppObjectBoundary testObj = this.testDBHelpFunc.createDummyObject();
                    Location location = new Location();
                    if (i % 2 == 0) {
                        testObj.setActive(false);
                        location.setLat(35.0);
                        location.setLng(28.0);
                    } else {
                        location.setLat(32.0);
                        location.setLng(30.0);
                    }
                    //Setting the location of the object
                    testObj.setLocation(location);

                    //Creating the object
                    this.restTemplate.postForObject(String.format(this.objectsUrl + "?userSuperapp=%s&userEmail=%s",
                                    this.superAppUser.getUserId().getSuperapp(), this.superAppUser.getUserId().getEmail()),
                            testObj, SuperAppObjectBoundary.class);


                });
        // WHEN I GET /superapp/objects/search/byLocation/{lat}/{lng}/{distance}?units=NEUTRAL&userSuperapp={superapp}&userEmail={userEmail}
        // THEN the server responds with status 400 BAD REQUEST
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<SuperAppObjectBoundary[]> responseEntity = this.restTemplate.exchange(String.format(this.objectsUrl + "/search/byLocation/35/28/300?units=NEUTRAL&userSuperapp=%s&userEmail=%s",
                    this.superAppUser.getUserId().getSuperapp(),
                    this.superAppUser.getUserId().getEmail()), HttpMethod.GET, null, SuperAppObjectBoundary[].class);
            assertEquals("Expected to get " + HttpStatus.BAD_REQUEST + " but got " + responseEntity.getStatusCode() +
                    " instead", HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        });
        // Additional checks for the exception
        assertEquals("Expected to get " + HttpStatus.BAD_REQUEST + " but got " + exception.getStatusCode() +
                " instead", HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }


    /**
     * Test a fetch objects by location as admin.
     *
     * @throws Exception the user is unauthorized.
     */
    @Test
    public void getAllObjectsByLocationAsAdminTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has several SuperAppObjectBoundaries within it
        // AND the user who invoked this command is an ADMIN
        IntStream.range(0, 4)
                .forEach(i -> {
                    SuperAppObjectBoundary testObj = this.testDBHelpFunc.createDummyObject();
                    Location location = new Location();
                    if (i % 2 == 0) {
                        testObj.setActive(false);
                        location.setLat(35.0);
                        location.setLng(28.0);
                    } else {
                        location.setLat(32.0);
                        location.setLng(30.0);
                    }
                    //Setting the location of the object
                    testObj.setLocation(location);

                    //Creating the object
                    this.restTemplate.postForObject(String.format(this.objectsUrl + "?userSuperapp=%s&userEmail=%s",
                                    this.superAppUser.getUserId().getSuperapp(), this.superAppUser.getUserId().getEmail()),
                            testObj, SuperAppObjectBoundary.class);


                });

        UserBoundary admin = this.restTemplate.postForObject(this.usersUrl, this.testDBHelpFunc.createDummyAdmin(), UserBoundary.class);
        assert admin != null;

        // WHEN I GET /superapp/objects/search/byLocation/{lat}/{lng}/{distance}?units=MILES&userSuperapp={superapp}&userEmail={userEmail}
        // THEN the server responds with status 401 UNAUTHORIZED
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<SuperAppObjectBoundary[]> responseEntity = this.restTemplate.exchange(String.format(this.objectsUrl + "/search/byLocation/35/28/300?units=MILES&userSuperapp=%s&userEmail=%s",
                    admin.getUserId().getSuperapp(),
                    admin.getUserId().getEmail()), HttpMethod.GET, null, SuperAppObjectBoundary[].class);
            assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + responseEntity.getStatusCode() +
                    " instead", HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        });
        // Additional checks for the exception
        assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + exception.getStatusCode() +
                " instead", HttpStatus.UNAUTHORIZED, exception.getStatusCode());

        this.restTemplate.delete(String.format(this.adminUsersUrl + "?userSuperapp=%s&userEmail=%s",
                admin.getUserId().getSuperapp(),
                admin.getUserId().getEmail()));
    }


    /**
     * Injects a JSON object into the database.
     *
     * @return the injected object as a SuperAppObjectBoundary.
     * @throws Exception if an error occurs while injecting the object or the response from the server is invalid.
     */
    public SuperAppObjectBoundary injectObjectToDB() throws Exception {
        // Creates a new SuperAppObjectBoundary object using the createDummy() method
        SuperAppObjectBoundary testObj = testDBHelpFunc.createDummyObject();
        UserIdInvoker userIdInvoker = new UserIdInvoker(this.superAppUser.getUserId());
        testObj.setCreatedBy(userIdInvoker);
        return this.restTemplate.postForObject(this.objectsUrl, testObj, SuperAppObjectBoundary.class);
    }


    /**
     * Compares two SuperAppObjectBoundary objects to determine if they are equal.
     *
     * @param expectedObj the expected object.
     * @param returnObj   the returned object.
     * @return true if the objects are equal, false otherwise.
     */
    public boolean compareResponseToRequest(SuperAppObjectBoundary expectedObj, SuperAppObjectBoundary returnObj) {
        return expectedObj.getType().equals(returnObj.getType()) &&
                expectedObj.getAlias().equals(returnObj.getAlias()) &&
                expectedObj.getActive().equals(returnObj.getActive()) &&
                expectedObj.getLocation().equals(returnObj.getLocation()) &&
                expectedObj.getObjectDetails().toString().equals(returnObj.getObjectDetails().toString());
    }

}