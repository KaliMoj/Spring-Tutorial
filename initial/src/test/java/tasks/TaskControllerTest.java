package tasks;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import tasks.bol.TaskService;
import tasks.bol.UserService;
import tasks.dao.Task;
import tasks.dao.User;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class TaskControllerTest {
	
	@Autowired
	private WebApplicationContext context;
	
	@Autowired
	private UserService userService;
	@Autowired
	private TaskService taskService;
	
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
		Long userId = createUser();
		getEmptyUserTaskList(userId);
		createUserTask(userId);
		getNonEmptyUserTaskList(userId);
	}
	
	@Test
	@Transactional
	public void updateTask() throws Exception {
		Long userId = createUser();
		Long taskId = createUserTask(userId);
		//Task task = taskService.getTaskById(taskId);
		
		//task.setDescription("Give le Moj his meds");
		updateTask(taskId);
	}
	
	@Test
	@Transactional
	public void deleteActiveUserTask() throws Exception {
		Long userId = createUser();
		Long taskId = createUserTask(userId);
		deleteActiveUserTask(taskId);
	}
	
	@Test
	@Transactional
	public void deleteInactiveUserTask() throws Exception {
		Long userId = createUser();
		Long taskId = createUserTask(userId);
		userService.deleteUser(userId);
		
		deleteInactiveUserTask(taskId);
	}
	
	public Long createUser() {
		User user = getMockUser();
		return userService.saveUser(user).getId();
	}
	
	public void getEmptyUserTaskList(Long userId) throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/user/" + userId + "/tasks")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string("[]"));
	}

	public Long createUserTask(Long userId) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Task task = getMockTask();
		
		MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/user/" + userId + "/tasks")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(task)))
				.andExpect(status().isOk())
				.andReturn();
		
		byte[] response = result.getResponse().getContentAsByteArray();
		Task taskResponse = mapper.readValue(response, Task.class);
		assertFalse("Response is not empty", response.length == 0);
		return taskResponse.getId();
	}
	
	public void getNonEmptyUserTaskList(Long userId) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		User user = userService.getUserById(userId);
		
		mvc.perform(MockMvcRequestBuilders.get("/user/" + userId + "/tasks")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string(equalTo(mapper.writeValueAsString(user.getTasks()))));
	}
	
	public void updateTask(Long taskId) throws Exception {
		Task taskBeforeUpdate = taskService.getTaskById(taskId);
		String taskDescriptionBeforeUpdate = taskBeforeUpdate.getDescription();
		Long taskUserIdBeforeUpdate = taskBeforeUpdate.getUser().getId();
		
		mvc.perform(MockMvcRequestBuilders.put("/tasks/" + taskId)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"description\":\"Give le Moj his meds\"}"))
				.andExpect(status().isOk());

		Task taskAfterUpdate = taskService.getTaskById(taskId);
		
		assertEquals("Task has the same user id", taskUserIdBeforeUpdate, taskAfterUpdate.getUser().getId());
		assertFalse("Task has different descriptions", taskDescriptionBeforeUpdate.equals(taskAfterUpdate.getDescription()));
	}
	
	public void deleteActiveUserTask(Long taskId) throws Exception {
		assertTrue("Task is active", taskService.getTaskById(taskId).isActive());
		mvc.perform(MockMvcRequestBuilders.delete("/tasks/" + taskId)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		assertFalse("Task is not active", taskService.getTaskById(taskId).isActive());
	}
	
	public void deleteInactiveUserTask(Long taskId) throws Exception {
		Task task = taskService.getTaskById(taskId);
		
		assertFalse("User is not active", task.getUser().isActive());
		mvc.perform(MockMvcRequestBuilders.delete("/tasks/" + taskId)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}
}
