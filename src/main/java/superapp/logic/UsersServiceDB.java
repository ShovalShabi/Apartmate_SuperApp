package superapp.logic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import superapp.boundaries.user.NewUserBoundary;
import superapp.boundaries.user.UserBoundary;
import superapp.converters.UserConverter;
import superapp.dal.UserCrud;
import superapp.data.UserEntity;
import superapp.data.UserRole;
import superapp.utils.GeneralUtils;
import superapp.utils.exceptions.*;

import java.util.*;
import java.util.stream.Collectors;

import static superapp.logic.ChatService.*;
import static superapp.utils.Constants.DEFAULT_SORTING_DIRECTION;


/**
 * The UsersServiceDB class is an implementation of the UsersServiceAdvanced interface. It provides database-specific
 * functionality for managing users.
 * This class is annotated with the Spring framework's @Service annotation, indicating that it is a service component.
 * It can be injected into other components or used as a dependency for managing user-related operations in the application.
 */
@Service
public class UsersServiceDB implements UsersServiceAdvanced {
    private UserCrud userCrud; // CrudRepository
    private UserConverter converter; // Entity/Boundary converter
    private String superapp;
    private final Log logger = LogFactory.getLog(UsersServiceDB.class);

    /**
     * Sets the ObjectCrud dependency.
     *
     * @param userCrud the ObjectCrud dependency
     */
    @Autowired
    public void setUserCrud(UserCrud userCrud) {
        this.userCrud = userCrud;
    }

    /**
     * Sets the ObjectConverter dependency.
     *
     * @param converter the ObjectConverter dependency
     */
    @Autowired
    public void setConverter(UserConverter converter) {
        this.converter = converter;
    }

    /**
     * Sets the value of the "spring.application.name" property to the "superapp" field.
     *
     * @param superapp The value of the "spring.application.name" property.
     */
    @Value("${spring.application.name}")
    public void setSuperapp(String superapp) {
        this.superapp = superapp;
    }

    /**
     * Creates a new UserBoundary object in the database.
     *
     * @param user The UserBoundary object to be created.
     * @return The created UserBoundary object.
     * @throws InvalidInputException if the email, avatar, role, or username in the user object are not valid.
     * @throws AlreadyExistException if a user with the same email already exists.
     */
    @Override
    public UserBoundary createUser(UserBoundary user) {
        this.logger.debug("-Creating new UserBoundary object");
        GeneralUtils.isValidNewUser(user.getUserId().getEmail(), user.getRole(), user.getUsername(), user.getAvatar());

        user.getUserId().setSuperapp(this.superapp);
        if (this.userCrud.findById(this.converter.createID(user.getUserId())).isPresent()) {
            this.logger.error("-User {%s} Already Exist".formatted(user.getUserId()));
            throw new AlreadyExistException("User {%s} Already Exist".formatted(user.getUserId()));
        }
        this.userCrud.save(this.converter.toEntity(user));
        this.logger.trace("-Created New UserBoundary object ");

        return user;
    }

    /**
     * Creates a new user with the provided user data.
     *
     * @param user The user data for creating a new user.
     * @return The created user.
     */
    @Override
    public UserBoundary createUser(NewUserBoundary user) {
        return this.createUser(new UserBoundary(user.getEmail(), user.getRole(), user.getUsername(), user.getAvatar()));
    }

    /**
     * Returns the UserBoundary object with the given userSuperApp and userEmail.
     *
     * @param userSuperApp The superapp of the user.
     * @param userEmail    The email of the user.
     * @return The UserBoundary object with the given userSuperApp and userEmail.
     * @throws InvalidInputException if userSuperApp doesn't match app superapp.
     * @throws NotFoundException     if no user is found with the given userSuperApp and userEmail.
     */
    @Override
    public UserBoundary login(String userSuperApp, String userEmail) {
        this.logger.debug("Trying to login");
        if (!userSuperApp.equals(this.superapp)) {
            this.logger.error("Invalid SuperApp, got {%s}, expected {%s}"
                    .formatted(userSuperApp, this.superapp));
            throw new InvalidInputException("Invalid SuperApp, got {%s}, expected {%s}"
                    .formatted(userSuperApp, this.superapp));
        }

        Optional<UserEntity> userCheck = this.userCrud.findById(this.converter.createID(userSuperApp, userEmail));
        if (userCheck.isEmpty()) {
            this.logger.error("User {%s, %s} Doesn't Exist".formatted(userSuperApp, userEmail));
            throw new NotFoundException("User {%s, %s} Doesn't Exist".formatted(userSuperApp, userEmail));
        }

        this.logger.trace("Login succeed");
        return this.converter.toBoundary(userCheck.get());
    }

