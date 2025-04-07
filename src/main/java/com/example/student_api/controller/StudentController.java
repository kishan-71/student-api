package com.example.student_api.controller;

import com.example.student_api.dto.ApiResponse;
import com.example.student_api.dto.PageResponse;
import com.example.student_api.dto.StudentDTO;
import com.example.student_api.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Student operations
 * Provides endpoints for CRUD operations on students
 */
@RestController
@RequestMapping("/api/students")
@Tag(name = "Student", description = "Student management APIs")
public class StudentController {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);
    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * Get all students
     *
     * @return List of all students
     */
    @Operation(summary = "Get all students", description = "Returns a list of all students")
    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentDTO>>> getAllStudents() {
        logger.debug("REST request to get all students");
        List<StudentDTO> students = studentService.getAllStudents();
        return ResponseEntity.ok(ApiResponse.success(students, "Students retrieved successfully"));
    }

    /**
     * Get paginated students
     *
     * @param page Page number (0-based)
     * @param size Page size
     * @return Paginated students
     */
    @Operation(summary = "Get all students with pagination", description = "Returns a paginated list of students")
    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PageResponse<StudentDTO>>> getAllStudentsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        logger.debug("REST request to get paginated students: page={}, size={}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        Page<StudentDTO> studentPage = studentService.getAllStudentsPaginated(pageable);

        PageResponse<StudentDTO> pageResponse = PageResponse.from(studentPage);
        return ResponseEntity.ok(ApiResponse.success(pageResponse, "Students retrieved successfully"));
    }

    /**
     * Get student by ID
     *
     * @param id Student ID
     * @return Student with the given ID
     */
    @Operation(summary = "Get student by ID", description = "Returns a student by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentDTO>> getStudentById(@PathVariable Long id) {
        logger.debug("REST request to get student by ID: {}", id);
        StudentDTO student = studentService.getStudentById(id);
        return ResponseEntity.ok(ApiResponse.success(student, "Student retrieved successfully"));
    }

    /**
     * Search students by name
     *
     * @param name Name to search for
     * @return List of matching students
     */
    @Operation(summary = "Search students by name", description = "Returns a list of students matching the name")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<StudentDTO>>> getStudentsByName(@RequestParam String name) {
        logger.debug("REST request to search students by name: {}", name);
        List<StudentDTO> students = studentService.getStudentsByName(name);
        return ResponseEntity.ok(ApiResponse.success(students, "Students retrieved successfully"));
    }

    /**
     * Search students by name with pagination
     *
     * @param name Name to search for
     * @param page Page number (0-based)
     * @param size Page size
     * @return Paginated matching students
     */
    @Operation(summary = "Search students by name with pagination",
               description = "Returns a paginated list of students matching the name")
    @GetMapping("/search/paged")
    public ResponseEntity<ApiResponse<PageResponse<StudentDTO>>> getStudentsByNamePaginated(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        logger.debug("REST request to search paginated students by name: {}, page={}, size={}",
                name, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        Page<StudentDTO> studentPage = studentService.getStudentsByNamePaginated(name, pageable);

        PageResponse<StudentDTO> pageResponse = PageResponse.from(studentPage);
        return ResponseEntity.ok(ApiResponse.success(pageResponse, "Students retrieved successfully"));
    }

    /**
     * Create a new student
     *
     * @param studentDTO Student data
     * @return Created student
     */
    @Operation(summary = "Create a new student", description = "Creates a new student and returns it")
    @PostMapping
    public ResponseEntity<ApiResponse<StudentDTO>> createStudent(@RequestBody StudentDTO studentDTO) {
        logger.debug("REST request to create student: {}", studentDTO.getName());
        StudentDTO createdStudent = studentService.saveStudent(studentDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdStudent, "Student created successfully"));
    }

    /**
     * Update an existing student
     *
     * @param id Student ID to update
     * @param studentDTO Updated student data
     * @return Updated student
     */
    @Operation(summary = "Update an existing student", description = "Updates a student and returns it")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentDTO>> updateStudent(
            @PathVariable Long id,
            @RequestBody StudentDTO studentDTO) {

        logger.debug("REST request to update student with ID: {}", id);
        StudentDTO updatedStudent = studentService.updateStudent(id, studentDTO);
        return ResponseEntity.ok(ApiResponse.success(updatedStudent, "Student updated successfully"));
    }

    /**
     * Delete a student
     *
     * @param id Student ID to delete
     * @return No content response
     */
    @Operation(summary = "Delete a student", description = "Deletes a student by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long id) {
        logger.debug("REST request to delete student with ID: {}", id);
        studentService.deleteStudent(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Student deleted successfully"));
    }
}
