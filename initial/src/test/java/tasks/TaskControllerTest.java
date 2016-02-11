package tasks;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.h2.util.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import tasks.dao.Task;
import tasks.dao.TaskRepository;
import tasks.dao.User;
import tasks.dao.UserRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class TaskControllerTest {
	
	@Autowired
	private WebApplicationContext context;
	
	@Autowired
	private UserRepository userJpaRepository;
	@Autowired
	private TaskRepository taskJpaRepository;
	
	private MockMvc mvc;
	
	public User getMockUser() {
		User newUser = new User();
		newUser.setFirstName("Jaime");
		newUser.setLastName("Garcia");
		newUser.setEmail("noemail@nodomain.com");
		
		return newUser;
	}
	
	public Task getMockTask() {
		Task task = new Task();
		task.setDescription("Pick up le Moj");
		
		return task;
	}

	@Before
	public void setUp() {
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
	}
	
	@Test
	@Transactional
	public void createTask() throws Exception {
		long userId = createUser();
		getEmptyUserTaskList(userId);
		createUserTask(userId);
		getNonEmptyUserTaskList(userId);
	}
	
	@Test
	@Transactional
	public void deleteActiveUserTask() throws Exception {
		long userId = createUser();
		long taskId = createUserTask(userId);
		deleteActiveUserTask(taskId);
	}
	
	@Test
	@Transactional
	public void deleteInactiveUserTask() throws Exception {
		long userId = createUser();
		long taskId = createUserTask(userId);
		User user = userJpaRepository.findOne(userId);
		user.setActive(false);
		userJpaRepository.save(user);
		
		deleteInactiveUserTask(taskId);
	}
	
	public long createUser() {
		User user = getMockUser();
		return userJpaRepository.save(user).getId();
	}
	
	public void getEmptyUserTaskList(long userId) throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/user/" + userId + "/tasks")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string("[]"));
	}

	public long createUserTask(long userId) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Task task = getMockTask();
		
		MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/user/" + userId + "/tasks")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(task)))
				.andExpect(status().isOk())
				.andReturn();
		
		String response = result.getResponse().getContentAsString();
		assertFalse("Response is not empty", response.isEmpty());
		assertTrue("Response is a number", StringUtils.isNumber(response));
		return Long.parseUnsignedLong(response);
	}
	
	public void getNonEmptyUserTaskList(long userId) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		User user = userJpaRepository.findOne(userId);
		
		mvc.perform(MockMvcRequestBuilders.get("/user/" + userId + "/tasks")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string(equalTo(mapper.writeValueAsString(user.getTasks()))));
	}
	
	public void deleteActiveUserTask(long taskId) throws Exception {
		assertTrue("Task is active", taskJpaRepository.findOne(taskId).isActive());
		mvc.perform(MockMvcRequestBuilders.delete("/tasks/" + taskId)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		assertFalse("Task is not active", taskJpaRepository.findOne(taskId).isActive());
	}
	
	public void deleteInactiveUserTask(long taskId) throws Exception {
		Task task = taskJpaRepository.findOne(taskId);
		
		assertFalse("User is not active", task.getUser().isActive());
		mvc.perform(MockMvcRequestBuilders.delete("/tasks/" + taskId)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isMethodNotAllowed());
	}
}
