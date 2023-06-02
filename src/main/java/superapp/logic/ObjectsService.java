package superapp.logic;

import superapp.boundaries.object.SuperAppObjectBoundary;

import java.util.List;

/**
 * The ObjectsService interface provides methods for managing SuperApp objects.
 */
public interface ObjectsService {
    /**
     * Creates a new SuperApp object.
     *
     * @param object the SuperApp object to create
     * @return the created SuperApp object
     */
    SuperAppObjectBoundary createObject(SuperAppObjectBoundary object);

    /**
     * Updates an existing SuperApp object.
     *
     * @param objectSuperApp   the SuperApp associated with the object
     * @param internalObjectId the internal ID of the object
     * @param update           the updated SuperApp object
     * @return the updated SuperApp object
     */
    SuperAppObjectBoundary updateObject(String objectSuperApp, String internalObjectId, SuperAppObjectBoundary update);

    /**
     * Retrieves a specific SuperApp object.
     *
     * @param objectSuperApp   the SuperApp associated with the object
     * @param internalObjectId the internal ID of the object
     * @return the retrieved SuperApp object
     */
    SuperAppObjectBoundary getSpecificObject(String objectSuperApp, String internalObjectId);

    /**
     * Retrieves a list of all SuperApp objects for a user.
     *
     * @return a list of SuperApp objects
     */
    List<SuperAppObjectBoundary> getAllObjects();

    /**
     * Deletes all SuperApp objects.
     */
    void deleteAllObjects();
}
