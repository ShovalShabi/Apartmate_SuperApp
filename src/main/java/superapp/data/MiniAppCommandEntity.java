package superapp.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import superapp.utils.Invokers.ObjectIdInvoker;
import superapp.utils.Invokers.UserIdInvoker;

import java.util.Date;
import java.util.Map;

/**
 * The MiniAppCommandEntity class represents a mini app command entity in the system.
 * It is annotated with @Document to indicate that instances of this class can be persisted to a database collection.
 * The collection name for this entity is "MINI_APP_COMMAND".
 */
@Document(collection = "MINI_APP_COMMAND")
public class MiniAppCommandEntity {

    // Instance variables:
    @Id
    private String id; //The ID of the SuperApp.
    private String miniapp; // The command miniapp.
    private String command; //The command string.
    private ObjectIdInvoker targetObject; //The ID of the target object.
    private Date invocationTimestamp; //The time when the command was invoked.
    private UserIdInvoker invokedBy; //The ID of the user who invoked the command.
    private Map<String, Object> commandAttributes; //The attributes of the command.

    public MiniAppCommandEntity() {
    } //Empty constructor.

    /**
     * Constructor with parameters.
     *
     * @param command             The command string.
     * @param targetObject        The ID of the target object.
     * @param invocationTimestamp The time when the command was invoked.
     * @param invokedBy           The ID of the user who invoked the command.
     * @param commandAttributes   The attributes of the command.
     */
    public MiniAppCommandEntity(String id,
                                String command, ObjectIdInvoker targetObject, Date invocationTimestamp,
                                UserIdInvoker invokedBy, Map<String, Object> commandAttributes) {
        this.id = id;
        this.command = command;
        this.targetObject = targetObject;
        this.invocationTimestamp = invocationTimestamp;
        this.invokedBy = invokedBy;
        this.commandAttributes = commandAttributes;
    }

    /**
     * Retrieves the ID of the object.
     *
     * @return the ID of the object
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the object.
     *
     * @param id the ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    public String getMiniapp() {
        return miniapp;
    }

    public void setMiniapp(String miniapp) {
        this.miniapp = miniapp;
    }

    /**
     * Getter for the target object ID.
     *
     * @return The ID of the target object.
     */
    public ObjectIdInvoker getTargetObject() {
        return targetObject;
    }

    /**
     * Setter for the target object ID.
     *
     * @param targetObject The ID of the target object.
     */
    public void setTargetObject(ObjectIdInvoker targetObject) {
        this.targetObject = targetObject;
    }

    /**
     * Getter for the ID of the user who invoked the command.
     *
     * @return The ID of the user who invoked the command.
     */
    public UserIdInvoker getInvokedBy() {
        return invokedBy;
    }

    /**
     * Setter for the ID of the user who invoked the command.
     *
     * @param invokedBy The ID of the user who invoked the command.
     */
    public void setInvokedBy(UserIdInvoker invokedBy) {
        this.invokedBy = invokedBy;
    }

    /**
     * Getter for the attributes of the command.
     *
     * @return The attributes of the command.
     */
    public Map<String, Object> getCommandAttributes() {
        return commandAttributes;
    }

    /**
     * Setter for the attributes of the command.
     *
     * @param commandAttributes The attributes of the command.
     */
    public void setCommandAttributes(Map<String, Object> commandAttributes) {
        this.commandAttributes = commandAttributes;
    }

    /**
     * Gets the command.
     *
     * @return the command
     */
    public String getCommand() {
        return command;
    }

    /**
     * Sets the command.
     *
     * @param command the command
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * Gets the invocation timestamp.
     *
     * @return the invocation timestamp
     */
    public Date getInvocationTimestamp() {
        return invocationTimestamp;
    }

    /**
     * Sets the invocation timestamp.
     *
     * @param invocationTimestamp the invocation timestamp
     */
    public void setInvocationTimestamp(Date invocationTimestamp) {
        this.invocationTimestamp = invocationTimestamp;
    }

    /**
     * Returns a string representation of the MiniAppCommandEntity.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "MiniAppCommandEntity{" +
                "id='" + id + '\'' +
                ", command='" + command + '\'' +
                ", targetObject=" + targetObject +
                ", invocationTimestamp=" + invocationTimestamp +
                ", invokedBy=" + invokedBy +
                ", commandAttributes=" + commandAttributes +
                '}';
    }
}
