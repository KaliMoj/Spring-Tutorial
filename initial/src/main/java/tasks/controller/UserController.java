package tasks.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import tasks.bol.UserNotFoundException;
import tasks.dao.User;
import tasks.dao.UserRepository;

@RestController
public class UserController {
	
	@Autowired
	private UserRepository userJpaRepository;

	@RequestMapping(value="/user/add", method=RequestMethod.POST)
    public long createUser(@RequestBody User user) {
		
        // Create a new user and return the user's ID
		return userJpaRepository.save(user).getId();
    }
	
	@RequestMapping(value="/user/{userid}", method=RequestMethod.GET)
    public User getUser(@PathVariable("userid") long userid) {
        // Return user's information
		User user = userJpaRepository.findOne(userid);
		if (user == null) throw new UserNotFoundException();
		return user;
    }
	
	@RequestMapping(value="/user/{userid}", method=RequestMethod.DELETE)
    public void deleteUser(@PathVariable("userid") long userid) {
		User user = userJpaRepository.findOne(userid);
		if (user == null) throw new UserNotFoundException();
		user.setActive(false);
		userJpaRepository.save(user);
    }
}
