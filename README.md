# Spring-Tutorial

1. [x] Create a github account

2. [ ] Create this very simple Spring application (start with just the backend as a set of REST endpoints)

  1. You probably want to start with Spring Boot and Spring 4 (makes applications a bit easier to write and has a lot of boilerplate configurations for you)  
  2. Your application should follow the standard MVC pattern, so you would have `@Controller` talking to `@Service` talking to Repositories/DAOs
  3. Your application should use a relational (SQL) database as a backend. So you don’t have to install one, you can look at options like H2 or HSQLDB which are lightweight databases that can also be in-memory (basically they are clean when your server starts up and die when you turn off your server).
  4. From a business requirements perspective here’s what I would have it do first  
    * [x] POST /user creates a new “user” and returns the user’s ID
    * [x] GET /user/{userid} returns the details of a user (you can choose what details you want, but I would suggest maybe email and user id)
    * [ ] GET /user/{userid}/tasks returns a list of tasks for the user with the given id
    * [ ] DELETE /user/{userid} “soft-delete” a user, basically means the user is not active
    * [ ] POST /user/{userid}/tasks creates a new task with the task given as part of the request body for the given user and returns the task id
    * [ ] PUT /tasks/{taskid} updates an existing task with the task given as part of the request body
    * [ ] DELETE /tasks/{taskid} performs a “soft-delete” that sets the task as inactive
    * [ ] Tasks for inactive users should not be able to be deleted and should return an appropriate HTTP error code (you can look up the 400 range error codes on Wikipedia).
    * [ ] Request bodies that are mal-formed should also return an appropriate HTTP error code (again, up to you what “appropriate” means)
  5. Make sure to include unit tests
  6. Make sure to include tests that make use of Spring MockMVC framework
  
3. [ ] Since it has no front-end, you can later develop a different front-end as a separate exercise as a completely different project.