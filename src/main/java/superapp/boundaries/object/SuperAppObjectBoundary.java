package superapp.boundaries.object;

import superapp.utils.Invokers.UserIdInvoker;
import superapp.utils.Location;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * ObjectBoundary is a class that represents an object boundary, containing information about the object such as its ID,
 * type, alias, activation status, creation timestamp, location, user ID who created it and details about the object.
 */
public class SuperAppObjectBoundary{

    private SuperAppObjectIdBoundary objectId; // ID of the object boundary
    private String type; // Type of the object boundary
    private String alias; // Alias of the object boundary
    private Boolean active; // Whether the object boundary is active or not
    private Date creationTimestamp; // Time when the object boundary was created
    private Location location; // Location of the object boundary
    private UserIdInvoker createdBy; // ID of the user who created the object boundary
    private Map<String, Object> objectDetails; // Details of the object boundary


    /**
     * Creates an empty object boundary.
     */
    public SuperAppObjectBoundary() {
    }


    /**
     * Creates an object boundary with the specified parameters.
     *
     * @param superAppObjectIdBoundary  the object ID boundary
     * @param type              the type of the object
     * @param alias             the alias of the object
     * @param active            whether the object is active or not
     * @param creationTimestamp the creation timestamp of the object
     * @param location          the location of the object
     * @param createdBy         the user ID who invoked the object
     * @param objectDetails     the details of the object
     */
    public SuperAppObjectBoundary(SuperAppObjectIdBoundary superAppObjectIdBoundary, String type, String alias, Boolean active, Date creationTimestamp, Location location, UserIdInvoker createdBy, Map<String, Object> objectDetails) {
        this.objectId = superAppObjectIdBoundary;
        this.type = type;
        this.alias = alias;
        this.active = active;
        this.creationTimestamp = creationTimestamp;
        this.location = location;
        this.createdBy = createdBy;
        this.objectDetails = objectDetails;
    }

    /**
     * This method sets the ObjectIdBoundary of the ObjectBoundary instance.
     *
     * @param superAppObjectIdBoundary The ObjectIdBoundary to be set.
     */
    public void setObjectId(SuperAppObjectIdBoundary superAppObjectIdBoundary) {
        this.objectId = superAppObjectIdBoundary;
    }

    /**
     * This method sets the type of the ObjectBoundary instance, if it is a valid type.
     *
     * @param type The type to be set.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * This method sets the alias of the ObjectBoundary instance, if it is a valid alias.
     *
     * @param alias The alias to be set.
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * This method sets the active state of the ObjectBoundary instance.
     *
     * @param active The active state to be set.
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * This method sets the creation timestamp of the ObjectBoundary instance, if it is a valid date.
     *
     * @param creationTimestamp The creation timestamp to be set.
     */
    public void setCreationTimestamp(Date creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    /**
     * This method sets the location of this object boundary
     *
     * @param location The location to set
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * This method sets the user ID of this object boundary
     *
     * @param createdBy The user ID to set
     */
    public void setCreatedBy(UserIdInvoker createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * This method sets the object details of this object boundary
     *
     * @param objectDetails The object details to set
     */
    public void setObjectDetails(Map<String, Object> objectDetails) {
        this.objectDetails = objectDetails;
    }

    /**
     * This method gets the object ID boundary of this object boundary
     *
     * @return The object ID boundary of this object boundary
     */
    public SuperAppObjectIdBoundary getObjectId() {
        return objectId;
    }

    /**
     * This method gets the type of this object boundary
     *
     * @return The type of this object boundary
     */
    public String getType() {
        return type;
    }

    /**
     * This method gets the alias of this object boundary
     *
     * @return The alias of this object boundary
     */
    public String getAlias() {
        return alias;
    }

    /**
     * This method checks if this object boundary is active
     *
     * @return true if this object boundary is active, false otherwise
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * This method gets the creation time stamp of this object boundary
     *
     * @return The creation time stamp of this object boundary
     */
    public Date getCreationTimestamp() {
        return creationTimestamp;
    }

    /**
     * This method gets the location of this object boundary
     *
     * @return The location of this object boundary
     */
    public Location getLocation() {
        return location;
    }

    /**
     * This method gets the user ID of this object boundary
     *
     * @return The user ID of this object boundary
     */
    public UserIdInvoker getCreatedBy() {
        return createdBy;
    }

    /**
     * This method gets the object details of this object boundary
     *
     * @return The object details of this object boundary
     */
    public Map<String, Object> getObjectDetails() {
        return objectDetails;
    }

    /**
     * Returns a string representation of the ObjectBoundary object.
     *
     * @return a string representation of the ObjectBoundary object
     */
    @Override
    public String toString() {
        return "ObjectBoundary{" + "objectId=" + objectId + ", type='" + type + '\'' + ", alias='" + alias + '\'' + ", active=" + active + ", creationTimeStamp=" + creationTimestamp + ", location=" + location + ", userId=" + createdBy + ", objectDetails=" + objectDetails + '}';
    }

    /**
     * Compares this SuperAppObjectBoundary to the specified object for equality.
     * @param o the object to compare
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SuperAppObjectBoundary that = (SuperAppObjectBoundary) o;
        return Objects.equals(objectId, that.objectId);
    }

    /**
     * Returns a hash code value for the SuperAppObjectBoundary object.
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(objectId, type, alias, active, creationTimestamp, location, createdBy, objectDetails);
    }
}
