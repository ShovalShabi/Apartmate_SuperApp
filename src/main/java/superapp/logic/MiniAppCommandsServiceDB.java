package superapp.logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import superapp.boundaries.command.MiniAppCommandBoundary;
import superapp.boundaries.command.MiniAppCommandIdBoundary;
import org.springframework.jms.core.JmsTemplate;
import superapp.converters.MiniAppCommandConverter;
import superapp.converters.ObjectConverter;
import superapp.converters.UserConverter;
import superapp.dal.MiniAppCommandCrud;
import superapp.dal.ObjectCrud;
import superapp.dal.UserCrud;
import superapp.data.MiniAppCommandEntity;
import superapp.data.UserRole;
import superapp.utils.GeneralUtils;
import superapp.utils.exceptions.InvalidInputException;
import superapp.utils.exceptions.MethodNotInUseException;
import superapp.utils.exceptions.UnauthorizedUserOperation;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static superapp.utils.Constants.DEFAULT_SORTING_DIRECTION;

/**
 * Service implementation for managing MiniApp commands in the database.
 */
@Service
public class MiniAppCommandsServiceDB implements MiniAppCommandsServiceAdvanced {
    private final MiniAppCommandCrud miniAppCommandCrud;
    private final ObjectCrud objectCrud;
    private final UserCrud userCrud;
    private String superApp;
    private final MiniAppCommandConverter commandConverter;
    private final UserConverter userConverter;
    private final ObjectConverter objectConverter;
    private ObjectMapper jackson;
    private JmsTemplate jmsTemplate;
    private final ApplicationContext applicationContext;
    private MiniAppService miniAppService;
    private final Log logger = LogFactory.getLog(MiniAppCommandsServiceDB.class);

    @Autowired
    public MiniAppCommandsServiceDB(MiniAppCommandCrud miniAppCommandCrud, ObjectCrud objectCrud, UserCrud userCrud,
                                    MiniAppCommandConverter commandConverter, UserConverter userConverter,
                                    ObjectConverter objectConverter, ApplicationContext applicationContext) {
        this.miniAppCommandCrud = miniAppCommandCrud;
        this.objectCrud = objectCrud;
        this.userCrud = userCrud;
        this.commandConverter = commandConverter;
        this.userConverter = userConverter;
        this.objectConverter = objectConverter;
        this.applicationContext = applicationContext;
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

    @Autowired
    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
        this.jmsTemplate.setDeliveryDelay(3000L);  //Delay set to 3 seconds
    }

    @PostConstruct
    public void setup() {
        this.jackson = new ObjectMapper();
    }

    /**
     * Creates a new MiniAppCommandBoundary object.
     *
     * @param command the MiniAppCommandBoundary object to create
     * @return the newly created MiniAppCommandBoundary object
     */
    @Override
    public Object invokeCommand(MiniAppCommandBoundary command) {
        this.logger.debug("Invoke new command object sync");
        this.validateCommand(command);

        /*TODO: Crate a mini_app command handler*/
        Object commandOutput = this.handleCommand(command);
        this.logger.debug("handled command object sync");

        MiniAppCommandEntity miniAppCommandEntity = this.commandConverter.toEntity(command);
        miniAppCommandEntity.setMiniapp(command.getCommandId().getMiniapp());
        this.miniAppCommandCrud.save(miniAppCommandEntity);
        this.logger.debug("Command was saved");

        if (commandOutput instanceof MiniAppCommandBoundary) {
            this.logger.trace("Command was executed: " + ((MiniAppCommandBoundary) commandOutput).getCommand());
            return "{We executed your command}";
        }
        return commandOutput;
    }

    /**
     * Retrieves all mini app commands.
     *
     * @return a list of all mini app commands
     * @deprecated
     */
    @Override
    @Deprecated
    public List<MiniAppCommandBoundary> getAllCommands() {
        this.logger.error("Method {getAllCommands()} is Deprecated");
        throw new MethodNotInUseException("Method {getAllCommands()} is Deprecated");
    }

    /**
     * Retrieves all mini app commands for a specific mini app.
     *
     * @return a list of all mini app commands for the specified mini app
     * @deprecated
     */
    @Override
    @Deprecated
    public List<MiniAppCommandBoundary> getAllMiniAppCommands() {
        this.logger.error("Method {getAllMiniAppCommands()} is Deprecated");
        throw new MethodNotInUseException("Method {getAllMiniAppCommands()} is Deprecated");
    }

