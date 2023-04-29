package superapp.converters;

import org.springframework.stereotype.Component;
import superapp.boundaries.user.UserBoundary;
import superapp.boundaries.user.UserIdBoundary;
import superapp.data.UserEntity;
import superapp.data.UserRole;

@Component
public class UserConverter {
    public UserConverter() { }

    public UserEntity toEntity(UserBoundary boundary) {
        UserEntity rv = new UserEntity();
        rv.setId(createID(boundary));
        rv.setAvatar(boundary.getAvatar());
        rv.setRole(toEntityAsEnum(boundary.getRole()));
        rv.setUsername(boundary.getUsername());
        return rv;
    }

    public UserBoundary toBoundary(UserEntity entity) {
        UserBoundary rv = new UserBoundary();
        String[] ids = entity.getId().split("\\$");
        rv.setUserId(new UserIdBoundary(ids[0], ids[1]));
        rv.setAvatar(entity.getAvatar());
        rv.setRole(entity.getRole().name());
        rv.setUsername(entity.getUsername());
        return rv;
    }

    public String createID(UserBoundary userBoundary) {
        String idFormat = "%s$%s";
        return idFormat.formatted(
                userBoundary.getUserId().getSuperapp(),
                userBoundary.getUserId().getEmail()
        );
    }

    public String createID(String userSuperApp, String userEmail) {
        String idFormat = "%s$%s";
        return idFormat.formatted(
                userSuperApp,
                userEmail
        );
    }

    private UserRole toEntityAsEnum (String value) {
        if (value != null)
            return UserRole.valueOf(value);
        return null;
    }
}
