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

    @GetMapping("/{name}")
    public Mono<InstructorDetailDto> getInstructorByName(@PathVariable String name) {
        return instructorService.getInstructorByName(name);
    }

    @GetMapping
    public Flux<InstructorDto> getAllInstructors() {
        return instructorService.getAllInstructors();
    }

    @PutMapping("/{name}")
    public Mono<InstructorDto> updateInstructor(@PathVariable String name, @RequestBody UpdateRequest request) {
        return instructorService.updateInstructor(name, request);
    }

    @DeleteMapping("/{name}")
    public Mono<Void> deleteInstructor(@PathVariable String name) {
        return instructorService.deleteInstructor(name);
    }
}