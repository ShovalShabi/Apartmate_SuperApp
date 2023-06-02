package superapp.boundaries.command;

import superapp.utils.Invokers.ObjectIdInvoker;
import superapp.utils.Invokers.UserIdInvoker;

import java.util.Date;
import java.util.Map;

/**
 * A boundary object representing a command executed by a user on a mini app object.
 * It contains information such as the command ID, the command string, the target object ID, the time of invocation,
 * the user who invoked the command and the attributes associated with the command.
 */
public class MiniAppCommandBoundary {

    // Instance variables:
    private MiniAppCommandIdBoundary commandId; // ID of the command boundary
    private String command; // Command string
    private ObjectIdInvoker targetObject; // ID of the target object
    private Date invocationTimestamp; // Time when the command was invoked
    private UserIdInvoker invokedBy; // ID of the user who invoked the command
    private Map<String, Object> commandAttributes; // Attributes of the command


    /**
     * Default constructor for MiniAppCommandBoundary.
     */
    public MiniAppCommandBoundary() {
    }

    /**
     * Constructor for MiniAppCommandBoundary with all parameters.
     *
     * @param commandId           ID of the command boundary
     * @param command             Command string
     * @param targetObject        ID of the target object
     * @param invocationTimestamp Time when the command was invoked
     * @param invokedBy           ID of the user who invoked the command
     * @param commandAttributes   Attributes of the command
     */
    public MiniAppCommandBoundary(MiniAppCommandIdBoundary commandId, String command, ObjectIdInvoker targetObject, Date invocationTimestamp, UserIdInvoker invokedBy,  Map<String, Object> commandAttributes) {
        this.commandId = commandId;
        this.command = command;
        this.targetObject = targetObject;
        this.invocationTimestamp = invocationTimestamp;
        this.invokedBy = invokedBy;
        this.commandAttributes = commandAttributes;
    }

    /**
     * Returns the invocation timestamp.
     *
     * @return the invocation timestamp
     */
    public Date getInvocationTimestamp() {
        return invocationTimestamp;
    }

    /**
     * Sets the invocation timestamp.
     *
     * @param invocationTimestamp the invocation timestamp to set
     */
    public void setInvocationTimestamp(Date invocationTimestamp) {
        this.invocationTimestamp = invocationTimestamp;
    }

    /**
     * Returns the command ID.
     *
     * @return the command ID
     */
    public MiniAppCommandIdBoundary getCommandId() {
        return commandId;
    }

    /**
     * Sets the command ID.
     *
     * @param commandId the command ID to set
     */
    public void setCommandId(MiniAppCommandIdBoundary commandId) {
        this.commandId = commandId;
    }

    /**
     * Returns the command.
     *
     * @return the command
     */
    public String getCommand() {
        return command;
    }

    /**
     * Sets the command.
     *
     * @param command the command to set
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * Returns the target object ID.
     *
     * @return the target object ID
     */
    public ObjectIdInvoker getTargetObject() {
        return targetObject;
    }

    /**
     * Sets the target object ID.
     *
     * @param targetObject the target object ID to set
     */
    public void setTargetObject(ObjectIdInvoker targetObject) {
        this.targetObject = targetObject;
    }

    /**
     * Returns the user ID who invoked the command.
     *
     * @return the user ID who invoked the command
     */
    public UserIdInvoker getInvokedBy() {
        return invokedBy;
    }

    /**
     * Sets the user ID who invoked the command.
     *
     * @param invokedBy the user ID who invoked the command to set
     */
    public void setInvokedBy(UserIdInvoker invokedBy) {
        this.invokedBy = invokedBy;
    }

    /**
     * Returns the map of command attributes for the command.
     *
     * @return a map of maps, where the outer map's key is the attribute name, and the value is another map of attribute
     * values, where the key is the attribute value and the value is a string representation of the frequency of the attribute
     * value in the command's data.
     */
    public  Map<String, Object> getCommandAttributes() {
        return commandAttributes;
    }

    /**
     * Sets the command attributes for this object.
     *
     * @param commandAttributes a map of string keys to maps of string keys and string values.
     */
    public void setCommandAttributes( Map<String, Object> commandAttributes) {
        this.commandAttributes = commandAttributes;
    }

    /**
     * A class representing a mini app command boundary, containing information about a command sent from a mini app to a super app.
     */
    @Override
    public String toString() {
        return "MiniAppCommandBoundary{" +
                "commandId=" + commandId +
                ", command='" + command + '\'' +
                ", targetObject=" + targetObject +
                ", invocationTimestamp=" + invocationTimestamp +
                ", invokedBy=" + invokedBy +
                ", commandAttributes=" + commandAttributes +
                '}';
    }
}
