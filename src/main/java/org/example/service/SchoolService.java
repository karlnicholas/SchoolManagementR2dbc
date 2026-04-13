package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.CreateSchoolRequest;
import org.example.dto.SchoolDto;
import org.example.exception.ResourceNotFoundException;
import org.example.mapper.EntityDtoMapper;
import org.example.model.School;
import org.example.repository.CourseRepository;
import org.example.repository.InstructorRepository;
import org.example.repository.SchoolRepository;
import org.example.repository.StudentRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashSet; // Added import

@Service
@RequiredArgsConstructor
public class SchoolService {

    private final SchoolRepository schoolRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;

    public Mono<SchoolDto> createSchool(CreateSchoolRequest request) {
        School school = new School();
        school.setName(request.name());
        return schoolRepository.save(school).map(EntityDtoMapper::toSchoolDto);
    }

    public Mono<SchoolDto> getSchoolByName(String name) {
        return schoolRepository.findByName(name)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("School not found with name: " + name)))
            .flatMap(school ->
                Mono.zip(
                    studentRepository.findAllBySchoolId(school.getId()).collectList(),
                    courseRepository.findAllBySchoolId(school.getId()).collectList(),
                    instructorRepository.findAllBySchoolId(school.getId()).collectList()
                ).map(tuple -> {
                    // FIX: Convert Lists to Sets
                    school.setStudents(new HashSet<>(tuple.getT1()));
                    school.setCourses(new HashSet<>(tuple.getT2()));
                    school.setInstructors(new HashSet<>(tuple.getT3()));
                    return EntityDtoMapper.toSchoolDto(school);
                })
            );
    }

    public Mono<SchoolDto> getSchoolById(Long id) {
        return schoolRepository.findById(id)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("School not found with id: " + id)))
            .flatMap(school ->
                Mono.zip(
                    studentRepository.findAllBySchoolId(school.getId()).collectList(),
                    courseRepository.findAllBySchoolId(school.getId()).collectList(),
                    instructorRepository.findAllBySchoolId(school.getId()).collectList()
                ).map(tuple -> {
                    // FIX: Convert Lists to Sets
                    school.setStudents(new HashSet<>(tuple.getT1()));
                    school.setCourses(new HashSet<>(tuple.getT2()));
                    school.setInstructors(new HashSet<>(tuple.getT3()));
                    return EntityDtoMapper.toSchoolDto(school);
                })
            );
    }
}