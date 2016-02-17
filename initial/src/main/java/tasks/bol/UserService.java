package tasks.bol;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tasks.dao.User;
import tasks.dao.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userJpaRepository;

	public User getUserById(Long userId) {
		User user = userJpaRepository.findOne(userId);
		if (user == null) throw new UserNotFoundException();
		return user;
	}
	
	public User saveUser(User user) {
		return userJpaRepository.save(user);
	}
	
	public void deleteUser(Long userId) {
		User user = getUserById(userId);
		deactivateUser(user);
		saveUser(user);
	}
	
	private void deactivateUser(User user) {
		user.setActive(false);
	}
}
