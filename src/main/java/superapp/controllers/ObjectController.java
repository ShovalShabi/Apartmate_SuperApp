package superapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.boundaries.object.SuperAppObjectIdBoundary;
import superapp.logic.ObjectsServiceAdvanced;
import java.util.List;

/**
 * The SuperAppObjectController class handles all requests related to SuperApp objects. This includes creating new objects,
 * updating existing objects, and retrieving information about objects. Objects in the SuperApp are represented as instances
 * of the ObjectBoundary class.
 */
@RestController
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
            @RequestBody SuperAppObjectBoundary objectToUpdate) {
        SuperAppObjectBoundary superAppObjectBoundary = this.objectsServiceAdvanced.updateObject(superapp, internalObjectId, objectToUpdate);
        System.err.println("The object change to :\n" + superAppObjectBoundary);
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
            @PathVariable("internalObjectId") String internalObjectId) {
        return this.objectsServiceAdvanced.getSpecificObject(superapp, internalObjectId);
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
    public List<SuperAppObjectBoundary> getAllObjects() {
        return this.objectsServiceAdvanced.getAllObjects();
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
        @RequestBody SuperAppObjectIdBoundary childObject) {
        this.objectsServiceAdvanced.bindChildObject(superapp, internalObjectId, childObject);
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
            @PathVariable("internalObjectId") String internalObjectId) {
            return this.objectsServiceAdvanced.getAllObjectChildren(
                    superapp, internalObjectId).toArray(new SuperAppObjectBoundary[0]);
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
            @PathVariable("internalObjectId") String internalObjectId) {
        return this.objectsServiceAdvanced.getAllObjectParents(
                superapp, internalObjectId).toArray(new SuperAppObjectBoundary[0]);
    }
}
