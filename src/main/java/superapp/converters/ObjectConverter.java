package superapp.converters;

import org.springframework.stereotype.Component;
import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.boundaries.object.SuperAppObjectIdBoundary;
import superapp.data.SuperAppObjectEntity;
import superapp.utils.Invokers.UserIdInvoker;
import superapp.utils.Location;

@Component
public class ObjectConverter {

    /**
     * The ObjectConverter class is responsible for converting between SuperAppObjectBoundary objects
     * and SuperAppObjectEntity objects.
     */
    public ObjectConverter() {}

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
        location.setLat(entity.getLat());
        location.setLng(entity.getLng());
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
        superAppObjectEntity.setLat(object.getLocation().getLat());
        superAppObjectEntity.setLng(object.getLocation().getLng());
        superAppObjectEntity.setUserIdInvoker(object.getCreatedBy());
        superAppObjectEntity.setObjectDetails(object.getObjectDetails());
        return superAppObjectEntity;
    }

    public String createID(SuperAppObjectIdBoundary superAppObjectIdBoundary) {
        String idFormat = "%s$%s";
        return idFormat.formatted(
                superAppObjectIdBoundary.getSuperapp(),
                superAppObjectIdBoundary.getInternalObjectId()
        );
    }

    public String createID(String objectSuperAppBoundary, String internalObjectId) {
        String idFormat = "%s$%s";
        return idFormat.formatted(
                objectSuperAppBoundary,
                internalObjectId
        );
    }

}
