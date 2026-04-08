package org.example.dto;
import java.util.List;
public record StudentDetailDto(Long id, String name, List<CourseDto> courses) {}
