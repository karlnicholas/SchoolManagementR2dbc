package org.example.api;

import lombok.RequiredArgsConstructor;
import org.example.dto.*;
import org.example.service.InstructorService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/instructor")
@RequiredArgsConstructor
public class InstructorController {

    private final InstructorService instructorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<InstructorDto> createInstructor(@RequestBody CreateInstructorRequest request) {
        return instructorService.createInstructor(request);
    }

    @GetMapping("/{id}")
    public Mono<InstructorDetailDto> getInstructor(@PathVariable Long id) {
        return instructorService.getInstructor(id);
    }

    @GetMapping
    public Flux<InstructorDto> getAllInstructors() {
        return instructorService.getAllInstructors();
    }

    @PutMapping("/{id}")
    public Mono<InstructorDto> updateInstructor(@PathVariable Long id, @RequestBody UpdateRequest request) {
        return instructorService.updateInstructor(id, request);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteInstructor(@PathVariable Long id) {
        return instructorService.deleteInstructor(id);
    }
}