package superapp.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import superapp.boundaries.user.NewUserBoundary;
import superapp.boundaries.user.UserBoundary;
import superapp.converters.UserConverter;
import superapp.dal.UserCrud;
import superapp.data.UserEntity;
import superapp.data.UserRole;
import superapp.utils.GeneralUtils;
import superapp.utils.exceptions.AlreadyExistException;
import superapp.utils.exceptions.InvalidInputException;
import superapp.utils.exceptions.NotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UsersServiceDB implements UsersServiceAdvanced {
    private UserCrud userCrud; // CrudRepository
    private UserConverter converter; // Entity/Boundary converter
    private String superapp;

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
        GeneralUtils.isValidNewUser(user.getUserId().getEmail(), user.getRole(), user.getUsername(), user.getAvatar());

        user.getUserId().setSuperapp(this.superapp);
        if (this.userCrud.findById(this.converter.createID(user)).isPresent())
            throw new AlreadyExistException("User {%s} Already Exist".formatted(user.getUserId()));

        this.userCrud.save(this.converter.toEntity(user));
        return user;
    }

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
        if (!userSuperApp.equals(this.superapp))
            throw new InvalidInputException("Invalid SuperApp, got {%s}, expected {%s}"
                    .formatted(userSuperApp, this.superapp));

        Optional<UserEntity> userCheck = this.userCrud.findById(this.converter.createID(userSuperApp, userEmail));
        if (userCheck.isEmpty())
            throw new NotFoundException("User {%s, %s} Doesn't Exist".formatted(userSuperApp, userEmail));

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
        Optional<UserEntity> userCheck = this.userCrud.findById(this.converter.createID(userSuperApp, userEmail));
        if (userCheck.isEmpty()) throw new NotFoundException("User's Key {%s} doesn't exist"
                .formatted(this.converter.createID(userSuperApp, userEmail)));

        UserEntity user = userCheck.get();
        String newRole = update.getRole();
        String newUsername = update.getUsername();
        String newAvatar = update.getAvatar();

        if (newRole != null) {
            if (GeneralUtils.isValidRole(newRole)) user.setRole(UserRole.valueOf(newRole));
            else throw new InvalidInputException("Role {%s} Is Invalid".formatted(newRole));
        }

        if (newUsername != null) {
            if (!newUsername.isEmpty()) user.setUsername(newUsername);
            else throw new InvalidInputException("Username Can't Be Empty");
        }

        if (newAvatar != null) {
            if (!newAvatar.isEmpty()) user.setAvatar(newAvatar);
            else throw new InvalidInputException("Avatar Can't Be Empty");
        }

        this.userCrud.save(user);
        return this.converter.toBoundary(user);
    }

    /**
     * Retrieves all the user entities from the database and converts them to user boundaries using the converter.
     *
     * @return a List of all UserBoundary objects in the database
     */
    @Override
    public List<UserBoundary> getAllUsers() {
        return this.userCrud.findAll().stream().map(this.converter::toBoundary).collect(Collectors.toList());
    }

    /**
     * Deletes all user entities from the database.
     */
    @Override
    public void deleteAllUsers() {
        this.userCrud.deleteAll();
    }
}

