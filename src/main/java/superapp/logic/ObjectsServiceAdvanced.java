package superapp.logic;

import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.boundaries.object.SuperAppObjectIdBoundary;

import java.util.List;

/**
 * The ObjectsServiceAdvanced interface extends the ObjectsService interface and provides additional advanced functionality
 * for managing objects.
 */
public interface ObjectsServiceAdvanced extends ObjectsService {
    /**
     * Binds a child object to a parent object.
     *
     * @param objectSuperApp   The SuperApp of the parent object.
     * @param internalObjectId The internal ID of the parent object.
     * @param childObject      The child object to bind.
     */
    void bindChildObject(String objectSuperApp, String internalObjectId, SuperAppObjectIdBoundary childObject);

    /**
     * Retrieves a list of children for a specific SuperApp object.
     *
     * @param objectSuperApp   The SuperApp associated with the object.
     * @param internalObjectId The internal ID of the object.
     * @return A list of SuperAppObjectBoundary objects representing the children of the specified object.
     */
    List<SuperAppObjectBoundary> getAllObjectChildren(String objectSuperApp, String internalObjectId);

    /**
     * Retrieves a list of parents for a specific SuperApp object.
     *
     * @param objectSuperApp   The SuperApp associated with the object.
     * @param internalObjectId The internal ID of the object.
     * @return A list of SuperAppObjectBoundary objects representing the parents of the specified object.
     */
    List<SuperAppObjectBoundary> getAllObjectParents(String objectSuperApp, String internalObjectId);

    /**
     * Retrieves a list of SuperAppObjectBoundary objects based on the specified type.
     *
     * @param type         The type of objects to retrieve.
     * @param userSuperApp The SuperApp of the user.
     * @param userEmail    The email of the user.
     * @param size         The maximum number of objects to retrieve per page.
     * @param page         The page number to retrieve (0-based index).
     * @return A list of SuperAppObjectBoundary objects that match the specified type.
     */
    List<SuperAppObjectBoundary> getObjectsByType(String type, String userSuperApp, String userEmail, int size, int page);

    /**
     * Retrieves a list of SuperAppObjectBoundary objects based on the specified alias.
     *
     * @param alias        The alias of the objects to retrieve.
     * @param userSuperApp The SuperApp of the user.
     * @param userEmail    The email of the user.
     * @param size         The maximum number of objects to retrieve per page.
     * @param page         The page number to retrieve (0-based index).
     * @return A list of SuperAppObjectBoundary objects that match the specified alias.
     */
    List<SuperAppObjectBoundary> getObjectsByAlias(String alias, String userSuperApp, String userEmail, int size, int page);

    /**
     * Retrieves a list of SuperAppObjectBoundary objects based on the specified location.
     *
     * @param lat           The latitude of the location.
     * @param lng           The longitude of the location.
     * @param distance      The maximum distance from the location to retrieve objects.
     * @param distanceUnits The units of the distance (e.g., "km", "mi").
     * @param userSuperApp  The SuperApp of the user.
     * @param userEmail     The email of the user.
     * @param size          The maximum number of objects to retrieve per page.
     * @param page          The page number to retrieve (0-based index).
     * @return A list of SuperAppObjectBoundary objects within the specified distance of the location.
     */
    List<SuperAppObjectBoundary> getObjectsByLocation(Double lat, Double lng, Double distance, String distanceUnits,
                                                      String userSuperApp, String userEmail, int size, int page);

    /**
     * Updates an existing SuperApp object.
     *
     * @param objectSuperApp   the SuperApp associated with the object
     * @param internalObjectId the internal ID of the object
     * @param update           the updated SuperApp object
     * @param userSuperApp     the SuperApp associated with the user
     * @param userEmail        the email of the user
     * @return the updated SuperApp object
     */
    SuperAppObjectBoundary updateObject(String objectSuperApp, String internalObjectId, SuperAppObjectBoundary update, String userSuperApp, String userEmail);

    /**
     * Retrieves a specific SuperApp object.
     *
     * @param objectSuperApp   the SuperApp associated with the object
     * @param internalObjectId the internal ID of the object
     * @param userSuperApp     the SuperApp associated with the user
     * @param userEmail        the email of the user
     * @return the retrieved SuperApp object
     */
    SuperAppObjectBoundary getSpecificObject(String objectSuperApp, String internalObjectId, String userSuperApp, String userEmail);

    /**
     * Retrieves a list of all SuperApp objects for a user.
     *
     * @param userSuperApp the SuperApp associated with the user
     * @param userEmail    the email of the user
     * @param size         the number of objects to retrieve per page
     * @param page         the page number
     * @return a list of SuperApp objects
     */
    List<SuperAppObjectBoundary> getAllObjects(String userSuperApp, String userEmail, int size, int page);

    /**
     * Deletes all SuperApp objects.
     */
    void deleteAllObjects(String userSuperApp, String userEmail);

    /**
     * Binds a child object to a parent object.
     *
     * @param objectSuperApp   The SuperApp of the parent object.
     * @param internalObjectId The internal ID of the parent object.
     * @param childObject      The child object to bind.
     * @param userSuperApp     The SuperApp of the user.
     * @param userEmail        The email of the user.
     */
    void bindChildObject(String objectSuperApp, String internalObjectId, SuperAppObjectIdBoundary childObject,
                         String userSuperApp, String userEmail);

    /**
     * Retrieves a list of children for a specific SuperApp object.
     *
     * @param objectSuperApp   The SuperApp associated with the object.
     * @param internalObjectId The internal ID of the object.
     * @param userSuperApp     The SuperApp associated with the user.
     * @param userEmail        The email of the user.
     * @param size             The maximum number of results to retrieve.
     * @param page             The page number of the results to retrieve.
     * @return A list of SuperAppObjectBoundary objects representing the children of the specified object.
     */
    List<SuperAppObjectBoundary> getAllObjectChildren(String objectSuperApp, String internalObjectId,
                                                      String userSuperApp, String userEmail, int size, int page);

    /**
     * Retrieves a list of parents for a specific SuperApp object.
     *
     * @param objectSuperApp   The SuperApp associated with the object.
     * @param internalObjectId The internal ID of the object.
     * @param userSuperApp     The SuperApp associated with the user.
     * @param userEmail        The email of the user.
     * @param size             The maximum number of results to retrieve.
     * @param page             The page number of the results to retrieve.
     * @return A list of SuperAppObjectBoundary objects representing the parents of the specified object.
     */
    List<SuperAppObjectBoundary> getAllObjectParents(String objectSuperApp, String internalObjectId,
                                                     String userSuperApp, String userEmail, int size, int page);

}
