package org.example.api;

import lombok.RequiredArgsConstructor;
import org.example.dto.CreateSchoolRequest;
import org.example.dto.SchoolDto;
import org.example.service.SchoolService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/school")
@RequiredArgsConstructor
public class SchoolController {

    private final SchoolService schoolService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<SchoolDto> createSchool(@RequestBody CreateSchoolRequest request) {
        return schoolService.createSchool(request);
    }

    @GetMapping("/{name}")
    public Mono<SchoolDto> getSchoolByName(@PathVariable String name) {
        return schoolService.getSchoolByName(name);
    }
}