    /**
     * @deprecated Deletes all mini app commands.
     */
    @Override
    @Deprecated
    public void deleteAllCommands() {
        this.logger.error("Method {deleteAllCommands()} is Deprecated");
        throw new MethodNotInUseException("Method {deleteAllCommands()} is Deprecated");
    }

    /**
     * Invokes a MiniApp command synchronously or asynchronously based on the provided flag.
     *
     * @param command     the MiniAppCommandBoundary object representing the command to invoke
     * @param miniAppName the name of the MiniApp
     * @param asyncFlag   flag indicating whether to invoke the command asynchronously
     * @return the result of the command invocation, or null if invoked asynchronously
     */
    @Override
    public Object invokeCommand(MiniAppCommandBoundary command, String miniAppName, Boolean asyncFlag) {
        this.logger.debug("Invoke new command object");
        command.setCommandId(new MiniAppCommandIdBoundary(
                this.superApp, miniAppName, UUID.randomUUID().toString()));
        command.setInvocationTimestamp(new Date());

        if (asyncFlag) {
            this.logger.debug("Command has async flag");
            return handleLater(command);
        }
        this.logger.debug("Command hasn't async flag");
        return this.invokeCommand(command);
    }

    /**
     * Validates the MiniAppCommandBoundary object to ensure it meets the required criteria.
     *
     * @param command the MiniAppCommandBoundary object to validate
     * @throws InvalidInputException     if the command is invalid or contains invalid input
     * @throws UnauthorizedUserOperation if the command is invoked by an unauthorized user
     */
    @Override
    public void validateCommand(MiniAppCommandBoundary command) {
        this.logger.debug("Validate Command");
        if (command.getInvokedBy() == null || command.getInvokedBy().getUserId() == null ||
                command.getInvokedBy().getUserId().getSuperapp().isBlank() ||
                command.getInvokedBy().getUserId().getEmail().isBlank()) {
            this.logger.error("Command's InvokedBy Is Invalid");
            throw new InvalidInputException("InvokedBy Is Invalid");
        }
        String userId = this.userConverter.createID(command.getInvokedBy().getUserId());
        GeneralUtils.isValidUser(userId, userCrud);
        if (!GeneralUtils.isAuthUserOperation(userId, UserRole.MINIAPP_USER, userCrud)) {
            this.logger.error("Unauthorized User: Only MiniApp User Can Invoke Commands");
            throw new UnauthorizedUserOperation("Only MiniApp User Can Invoke Commands");
        }

        if (command.getTargetObject() == null ||
                !GeneralUtils.isValidObject(this.objectConverter.createID(command.getTargetObject().getObjectId()), objectCrud)) {
            this.logger.error("Command Object Is Either Not In The Database Or Not Active");
            throw new InvalidInputException("Object %s Is Either Not In The Database Or Not Active");
        }

        if (command.getCommand() == null || command.getCommand().isBlank()) {
            this.logger.error("Command Can't Be Empty");
            throw new InvalidInputException("Command Can't Be Empty");
        }
        this.logger.trace("Command object has validate");
    }

    /**
     * Handles the command.
     *
     * @return the result of handling the command
     */
    @Override
    public Object handleCommand(MiniAppCommandBoundary command) {
        this.logger.debug("Handling Command");
        String miniApp = command.getCommandId().getMiniapp();

        switch (miniApp) {
            case ("Cart") -> {
                this.logger.debug("Direct Command to Cart MiniApp");
                this.miniAppService = this.applicationContext.getBean("Cart", CartService.class);
            }
            case ("Chat") -> {
                this.logger.debug("Direct Command to Chat MiniApp");
                this.miniAppService = this.applicationContext.getBean("Chat", ChatService.class);
            }
            case ("Professionals") -> {
                this.logger.debug("Direct Command to Professionals MiniApp");
                this.miniAppService = this.applicationContext.getBean("Professionals", ProfessionalService.class);
            }
            default -> {
                this.logger.debug("mini app command is not recognized");
                return command; // In case the mini app command is not recognized
            }
        }
        this.logger.trace("Handled Command finished");
        return this.miniAppService.runCommand(command);
    }


