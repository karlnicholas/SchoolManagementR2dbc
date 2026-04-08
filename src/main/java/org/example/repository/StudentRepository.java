package org.example.repository;

import org.example.model.Student;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StudentRepository extends R2dbcRepository<Student, Long> {

    Mono<Student> findByName(String name);

    // 1-to-Many relationship (auto-mapped by Spring Data R2DBC)
    Flux<Student> findAllBySchoolId(Long schoolId);

    // Many-to-Many relationship requires a custom query traversing the pivot table
    @Query("SELECT s.* FROM student s JOIN student_course sc ON s.id = sc.student_id WHERE sc.course_id = :courseId")
    Flux<Student> findAllByCourseId(Long courseId);
}