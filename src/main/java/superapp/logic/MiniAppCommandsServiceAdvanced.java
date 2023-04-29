package superapp.logic;

import superapp.boundaries.command.MiniAppCommandBoundary;

public interface MiniAppCommandsServiceAdvanced extends MiniAppCommandsService{
    Object invokeCommand(MiniAppCommandBoundary command, String miniAppName);
}
