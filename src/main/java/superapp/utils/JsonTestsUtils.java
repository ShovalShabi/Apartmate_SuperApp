package superapp.utils;

import org.json.JSONException;
import org.json.JSONObject;
import static superapp.utils.GeneralUtils.*;

/**
 * The main.java.superapp.utils.Json_Utils class provides utility functions to validate JSON strings for different types of
 * superapp.Boundaries such as UserBoundary, SuperApp ObjectBoundary, and MiniApp Command Boundary.
 */
public class JsonTestsUtils {
    /**
     * This method validates UserBoundary JSON string.
     *
     * @param jsonString JSON string for UserBoundary
     * @throws Exception if exists any errors with all errors in th msg
     */
    public void isValidateUserBoundaryJson(String jsonString) throws Exception {
        String errors = "";
        try {
            JSONObject json = new JSONObject(jsonString); // parse JSON string

            // validate userId object
            JSONObject userId = json.getJSONObject("userId");
            String email = userId.getString("email");
            if (!isValidEmail(email))
                errors += "\nUserID is not Valid";

            // validate role
            String role = json.getString("role");
            if (!isValidRole(role))
                errors += "\nRole is not Valid";

            if (!errors.equals(""))
                throw new Exception("User Boundary Json is not valid\n" + errors);

        } catch (JSONException e) {
            throw new Exception("User Boundary Json had problems errors:\n" + errors);
        }
    }

    /**
     * This method validates SuperApp ObjectBoundary JSON string.
     *
     * @param jsonString JSON string for SuperApp ObjectBoundary
     * @throws Exception if exists any errors with all errors in th msg
     */
    public void isValidateSuperAppObjectBoundaryJson(String jsonString) throws Exception {
        String errors = "";

        // parse JSON string
        JSONObject json = new JSONObject(jsonString);
        // validate type
        String type = json.getString("type");
        if (!isValidType(type))
            errors += "\nType is not Valid";

        // validate alias
        String alias = json.getString("alias");
        if (!isValidAlias(alias))
            errors += "\nAlias is not Valid";

        // validate active
        boolean active = json.getBoolean("active");
        //errors += "\nActive is not Valid"; TODO check if boolean has problems when getting not boolean

        // validate creationTimestamp
//        String creationTimestamp = json.getString("creationTimestamp");
//        if (!isValidDate(creationTimestamp))
//            errors += "\nDate is not Valid";

        // validate location object
        JSONObject location = json.getJSONObject("location");
        double lat = location.getDouble("lat");
        double lng = location.getDouble("lng");
        if (!isValidLocation(lat, lng))
            errors += "\nLocation is not Valid";

        // validate createdBy object
        JSONObject createdBy = json.getJSONObject("createdBy");
        JSONObject userId = createdBy.getJSONObject("userId");
        String email = userId.getString("email");
        if (!isValidEmail(email))
            errors += "\nUserID in Created By is not Valid";

        // validate Object Details
        //TODO how to validate Object Details

        if (!errors.equals(""))
            throw new Exception("SuperApp Object Boundary Json is not valid\n" + errors);


    }

    /**
     * This method validates MiniApp Command Boundary JSON string.
     *
     * @param jsonString JSON string for MiniApp Command Boundary
     * @throws Exception if exists any errors with all errors in th msg
     */
    public void isValidateMiniAppCommandBoundaryJson(String jsonString) throws Exception {
        String errors = "";
        try {
            // parse JSON string
            JSONObject json = new JSONObject(jsonString);

            // validate commandID object
            JSONObject commandId = json.getJSONObject("commandId");
            String miniapp = commandId.getString("miniapp");
            String internalCommandId = commandId.getString("internalCommandId");
            if (!isValidCommandID(miniapp, internalCommandId))
                errors += "\nCommandID is not Valid";

            // validate command object
            String command = json.getString("command");
            if (!isValidCommand(command))
                errors += "\nCommand is not Valid";

            // validate invokedBy
            JSONObject invokedBy = json.getJSONObject("invokedBy");
            JSONObject userId = invokedBy.getJSONObject("userId");
            String email = userId.getString("email");
            if (!isValidEmail(email))
                errors += "\nUser email is not Valid";

            if (!errors.equals(""))
                throw new Exception("User Boundary Json is not valid\n" + errors);

        } catch (JSONException e) {
            throw new Exception("MiniApp Command Boundary Json had problems errors:\n" + errors);
        }
    }
}
