package view;

import dao.AttendanceDAO;
import dao.DepartmentDAO;
import dao.StudentDAO;
import model.Department;
import model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendancePanel {
    private DefaultTableModel tableModel;
    private Map<Integer, String> temporaryAttendanceData; // Temporary storage for attendance data
    private String currentDate; // To track the selected date

    public JPanel getContentPanel() {
        // Main panel with null layout for absolute positioning
        JPanel mainPanel = new JPanel(null);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setPreferredSize(new Dimension(1200, 800)); // Set preferred size

        // Header Panel
        JLabel titleLabel = new JLabel("Mark Attendance");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBounds(10, 10, 300, 30); // x, y, width, height
        mainPanel.add(titleLabel);

        // Select Department Label
        JLabel departmentLabel = new JLabel("Select Department:");
        departmentLabel.setBounds(10, 60, 150, 30);
        mainPanel.add(departmentLabel);

        // Department ComboBox
        JComboBox<String> departmentBox = new JComboBox<>();
        populateDepartments(departmentBox);
        departmentBox.setBounds(170, 60, 200, 30);
        mainPanel.add(departmentBox);

        // Date Picker Label
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setBounds(400, 60, 100, 30);
        mainPanel.add(dateLabel);

        // Fixed Date Field (Non-editable)
        JTextField dateField = new JTextField(LocalDate.now().toString());
        dateField.setEditable(false); // Make the date field non-editable
        dateField.setColumns(10);
        dateField.setBounds(510, 60, 150, 30);
        mainPanel.add(dateField);

        // Load Students Button
        JButton loadButton = new JButton("Load Students");
        loadButton.setBounds(680, 60, 150, 30);
        mainPanel.add(loadButton);

        // Table Panel
        JPanel tablePanel = new JPanel(null);
        tablePanel.setBorder(BorderFactory.createTitledBorder("Students"));
        tablePanel.setBounds(10, 110, 1160, 500); // Position and size the table panel
        mainPanel.add(tablePanel);

        // Table setup
        String[] columns = {"ID", "Name", "Department", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disable editing for all cells
            }
        };
        JTable table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setBounds(10, 20, 1140, 460); // Position and size the table scroll pane
        tablePanel.add(tableScrollPane);

        // Buttons Panel (Bottom)
        JPanel buttonsPanel = new JPanel(null);
        buttonsPanel.setBounds(10, 620, 1160, 50); // Position and size the buttons panel
        mainPanel.add(buttonsPanel);

        // Mark Present Button
        JButton presentButton = new JButton("Mark Present");
        presentButton.setBounds(10, 10, 150, 30);
        buttonsPanel.add(presentButton);

        // Mark Absent Button
        JButton absentButton = new JButton("Mark Absent");
        absentButton.setBounds(180, 10, 150, 30);
        buttonsPanel.add(absentButton);

        // Mark Sick Button
        JButton sickButton = new JButton("Mark Sick");
        sickButton.setBounds(350, 10, 150, 30);
        buttonsPanel.add(sickButton);

        // Attendance Report Button
        JButton reportButton = new JButton("Attendance Report");
        reportButton.setBounds(520, 10, 150, 30);
        buttonsPanel.add(reportButton);

        // Add action listeners for the buttons
        presentButton.addActionListener(e -> markAttendanceForSelectedStudents(table, "Present"));
        absentButton.addActionListener(e -> markAttendanceForSelectedStudents(table, "Absent"));
        sickButton.addActionListener(e -> markAttendanceForSelectedStudents(table, "Sick"));

        // Action listener for the Attendance Report button
        reportButton.addActionListener(e -> saveAttendanceReport());

        // Load Students for Selected Department
        loadButton.addActionListener(e -> {
            String departmentId = (String) departmentBox.getSelectedItem();
            if (departmentId == null || departmentId.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please select a department.");
                return;
            }

            String date = LocalDate.now().toString(); // Always use the current date
            currentDate = date; // Store the current date

            // Fetch all students in the selected department
            List<Student> students = new StudentDAO().getStudentsByDepartment(departmentId);

            // Fetch attendance records for the selected department and date
            AttendanceDAO attendanceDAO = new AttendanceDAO();
            Map<Integer, String> attendanceRecords = attendanceDAO.getAttendanceByDateAndDepartment(date, departmentId);

            tableModel.setRowCount(0); // Clear existing rows
            temporaryAttendanceData = new HashMap<>(); // Reset temporary data

            for (Student student : students) {
                String status = attendanceRecords.getOrDefault(student.getId(), "Absent"); // Default to "Absent"
                tableModel.addRow(new Object[]{
                        student.getId(),
                        student.getName(), // Combine first and last name
                        departmentId, // Use the department ID
                        status // Status from attendance records
                });

                // Initialize temporary attendance data
                temporaryAttendanceData.put(student.getId(), status);
            }
        });

        return mainPanel;
    }

    // Helper method to populate departments
    private void populateDepartments(JComboBox<String> departmentBox) {
        List<Department> departments = new DepartmentDAO().getAllDepartments();
        if (departments != null) {
            for (Department dept : departments) {
                departmentBox.addItem(dept.getId()); // Showing ID like "Comp.Sci"
            }
        }
    }

    // Method to mark attendance for selected students temporarily
    private void markAttendanceForSelectedStudents(JTable table, String status) {
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(null, "Please select at least one student.");
            return;
        }

        for (int row : selectedRows) {
            int studentId = (int) tableModel.getValueAt(row, 0); // Student ID
            temporaryAttendanceData.put(studentId, status); // Update temporary data
            tableModel.setValueAt(status, row, 3); // Update the status in the table
        }
    }

    // Method to save the attendance report by inserting data into the database
    private void saveAttendanceReport() {
        if (temporaryAttendanceData == null || temporaryAttendanceData.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No attendance data to save.");
            return;
        }

        AttendanceDAO attendanceDAO = new AttendanceDAO();
        boolean success = true;

        for (Map.Entry<Integer, String> entry : temporaryAttendanceData.entrySet()) {
            int studentId = entry.getKey();
            String status = entry.getValue();

            // Fetch the department ID from the first row of the table
            String departmentId = (String) tableModel.getValueAt(0, 2); // Column index for "Department"

            // Insert attendance record into the database
            boolean result = attendanceDAO.markAttendance(studentId, departmentId, status, currentDate);
            if (!result) {
                success = false; // Track failure
            }
        }

        if (success) {
            JOptionPane.showMessageDialog(null, "Attendance report saved successfully!");
            temporaryAttendanceData.clear(); // Clear temporary data after saving
        } else {
            JOptionPane.showMessageDialog(null, "Failed to save some attendance records.");
        }
    }
}