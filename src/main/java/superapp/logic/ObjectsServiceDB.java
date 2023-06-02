package superapp.logic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;
import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.boundaries.object.SuperAppObjectIdBoundary;
import superapp.boundaries.user.UserIdBoundary;
import superapp.converters.ObjectConverter;
import superapp.converters.UserConverter;
import superapp.dal.ObjectCrud;
import superapp.dal.UserCrud;
import superapp.data.SuperAppObjectEntity;
import superapp.data.UserRole;
import superapp.utils.GeneralUtils;
import superapp.utils.exceptions.*;

import java.util.*;
import java.util.stream.Collectors;

import static superapp.utils.Constants.*;

/**
 * This class is responsible for providing all business logic functionality related to the ObjectsService.
 * It implements the ObjectsService interface, which defines the main functions related to creating, retrieving, updating,
 * and deleting SuperAppObjectBoundary objects.
 * <p>
 * This class uses Spring annotations to perform dependency injection of its collaborators:
 * - ObjectCrud: A CRUD repository for SuperAppObjectEntity objects.
 * - ObjectConverter: A converter class for converting SuperAppObjectEntity objects to and from SuperAppObjectBoundary objects.
 * <p>
 * This class also uses the @Value annotation to inject the value of the "spring.application.name" property into the "superapp" field.
 */
@Service
public class ObjectsServiceDB implements ObjectsServiceAdvanced {

    private ObjectCrud objectCrud;  //CRUD object
    private UserCrud userCrud;
    private ObjectConverter objectConverter;    //Converter for SuperAppObjectBoundary to SuperAppObjectEntity and the opposite
    private UserConverter userConverter;
    private String superapp;    //The identifier of the super app
    private final Log logger = LogFactory.getLog(ObjectsServiceDB.class);

    /**
     * Sets the ObjectCrud dependency.
     *
     * @param objectCrud the ObjectCrud dependency
     */
    @Autowired
    public void setObjectCrud(ObjectCrud objectCrud) {
        this.objectCrud = objectCrud;
    }

    /**
     * Sets the setUserCrud dependency.
     *
     * @param userCrud the UserCrud dependency
     */
    @Autowired
    public void setUserCrud(UserCrud userCrud) {
        this.userCrud = userCrud;
    }

    /**
     * Sets the ObjectConverter dependency.
     *
     * @param objectConverter the ObjectConverter dependency
     */
    @Autowired
    public void setObjectConverter(ObjectConverter objectConverter) {
        this.objectConverter = objectConverter;
    }

    /**
     * Sets the UserConverter dependency.
     *
     * @param userConverter the UserConverter dependency
     */
    @Autowired
    public void setUserConverter(UserConverter userConverter) {
        this.userConverter = userConverter;
    }

    /**
     * Sets the value of the "spring.application.name" property to the "superapp" field.
     *
     * @param superapp the value of the "spring.application.name" property
     */
    @Value("${spring.application.name}")
    public void setSuperapp(String superapp) {
        this.superapp = superapp;
    }


    /**
     * Creates a new SuperAppObjectBoundary object.
     *
     * @param object the SuperAppObjectBoundary object to create
     * @return the newly created SuperAppObjectBoundary object
     */
    @Override
    public SuperAppObjectBoundary createObject(SuperAppObjectBoundary object) {
        this.logger.debug("Creating new SuperAppObjectBoundary object");
        if (!GeneralUtils.isValidAlias(object.getAlias()) || !GeneralUtils.isValidType(object.getType())) {
            this.logger.error("Alias or Type are invalid");
            throw new InvalidInputException("Alias or Type are invalid");
        }

        UserIdBoundary userId = object.getCreatedBy().getUserId();
        if (!GeneralUtils.isAuthUserOperation(this.userConverter.createID(userId), UserRole.SUPERAPP_USER, userCrud)) {
            this.logger.error("Only SUPERAPP_USER Can Crate SuperApp Objects");
            throw new UnauthorizedUserOperation("Only SUPERAPP_USER Can Crate SuperApp Objects");
        }

        //Creation of SuperAppObjectIdBoundary, the dual identifier of SuperAppObjectBoundary
        SuperAppObjectIdBoundary idBoundary = new SuperAppObjectIdBoundary();
        idBoundary.setInternalObjectId(UUID.randomUUID().toString());
        idBoundary.setSuperapp(this.superapp);

        //Filling the creation time stamp and the internal id by the server
        object.setObjectId(idBoundary);
        object.setCreationTimestamp(new Date());

        //Saving the entity to the DB
        SuperAppObjectEntity superAppObjectEntity = this.objectConverter.toEntity(object);
        superAppObjectEntity = this.objectCrud.save(superAppObjectEntity);
        this.logger.trace("Created and saved new SuperAppObjectBoundary object");
        return this.objectConverter.toBoundary(superAppObjectEntity);
    }

