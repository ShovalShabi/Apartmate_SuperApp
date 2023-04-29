package superapp.logic;

import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.boundaries.object.SuperAppObjectIdBoundary;

import java.util.List;

public interface ObjectsServiceAdvanced extends ObjectsService {
    void bindChildObject(String objectSuperApp, String internalObjectId ,SuperAppObjectIdBoundary childObject);

    List<SuperAppObjectBoundary> getAllObjectChildren(String objectSuperApp, String internalObjectId);

    List<SuperAppObjectBoundary> getAllObjectParents(String objectSuperApp, String internalObjectId);

}
