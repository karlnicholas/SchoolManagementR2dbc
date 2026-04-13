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
public class Course {
    @Id
    private Long id;

    private String name;

    // Add explicit foreign keys matching the schema
    @Column("school_id")
    private Long schoolId;

    @Column("instructor_id")
    private Long instructorId;

    // Mark complex relations as Transient
    @ToString.Exclude
    @Transient
    private School school;

    @ToString.Exclude
    @Transient
    private Instructor instructor;

    @ToString.Exclude
    @Transient
    private Set<Student> students;

    // Helper methods to keep foreign keys in sync
    public void setSchool(School school) {
        this.school = school;
        this.schoolId = (school != null) ? school.getId() : null;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
        this.instructorId = (instructor != null) ? instructor.getId() : null;
    }
}