    /**
     * Updates an existing SuperApp object.
     *
     * @param objectSuperApp   the SuperApp associated with the object
     * @param internalObjectId the internal ID of the object
     * @param update           the updated SuperApp object
     * @return the updated SuperApp object
     * @deprecated
     */
    @Override
    @Deprecated
    public SuperAppObjectBoundary updateObject(String objectSuperApp, String internalObjectId, SuperAppObjectBoundary update) {
        this.logger.error("Method {updateObject(String objectSuperApp, String internalObjectId, SuperAppObjectBoundary update)} is Deprecated");
        throw new MethodNotInUseException("Method {updateObject(String objectSuperApp, String internalObjectId, SuperAppObjectBoundary update)} is Deprecated");
    }

    /**
     * Retrieves a specific SuperApp object.
     *
     * @param objectSuperApp   the SuperApp associated with the object
     * @param internalObjectId the internal ID of the object
     * @return the retrieved SuperApp object
     * @deprecated
     */
    @Override
    @Deprecated
    public SuperAppObjectBoundary getSpecificObject(String objectSuperApp, String internalObjectId) {
        this.logger.error("Method {getSpecificObject(String objectSuperApp, String internalObjectId)} is Deprecated");
        throw new MethodNotInUseException("Method {getSpecificObject(String objectSuperApp, String internalObjectId)} is Deprecated");
    }

    /**
     * @return a list of SuperApp objects
     * @deprecated Retrieves a list of all SuperApp objects for a user.
     */
    @Override
    @Deprecated
    public List<SuperAppObjectBoundary> getAllObjects() {
        this.logger.error("Method {getAllObjects()} is Deprecated");
        throw new MethodNotInUseException("Method {getAllObjects()} is Deprecated");
    }

    /**
     * @deprecated Deletes all SuperApp objects.
     */
    @Override
    @Deprecated
    public void deleteAllObjects() {
        this.logger.error("Method {deleteAllObjects()} is Deprecated");
        throw new MethodNotInUseException("Method {deleteAllObjects()} is Deprecated");
    }

