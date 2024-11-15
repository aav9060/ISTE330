-- Created by:
-- 11/14/2024

-- CREATING THE DATABASE
DROP DATABASE IF EXISTS faculty_research_group5;
CREATE DATABASE faculty_research_group5;
USE faculty_research_group5;



-- CREATION OF THE ACCOUNTS TABLE
-- used to store account details for all types of users (faculty, staff, and public).

DROP TABLE IF EXISTS account;
CREATE TABLE account (
    account_id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    type ENUM('Faculty', 'Staff', 'Public') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;






-- CREATION OF THE FACULTY TABLE
-- used to store faculty information ONLY.

DROP TABLE IF EXISTS faculty;
CREATE TABLE faculty (
    faculty_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    abstract_id INT,
    department VARCHAR(100),
    building VARCHAR(100),
	office VARCHAR(30),
    email VARCHAR(50) UNIQUE,
    password VARCHAR(255),
    interests VARCHAR(100),
    FOREIGN KEY (abstract_id) REFERENCES faculty_abstract(abstract_ID)
		ON DELETE SET NULL 
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;






-- CREATION OF THE FACULTY_ABSTRACT TABLE
-- used to store abstracts for faculty member's research topics.

DROP TABLE IF EXISTS faculty_abstract;
CREATE TABLE faculty_abstract (
    abstract_ID INT PRIMARY KEY AUTO_INCREMENT,
    abstract TEXT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;






-- CREATION OF THE FACULTY_INTERESTS TABLE
-- used to associate the faculty member with their specific research interets.

DROP TABLE IF EXISTS faculty_interests;
CREATE TABLE faculty_interests (
    faculty_ID INT,
    interest_ID VARCHAR(3),
    PRIMARY KEY (faculty_ID, interest_ID),
    FOREIGN KEY (faculty_ID) REFERENCES faculty(faculty_id)
		ON DELETE CASCADE 
        ON UPDATE CASCADE,
    FOREIGN KEY (interest_ID) REFERENCES interests(interest_ID)
		ON DELETE CASCADE 
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;






-- CREATION OF THE STUDENTS TABLE
-- used to store student information ONLY.

DROP TABLE IF EXISTS students;
CREATE TABLE students (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(20),
    email VARCHAR(50) UNIQUE,
    major VARCHAR(100),
    year VARCHAR(10)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;






-- CREATION OF THE STUDENT_INTERESTS TABLE
-- used to associate a student with their specific research interets.

DROP TABLE IF EXISTS student_interests;
CREATE TABLE student_interests (
    student_id INT,
    interest_ID VARCHAR(3),
    PRIMARY KEY (student_id, interest_ID),
    FOREIGN KEY (student_id) REFERENCES students(student_id)
		ON DELETE CASCADE 
        ON UPDATE CASCADE,
    FOREIGN KEY (interest_ID) REFERENCES interests(interest_ID)
		ON DELETE CASCADE 
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;






-- CREATION OF THE PUBLIC USERS TABLE
-- used to store information about public users who wish to search the database.

DROP TABLE IF EXISTS public;
CREATE TABLE public (
    public_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50),
    address VARCHAR(100),
    interest_id VARCHAR(3),
    FOREIGN KEY (interest_id) REFERENCES interests(interest_ID)
		ON DELETE CASCADE 
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;






-- CREATION OF THE INTERESTS TABLE
-- used to store information of all the interests which the faculty or student might have.

DROP TABLE IF EXISTS interests;
CREATE TABLE interests (
    interest_ID VARCHAR(3) PRIMARY KEY,
    interest VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
