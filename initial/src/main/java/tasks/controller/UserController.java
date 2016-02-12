package tasks.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import tasks.bol.UserService;
import tasks.dao.User;

@RestController
public class UserController {

	@Autowired
	private UserService userService;
	
	@RequestMapping(value="/user/add", method=RequestMethod.POST)
    public long createUser(@RequestBody User user) {
		return userService.saveUser(user).getId();
    }
	
	@RequestMapping(value="/user/{userid}", method=RequestMethod.GET)
    public User getUser(@PathVariable("userid") long userId) {
        return userService.getUserById(userId);
    }
	
	@RequestMapping(value="/user/{userid}", method=RequestMethod.DELETE)
    public void deleteUser(@PathVariable("userid") long userId) {
		userService.deleteUser(userId);
    }
}
