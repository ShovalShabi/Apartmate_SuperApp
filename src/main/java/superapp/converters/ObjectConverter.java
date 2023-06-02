package superapp.converters;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.boundaries.object.SuperAppObjectIdBoundary;
import superapp.data.SuperAppObjectEntity;
import superapp.utils.Invokers.UserIdInvoker;
import superapp.utils.Location;

import java.util.Map;

/**
 * The ObjectConverter class is responsible for converting between SuperAppObjectBoundary objects and SuperAppObjectEntity objects.
 */
@Component
public class ObjectConverter {

    private final ObjectMapper mapper;

    /**
     * The ObjectConverter class is responsible for converting between SuperAppObjectBoundary objects
     * and SuperAppObjectEntity objects.
     */
    public ObjectConverter() { this.mapper = new ObjectMapper(); }

    /**
     * Converts a SuperAppObjectEntity to a SuperAppObjectBoundary.
     *
     * @param entity the SuperAppObjectEntity to convert
     * @return a SuperAppObjectBoundary object
     */
    public SuperAppObjectBoundary toBoundary(SuperAppObjectEntity entity) {
        SuperAppObjectBoundary superAppObjectBoundary = new SuperAppObjectBoundary();

        String[] ids = entity.getId().split("\\$");
        superAppObjectBoundary.setObjectId(new SuperAppObjectIdBoundary(ids[0], ids[1]));
        superAppObjectBoundary.setType(entity.getType());
        superAppObjectBoundary.setAlias(entity.getAlias());
        superAppObjectBoundary.setActive(entity.getActive());
        superAppObjectBoundary.setCreationTimestamp(entity.getCreationTimeStamp());

        Location location = new Location();
        location.setLat(entity.getLocation().getX());
        location.setLng(entity.getLocation().getY());
        superAppObjectBoundary.setLocation(location);

        UserIdInvoker userIdInvoker = new UserIdInvoker();
        userIdInvoker.setUserId(entity.getUserIdInvoker().getUserId());
        superAppObjectBoundary.setCreatedBy(userIdInvoker);

        superAppObjectBoundary.setObjectDetails(entity.getObjectDetails());
        return superAppObjectBoundary;
    }

    /**
     * Converts a SuperAppObjectBoundary to a SuperAppObjectEntity.
     *
     * @param object the SuperAppObjectBoundary to convert
     * @return a SuperAppObjectEntity object
     */
    public SuperAppObjectEntity toEntity(SuperAppObjectBoundary object) {
        SuperAppObjectEntity superAppObjectEntity = new SuperAppObjectEntity();
        superAppObjectEntity.setId(createID(object.getObjectId()));
        superAppObjectEntity.setType(object.getType());
        superAppObjectEntity.setAlias(object.getAlias());
        superAppObjectEntity.setActive(object.getActive());
        superAppObjectEntity.setCreationTimeStamp(object.getCreationTimestamp());
        superAppObjectEntity.setLocation(new GeoJsonPoint(object.getLocation().getLat(), object.getLocation().getLng()));
        superAppObjectEntity.setUserIdInvoker(object.getCreatedBy());
        superAppObjectEntity.setObjectDetails(object.getObjectDetails());
        return superAppObjectEntity;
    }

    /**
     * Creates an ID for a SuperAppObjectIdBoundary based on its components.
     *
     * @param superAppObjectIdBoundary the SuperAppObjectIdBoundary from which to create the ID
     * @return a String representing the created ID
     */
    public String createID(SuperAppObjectIdBoundary superAppObjectIdBoundary) {
        String idFormat = "%s$%s";
        return idFormat.formatted(
                superAppObjectIdBoundary.getSuperapp(),
                superAppObjectIdBoundary.getInternalObjectId()
        );
    }

    /**
     * Creates an ID for a SuperAppObjectBoundary based on the provided components.
     *
     * @param objectSuperAppBoundary the SuperApp boundary component of the ID
     * @param internalObjectId the internal object ID component of the ID
     * @return a String representing the created ID
     */
    public String createID(String objectSuperAppBoundary, String internalObjectId) {
        String idFormat = "%s$%s";
        return idFormat.formatted(
                objectSuperAppBoundary,
                internalObjectId
        );
    }


    /**
     * Retrieves an ID for a SuperAppObjectBoundary based on the provided components.
     *
     * @param id the internal object ID constructed with the superapp identifier component of the ID
     * @return a String representing the created UUID
     */
    public String retrieveInternalObjectID(String id) {
        String[] arr = id.split("\\$");
        return arr[1]; // The internal object ID

    }

}
