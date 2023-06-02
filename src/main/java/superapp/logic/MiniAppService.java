package superapp.logic;

import superapp.boundaries.command.MiniAppCommandBoundary;

/**
 * Service interface for handling miniApps.
 */
public interface MiniAppService {
    Object runCommand(MiniAppCommandBoundary command);
}
