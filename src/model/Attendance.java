package model;

import java.time.LocalDate;

public class Attendance {
    private int studentId;
    private String departmentId;
    private String attendanceStatus; // "Present", "Absent", "Sick"
    private LocalDate date;

    // Constructor
    public Attendance() {}

    public Attendance(int studentId, String departmentId, String attendanceStatus, LocalDate date) {
        this.studentId = studentId;
        this.departmentId = departmentId;
        this.attendanceStatus = attendanceStatus;
        this.date = date;
    }

    // Getters and Setters
    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(String attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}