    /**
     * Updates an existing SuperAppObjectBoundary object.
     *
     * @param objectSuperAppBoundary the SuperApp of the SuperAppObjectBoundary to update
     * @param internalObjectId       the internal object ID of the SuperAppObjectBoundary to update
     * @param update                 the SuperAppObjectBoundary object with the updated values
     * @return the updated SuperAppObjectBoundary object
     * @throws NotFoundException if the SuperAppObjectBoundary cannot be found by the given ID
     */
    @Override
    public SuperAppObjectBoundary updateObject(String objectSuperAppBoundary, String internalObjectId, SuperAppObjectBoundary update,
                                               String userSuperApp, String userEmail) {
        //Notice that creationTimeStamp doesn't change because the object is not being recreated
        //also the invoker of the object doesn't change
        this.logger.debug("Updating a SuperAppObjectBoundary object");
        SuperAppObjectEntity superAppObjectEntity = this.objectCrud
                .findById(this.objectConverter.createID(objectSuperAppBoundary, internalObjectId))
                .orElseThrow(() -> new NotFoundException("Could not find super app boundary object for update by id: " + internalObjectId));

        if (!GeneralUtils.isAuthUserOperation(userConverter.createID(userSuperApp, userEmail), UserRole.SUPERAPP_USER, userCrud)) {
            this.logger.error("Only SUPERAPP_USER Can Update SuperApp Objects");
            throw new UnauthorizedUserOperation("Only SUPERAPP_USER Can Update SuperApp Objects");
        }

        if (update.getCreationTimestamp() != null && !update.getCreationTimestamp().equals(superAppObjectEntity.getCreationTimeStamp())) {
            this.logger.error("Cannot Change SuperApp Object CreationTime");
            throw new UnauthorizedUserOperation("Cannot Change SuperApp Object CreationTime");
        }

        if (update.getObjectId() != null && !update.getObjectId().getInternalObjectId().equals(objectConverter.retrieveInternalObjectID(superAppObjectEntity.getId()))) {
            this.logger.error("Cannot Change SuperApp Object Id");
            throw new UnauthorizedUserOperation("Cannot Change SuperApp Object Id");
        }

        if (update.getCreatedBy() != null && !update.getCreatedBy().getUserId().equals(superAppObjectEntity.getUserIdInvoker().getUserId())) {
            this.logger.error("Cannot Change Object Creator");
            throw new UnauthorizedUserOperation("Cannot Change Object Creator");
        }

        if (update.getAlias() != null) {
            if (!update.getAlias().isBlank()) superAppObjectEntity.setAlias(update.getAlias());
            else {
                this.logger.error("Alias Can't Be An Empty String");
                throw new InvalidInputException("Alias Can't Be An Empty String");
            }
        }

        if (update.getType() != null) {
            if (!update.getType().isBlank()) superAppObjectEntity.setType(update.getType());
            else {
                this.logger.error("Type Can't Be An Empty String");
                throw new InvalidInputException("Type Can't Be An Empty String");
            }
        }

        if (update.getActive() != null) {
            superAppObjectEntity.setActive(update.getActive());
        }

        if (update.getLocation() != null) {
            superAppObjectEntity.setLocation(new GeoJsonPoint(update.getLocation().getLat(), update.getLocation().getLng()));
        }

        if (update.getObjectDetails() != null) {
            superAppObjectEntity.setObjectDetails(update.getObjectDetails());
        }

        //Saving the entity to the DB
        superAppObjectEntity = this.objectCrud.save(superAppObjectEntity);
        this.logger.trace("Updated a SuperAppObjectBoundary object");
        return this.objectConverter.toBoundary(superAppObjectEntity);
    }

    /**
     * Retrieves a specific SuperAppObject from the database with the given objectSuperApp and internalObjectId.
     *
     * @param objectSuperApp   the name of the object's SuperApp
     * @param internalObjectId the object's internal ID
     * @return the SuperAppObjectBoundary representing the retrieved SuperAppObject
     * @throws NotFoundException if no matching object is found
     */
    @Override
    public SuperAppObjectBoundary getSpecificObject(String objectSuperApp, String internalObjectId, String userSuperApp, String userEmail) {
        this.logger.debug("Getting a Specific SuperAppObjectBoundary object");
        Optional<SuperAppObjectEntity> tempObjectEntity = this.objectCrud.findById(this.objectConverter.createID(objectSuperApp, internalObjectId));
        String userId = this.userConverter.createID(userSuperApp, userEmail);

        if (tempObjectEntity.isEmpty()) {
            this.logger.error("Object {%s} Doesn't Exist".formatted(internalObjectId));
            throw new NotFoundException("Object {%s} Doesn't Exist".formatted(internalObjectId));
        }
        if (GeneralUtils.isAuthUserOperation(userId, UserRole.SUPERAPP_USER, userCrud))
            return this.objectConverter.toBoundary(tempObjectEntity.get());
        if (GeneralUtils.isAuthUserOperation(userId, UserRole.MINIAPP_USER, userCrud)) {
            if (tempObjectEntity.get().getActive()) {
                this.logger.trace("Got a specific Object Succeed ");
                return this.objectConverter.toBoundary(tempObjectEntity.get());
            }
            this.logger.error("Object {%s} Doesn't Exist".formatted(internalObjectId));
            throw new NotFoundException("Object {%s} Doesn't Exist".formatted(internalObjectId));
        }
        this.logger.error("Unauthorized User: Only SuperApp and MiniApp Can Get Specific Object");
        throw new UnauthorizedUserOperation("Only SuperApp and MiniApp Can Get Specific Object");
    }

