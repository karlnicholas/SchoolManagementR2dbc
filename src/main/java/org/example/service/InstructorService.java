package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.CreateInstructorRequest;
import org.example.dto.InstructorDetailDto;
import org.example.dto.InstructorDto;
import org.example.dto.UpdateRequest;
import org.example.exception.ResourceNotFoundException;
import org.example.mapper.EntityDtoMapper;
import org.example.model.Instructor;
import org.example.repository.CourseRepository;
import org.example.repository.InstructorRepository;
import org.example.repository.SchoolRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet; // Added import

@Service
@RequiredArgsConstructor
public class InstructorService {
    private final InstructorRepository instructorRepository;
    private final SchoolRepository schoolRepository;
    private final CourseRepository courseRepository;

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

    public Mono<InstructorDetailDto> getInstructor(Long id) {
        return instructorRepository.findById(id)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Instructor not found with id: " + id)))
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

    public Mono<InstructorDto> updateInstructor(Long id, UpdateRequest request) {
        return instructorRepository.findById(id)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Instructor not found with id: " + id)))
            .flatMap(instructor -> {
                instructor.setName(request.newName());
                return instructorRepository.save(instructor);
            })
            .map(EntityDtoMapper::toInstructorDto);
    }

    public Mono<Void> deleteInstructor(Long id) {
        return instructorRepository.findById(id)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Instructor not found with id: " + id)))
            .flatMap(instructorRepository::delete);
    }
}