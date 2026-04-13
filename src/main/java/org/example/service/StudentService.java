package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.CreateStudentRequest;
import org.example.dto.StudentDetailDto;
import org.example.dto.StudentDto;
import org.example.dto.UpdateRequest;
import org.example.exception.ResourceNotFoundException;
import org.example.mapper.EntityDtoMapper;
import org.example.model.Student;
import org.example.repository.CourseRepository;
import org.example.repository.SchoolRepository;
import org.example.repository.StudentRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet; // Added import

@Service
@RequiredArgsConstructor
public class StudentService {

  private final StudentRepository studentRepository;
  private final SchoolRepository schoolRepository;
  private final CourseRepository courseRepository;

  public Mono<StudentDto> createStudent(CreateStudentRequest request) {
    return schoolRepository.findByName(request.schoolName())
        .switchIfEmpty(Mono.error(new ResourceNotFoundException("School not found: " + request.schoolName())))
        .flatMap(school -> {
          Student student = new Student();
          student.setName(request.name());
          student.setSchool(school);
          return studentRepository.save(student);
        })
        .map(EntityDtoMapper::toStudentDto);
  }

  public Mono<StudentDetailDto> getStudent(Long id) {
    return studentRepository.findById(id)
        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Student not found with id: " + id)))
        .flatMap(student ->
            courseRepository.findAllByStudentId(student.getId())
                .collectList()
                .map(courses -> {
                  // FIX: Convert List to Set
                  student.setCourses(new HashSet<>(courses));
                  return EntityDtoMapper.toStudentDetailDto(student);
                })
        );
  }

  public Flux<StudentDto> getAllStudents() {
    return studentRepository.findAll()
        .map(EntityDtoMapper::toStudentDto);
  }

  public Mono<StudentDto> updateStudent(Long id, UpdateRequest request) {
    return studentRepository.findById(id)
        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Student not found with id: " + id)))
        .flatMap(student -> {
          student.setName(request.newName());
          return studentRepository.save(student);
        })
        .map(EntityDtoMapper::toStudentDto);
  }

  public Mono<Void> deleteStudent(Long id) {
    return studentRepository.findById(id)
        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Student not found with id: " + id)))
        .flatMap(studentRepository::delete);
  }
}