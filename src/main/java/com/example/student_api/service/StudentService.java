package com.example.student_api.service;

import com.example.student_api.dto.StudentDTO;
import com.example.student_api.exception.ResourceNotFoundException;
import com.example.student_api.exception.ValidationException;
import com.example.student_api.model.Student;
import com.example.student_api.repository.StudentRepository;
import com.example.student_api.util.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing student data
 * Handles business logic for student operations
 */
@Service
@Transactional
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);
    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * Get all students
     *
     * @return List of all students with photos converted to Base64
     */
    @Transactional(readOnly = true)
    public List<StudentDTO> getAllStudents() {
        logger.debug("Getting all students");
        List<Student> students = studentRepository.findAll();
        return students.stream()
                .map(this::convertToBase64)
                .map(StudentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get paginated students
     *
     * @param pageable Pagination information
     * @return Page of students with photos converted to Base64
     */
    @Transactional(readOnly = true)
    public Page<StudentDTO> getAllStudentsPaginated(Pageable pageable) {
        logger.debug("Getting paginated students: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Student> studentPage = studentRepository.findAll(pageable);
        List<StudentDTO> convertedStudents = studentPage.getContent().stream()
                .map(this::convertToBase64)
                .map(StudentDTO::fromEntity)
                .collect(Collectors.toList());
        return new PageImpl<>(convertedStudents, pageable, studentPage.getTotalElements());
    }

    /**
     * Get student by ID
     *
     * @param id Student ID
     * @return Student with photo converted to Base64
     * @throws ResourceNotFoundException if student not found
     */
    @Transactional(readOnly = true)
    public StudentDTO getStudentById(Long id) {
        logger.debug("Getting student by ID: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
        return StudentDTO.fromEntity(convertToBase64(student));
    }

    /**
     * Get students by name
     *
     * @param name Name to search for
     * @return List of matching students with photos converted to Base64
     */
    @Transactional(readOnly = true)
    public List<StudentDTO> getStudentsByName(String name) {
        logger.debug("Searching students by name: {}", name);
        List<Student> students = studentRepository.findByNameContainingIgnoreCase(name);
        return students.stream()
                .map(this::convertToBase64)
                .map(StudentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get paginated students by name
     *
     * @param name Name to search for
     * @param pageable Pagination information
     * @return Page of matching students with photos converted to Base64
     */
    @Transactional(readOnly = true)
    public Page<StudentDTO> getStudentsByNamePaginated(String name, Pageable pageable) {
        logger.debug("Searching paginated students by name: {}, page={}, size={}",
                name, pageable.getPageNumber(), pageable.getPageSize());
        Page<Student> studentPage = studentRepository.findByNameContainingIgnoreCase(name, pageable);
        List<StudentDTO> convertedStudents = studentPage.getContent().stream()
                .map(this::convertToBase64)
                .map(StudentDTO::fromEntity)
                .collect(Collectors.toList());
        return new PageImpl<>(convertedStudents, pageable, studentPage.getTotalElements());
    }

    /**
     * Save a new student
     *
     * @param studentDTO Student data to save
     * @return Saved student with photo converted to Base64
     * @throws ValidationException if validation fails
     */
    public StudentDTO saveStudent(StudentDTO studentDTO) {
        logger.debug("Saving new student: {}", studentDTO.getName());
        validateStudent(studentDTO);

        Student student = studentDTO.toEntity();

        if (studentDTO.getPhotoBase64() != null && !studentDTO.getPhotoBase64().isEmpty()) {
            student.setPhoto(ImageUtil.decodeFromBase64(studentDTO.getPhotoBase64()));
        }

        Student savedStudent = studentRepository.save(student);
        logger.info("Student saved successfully with ID: {}", savedStudent.getId());
        return StudentDTO.fromEntity(convertToBase64(savedStudent));
    }

    /**
     * Update an existing student
     *
     * @param id Student ID to update
     * @param studentDTO Updated student data
     * @return Updated student with photo converted to Base64
     * @throws ResourceNotFoundException if student not found
     * @throws ValidationException if validation fails
     */
    public StudentDTO updateStudent(Long id, StudentDTO studentDTO) {
        logger.debug("Updating student with ID: {}", id);
        validateStudent(studentDTO);

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));

        student.setName(studentDTO.getName());
        student.setBirthDate(studentDTO.getBirthDate());
        student.setMobileNo(studentDTO.getMobileNo());

        // Update photo only if a new one is provided
        if (studentDTO.getPhotoBase64() != null && !studentDTO.getPhotoBase64().isEmpty()) {
            student.setPhoto(ImageUtil.decodeFromBase64(studentDTO.getPhotoBase64()));
        }

        Student updatedStudent = studentRepository.save(student);
        logger.info("Student updated successfully with ID: {}", updatedStudent.getId());
        return StudentDTO.fromEntity(convertToBase64(updatedStudent));
    }

    /**
     * Delete a student by ID
     *
     * @param id Student ID to delete
     * @throws ResourceNotFoundException if student not found
     */
    public void deleteStudent(Long id) {
        logger.debug("Deleting student with ID: {}", id);
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student", "id", id);
        }
        studentRepository.deleteById(id);
        logger.info("Student deleted successfully with ID: {}", id);
    }

    /**
     * Convert student photo to Base64
     *
     * @param student Student entity
     * @return Student with photo converted to Base64
     */
    private Student convertToBase64(Student student) {
        if (student.getPhoto() != null) {
            student.setPhotoBase64(ImageUtil.encodeToBase64(student.getPhoto()));
        }
        return student;
    }

    /**
     * Validate student data
     *
     * @param studentDTO Student data to validate
     * @throws ValidationException if validation fails
     */
    private void validateStudent(StudentDTO studentDTO) {
        if (studentDTO == null) {
            throw new ValidationException("Student data cannot be null");
        }

        ValidationException validationException = new ValidationException("Validation failed");

        if (studentDTO.getName() == null || studentDTO.getName().trim().isEmpty()) {
            validationException.addError("name", "Name cannot be empty");
        }

        if (studentDTO.getBirthDate() == null) {
            validationException.addError("birthDate", "Birth date cannot be empty");
        }

        if (studentDTO.getMobileNo() == null || studentDTO.getMobileNo().trim().isEmpty()) {
            validationException.addError("mobileNo", "Mobile number cannot be empty");
        }

        if (!validationException.getErrors().isEmpty()) {
            throw validationException;
        }
    }
}
