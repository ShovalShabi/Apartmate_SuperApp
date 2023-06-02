package superapp.boundaries.user;

import java.util.Objects;

/**
 * Represents a NewUserBoundary object that holds information about a new user.
 */
public class NewUserBoundary {
    private String email;
    private String role;
    private String username;
    private String avatar;

    /**
     * Default constructor for the NewUserBoundary class.
     */
    public NewUserBoundary() { }

    /**
     * Constructs a NewUserBoundary object with the specified email, role, username, and avatar.
     *
     * @param email    the email of the new user
     * @param role     the role of the new user
     * @param username the username of the new user
     * @param avatar   the avatar of the new user
     */
    public NewUserBoundary(String email, String role, String username, String avatar) {
        this.email = email;
        this.role = role;
        this.username = username;
        this.avatar = avatar;
    }

    /**
     * Retrieves the email of the new user.
     *
     * @return the email of the new user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the new user.
     *
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retrieves the role of the new user.
     *
     * @return the role of the new user
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the role of the new user.
     *
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Retrieves the username of the new user.
     *
     * @return the username of the new user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the new user.
     *
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Retrieves the avatar of the new user.
     *
     * @return the avatar of the new user
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * Sets the avatar of the new user.
     *
     * @param avatar the avatar to set
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * Returns a string representation of the NewUserBoundary object.
     *
     * @return a string representation of the NewUserBoundary object
     */
    @Override
    public String toString() {
        return "NewUserBoundary{" +
                "email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", username='" + username + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }

    /**
     * Compares this NewUserBoundary to the specified object for equality.
     *
     * @param o the object to compare
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewUserBoundary that = (NewUserBoundary) o;
        return Objects.equals(email, that.email);
    }

    /**
     * Returns a hash code value for the NewUserBoundary object.
     *
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(email, role, username, avatar);
    }
}
