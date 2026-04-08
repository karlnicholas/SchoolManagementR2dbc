package org.example.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class School {
    @Id
    private Long id;

    private String name;

    @ToString.Exclude
    @Transient
    private Set<Course> courses;

    @ToString.Exclude
    @Transient
    private Set<Student> students;

    @ToString.Exclude
    @Transient
    private Set<Instructor> instructors;
}
