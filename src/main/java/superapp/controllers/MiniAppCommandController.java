package superapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import superapp.boundaries.command.MiniAppCommandBoundary;
import superapp.logic.MiniAppCommandsServiceAdvanced;

import static superapp.utils.Constants.DEFAULT_ASYNC_FLAG;

/**
 * The MiniAppCommandController class provides an endpoint for invoking commands on a specific mini app in the SuperApp system.
 * To use this class, simply create an instance of it and map the desired endpoint to the invokeMiniAppCommand() method, which
 * will handle the incoming HTTP POST request and return the appropriate response.
 */
@RestController
@CrossOrigin
public class MiniAppCommandController {
    private MiniAppCommandsServiceAdvanced miniAppCommandsServiceAdvanced;

    /**
     * Sets the MiniAppCommandsServiceAdvanced used by the UserChatController.
     *
     * @param miniAppCommandsServiceAdvanced the MiniAppCommandsServiceAdvanced to be set
     */
    @Autowired
    public void setMiniAppCommandsService(MiniAppCommandsServiceAdvanced miniAppCommandsServiceAdvanced) {
        this.miniAppCommandsServiceAdvanced = miniAppCommandsServiceAdvanced;
    }

    /**
     * Handles an HTTP POST request to invoke a command for a specific mini app.
     *
     * @param miniAppName The name of the mini app for which the command is being invoked.
     * @param command     The command to be invoked on the mini app.
     * @return The command object that was passed in as a parameter.
     */
    @RequestMapping(
            path = {"/superapp/miniapp/{miniAppName}"},
            method = {RequestMethod.POST},
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    public Object invokeMiniAppCommand(@PathVariable("miniAppName") String miniAppName,
                                       @RequestParam(name = "async", required = false, defaultValue = DEFAULT_ASYNC_FLAG) Boolean asyncFlag,
                                       @RequestBody MiniAppCommandBoundary command) {
        return miniAppCommandsServiceAdvanced.invokeCommand(command, miniAppName, asyncFlag);
    }
}
