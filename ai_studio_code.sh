#!/bin/bash

# A script to scaffold the Spring Boot School Management API project.

echo "[2/4] Configuring project files (pom.xml, application.properties)..."

# 2. Overwrite pom.xml to ensure exact versions and config
cat <<EOF > pom.xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.0</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>org.example</groupId>
    <artifactId>school-management-api</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>school-management-api</name>
    <description>School Management System REST API</description>
    <properties>
        <java.version>21</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
EOF

# 3. Overwrite application.properties
cat <<EOF > src/main/resources/application.properties
# H2 Database Configuration
spring.datasource.url=jdbc:h2:mem:schooldb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true

# JPA/Hibernate Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
EOF

# 4. Rename main application class for clarity
mv ${BASE_PACKAGE_PATH}/SchoolManagementApiApplication.java ${BASE_PACKAGE_PATH}/SchoolManagementApiApplication.java.bak
cat <<EOF > ${BASE_PACKAGE_PATH}/SchoolManagementApiApplication.java
package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SchoolManagementApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchoolManagementApiApplication.class, args);
	}

}
EOF

echo "[3/4] Creating packages and source files..."

# --- Create Packages ---
mkdir -p ${BASE_PACKAGE_PATH}/{api,dto,exception,mapper,model,repository,service}

# --- Create Model Entities ---
echo "  - Creating model entities..."
cat <<EOF > ${BASE_PACKAGE_PATH}/model/School.java
package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class School {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Course> courses;

    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Student> students;

    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Instructor> instructors;
}
EOF

cat <<EOF > ${BASE_PACKAGE_PATH}/model/Student.java
package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    @ToString.Exclude
    private School school;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "student_courses",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    @ToString.Exclude
    private Set<Course> courses;
}
EOF

cat <<EOF > ${BASE_PACKAGE_PATH}/model/Course.java
package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    @ToString.Exclude
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    @ToString.Exclude
    private Instructor instructor;

    @ManyToMany(mappedBy = "courses", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Student> students;
}
EOF

cat <<EOF > ${BASE_PACKAGE_PATH}/model/Instructor.java
package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Instructor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    @ToString.Exclude
    private School school;

    @OneToMany(mappedBy = "instructor", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Course> courses;
}
EOF

# --- Create DTOs ---
echo "  - Creating DTOs..."
cat <<EOF > ${BASE_PACKAGE_PATH}/dto/StudentDto.java
package org.example.dto;
public record StudentDto(Long id, String name) {}
EOF

cat <<EOF > ${BASE_PACKAGE_PATH}/dto/CourseDto.java
package org.example.dto;
public record CourseDto(Long id, String name) {}
EOF

cat <<EOF > ${BASE_PACKAGE_PATH}/dto/InstructorDto.java
package org.example.dto;
public record InstructorDto(Long id, String name) {}
EOF

cat <<EOF > ${BASE_PACKAGE_PATH}/dto/StudentDetailDto.java
package org.example.dto;
import java.util.List;
public record StudentDetailDto(Long id, String name, List<CourseDto> courses) {}
EOF

cat <<EOF > ${BASE_PACKAGE_PATH}/dto/CourseDetailDto.java
package org.example.dto;
import java.util.List;
public record CourseDetailDto(Long id, String name, InstructorDto instructor, List<StudentDto> students) {}
EOF

cat <<EOF > ${BASE_PACKAGE_PATH}/dto/InstructorDetailDto.java
package org.example.dto;
import java.util.List;
public record InstructorDetailDto(Long id, String name, List<CourseDto> courses) {}
EOF

cat <<EOF > ${BASE_PACKAGE_PATH}/dto/SchoolDto.java
package org.example.dto;
import java.util.List;
public record SchoolDto(Long id, String name, List<InstructorDto> instructors, List<CourseDto> courses, List<StudentDto> students) {}
EOF

cat <<EOF > ${BASE_PACKAGE_PATH}/dto/CreateSchoolRequest.java
package org.example.dto;
public record CreateSchoolRequest(String name) {}
EOF

