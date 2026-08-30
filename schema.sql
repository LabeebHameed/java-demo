-- ==========================================================
-- College Student Management System - Database Schema
-- ==========================================================

-- 1. Create Database if it does not exist
CREATE DATABASE IF NOT EXISTS college_db;
USE college_db;

-- 2. Create Students Table
CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    roll_no VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    department VARCHAR(50) NOT NULL,
    year_level VARCHAR(20) NOT NULL,
    gpa DECIMAL(3,2) NOT NULL,
    status ENUM('Active', 'Graduated', 'On Leave') DEFAULT 'Active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Insert Initial Sample Records for Testing / Demo
INSERT INTO students (roll_no, name, email, department, year_level, gpa, status)
VALUES
    ('CS-2024-001', 'Alex Johnson', 'alex.j@college.edu', 'Computer Science', '3rd Year', 3.85, 'Active'),
    ('CS-2024-002', 'Sophia Martinez', 'sophia.m@college.edu', 'Computer Science', '2nd Year', 3.92, 'Active'),
    ('EC-2024-015', 'Rahul Sharma', 'rahul.s@college.edu', 'Electronics', '4th Year', 3.65, 'Active'),
    ('ME-2024-042', 'David Kim', 'david.k@college.edu', 'Mechanical', '1st Year', 3.40, 'Active'),
    ('DS-2024-008', 'Emma Watson', 'emma.w@college.edu', 'Data Science', '3rd Year', 3.98, 'Active'),
    ('BA-2023-090', 'Lucas Silva', 'lucas.s@college.edu', 'Business Admin', '4th Year', 3.50, 'Graduated'),
    ('CS-2024-033', 'Priya Patel', 'priya.p@college.edu', 'Computer Science', '2nd Year', 3.78, 'On Leave')
ON DUPLICATE KEY UPDATE roll_no = roll_no;
