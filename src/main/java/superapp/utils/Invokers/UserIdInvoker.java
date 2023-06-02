package superapp.utils.Invokers;

import superapp.boundaries.user.UserIdBoundary;

/**
 * The UserIdInvoker class represents an object that encapsulates a UserIdBoundary object and allows access to its data.
 */
public class UserIdInvoker {
    private UserIdBoundary userId; // The UserIdBoundary object that this class encapsulates

    /**
     * Constructs a new instance of UserIdInvoker with an empty constructor.
     */
    public UserIdInvoker() {
    }

    /**
     * Constructs a new instance of UserIdInvoker with the provided UserIdBoundary object.
     *
     * @param userId the UserIdBoundary object to be encapsulated.
     */
    public UserIdInvoker(UserIdBoundary userId) {
        this.userId = userId;
    }

    /**
     * Returns the UserIdBoundary object that this class encapsulates.
     *
     * @return the UserIdBoundary object that this class encapsulates.
     */
    public UserIdBoundary getUserId() {
        return userId;
    }

    /**
     * Sets the UserIdBoundary object that this class encapsulates.
     *
     * @param userId the UserIdBoundary object to be encapsulated.
     */
    public void setUserId(UserIdBoundary userId) {
        this.userId = userId;
    }

    /**
     * Returns a string representation of the UserIdInvoker object.
     * <p>
     * The string representation is in the format "UserIdInvoker{userId=<userId>}".
     * The <userId> placeholder is replaced with the actual userId value.
     *
     * @return a string representation of the UserIdInvoker object
     */
    @Override
    public String toString() {
        return "UserIdInvoker{" +
                "userId=" + userId +
                '}';
    }
}
