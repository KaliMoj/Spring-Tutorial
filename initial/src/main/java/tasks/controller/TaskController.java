package tasks.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
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
public class TaskController {
	
	@Autowired
	private TaskService taskService;
	@Autowired
	private UserService userService;

	@RequestMapping(value="/user/{userid}/tasks", method=RequestMethod.POST)
    public long createUserTask(@PathVariable("userid") long userId, @RequestBody Task task) {
		User user = userService.getUserById(userId);
		return taskService.createTaskForUser(task, user).getId();
    }
	
	@RequestMapping(value="/user/{userid}/tasks", method=RequestMethod.GET)
    public Set<Task> getUserTasks(@PathVariable("userid") long userId) {
		return userService.getUserById(userId).getTasks();
    }
	
	@RequestMapping(value="/tasks/{taskid}", method=RequestMethod.DELETE)
	public void deleteTask(@PathVariable("taskid") long taskId) {
		taskService.deleteTask(taskId);
	}
	
	@RequestMapping(value="/tasks/{taskid}", method=RequestMethod.PUT)
    public void updateTask(@PathVariable("taskid") long taskId, @RequestBody Task task) {
		taskService.updateTask(taskId, task);
    }
}