    /**
     * Handles the command later by sending it to the "ApartMateQueue" JMS destination.
     *
     * @param command the MiniAppCommandBoundary object representing the command to handle
     * @return the MiniAppCommandBoundary object that was sent to the queue
     * @throws RuntimeException if an error occurs while converting the command to JSON or sending it to the queue
     */
    @Override
    public MiniAppCommandBoundary handleLater(MiniAppCommandBoundary command) {
        try {
            String json = this.jackson.writeValueAsString(command);
            this.jmsTemplate.convertAndSend("ApartMateQueue", json);
            this.logger.trace("added command to async queue");
            return command;
        } catch (Exception e) {
            this.logger.fatal(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Listens to the "ApartMateQueue" JMS destination and processes the received JSON message.
     *
     * @param json the JSON message received from the JMS destination
     */
    @Override
    @JmsListener(destination = "ApartMateQueue")
    public void listenToMyMom(String json) {
        try {
            MiniAppCommandBoundary command = this.jackson.readValue(json, MiniAppCommandBoundary.class);
            if (command.getCommandId() == null) {
                command.setCommandId(new MiniAppCommandIdBoundary(
                        this.superApp, "APP_NAME_LOST", UUID.randomUUID().toString()));
            }

            if (command.getInvocationTimestamp() == null) {
                command.setInvocationTimestamp(new Date());
            }

            this.validateCommand(command);
            this.handleCommand(command);
            MiniAppCommandEntity miniAppCommandEntity = this.commandConverter.toEntity(command);
            if (!command.getCommandId().getMiniapp().equals("APP_NAME_LOST"))
                miniAppCommandEntity.setMiniapp(command.getCommandId().getMiniapp());
            this.miniAppCommandCrud.save(miniAppCommandEntity);
            this.logger.trace("Command was saved from async queue");
        } catch (Exception e) {
            this.logger.fatal(e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    /**
     * Retrieves all MiniAppCommandBoundary from the database.
     *
     * @return a List of MiniAppCommandBoundary objects in the database
     */
    @Override
    public List<MiniAppCommandBoundary> getAllCommands(String userSuperApp, String userEmail, int size, int page) {
        this.logger.debug("Get all commands");
        String userId = this.userConverter.createID(userSuperApp, userEmail);
        PageRequest pageReq = PageRequest.of(page, size, DEFAULT_SORTING_DIRECTION, "invocationTimestamp", "id");

        if (GeneralUtils.isAuthUserOperation(userId, UserRole.ADMIN, userCrud)) {
            this.logger.trace("Got all commands");
            return this.miniAppCommandCrud
                    .findAll(pageReq)
                    .stream()
                    .map(this.commandConverter::toBoundary)
                    .collect(Collectors.toList());
        } else {
            this.logger.error("Unauthorized User: Only Admin Can Get All Commands");
            throw new UnauthorizedUserOperation("Only Admin Can Get All Commands");
        }
    }

    /**
     * Retrieves all MiniAppCommandBoundary of a specific miniApp from the database.
     *
     * @param miniApp the name of the miniApp
     * @return a List of MiniAppCommandBoundary objects of a specific miniApp from the database
     */
    @Override
    public List<MiniAppCommandBoundary> getAllMiniAppCommands(String miniApp, String userSuperApp, String userEmail, int size, int page) {
        this.logger.debug("Get all MiniApp commands");
        String userId = this.userConverter.createID(userSuperApp, userEmail);
        PageRequest pageReq = PageRequest.of(page, size, DEFAULT_SORTING_DIRECTION, "invocationTimestamp", "id");

        if (GeneralUtils.isAuthUserOperation(userId, UserRole.ADMIN, userCrud)) {
            this.logger.trace("Got all MiniApp commands");
            return this.miniAppCommandCrud
                    .findByMiniapp(miniApp, pageReq)
                    .stream()
                    .map(this.commandConverter::toBoundary)
                    .collect(Collectors.toList());
        } else {
            this.logger.error("Unauthorized User: Only Admin Can Get All MiniApp Commands");
            throw new UnauthorizedUserOperation("Only Admin Can Get All MiniApp Commands");
        }
    }

    /**
     * Deletes all MiniAppCommands from the database.
     */
    @Override
    public void deleteAllCommands(String userSuperApp, String userEmail) {
        this.logger.debug("Delete all commands");

        String userId = this.userConverter.createID(userSuperApp, userEmail);
        if (GeneralUtils.isAuthUserOperation(userId, UserRole.ADMIN, userCrud)) {
            this.miniAppCommandCrud.deleteAll();
            this.logger.trace("Deleted all commands");
        } else {
            this.logger.error("Unauthorized User: Only Admin Can Remove All Commands");
            throw new UnauthorizedUserOperation("Only Admin Can Remove All Commands");
        }
    }
}