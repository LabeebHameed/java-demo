package com.college;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseHelper handles all interactions with the MySQL database.
 * Demonstrates clean JDBC practices: Connection handling, PreparedStatements,
 * and CRUD operations (Create, Read, Update, Delete).
 */
public class DatabaseHelper {

    // === Database Configuration (Modify these to match your local MySQL setup) ===
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "college_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = ""; // Leave blank for default Homebrew MySQL, or set your password

    private static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String BASE_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    /**
     * Initializes the database and table if they do not already exist.
     * Inserts demo records if the table is freshly created.
     */
    public static void initDatabase() throws SQLException {
        // Step 1: Ensure database exists
        try (Connection conn = DriverManager.getConnection(BASE_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
        }

        // Step 2: Ensure table exists
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS students ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "roll_no VARCHAR(20) NOT NULL UNIQUE, "
                    + "name VARCHAR(100) NOT NULL, "
                    + "email VARCHAR(100) NOT NULL UNIQUE, "
                    + "department VARCHAR(50) NOT NULL, "
                    + "year_level VARCHAR(20) NOT NULL, "
                    + "gpa DECIMAL(3,2) NOT NULL, "
                    + "status ENUM('Active', 'Graduated', 'On Leave') DEFAULT 'Active', "
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ");";
            stmt.executeUpdate(createTableSQL);

            // Seed sample data if empty
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM students");
            if (rs.next() && rs.getInt(1) == 0) {
                seedDemoData(conn);
            }
        }
    }

    /**
     * Establishes and returns a Connection to the MySQL database.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Seeds initial demo records so juniors can immediately see data in action.
     */
    private static void seedDemoData(Connection conn) throws SQLException {
        String seedSQL = "INSERT INTO students (roll_no, name, email, department, year_level, gpa, status) VALUES "
                + "('CS-2024-001', 'Alex Johnson', 'alex.j@college.edu', 'Computer Science', '3rd Year', 3.85, 'Active'),"
                + "('CS-2024-002', 'Sophia Martinez', 'sophia.m@college.edu', 'Computer Science', '2nd Year', 3.92, 'Active'),"
                + "('EC-2024-015', 'Rahul Sharma', 'rahul.s@college.edu', 'Electronics', '4th Year', 3.65, 'Active'),"
                + "('ME-2024-042', 'David Kim', 'david.k@college.edu', 'Mechanical', '1st Year', 3.40, 'Active'),"
                + "('DS-2024-008', 'Emma Watson', 'emma.w@college.edu', 'Data Science', '3rd Year', 3.98, 'Active'),"
                + "('BA-2023-090', 'Lucas Silva', 'lucas.s@college.edu', 'Business Admin', '4th Year', 3.50, 'Graduated'),"
                + "('CS-2024-033', 'Priya Patel', 'priya.p@college.edu', 'Computer Science', '2nd Year', 3.78, 'On Leave');";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(seedSQL);
        }
    }

    /**
     * Retrieves all student records ordered by ID descending (newest first).
     */
    public static List<Student> getAllStudents() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT id, roll_no, name, email, department, year_level, gpa, status FROM students ORDER BY id DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Student(
                        rs.getInt("id"),
                        rs.getString("roll_no"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("department"),
                        rs.getString("year_level"),
                        rs.getDouble("gpa"),
                        rs.getString("status")
                ));
            }
        }
        return list;
    }

    /**
     * Searches students across Roll Number, Name, Email, or Department.
     */
    public static List<Student> searchStudents(String keyword) throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT id, roll_no, name, email, department, year_level, gpa, status FROM students "
                + "WHERE roll_no LIKE ? OR name LIKE ? OR email LIKE ? OR department LIKE ? "
                + "ORDER BY id DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String term = "%" + keyword + "%";
            ps.setString(1, term);
            ps.setString(2, term);
            ps.setString(3, term);
            ps.setString(4, term);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Student(
                            rs.getInt("id"),
                            rs.getString("roll_no"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("department"),
                            rs.getString("year_level"),
                            rs.getDouble("gpa"),
                            rs.getString("status")
                    ));
                }
            }
        }
        return list;
    }

    /**
     * Inserts a new student record using parameterized PreparedStatement.
     */
    public static boolean addStudent(Student student) throws SQLException {
        String sql = "INSERT INTO students (roll_no, name, email, department, year_level, gpa, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, student.getRollNo());
            ps.setString(2, student.getName());
            ps.setString(3, student.getEmail());
            ps.setString(4, student.getDepartment());
            ps.setString(5, student.getYearLevel());
            ps.setDouble(6, student.getGpa());
            ps.setString(7, student.getStatus());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Updates an existing student record by ID.
     */
    public static boolean updateStudent(Student student) throws SQLException {
        String sql = "UPDATE students SET roll_no = ?, name = ?, email = ?, department = ?, year_level = ?, gpa = ?, status = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, student.getRollNo());
            ps.setString(2, student.getName());
            ps.setString(3, student.getEmail());
            ps.setString(4, student.getDepartment());
            ps.setString(5, student.getYearLevel());
            ps.setDouble(6, student.getGpa());
            ps.setString(7, student.getStatus());
            ps.setInt(8, student.getId());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a student record by ID.
     */
    public static boolean deleteStudent(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Helper record/class for dashboard summary statistics.
     */
    public static class DashboardStats {
        public final int totalStudents;
        public final int activeStudents;
        public final double averageGpa;

        public DashboardStats(int total, int active, double avgGpa) {
            this.totalStudents = total;
            this.activeStudents = active;
            this.averageGpa = avgGpa;
        }
    }

    /**
     * Calculates live summary metrics for top cards.
     */
    public static DashboardStats getDashboardStats() throws SQLException {
        String sql = "SELECT COUNT(*) AS total, "
                + "SUM(CASE WHEN status = 'Active' THEN 1 ELSE 0 END) AS active_count, "
                + "COALESCE(AVG(gpa), 0.0) AS avg_gpa "
                + "FROM students";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                int total = rs.getInt("total");
                int active = rs.getInt("active_count");
                double avgGpa = rs.getDouble("avg_gpa");
                return new DashboardStats(total, active, avgGpa);
            }
        }
        return new DashboardStats(0, 0, 0.0);
    }
}
