package superapp.boundaries.user;

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

    public UserIdBoundary getUserId() {
        return userId;
    }

    public void setUserId(UserIdBoundary userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "UserBoundary{" +
                "userId=" + userId +
                ", role='" + role + '\'' +
                ", username='" + username + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
