package superapp.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.*;
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
@CompoundIndex(def = "{'location': '2dsphere'}", name = "location_index")
public class SuperAppObjectEntity {
    @Id
    private String id;  //specific superApp specifier
    private String type; // Type of the object boundary
    private String alias; // Alias of the object boundary
    private Boolean active; // Whether the object boundary is active or not
    private Date creationTimeStamp; // Time when the object boundary was created
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location; // used to locate objects within earth's sphere
    private UserIdInvoker userIdInvoker;  //The email address associated with the user ID. comes from UserIdInvoker -> UserIdBoundary.email
    private Map<String, Object> objectDetails; // The other details of the object
    @DBRef(lazy = true)
    private Set<SuperAppObjectEntity> children; // ObjectEntity Children
    @DBRef(lazy = true)
    private Set<SuperAppObjectEntity> parents; // ObjectEntity Parents

    /**
     * Constructs a SuperAppObjectEntity with the specified parameters.
     *
     * @param id                the ID of the super app object
     * @param type              the type of the super app object
     * @param alias             the alias of the super app object
     * @param active            indicates whether the super app object is active
     * @param creationTimeStamp the timestamp of when the super app object was created
     * @param location          the location of the super app object's location
     * @param userIdInvoker     the user ID invoker associated with the super app object
     * @param objectDetails     additional details of the super app object
     */
    public SuperAppObjectEntity(String id, String type, String alias, Boolean active, Date creationTimeStamp,
                                GeoJsonPoint location, UserIdInvoker userIdInvoker, Map<String, Object> objectDetails,
                                Set<SuperAppObjectEntity> children, Set<SuperAppObjectEntity> parents) {
        this.id = id;
        this.type = type;
        this.alias = alias;
        this.active = active;
        this.creationTimeStamp = creationTimeStamp;
        this.location = location;
        this.userIdInvoker = userIdInvoker;
        this.objectDetails = objectDetails;
        this.children = children;
        this.parents = parents;
    }

    /**
     * Default constructor for the SuperAppObjectEntity class.
     * Initializes the objectDetails field as an empty TreeMap.
     */
    public SuperAppObjectEntity() {
        this.objectDetails = new TreeMap<>();
        this.children = new TreeSet<>();
        this.parents = new TreeSet<>();
    }

    /**
     * Retrieves the ID of the super app object.
     *
     * @return the ID of the super app object
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the super app object.
     *
     * @param id the ID of the super app object
     */
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
     * Sets the longitude of the object.
     *
     * @return location The location of the object.
     */
    public GeoJsonPoint getLocation() {
        return location;
    }

    /**
     * Gets the location of the object.
     *
     * @param location The location of the object.
     */
    public void setLocation(GeoJsonPoint location) {
        this.location = location;
    }

    /**
     * Retrieves the user ID invoker associated with the SuperAppObjectEntity.
     *
     * @return the user ID invoker
     */
    public UserIdInvoker getUserIdInvoker() {
        return userIdInvoker;
    }

    /**
     * Sets the user ID invoker for the SuperAppObjectEntity.
     *
     * @param userIdInvoker the user ID invoker to set
     */
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

    /**
     * Retrieves the children of the SuperAppObjectEntity.
     *
     * @return the set of children
     */
    public Set<SuperAppObjectEntity> getChildren() {
        return children;
    }

    /**
     * Sets the children of the SuperAppObjectEntity.
     *
     * @param children the set of children to set
     */
    public void setChildren(Set<SuperAppObjectEntity> children) {
        this.children = children;
    }

    /**
     * Retrieves the parents of the SuperAppObjectEntity.
     *
     * @return the set of parents
     */
    public Set<SuperAppObjectEntity> getParents() {
        return parents;
    }

    /**
     * Sets the parents of the SuperAppObjectEntity.
     *
     * @param parents the set of parents to set
     */
    public void setParents(Set<SuperAppObjectEntity> parents) {
        this.parents = parents;
    }

    /**
     * Adds a child SuperAppObjectEntity to the set of children.
     *
     * @param child the child to add
     * @return true if the child is successfully added, false otherwise
     */
    public boolean addChild(SuperAppObjectEntity child) {
        return this.children.add(child);
    }

    /**
     * Adds a parent SuperAppObjectEntity to the set of parents.
     *
     * @param parent the parent to add
     * @return true if the parent is successfully added, false otherwise
     */
    public boolean addParent(SuperAppObjectEntity parent) {
        return this.parents.add(parent);
    }

    /**
     * Returns a string representation of the SuperAppObjectEntity.
     *
     * @return a string representation of the object
     */

    @Override
    public String toString() {
        return "SuperAppObjectEntity{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", alias='" + alias + '\'' +
                ", active=" + active +
                ", creationTimeStamp=" + creationTimeStamp +
                ", location=" + location +
                ", userIdInvoker=" + userIdInvoker +
                ", objectDetails=" + objectDetails +
                ", children=" + children +
                ", parents=" + parents +
                '}';
    }

    /**
     * Checks if the SuperAppObjectEntity is equal to another object.
     *
     * @param o the object to compare
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SuperAppObjectEntity that = (SuperAppObjectEntity) o;
        return this.id.equals(that.getId());
    }

    /**
     * Generates a hash code for the SuperAppObjectEntity.
     *
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
