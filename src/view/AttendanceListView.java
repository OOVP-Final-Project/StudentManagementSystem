package view;

import dao.AttendanceDAO;
import dao.DepartmentDAO;
import dao.StudentDAO;
import model.Department;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class AttendanceListView {
    private DefaultTableModel tableModel;

    public JPanel getContentPanel() {
        // Main Panel
        JPanel mainPanel = new JPanel(null); // Use null layout for absolute positioning
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.WHITE);

        // Header Panel (Only Title)
        JPanel headerPanel = new JPanel();
        headerPanel.setBounds(10, 10, 1180, 60); // Set bounds (x, y, width, height)
        headerPanel.setBackground(new Color(255, 192, 0)); // Orange color
        headerPanel.setLayout(null);

        JLabel titleLabel = new JLabel("Attendance List");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(10, 10, 1160, 40); // Center-align the title within the header panel
        headerPanel.add(titleLabel);

        mainPanel.add(headerPanel);

        // Filter Panel (Below Header)
        JPanel filterPanel = new JPanel(null); // Use null layout for absolute positioning
        filterPanel.setBounds(10, 80, 1180, 100); // Set bounds below the header
        filterPanel.setBackground(new Color(245, 245, 245)); // Light gray background

        // Department Filter
        JLabel departmentLabel = new JLabel("Department:");
        departmentLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        departmentLabel.setBounds(20, 20, 100, 30); // Position the label
        filterPanel.add(departmentLabel);

        JComboBox<String> departmentBox = new JComboBox<>();
        populateDepartments(departmentBox);
        departmentBox.setBounds(130, 20, 150, 30); // Position the combo box
        filterPanel.add(departmentBox);

        // Date Filter
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        dateLabel.setBounds(300, 20, 50, 30); // Position the label
        filterPanel.add(dateLabel);

        JTextField dateField = new JTextField(LocalDate.now().toString(), 10); // Default to current date
        dateField.setBounds(360, 20, 120, 30); // Position the text field
        filterPanel.add(dateField);

        JButton datePickerButton = new JButton("Pick Date");
        datePickerButton.setBounds(490, 20, 100, 30); // Position the button
        datePickerButton.addActionListener(_ -> {
            String selectedDate = showDatePickerDialog();
            if (selectedDate != null) {
                dateField.setText(selectedDate);
            }
        });
        filterPanel.add(datePickerButton);

        // Apply Filters Button
        JButton applyFiltersButton = new JButton("Apply Filters");
        applyFiltersButton.setFont(new Font("Arial", Font.BOLD, 14));
        applyFiltersButton.setBounds(600, 20, 150, 30); // Position the button
        applyFiltersButton.addActionListener(_ -> {
            String departmentId = (String) departmentBox.getSelectedItem();
            String selectedDate = dateField.getText();

            // Fetch attendance data based on filters
            AttendanceDAO attendanceDAO = new AttendanceDAO();
            Map<Integer, String> attendanceRecords = attendanceDAO.getAttendanceByDateAndDepartment(selectedDate, departmentId);

            // Populate the table with filtered data
            populateTable(attendanceRecords);
        });
        filterPanel.add(applyFiltersButton);

        mainPanel.add(filterPanel);

        // Table Setup
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
        tableScrollPane.setBounds(10, 190, 1180, 400); // Set bounds below the filter panel
        tableScrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        mainPanel.add(tableScrollPane);

        return mainPanel;
    }

    // Show a simple date picker dialog
    private String showDatePickerDialog() {
        JSpinner spinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "yyyy-MM-dd");
        spinner.setEditor(editor);

        int result = JOptionPane.showConfirmDialog(null, spinner, "Select Date", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            return editor.getFormat().format(((java.util.Date) spinner.getValue()));
        }
        return null;
    }

    // Populate the table with attendance data
    private void populateTable(Map<Integer, String> attendanceRecords) {
        tableModel.setRowCount(0); // Clear existing rows

        if (attendanceRecords == null || attendanceRecords.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No attendance records found for the selected filters.");
            return;
        }

        // Fetch student details and populate the table
        for (Map.Entry<Integer, String> entry : attendanceRecords.entrySet()) {
            int studentId = entry.getKey();
            String status = entry.getValue();

            // Fetch student details from the database
            StudentDAO studentDAO = new StudentDAO();
            model.Student student = studentDAO.getStudentById(studentId);

            if (student != null) {
                tableModel.addRow(new Object[]{
                        student.getId(),
                        student.getName(),
                        student.getDepartmentId(),
                        status
                });
            }
        }
    }

    // Helper method to populate departments
    private void populateDepartments(JComboBox<String> departmentBox) {
        new DepartmentDAO();
		List<Department> departments = DepartmentDAO.getAllDepartments();
        if (departments != null) {
            for (Department dept : departments) {
                departmentBox.addItem(dept.getId()); // Showing ID like "Comp.Sci"
            }
        }
    }
}