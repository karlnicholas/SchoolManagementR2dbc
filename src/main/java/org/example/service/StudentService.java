package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.*;
import org.example.exception.ResourceNotFoundException;
import org.example.mapper.EntityDtoMapper;
import org.example.model.Student;
import org.example.repository.CourseRepository;
import org.example.repository.SchoolRepository;
import org.example.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet; // Added import

@Service
@RequiredArgsConstructor
public class StudentService {

  private final StudentRepository studentRepository;
  private final SchoolRepository schoolRepository;
  private final CourseRepository courseRepository;

  @Transactional
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

  @Transactional(readOnly = true)
  public Mono<StudentDetailDto> getStudentByName(String name) {
    return studentRepository.findByName(name)
        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Student not found with name: " + name)))
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

  @Transactional
  public Mono<StudentDto> updateStudent(String name, UpdateRequest request) {
    return studentRepository.findByName(name)
        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Student not found with name: " + name)))
        .flatMap(student -> {
          student.setName(request.newName());
          return studentRepository.save(student);
        })
        .map(EntityDtoMapper::toStudentDto);
  }

  @Transactional
  public Mono<Void> deleteStudent(String name) {
    return studentRepository.findByName(name)
        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Student not found with name: " + name)))
        .flatMap(studentRepository::delete);
  }
}