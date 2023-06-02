package superapp.utils.Invokers;

import superapp.boundaries.object.SuperAppObjectIdBoundary;

/**
 * The ObjectIdInvoker class is responsible for invoking methods from the ObjectIdBoundary interface.
 * It has a private instance variable of type ObjectIdBoundary and provides getter and setter methods to access it.
 */
public class ObjectIdInvoker {
    private SuperAppObjectIdBoundary objectId; // The instance variable of type ObjectIdBoundary

    /**
     * Constructs an ObjectIdInvoker instance with no arguments.
     */
    public ObjectIdInvoker() {
    }

    /**
     * Constructs an ObjectIdInvoker instance with the provided objectIdBoundary instance variable.
     *
     * @param objectId an instance of the ObjectIdBoundary interface.
     */
    public ObjectIdInvoker(SuperAppObjectIdBoundary objectId) {
        this.objectId = objectId;
    }

    /**
     * Returns the ObjectIdBoundary instance variable of the ObjectIdInvoker instance.
     *
     * @return the objectIdBoundary instance variable.
     */
    public SuperAppObjectIdBoundary getObjectId() {
        return objectId;
    }

    /**
     * Sets the ObjectIdBoundary instance variable of the ObjectIdInvoker instance.
     *
     * @param superAppObjectIdBoundary an instance of the ObjectIdBoundary interface.
     */
    public void setObjectId(SuperAppObjectIdBoundary superAppObjectIdBoundary) {
        this.objectId = superAppObjectIdBoundary;
    }

    /**
     * Returns a string representation of the ObjectIdInvoker object.
     * <p>
     * The string representation is in the format "ObjectIdInvoker{objectId=<objectId>}".
     * The <objectId> placeholder is replaced with the actual objectId value.
     *
     * @return a string representation of the ObjectIdInvoker object
     */
    @Override
    public String toString() {
        return "ObjectIdInvoker{" +
                "objectId=" + objectId +
                '}';
    }
}
