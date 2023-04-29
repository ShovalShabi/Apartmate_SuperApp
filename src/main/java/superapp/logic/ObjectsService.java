package superapp.logic;

import superapp.boundaries.object.SuperAppObjectBoundary;

import java.util.List;

public interface ObjectsService {

    SuperAppObjectBoundary createObject(SuperAppObjectBoundary object);

    SuperAppObjectBoundary updateObject(String objectSuperApp, String internalObjectId, SuperAppObjectBoundary update);

    SuperAppObjectBoundary getSpecificObject(String objectSuperApp, String internalObjectId);

    List<SuperAppObjectBoundary> getAllObjects();

    void  deleteAllObjects();

}
