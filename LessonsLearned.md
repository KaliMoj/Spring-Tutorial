1. Infinite recursion when saving a task to a user

  - SOLUTION: Ensure that the `@OneToMany` annotation includes a `cascade` parameter
  
2. Multiple inserts occurring when adding a single task

  - SOLUTION: Originally had the declaration of tasks as `List<Tasks>` which was the cause of the problem. Changing to use `Set<Tasks>` corrected the multiple insertions.

3. Could not access database within test classes

  - SOLUTION: `@Autowired` annotation with declaration of required repository interface provides access to database.
  
4. IDs were not being reset between tests even with `@Transactional` annotation

  - SOLUTION: Tables were only created / dropped at the end of running all tests. So even though the newly created records were rolled back, the nature of database ids is that they continue incrementing. Changed assertions to check for non-empty and numeric response rather than a specific id.
  
5. Curl options to keep in mind

  * `-X` method request type (POST / PUT / DELETE etc)
  * `-v` place as one of the initial options; view header information
  * `-H` set header information (ie - `Content-Type`)
  * `-d` response body being submitted with the request 
