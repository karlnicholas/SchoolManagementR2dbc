package org.example.dto;
import java.util.List;
public record InstructorDetailDto(Long id, String name, List<CourseDto> courses) {}
