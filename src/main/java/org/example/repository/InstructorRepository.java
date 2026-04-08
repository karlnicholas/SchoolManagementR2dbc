package org.example.repository;

import org.example.model.Instructor;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InstructorRepository extends R2dbcRepository<Instructor, Long> {

    Mono<Instructor> findByName(String name);

    // 1-to-Many relationship (auto-mapped by Spring Data R2DBC)
    Flux<Instructor> findAllBySchoolId(Long schoolId);
}