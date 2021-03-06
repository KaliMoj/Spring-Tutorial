package tasks.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import tasks.bol.TaskService;
import tasks.bol.UserService;
import tasks.dao.Task;
import tasks.dao.User;

@RestController
@CrossOrigin(origins="*")
public class TaskController {
	
	@Autowired
	private TaskService taskService;
	@Autowired
	private UserService userService;

	@RequestMapping(value="/user/{userid}/tasks", method=RequestMethod.POST)
    public Task createUserTask(@PathVariable("userid") Long userId, @RequestBody Task task) {
		User user = userService.getUserById(userId);
		return taskService.createTaskForUser(task, user);
    }
	
	@RequestMapping(value="/user/{userid}/tasks", method=RequestMethod.GET)
    public Set<Task> getUserTasks(@PathVariable("userid") Long userId) {
		return userService.getUserById(userId).getTasks();
    }
	
	@RequestMapping(value="/tasks/{taskid}", method=RequestMethod.DELETE)
	public void deleteTask(@PathVariable("taskid") Long taskId) {
		taskService.deleteTask(taskId);
	}
	
	@RequestMapping(value="/tasks/{taskid}", method=RequestMethod.PUT)
    public void updateTask(@PathVariable("taskid") Long taskId, @RequestBody Task task) {
		taskService.updateTask(taskId, task);
    }
}
