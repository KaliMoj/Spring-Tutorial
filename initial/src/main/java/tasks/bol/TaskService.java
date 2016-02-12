package tasks.bol;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tasks.dao.Task;
import tasks.dao.TaskRepository;
import tasks.dao.User;

@Service
public class TaskService {

	@Autowired
	private TaskRepository taskJpaRepository;

	public Task getTaskById(long taskId) {
		Task task = taskJpaRepository.findOne(taskId);
		if (task == null) throw new TaskNotFoundException();
		return task;
	}
	
	public Task createTaskForUser(Task task, User user) {
		task.setUser(user);
		return saveTask(task);
	}
	
	public void deleteTask(long taskId) {
		Task task = getTaskById(taskId);
		if (!task.getUser().isActive()) throw new InactiveUserTaskException();
		task.setActive(false);
		saveTask(task);
	}
	
	public Task updateTask(long taskId, Task task) {
		if (!taskExists(taskId)) throw new TaskNotFoundException();
		task.setId(taskId);
		return saveTask(task);
	}
	
	private boolean taskExists(long taskId) {
		return taskJpaRepository.exists(taskId);
	}
	
	private Task saveTask(Task task) {
		return taskJpaRepository.save(task);
	}
}
