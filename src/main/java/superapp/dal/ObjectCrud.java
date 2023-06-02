package superapp.dal;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import superapp.data.SuperAppObjectEntity;

import java.util.List;

/**
 * The ObjectCrud interface provides CRUD (Create, Read, Update, Delete) operations for SuperAppObjectEntity objects.
 * It extends the ListCrudRepository interface with SuperAppObjectEntity as the entity type and String as the ID type.
 */
public interface ObjectCrud extends ListCrudRepository<SuperAppObjectEntity, String> {
    /**
     * Retrieves a list of all SuperAppObjectEntity objects with pagination support.
     *
     * @param page The pageable object specifying the page number and size of the results.
     * @return A list of SuperAppObjectEntity objects.
     */
    List<SuperAppObjectEntity> findAll(Pageable page);

    /**
     * Retrieves a list of active SuperAppObjectEntity objects with pagination support.
     *
     * @param page The pageable object specifying the page number and size of the results.
     * @return A list of active SuperAppObjectEntity objects.
     */
    List<SuperAppObjectEntity> findAllByActiveIsTrue(Pageable page);

    /**
     * Retrieves a list of SuperAppObjectEntity objects based on the specified type with pagination support.
     *
     * @param type The type of SuperAppObjectEntity to retrieve.
     * @param page The pageable object specifying the page number and size of the results.
     * @return A list of SuperAppObjectEntity objects with the specified type.
     */
    List<SuperAppObjectEntity> findByType(@Param("type") String type, Pageable page);

    /**
     * Retrieves a list of active SuperAppObjectEntity objects based on the specified type with pagination support.
     *
     * @param type The type of SuperAppObjectEntity to retrieve.
     * @param page The pageable object specifying the page number and size of the results.
     * @return A list of active SuperAppObjectEntity objects with the specified type.
     */
    List<SuperAppObjectEntity> findByTypeAndActiveIsTrue(@Param("type") String type, Pageable page);

    /**
     * Retrieves a list of SuperAppObjectEntity objects based on the specified alias with pagination support.
     *
     * @param alias The alias of SuperAppObjectEntity to retrieve.
     * @param page  The pageable object specifying the page number and size of the results.
     * @return A list of SuperAppObjectEntity objects with the specified alias.
     */
    List<SuperAppObjectEntity> findByAlias(@Param("alias") String alias, Pageable page);

    /**
     * Retrieves a list of active SuperAppObjectEntity objects based on the specified alias with pagination support.
     *
     * @param alias The alias of SuperAppObjectEntity to retrieve.
     * @param page  The pageable object specifying the page number and size of the results.
     * @return A list of active SuperAppObjectEntity objects with the specified alias.
     */
    List<SuperAppObjectEntity> findByAliasAndActiveIsTrue(@Param("alias") String alias, Pageable page);

    /**
     * Retrieves a list of entities that are within a specified distance from the given point.
     *
     * @param longitude   The longitude of the center point.
     * @param latitude    The latitude of the center point.
     * @param maxDistance The maximum distance (in radians) from the center point.
     * @param page        The pagination information.
     * @return entities within the specified distance.
     */
    @Query("{'location': { $geoWithin: { $centerSphere: [ [ ?0, ?1 ], ?2 ] } } }")
    List<SuperAppObjectEntity> findByLocationNear(double longitude, double latitude, double maxDistance, Pageable page);

    /**
     * Retrieves a list of entities that are within a specified distance from the given point and active is true.
     *
     * @param longitude   The longitude of the center point.
     * @param latitude    The latitude of the center point.
     * @param maxDistance The maximum distance (in radians) from the center point.
     * @param page        The pagination information.
     * @return entities within the specified distance.
     */
    @Query("{'location': { $geoWithin: { $centerSphere: [ [ ?0, ?1 ], ?2 ] } }, 'active':true }")
    List<SuperAppObjectEntity> findByLocationNearAndActiveIsTrue(double longitude, double latitude, double maxDistance, Pageable page);

    /**
     * Retrieves a list of SuperAppObjectEntity objects that contain the specified child object with pagination support.
     *
     * @param child The child SuperAppObjectEntity to search for.
     * @param page  The pageable object specifying the page number and size of the results.
     * @return A list of SuperAppObjectEntity objects containing the specified child.
     */
    List<SuperAppObjectEntity> findByChildrenContaining(SuperAppObjectEntity child, Pageable page);

    /**
     * Retrieves a list of active SuperAppObjectEntity objects that contain the specified child object with pagination support.
     *
     * @param child The child SuperAppObjectEntity to search for.
     * @param page  The pageable object specifying the page number and size of the results.
     * @return A list of active SuperAppObjectEntity objects containing the specified child.
     */
    List<SuperAppObjectEntity> findByChildrenContainingAndActiveIsTrue(SuperAppObjectEntity child, Pageable page);

    /**
     * Retrieves a list of SuperAppObjectEntity objects that have the specified parent object in their list of parents, with pagination support.
     *
     * @param parent The parent SuperAppObjectEntity to search for.
     * @param page   The pageable object specifying the page number and size of the results.
     * @return A list of SuperAppObjectEntity objects that have the specified parent.
     */
    List<SuperAppObjectEntity> findByParentsContaining(SuperAppObjectEntity parent, Pageable page);

    /**
     * Retrieves a list of active SuperAppObjectEntity objects that have the specified parent object in their list of parents, with pagination support.
     *
     * @param parent The parent SuperAppObjectEntity to search for.
     * @param page   The pageable object specifying the page number and size of the results.
     * @return A list of active SuperAppObjectEntity objects that have the specified parent.
     */
    List<SuperAppObjectEntity> findByParentsContainingAndActiveIsTrue(SuperAppObjectEntity parent, Pageable page);

}
