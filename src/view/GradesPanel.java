package view;

import dao.DepartmentDAO;
import dao.GradeDAO;
import dao.StudentDAO;
import model.Student;
import model.StudentGrade;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GradesPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JComboBox<String> departmentFilter;
    private JTextField searchField;

    // Declare the JTable as a global instance variable
    private JTable table;

    public GradesPanel() {
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header Panel (Filter and Search)
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Table Panel
        add(createTablePanel(), BorderLayout.CENTER);

        // Footer Panel (Buttons)
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    // Create the Header Panel with Filter and Search
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        filterPanel.setBackground(headerPanel.getBackground());

        // Department Filter
        JLabel departmentLabel = new JLabel("Department:");
        departmentFilter = new JComboBox<>();
        departmentFilter.addItem("All Departments");
        populateDepartments(departmentFilter); // Populate departments
        filterPanel.add(departmentLabel);
        filterPanel.add(departmentFilter);

        // Search Field
        JLabel searchLabel = new JLabel("Search (ID/Name):");
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(300, 30));
        searchField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        filterPanel.add(searchLabel);
        filterPanel.add(searchField);

        // Add action listeners
        departmentFilter.addActionListener(e -> applyFilters());
        searchField.addActionListener(e -> applyFilters());

        headerPanel.add(filterPanel, BorderLayout.CENTER);
        return headerPanel;
    }

    // Populate the Department Combo Box
    private void populateDepartments(JComboBox<String> departmentBox) {
        List<String> departments = DepartmentDAO.getAllDepartments().stream()
                .map(dept -> dept.getId() + " - " + dept.getName())
                .toList();
        if (departments != null) {
            for (String dept : departments) {
                departmentBox.addItem(dept);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Failed to load departments.");
        }
    }

    // Create the Table Panel
    private JScrollPane createTablePanel() {
        String[] columns = {"ID", "Name", "Department", "Grade", "Semester", "Academic Year"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disable editing for all cells
            }
        };

        // Initialize the global JTable instance
        table = new JTable(tableModel);

        // Adjust Column Widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Department
        table.getColumnModel().getColumn(3).setPreferredWidth(80);  // Grade
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // Semester
        table.getColumnModel().getColumn(5).setPreferredWidth(100); // Academic Year

        refreshTable(); // Populate the table initially
        return new JScrollPane(table);
    }

    // Refresh the Table Data
    private void refreshTable() {
        List<StudentGrade> studentGrades = new GradeDAO().getStudentGrades();
        tableModel.setRowCount(0); // Clear existing rows

        for (StudentGrade sg : studentGrades) {
            tableModel.addRow(new Object[]{
                sg.getStudentId(),
                sg.getName(),
                sg.getDepartment(),
                sg.getGrade() != null ? sg.getGrade() : "", // Empty if null
                sg.getSemester() != null ? sg.getSemester() : "", // Empty if null
                sg.getAcademicYear() > 0 ? sg.getAcademicYear() : "" // Empty if 0
            });
        }
    }

    // Apply Filters Based on Department and Search Text
    private void applyFilters() {
        String selectedDepartment = (String) departmentFilter.getSelectedItem();
        String searchText = searchField.getText().trim();

        List<StudentGrade> filtered = new GradeDAO().getStudentGrades().stream()
                .filter(sg -> {
                    boolean matchesDepartment = "All Departments".equals(selectedDepartment) ||
                            sg.getDepartment().equalsIgnoreCase(selectedDepartment.split(" - ")[0]);
                    boolean matchesSearch = searchText.isEmpty() ||
                            String.valueOf(sg.getStudentId()).contains(searchText) ||
                            sg.getName().toLowerCase().contains(searchText.toLowerCase());
                    return matchesDepartment && matchesSearch;
                })
                .toList();

        // Update the table with filtered results
        tableModel.setRowCount(0); // Clear existing rows
        for (StudentGrade sg : filtered) {
            tableModel.addRow(new Object[]{
                sg.getStudentId(),
                sg.getName(),
                sg.getDepartment(),
                sg.getGrade() != null ? sg.getGrade() : "",
                sg.getSemester() != null ? sg.getSemester() : "",
                sg.getAcademicYear() > 0 ? sg.getAcademicYear() : ""
            });
        }
    }

    // Create the Footer Panel with Buttons
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnEdit = new JButton("Edit");
        JButton btnAddGrade = new JButton("Add Grade");

        // Action Listeners
        btnEdit.addActionListener(e -> editSelectedStudent());
        btnAddGrade.addActionListener(e -> addGradeForSelectedStudent());

        footerPanel.add(btnEdit);
        footerPanel.add(btnAddGrade);
        return footerPanel;
    }

    // Edit Selected Student
    private void editSelectedStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a grade record to edit.");
            return;
        }

        int studentId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentGrade = (String) tableModel.getValueAt(selectedRow, 3);
        String currentSemester = (String) tableModel.getValueAt(selectedRow, 4);
        int currentAcademicYear = (int) tableModel.getValueAt(selectedRow, 5);
        String studentName = (String) tableModel.getValueAt(selectedRow, 1);

        showEditGradeDialog(studentId, studentName, currentGrade, currentSemester, currentAcademicYear);
    }

    // Add Grade for Selected Student
    private void addGradeForSelectedStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a student to add a grade.");
            return;
        }

        int studentId = (int) tableModel.getValueAt(selectedRow, 0);
        String studentName = (String) tableModel.getValueAt(selectedRow, 1);

        showAddGradeDialog(studentId, studentName);
        refreshTable(); // Refresh the table after adding a grade
    }

    // Show Edit Student Dialog
    private void showEditGradeDialog(int studentId, String studentName, 
            String currentGrade, String currentSemester, 
            int currentAcademicYear) {
			JDialog editDialog = new JDialog();
			editDialog.setTitle("Edit Grade Record");
			editDialog.setSize(500, 350);
			editDialog.setLocationRelativeTo(null);
			editDialog.setModal(true);
			editDialog.setLayout(new BorderLayout());
			
			// Header Panel
			JPanel headerPanel = new JPanel();
			headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			JLabel titleLabel = new JLabel("Edit Grade for: " + studentName);
			titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
			headerPanel.add(titleLabel);
			editDialog.add(headerPanel, BorderLayout.NORTH);
			
			// Main Form Panel
			JPanel formPanel = new JPanel(new GridBagLayout());
			formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			
			// Student Info (read-only)
			gbc.gridx = 0; gbc.gridy = 0;
			formPanel.add(new JLabel("Student ID:"), gbc);
			gbc.gridx = 1;
			JTextField txtId = new JTextField(String.valueOf(studentId));
			txtId.setEditable(false);
			txtId.setBackground(Color.LIGHT_GRAY);
			formPanel.add(txtId, gbc);
			
			gbc.gridx = 0; gbc.gridy = 1;
			formPanel.add(new JLabel("Student Name:"), gbc);
			gbc.gridx = 1;
			JTextField txtName = new JTextField(studentName);
			txtName.setEditable(false);
			txtName.setBackground(Color.LIGHT_GRAY);
			formPanel.add(txtName, gbc);
			
			// Grade Selection
			gbc.gridx = 0; gbc.gridy = 2;
			formPanel.add(new JLabel("Grade:"), gbc);
			gbc.gridx = 1;
			JComboBox<String> gradeCombo = new JComboBox<>(new String[]{"A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "F"});
			gradeCombo.setSelectedItem(currentGrade);
			gradeCombo.setPreferredSize(new Dimension(200, 25));
			formPanel.add(gradeCombo, gbc);
			
			// Semester Input
			gbc.gridx = 0; gbc.gridy = 3;
			formPanel.add(new JLabel("Semester:"), gbc);
			gbc.gridx = 1;
			JTextField txtSemester = new JTextField(currentSemester);
			txtSemester.setPreferredSize(new Dimension(200, 25));
			formPanel.add(txtSemester, gbc);
			
			// Academic Year Input
			gbc.gridx = 0; gbc.gridy = 4;
			formPanel.add(new JLabel("Academic Year:"), gbc);
			gbc.gridx = 1;
			JTextField txtYear = new JTextField(String.valueOf(currentAcademicYear));
			txtYear.setPreferredSize(new Dimension(200, 25));
			formPanel.add(txtYear, gbc);
			
			editDialog.add(formPanel, BorderLayout.CENTER);
			
			// Button Panel
			JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
			buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
			
			JButton btnCancel = new JButton("Cancel");
			btnCancel.addActionListener(e -> editDialog.dispose());
			buttonPanel.add(btnCancel);
			
			JButton btnSubmit = new JButton("Save Changes");
			btnSubmit.setBackground(new Color(76, 175, 80));
			btnSubmit.setForeground(Color.WHITE);
			btnSubmit.addActionListener(e -> {
			try {
			String newGrade = (String) gradeCombo.getSelectedItem();
			String newSemester = txtSemester.getText().trim();
			int newAcademicYear = Integer.parseInt(txtYear.getText());
			
			if (newSemester.isEmpty()) {
			JOptionPane.showMessageDialog(editDialog, "Please enter a semester", "Error", JOptionPane.ERROR_MESSAGE);
			return;
			}
			
			boolean success = new GradeDAO().updateGrade(studentId, newGrade, newSemester, newAcademicYear);
			if (success) {
			JOptionPane.showMessageDialog(editDialog, "Grade updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
			refreshTable();
			editDialog.dispose();
			} else {
			JOptionPane.showMessageDialog(editDialog, "Failed to update grade", "Error", JOptionPane.ERROR_MESSAGE);
			}
			} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(editDialog, "Please enter a valid academic year", "Error", JOptionPane.ERROR_MESSAGE);
			} catch (Exception ex) {
			JOptionPane.showMessageDialog(editDialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
			});
			buttonPanel.add(btnSubmit);
			
			editDialog.add(buttonPanel, BorderLayout.SOUTH);
			
			editDialog.setVisible(true);
}

    // Show Add Grade Dialog
    private void showAddGradeDialog(int studentId, String studentName) {
        JDialog addDialog = new JDialog();
        addDialog.setTitle("Add New Grade");
        addDialog.setSize(500, 350);
        addDialog.setLocationRelativeTo(null);
        addDialog.setModal(true);
        addDialog.setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Add Grade for: " + studentName);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerPanel.add(titleLabel);
        addDialog.add(headerPanel, BorderLayout.NORTH);

        // Main Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Student Info (read-only)
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Student ID:"), gbc);
        gbc.gridx = 1;
        JTextField txtId = new JTextField(String.valueOf(studentId));
        txtId.setEditable(false);
        txtId.setBackground(Color.LIGHT_GRAY);
        formPanel.add(txtId, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Student Name:"), gbc);
        gbc.gridx = 1;
        JTextField txtName = new JTextField(studentName);
        txtName.setEditable(false);
        txtName.setBackground(Color.LIGHT_GRAY);
        formPanel.add(txtName, gbc);

        // Grade Selection
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Grade:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> gradeCombo = new JComboBox<>(new String[]{"A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "F"});
        gradeCombo.setSelectedIndex(0);
        gradeCombo.setPreferredSize(new Dimension(200, 25));
        formPanel.add(gradeCombo, gbc);

        // Semester Input
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Semester:"), gbc);
        gbc.gridx = 1;
        JTextField txtSemester = new JTextField();
        txtSemester.setPreferredSize(new Dimension(200, 25));
        formPanel.add(txtSemester, gbc);

        // Academic Year Input
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Academic Year:"), gbc);
        gbc.gridx = 1;
        JTextField txtYear = new JTextField();
        txtYear.setPreferredSize(new Dimension(200, 25));
        formPanel.add(txtYear, gbc);

        addDialog.add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> addDialog.dispose());
        buttonPanel.add(btnCancel);

        JButton btnSubmit = new JButton("Add Grade");
        btnSubmit.setBackground(new Color(76, 175, 80)); // Green color
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.addActionListener(e -> {
            try {
                String grade = (String) gradeCombo.getSelectedItem();
                String semester = txtSemester.getText().trim();
                int academicYear = Integer.parseInt(txtYear.getText());

                if (semester.isEmpty()) {
                    JOptionPane.showMessageDialog(addDialog, "Please enter a semester", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success = new GradeDAO().addGrade(studentId, grade, semester, academicYear);
                if (success) {
                    JOptionPane.showMessageDialog(addDialog, "Grade added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshTable();
                    addDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(addDialog, "Failed to add grade", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(addDialog, "Please enter a valid academic year", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(addDialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(btnSubmit);

        addDialog.add(buttonPanel, BorderLayout.SOUTH);
        addDialog.setVisible(true);
    }
}