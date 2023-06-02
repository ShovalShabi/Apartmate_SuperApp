package superapp.converters;

import org.springframework.stereotype.Component;
import superapp.boundaries.user.UserBoundary;
import superapp.boundaries.user.UserIdBoundary;
import superapp.data.UserEntity;
import superapp.data.UserRole;

/**
 * The UserConverter class is responsible for converting UserBoundary objects to UserEntity objects and vice versa.
 */
@Component
public class UserConverter {

    /**
     * Constructs a new UserConverter object.
     */
    public UserConverter() {
    }

    /**
     * Converts a UserBoundary to a UserEntity.
     *
     * @param boundary the UserBoundary to convert
     * @return a UserEntity object
     */
    public UserEntity toEntity(UserBoundary boundary) {
        UserEntity rv = new UserEntity();
        rv.setId(createID(boundary.getUserId()));
        rv.setAvatar(boundary.getAvatar());
        rv.setRole(toEntityAsEnum(boundary.getRole()));
        rv.setUsername(boundary.getUsername());
        return rv;
    }

    /**
     * Converts a UserEntity to a UserBoundary.
     *
     * @param entity the UserEntity to convert
     * @return a UserBoundary object
     */
    public UserBoundary toBoundary(UserEntity entity) {
        UserBoundary rv = new UserBoundary();
        String[] ids = entity.getId().split("\\$");
        rv.setUserId(new UserIdBoundary(ids[0], ids[1]));
        rv.setAvatar(entity.getAvatar());
        rv.setRole(entity.getRole().name());
        rv.setUsername(entity.getUsername());
        return rv;
    }

    /**
     * Creates an ID for a UserBoundary based on its userId.
     *
     * @param userIdBoundary the UserIdBoundary from which to create the ID
     * @return a String representing the created ID
     */
    public String createID(UserIdBoundary userIdBoundary) {
        String idFormat = "%s$%s";
        return idFormat.formatted(
                userIdBoundary.getSuperapp(),
                userIdBoundary.getEmail()
        );
    }

    /**
     * Creates an ID for a UserBoundary based on provided userSuperApp and userEmail.
     *
     * @param userSuperApp the userSuperApp component of the ID
     * @param userEmail    the userEmail component of the ID
     * @return a String representing the created ID
     */
    public String createID(String userSuperApp, String userEmail) {
        String idFormat = "%s$%s";
        return idFormat.formatted(
                userSuperApp,
                userEmail
        );
    }

    /**
     * Converts a String value to a UserRole enum.
     *
     * @param value the String value to convert
     * @return a UserRole enum value
     */
    private UserRole toEntityAsEnum(String value) {
        if (value != null)
            return UserRole.valueOf(value);
        return null;
    }
}
