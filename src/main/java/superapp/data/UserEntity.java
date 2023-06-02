package superapp.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

/**
 * The UserEntity class represents a user entity in the application.
 * It is annotated with `@Document` to specify the MongoDB collection name.
 */
@Document(collection = "USERS")
public class UserEntity {
    @Id
    private String id; //The superApp associated with the user ID.
    private UserRole role;  //The role of the user.
    private String username; //The username of the user.
    private String avatar;  //The avatar of the user.

    /**
     * Default constructor.
     */
    public UserEntity() {
    }

    /**
     * Constructs a UserEntity with the specified parameters.
     *
     * @param id       the superApp associated with the user ID
     * @param role     the role of the user
     * @param username the username of the user
     * @param avatar   the avatar of the user
     */
    public UserEntity(String id, String email, UserRole role, String username, String avatar) {
        this.id = id;
        this.role = role;
        this.username = username;
        this.avatar = avatar;
    }

    /**
     * Retrieves the ID of the user.
     *
     * @return the ID of the user
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the user.
     *
     * @param id the ID of the user
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Retrieves the role of the user.
     *
     * @return the role of the user
     */
    public UserRole getRole() {
        return role;
    }

    /**
     * Sets the role of the user.
     *
     * @param role the role of the user
     */
    public void setRole(UserRole role) {
        this.role = role;
    }

    /**
     * Retrieves the username of the user.
     *
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     *
     * @param username the username of the user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Retrieves the avatar of the user.
     *
     * @return the avatar of the user
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * Sets the avatar of the user.
     *
     * @param avatar the avatar of the user
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * Compares the UserEntity with another object for equality.
     *
     * @param o the object to compare
     * @return true if the UserEntity is equal to the object, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity entity = (UserEntity) o;
        return Objects.equals(id, entity.id) && role == entity.role && Objects.equals(username, entity.username) && Objects.equals(avatar, entity.avatar);
    }

    /**
     * Generates a string representation of the UserEntity.
     *
     * @return a string representation of the UserEntity
     */
    @Override
    public String toString() {
        return "UserEntity{" +
                "id='" + id + '\'' +
                ", role=" + role +
                ", username='" + username + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}

