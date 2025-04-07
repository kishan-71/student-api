package com.example.student_api.dto;

import com.example.student_api.model.Student;

import java.time.LocalDate;

/**
 * Data Transfer Object for Student entity
 * Used for transferring student data between layers
 */
public class StudentDTO {
    private Long id;
    private String name;
    private LocalDate birthDate;
    private String mobileNo;
    private String photoBase64;

    public StudentDTO() {
    }

    public StudentDTO(Long id, String name, LocalDate birthDate, String mobileNo, String photoBase64) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.mobileNo = mobileNo;
        this.photoBase64 = photoBase64;
    }

    /**
     * Convert Student entity to StudentDTO
     *
     * @param student Student entity
     * @return StudentDTO
     */
    public static StudentDTO fromEntity(Student student) {
        return new StudentDTO(
                student.getId(),
                student.getName(),
                student.getBirthDate(),
                student.getMobileNo(),
                student.getPhotoBase64()
        );
    }

    /**
     * Convert StudentDTO to Student entity
     *
     * @return Student entity
     */
    public Student toEntity() {
        Student student = new Student();
        student.setId(this.id);
        student.setName(this.name);
        student.setBirthDate(this.birthDate);
        student.setMobileNo(this.mobileNo);
        student.setPhotoBase64(this.photoBase64);
        return student;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getPhotoBase64() {
        return photoBase64;
    }

    public void setPhotoBase64(String photoBase64) {
        this.photoBase64 = photoBase64;
    }
}
