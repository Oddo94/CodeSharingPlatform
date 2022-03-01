package platform.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import platform.model.Data;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodeSnippetsRepository extends CrudRepository<Data, Long> {
    List<Data> findAllByOrderByDateDesc();
    Optional<Data> findByUUID(String UUID);
    boolean existsByUUID(String UUID);

}
