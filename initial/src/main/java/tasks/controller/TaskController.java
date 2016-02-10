package tasks.controller;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
	@Autowired
	private TaskService taskService;

	@org.springframework.transaction.annotation.Transactional
	@RequestMapping(value="/user/{userid}/tasks", method=RequestMethod.POST)
    public long createUserTask(@PathVariable("userid") long userid, @RequestBody Task task) {
		return taskService.addTaskToUser(userid, task);
    }
	
	/**
	 * List tasks
	 * @param userid
	 * @return
	 */
	@RequestMapping(value="/user/{userid}/tasks", method=RequestMethod.GET)
    public List<Task> getUserTasks(@PathVariable("userid") long userid) {
		User user = userJpaRepository.findOne(userid);
		if (user == null) throw new UserNotFoundException();
		return user.getTasks();
    }
}
