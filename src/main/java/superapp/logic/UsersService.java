package superapp.logic;

import superapp.boundaries.user.UserBoundary;

import java.util.List;

/**
 * Service interface for managing users.
 */
public interface UsersService {
    /**
     * Creates a new user.
     *
     * @param user the UserBoundary object representing the user to create
     * @return the UserBoundary object representing the created user
     */
    UserBoundary createUser(UserBoundary user);

    /**
     * Logs in a user with the specified super app and email.
     *
     * @param userSuperApp the super app of the user
     * @param userEmail    the email of the user
     * @return the UserBoundary object representing the logged-in user
     */
    UserBoundary login(String userSuperApp, String userEmail);

    /**
     * Updates an existing user.
     *
     * @param userSuperApp the super app of the user to update
     * @param userEmail    the email of the user to update
     * @param update       the UserBoundary object representing the updates to apply
     * @return the UserBoundary object representing the updated user
     */
    UserBoundary updateUser(String userSuperApp, String userEmail, UserBoundary update);

    /**
     * Retrieves all users.
     *
     * @return a list of UserBoundary objects representing all the users
     */
    List<UserBoundary> getAllUsers();

    /**
     * Deletes all users.
     */
    void deleteAllUsers();

}
