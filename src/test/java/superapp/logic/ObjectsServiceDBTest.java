package superapp.logic;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;
import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.boundaries.user.UserIdBoundary;
import superapp.utils.Invokers.UserIdInvoker;
import superapp.utils.Location;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Predicate;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ObjectsServiceDBTest {
    private String baseUrl;
    private String deleteUrl;
    private RestTemplate restTemplate;
    private final String parentJsonFileName = "src/test/java/SuperAppObjectBoundaryTemplate1.json";
    private final String childJsonFileName = "src/test/java/SuperAppObjectBoundaryTemplate2.json";

    @LocalServerPort
    public void setup(int port) {
        this.baseUrl = "http://localhost:" + port + "/superapp/objects";
        this.deleteUrl = "http://localhost:" + port + "/superapp/admin/objects";
        this.restTemplate = new RestTemplate();
    }


    @AfterEach
    public void teardown() {
        this.restTemplate.delete(this.deleteUrl);
    }


    /**
     * Test creation of  a new SuperAppObjectBoundary object.
     *
     * @throws Exception if the SuperAppObjectBoundary cannot be found by the given ID
     */
    @Test
    public void createObjectTest() throws Exception {
        // GIVEN the server is up
        // AND the database is empty

        // WHEN I POST /superapp/objects
        // AND the request body has SuperAppObjectBoundary as JSON format with no objectId
        SuperAppObjectBoundary testObj = this.createDummy(childJsonFileName);

        SuperAppObjectBoundary returnObj = this.restTemplate.postForObject(this.baseUrl, testObj, SuperAppObjectBoundary.class);

        // THEN the server responds with status 2xx
        // AND the response body is the same SuperAppObjectBoundary that sent to the ObjectsServiceDB but now with objectId
        if (returnObj == null || returnObj.getObjectId() == null ||
                returnObj.getObjectId().getInternalObjectId() == null ||
                returnObj.getObjectId().getSuperapp() == null) {
            throw new Exception("Invalid data from server, expected an existent object, but received field with null instead");
        }

        if (returnObj.getCreationTimestamp() == null)
            throw new Exception("Invalid data from server, the return object should hold creation time stamp ");

        if (this.compareResponseToRequest(testObj, returnObj))
            throw new Exception("Some fields except objectId are different.\n" +
                    "expected the object: " + testObj + "\nbut instead got the object " + returnObj);

    }

    /**
     * Test an update of existing SuperAppObjectBoundary object.
     *
     * @throws Exception if the SuperAppObjectBoundary cannot be found by the given ID
     */
    @Test
    public void updateObjectTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has specific SuperAppObjectBoundary within it
        SuperAppObjectBoundary testObj = this.injectObjectToDB(this.childJsonFileName);


        // WHEN I PUT /superapp/objects/{superapp}/{internalObjectId}
        // AND the request body has SuperAppObjectBoundary as JSON format with different fields
        testObj.setActive(false);

        this.restTemplate.put(String.format(this.baseUrl + "/%s/%s", testObj.getObjectId().getSuperapp(),
                testObj.getObjectId().getInternalObjectId()), testObj);

        // THEN the server responds with status 2xx
        // AND the response body is empty
        // AND I GET /superapp/objects/{superapp}/{internalObjectId} for identity validation, the objects should be the same fields

        SuperAppObjectBoundary returnObj = this.restTemplate.getForObject(
                String.format(this.baseUrl + "/%s/%s", testObj.getObjectId().getSuperapp(),
                        testObj.getObjectId().getInternalObjectId()), SuperAppObjectBoundary.class);

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
     * Test a retrieve of a specific SuperAppObject from the database with the given objectSuperApp and internalObjectId.
     *
     * @throws Exception if no matching object is found
     */
    @Test
    public void getSpecificObjectTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has specific SuperAppObjectBoundary within it
        SuperAppObjectBoundary testObj = this.injectObjectToDB(this.childJsonFileName);


        // WHEN I GET /superapp/objects/{superapp}/{internalObjectId}
        // AND there is no request body
        // AND the injected object hasn't been changed in the meanwhile

        SuperAppObjectBoundary returnObj = this.restTemplate.getForObject(
                String.format(this.baseUrl + "/%s/%s", testObj.getObjectId().getSuperapp(),
                        testObj.getObjectId().getInternalObjectId()), SuperAppObjectBoundary.class);

        // THEN the server responds with status 2xx
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
     * Test a retrieve of all SuperAppObjects from the database.
     *
     * @throws Exception if no matching object is found
     */
    @Test
    public void getAllObjectsTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has several SuperAppObjectBoundary within it
        this.injectObjectToDB(childJsonFileName);


        // WHEN I GET /superapp/objects/
        // AND there is no request body

        SuperAppObjectBoundary[] superAppObjectBoundaries = this.restTemplate.getForObject(this.baseUrl, SuperAppObjectBoundary[].class);

        // THEN the server responds with status 2xx
        // AND the response body is an array of SuperAppObjectBoundaries that is not empty

        if (superAppObjectBoundaries == null || superAppObjectBoundaries.length == 0) {
            throw new Exception("Invalid data from server, expected an existent array of SuperAppObjectBoundaries with overall array length greater than 0");
        }

    }

    /**
     * Test deletion of all SuperAppObjects from the database.
     *
     * @throws Exception if no matching object is found
     */
    @Test
    public void deleteAllObjectsTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has several SuperAppObjectBoundary within it
        this.injectObjectToDB(this.childJsonFileName);


        // WHEN I DELETE /superapp/objects/
        // AND there is no request body
        this.restTemplate.delete(this.deleteUrl);

        // THEN the server responds with status 2xx
        // AND the response body is empty
        // AND when I GET /superapp/objects/ I'll receive an empty array
        SuperAppObjectBoundary[] superAppObjectBoundaries = this.restTemplate.getForObject(this.baseUrl, SuperAppObjectBoundary[].class);

        if (superAppObjectBoundaries == null) {
            throw new Exception("Invalid data from server, expected an existent array of SuperAppObjectBoundaries with overall array length equal to 0 but got null instead");
        }

        if (superAppObjectBoundaries.length > 0) {
            throw new Exception("Invalid data from server, expected an existent array of SuperAppObjectBoundaries with overall array equal to 0 but got non empty array");
        }
    }

    /**
     * Test a binding of a child object to a parent object.
     * Both parent and child must exist and be different entities.
     *
     * @throws Exception if something goes wrong while saving the objects.
     */
    @Test
    public void bindChildObjectTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has existing SuperAppObjectBoundary parent and target children within it
        SuperAppObjectBoundary parentObj = this.injectObjectToDB(this.parentJsonFileName);
        SuperAppObjectBoundary childObj = this.injectObjectToDB(this.childJsonFileName);


        // WHEN I PUT /superapp/objects/{superapp}/{internalObjectId}/children
        // AND the request body has SuperAppObjectIdBoundary as JSON format

        this.restTemplate.put(String.format(this.baseUrl + "/%s/%s/children", parentObj.getObjectId().getSuperapp(),
                parentObj.getObjectId().getInternalObjectId()), childObj.getObjectId());

        // THEN the server responds with status 2xx
        // AND the response body is empty
        // AND I GET /superapp/objects/{superapp}/{internalObjectId} for relationship validation, the array should represent all the children objects of the parent object

        SuperAppObjectBoundary[] childrenArr = this.restTemplate.getForObject(
                String.format(this.baseUrl + "/%s/%s/children", parentObj.getObjectId().getSuperapp(),
                        parentObj.getObjectId().getInternalObjectId()), SuperAppObjectBoundary[].class);

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
     * Test a fetch of all the children of a parent object.
     *
     * @throws Exception if the parent object does not exist.
     */
    @Test
    public void getAllObjectChildrenTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has existing SuperAppObjectBoundary parent and some children within it
        // AND I PUT /superapp/objects/{superapp}/{internalObjectId} for relationship setup
        SuperAppObjectBoundary parentObj = this.injectObjectToDB(this.parentJsonFileName);
        SuperAppObjectBoundary childObj = this.injectObjectToDB(this.childJsonFileName);
        this.restTemplate.put(String.format(this.baseUrl + "/%s/%s/children", parentObj.getObjectId().getSuperapp(),
                parentObj.getObjectId().getInternalObjectId()), childObj.getObjectId());

        // WHEN I GET /superapp/objects/{superapp}/{internalObjectId}/children
        // AND the request body is empty
        // THEN the server responds with status 2xx
        // AND the response body is an array of SuperAppObjectsFormatted as JSON
        SuperAppObjectBoundary[] objectBoundaries = this.restTemplate.getForObject(
                String.format(this.baseUrl + "/%s/%s/children", parentObj.getObjectId().getSuperapp(),
                        parentObj.getObjectId().getInternalObjectId()), SuperAppObjectBoundary[].class);

        if (objectBoundaries == null || objectBoundaries.length == 0) {
            throw new Exception("Invalid data from server, expected an existent array of SuperAppObjectBoundaries with overall array length greater than 0");
        }
    }

    /**
     * Test a fetch of all the parents of a child object.
     *
     * @throws Exception if the child object does not exist.
     */
    @Test
    public void getAllObjectParentsTest() throws Exception {
        // GIVEN the server is up
        // AND the database is not empty and has existing SuperAppObjectBoundary parent and some children within it
        // AND I PUT /superapp/objects/{superapp}/{internalObjectId} for relationship setup
        SuperAppObjectBoundary parentObj = this.injectObjectToDB(this.parentJsonFileName);
        SuperAppObjectBoundary childObj = this.injectObjectToDB(this.childJsonFileName);
        this.restTemplate.put(String.format(this.baseUrl + "/%s/%s/children", parentObj.getObjectId().getSuperapp(),
                parentObj.getObjectId().getInternalObjectId()), childObj.getObjectId());

        // WHEN I GET /superapp/objects/{superapp}/{internalObjectId}/children
        // AND the request body is empty
        // THEN the server responds with status 2xx
        // AND the response body is an array of SuperAppObjectsFormatted as JSON
        SuperAppObjectBoundary[] objectBoundaries = this.restTemplate.getForObject(
                String.format(this.baseUrl + "/%s/%s/parents", childObj.getObjectId().getSuperapp(),
                        childObj.getObjectId().getInternalObjectId()), SuperAppObjectBoundary[].class);

        if (objectBoundaries == null || objectBoundaries.length == 0) {
            throw new Exception("Invalid data from server, expected an existent array of SuperAppObjectBoundaries with overall array length greater than 0");
        }
    }


    /**
     * Injects a JSON object into the database.
     *
     * @param jsonFileName the name of the JSON file containing the object to be injected.
     * @return the injected object as a SuperAppObjectBoundary.
     * @throws Exception if an error occurs while injecting the object or the response from the server is invalid.
     */
    public SuperAppObjectBoundary injectObjectToDB(String jsonFileName) throws Exception {
        // Creates a new SuperAppObjectBoundary object using the createDummy() method
        SuperAppObjectBoundary testObj = this.createDummy(jsonFileName);

        // Posts the object to the database using the REST template
        testObj = this.restTemplate.postForObject(this.baseUrl, testObj, SuperAppObjectBoundary.class);

        // Throws an exception if the returned object is null
        if (testObj == null) {
            throw new Exception("Invalid data from server. Expected an existent object, but received field with null instead.");
        }

        // Returns the injected object
        return testObj;
    }

    /**
     * Creates a new SuperAppObjectBoundary object using a JSON file.
     *
     * @param jsonFileName the name of the JSON file containing the object data.
     * @return a new SuperAppObjectBoundary object.
     * @throws FileNotFoundException if the specified file does not exist.
     */
    public SuperAppObjectBoundary createDummy(String jsonFileName) throws FileNotFoundException {
        // Reads the JSON file
        FileReader reader = new FileReader(jsonFileName);

        // Parses the JSON file to a JSONTokener
        JSONTokener tokener = new JSONTokener(reader);

        // Converts the JSONTokener to a JSONObject
        JSONObject jsonObject = new JSONObject(tokener);

        // Creates a new UserIdBoundary object
        UserIdBoundary userIdBoundary = new UserIdBoundary(
                jsonObject.getJSONObject("createdBy").getJSONObject("userId").getString("superapp"),
                jsonObject.getJSONObject("createdBy").getJSONObject("userId").getString("email"));

        // Creates a new UserIdInvoker object
        UserIdInvoker createdBy = new UserIdInvoker(userIdBoundary);

        // Creates a new Location object
        Location location = new Location(
                jsonObject.getJSONObject("location").getDouble("lat"),
                jsonObject.getJSONObject("location").getDouble("lng"));

        // Creates a new TreeMap object for objectDetails
        Map<String, Object> objectDetails = new TreeMap<>(jsonObject.getJSONObject("objectDetails").toMap());

        // Returns the new SuperAppObjectBoundary object
        return new SuperAppObjectBoundary(
                null,
                jsonObject.getString("type"),
                jsonObject.getString("alias"),
                jsonObject.getBoolean("active"),
                null,
                location,
                createdBy,
                objectDetails);
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
                expectedObj.getObjectDetails().equals(returnObj.getObjectDetails());
    }

}
