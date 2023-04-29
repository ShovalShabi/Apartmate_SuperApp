package superapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import superapp.boundaries.command.MiniAppCommandBoundary;
import superapp.boundaries.user.UserBoundary;
import superapp.logic.MiniAppCommandsService;
import superapp.logic.ObjectsService;
import superapp.logic.UsersService;

import java.util.Arrays;

/**
 * The AdminController class provides several REST API endpoints for managing the SuperApp system as an admin.
 * These endpoints include exporting a list of all users, exporting a list of all miniapp commands, exporting a list of miniapp
 * commands for a specific miniapp, deleting all users, deleting all objects, and deleting all commands.
 */
@RestController
public class AdminController {
    private UsersService usersService;
    private MiniAppCommandsService miniAppCommandsService;
    private ObjectsService objectsService;

    @Autowired
    public void setUsersService(UsersService usersService) {
        this.usersService = usersService;
    }

    @Autowired
    public void setMiniAppCommandsService(MiniAppCommandsService miniAppCommandsService) {
        this.miniAppCommandsService = miniAppCommandsService;
    }

    @Autowired
    public void setObjectsService(ObjectsService objectsService) {
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
    public UserBoundary[] exportUsers() {
        return this.usersService.getAllUsers().toArray(new UserBoundary[0]);
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
    public MiniAppCommandBoundary[] exportAllMiniAppsCommands() {
        return this.miniAppCommandsService.getAllCommands().toArray(new MiniAppCommandBoundary[0]);
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
    public MiniAppCommandBoundary[] exportMiniAppCommands(@PathVariable("miniAppName") String miniapp) {
        return this.miniAppCommandsService.getAllMiniAppCommands(miniapp).toArray(new MiniAppCommandBoundary[0]);
    }

    /**
     * Deletes all users in the system.
     */
    @RequestMapping(
            path = {"/superapp/admin/users"},
            method = {RequestMethod.DELETE})
    public void deleteUsers() {
        this.usersService.deleteAllUsers();
    }

    /**
     * Deletes all objects in the system.
     */
    @RequestMapping(
            path = {"/superapp/admin/objects"},
            method = {RequestMethod.DELETE})
    public void deleteObjects() {
        this.objectsService.deleteAllObjects();
    }

    /**
     * Deletes all commands in the system.
     */
    @RequestMapping(
            path = {"/superapp/admin/miniapp"},
            method = {RequestMethod.DELETE})
    public void deleteCommands() {
        this.miniAppCommandsService.deleteAllCommands();
    }
}