    /**
     * Retrieves all SuperAppObjects from the database.
     *
     * @return a List of SuperAppObjectBoundary objects representing all SuperAppObjects in the database
     */
    @Override
    public List<SuperAppObjectBoundary> getAllObjects(String userSuperApp, String userEmail, int size, int page) {
        this.logger.debug("Getting all SuperAppObjectBoundary objects");
        String userId = this.userConverter.createID(userSuperApp, userEmail);
        PageRequest pageReq = PageRequest.of(page, size, DEFAULT_SORTING_DIRECTION, "creationTimeStamp", "id");

        if (GeneralUtils.isAuthUserOperation(userId, UserRole.SUPERAPP_USER, userCrud)) {
            this.logger.trace("Getting all SuperAppObjectBoundary objects");
            return this.objectCrud
                    .findAll(pageReq)
                    .stream()
                    .map(this.objectConverter::toBoundary)
                    .collect(Collectors.toList());
        }

        if (GeneralUtils.isAuthUserOperation(userId, UserRole.MINIAPP_USER, userCrud)) {
            this.logger.trace("Getting all active SuperAppObjectBoundary objects");
            return this.objectCrud
                    .findAllByActiveIsTrue(pageReq)
                    .stream()
                    .map(this.objectConverter::toBoundary)
                    .collect(Collectors.toList());
        }
        this.logger.error("Unauthorized User: Only SuperApp and MiniApp Can Get All Object");
        throw new UnauthorizedUserOperation("Only SuperApp and MiniApp Can Get All Object");
    }

    /**
     * Deletes all SuperAppObjects from the database.
     */
    @Override
    public void deleteAllObjects(String userSuperApp, String userEmail) {
        this.logger.debug("Deleting all SuperAppObjectBoundary objects");
        String userId = this.userConverter.createID(userSuperApp, userEmail);
        if (GeneralUtils.isAuthUserOperation(userId, UserRole.ADMIN, userCrud)) {
            this.objectCrud.deleteAll();
            this.logger.trace("Deleted All SuperAppObjectBoundary objects");
        } else {
            this.logger.error("Unauthorized User: Only Admin Can Remove All Objects");
            throw new UnauthorizedUserOperation("Only Admin Can Remove All Objects");
        }
    }

    /**
     * Binds a child object to a parent object. Both parent and child must exist and be different entities.
     *
     * @param objectSuperApp   The super app of the parent object.
     * @param internalObjectId The internal object ID of the parent object.
     * @param childObject      The child object to be bound.
     * @throws NotFoundException     if either the parent or the child does not exist.
     * @throws AlreadyExistException if the parent object already has the child object.
     * @throws InvalidInputException if the parent object and the child object are the same.
     * @throws RuntimeException      if something goes wrong while saving the objects.
     */
    @Override
    public void bindChildObject(String objectSuperApp, String internalObjectId, SuperAppObjectIdBoundary childObject,
                                String userSuperApp, String userEmail) {
        this.logger.debug("Binding a Child object");
        String userId = this.userConverter.createID(userSuperApp, userEmail);
        if (!GeneralUtils.isAuthUserOperation(userId, UserRole.SUPERAPP_USER, userCrud)) {
            this.logger.error("Unauthorized User: Only SuperApp User Can Bind Objects");
            throw new UnauthorizedUserOperation("Only SuperApp User Can Bind Objects");
        }

        SuperAppObjectEntity parent = this.objectCrud
                .findById(this.objectConverter.createID(objectSuperApp, internalObjectId))
                .orElseThrow(() -> new NotFoundException("Parent Object Doesn't Exist : " + internalObjectId));

        SuperAppObjectEntity child = this.objectCrud
                .findById(this.objectConverter.createID(childObject))
                .orElseThrow(() -> new NotFoundException("Child Object Doesn't Exist : " + childObject.getInternalObjectId()));

        if (parent.addChild(child) && child.addParent(parent)) {
            this.objectCrud.save(parent); // Update Parent Object
            this.objectCrud.save(child); // Update Child Object
            this.logger.trace("Child object Bound");
        } else {
            this.logger.error("-Objects Are Already Bound");
            throw new InvalidInputException("Objects Are Already Bound");
        }
    }

