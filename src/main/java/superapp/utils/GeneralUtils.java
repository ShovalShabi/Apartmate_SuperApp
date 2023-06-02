package superapp.utils;

import superapp.dal.ObjectCrud;
import superapp.dal.UserCrud;
import superapp.data.SuperAppObjectEntity;
import superapp.data.UserEntity;
import superapp.data.UserRole;
import superapp.utils.exceptions.InvalidInputException;
import superapp.utils.exceptions.NotFoundException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;


/**
 * The GeneralUtils class provides various utility methods for validation, such as validating email addresses,
 * roles, usernames, avatars, user IDs, object IDs, types, aliases, activity status, timestamps, and location coordinates.
 * The class also includes a method for checking whether a given URL contains an image. All methods are static and can
 * can be used without instantiating the class.
 */
public class GeneralUtils {

    public static boolean isValidNewUser(String email, String role, String username, String avatar) {
        if (!isValidEmail(email))
            throw new InvalidInputException("Email {%s} Is Invalid".formatted(email));

        if (role == null || !isValidRole(role))
            throw new InvalidInputException("Role {%s} Is Invalid".formatted(role));

        if (username == null || username.isBlank())
            throw new InvalidInputException("Username Can't Be Empty");

        if (avatar == null || avatar.isBlank())
            throw new InvalidInputException("Avatar Can't Be Empty");

        return true;
    }

    /**
     * Checks if a user with the specified userId exists.
     *
     * @param userId   the ID of the user to check
     * @param userCrud the user CRUD repository
     * @return true if the user exists; false otherwise
     * @throws NotFoundException if the user doesn't exist
     */
    public static boolean isValidUser(String userId, UserCrud userCrud) {
        Optional<UserEntity> userOptional = userCrud.findById(userId);
        if (userOptional.isEmpty())
            throw new NotFoundException("User {%s} Doesn't Exist".formatted(userId));

        return true;
    }

    /**
     * Checks if an object with the specified objectId exists and is active.
     *
     * @param objectId   the ID of the object to check
     * @param objectCrud the object CRUD repository
     * @return true if the object exists and is active; false otherwise
     * @throws NotFoundException if the object doesn't exist
     */
    public static boolean isValidObject(String objectId, ObjectCrud objectCrud) {
        Optional<SuperAppObjectEntity> objectOptional = objectCrud.findById(objectId);
        if (objectOptional.isEmpty())
            throw new NotFoundException("Object {%s} Doesn't Exist".formatted(objectId));

        return objectOptional.get().getActive();
    }

    /**
     * Checks if the authenticated user has the expected role for a specific operation.
     *
     * @param userId       The ID of the user.
     * @param expectedRole The expected role for the user.
     * @param userCrud     An instance of the UserCrud class used to retrieve user information.
     * @return True if the user has the expected role, false otherwise.
     * @throws NotFoundException if the user with the specified ID doesn't exist.
     */
    public static boolean isAuthUserOperation(String userId, UserRole expectedRole, UserCrud userCrud) {
        Optional<UserEntity> user = userCrud.findById(userId);
        if (user.isEmpty())
            throw new NotFoundException("User {%s} Doesn't Exist".formatted(userId));
        return user.get().getRole().equals(expectedRole);
    }

    /**
     * Validates an email address using a regular expression.
     *
     * @param email The email address to validate.
     * @return true if the email address is valid, false otherwise.
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) return false;
        String regexPattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
        return Pattern.compile(regexPattern).matcher(email).matches();
    }

    /**
     * Validates a user role by checking if it exists in a pre-defined list.
     *
     * @param role The user role to validate.
     * @return true if the role is valid, false otherwise.
     */
    public static boolean isValidRole(String role) {
        if (role.isBlank()) return false;
        return Arrays.stream(UserRole.values()).anyMatch(eRole -> eRole.toString().equals(role));
    }


    /**
     * Checks whether a URL address contains an image.
     *
     * @param link The URL address to check.
     * @return true if the URL contains an image, false otherwise.
     */
    public static boolean imageChecker(String link) {
        /**
         *@param url address
         * @return: is this address contains an image or not
         */
        try {
            URL imageURL = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) imageURL.openConnection();
            connection.setRequestMethod("HEAD");
            connection.connect();
            String contentType = connection.getContentType();
            return contentType != null && contentType.startsWith("image");
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * Checks whether the given type string contains only letters.
     *
     * @param type the type string to be validated
     * @return true if the type string contains only letters, false otherwise
     */
    public static boolean isValidType(String type) {
        return !(type == null || type.isBlank());
    }

    /**
     * Checks whether the given alias string contains only letters.
     *
     * @param alias the alias string to be validated
     * @return true if the alias string contains only letters, false otherwise
     */
    public static boolean isValidAlias(String alias) {
        return !(alias == null || alias.isBlank());
    }

    /**
     * Checks whether the given isActive string contains only "true" or "false".
     *
     * @param isActive the isActive string to be validated
     * @return true if the isActive string contains only "true" or "false", false otherwise
     */
    public static boolean activeTest(String isActive) {
        ArrayList<String> bool = new ArrayList<String>() {{
            add("true");
            add("false");
        }};
        return bool.contains(isActive.toLowerCase());
    }

    /**
     * Checks if the input latitude and longitude values are within a valid range and not null.
     *
     * @param lat the latitude coordinate value
     * @param lng the longitude coordinate value
     * @return true if the input latitude and longitude values are within a valid range and not null, false otherwise
     */
    public static boolean isValidLocation(Double lat, Double lng) {
        if (lat == null || lng == null)
            return false;
        return (lat >= -90 && lat <= 90) && (lng >= -180 && lng <= 180);
    }

    /**
     * Checks if the input values for superapp, miniapp, and internalCommandId are not null or empty strings, and if the
     * internalCommandId value is numeric.
     *
     * @param miniapp           the miniapp string value
     * @param internalCommandId the internal command id string value
     * @return true if the input values for superapp, miniapp, and internalCommandId are valid, false otherwise
     */
    public static boolean isValidCommandID(String miniapp, String internalCommandId) {
        if (miniapp == null || miniapp.isEmpty())
            return false;
        return internalCommandId != null && !internalCommandId.isEmpty();
    }

    /**
     * Checks if the input command value is not null or empty.
     *
     * @param command the command string value
     * @return true if the input command value is not null or empty, false otherwise
     */
    public static boolean isValidCommand(String command) {
        return command != null && !command.isEmpty();
    }
}
