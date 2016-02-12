# TODO Application comments

* The name of the package `bol` doesn't represent what the package is actually for. I see that it has both a mix of services and exceptions. (I get it now :)). A package structure I would use is:
```
inside the /src/main/java	    // PRODUCTION CODE 
cat.lemoj.tasks                 // every Java application uses a fully qualified domain name an then you have your project name (tasks)
     - services	                // your business logic BOL
        - XXX                   // groups of services
        - impl                  // in bigger systems, you define the Java interfaces for 
                                // the services at the parent level but the implementations at 
                                // the subpackage level
            - XXX               // group of implementations
     - endpoints/controllers    // I don't like that spring calls them Controllers
        - XXX                   // groups of controllers
     - domain                   // domain objects including JPAs
        - dao/repository        // objects that let you take your JPA's and interact with a data
                                // store (database, file, etc)
        - XXX                   // subpackage name for larger domains that break out groups of JPAs
    - shared
        - exceptions            // exceptions!
        - util                  // utility libraries (keep these to a minimum, because utilities
                                // are hard to mock because they tend to have static methods)
```

Unit tests mirror the same package structure under the `src/test/java`

For integration and functional tests, I tend to order them by business functionality

```
inside the /src/*test/java folder: (you could have src/intTest/java, src/functionalTest/java, etc)

cat.lemoj.tasks
    - users
        + UserCreationTests.java        // just a sample, this application is too small to need more than 1
        + UserManagentTests.java
    - tasks
        + TaskManagementTests.java
        + TaskReportingTests.java
        + ...
```
 
* In test methods you should order your methods in a way that the `@Test` come first, then your `protected` or `private` helpers.
* Your  helper methods in tests should either be `protected` or `private`. `protected` if they need to be exposed to subclasses, `private` otherwise.
* Consider the following: 
My assumption from reading this method is that the you're creating a user and creating a task for that user. The two calls of `get*UserTaskList` are a bit confusing because they are named using a standard *getter/setter* convention. I actually think these methods are doing verifications inside of them. 
```java
	@Test
	@Transactional
	public void createTask() throws Exception {
		long userId = createUser();
		getEmptyUserTaskList(userId);
		createUserTask(userId);
		getNonEmptyUserTaskList(userId);
	}
```
compare to

```java
	@Test
	@Transactional
	public void canAddATaskToAUser() throws Exception {
		long userId = createUser();
        
		createUserTask(userId);
        
		verifyUserHasTasks(userId);
	}
```

I changed the reference to `TaskList` because I think that's implementation specific. Therefore, only referring to the user as having Tasks. Also, making your methods start with `verify*` leaves no ambiguity on what their intent is.

I also like to structure tests in a **Given**, **When**, **Then** format. Or **Assemble, Act, Assert**. With this in mind you would not have verifications until the end of the test. Following this, the first verification on the test above needs to be moved to the `UserControllerTest`. When I write the tests in this format, I usuall leave whitespace between the three clauses for readibility.

* For this test `deleteInactiveUserTask` consider renaming to `deletingAnInactiveUserTaskReturnsABadRequest` to make it easier for other developers to quickly scan what the tests do.

* Developer assertions should be done with the `assert` keyword

* Since both of your tests are using the same Spring annotations to configure the test class, consider creating an abstract parent test class for your controller tests. The annotations can be defined on that class. 
```java
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public abstract ControllerTest {
    // you can even add the MockMvc setup 
    // logic here
    protected MockMvc mvc;
    ...
    @Before 
    public void setup() {
        ...
    }
}
```

With this in place, you can even place shared logic here, like `createTestUser()` etc.

* **No!** `if (task == null) throw new TaskNotFoundException();`
  **yes!**
```java
    // and do NOT think the curly braces are optional!
    if (task == null) {
        throw new TaskNotFoundException();
    }
```
* :+1: `if (!taskExists(taskId))`, it implies intent!!! :)
+ :+1: `deactivateUser(user);` I like this name!
* Try to stick to using `Long`, `Integer` (the wrappers) for JPAs, endpoints, etc. The reason is that Java does autoboxing, so for example, this would fail at runtime in Java. You can't guarantee what the runtime behavior will be, and you may get an ugly stack trace. 

```java
public static void printMyAge(long myAge) {
    System.out.println("I am " + myAge + " years old!");
}

public static void main(String[] args) {
    Long ageArgument = null;
    if (args.length > 0) {
        ageArgument = Long.parse(args[0]);
    }
    printMyAge(ageArgument);
}
```

The caveat, is that for `boolean` I prefer the primitive type everywhere. Otherwise you're left doing this
` if (Boolean.TRUE.equals(myIndeterminateBooleanValue)) { ... }` because you have possible values of `true, false, null`.

* Also, **always** use the `equals()` method to check for equality, even for `Integer`, `Long`, and specially for `String`.