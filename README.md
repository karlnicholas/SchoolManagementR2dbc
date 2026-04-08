# School Management System REST API

This is a Spring Boot application that provides a REST API for a simple school management system. It allows for managing schools, students, courses, and instructors using a JPA-based persistence layer with an H2 in-memory database.

## Technologies Used
- **Java 21**
- **Spring Boot 3.3.0**
- **Spring Data JPA**
- **Maven**
- **H2 Database**
- **Lombok**

## How to Run
1.  **Prerequisites**: Make sure you have JDK 21 and Maven installed.
2.  **Navigate to the project directory**:
    ```bash
    cd school-management-api
    ```
3.  **Build and run the application**:
    ```bash
    ./mvnw spring-boot:run
    ```
4. The application will start on `http://localhost:8080`.
5. You can access the H2 console at `http://localhost:8080/h2-console` with the JDBC URL `jdbc:h2:mem:schooldb`, username `sa`, and an empty password.

## API Endpoints

Here are some example `curl` commands for the available endpoints.

### School
*   **Create a School**
    ```bash
    curl -X POST http://localhost:8080/api/school \
    -H "Content-Type: application/json" \
    -d '{"name": "Springfield University"}'
    ```
*   **Get a School by Name**
    ```bash
    curl http://localhost:8080/api/school/Springfield%20University
    ```

### Instructor
*   **Create an Instructor**
    ```bash
    curl -X POST http://localhost:8080/api/instructor \
    -H "Content-Type: application/json" \
    -d '{"name": "Dr. Smith", "schoolName": "Springfield University"}'
    ```
*   **Get an Instructor by Name**
    ```bash
    curl http://localhost:8080/api/instructor/Dr.%20Smith
    ```

### Student
*   **Create a Student**
    ```bash
    curl -X POST http://localhost:8080/api/student \
    -H "Content-Type: application/json" \
    -d '{"name": "Alice", "schoolName": "Springfield University"}'
    ```
*   **Get a Student by Name**
    ```bash
    curl http://localhost:8080/api/student/Alice
    ```

### Course
*   **Create a Course**
    ```bash
    curl -X POST http://localhost:8080/api/course \
    -H "Content-Type: application/json" \
    -d '{"name": "Intro to JPA", "schoolName": "Springfield University", "instructorName": "Dr. Smith"}'
    ```
*   **Get a Course by Name**
    ```bash
    curl http://localhost:8080/api/course/Intro%20to%20JPA
    ```

### Business Logic
*   **Add a Student to a Course**
    ```bash
    # First, create another student
    curl -X POST http://localhost:8080/api/student \
    -H "Content-Type: application/json" \
    -d '{"name": "Bob", "schoolName": "Springfield University"}'
    
    # Now, add Bob to the course
    curl -X POST http://localhost:8080/api/course/Intro%20to%20JPA/students/Bob
    ```

## SQL Query Optimization Justification

The requirement was to ensure efficient SQL query generation, particularly for the `POST /{courseName}/students/{studentName}` endpoint.

### Design Choice: Relationship Ownership

In the `Student-Course` many-to-many relationship, the `Student` entity was designated as the **owning side**. This means the `Student` entity's table metadata includes the `@JoinTable` annotation, making it responsible for managing the `student_courses` join table.

### `addStudentToCourse` Endpoint Analysis

The `CourseService.addStudentToCourse` method triggers the following minimal and efficient SQL queries:

1.  **Fetch the Student**: A targeted `SELECT` to find the student by their unique name.
2.  **Fetch the Course**: A targeted `SELECT` to find the course by its unique name.
3.  **Fetch Existing Associations**: A `SELECT` on the join table for the given student to initialize the lazy collection.
4.  **Insert into Join Table**: A single, direct `INSERT` into the join table to create the new association.

### Why this is Efficient:

-   **No N+1 Problems**: Queries are targeted lookups by name.
-   **Minimal Data Transfer**: Only necessary entities are fetched due to `FetchType.LAZY`.
-   **Direct Manipulation**: Modifying the collection on the owning side (`Student`) translates directly to an `INSERT` on the join table, which is the most efficient way to manage this relationship in JPA.
