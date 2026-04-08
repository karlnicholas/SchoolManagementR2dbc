package org.example.dto;
import java.util.List;
public record CourseDetailDto(Long id, String name, InstructorDto instructor, List<StudentDto> students) {}
