package org.example.api;

import lombok.RequiredArgsConstructor;
import org.example.dto.CourseDetailDto;
import org.example.dto.CourseDto;
import org.example.dto.CreateCourseRequest;
import org.example.dto.UpdateRequest;
import org.example.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
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

    @GetMapping("/{id}")
    public Mono<CourseDetailDto> getCourseByName(@PathVariable Long id) {
        return courseService.getCourse(id);
    }

    @GetMapping
    public Flux<CourseDto> getAllCourses() {
        return courseService.getAllCourses();
    }

    @PutMapping("/{id}")
    public Mono<CourseDto> updateCourse(@PathVariable Long id, @RequestBody UpdateRequest request) {
        return courseService.updateCourse(id, request);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteCourse(@PathVariable Long id) {
        return courseService.deleteCourse(id);
    }

    @PostMapping("/{courseId}/student/{studentId}")
    public Mono<Void> addStudentToCourse(@PathVariable Long courseId, @PathVariable Long studentId) {
        return courseService.addStudentToCourse(studentId, courseId);
    }
}