cat <<EOF > ${BASE_PACKAGE_PATH}/dto/CreateStudentRequest.java
package org.example.dto;
public record CreateStudentRequest(String name, String schoolName) {}
EOF

cat <<EOF > ${BASE_PACKAGE_PATH}/dto/CreateInstructorRequest.java
package org.example.dto;
public record CreateInstructorRequest(String name, String schoolName) {}
EOF

cat <<EOF > ${BASE_PACKAGE_PATH}/dto/CreateCourseRequest.java
package org.example.dto;
public record CreateCourseRequest(String name, String schoolName, String instructorName) {}
EOF

cat <<EOF > ${BASE_PACKAGE_PATH}/dto/UpdateRequest.java
package org.example.dto;
public record UpdateRequest(String newName) {}
EOF

# --- Create Repositories ---
echo "  - Creating repositories..."
cat <<EOF > ${BASE_PACKAGE_PATH}/repository/SchoolRepository.java
package org.example.repository;
import org.example.model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface SchoolRepository extends JpaRepository<School, Long> {
    Optional<School> findByName(String name);
}
EOF

cat <<EOF > ${BASE_PACKAGE_PATH}/repository/StudentRepository.java
package org.example.repository;
import org.example.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByName(String name);
}
EOF

cat <<EOF > ${BASE_PACKAGE_PATH}/repository/CourseRepository.java
package org.example.repository;
import org.example.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByName(String name);
}
EOF

cat <<EOF > ${BASE_PACKAGE_PATH}/repository/InstructorRepository.java
package org.example.repository;
import org.example.model.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    Optional<Instructor> findByName(String name);
}
EOF

# --- Create Exception Handling ---
echo "  - Creating exception handlers..."
cat <<EOF > ${BASE_PACKAGE_PATH}/exception/ResourceNotFoundException.java
package org.example.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
EOF

cat <<EOF > ${BASE_PACKAGE_PATH}/api/GlobalExceptionHandler.java
package org.example.api;

import org.example.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ResponseEntity<>(Map.of("error", ex.getMessage()), HttpStatus.NOT_FOUND);
    }
}
EOF

# --- Create Mapper ---
echo "  - Creating mapper..."
cat <<EOF > ${BASE_PACKAGE_PATH}/mapper/EntityDtoMapper.java
package org.example.mapper;

import org.example.dto.*;
import org.example.model.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EntityDtoMapper {

    public static StudentDto toStudentDto(Student student) {
        return new StudentDto(student.getId(), student.getName());
    }
    
    public static CourseDto toCourseDto(Course course) {
        return new CourseDto(course.getId(), course.getName());
    }

    public static InstructorDto toInstructorDto(Instructor instructor) {
        return new InstructorDto(instructor.getId(), instructor.getName());
    }

    public static StudentDetailDto toStudentDetailDto(Student student) {
        List<CourseDto> courses = student.getCourses() == null ? Collections.emptyList() :
                student.getCourses().stream().map(EntityDtoMapper::toCourseDto).collect(Collectors.toList());
        return new StudentDetailDto(student.getId(), student.getName(), courses);
    }

    public static CourseDetailDto toCourseDetailDto(Course course) {
        InstructorDto instructorDto = course.getInstructor() != null ? toInstructorDto(course.getInstructor()) : null;
        List<StudentDto> students = course.getStudents() == null ? Collections.emptyList() :
                course.getStudents().stream().map(EntityDtoMapper::toStudentDto).collect(Collectors.toList());
        return new CourseDetailDto(course.getId(), course.getName(), instructorDto, students);
    }

    public static InstructorDetailDto toInstructorDetailDto(Instructor instructor) {
        List<CourseDto> courses = instructor.getCourses() == null ? Collections.emptyList() :
                instructor.getCourses().stream().map(EntityDtoMapper::toCourseDto).collect(Collectors.toList());
        return new InstructorDetailDto(instructor.getId(), instructor.getName(), courses);
    }
    
    public static SchoolDto toSchoolDto(School school) {
        List<InstructorDto> instructors = school.getInstructors() == null ? Collections.emptyList() :
                school.getInstructors().stream().map(EntityDtoMapper::toInstructorDto).collect(Collectors.toList());
        List<CourseDto> courses = school.getCourses() == null ? Collections.emptyList() :
                school.getCourses().stream().map(EntityDtoMapper::toCourseDto).collect(Collectors.toList());
        List<StudentDto> students = school.getStudents() == null ? Collections.emptyList() :
                school.getStudents().stream().map(EntityDtoMapper::toStudentDto).collect(Collectors.toList());
        return new SchoolDto(school.getId(), school.getName(), instructors, courses, students);
    }
}
EOF

