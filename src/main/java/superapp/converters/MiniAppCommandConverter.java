package superapp.converters;

import org.springframework.stereotype.Component;
import superapp.boundaries.command.MiniAppCommandBoundary;
import superapp.boundaries.command.MiniAppCommandIdBoundary;
import superapp.boundaries.object.SuperAppObjectIdBoundary;
import superapp.boundaries.user.UserIdBoundary;
import superapp.data.MiniAppCommandEntity;
import superapp.utils.Invokers.ObjectIdInvoker;
import superapp.utils.Invokers.UserIdInvoker;

/**
 * The MiniAppCommandConverter class is responsible for converting MiniAppCommandEntity objects to MiniAppCommandBoundary objects and vice versa.
 */
@Component
public class MiniAppCommandConverter {

    /**
     * Converts a MiniAppCommandEntity to a MiniAppCommandBoundary.
     *
     * @return a MiniAppCommandBoundary object
     */
    public MiniAppCommandConverter() {
    }

    /**
     * Converts a MiniAppCommandEntity to a MiniAppCommandBoundary.
     *
     * @param entity the MiniAppCommandEntity to convert
     * @return a MiniAppCommandBoundary object
     */
    public MiniAppCommandBoundary toBoundary(MiniAppCommandEntity entity) {
        MiniAppCommandBoundary boundary = new MiniAppCommandBoundary();

        String[] ids = entity.getId().split("\\$");
        boundary.setCommandId(new MiniAppCommandIdBoundary(ids[0], ids[1], ids[2]));
        boundary.setCommand(entity.getCommand());
        boundary.setTargetObject(new ObjectIdInvoker(new SuperAppObjectIdBoundary(entity.getTargetObject().getObjectId().getSuperapp(), entity.getTargetObject().getObjectId().getInternalObjectId())));
        boundary.setInvocationTimestamp(entity.getInvocationTimestamp());
        boundary.setInvokedBy(new UserIdInvoker(new UserIdBoundary(entity.getInvokedBy().getUserId().getSuperapp(), entity.getInvokedBy().getUserId().getEmail())));
        boundary.setCommandAttributes(entity.getCommandAttributes());

        return boundary;
    }

    /**
     * Converts a MiniAppCommandBoundary to a MiniAppCommandEntity.
     *
     * @param boundary the MiniAppCommandBoundary to convert
     * @return a MiniAppCommandEntity object
     */
    public MiniAppCommandEntity toEntity(MiniAppCommandBoundary boundary) {
        MiniAppCommandEntity entity = new MiniAppCommandEntity();
        entity.setId(createID(boundary));
        entity.setCommand(boundary.getCommand());
        entity.setTargetObject(boundary.getTargetObject());
        entity.setInvocationTimestamp(boundary.getInvocationTimestamp());
        entity.setInvokedBy(boundary.getInvokedBy());
        entity.setCommandAttributes(boundary.getCommandAttributes());
        return entity;
    }

    /**
     * Creates an ID for a MiniAppCommandBoundary based on its attributes.
     *
     * @param miniAppCommandBoundary the MiniAppCommandBoundary for which to create the ID
     * @return a String representing the created ID
     */
    private String createID(MiniAppCommandBoundary miniAppCommandBoundary) {
        String idFormat = "%s$%s$%s";
        return idFormat.formatted(
                miniAppCommandBoundary.getCommandId().getSuperapp(),
                miniAppCommandBoundary.getCommandId().getMiniapp(),
                miniAppCommandBoundary.getCommandId().getInternalCommandId()
        );
    }
}
