package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.*;
import org.example.exception.ResourceNotFoundException;
import org.example.mapper.EntityDtoMapper;
import org.example.model.Instructor;
import org.example.repository.CourseRepository;
import org.example.repository.InstructorRepository;
import org.example.repository.SchoolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet; // Added import

@Service
@RequiredArgsConstructor
public class InstructorService {
    private final InstructorRepository instructorRepository;
    private final SchoolRepository schoolRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public Mono<InstructorDto> createInstructor(CreateInstructorRequest request) {
        return schoolRepository.findByName(request.schoolName())
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("School not found: " + request.schoolName())))
            .flatMap(school -> {
                Instructor instructor = new Instructor();
                instructor.setName(request.name());
                instructor.setSchool(school);
                return instructorRepository.save(instructor);
            })
            .map(EntityDtoMapper::toInstructorDto);
    }

    @Transactional(readOnly = true)
    public Mono<InstructorDetailDto> getInstructorByName(String name) {
        return instructorRepository.findByName(name)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Instructor not found with name: " + name)))
            .flatMap(instructor ->
                courseRepository.findAllByInstructorId(instructor.getId())
                    .collectList()
                    .map(courses -> {
                        // FIX: Convert List to Set
                        instructor.setCourses(new HashSet<>(courses));
                        return EntityDtoMapper.toInstructorDetailDto(instructor);
                    })
            );
    }

    public Flux<InstructorDto> getAllInstructors() {
        return instructorRepository.findAll()
            .map(EntityDtoMapper::toInstructorDto);
    }

    @Transactional
    public Mono<InstructorDto> updateInstructor(String name, UpdateRequest request) {
        return instructorRepository.findByName(name)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Instructor not found with name: " + name)))
            .flatMap(instructor -> {
                instructor.setName(request.newName());
                return instructorRepository.save(instructor);
            })
            .map(EntityDtoMapper::toInstructorDto);
    }

    @Transactional
    public Mono<Void> deleteInstructor(String name) {
        return instructorRepository.findByName(name)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Instructor not found with name: " + name)))
            .flatMap(instructorRepository::delete);
    }
}