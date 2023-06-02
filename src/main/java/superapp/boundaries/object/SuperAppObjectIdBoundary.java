package superapp.boundaries.object;

import java.util.Objects;

/**
 * This is a class which is trusted on that identifier of a boundary object.
 * The class has superApp specifier and internal object id.
 */
public class SuperAppObjectIdBoundary {
    private String superapp;  //specific superApp specifier
    private String internalObjectId;  //internal object id

    /**
     * Constructs a new ObjectIdBoundary instance.
     */
    public SuperAppObjectIdBoundary() {
    }

    /**
     * Constructs a new ObjectIdBoundary instance with the given superapp and internal object ID.
     *
     * @param superapp              the name of the superapp
     * @param internalObjectId the internal object ID
     */
    public SuperAppObjectIdBoundary(String superapp, String internalObjectId) {
        this.superapp = superapp;
        this.internalObjectId = internalObjectId;
    }

    /**
     * Constructs a new ObjectIdBoundary instance with the given internal object ID and a default superapp name.
     *
     * @param internalObjectId the internal object ID
     */
    public SuperAppObjectIdBoundary(String internalObjectId) {
        this.internalObjectId = internalObjectId;
    }

    /**
     * Returns the superapp name.
     *
     * @return the superapp name
     */
    public String getSuperapp() {
        return superapp;
    }

    /**
     * Sets the superapp name.
     *
     * @param superapp the superapp name
     */
    public void setSuperapp(String superapp) {
        this.superapp = superapp;
    }

    /**
     * Returns the internal object ID.
     *
     * @return the internal object ID
     */
    public String getInternalObjectId() {
        return internalObjectId;
    }

    /**
     * Setter method for the internal object id
     *
     * @param internalObjectId a String representing the internal object id
     */
    public void setInternalObjectId(String internalObjectId) {
        this.internalObjectId = internalObjectId;
    }

    /**
     * Returns a String representation of the ObjectIdBoundary object
     *
     * @return a String representing the ObjectIdBoundary object
     */
    @Override
    public String toString() {
        return "ObjectIdBoundary{" +
                "superapp='" + superapp + '\'' +
                ",internalObjectId='" + internalObjectId + '\'' +
                '}';
    }

    /**
     * Checks if this SuperAppObjectIdBoundary is equal to another object.
     * <p>
     * Two SuperAppObjectIdBoundary objects are considered equal if they have the same values for the 'superapp' and 'internalObjectId' properties.
     *
     * @param o the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SuperAppObjectIdBoundary that = (SuperAppObjectIdBoundary) o;
        return Objects.equals(superapp, that.superapp) && Objects.equals(internalObjectId, that.internalObjectId);
    }

    /**
     * Returns the hash code value for this SuperAppObjectIdBoundary.
     * The hash code is generated based on the 'superapp' and 'internalObjectId' properties.
     * @return the hash code value for this SuperAppObjectIdBoundary
     */
    @Override
    public int hashCode() {
        return Objects.hash(superapp, internalObjectId);
    }
}
