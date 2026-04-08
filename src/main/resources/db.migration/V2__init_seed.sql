-- =========================================
-- INITIAL DATA SEEDING SCRIPT (MS SQL Server)
-- =========================================

-- 1. Create the School
INSERT INTO school (name) VALUES ('Reactive Academy');

-- Capture the generated ID of the school to use for subsequent inserts
DECLARE @schoolId BIGINT = SCOPE_IDENTITY();

-- 2. Create the Instructors
INSERT INTO instructor (name, school_id) VALUES
                                             ('Isaac Newton', @schoolId),        -- Math
                                             ('Marie Curie', @schoolId),         -- Science
                                             ('William Shakespeare', @schoolId), -- English
                                             ('Herodotus', @schoolId),           -- History
                                             ('Max Weber', @schoolId);           -- Sociology

-- 3. Create the Courses
-- We map each course to its respective instructor by looking up their ID dynamically
INSERT INTO course (name, school_id, instructor_id) VALUES
                                                        ('Mathematics', @schoolId, (SELECT id FROM instructor WHERE name = 'Isaac Newton' AND school_id = @schoolId)),
                                                        ('Science', @schoolId, (SELECT id FROM instructor WHERE name = 'Marie Curie' AND school_id = @schoolId)),
                                                        ('English Literature', @schoolId, (SELECT id FROM instructor WHERE name = 'William Shakespeare' AND school_id = @schoolId)),
                                                        ('World History', @schoolId, (SELECT id FROM instructor WHERE name = 'Herodotus' AND school_id = @schoolId)),
                                                        ('Sociology 101', @schoolId, (SELECT id FROM instructor WHERE name = 'Max Weber' AND school_id = @schoolId));

-- 4. Create 100 Students and Assign Courses
DECLARE @counter INT = 1;
DECLARE @currentStudentId BIGINT;
DECLARE @randomCourseCount INT;

WHILE @counter <= 100
BEGIN
    -- Insert the Student
INSERT INTO student (name, school_id)
VALUES ('Student ' + CAST(@counter AS NVARCHAR(10)), @schoolId);

-- Capture the ID of the student we just created
SET @currentStudentId = SCOPE_IDENTITY();

    -- Generate a random number between 3 and 5 for the course load
    SET @randomCourseCount = ABS(CHECKSUM(NEWID()) % 3) + 3;

    -- Assign the random courses to the student
    -- ORDER BY NEWID() randomizes the rows from the course table,
    -- and TOP(@randomCourseCount) grabs exactly 3, 4, or 5 unique classes for this student.
INSERT INTO student_course (student_id, course_id)
SELECT TOP (@randomCourseCount) @currentStudentId, id
FROM course
WHERE school_id = @schoolId
ORDER BY NEWID();

-- Increment loop counter
SET @counter = @counter + 1;
END;
GO