# --- Create Services ---
echo "  - Creating services..."
cat <<EOF > ${BASE_PACKAGE_PATH}/service/SchoolService.java
package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.CreateSchoolRequest;
import org.example.dto.SchoolDto;
import org.example.exception.ResourceNotFoundException;
import org.example.mapper.EntityDtoMapper;
import org.example.model.School;
import org.example.repository.SchoolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SchoolService {

    private final SchoolRepository schoolRepository;

    public SchoolDto createSchool(CreateSchoolRequest request) {
        School school = new School();
        school.setName(request.name());
        return EntityDtoMapper.toSchoolDto(schoolRepository.save(school));
    }

    @Transactional(readOnly = true)
    public SchoolDto getSchoolByName(String name) {
        School school = schoolRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with name: " + name));
        // Eagerly fetch collections for the DTO
        school.getStudents().size();
        school.getCourses().size();
        school.getInstructors().size();
        return EntityDtoMapper.toSchoolDto(school);
    }
}
EOF

cat <<EOF > ${BASE_PACKAGE_PATH}/service/StudentService.java
package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.*;
import org.example.exception.ResourceNotFoundException;
import org.example.mapper.EntityDtoMapper;
import org.example.model.School;
import org.example.model.Student;
import org.example.repository.SchoolRepository;
import org.example.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final SchoolRepository schoolRepository;

    @Transactional
    public StudentDto createStudent(CreateStudentRequest request) {
        School school = schoolRepository.findByName(request.schoolName())
                .orElseThrow(() -> new ResourceNotFoundException("School not found: " + request.schoolName()));
        Student student = new Student();
        student.setName(request.name());
        student.setSchool(school);
        return EntityDtoMapper.toStudentDto(studentRepository.save(student));
    }
    
    @Transactional(readOnly = true)
    public StudentDetailDto getStudentByName(String name) {
        Student student = studentRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with name: " + name));
        student.getCourses().size(); // Initialize courses
        return EntityDtoMapper.toStudentDetailDto(student);
    }

    public List<StudentDto> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(EntityDtoMapper::toStudentDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public StudentDto updateStudent(String name, UpdateRequest request) {
        Student student = studentRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with name: " + name));
        student.setName(request.newName());
        return EntityDtoMapper.toStudentDto(studentRepository.save(student));
    }

    @Transactional
    public void deleteStudent(String name) {
        Student student = studentRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with name: " + name));
        studentRepository.delete(student);
    }
}
EOF

