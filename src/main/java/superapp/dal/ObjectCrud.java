package superapp.dal;

import org.springframework.data.repository.ListCrudRepository;
import superapp.data.SuperAppObjectEntity;

public interface ObjectCrud extends ListCrudRepository<SuperAppObjectEntity, String> {

}
