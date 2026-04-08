package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.*;
import org.example.exception.ResourceNotFoundException;
import org.example.mapper.EntityDtoMapper;
import org.example.model.Course;
import org.example.repository.CourseRepository;
import org.example.repository.InstructorRepository;
import org.example.repository.SchoolRepository;
import org.example.repository.StudentRepository;
import org.example.repository.StudentCourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet; // Added import

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final SchoolRepository schoolRepository;
    private final InstructorRepository instructorRepository;
    private final StudentCourseRepository studentCourseRepository;

    @Transactional
    public Mono<CourseDto> createCourse(CreateCourseRequest request) {
        return Mono.zip(
            schoolRepository.findByName(request.schoolName())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("School not found: " + request.schoolName()))),
            instructorRepository.findByName(request.instructorName())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Instructor not found: " + request.instructorName())))
        ).flatMap(tuple -> {
            Course course = new Course();
            course.setName(request.name());
            course.setSchool(tuple.getT1());
            course.setInstructor(tuple.getT2());
            return courseRepository.save(course);
        }).map(EntityDtoMapper::toCourseDto);
    }

    @Transactional(readOnly = true)
    public Mono<CourseDetailDto> getCourseByName(String name) {
        return courseRepository.findByName(name)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Course not found with name: " + name)))
            .flatMap(course ->
                studentRepository.findAllByCourseId(course.getId())
                    .collectList()
                    .map(students -> {
                        // FIX: Convert List to Set
                        course.setStudents(new HashSet<>(students));
                        return EntityDtoMapper.toCourseDetailDto(course);
                    })
            );
    }

    @Transactional(readOnly = true)
    public Flux<CourseDto> getAllCourses() {
        return courseRepository.findAll()
            .map(EntityDtoMapper::toCourseDto);
    }

    @Transactional
    public Mono<CourseDto> updateCourse(String name, UpdateRequest request) {
        return courseRepository.findByName(name)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Course not found with name: " + name)))
            .flatMap(course -> {
                course.setName(request.newName());
                return courseRepository.save(course);
            })
            .map(EntityDtoMapper::toCourseDto);
    }

    @Transactional
    public Mono<Void> deleteCourse(String name) {
        return courseRepository.findByName(name)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Course not found with name: " + name)))
            .flatMap(course ->
                studentCourseRepository.deleteByCourseId(course.getId())
                    .then(courseRepository.delete(course))
            );
    }

    @Transactional
    public Mono<Void> addStudentToCourse(String courseName, String studentName) {
        return Mono.zip(
            studentRepository.findByName(studentName)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Student not found: " + studentName))),
            courseRepository.findByName(courseName)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Course not found: " + courseName)))
        ).flatMap(tuple -> {
            return studentCourseRepository.saveLink(tuple.getT1().getId(), tuple.getT2().getId());
        }).then();
    }
}