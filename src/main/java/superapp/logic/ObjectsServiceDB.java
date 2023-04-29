package superapp.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.boundaries.object.SuperAppObjectIdBoundary;
import superapp.converters.ObjectConverter;
import superapp.dal.ObjectCrud;
import superapp.data.SuperAppObjectEntity;
import superapp.utils.GeneralUtils;
import superapp.utils.Invokers.UserIdInvoker;
import superapp.utils.exceptions.AlreadyExistException;
import superapp.utils.exceptions.InvalidInputException;
import superapp.utils.exceptions.NotFoundException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
    private ObjectConverter objectConverter;    //Converter for SuperAppObjectBoundary to SuperAppObjectEntity and the opposite
    private String superapp;    //The identifier of the super app


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
     * Sets the ObjectConverter dependency.
     *
     * @param objectConverter the ObjectConverter dependency
     */
    @Autowired
    public void setObjectConverter(ObjectConverter objectConverter) {
        this.objectConverter = objectConverter;
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

        if (!GeneralUtils.isValidAlias(object.getAlias()) || !GeneralUtils.isValidType(object.getType()))
            throw new InvalidInputException("Alias or type are invalid");

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
        return this.objectConverter.toBoundary(superAppObjectEntity);
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
    public SuperAppObjectBoundary updateObject(String objectSuperAppBoundary, String internalObjectId, SuperAppObjectBoundary update) {
        //Notice that creationTimeStamp doesn't change because the object is not being recreated
        //also the invoker of the object doesn't change
        SuperAppObjectEntity superAppObjectEntity = this.objectCrud
                .findById(this.objectConverter.createID(objectSuperAppBoundary, internalObjectId))
                .orElseThrow(() -> new NotFoundException("Could not find super app boundary object for update by id: " + internalObjectId));

        if (update.getAlias() != null) {
            if (!update.getAlias().isBlank()) superAppObjectEntity.setAlias(update.getAlias());
            else throw new InvalidInputException("Alias Can't Be An Empty String");
        }

        if (update.getType() != null) {
            if (!update.getType().isBlank()) superAppObjectEntity.setType(update.getType());
            else throw new InvalidInputException("Type Can't Be An Empty String");
        }

        if (update.getCreatedBy() != null) {
            UserIdInvoker userIdInvoker = update.getCreatedBy();
            if (!(userIdInvoker.getUserId().getSuperapp().isBlank() &&
                GeneralUtils.isValidEmail(userIdInvoker.getUserId().getEmail())))
                    superAppObjectEntity.setUserIdInvoker(update.getCreatedBy());
            else throw new InvalidInputException("User Creator SuperApp or Email Aren't valid");
        }

        if (update.getActive() != null) {
            superAppObjectEntity.setActive(update.getActive());
        }

        if (update.getLocation() != null) {
            superAppObjectEntity.setLat(update.getLocation().getLat());
            superAppObjectEntity.setLng(update.getLocation().getLng());
        }

        if (update.getObjectDetails() != null) {
            superAppObjectEntity.setObjectDetails(update.getObjectDetails());
        }


        //Saving the entity to the DB
        superAppObjectEntity = this.objectCrud.save(superAppObjectEntity);
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
    public SuperAppObjectBoundary getSpecificObject(String objectSuperApp, String internalObjectId) {
        Optional<SuperAppObjectEntity> tempObjectEntity = this.objectCrud.findById(this.objectConverter.createID(objectSuperApp, internalObjectId));
        if (tempObjectEntity.isEmpty())
            throw new NotFoundException("Could not find super app boundary object for update by id: " + internalObjectId);
        return this.objectConverter.toBoundary(tempObjectEntity.get());
    }

    /**
     * Retrieves all SuperAppObjects from the database.
     *
     * @return a List of SuperAppObjectBoundary objects representing all SuperAppObjects in the database
     */
    @Override
    public List<SuperAppObjectBoundary> getAllObjects() {
        Iterable<SuperAppObjectEntity> iterable = this.objectCrud.findAll();
        return StreamSupport.stream(iterable.spliterator(), false).map(this.objectConverter::toBoundary).collect(Collectors.toList());
    }

    /**
     * Deletes all SuperAppObjects from the database.
     */
    @Override
    public void deleteAllObjects() {
        this.objectCrud.deleteAll();
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
    public void bindChildObject(String objectSuperApp, String internalObjectId, SuperAppObjectIdBoundary childObject) {
        SuperAppObjectEntity parent = this.objectCrud
                .findById(this.objectConverter.createID(objectSuperApp, internalObjectId))
                .orElseThrow(() -> new NotFoundException("Parent Object Doesn't Exist : " + internalObjectId));

        SuperAppObjectEntity child = this.objectCrud
                .findById(this.objectConverter.createID(childObject))
                .orElseThrow(() -> new NotFoundException("Child Object Doesn't Exist : " + childObject.getInternalObjectId()));

        if (parent.getChildren().contains(child))
            throw new AlreadyExistException("Parent Object %s Already Has Child Object %s"
                    .formatted(internalObjectId, childObject.getInternalObjectId()));

        if (parent.getId().equals(child.getId()))
            throw new InvalidInputException("Can't Bind Parent To Himself");

        if (parent.addChild(child) && child.addParent(parent)) {
            this.objectCrud.save(parent); // Update Parent Object
            this.objectCrud.save(child); // Update Child Object
        } else
            throw new RuntimeException("Something Went Wrong, Cannot Bind Child To Parent");
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
    public List<SuperAppObjectBoundary> getAllObjectChildren(String objectSuperApp, String internalObjectId) {
        SuperAppObjectEntity parent = this.objectCrud
                .findById(this.objectConverter.createID(objectSuperApp, internalObjectId))
                .orElseThrow(() -> new NotFoundException("Parent Object Doesn't Exist : " + internalObjectId));
        return parent.getChildren().stream().map(this.objectConverter::toBoundary).collect(Collectors.toList());
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
    public List<SuperAppObjectBoundary> getAllObjectParents(String objectSuperApp, String internalObjectId) {
        SuperAppObjectEntity parent = this.objectCrud
                .findById(this.objectConverter.createID(objectSuperApp, internalObjectId))
                .orElseThrow(() -> new NotFoundException("Parent Object Doesn't Exist : " + internalObjectId));
        return parent.getParents().stream().map(this.objectConverter::toBoundary).collect(Collectors.toList());
    }
}
