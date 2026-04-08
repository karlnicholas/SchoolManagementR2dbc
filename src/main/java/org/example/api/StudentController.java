package org.example.api;

import lombok.RequiredArgsConstructor;
import org.example.dto.*;
import org.example.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<StudentDto> createStudent(@RequestBody CreateStudentRequest request) {
        return studentService.createStudent(request);
    }

    @GetMapping("/{name}")
    public Mono<StudentDetailDto> getStudentByName(@PathVariable String name) {
        return studentService.getStudentByName(name);
    }

    @GetMapping
    public Flux<StudentDto> getAllStudents() {
        return studentService.getAllStudents();
    }

    @PutMapping("/{name}")
    public Mono<StudentDto> updateStudent(@PathVariable String name, @RequestBody UpdateRequest request) {
        return studentService.updateStudent(name, request);
    }

    @DeleteMapping("/{name}")
    public Mono<Void> deleteStudent(@PathVariable String name) {
        return studentService.deleteStudent(name);
    }
}