cat <<EOF > ${BASE_PACKAGE_PATH}/service/InstructorService.java
package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.*;
import org.example.exception.ResourceNotFoundException;
import org.example.mapper.EntityDtoMapper;
import org.example.model.Instructor;
import org.example.model.School;
import org.example.repository.InstructorRepository;
import org.example.repository.SchoolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstructorService {
    private final InstructorRepository instructorRepository;
    private final SchoolRepository schoolRepository;

    @Transactional
    public InstructorDto createInstructor(CreateInstructorRequest request) {
        School school = schoolRepository.findByName(request.schoolName())
                .orElseThrow(() -> new ResourceNotFoundException("School not found: " + request.schoolName()));
        Instructor instructor = new Instructor();
        instructor.setName(request.name());
        instructor.setSchool(school);
        return EntityDtoMapper.toInstructorDto(instructorRepository.save(instructor));
    }

    @Transactional(readOnly = true)
    public InstructorDetailDto getInstructorByName(String name) {
        Instructor instructor = instructorRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with name: " + name));
        instructor.getCourses().size(); // Initialize courses
        return EntityDtoMapper.toInstructorDetailDto(instructor);
    }

    public List<InstructorDto> getAllInstructors() {
        return instructorRepository.findAll().stream()
                .map(EntityDtoMapper::toInstructorDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public InstructorDto updateInstructor(String name, UpdateRequest request) {
        Instructor instructor = instructorRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with name: " + name));
        instructor.setName(request.newName());
        return EntityDtoMapper.toInstructorDto(instructorRepository.save(instructor));
    }

    @Transactional
    public void deleteInstructor(String name) {
        Instructor instructor = instructorRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with name: " + name));
        instructorRepository.delete(instructor);
    }
}
EOF

cat <<EOF > ${BASE_PACKAGE_PATH}/service/CourseService.java
package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.*;
import org.example.exception.ResourceNotFoundException;
import org.example.mapper.EntityDtoMapper;
import org.example.model.Course;
import org.example.model.Instructor;
import org.example.model.School;
import org.example.model.Student;
import org.example.repository.CourseRepository;
import org.example.repository.InstructorRepository;
import org.example.repository.SchoolRepository;
import org.example.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final SchoolRepository schoolRepository;
    private final InstructorRepository instructorRepository;

    @Transactional
    public CourseDto createCourse(CreateCourseRequest request) {
        School school = schoolRepository.findByName(request.schoolName())
                .orElseThrow(() -> new ResourceNotFoundException("School not found: " + request.schoolName()));
        Instructor instructor = instructorRepository.findByName(request.instructorName())
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found: " + request.instructorName()));

        Course course = new Course();
        course.setName(request.name());
        course.setSchool(school);
        course.setInstructor(instructor);
        return EntityDtoMapper.toCourseDto(courseRepository.save(course));
    }

    @Transactional(readOnly = true)
    public CourseDetailDto getCourseByName(String name) {
        Course course = courseRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with name: " + name));
        course.getStudents().size(); // Initialize students collection
        return EntityDtoMapper.toCourseDetailDto(course);
    }

    @Transactional(readOnly = true)
    public List<CourseDto> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(EntityDtoMapper::toCourseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CourseDto updateCourse(String name, UpdateRequest request) {
        Course course = courseRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with name: " + name));
        course.setName(request.newName());
        return EntityDtoMapper.toCourseDto(courseRepository.save(course));
    }

    @Transactional
    public void deleteCourse(String name) {
        Course course = courseRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with name: " + name));
        
        for(Student student : new HashSet<>(course.getStudents())) {
            student.getCourses().remove(course);
        }
        
        courseRepository.delete(course);
    }

    @Transactional
    public void addStudentToCourse(String courseName, String studentName) {
        Student student = studentRepository.findByName(studentName)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with name: " + studentName));
        Course course = courseRepository.findByName(courseName)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with name: " + courseName));
        
        student.getCourses().add(course);
        studentRepository.save(student);
    }
}
EOF

# --- Create Controllers ---
echo "  - Creating controllers..."
cat <<EOF > ${BASE_PACKAGE_PATH}/api/SchoolController.java
package org.example.api;

import lombok.RequiredArgsConstructor;
import org.example.dto.CreateSchoolRequest;
import org.example.dto.SchoolDto;
import org.example.service.SchoolService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/school")
@RequiredArgsConstructor
public class SchoolController {
    
    private final SchoolService schoolService;
    
    @PostMapping
    public ResponseEntity<SchoolDto> createSchool(@RequestBody CreateSchoolRequest request) {
        // A simple POST to create a school, not in original reqs but useful for setup
        return new ResponseEntity<>(schoolService.createSchool(request), HttpStatus.CREATED);
    }

    @GetMapping("/{name}")
    public ResponseEntity<SchoolDto> getSchoolByName(@PathVariable String name) {
        return ResponseEntity.ok(schoolService.getSchoolByName(name));
    }
}
EOF

cat <<EOF > ${BASE_PACKAGE_PATH}/api/StudentController.java
package org.example.api;

import lombok.RequiredArgsConstructor;
import org.example.dto.*;
import org.example.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {
    
    private final StudentService studentService;
    
    @PostMapping
    public ResponseEntity<StudentDto> createStudent(@RequestBody CreateStudentRequest request) {
        return new ResponseEntity<>(studentService.createStudent(request), HttpStatus.CREATED);
    }
    
    @GetMapping("/{name}")
    public ResponseEntity<StudentDetailDto> getStudentByName(@PathVariable String name) {
        return ResponseEntity.ok(studentService.getStudentByName(name));
    }

    @GetMapping
    public ResponseEntity<List<StudentDto>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }
    
    @PutMapping("/{name}")
    public ResponseEntity<StudentDto> updateStudent(@PathVariable String name, @RequestBody UpdateRequest request) {
        return ResponseEntity.ok(studentService.updateStudent(name, request));
    }
    
    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteStudent(@PathVariable String name) {
        studentService.deleteStudent(name);
        return ResponseEntity.ok().build();
    }
}
EOF

cat <<EOF > ${BASE_PACKAGE_PATH}/api/InstructorController.java
package org.example.api;

import lombok.RequiredArgsConstructor;
import org.example.dto.*;
import org.example.service.InstructorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/instructor")
@RequiredArgsConstructor
public class InstructorController {
    
    private final InstructorService instructorService;

    @PostMapping
    public ResponseEntity<InstructorDto> createInstructor(@RequestBody CreateInstructorRequest request) {
        return new ResponseEntity<>(instructorService.createInstructor(request), HttpStatus.CREATED);
    }
    
    @GetMapping("/{name}")
    public ResponseEntity<InstructorDetailDto> getInstructorByName(@PathVariable String name) {
        return ResponseEntity.ok(instructorService.getInstructorByName(name));
    }
    
    @GetMapping
    public ResponseEntity<List<InstructorDto>> getAllInstructors() {
        return ResponseEntity.ok(instructorService.getAllInstructors());
    }
    
    @PutMapping("/{name}")
    public ResponseEntity<InstructorDto> updateInstructor(@PathVariable String name, @RequestBody UpdateRequest request) {
        return ResponseEntity.ok(instructorService.updateInstructor(name, request));
    }
    
    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteInstructor(@PathVariable String name) {
        instructorService.deleteInstructor(name);
        return ResponseEntity.ok().build();
    }
}
EOF

cat <<EOF > ${BASE_PACKAGE_PATH}/api/CourseController.java
package org.example.api;

import lombok.RequiredArgsConstructor;
import org.example.dto.*;
import org.example.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<CourseDto> createCourse(@RequestBody CreateCourseRequest request) {
        return new ResponseEntity<>(courseService.createCourse(request), HttpStatus.CREATED);
    }

    @GetMapping("/{name}")
    public ResponseEntity<CourseDetailDto> getCourseByName(@PathVariable String name) {
        return ResponseEntity.ok(courseService.getCourseByName(name));
    }

    @GetMapping
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @PutMapping("/{name}")
    public ResponseEntity<CourseDto> updateCourse(@PathVariable String name, @RequestBody UpdateRequest request) {
        return ResponseEntity.ok(courseService.updateCourse(name, request));
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteCourse(@PathVariable String name) {
        courseService.deleteCourse(name);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{courseName}/students/{studentName}")
    public ResponseEntity<Void> addStudentToCourse(@PathVariable String courseName, @PathVariable String studentName) {
        courseService.addStudentToCourse(courseName, studentName);
        return ResponseEntity.ok().build();
    }
}
EOF

# --- Create README.md ---
echo "[4/4] Creating README.md..."
cat <<'EOF' > README.md
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
EOF

# --- Final Message ---
echo ""
echo "--- ✅ Project '$PROJECT_NAME' created successfully! ---"
echo ""
echo "To run the application:"
echo "1. cd $PROJECT_NAME"
echo "2. ./mvnw spring-boot:run"
echo ""
echo "The API will be available at http://localhost:8080"
echo "The H2 console will be at http://localhost:8080/h2-console"
