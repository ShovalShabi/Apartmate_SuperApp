package superapp.boundaries.user;

import java.util.Objects;

/**
 * This is the class which representing a specific type of boundary object, role, and it's avatar.
 * The class has also user identifier.
 */

public class UserBoundary {
    private UserIdBoundary userId; //The user ID of the user.
    private String role;  //The role of the user.
    private String username; //The username of the user.
    private String avatar;  //The avatar of the user.

    /**
     * Creates a new UserBoundary object with default values for all fields.
     */
    public UserBoundary() {
    }

    /**
     * Creates a new UserBoundary object with the specified values for all fields.
     *
     * @param userId   The user ID of the user.
     * @param role     The role of the user.
     * @param username The username of the user.
     * @param avatar   The avatar of the user.
     */
    public UserBoundary(UserIdBoundary userId, String role, String username, String avatar) {
        this.userId = userId;
        this.role = role;
        this.username = username;
        this.avatar = avatar;
    }

    /**
     * Creates a new UserBoundary object with the specified values for all fields except user ID, which is created using the provided superapp and email.
     *
     * @param superapp The superapp ID to include in the user ID.
     * @param email    The email address to include in the user ID.
     * @param role     The role of the user.
     * @param username The username of the user.
     * @param avatar   The avatar of the user.
     */
    public UserBoundary(String superapp, String email, String role, String username, String avatar) {
        this(email, role, username, avatar);
        this.userId = new UserIdBoundary(superapp, email);
    }

    /**
     * Creates a new UserBoundary object with the specified values for all fields except user ID, which is created using the provided email.
     *
     * @param email    The email address to include in the user ID.
     * @param role     The role of the user.
     * @param username The username of the user.
     * @param avatar   The avatar of the user.
     */
    public UserBoundary(String email, String role, String username, String avatar) {
        this.userId = new UserIdBoundary(email);
        this.role = role;
        this.username = username;
        this.avatar = avatar;
    }

    /**
     * Retrieves the UserIdBoundary object associated with this UserBoundary.
     *
     * @return the UserIdBoundary object
     */
    public UserIdBoundary getUserId() {
        return userId;
    }

    /**
     * Sets the UserIdBoundary object for this UserBoundary.
     *
     * @param userId the UserIdBoundary object to set
     */
    public void setUserId(UserIdBoundary userId) {
        this.userId = userId;
    }

    /**
     * Retrieves the role of the user.
     *
     * @return the user's role
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the role of the user.
     *
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Retrieves the username of the user.
     *
     * @return the user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     *
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Retrieves the avatar of the user.
     *
     * @return the user's avatar
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * Sets the avatar of the user.
     *
     * @param avatar the avatar to set
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * Returns a string representation of the UserBoundary object.
     *
     * @return a string representation of the UserBoundary object
     */
    @Override
    public String toString() {
        return "UserBoundary{" +
                "userId=" + userId +
                ", role='" + role + '\'' +
                ", username='" + username + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }

    /**
     * Compares this UserBoundary to the specified object for equality.
     *
     * @param o the object to compare
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserBoundary that = (UserBoundary) o;
        return Objects.equals(userId, that.userId);
    }

    /**
     * Returns a hash code value for the UserBoundary object.
     *
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(userId, role, username, avatar);
    }
}
