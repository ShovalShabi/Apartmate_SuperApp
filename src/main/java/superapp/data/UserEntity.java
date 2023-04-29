package superapp.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;


@Document(collection = "USERS")
public class UserEntity {
    @Id
    private String id; //The superApp associated with the user ID.
    private UserRole role;  //The role of the user.
    private String username; //The username of the user.
    private String avatar;  //The avatar of the user.

    public UserEntity() {
    }

    public UserEntity(String id, String email, UserRole role, String username, String avatar) {
        this.id = id;
        this.role = role;
        this.username = username;
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity entity = (UserEntity) o;
        return Objects.equals(id, entity.id) && role == entity.role && Objects.equals(username, entity.username) && Objects.equals(avatar, entity.avatar);
    }

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

