package superapp.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import superapp.boundaries.command.MiniAppCommandBoundary;
import superapp.boundaries.command.MiniAppCommandIdBoundary;
import superapp.converters.MiniAppCommandConverter;
import superapp.dal.MiniAppCommandCrud;
import superapp.data.MiniAppCommandEntity;
import superapp.utils.GeneralUtils;
import superapp.utils.exceptions.InvalidInputException;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MiniAppCommandsServiceDB implements MiniAppCommandsServiceAdvanced {
    private MiniAppCommandCrud miniAppCommandCrud;
    private String superApp;
    private MiniAppCommandConverter commandConverter;

    /**
     * Sets the MiniAppCommandCrud dependency.
     *
     * @param miniAppCommandCrud the MiniAppCommandCrud dependency
     */
    @Autowired
    public void setMiniAppCommandCrud(MiniAppCommandCrud miniAppCommandCrud) {
        this.miniAppCommandCrud = miniAppCommandCrud;
    }

    /**
     * Sets the MiniAppCommandConverter dependency.
     *
     * @param commandConverter the MiniAppCommandConverter dependency
     */
    @Autowired
    public void setCommandConverter(MiniAppCommandConverter commandConverter) {
        this.commandConverter = commandConverter;
    }

    /**
     * Sets the value of the "spring.application.name" property to the "superApp" field.
     *
     * @param superApp the value of the "spring.application.name" property
     */
    @Value("${spring.application.name}")
    public void setSuperApp(String superApp) {
        this.superApp = superApp;
    }

    /**
     * Creates a new MiniAppCommandBoundary object.
     *
     * @param command the MiniAppCommandBoundary object to create
     * @return the newly created MiniAppCommandBoundary object
     */
    //TODO: add internal comments for better comprehending
    @Override
    public Object invokeCommand(MiniAppCommandBoundary command) {
        if (command.getTargetObject() == null)
            throw new InvalidInputException("TargetObject Is Invalid");

        if (command.getInvokedBy() == null)
            throw new InvalidInputException("InvokedBy Is Invalid");

        GeneralUtils.isValidNewCommand(command.getCommand(), command.getTargetObject().getObjectId().getSuperapp(),
                command.getTargetObject().getObjectId().getInternalObjectId(), command.getInvokedBy().getUserId().getSuperapp(),
                command.getInvokedBy().getUserId().getEmail());

        command.setInvocationTimestamp(new Date());

        MiniAppCommandEntity miniAppCommandEntity = this.commandConverter.toEntity(command);
        miniAppCommandEntity = this.miniAppCommandCrud.save(miniAppCommandEntity);
        return this.commandConverter.toBoundary(miniAppCommandEntity);
    }

    @Override
    public Object invokeCommand(MiniAppCommandBoundary command, String miniAppName) {
        command.setCommandId(new MiniAppCommandIdBoundary(
                this.superApp, miniAppName, UUID.randomUUID().toString()));

        return this.invokeCommand(command);
    }

    /**
     * Retrieves all MiniAppCommandBoundary from the database.
     *
     * @return a List of MiniAppCommandBoundary objects in the database
     */
    @Override
    public List<MiniAppCommandBoundary> getAllCommands() {
        return this.miniAppCommandCrud.findAll().stream()
                .map(this.commandConverter::toBoundary)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all MiniAppCommandBoundary of a specific miniApp from the database.
     *
     * @param miniAppName the name of the miniApp
     * @return a List of MiniAppCommandBoundary objects of a specific miniApp from the database
     */
    @Override
    public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniAppName) {
        return this.miniAppCommandCrud.findAll().stream()
                .map(this.commandConverter::toBoundary)
                .filter(boundary -> boundary.getCommandId().getMiniapp().equals(miniAppName))
                .collect(Collectors.toList());
    }

    /**
     * Deletes all MiniAppCommands from the database.
     */
    @Override
    public void deleteAllCommands() {
        this.miniAppCommandCrud.deleteAll();
    }
}
