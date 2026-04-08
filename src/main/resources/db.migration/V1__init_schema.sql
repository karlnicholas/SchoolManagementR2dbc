-- =========================================
-- DROP TABLES (Child to Parent order)
-- =========================================
DROP TABLE IF EXISTS student_course;
DROP TABLE IF EXISTS course;
DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS instructor;
DROP TABLE IF EXISTS school;

-- =========================================
-- CREATE TABLES (Parent to Child order)
-- =========================================

CREATE TABLE school (
                        id BIGINT IDENTITY(1,1) PRIMARY KEY,
                        name NVARCHAR(255) NOT NULL
);

CREATE TABLE instructor (
                            id BIGINT IDENTITY(1,1) PRIMARY KEY,
                            name NVARCHAR(255) NOT NULL,
                            school_id BIGINT,
                            CONSTRAINT fk_instructor_school FOREIGN KEY (school_id) REFERENCES school(id)
);

CREATE TABLE student (
                         id BIGINT IDENTITY(1,1) PRIMARY KEY,
                         name NVARCHAR(255) NOT NULL,
                         school_id BIGINT,
                         CONSTRAINT fk_student_school FOREIGN KEY (school_id) REFERENCES school(id)
);

CREATE TABLE course (
                        id BIGINT IDENTITY(1,1) PRIMARY KEY,
                        name NVARCHAR(255) NOT NULL,
                        school_id BIGINT,
                        instructor_id BIGINT,
                        CONSTRAINT fk_course_school FOREIGN KEY (school_id) REFERENCES school(id),
                        CONSTRAINT fk_course_instructor FOREIGN KEY (instructor_id) REFERENCES instructor(id)
);

-- Pivot table for Many-to-Many Student <-> Course relationship
CREATE TABLE student_course (
                                student_id BIGINT NOT NULL,
                                course_id BIGINT NOT NULL,
                                PRIMARY KEY (student_id, course_id),
                                CONSTRAINT fk_sc_student FOREIGN KEY (student_id) REFERENCES student(id),
                                CONSTRAINT fk_sc_course FOREIGN KEY (course_id) REFERENCES course(id)
);