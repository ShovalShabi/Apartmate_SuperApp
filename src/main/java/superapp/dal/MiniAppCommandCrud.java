package superapp.dal;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import superapp.boundaries.command.MiniAppCommandBoundary;
import superapp.data.MiniAppCommandEntity;

import java.util.List;

/**
 * The MiniAppCommandCrud interface provides CRUD (Create, Read, Update, Delete) operations for MiniAppCommandEntity objects.
 * It extends the ListCrudRepository interface with MiniAppCommandEntity as the entity type and String as the ID type.
 */
public interface MiniAppCommandCrud extends ListCrudRepository<MiniAppCommandEntity, String> {
    /**
     * Retrieves all MiniAppCommandEntity objects using pagination.
     *
     * @param page the Pageable object representing the pagination information
     * @return a List of MiniAppCommandEntity objects
     */
    List<MiniAppCommandEntity> findAll(Pageable page);

    /**
     * Retrieves MiniAppCommandEntity objects containing the specified miniAppName using pagination.
     *
     * @param miniApp the miniAppName to search for
     * @param page    the Pageable object representing the pagination information
     * @return a List of MiniAppCommandEntity objects matching the search criteria
     */
    List<MiniAppCommandEntity> findByMiniapp(@Param("miniApp") String miniApp, Pageable page);
}
