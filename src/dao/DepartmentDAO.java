package dao;

import model.Department;
import java.sql.*;
import java.util.*;

import config.DBConnection;

public class DepartmentDAO {

    // Existing method to fetch all departments
    public static List<Department> getAllDepartments() {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT * FROM department";  // Verify this query is correct

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("Executing query: " + sql);  // Log the query

            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Department department = new Department();
                    department.setId(rs.getString("id"));
                    department.setName(rs.getString("name"));
                    departments.add(department);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error during SQL execution: " + e.getMessage());
            e.printStackTrace();  // Print the full stack trace for debugging
        }

        return departments;
    }

    // New method to get student count by department
    public static Map<String, Integer> getStudentCountByDepartment() {
        Map<String, Integer> departmentCounts = new HashMap<>();
        String sql = "SELECT department_id, COUNT(*) AS student_count " +
                     "FROM students " +
                     "GROUP BY department_id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("Executing query: " + sql);  // Log the query

            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    String departmentId = rs.getString("department_id");
                    int studentCount = rs.getInt("student_count");
                    departmentCounts.put(departmentId, studentCount);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error during SQL execution: " + e.getMessage());
            e.printStackTrace();  // Print the full stack trace for debugging
        }

        return departmentCounts;
    }
}