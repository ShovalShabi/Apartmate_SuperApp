package superapp.logic;

import superapp.boundaries.command.MiniAppCommandBoundary;

import java.util.List;

/**
 * Advanced interface for managing MiniApp commands.
 */
public interface MiniAppCommandsServiceAdvanced extends MiniAppCommandsService {
    /**
     * Invokes a MiniApp command with the specified parameters.
     *
     * @param command     the MiniAppCommandBoundary object representing the command to invoke
     * @param miniAppName the name of the mini app
     * @param asyncFlag   a boolean indicating whether to handle the command asynchronously
     * @return the result of invoking the command
     */
    Object invokeCommand(MiniAppCommandBoundary command, String miniAppName, Boolean asyncFlag);

    /**
     * Validates a MiniApp command.
     *
     * @param command the MiniAppCommandBoundary object representing the command to validate
     */
    void validateCommand(MiniAppCommandBoundary command);

    /**
     * Handles a MiniApp command.
     *
     * @return the result of handling the command
     */
    Object handleCommand(MiniAppCommandBoundary command);

    /**
     * Handles a MiniApp command at a later time.
     *
     * @param command the MiniAppCommandBoundary object representing the command to handle later
     * @return the MiniAppCommandBoundary object representing the handled command
     */
    MiniAppCommandBoundary handleLater(MiniAppCommandBoundary command);

    /**
     * Listens to a message from the "ApartMateQueue".
     *
     * @param json the JSON message received
     */
    void listenToMyMom(String json);

    /**
     * Retrieves all mini app commands.
     *
     * @return a list of all mini app commands
     */
    List<MiniAppCommandBoundary> getAllCommands(String userSuperApp, String userEmail, int size, int page);

    /**
     * Retrieves all mini app commands for a specific mini app.
     *
     * @param miniAppName the name of the mini app
     * @return a list of all mini app commands for the specified mini app
     */
    List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName, String userSuperApp, String userEmail, int size, int page);

    /**
     * Deletes all mini app commands.
     */
    void deleteAllCommands(String userSuperApp, String userEmail);
}
