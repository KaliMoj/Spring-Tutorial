package tasks.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import tasks.bol.UserNotFoundException;
import tasks.dao.Task;
import tasks.dao.User;
import tasks.dao.UserRepository;

@Component
public class TaskService {

	@Autowired
	private UserRepository userJpaRepository;

	@Transactional
	public long addTaskToUser(long userid, Task task) {
		User user = userJpaRepository.findOne(userid);
		if (user == null) throw new UserNotFoundException();
		user.addTask(task);
		System.out.println("ABOUT TO SAVE");
		user = userJpaRepository.save(user);
		System.out.println("DONE");
		return user.getTasks().get(user.getTasks().size()-1).getId();
	}
}
