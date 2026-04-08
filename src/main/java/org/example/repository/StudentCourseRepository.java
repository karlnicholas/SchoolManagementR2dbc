package org.example.repository;

import org.example.model.StudentCourse;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface StudentCourseRepository extends ReactiveCrudRepository<StudentCourse, Long> {

  @Modifying
  @Query("INSERT INTO student_course (student_id, course_id) VALUES (:studentId, :courseId)")
  Mono<Void> saveLink(Long studentId, Long courseId);

  @Modifying
  @Query("DELETE FROM student_course WHERE course_id = :courseId")
  Mono<Void> deleteByCourseId(Long courseId);
}