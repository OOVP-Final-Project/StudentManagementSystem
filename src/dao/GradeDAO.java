package dao;

import model.Grade;
import model.StudentGrade;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import config.DBConnection;

public class GradeDAO {

    private Connection connect() throws SQLException {
        return DBConnection.getConnection();
    }

    // Add a new grade
    public boolean addGrade(int studentId, String grade, String semester, int academicYear) {
        String sql = "INSERT INTO grades (student_id, grade, semester, academic_year) VALUES (?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setString(2, grade);
            stmt.setString(3, semester);
            stmt.setInt(4, academicYear);

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all grades for a student
    public List<Grade> getGradesByStudent(int studentId) {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT * FROM grades WHERE student_id = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Grade grade = new Grade();
                grade.setId(rs.getInt("id"));
                grade.setStudentId(rs.getInt("student_id"));
                grade.setGrade(rs.getString("grade"));
                grade.setSemester(rs.getString("semester"));
                grade.setAcademicYear(rs.getInt("academic_year"));
                grades.add(grade);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return grades;
    }
    
    public List<StudentGrade> getStudentGrades() {
        List<StudentGrade> studentGrades = new ArrayList<>();
        String sql = "SELECT s.id AS student_id, s.name AS student_name, s.department_id AS department, "
                   + "g.grade AS grade, g.semester AS semester, g.academic_year AS academic_year "
                   + "FROM students s LEFT JOIN grades g ON s.id = g.student_id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                StudentGrade sg = new StudentGrade();
                sg.setStudentId(rs.getInt("student_id"));
                sg.setName(rs.getString("student_name"));
                sg.setDepartment(rs.getString("department"));
                sg.setGrade(rs.getString("grade")); // Can be null
                sg.setSemester(rs.getString("semester")); // Can be null
                sg.setAcademicYear(rs.getInt("academic_year")); // Default to 0 if null
                studentGrades.add(sg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return studentGrades;
    }

	public boolean updateGrade(int studentId, String newGrade, String newSemester, int newAcademicYear) {
		// TODO Auto-generated method stub
		return false;
	}
}