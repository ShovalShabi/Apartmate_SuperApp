package superapp.boundaries.user;

import java.util.Objects;

/**
 * A class which is trusted on user identifier to specific superApp.
 * The class has email that associated with the user and superApp specifier for specific super App.
 */
public class UserIdBoundary {
    private String superapp;  //The superApp associated with the user ID.
    private String email;  //The email address associated with the user ID.

    /**
     * Constructs a new UserIdBoundary with no associated superapp or email address.
     */
    public UserIdBoundary() { }

    /**
     * Constructs a new UserIdBoundary with the specified superapp and email address.
     * @param superapp the superapp associated with the user ID
     * @param email    the email address associated with the user ID
     */
    public UserIdBoundary(String superapp, String email) {
        this.superapp = superapp;
        this.email = email;
    }

    /**
     * Constructs a new UserIdBoundary with the specified superapp and email address.
     * @param email the email address associated with the user ID
     */
    public UserIdBoundary(String email) {
        this.email = email;
    }

    /**
     * Returns the superapp associated with the user ID.
     *
     * @return the superapp associated with the user ID
     */
    public String getSuperapp() {
        return superapp;
    }

    /**
     * Sets the superapp associated with the user ID.
     *
     * @param superapp the superapp to set for the user ID
     */
    public void setSuperapp(String superapp) {
        this.superapp = superapp;
    }

    /**
     * Returns the email address associated with the user ID.
     *
     * @return the email address associated with the user ID.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address associated with the user ID.
     *
     * @param email the email address to set for the user ID
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns a String representation of the UserIdBoundary object
     *
     * @return a String representing the UserIdBoundary object
     */
    @Override
    public String toString() {
        return "UserIdBoundary{" + "superapp='" + superapp + '\'' + ", email='" + email + '\'' + '}';
    }

    /**
     * Compares this UserIdBoundary to the specified object for equality.
     *
     * @param o the object to compare
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserIdBoundary that = (UserIdBoundary) o;
        return Objects.equals(superapp, that.superapp) && Objects.equals(email, that.email);
    }

    /**
     * Returns a hash code value for the UserIdBoundary object.
     *
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(superapp, email);
    }
}