    /**
     * Gets all the children of a parent object.
     *
     * @param objectSuperApp   The super app of the parent object.
     * @param internalObjectId The internal object ID of the parent object.
     * @return A list of all the children of the parent object.
     * @throws NotFoundException if the parent object does not exist.
     */
    @Override
    public List<SuperAppObjectBoundary> getAllObjectChildren(String objectSuperApp, String internalObjectId,
                                                             String userSuperApp, String userEmail, int size, int page) {
        this.logger.debug("Getting all object Children");
        SuperAppObjectEntity parent = this.objectCrud
                .findById(this.objectConverter.createID(objectSuperApp, internalObjectId))
                .orElseThrow(() -> new NotFoundException("Parent Object Doesn't Exist : " + internalObjectId));

        String userId = this.userConverter.createID(userSuperApp, userEmail);
        PageRequest pageReq = PageRequest.of(page, size, DEFAULT_SORTING_DIRECTION, "creationTimeStamp", "id");

        if (GeneralUtils.isAuthUserOperation(userId, UserRole.SUPERAPP_USER, userCrud)) {
            this.logger.trace("Getting all object Children");
            return this.objectCrud
                    .findByParentsContaining(parent, pageReq)
                    .stream()
                    .map(this.objectConverter::toBoundary)
                    .collect(Collectors.toList());
        }

        if (GeneralUtils.isAuthUserOperation(userId, UserRole.MINIAPP_USER, userCrud)) {
            this.logger.trace("Getting all active object Children");
            return this.objectCrud
                    .findByParentsContainingAndActiveIsTrue(parent, pageReq)
                    .stream()
                    .map(this.objectConverter::toBoundary)
                    .collect(Collectors.toList());
        }
        this.logger.error("Unauthorized User: Only SuperApp and MiniApp Can Get Children Object");
        throw new UnauthorizedUserOperation("Only SuperApp and MiniApp Can Get Children Object");
    }

    /**
     * Gets all the parents of a child object.
     *
     * @param objectSuperApp   The super app of the child object.
     * @param internalObjectId The internal object ID of the child object.
     * @return A list of all the parents of the child object.
     * @throws NotFoundException if the child object does not exist.
     */
    @Override
    public List<SuperAppObjectBoundary> getAllObjectParents(String objectSuperApp, String internalObjectId,
                                                            String userSuperApp, String userEmail, int size, int page) {
        this.logger.debug("Getting all object parents");
        SuperAppObjectEntity parent = this.objectCrud
                .findById(this.objectConverter.createID(objectSuperApp, internalObjectId))
                .orElseThrow(() -> new NotFoundException("Parent Object Doesn't Exist : " + internalObjectId));

        String userId = this.userConverter.createID(userSuperApp, userEmail);
        PageRequest pageReq = PageRequest.of(page, size, DEFAULT_SORTING_DIRECTION, "creationTimeStamp", "id");

        if (GeneralUtils.isAuthUserOperation(userId, UserRole.SUPERAPP_USER, userCrud)) {
            this.logger.trace("Getting all object Children parents");
            return this.objectCrud
                    .findByChildrenContaining(parent, pageReq)
                    .stream()
                    .map(this.objectConverter::toBoundary)
                    .collect(Collectors.toList());
        }

        if (GeneralUtils.isAuthUserOperation(userId, UserRole.MINIAPP_USER, userCrud)) {
            this.logger.trace("Getting all active object parents");
            return this.objectCrud
                    .findByChildrenContainingAndActiveIsTrue(parent, pageReq)
                    .stream()
                    .map(this.objectConverter::toBoundary)
                    .collect(Collectors.toList());
        }
        this.logger.error("Unauthorized User: Only SuperApp and MiniApp Can Get Parents Object");
        throw new UnauthorizedUserOperation("Only SuperApp and MiniApp Can Get Parents Object");
    }

