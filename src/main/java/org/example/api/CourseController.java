package org.example.api;

import lombok.RequiredArgsConstructor;
import org.example.dto.*;
import org.example.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CourseDto> createCourse(@RequestBody CreateCourseRequest request) {
        return courseService.createCourse(request);
    }

    @GetMapping("/{name}")
    public Mono<CourseDetailDto> getCourseByName(@PathVariable String name) {
        return courseService.getCourseByName(name);
    }

    @GetMapping
    public Flux<CourseDto> getAllCourses() {
        return courseService.getAllCourses();
    }

    @PutMapping("/{name}")
    public Mono<CourseDto> updateCourse(@PathVariable String name, @RequestBody UpdateRequest request) {
        return courseService.updateCourse(name, request);
    }

    @DeleteMapping("/{name}")
    public Mono<Void> deleteCourse(@PathVariable String name) {
        return courseService.deleteCourse(name);
    }

    @PostMapping("/{courseName}/students/{studentName}")
    public Mono<Void> addStudentToCourse(@PathVariable String courseName, @PathVariable String studentName) {
        return courseService.addStudentToCourse(courseName, studentName);
    }
}