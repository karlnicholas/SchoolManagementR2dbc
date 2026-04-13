package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Instructor {
    @Id
    private Long id;

    private String name;

    // Add explicit foreign key matching the schema
    @Column("school_id")
    private Long schoolId;

    // Mark complex relations as Transient
    @ToString.Exclude
    @Transient
    private School school;

    @ToString.Exclude
    @Transient
    private Set<Course> courses;

    // Helper method to keep foreign key in sync
    public void setSchool(School school) {
        this.school = school;
        this.schoolId = (school != null) ? school.getId() : null;
    }
}