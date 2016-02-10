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
import org.springframework.web.context.WebApplicationContext;

import tasks.dao.User;
import tasks.dao.UserRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class UserControllerTest {
	
	@Autowired
	private WebApplicationContext context;
	
	@Autowired
	private UserRepository userJpaRepository;
	
	private MockMvc mvc;
	
	public User getMockUser() {
		User newUser = new User();
		newUser.setFirstName("Crystal");
		newUser.setLastName("Garcia");
		newUser.setEmail("johndoe@nodomain.com");
		
		return newUser;
	}

	@Before
	public void setUp() {
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	@Test
	public void getUser() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		User user = getMockUser();
		long userId = userJpaRepository.save(user).getId();
		
		mvc.perform(MockMvcRequestBuilders.get("/user/" + userId).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string(equalTo(mapper.writeValueAsString(user))));
	}
	
	@Test
	public void createNewUser() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		User mockUser = getMockUser();
		mvc.perform(MockMvcRequestBuilders.post("/user/add")
				.content(mapper.writeValueAsString(mockUser)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	public void preventGetRequestCreateNewUser() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/user/add").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError());
	}
	
	@Test
	public void softDeleteUser() throws Exception {
		User user = getMockUser();
		long userId = userJpaRepository.save(user).getId();
		
		assertTrue("User is active", user.isActive());
		mvc.perform(MockMvcRequestBuilders.delete("/user/" + userId).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		assertFalse("User is not active", userJpaRepository.findOne(userId).isActive());
	}
}
