package superapp.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import superapp.utils.Invokers.UserIdInvoker;

import java.util.*;

/**
 * The SuperAppObjectEntity class represents an object boundary in the SuperApp system.
 * <p>
 * It is an entity class that maps to the SUPERAPPOBJECTS table in the database.
 * <p>
 * The object boundary is identified by a combination of superapp and internalObjectId.
 */

@Document(collection = "SUPER_APP_OBJECTS")
public class SuperAppObjectEntity {
    @Id
    private String id;  //specific superApp specifier
    private String type; // Type of the object boundary
    private String alias; // Alias of the object boundary
    private Boolean active; // Whether the object boundary is active or not
    private Date creationTimeStamp; // Time when the object boundary was created
    private Double lat; // The latitude of the location
    private Double lng; // The longitude of the location.
    private UserIdInvoker userIdInvoker;  //The email address associated with the user ID. comes from UserIdInvoker -> UserIdBoundary.email
    private Map<String, Object> objectDetails; // The other details of the object
    @DBRef(lazy = true)
    private List<SuperAppObjectEntity> children; // ObjectEntity Children
    @DBRef(lazy = true)
    private List<SuperAppObjectEntity> parents; // ObjectEntity Parents

    public SuperAppObjectEntity(String id, String type, String alias, Boolean active, Date creationTimeStamp, Double lat, Double lng, UserIdInvoker userIdInvoker, Map<String, Object> objectDetails) {
        this.id = id;
        this.type = type;
        this.alias = alias;
        this.active = active;
        this.creationTimeStamp = creationTimeStamp;
        this.lat = lat;
        this.lng = lng;
        this.userIdInvoker = userIdInvoker;
        this.objectDetails = objectDetails;
    }

    /**
     * Default constructor for the SuperAppObjectEntity class.
     * Initializes the objectDetails field as an empty TreeMap.
     */
    public SuperAppObjectEntity() {
        this.objectDetails = new TreeMap<>();
        this.children = new ArrayList<>();
        this.parents = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the type of the object.
     *
     * @return The type of the object.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the object.
     *
     * @param type The type of the object.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the alias of the object.
     *
     * @return The alias of the object.
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Sets the alias of the object.
     *
     * @param alias The alias of the object.
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * Returns the active status of the object.
     *
     * @return The active status of the object.
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * Sets the active status of the object.
     *
     * @param active The active status of the object.
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * Returns the creation timestamp of the object.
     *
     * @return The creation timestamp of the object.
     */
    public Date getCreationTimeStamp() {
        return creationTimeStamp;
    }

    /**
     * Sets the creation timestamp of the object.
     *
     * @param creationTimeStamp The creation timestamp of the object.
     */
    public void setCreationTimeStamp(Date creationTimeStamp) {
        this.creationTimeStamp = creationTimeStamp;
    }

    /**
     * Returns the latitude of the object.
     *
     * @return The latitude of the object.
     */
    public Double getLat() {
        return lat;
    }

    /**
     * Sets the latitude of the object.
     *
     * @param lat The latitude of the object.
     */
    public void setLat(Double lat) {
        this.lat = lat;
    }

    /**
     * Returns the longitude of the object.
     *
     * @return The longitude of the object.
     */
    public Double getLng() {
        return lng;
    }

    /**
     * Sets the longitude of the object.
     *
     * @param lng The longitude of the object.
     */
    public void setLng(Double lng) {
        this.lng = lng;
    }

    public UserIdInvoker getUserIdInvoker() {
        return userIdInvoker;
    }

    public void setUserIdInvoker(UserIdInvoker userIdInvoker) {
        this.userIdInvoker = userIdInvoker;
    }

    /**
     * Returns the object details map associated with this object.
     *
     * @return The object details map.
     */
    public Map<String, Object> getObjectDetails() {
        return objectDetails;
    }

    /**
     * Sets the object details map associated with this object.
     *
     * @param objectDetails The new object details map to set.
     */
    public void setObjectDetails(Map<String, Object> objectDetails) {
        this.objectDetails = objectDetails;
    }

    public List<SuperAppObjectEntity> getChildren() {
        return children;
    }

    public void setChildren(List<SuperAppObjectEntity> children) {
        this.children = children;
    }

    public List<SuperAppObjectEntity> getParents() {
        return parents;
    }

    public void setParents(List<SuperAppObjectEntity> parents) {
        this.parents = parents;
    }

    public boolean addChild(SuperAppObjectEntity child) {
        return this.children.add(child);
    }

    public boolean addParent(SuperAppObjectEntity parent) {
        return this.parents.add(parent);
    }

    @Override
    public String toString() {
        return "ObjectEntity{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", alias='" + alias + '\'' +
                ", active=" + active +
                ", creationTimeStamp=" + creationTimeStamp +
                ", lat=" + lat +
                ", lng=" + lng +
                ", userIdInvoker=" + userIdInvoker +
                ", objectDetails=" + objectDetails +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SuperAppObjectEntity that = (SuperAppObjectEntity) o;
        return this.id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, alias, active, creationTimeStamp, lat, lng, userIdInvoker, objectDetails, children, parents);
    }
}
