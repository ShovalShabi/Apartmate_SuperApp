package superapp.dal;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import superapp.data.UserEntity;

import java.util.List;

/**
 * The UserCrud interface provides CRUD (Create, Read, Update, Delete) operations for UserEntity objects.
 * It extends the ListCrudRepository interface with UserEntity as the entity type and String as the ID type.
 */
public interface UserCrud extends ListCrudRepository<UserEntity, String> {
    /**
     * Retrieves all UserEntity objects using pagination.
     *
     * @param page the Pageable object representing the pagination information
     * @return a List of UserEntity objects
     */
    List<UserEntity> findAll(Pageable page);
}