    /**
     * Updates the UserEntity object with the given userSuperApp and userEmail using the fields in the given UserBoundary object.
     *
     * @param userSuperApp The superapp of the user to be updated.
     * @param userEmail    The email of the user to be updated.
     * @param update       The UserBoundary object containing the fields to be updated.
     * @return The updated UserBoundary object.
     * @throws InvalidInputException if userSuperApp doesn't match app superapp or any non-valid user information.
     * @throws NotFoundException     if no user is found with the given userSuperApp and userEmail.
     */
    @Override
    public UserBoundary updateUser(String userSuperApp, String userEmail, UserBoundary update) {
        this.logger.debug("Updating a UserBoundary object");
        Optional<UserEntity> userCheck = this.userCrud.findById(this.converter.createID(userSuperApp, userEmail));
        if (userCheck.isEmpty()) {
            this.logger.error("User's Key {%s} doesn't exist"
                    .formatted(this.converter.createID(userSuperApp, userEmail)));
            throw new NotFoundException("User's Key {%s} doesn't exist"
                    .formatted(this.converter.createID(userSuperApp, userEmail)));
        }

        UserEntity user = userCheck.get();
        String newRole = update.getRole();
        String newUsername = update.getUsername();
        String newAvatar = update.getAvatar();

        if (newRole != null) {
            if (GeneralUtils.isValidRole(newRole)) user.setRole(UserRole.valueOf(newRole));
            else {
                this.logger.error("Role {%s} Is Invalid".formatted(newRole));
                throw new InvalidInputException("Role {%s} Is Invalid".formatted(newRole));
            }
        }

        if (newUsername != null) {
            if (!newUsername.isEmpty()) user.setUsername(newUsername);
            else {
                this.logger.error("Username Can't Be Empty");
                throw new InvalidInputException("Username Can't Be Empty");
            }
        }

        if (newAvatar != null) {
            if (!newAvatar.isEmpty()) user.setAvatar(newAvatar);
            else {
                this.logger.error("Avatar Can't Be Empty");
                throw new InvalidInputException("Avatar Can't Be Empty");
            }
        }

        this.userCrud.save(user);
        this.logger.trace("Updated a UserBoundary object");
        return this.converter.toBoundary(user);
    }

    /**
     * Retrieves all users.
     *
     * @return a list of UserBoundary objects representing all the users
     * @deprecated
     */
    @Override
    @Deprecated
    public List<UserBoundary> getAllUsers() {
        this.logger.error("Method {getAllUsers()} is Deprecated");
        throw new MethodNotInUseException("Method {getAllUsers()} is Deprecated");
    }

    /**
     * @deprecated Deletes all users.
     */
    @Override
    @Deprecated
    public void deleteAllUsers() {
        this.logger.error("Method {deleteAllUsers()} is Deprecated");
        throw new MethodNotInUseException("Method {deleteAllUsers()} is Deprecated");
    }

    /**
     * Retrieves all the user entities from the database and converts them to user boundaries using the converter.
     *
     * @return a List of all UserBoundary objects in the database
     */
    @Override
    public List<UserBoundary> getAllUsers(String userSuperApp, String userEmail, int size, int page) {
        this.logger.debug("Getting all UserBoundary objects");
        String userId = this.converter.createID(userSuperApp, userEmail);
        PageRequest pageReq = PageRequest.of(page, size, DEFAULT_SORTING_DIRECTION, "id", "role");

        if (GeneralUtils.isAuthUserOperation(userId, UserRole.ADMIN, userCrud)) {
            this.logger.trace("Got all UserBoundary objects");
            return this.userCrud
                    .findAll(pageReq)
                    .stream()
                    .map(this.converter::toBoundary)
                    .collect(Collectors.toList());
        } else {
            this.logger.error("Unauthorized User: Only Admin Can Get All Users");
            throw new UnauthorizedUserOperation("Only Admin Can Get All Users");
        }
    }

    /**
     * Deletes all user entities from the database.
     */
    @Override
    public void deleteAllUsers(String userSuperApp, String userEmail) {
        this.logger.debug("Deleting all UserBoundary objects");
        String userId = this.converter.createID(userSuperApp, userEmail);
        if (GeneralUtils.isAuthUserOperation(userId, UserRole.ADMIN, userCrud)) {
            this.userCrud.deleteAll();
            this.logger.trace("Delete All Users Succeed");
        } else {
            this.logger.error("Unauthorized User: Only Admin Can Remove All Users");
            throw new UnauthorizedUserOperation("Only Admin Can Remove All Users");
        }
    }
}

