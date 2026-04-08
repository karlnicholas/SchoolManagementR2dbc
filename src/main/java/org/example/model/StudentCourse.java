package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("student_course")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentCourse {

  private Long studentId;

  private Long courseId;
}