package org.example.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Student {
    @Id
    private Long id;

    private String name;

    @Column("school_id")
    private Long schoolId;

    @ToString.Exclude
    @Transient
    private School school;

    @ToString.Exclude
    @Transient
    private Set<Course> courses;

    // ADD THIS HELPER METHOD
    public void setSchool(School school) {
        this.school = school;
        this.schoolId = (school != null) ? school.getId() : null;
    }
}