    /**
     * Binds a child object to a parent object.
     *
     * @param objectSuperApp   The SuperApp of the parent object.
     * @param internalObjectId The internal ID of the parent object.
     * @param childObject      The child object to bind.
     * @deprecated
     */
    @Override
    @Deprecated
    public void bindChildObject(String objectSuperApp, String internalObjectId, SuperAppObjectIdBoundary childObject) {
        this.logger.error("Method {bindChildObject(String objectSuperApp, String internalObjectId, SuperAppObjectIdBoundary childObject)} is Deprecated");
        throw new MethodNotInUseException("Method {bindChildObject(String objectSuperApp, String internalObjectId, SuperAppObjectIdBoundary childObject)} is Deprecated");
    }

    /**
     * Retrieves a list of children for a specific SuperApp object.
     *
     * @param objectSuperApp   The SuperApp associated with the object.
     * @param internalObjectId The internal ID of the object.
     * @return A list of SuperAppObjectBoundary objects representing the children of the specified object.
     * @deprecated
     */
    @Override
    @Deprecated
    public List<SuperAppObjectBoundary> getAllObjectChildren(String objectSuperApp, String internalObjectId) {
        this.logger.error("Method {getAllObjectChildren(String objectSuperApp, String internalObjectId)} is Deprecated");
        throw new MethodNotInUseException("Method {getAllObjectChildren(String objectSuperApp, String internalObjectId)} is Deprecated");
    }

    /**
     * Retrieves a list of parents for a specific SuperApp object.
     *
     * @param objectSuperApp   The SuperApp associated with the object.
     * @param internalObjectId The internal ID of the object.
     * @return A list of SuperAppObjectBoundary objects representing the parents of the specified object.
     * @deprecated
     */
    @Override
    @Deprecated
    public List<SuperAppObjectBoundary> getAllObjectParents(String objectSuperApp, String internalObjectId) {
        this.logger.error("Method {getAllObjectParents(String objectSuperApp, String internalObjectId)} is Deprecated");
        throw new MethodNotInUseException("Method {getAllObjectParents(String objectSuperApp, String internalObjectId)} is Deprecated");
    }

    /**
     * Retrieves a list of SuperAppObjectBoundary objects by type.
     *
     * @param type         The type of objects to retrieve.
     * @param userSuperApp The SuperApp of the user.
     * @param userEmail    The email of the user.
     * @param size         The maximum number of objects to retrieve per page.
     * @param page         The page number to retrieve (0-based index).
     * @return A list of SuperAppObjectBoundary objects that match the specified type.
     */
    @Override
    public List<SuperAppObjectBoundary> getObjectsByType(String type, String userSuperApp, String userEmail, int size, int page) {
        this.logger.debug("Getting all object by type");
        String userId = this.userConverter.createID(userSuperApp, userEmail);
        PageRequest pageReq = PageRequest.of(page, size, DEFAULT_SORTING_DIRECTION, "creationTimeStamp", "id");

        if (GeneralUtils.isAuthUserOperation(userId, UserRole.SUPERAPP_USER, userCrud)) {
            this.logger.trace("Getting all object by type");
            return this.objectCrud
                    .findByType(type, pageReq)
                    .stream()
                    .map(this.objectConverter::toBoundary)
                    .collect(Collectors.toList());
        }

        if (GeneralUtils.isAuthUserOperation(userId, UserRole.MINIAPP_USER, userCrud)) {
            this.logger.trace("Getting all active object by type");
            return this.objectCrud
                    .findByTypeAndActiveIsTrue(type, pageReq)
                    .stream()
                    .map(this.objectConverter::toBoundary)
                    .collect(Collectors.toList());
        }
        this.logger.error("Unauthorized User: Only SuperApp and MiniApp Can Get Object By Type");
        throw new UnauthorizedUserOperation("Only SuperApp and MiniApp Can Get Object By Type");
    }

