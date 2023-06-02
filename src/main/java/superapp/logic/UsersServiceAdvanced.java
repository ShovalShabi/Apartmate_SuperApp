package superapp.logic;

import superapp.boundaries.user.NewUserBoundary;
import superapp.boundaries.user.UserBoundary;

import java.util.List;

/**
 * The UsersServiceAdvanced interface extends the UsersService interface and provides additional advanced functionality
 * for managing users.
 */
public interface UsersServiceAdvanced extends UsersService {

    /**
     * Creates a new user with the provided user data.
     *
     * @param user The user data for creating a new user.
     * @return The created user.
     * @throws RuntimeException if an error occurs while creating the user.
     */
    UserBoundary createUser(NewUserBoundary user);

    /**
     * Retrieves all users.
     *
     * @return a list of UserBoundary objects representing all the users
     */
    List<UserBoundary> getAllUsers(String userSuperApp, String userEmail, int size, int page);

    /**
     * Deletes all users.
     */
    void deleteAllUsers(String userSuperApp, String userEmail);
}
