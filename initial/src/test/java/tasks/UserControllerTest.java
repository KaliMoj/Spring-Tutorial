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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import tasks.bol.UserService;
import tasks.dao.User;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class UserControllerTest {
	
	@Autowired
	private WebApplicationContext context;
	
	@Autowired
	private UserService userService;
	
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
	@Transactional
	public void createUser() throws Exception {
		preventGetRequestCreateNewUser();
		Long userId = createNewUser();
		getUser(userId);
	}
	
	@Test
	@Transactional
	public void deleteUser() throws Exception {
		Long userId = userService.saveUser(getMockUser()).getId();
		softDeleteUser(userId);
	}

	public void getUser(Long userId) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		User user = userService.getUserById(userId);
		
		mvc.perform(MockMvcRequestBuilders.get("/user/" + userId).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string(equalTo(mapper.writeValueAsString(user))));
	}
	
	public Long createNewUser() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		User mockUser = getMockUser();
		MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/user")
				.content(mapper.writeValueAsString(mockUser)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		
		String response = result.getResponse().getContentAsString();
		assertFalse("Response is not empty", response.isEmpty());
		assertTrue("Response is a number", StringUtils.isNumber(response));
		return Long.parseUnsignedLong(response);
	}
	
	public void preventGetRequestCreateNewUser() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/user").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError());
	}
	
	public void softDeleteUser(Long userId) throws Exception {
		assertTrue("User is active", userService.getUserById(userId).isActive());
		mvc.perform(MockMvcRequestBuilders.delete("/user/" + userId).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		assertFalse("User is not active", userService.getUserById(userId).isActive());
	}
}
