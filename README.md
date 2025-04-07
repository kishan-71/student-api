# Student Management System

A Spring Boot application for managing student records with photo upload functionality.

## Features

- CRUD operations for student management
- Photo upload and storage
- Pagination support (5 records per page)
- Search functionality
- RESTful API with Swagger documentation
- Responsive frontend UI

## Technology Stack

- **Backend**: Spring Boot 3.2.3, Spring Data JPA
- **Database**: MySQL
- **Frontend**: HTML, CSS, JavaScript, Bootstrap 5
- **Documentation**: Swagger/OpenAPI

## Project Structure

```
student-api/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── student_api/
│   │   │               ├── config/         # Configuration classes
│   │   │               ├── controller/     # REST controllers
│   │   │               ├── dto/            # Data Transfer Objects
│   │   │               ├── exception/      # Exception handling
│   │   │               ├── model/          # Entity classes
│   │   │               ├── repository/     # Data repositories
│   │   │               ├── service/        # Business logic
│   │   │               └── util/           # Utility classes
│   │   └── resources/
│   │       ├── static/                     # Frontend resources
│   │       └── application.properties      # Application configuration
│   └── test/                               # Test classes
└── pom.xml                                 # Maven configuration
```

## Getting Started

### Prerequisites

- Java 21
- Maven
- MySQL

### Database Setup

1. Create a MySQL database named `emp_db`
2. Create a user `emp_user` with password `emp_pass`
3. Grant all privileges on `emp_db` to `emp_user`

```sql
CREATE DATABASE emp_db;
CREATE USER 'emp_user'@'localhost' IDENTIFIED BY 'emp_pass';
GRANT ALL PRIVILEGES ON emp_db.* TO 'emp_user'@'localhost';
FLUSH PRIVILEGES;
```

### Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application using Maven:

```bash
mvn spring-boot:run
```

4. Access the application at http://localhost:8081
5. Access the Swagger UI at http://localhost:8081/swagger-ui.html

## API Endpoints

| Method | URL                           | Description                   |
|--------|-------------------------------|-------------------------------|
| GET    | /api/students                 | Get all students              |
| GET    | /api/students/paged           | Get paginated students        |
| GET    | /api/students/{id}            | Get student by ID             |
| GET    | /api/students/search          | Search students by name       |
| GET    | /api/students/search/paged    | Search with pagination        |
| POST   | /api/students                 | Create a new student          |
| PUT    | /api/students/{id}            | Update an existing student    |
| DELETE | /api/students/{id}            | Delete a student              |

## Application Flow

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Browser   │────▶│ Controllers │────▶│  Services   │────▶│ Repositories│
└─────────────┘     └─────────────┘     └─────────────┘     └─────────────┘
                           │                   │                    │
                           │                   │                    │
                           ▼                   ▼                    ▼
                    ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
                    │    DTOs     │     │   Entities  │     │  Database   │
                    └─────────────┘     └─────────────┘     └─────────────┘
```

## Detailed API Methods and Data Flow

### API Methods

| Method | URL                           | Description                   | Request Body                  | Response Body                 |
|--------|-------------------------------|-------------------------------|-------------------------------|-------------------------------|
| GET    | /api/students                 | Get all students              | None                          | ApiResponse<List<StudentDTO>> |
| GET    | /api/students/paged           | Get paginated students        | page, size (query params)     | ApiResponse<PageResponse<StudentDTO>> |
| GET    | /api/students/{id}            | Get student by ID             | None                          | ApiResponse<StudentDTO>       |
| GET    | /api/students/search          | Search students by name       | name (query param)            | ApiResponse<List<StudentDTO>> |
| GET    | /api/students/search/paged    | Search with pagination        | name, page, size (query params) | ApiResponse<PageResponse<StudentDTO>> |
| POST   | /api/students                 | Create a new student          | StudentDTO                    | ApiResponse<StudentDTO>       |
| PUT    | /api/students/{id}            | Update an existing student    | StudentDTO                    | ApiResponse<StudentDTO>       |
| DELETE | /api/students/{id}            | Delete a student              | None                          | ApiResponse<Void>             |

### Data Flow for Each Function

#### 1. Get All Students
```
Browser → GET /api/students → StudentController.getAllStudents() → StudentService.getAllStudents() → StudentRepository.findAll() → Database
                                                                                                  ↓
                                                                                        Convert to StudentDTO
                                                                                                  ↓
                                                                                        Wrap in ApiResponse
                                                                                                  ↓
                                                                                        Return to Browser
