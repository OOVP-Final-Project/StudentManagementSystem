package dao;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import config.DBConnection;

public class AttendanceDAO {
    // Method to fetch attendance records for a specific date and department
    public Map<Integer, String> getAttendanceByDateAndDepartment(String date, String departmentId) {
        Map<Integer, String> attendanceRecords = new HashMap<>();
        String sql = "SELECT student_id, attendance_status FROM attendance WHERE date = ? AND department_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(date));
            stmt.setString(2, departmentId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int studentId = rs.getInt("student_id");
                String status = rs.getString("attendance_status");
                attendanceRecords.put(studentId, status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return attendanceRecords;
    }

    // Method to mark attendance (insert or update)
    public boolean markAttendance(int studentId, String departmentId, String status, String date) {
        // Validate input parameters
        if (studentId <= 0 || departmentId == null || status == null || date == null) {
            System.err.println("Invalid input parameters");
            return false;
        }

        // Parse date
        Date sqlDate;
        try {
            sqlDate = Date.valueOf(date);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid date format: " + date);
            return false;
        }

        // Define SQL queries
        String sqlInsert = "INSERT INTO attendance (student_id, department_id, attendance_status, date) VALUES (?, ?, ?, ?)";
        String sqlUpdate = "UPDATE attendance SET attendance_status = ? WHERE student_id = ? AND department_id = ? AND date = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            // Try to insert the record
            try (PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert)) {
                stmtInsert.setInt(1, studentId);
                stmtInsert.setString(2, departmentId);
                stmtInsert.setString(3, status);
                stmtInsert.setDate(4, sqlDate);

                int rowsInserted = stmtInsert.executeUpdate();
                if (rowsInserted > 0) {
                    conn.commit(); // Commit transaction
                    return true; // Insert successful
                }
            }

            // Try to update the record
            try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
                stmtUpdate.setString(1, status);
                stmtUpdate.setInt(2, studentId);
                stmtUpdate.setString(3, departmentId);
                stmtUpdate.setDate(4, sqlDate);

                int rowsUpdated = stmtUpdate.executeUpdate();
                if (rowsUpdated > 0) {
                    conn.commit(); // Commit transaction
                    return true; // Update successful
                }
            }

            conn.rollback(); // Rollback if both fail
            return false;

        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}