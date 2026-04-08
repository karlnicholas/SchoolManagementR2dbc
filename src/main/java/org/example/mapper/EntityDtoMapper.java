package org.example.mapper;

import org.example.dto.*;
import org.example.model.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EntityDtoMapper {

    public static StudentDto toStudentDto(Student student) {
        return new StudentDto(student.getId(), student.getName());
    }
    
    public static CourseDto toCourseDto(Course course) {
        return new CourseDto(course.getId(), course.getName());
    }

    public static InstructorDto toInstructorDto(Instructor instructor) {
        return new InstructorDto(instructor.getId(), instructor.getName());
    }

    public static StudentDetailDto toStudentDetailDto(Student student) {
        List<CourseDto> courses = student.getCourses() == null ? Collections.emptyList() :
                student.getCourses().stream().map(EntityDtoMapper::toCourseDto).collect(Collectors.toList());
        return new StudentDetailDto(student.getId(), student.getName(), courses);
    }

    public static CourseDetailDto toCourseDetailDto(Course course) {
        InstructorDto instructorDto = course.getInstructor() != null ? toInstructorDto(course.getInstructor()) : null;
        List<StudentDto> students = course.getStudents() == null ? Collections.emptyList() :
                course.getStudents().stream().map(EntityDtoMapper::toStudentDto).collect(Collectors.toList());
        return new CourseDetailDto(course.getId(), course.getName(), instructorDto, students);
    }

    public static InstructorDetailDto toInstructorDetailDto(Instructor instructor) {
        List<CourseDto> courses = instructor.getCourses() == null ? Collections.emptyList() :
                instructor.getCourses().stream().map(EntityDtoMapper::toCourseDto).collect(Collectors.toList());
        return new InstructorDetailDto(instructor.getId(), instructor.getName(), courses);
    }
    
    public static SchoolDto toSchoolDto(School school) {
        List<InstructorDto> instructors = school.getInstructors() == null ? Collections.emptyList() :
                school.getInstructors().stream().map(EntityDtoMapper::toInstructorDto).collect(Collectors.toList());
        List<CourseDto> courses = school.getCourses() == null ? Collections.emptyList() :
                school.getCourses().stream().map(EntityDtoMapper::toCourseDto).collect(Collectors.toList());
        List<StudentDto> students = school.getStudents() == null ? Collections.emptyList() :
                school.getStudents().stream().map(EntityDtoMapper::toStudentDto).collect(Collectors.toList());
        return new SchoolDto(school.getId(), school.getName(), instructors, courses, students);
    }
}