```

#### 2. Get Paginated Students
```
Browser → GET /api/students/paged?page=0&size=5 → StudentController.getAllStudentsPaginated() → StudentService.getAllStudentsPaginated() → StudentRepository.findAll(pageable) → Database
                                                                                                                                        ↓
                                                                                                                              Convert to StudentDTO
                                                                                                                                        ↓
                                                                                                                              Create PageResponse
                                                                                                                                        ↓
                                                                                                                              Wrap in ApiResponse
                                                                                                                                        ↓
                                                                                                                              Return to Browser
```

#### 3. Get Student by ID
```
Browser → GET /api/students/{id} → StudentController.getStudentById() → StudentService.getStudentById() → StudentRepository.findById() → Database
                                                                                                        ↓
                                                                                              Convert to StudentDTO
                                                                                                        ↓
                                                                                              Wrap in ApiResponse
                                                                                                        ↓
                                                                                              Return to Browser
```

#### 4. Search Students by Name
```
Browser → GET /api/students/search?name=John → StudentController.getStudentsByName() → StudentService.getStudentsByName() → StudentRepository.findByNameContainingIgnoreCase() → Database
                                                                                                                          ↓
                                                                                                                Convert to StudentDTO
                                                                                                                          ↓
                                                                                                                Wrap in ApiResponse
                                                                                                                          ↓
                                                                                                                Return to Browser
```

#### 5. Search Students with Pagination
```
Browser → GET /api/students/search/paged?name=John&page=0&size=5 → StudentController.getStudentsByNamePaginated() → StudentService.getStudentsByNamePaginated() → StudentRepository.findByNameContainingIgnoreCase(name, pageable) → Database
                                                                                                                                                              ↓
                                                                                                                                                    Convert to StudentDTO
                                                                                                                                                              ↓
                                                                                                                                                    Create PageResponse
                                                                                                                                                              ↓
                                                                                                                                                    Wrap in ApiResponse
                                                                                                                                                              ↓
                                                                                                                                                    Return to Browser
```

#### 6. Create a New Student
```
Browser → POST /api/students (StudentDTO) → StudentController.createStudent() → StudentService.saveStudent() → Validate Student
                                                                                                            ↓
                                                                                              Convert to Entity
                                                                                                            ↓
                                                                                              Process Photo (if any)
                                                                                                            ↓
                                                                                              StudentRepository.save() → Database
                                                                                                            ↓
                                                                                              Convert to StudentDTO
                                                                                                            ↓
                                                                                              Wrap in ApiResponse
                                                                                                            ↓
                                                                                              Return to Browser
```

#### 7. Update an Existing Student
```
Browser → PUT /api/students/{id} (StudentDTO) → StudentController.updateStudent() → StudentService.updateStudent() → Check if Student exists
                                                                                                                  ↓
                                                                                                        Validate Student
                                                                                                                  ↓
                                                                                                        Update Entity fields
                                                                                                                  ↓
                                                                                                        Process Photo (if changed)
                                                                                                                  ↓
                                                                                                        StudentRepository.save() → Database
                                                                                                                  ↓
                                                                                                        Convert to StudentDTO
                                                                                                                  ↓
                                                                                                        Wrap in ApiResponse
                                                                                                                  ↓
                                                                                                        Return to Browser
```

#### 8. Delete a Student
```
Browser → DELETE /api/students/{id} → StudentController.deleteStudent() → StudentService.deleteStudent() → Check if Student exists
                                                                                                        ↓
                                                                                              StudentRepository.deleteById() → Database
                                                                                                        ↓
                                                                                              Create success ApiResponse
                                                                                                        ↓
                                                                                              Return to Browser
```

### Frontend-Backend Interaction

1. **Loading Students**:
   - Frontend makes GET request to `/api/students`
   - Backend returns all students as JSON
   - Frontend displays students in a table
   - Pagination is handled client-side

2. **Adding a Student**:
   - User fills out form
   - Frontend converts photo to Base64
   - Frontend makes POST request to `/api/students`
   - Backend validates, saves, and returns the new student
   - Frontend updates the table

3. **Editing a Student**:
   - User clicks Edit button
   - Frontend makes GET request to `/api/students/{id}`
   - Frontend populates form with student data
   - User makes changes
   - Frontend makes PUT request to `/api/students/{id}`
   - Backend validates, updates, and returns the updated student
   - Frontend updates the table

4. **Deleting a Student**:
   - User clicks Delete button
   - Frontend shows confirmation dialog
   - Frontend makes DELETE request to `/api/students/{id}`
   - Backend deletes the student
   - Frontend removes the student from the table

5. **Searching Students**:
   - User enters search term
   - Frontend makes GET request to `/api/students/search?name={term}`
   - Backend returns matching students
   - Frontend displays search results

## License

This project is licensed under the MIT License.
