package superapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.boundaries.object.SuperAppObjectIdBoundary;
import superapp.logic.ObjectsServiceAdvanced;

import java.util.List;

import static superapp.utils.Constants.*;

/**
 * The SuperAppObjectController class handles all requests related to SuperApp objects. This includes creating new objects,
 * updating existing objects, and retrieving information about objects. Objects in the SuperApp are represented as instances
 * of the ObjectBoundary class.
 */
@RestController
@CrossOrigin
public class ObjectController {

    private final ObjectsServiceAdvanced objectsServiceAdvanced;  //The service of the super app object boundaries

    /**
     * Creates an object boundary with the specified parameters.
     *
     * @param objectsServiceAdvanced the service of the super app object boundaries
     */
    @Autowired
    public ObjectController(ObjectsServiceAdvanced objectsServiceAdvanced) {
        this.objectsServiceAdvanced = objectsServiceAdvanced;
    }

    /**
     * This method creates a new object in the system.
     *
     * @param newObject The object to create.
     * @return The created object.
     */
    @RequestMapping(
            path = {"/superapp/objects"},
            method = {RequestMethod.POST},
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    public SuperAppObjectBoundary createObject(@RequestBody SuperAppObjectBoundary newObject) {
        return this.objectsServiceAdvanced.createObject(newObject);
    }

    /**
     * This method updates an existing object in the system.
     *
     * @param superapp         The superapp the object belongs to.
     * @param internalObjectId The internal ID of the object.
     * @param objectToUpdate   The updated object.
     */
    @RequestMapping(
            path = {"/superapp/objects/{superapp}/{internalObjectId}"},
            method = {RequestMethod.PUT},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    void updateObject(
            @PathVariable("superapp") String superapp,
            @PathVariable("internalObjectId") String internalObjectId,
            @RequestParam(name = "userSuperapp", required = true, defaultValue = "") String userSuperapp,
            @RequestParam(name = "userEmail", required = true, defaultValue = "") String userEmail,
            @RequestBody SuperAppObjectBoundary objectToUpdate) {
        this.objectsServiceAdvanced.updateObject(superapp, internalObjectId, objectToUpdate, userSuperapp, userEmail);
    }

    /**
     * This method retrieves an object from the system.
     *
     * @param superapp         The superapp the object belongs to.
     * @param internalObjectId The internal ID of the object.
     * @return The retrieved object.
     */
    @RequestMapping(
            path = {"/superapp/objects/{superapp}/{internalObjectId}"},
            method = {RequestMethod.GET},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public SuperAppObjectBoundary retrieveObject(
            @PathVariable("superapp") String superapp,
            @PathVariable("internalObjectId") String internalObjectId,
            @RequestParam(name = "userSuperapp", required = true, defaultValue = "") String userSuperapp,
            @RequestParam(name = "userEmail", required = true, defaultValue = "") String userEmail) {
        return this.objectsServiceAdvanced.getSpecificObject(superapp, internalObjectId, userSuperapp, userEmail);
    }

    /**
     * This method retrieves all objects in the system.
     *
     * @return A list of all objects.
     */
    @RequestMapping(
            path = {"/superapp/objects"},
            method = {RequestMethod.GET},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<SuperAppObjectBoundary> getAllObjects(
            @RequestParam(name = "userSuperapp", required = true, defaultValue = "") String userSuperapp,
            @RequestParam(name = "userEmail", required = true, defaultValue = "") String userEmail,
            @RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE) int page) {
        return this.objectsServiceAdvanced.getAllObjects(userSuperapp, userEmail, size, page);
    }

    /**
     * Binds a new child object to an existing parent object.
     *
     * @param superapp         The super app of the parent object.
     * @param internalObjectId The internal object ID of the parent object.
     * @param childObject      The child object to be bound.
     */
    @RequestMapping(
            path = {"/superapp/objects/{superapp}/{internalObjectId}/children"},
            method = {RequestMethod.PUT},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public void bindNewChild(
            @PathVariable("superapp") String superapp,
            @PathVariable("internalObjectId") String internalObjectId,
            @RequestParam(name = "userSuperapp", required = true, defaultValue = "") String userSuperapp,
            @RequestParam(name = "userEmail", required = true, defaultValue = "") String userEmail,
            @RequestBody SuperAppObjectIdBoundary childObject) {
        this.objectsServiceAdvanced.bindChildObject(superapp, internalObjectId, childObject, userSuperapp, userEmail);
    }

    /**
     * Gets all the children of an existing parent object.
     *
     * @param superapp         The super app of the parent object.
     * @param internalObjectId The internal object ID of the parent object.
     * @return An array of all the children of the parent object.
     */
    @RequestMapping(
            path = {"/superapp/objects/{superapp}/{internalObjectId}/children"},
            method = {RequestMethod.GET},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public SuperAppObjectBoundary[] getAllObjectChildren(
            @PathVariable("superapp") String superapp,
            @PathVariable("internalObjectId") String internalObjectId,
            @RequestParam(name = "userSuperapp", required = true, defaultValue = "") String userSuperapp,
            @RequestParam(name = "userEmail", required = true, defaultValue = "") String userEmail,
            @RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE) int page) {
        return this.objectsServiceAdvanced.getAllObjectChildren(
                superapp, internalObjectId, userSuperapp, userEmail, size, page).toArray(new SuperAppObjectBoundary[0]);
    }

    /**
     * Gets all the parents of an existing child object.
     *
     * @param superapp         The super app of the child object.
     * @param internalObjectId The internal object ID of the child object.
     * @return An array of all the parents of the child object.
     */
    @RequestMapping(
            path = {"/superapp/objects/{superapp}/{internalObjectId}/parents"},
            method = {RequestMethod.GET},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public SuperAppObjectBoundary[] getAllObjectParents(
            @PathVariable("superapp") String superapp,
            @PathVariable("internalObjectId") String internalObjectId,
            @RequestParam(name = "userSuperapp", required = true, defaultValue = "") String userSuperapp,
            @RequestParam(name = "userEmail", required = true, defaultValue = "") String userEmail,
            @RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE) int page) {
        return this.objectsServiceAdvanced.getAllObjectParents(
                superapp, internalObjectId, userSuperapp, userEmail, size, page).toArray(new SuperAppObjectBoundary[0]);
    }

    /**
     * Retrieves SuperAppObjectBoundary objects by type.
     *
     * @param type         the type of objects to retrieve
     * @param userSuperapp the superapp associated with the user
     * @param userEmail    the email of the user
     * @param size         the number of objects to retrieve per page
     * @param page         the page number to retrieve
     * @return an array of SuperAppObjectBoundary objects matching the specified type
     */
    @RequestMapping(
            path = {"/superapp/objects/search/byType/{type}"},
            method = {RequestMethod.GET},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public SuperAppObjectBoundary[] getObjectsByType(
            @PathVariable("type") String type,
            @RequestParam(name = "userSuperapp", required = true, defaultValue = "") String userSuperapp,
            @RequestParam(name = "userEmail", required = true, defaultValue = "") String userEmail,
            @RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE) int page) {
        return this.objectsServiceAdvanced.getObjectsByType(type, userSuperapp, userEmail, size, page)
                .toArray(new SuperAppObjectBoundary[0]);
    }

    /**
     * Retrieves SuperAppObjectBoundary objects by alias.
     *
     * @param alias        the alias of the objects to retrieve
     * @param userSuperapp the superapp associated with the user
     * @param userEmail    the email of the user
     * @param size         the number of objects to retrieve per page
     * @param page         the page number to retrieve
     * @return an array of SuperAppObjectBoundary objects matching the specified alias
     */
    @RequestMapping(
            path = {"/superapp/objects/search/byAlias/{alias}"},
            method = {RequestMethod.GET},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public SuperAppObjectBoundary[] getObjectsByAlias(
            @PathVariable("alias") String alias,
            @RequestParam(name = "userSuperapp", required = true, defaultValue = "") String userSuperapp,
            @RequestParam(name = "userEmail", required = true, defaultValue = "") String userEmail,
            @RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE) int page) {
        return this.objectsServiceAdvanced.getObjectsByAlias(alias, userSuperapp, userEmail, size, page)
                .toArray(new SuperAppObjectBoundary[0]);
    }

    /**
     * Retrieves SuperAppObjectBoundary objects by location.
     *
     * @param lat           the latitude of the location
     * @param lng           the longitude of the location
     * @param distance      the distance from the location
     * @param distanceUnits the unit of distance measurement, when the user does not specify the measuring unit,
     *                      the default is KM otherwise it will be MILES
     * @param userSuperapp  the superapp associated with the user
     * @param userEmail     the email of the user
     * @param size          the number of objects to retrieve per page
     * @param page          the page number to retrieve
     * @return an array of SuperAppObjectBoundary objects within the specified distance of the location
     */
    @RequestMapping(
            path = {"/superapp/objects/search/byLocation/{lat}/{lng}/{distance}"},
            method = {RequestMethod.GET},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public SuperAppObjectBoundary[] getObjectsByLocation(
            @PathVariable("lat") Double lat,
            @PathVariable("lng") Double lng,
            @PathVariable("distance") Double distance,
            @RequestParam(name = "units", required = false, defaultValue = KM_DISTANCE_TYPE) String distanceUnits,
            @RequestParam(name = "userSuperapp", required = true, defaultValue = "") String userSuperapp,
            @RequestParam(name = "userEmail", required = true, defaultValue = "") String userEmail,
            @RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE) int page) {
        return this.objectsServiceAdvanced.getObjectsByLocation(lat, lng, distance, distanceUnits, userSuperapp, userEmail, size, page)
                .toArray(new SuperAppObjectBoundary[0]);
    }
}
