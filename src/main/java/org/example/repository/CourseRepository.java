package org.example.repository;

import org.example.model.Course;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CourseRepository extends R2dbcRepository<Course, Long> {

    Mono<Course> findByName(String name);

    // 1-to-Many relationship (auto-mapped by Spring Data R2DBC)
    Flux<Course> findAllBySchoolId(Long schoolId);
    Flux<Course> findAllByInstructorId(Long instructorId);

    // Many-to-Many relationship requires a custom query traversing the pivot table
    @Query("SELECT c.* FROM course c JOIN student_course sc ON c.id = sc.course_id WHERE sc.student_id = :studentId")
    Flux<Course> findAllByStudentId(Long studentId);
}