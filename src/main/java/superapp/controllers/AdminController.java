package superapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import superapp.boundaries.command.MiniAppCommandBoundary;
import superapp.boundaries.user.UserBoundary;
import superapp.logic.*;

import static superapp.utils.Constants.DEFAULT_PAGE;
import static superapp.utils.Constants.DEFAULT_PAGE_SIZE;

/**
 * The AdminController class provides several REST API endpoints for managing the SuperApp system as an admin.
 * These endpoints include exporting a list of all users, exporting a list of all miniapp commands, exporting a list of miniapp
 * commands for a specific miniapp, deleting all users, deleting all objects, and deleting all commands.
 */
@RestController
@CrossOrigin
public class AdminController {
    private UsersServiceAdvanced usersService;
    private MiniAppCommandsServiceAdvanced miniAppCommandsService;
    private ObjectsServiceAdvanced objectsService;

    /**
     * Sets the UsersService used by the class.
     *
     * @param usersService the UsersService to be set
     */
    @Autowired
    public void setUsersService(UsersServiceAdvanced usersService) {
        this.usersService = usersService;
    }

    /**
     * Sets the MiniAppCommandsService used by the class.
     *
     * @param miniAppCommandsService the MiniAppCommandsService to be set
     */
    @Autowired
    public void setMiniAppCommandsService(MiniAppCommandsServiceAdvanced miniAppCommandsService) {
        this.miniAppCommandsService = miniAppCommandsService;
    }

    /**
     * Sets the ObjectsService used by the class.
     *
     * @param objectsService the ObjectsService to be set
     */
    @Autowired
    public void setObjectsService(ObjectsServiceAdvanced objectsService) {
        this.objectsService = objectsService;
    }

    /**
     * Exports a list of all users in the SuperApp.
     *
     * @return A list of UserBoundary objects representing all users in the SuperApp.
     */
    @RequestMapping(
            path = {"/superapp/admin/users"},
            method = {RequestMethod.GET},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public UserBoundary[] exportUsers(
            @RequestParam(name = "userSuperapp", required = true, defaultValue = "") String userSuperapp,
            @RequestParam(name = "userEmail", required = true, defaultValue = "") String userEmail,
            @RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE) int page) {
        return this.usersService.getAllUsers(userSuperapp, userEmail, size, page).toArray(new UserBoundary[0]);
    }

    /**
     * Exports a list of all miniapp commands in the SuperApp.
     *
     * @return A list of MiniAppCommandBoundary objects representing all miniapp commands in the SuperApp.
     */
    @RequestMapping(
            path = {"/superapp/admin/miniapp"},
            method = {RequestMethod.GET},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public MiniAppCommandBoundary[] exportAllMiniAppsCommands(
            @RequestParam(name = "userSuperapp", required = true, defaultValue = "") String userSuperapp,
            @RequestParam(name = "userEmail", required = true, defaultValue = "") String userEmail,
            @RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE) int page) {
        return this.miniAppCommandsService.getAllCommands(userSuperapp, userEmail, size, page).toArray(new MiniAppCommandBoundary[0]);
    }

    /**
     * Exports a list of miniapp commands for a specific miniapp in the SuperApp.
     *
     * @param miniapp - The name of the miniapp to export commands for.
     * @return A list of MiniAppCommandBoundary objects representing the commands for the specified miniapp.
     */
    @RequestMapping(
            path = {"/superapp/admin/miniapp/{miniAppName}"},
            method = {RequestMethod.GET},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public MiniAppCommandBoundary[] exportMiniAppCommands(
            @PathVariable("miniAppName") String miniapp,
            @RequestParam(name = "userSuperapp", required = true, defaultValue = "") String userSuperapp,
            @RequestParam(name = "userEmail", required = true, defaultValue = "") String userEmail,
            @RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE) int page) {
        return this.miniAppCommandsService.getAllMiniAppCommands(miniapp, userSuperapp, userEmail, size, page).toArray(new MiniAppCommandBoundary[0]);
    }

    /**
     * Deletes all users in the system.
     */
    @RequestMapping(
            path = {"/superapp/admin/users"},
            method = {RequestMethod.DELETE})
    public void deleteUsers(
            @RequestParam(name = "userSuperapp", required = true, defaultValue = "") String userSuperapp,
            @RequestParam(name = "userEmail", required = true, defaultValue = "") String userEmail) {
        this.usersService.deleteAllUsers(userSuperapp, userEmail);
    }

    /**
     * Deletes all objects in the system.
     */
    @RequestMapping(
            path = {"/superapp/admin/objects"},
            method = {RequestMethod.DELETE})
    public void deleteObjects(
            @RequestParam(name = "userSuperapp", required = true, defaultValue = "") String userSuperapp,
            @RequestParam(name = "userEmail", required = true, defaultValue = "") String userEmail) {
        this.objectsService.deleteAllObjects(userSuperapp, userEmail);
    }

    /**
     * Deletes all commands in the system.
     */
    @RequestMapping(
            path = {"/superapp/admin/miniapp"},
            method = {RequestMethod.DELETE})
    public void deleteCommands(
            @RequestParam(name = "userSuperapp", required = true, defaultValue = "") String userSuperapp,
            @RequestParam(name = "userEmail", required = true, defaultValue = "") String userEmail) {
        this.miniAppCommandsService.deleteAllCommands(userSuperapp, userEmail);
    }
}
