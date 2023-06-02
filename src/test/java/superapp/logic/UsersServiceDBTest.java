package superapp.logic;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import superapp.boundaries.user.NewUserBoundary;
import superapp.boundaries.user.UserBoundary;
import superapp.utils.TestDBHelpFunc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertEquals;

/**
 * This class contains integration tests for the UsersServiceDB class.
 * It focuses on testing the CRUD operations of the users service using a database.
 * In addition, this class is testing the permissions of different users.
 */
@Tag("UsersServiceDBTest")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UsersServiceDBTest {

    private int port;
    private String usersUrl; // Users url as simple base url
    private String adminUsersUrl; // Admin url for users API as simple base url
    private UserBoundary miniAppUser; // MiniApp user boundary object
    private UserBoundary superAppUser; // SuperApp user boundary object
    private RestTemplate restTemplate; // REST template object
    private TestDBHelpFunc testDBHelpFunc; //Helper class object

    /**
     * Setup method to configure the base URL and REST endpoints.
     * It runs before each test case.
     *
     * @param port The randomly assigned port for the server.
     */
    @LocalServerPort
    public void setup(int port) {
        this.usersUrl = "http://localhost:" + port + "/superapp/users";
        this.adminUsersUrl = "http://localhost:" + port + "/superapp/admin/users";
        this.restTemplate = new RestTemplate();
        this.port = port;
        this.testDBHelpFunc = new TestDBHelpFunc();

    }

    /**
     * Cleans up the database after each test by sending a delete request to the delete URL to eac service.
     */
    @BeforeEach
    public void setup() {
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
    public void tearDown() {
        UserBoundary admin = this.restTemplate.postForObject(this.usersUrl,
                this.testDBHelpFunc.createDummyAdmin(),
                UserBoundary.class);
        assert admin != null;

        this.restTemplate.delete(String.format(this.adminUsersUrl + "?userSuperapp=%s&userEmail=%s",
                admin.getUserId().getSuperapp(),
                admin.getUserId().getEmail()));
    }

    /**
     * Test creation of  a new UserBoundary object.
     *
     * @throws Exception if the posted user is not the same as the retrieve user
     */
    @Test
    public void createUserTest() throws Exception {
        // GIVEN the server is up
        // AND the database is empty

        // WHEN I POST /superapp/users
        // AND the request body has NewUserBoundary as JSON format with no userId
        NewUserBoundary newUserBoundary = this.testDBHelpFunc.createDummySuperappUser();
        newUserBoundary.setEmail("stam@demo.org");
        UserBoundary returnObj = this.restTemplate.postForObject(this.usersUrl, newUserBoundary, UserBoundary.class);

        // AND the response body is a UserBoundary that has all the NewUserBoundary files that sent to the UsersServiceDB
        // but now with userId
        if (returnObj == null || returnObj.getUserId() == null) {
            throw new Exception("Invalid data from server, expected an existent object, but received field with null instead");
        }
        // THEN the user is stored to the database
        UserBoundary check = this.restTemplate.getForObject(this.usersUrl + "/login/" + returnObj.getUserId().getSuperapp() + "/" +
                returnObj.getUserId().getEmail(), UserBoundary.class);
        assert check != null;

        if (!this.compareResponseToRequest(returnObj, check))
            throw new Exception("Some fields except objectId are different.\n" +
                    "expected the object: " + check + "\nbut instead got the object " + returnObj);

    }


    /**
     * Test creation of a new SuperAppObjectBoundary object.
     *
     * @throws Exception if the user is already exists
     */
    @Test
    public void createUserAlreadyExistTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty
        // AND there is a user with the same information

        // WHEN I POST /superapp/users
        // AND the request body has NewUserBoundary as JSON format with no userId
        // AND there is a user with same details
        NewUserBoundary newUserBoundary = this.testDBHelpFunc.createDummySuperappUser();

        // THEN the server has thrown an exception that the user is already exists
        // AND the server responds with status 409 CONFLICT
        // AND the response body is empty
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<UserBoundary> responseEntity = restTemplate.postForEntity(this.usersUrl, newUserBoundary, UserBoundary.class);
            assertEquals("Expected to get " + HttpStatus.CONFLICT + " but got " + responseEntity.getStatusCode() +
                    " instead ", HttpStatus.CONFLICT, responseEntity.getStatusCode());
        });
        assertEquals("Expected to get " + HttpStatus.CONFLICT + " but got " + exception.getStatusCode() +
                " instead ", HttpStatus.CONFLICT, exception.getStatusCode());

    }

    /**
     * Test a retrieve of a specific UserBoundary from the database with the given superapp and email.
     *
     * @throws Exception if no matching user is found
     */
    @Test
    public void loginTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has specific UserBoundary within it

        // WHEN I GET /superapp/users/login/{superapp}/{email}
        // AND there is no request body
        // AND the user's details hasn't been changed in the meanwhile
        UserBoundary returnObj = this.restTemplate.getForObject(
                String.format(this.usersUrl + "/login/%s/%s?", this.superAppUser.getUserId().getSuperapp(),
                        this.superAppUser.getUserId().getEmail()), UserBoundary.class);

        // THEN the server responds with status 2xx
        // AND the response body contains the UserBoundary with the matching userId

        if (returnObj == null || returnObj.getUserId() == null) {
            throw new Exception("Invalid data from server, expected an existent object, but received field with null instead");
        }

        if (!this.compareResponseToRequest(this.superAppUser, returnObj))
            throw new Exception("Some fields except objectId are different.\n" +
                    "expected the object: " + this.superAppUser + "\nbut instead got the object " + returnObj);

    }

    /**
     * Test a retrieve of non-existent UserBoundary from the database with the given superapp and email.
     *
     * @throws Exception if no matching user is found
     */
    @Test
    public void loginUserNotExistTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and the tested UserBoundary is not within it

        // WHEN I GET /superapp/users/login/{superapp}/{email}
        // AND there is no request body

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<UserBoundary> responseEntity = restTemplate.exchange(
                    String.format(this.usersUrl + "/login/%s/%s",
                            this.superAppUser.getUserId().getSuperapp(),
                            "stam@gmail.com"), HttpMethod.GET, null, UserBoundary.class);
            // THEN the server responds with status 404 NOT FOUND
            // AND the response body is empty
            assertEquals("Expected to get " + HttpStatus.NOT_FOUND + " but got " + responseEntity.getStatusCode() +
                    " instead ", HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        });
        assertEquals("Expected to get " + HttpStatus.NOT_FOUND + " but got " + exception.getStatusCode() +
                " instead ", HttpStatus.NOT_FOUND, exception.getStatusCode());
    }


    /**
     * Test a retrieve of UserBoundary with unrecognized superapp identifier
     *
     * @throws Exception if the superapp identifier is not legal
     */
    @Test
    public void loginInvalidSuperAppTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and the tested UserBoundary is not within it

        // WHEN I GET /superapp/users/login/{superapp}/{email}
        // and the superapp is unrecognized
        // AND there is no request body

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<UserBoundary> responseEntity = restTemplate.exchange(
                    String.format(this.usersUrl + "/login/%s/%s",
                            "MetaSuperApp",
                            this.superAppUser.getUserId().getEmail()), HttpMethod.GET, null, UserBoundary.class);
            // THEN the server responds with status 400 BAD REQUEST
            // AND the response body is empty
            assertEquals("Expected to get " + HttpStatus.NOT_FOUND + " but got " + responseEntity.getStatusCode() +
                    " instead ", HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        });
        assertEquals("Expected to get " + HttpStatus.BAD_REQUEST + " but got " + exception.getStatusCode() +
                " instead ", HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    /**
     * Test an update of existing UserBoundary object.
     *
     * @throws Exception if the UserBoundary cannot be found by the given ID
     */
    @Test
    public void updateUserTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has specific SUPERAPP_USER within it

        // WHEN I PUT /superapp/users/{superapp}/{email}
        // AND the request body has UserBoundary as JSON format with different fields
        this.superAppUser.setUsername(this.superAppUser.getUsername().concat(".test"));
        this.superAppUser.setAvatar(this.superAppUser.getAvatar().concat(".test"));
        this.superAppUser.setRole("MINIAPP_USER");

        this.restTemplate.put(String.format(this.usersUrl + "/%s/%s", this.superAppUser.getUserId().getSuperapp(),
                this.superAppUser.getUserId().getEmail()), this.superAppUser);

        // THEN the server responds with status 2xx
        // AND the response body is empty
        // AND I GET /superapp/users/login/{superapp}/{email} for identity validation, the objects should be the same fields
        UserBoundary returnObj = this.restTemplate.getForObject(
                String.format(this.usersUrl + "/login/%s/%s?", this.superAppUser.getUserId().getSuperapp(),
                        this.superAppUser.getUserId().getEmail()), UserBoundary.class);

        if (returnObj == null || returnObj.getUserId() == null)
            throw new Exception("Invalid data from server, expected an existent object, but received field with null instead");

        if (!this.compareResponseToRequest(this.superAppUser, returnObj))
            throw new Exception("Some fields except objectId are different.\n" +
                    "expected the object: " + this.superAppUser + "\nbut instead got the object " + returnObj);
    }

    /**
     * Test an update of invalid role for UserBoundary object.
     *
     * @throws Exception if the UserRole is invalid
     */
    @Test
    public void updateUserInvalidRoleTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has specific SUPERAPP_USER within it

        // WHEN I PUT /superapp/users/{superapp}/{email}
        // AND the UserRole is invalid
        // AND the request body has UserBoundary as JSON format with different fields
        this.superAppUser.setRole("SUPER_POWER_ADMIN");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<UserBoundary> requestEntity = new HttpEntity<>(this.superAppUser, headers);

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<UserBoundary> responseEntity = restTemplate.exchange(String.format(this.usersUrl + "/%s/%s",
                            this.superAppUser.getUserId().getSuperapp(),
                            this.superAppUser.getUserId().getEmail()), HttpMethod.PUT,
                    requestEntity,
                    UserBoundary.class);
            // THEN the server responds with status 400 BAD REQUEST
            // AND the response body is empty
            assertEquals("Expected to get " + HttpStatus.BAD_REQUEST + " but got " + responseEntity.getStatusCode() +
                    " instead ", HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        });
        assertEquals("Expected to get " + HttpStatus.BAD_REQUEST + " but got " + exception.getStatusCode() +
                " instead ", HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    /**
     * Test an update of empty username for UserBoundary object.
     *
     * @throws Exception if the username is empty
     */
    @Test
    public void updateUserEmptyUsernameTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has specific SUPERAPP_USER within it

        // WHEN I PUT /superapp/users/{superapp}/{email}
        // AND the username is empty
        // AND the request body has UserBoundary as JSON format with different fields
        this.superAppUser.setUsername("");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<UserBoundary> requestEntity = new HttpEntity<>(this.superAppUser, headers);

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<UserBoundary> responseEntity = restTemplate.exchange(String.format(this.usersUrl + "/%s/%s",
                            this.superAppUser.getUserId().getSuperapp(),
                            this.superAppUser.getUserId().getEmail()), HttpMethod.PUT,
                    requestEntity,
                    UserBoundary.class);
            // THEN the server responds with status 400 BAD REQUEST
            // AND the response body is empty
            assertEquals("Expected to get " + HttpStatus.BAD_REQUEST + " but got " + responseEntity.getStatusCode() +
                    " instead ", HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        });
        assertEquals("Expected to get " + HttpStatus.BAD_REQUEST + " but got " + exception.getStatusCode() +
                " instead ", HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }


    /**
     * Test an update of empty username for UserBoundary object.
     *
     * @throws Exception if the user's avatar is empty
     */
    @Test
    public void updateUserEmptyAvatarTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has specific SUPERAPP_USER within it

        // WHEN I PUT /superapp/users/{superapp}/{email}
        // AND the avatar is empty
        // AND the request body has UserBoundary as JSON format with different fields
        this.superAppUser.setAvatar("");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<UserBoundary> requestEntity = new HttpEntity<>(this.superAppUser, headers);

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<UserBoundary> responseEntity = restTemplate.exchange(String.format(this.usersUrl + "/%s/%s",
                            this.superAppUser.getUserId().getSuperapp(),
                            this.superAppUser.getUserId().getEmail()), HttpMethod.PUT,
                    requestEntity,
                    UserBoundary.class);
            // THEN the server responds with status 400 BAD REQUEST
            // AND the response body is empty
            assertEquals("Expected to get " + HttpStatus.BAD_REQUEST + " but got " + responseEntity.getStatusCode() +
                    " instead ", HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        });
        assertEquals("Expected to get " + HttpStatus.BAD_REQUEST + " but got " + exception.getStatusCode() +
                " instead ", HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    /**
     * Test a retrieve of all UserBoundaries from the database.
     *
     * @throws Exception if no matching object is found
     */
    @Test
    public void getAllUsersTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has several SUPERAPP_USERs and MINIAPP_USERs  and ADMIN within it
        UserBoundary admin = this.restTemplate.postForObject(this.usersUrl,
                this.testDBHelpFunc.createDummyAdmin(),
                UserBoundary.class);
        assert admin != null;


        // WHEN I GET /superapp/users/
        // AND there is no request body

        UserBoundary[] usersFromDatabase = this.restTemplate.getForObject(String.format(this.adminUsersUrl +
                "?userSuperapp=%s&userEmail=%s&size=2&page=1", admin.getUserId().getSuperapp(), admin.getUserId().getEmail()), UserBoundary[].class);

        // THEN the server responds with status 2xx
        // AND the response body is an array of UserBoundary that is not empty
        // AND the array contains only the ADMIN

        if (usersFromDatabase == null || usersFromDatabase.length == 0)
            throw new Exception("Expected array of UserBoundary, instead got empty array");
        assertThat(usersFromDatabase).contains(admin).hasSize(1);

        this.restTemplate.delete(String.format(this.adminUsersUrl + "?userSuperapp=%s&userEmail=%s",
                admin.getUserId().getSuperapp(),
                admin.getUserId().getEmail()));

    }


    /**
     * Test a retrieve of all UserBoundaries from the database by miniapp user.
     *
     * @throws Exception if no matching object is found
     */
    @Test
    public void getAllUsersByMiniAppUserTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has several SUPERAPP_USERs and MINIAPP_USERs  and ADMIN within it

        // WHEN I GET /superapp/users/
        // AND the user who requested the url is a MINIAPP_USER
        // AND there is no request body

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<UserBoundary[]> responseEntity = restTemplate.exchange(
                    String.format(this.adminUsersUrl + "?userSuperapp=%s&userEmail=%s",
                            this.miniAppUser.getUserId().getSuperapp(),
                            this.miniAppUser.getUserId().getEmail()), HttpMethod.GET, null, UserBoundary[].class);
            // THEN the server responds with status 401 UNAUTHORIZED
            // AND the response body is empty
            assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + responseEntity.getStatusCode() +
                    " instead ", HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        });
        assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + exception.getStatusCode() +
                " instead ", HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    /**
     * Test deletion of all UserBoundaries from the database.
     *
     * @throws Exception if no matching object is found
     */
    @Test
    public void deleteAllUsersTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has ADMIN within it
        UserBoundary admin = this.restTemplate.postForObject(this.usersUrl,
                this.testDBHelpFunc.createDummyAdmin(),
                UserBoundary.class);
        assert admin != null;

        // WHEN I DELETE /superapp/users?userSuperapp={superapp}&userEmail={userEmail}
        // AND there is no request body
        this.restTemplate.delete(String.format(this.adminUsersUrl + "?userSuperapp=%s&userEmail=%s",
                admin.getUserId().getSuperapp(),
                admin.getUserId().getEmail()));
        this.restTemplate.postForObject(this.usersUrl, testDBHelpFunc.createDummyAdmin(), UserBoundary.class);

        // THEN the server responds with status 2xx
        // AND the response body is empty
        // AND when I GET /superapp/users?userSuperapp={superapp}&userEmail={userEmail} I'll receive an empty array
        UserBoundary[] usersFromDatabase = this.restTemplate.getForObject(String.format(this.adminUsersUrl + "?userSuperapp=%s&userEmail=%s",
                admin.getUserId().getSuperapp(),
                admin.getUserId().getEmail()), UserBoundary[].class);

        if (usersFromDatabase == null) {
            throw new Exception("Invalid data from server, expected an existent array of SuperAppObjectBoundaries with overall array length equal to 0 but got null instead");
        }

        if (usersFromDatabase.length != 1) {
            throw new Exception("Invalid data from server, expected an existent array of SuperAppObjectBoundaries with overall array equal to 0 but got non empty array");
        }

        this.restTemplate.delete(String.format(this.adminUsersUrl + "?userSuperapp=%s&userEmail=%s",
                admin.getUserId().getSuperapp(),
                admin.getUserId().getEmail()));
    }


    /**
     * Test deletion of unauthorized user.
     *
     * @throws Exception if th user is unauthorized to delete users
     */
    @Test
    public void deleteAllUsersFailedTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty

        // WHEN I DELETE /superapp/users?userSuperapp={superapp}&userEmail={userEmail}
        // AND the user in unauthorized to delete users
        // AND there is no request body

        // THEN the server responds with status 401 UNAUTHORIZED
        // AND the response body is empty
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<UserBoundary[]> responseEntity = restTemplate.exchange(
                    String.format(this.adminUsersUrl + "?userSuperapp=%s&userEmail=%s",
                            this.miniAppUser.getUserId().getSuperapp(),
                            this.miniAppUser.getUserId().getEmail()), HttpMethod.DELETE, null, UserBoundary[].class);
            // THEN the server responds with status 401 UNAUTHORIZED
            // AND the response body is empty
            assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + responseEntity.getStatusCode() +
                    " instead ", HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        });
        assertEquals("Expected to get " + HttpStatus.UNAUTHORIZED + " but got " + exception.getStatusCode() +
                " instead ", HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }


    /**
     * Compares NewUserBoundary and UserBoundary objects to determine if they are equal.
     *
     * @param expectedObj the expected object.
     * @param returnObj   the returned object.
     * @return true if the objects are equal, false otherwise.
     */
    public boolean compareResponseToRequest(UserBoundary expectedObj, UserBoundary returnObj) {
        return expectedObj.getUserId().getEmail().equals(returnObj.getUserId().getEmail()) &&
                expectedObj.getRole().equals(returnObj.getRole()) &&
                expectedObj.getUsername().equals(returnObj.getUsername()) &&
                expectedObj.getAvatar().equals(returnObj.getAvatar());
    }
}