    /**
     * Retrieves a list of SuperAppObjectBoundary objects by alias.
     *
     * @param alias        The alias of the objects to retrieve.
     * @param userSuperApp The SuperApp of the user.
     * @param userEmail    The email of the user.
     * @param size         The maximum number of objects to retrieve per page.
     * @param page         The page number to retrieve (0-based index).
     * @return A list of SuperAppObjectBoundary objects that match the specified alias.
     */
    @Override
    public List<SuperAppObjectBoundary> getObjectsByAlias(String alias, String userSuperApp, String userEmail, int size, int page) {
        this.logger.debug("Getting all object by Alias");
        String userId = this.userConverter.createID(userSuperApp, userEmail);
        PageRequest pageReq = PageRequest.of(page, size, DEFAULT_SORTING_DIRECTION, "creationTimeStamp", "id");

        if (GeneralUtils.isAuthUserOperation(userId, UserRole.SUPERAPP_USER, userCrud)) {
            this.logger.trace("Getting all object by Alias");
            return this.objectCrud
                    .findByAlias(alias, pageReq)
                    .stream()
                    .map(this.objectConverter::toBoundary)
                    .collect(Collectors.toList());
        }

        if (GeneralUtils.isAuthUserOperation(userId, UserRole.MINIAPP_USER, userCrud)) {
            this.logger.trace("Getting all active object by Alias");
            return this.objectCrud
                    .findByAliasAndActiveIsTrue(alias, pageReq)
                    .stream()
                    .map(this.objectConverter::toBoundary)
                    .collect(Collectors.toList());
        }
        this.logger.error("Unauthorized User: Only SuperApp and MiniApp Can Get Object By Alias");
        throw new UnauthorizedUserOperation("Only SuperApp and MiniApp Can Get Object By Alias");
    }

    /**
     * Retrieves a list of SuperAppObjectBoundary objects near a specified location.
     *
     * @param lat           The latitude of the center location.
     * @param lng           The longitude of the center location.
     * @param distance      The distance radius from the center location.
     * @param distanceUnits The units of the distance (e.g., "km", "mi").
     * @param userSuperApp  The SuperApp of the user.
     * @param userEmail     The email of the user.
     * @param size          The maximum number of objects to retrieve per page.
     * @param page          The page number to retrieve (0-based index).
     * @return A list of SuperAppObjectBoundary objects within the specified distance of the location.
     */
    @Override
    public List<SuperAppObjectBoundary> getObjectsByLocation(Double lat, Double lng, Double distance, String distanceUnits, String userSuperApp, String userEmail, int size, int page) {
        this.logger.debug("Getting all object by Location");
        String userId = this.userConverter.createID(userSuperApp, userEmail);
        PageRequest pageReq = PageRequest.of(page, size, DEFAULT_SORTING_DIRECTION, "creationTimeStamp", "id");
        if (!(distanceUnits.equalsIgnoreCase(KM_DISTANCE_TYPE) || distanceUnits.equalsIgnoreCase(MILE_DISTANCE_TYPE)))
            throw new InvalidInputException("The distance type is invalid");
        double maxDistance;

        if (Objects.equals(distanceUnits, KM_DISTANCE_TYPE)) {
            maxDistance = distance / Metrics.KILOMETERS.getMultiplier(); // Staying in metric method -> converted to radians
        } else {
            maxDistance = distance / Metrics.MILES.getMultiplier(); // Staying in miles method  -> converted to radians
        }

        if (GeneralUtils.isAuthUserOperation(userId, UserRole.SUPERAPP_USER, userCrud)) {
            this.logger.trace("Getting all object by Location");
            return this.objectCrud
                    .findByLocationNear(lat, lng, maxDistance, pageReq)
                    .stream()
                    .map(this.objectConverter::toBoundary)
                    .collect(Collectors.toList());
        }

        if (GeneralUtils.isAuthUserOperation(userId, UserRole.MINIAPP_USER, userCrud)) {
            this.logger.trace("Getting all active object by Location");
            return this.objectCrud
                    .findByLocationNearAndActiveIsTrue(lat, lng, maxDistance, pageReq)
                    .stream()
                    .map(this.objectConverter::toBoundary)
                    .collect(Collectors.toList());
        }
        this.logger.error("Unauthorized User: Only SuperApp and MiniApp Can Get Object By Location");
        throw new UnauthorizedUserOperation("Only SuperApp and MiniApp Can Get Object By Location");
    }
}
