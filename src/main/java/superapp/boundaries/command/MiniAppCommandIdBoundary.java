package superapp.boundaries.command;

/**
 * CommandIdBoundary is a class representing a command identifier that contains information about the superapp, miniapp, and internal object ID.
 * It provides methods for setting and getting values of these parameters and for creating instances of the CommandIdBoundary class
 */
public class MiniAppCommandIdBoundary {

    private String superapp; // The superapp parameter of the command identifier
    private String miniapp; // The miniapp parameter of the command identifier
    private String internalCommandId; // The internal object ID parameter of the command identifier


    /**
     * Constructs a new instance of the CommandIdBoundary class with default values for all parameters
     */
    public MiniAppCommandIdBoundary() {
    }

    /**
     * Constructs a new instance of the CommandIdBoundary class with specified values for all parameters
     *
     * @param superapp         the superapp parameter of the command identifier
     * @param miniapp          the miniapp parameter of the command identifier
     * @param internalObjectId the internal object ID parameter of the command identifier
     */
    public MiniAppCommandIdBoundary(String superapp, String miniapp, String internalObjectId) {
        this.superapp = superapp;
        this.miniapp = miniapp;
        this.internalCommandId = internalObjectId;
    }

    /**
     * Constructs a new instance of the CommandIdBoundary class with default values for the superapp and specified value for the internalObjectId parameter
     *
     * @param miniapp          the miniapp parameter of the command identifier
     * @param internalObjectId the internal object ID parameter of the command identifier
     */
    public MiniAppCommandIdBoundary(String miniapp, String internalObjectId) {
        this.miniapp = miniapp;
        this.internalCommandId = internalObjectId;
    }

    /**
     * Returns the value of the superapp parameter of the command identifier
     *
     * @return the superapp parameter of the command identifier
     */
    public String getSuperapp() {
        return superapp;
    }

    /**
     * Sets the value of the "spring.application.name" property to the "superapp" field.
     *
     * @param superApp the value of the "spring.application.name" property
     */
    public void setSuperapp(String superApp) {
        this.superapp = superApp;
    } // gets superApp name from resources

    /**
     * Gets the miniapp for this object.
     *
     * @return the miniapp for this object
     */
    public String getMiniapp() {
        return miniapp;
    }

    /**
     * Sets the miniapp for this object.
     *
     * @param miniapp the miniapp to be set for this object
     */
    public void setMiniapp(String miniapp) {
        this.miniapp = miniapp;
    }

    /**
     * Gets the internal object ID for this object.
     *
     * @return the internal object ID for this object
     */
    public String getInternalCommandId() {
        return internalCommandId;
    }

    /**
     * Sets the internal object ID for this object.
     *
     * @param internalCommandId the internal object ID to be set for this object
     */
    public void setInternalCommandId(String internalCommandId) {
        this.internalCommandId = internalCommandId;
    }


    /**
     * The string representation includes the values of the 'superapp', 'miniapp', and 'internalCommandId' properties.
     * @return a string representation of the User object
     */
    @Override
    public String toString() {
        return "CommandIdBoundary{" +
                "superapp='" + superapp + '\'' +
                ", miniapp='" + miniapp + '\'' +
                ", internalCommandId='" + internalCommandId + '\'' +
                '}';
    }
}
