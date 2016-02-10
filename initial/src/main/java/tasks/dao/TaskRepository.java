package tasks.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "task", path = "task")
public interface TaskRepository extends CrudRepository<Task, Long> {
	
}
