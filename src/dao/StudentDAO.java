package dao;

import model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import config.DBConnection;

public class StudentDAO {

    // Method to establish a connection to the database
    private Connection connect() throws SQLException {
        return DBConnection.getConnection();
    }

    // Add a new student to the database
    public boolean addStudent(Student student) {
        String sql = "INSERT INTO students (name, gender, dob, email, phone, status, address, department_id, image_path, enrollment_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, student.getName());
            stmt.setString(2, student.getGender());
            stmt.setDate(3, new java.sql.Date(student.getDob().getTime()));
            stmt.setString(4, student.getEmail());
            stmt.setString(5, student.getPhone());
            stmt.setString(6, student.getStatus());
            stmt.setString(7, student.getAddress());
            stmt.setString(8, student.getDepartmentId());
            stmt.setString(9, student.getImagePath());
            stmt.setDate(10, new java.sql.Date(student.getEnrollmentDate().getTime())); // Add enrollment date

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            System.err.println("Error adding student: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Fetch all students from the database
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Student s = new Student();
                s.setId(rs.getInt("id"));
                s.setName(rs.getString("name"));
                s.setGender(rs.getString("gender"));
                s.setDob(rs.getDate("dob"));
                s.setEmail(rs.getString("email"));
                s.setPhone(rs.getString("phone"));
                s.setStatus(rs.getString("status"));
                s.setAddress(rs.getString("address"));
                s.setDepartmentId(rs.getString("department_id"));
                s.setImagePath(rs.getString("image_path"));
                s.setEnrollmentDate(rs.getDate("enrollment_date")); // Fetch enrollment date
                students.add(s);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching students: " + e.getMessage());
            e.printStackTrace();
        }

        return students;
    }

    // Fetch a single student by ID
    public Student getStudentById(int studentId) {
        String sql = "SELECT * FROM students WHERE id = ?";
        Student student = null;

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                student = new Student();
                student.setId(rs.getInt("id"));
                student.setName(rs.getString("name"));
                student.setGender(rs.getString("gender"));
                student.setDob(rs.getDate("dob"));
                student.setEmail(rs.getString("email"));
                student.setPhone(rs.getString("phone"));
                student.setStatus(rs.getString("status"));
                student.setAddress(rs.getString("address"));
                student.setDepartmentId(rs.getString("department_id"));
                student.setImagePath(rs.getString("image_path"));
                student.setEnrollmentDate(rs.getDate("enrollment_date")); // Fetch enrollment date
            }
        } catch (SQLException e) {
            System.err.println("Error fetching student by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return student;
    }

    // Update a student in the database
    public boolean updateStudent(Student student) {
        String sql = "UPDATE students SET name=?, gender=?, dob=?, email=?, phone=?, status=?, address=?, department_id=?, image_path=?, enrollment_date=? WHERE id=?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, student.getName());
            stmt.setString(2, student.getGender());
            stmt.setDate(3, new java.sql.Date(student.getDob().getTime()));
            stmt.setString(4, student.getEmail());
            stmt.setString(5, student.getPhone());
            stmt.setString(6, student.getStatus());
            stmt.setString(7, student.getAddress());
            stmt.setString(8, student.getDepartmentId());
            stmt.setString(9, student.getImagePath());
            stmt.setDate(10, new java.sql.Date(student.getEnrollmentDate().getTime())); // Update enrollment date
            stmt.setInt(11, student.getId());

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.err.println("Error updating student: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Delete a student from the database
    public boolean deleteStudent(int studentId) {
        String sql = "DELETE FROM students WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);

            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting student: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    

    // Get students by department ID
    public List<Student> getStudentsByDepartment(String departmentId) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE department_id = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, departmentId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setName(rs.getString("name"));
                student.setGender(rs.getString("gender"));
                student.setDob(rs.getDate("dob"));
                student.setEmail(rs.getString("email"));
                student.setPhone(rs.getString("phone"));
                student.setStatus(rs.getString("status"));
                student.setAddress(rs.getString("address"));
                student.setDepartmentId(rs.getString("department_id"));
                student.setImagePath(rs.getString("image_path"));
                student.setEnrollmentDate(rs.getDate("enrollment_date")); // Fetch enrollment date
                students.add(student);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching students by department: " + e.getMessage());
            e.printStackTrace();
        }

        return students;
    }
}