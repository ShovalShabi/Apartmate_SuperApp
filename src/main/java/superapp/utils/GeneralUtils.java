package superapp.utils;

import superapp.data.UserRole;
import superapp.utils.exceptions.InvalidInputException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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

    public static boolean isValidNewCommand(String command, String objectSuperApp, String internalObjectId,
                                            String userSuperApp, String userEmail) {
        if (command == null || command.isBlank())
            throw new InvalidInputException("Command Can't Be Empty");

        if (objectSuperApp == null || objectSuperApp.isBlank())
            throw new InvalidInputException("objectSuperApp Can't Be Empty");

        if (internalObjectId == null || internalObjectId.isBlank())
            throw new InvalidInputException("InternalObjectId Can't Be Empty");

        if (userSuperApp == null || userSuperApp.isBlank())
            throw new InvalidInputException("UserSuperApp Can't Be Empty");

        if (!isValidEmail(userEmail))
            throw new InvalidInputException("Email {%s} Is Invalid".formatted(userEmail));

        return true;
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
        if (type == null)
            return false;
        String regexPattern = "^[a-zA-Z ]+$";
        return Pattern.compile(regexPattern).matcher(type).matches();
    }

    /**
     * Checks whether the given alias string contains only letters.
     *
     * @param alias the alias string to be validated
     * @return true if the alias string contains only letters, false otherwise
     */
    public static boolean isValidAlias(String alias) {
        if (alias == null)
            return false;
        String regexPattern = "^[a-zA-Z ]+$";
        return Pattern.compile(regexPattern).matcher(alias).matches();
    }

    /**
     * Checks whether the given isActive string contains only "true" or "false".
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
