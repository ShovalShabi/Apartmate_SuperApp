package superapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import superapp.boundaries.user.NewUserBoundary;
import superapp.boundaries.user.UserBoundary;
import superapp.logic.UsersServiceAdvanced;

/**
 * The UserController class handles HTTP requests related to user management.
 * It provides methods for creating new users, logging in existing users, and updating user data.
 */
@RestController
@CrossOrigin
public class UserController {
    private UsersServiceAdvanced usersServiceAdvanced;

    /**
     * Sets the UsersServiceAdvanced dependency using the Autowired annotation.
     *
     * @param usersServiceAdvanced the UsersServiceAdvanced instance to set
     */
    @Autowired
    public void setUsersService(UsersServiceAdvanced usersServiceAdvanced) {
        this.usersServiceAdvanced = usersServiceAdvanced;
    }

    /**
     * Creates a new user with the given user data.
     *
     * @param newUser The user data for the new user.
     * @return The created user data.
     */
    @RequestMapping(path = {"/superapp/users"}, method = {RequestMethod.POST},
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public UserBoundary createUser(@RequestBody NewUserBoundary newUser) {
        return this.usersServiceAdvanced.createUser(newUser);
    }

    /**
     * Logs in a user with the given email and super app.
     *
     * @param superapp The super app for the user.
     * @param email    The email of the user.
     * @return The user data for the logged-in user.
     */
    @RequestMapping(path = {"/superapp/users/login/{superapp}/{email}"},
            method = {RequestMethod.GET},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public UserBoundary login(@PathVariable("superapp") String superapp, @PathVariable("email") String email) {
        return this.usersServiceAdvanced.login(superapp, email);
    }

    /**
     * Updates the user with the given email and super app.
     *
     * @param superapp     The super app for the user.
     * @param email        The email of the user to be updated.
     * @param userToUpdate The new user data for the user.
     */
    @RequestMapping(path = {"/superapp/users/{superapp}/{userEmail}"},
            method = {RequestMethod.PUT},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public void updateUser(@PathVariable("superapp") String superapp, @PathVariable("userEmail") String email, @RequestBody UserBoundary userToUpdate) {
        this.usersServiceAdvanced.updateUser(superapp, email, userToUpdate);
    }
}
