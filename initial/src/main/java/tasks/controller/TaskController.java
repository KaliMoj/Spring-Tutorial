package tasks.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import tasks.bol.InactiveUserTaskException;
import tasks.bol.TaskNotFoundException;
import tasks.bol.UserNotFoundException;
import tasks.dao.Task;
import tasks.dao.TaskRepository;
import tasks.dao.User;
import tasks.dao.UserRepository;

@RestController
public class TaskController {
	
	@Autowired
	private TaskRepository taskJpaRepository;
	@Autowired
	private UserRepository userJpaRepository;

	@RequestMapping(value="/user/{userid}/tasks", method=RequestMethod.POST)
    public long createUserTask(@PathVariable("userid") long userid, @RequestBody Task task) {
		User user = userJpaRepository.findOne(userid);
		if (user == null) throw new UserNotFoundException();

		task.setUser(user);
		return taskJpaRepository.save(task).getId();
    }
	
	/**
	 * List tasks
	 * @param userid
	 * @return
	 */
	@RequestMapping(value="/user/{userid}/tasks", method=RequestMethod.GET)
    public Set<Task> getUserTasks(@PathVariable("userid") long userId) {
		User user = userJpaRepository.findOne(userId);
		if (user == null) throw new UserNotFoundException();
		return user.getTasks();
    }
	
	@RequestMapping(value="/tasks/{taskid}", method=RequestMethod.DELETE)
	public void deleteTask(@PathVariable("taskid") long taskId) {
		Task task = taskJpaRepository.findOne(taskId);
		if (task == null) throw new TaskNotFoundException();
		if (!task.getUser().isActive()) throw new InactiveUserTaskException();
		task.setActive(false);
		taskJpaRepository.save(task);
	}
}
