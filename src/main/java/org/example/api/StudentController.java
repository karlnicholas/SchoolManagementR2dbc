package org.example.api;

import lombok.RequiredArgsConstructor;
import org.example.dto.CreateStudentRequest;
import org.example.dto.StudentDetailDto;
import org.example.dto.StudentDto;
import org.example.dto.UpdateRequest;
import org.example.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
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

    @GetMapping("/{id}")
    public Mono<StudentDetailDto> getStudent(@PathVariable Long id) {
        return studentService.getStudent(id);
    }

    @GetMapping
    public Flux<StudentDto> getAllStudents() {
        return studentService.getAllStudents();
    }

    @PatchMapping("/{id}")
    public Mono<StudentDto> updateStudent(@PathVariable Long id, @RequestBody UpdateRequest request) {
        return studentService.updateStudent(id, request);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteStudent(@PathVariable Long id) {
        return studentService.deleteStudent(id);
    }
}