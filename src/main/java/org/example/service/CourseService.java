package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.*;
import org.example.exception.ResourceNotFoundException;
import org.example.mapper.EntityDtoMapper;
import org.example.model.Course;
import org.example.model.Instructor;
import org.example.model.Student;
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
import java.util.List;

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
    public Mono<CourseDetailDto> getCourse(Long id) {
        return courseRepository.findById(id)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Course not found with id: " + id)))
            .flatMap(course -> {
                // 1. Query the students
                Mono<List<Student>> studentsMono = studentRepository.findAllByCourseId(course.getId()).collectList();

                // 2. Query the instructor (safely handle cases where instructorId might be null)
                Mono<Instructor> instructorMono = course.getInstructorId() != null
                    ? instructorRepository.findById(course.getInstructorId())
                    : Mono.empty();

                // 3. Zip the queries together so they execute concurrently.
                // We use .defaultIfEmpty() so Mono.zip doesn't cancel if there is no instructor.
                return Mono.zip(studentsMono, instructorMono.defaultIfEmpty(new Instructor()))
                    .map(tuple -> {
                        // Set the fetched students
                        course.setStudents(new HashSet<>(tuple.getT1()));

                        // Set the fetched instructor (if it exists)
                        Instructor instructor = tuple.getT2();
                        if (instructor.getId() != null) {
                            course.setInstructor(instructor);
                        }

                        return EntityDtoMapper.toCourseDetailDto(course);
                    });
            });
    }

    @Transactional(readOnly = true)
    public Flux<CourseDto> getAllCourses() {
        return courseRepository.findAll()
            .map(EntityDtoMapper::toCourseDto);
    }

    @Transactional
    public Mono<CourseDto> updateCourse(Long id, UpdateRequest request) {
        return courseRepository.findById(id)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Course not found with id: " + id)))
            .flatMap(course -> {
                course.setName(request.newName());
                return courseRepository.save(course);
            })
            .map(EntityDtoMapper::toCourseDto);
    }

    @Transactional
    public Mono<Void> deleteCourse(Long id) {
        return courseRepository.findById(id)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Course not found with id: " + id)))
            .flatMap(course ->
                studentCourseRepository.deleteByCourseId(course.getId())
                    .then(courseRepository.delete(course))
            );
    }

    @Transactional
    public Mono<Void> addStudentToCourse(Long studentId, Long courseId) {
        return Mono.zip(
            studentRepository.findById(studentId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("studentId not found: " + studentId))),
            courseRepository.findById(courseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("courseId not found: " + courseId)))
        ).flatMap(tuple ->
            studentCourseRepository.saveLink(tuple.getT1().getId(), tuple.getT2().getId())
        ).then();
    }
}