package superapp.logic;

import superapp.boundaries.command.MiniAppCommandBoundary;

import java.util.List;

/**
 * The MiniAppCommandsService interface provides methods for managing mini app commands.
 */
public interface MiniAppCommandsService {
    /**
     * Invokes the specified mini app command.
     *
     * @param command the mini app command to invoke
     * @return the result of the command invocation
     */
    Object invokeCommand(MiniAppCommandBoundary command);

    /**
     * Retrieves all mini app commands.
     *
     * @return a list of all mini app commands
     */
    List<MiniAppCommandBoundary> getAllCommands();

    /**
     * Retrieves all mini app commands for a specific mini app.
     *
     * @return a list of all mini app commands for the specified mini app
     */
    List<MiniAppCommandBoundary> getAllMiniAppCommands();

    /**
     * Deletes all mini app commands.
     */
    void deleteAllCommands();
}
