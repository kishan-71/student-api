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

- **Backend**: Spring Boot 3.4.4, Spring Data JPA
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

## License

This project is licensed under the MIT License.
