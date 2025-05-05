package model;

import java.util.Date;

public class Student {
    private int id;
    private String name; // Combined name field
    private String gender;
    private Date dob;
    private String email;
    private String phone;
    private String status; // Status field
    private String address;
    private String departmentId;
    private String imagePath;
    private Date enrollmentDate; // New field for enrollment date

    // Constructor
    public Student() {}

    public Student(int id, String name, String gender, Date dob, String email, String phone,
                    String status, String address, String departmentId, 
                   String imagePath, Date enrollmentDate) { // Updated constructor
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.dob = dob;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.address = address;
        this.departmentId = departmentId;
        this.imagePath = imagePath;
        this.enrollmentDate = enrollmentDate; // Initialize enrollment date
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    // Getter and Setter for enrollmentDate
    public Date getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(Date enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }
}