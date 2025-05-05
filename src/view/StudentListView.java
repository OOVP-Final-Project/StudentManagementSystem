package view;

import dao.DepartmentDAO;
import dao.GradeDAO;
import dao.StudentDAO;
import model.Department;
import model.Grade;
import model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

public class StudentListView {
    private DefaultTableModel tableModel;
    private List<Student> allStudents; // Store all students for filtering

    public JPanel getContentPanel() {
        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header Panel (Search Bar)
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Table Setup
        JTable table = createTable();
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Image Panel (Right Side)
        JPanel imagePanel = createImagePanel(table);

        // Split Pane (Table + Image Panel)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScrollPane, imagePanel);
        splitPane.setDividerLocation(800); // Set divider position
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        mainPanel.add(splitPane, BorderLayout.CENTER);
        return mainPanel;
    }

    // Create Header Panel with Search Bar
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(new Color(255, 192, 0));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title Panel (Top)
        JLabel titleLabel = new JLabel("View Students");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        headerPanel.add(titlePanel, BorderLayout.NORTH);

        // Filter Panel (Bottom)
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(headerPanel.getBackground());

        // Department ComboBox
        JComboBox<String> departmentComboBox = new JComboBox<>();
        departmentComboBox.addItem("All Departments");
        for (Department d : new DepartmentDAO().getAllDepartments()) {
            departmentComboBox.addItem(d.getId());
        }

        // Gender ComboBox
        JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"All Genders", "Male", "Female"});

        // Search Field
        JLabel searchLabel = new JLabel("Search:");
        JTextField searchField = new JTextField(15);

        // Find Button
        JButton findButton = new JButton("Find");
        findButton.addActionListener((ActionEvent e) -> {
            String dept = (String) departmentComboBox.getSelectedItem();
            String gender = (String) genderComboBox.getSelectedItem();
            String query = searchField.getText().trim();
            filterStudents(dept, gender, query);
        });

        filterPanel.add(new JLabel("Department:"));
        filterPanel.add(departmentComboBox);
        filterPanel.add(new JLabel("Gender:"));
        filterPanel.add(genderComboBox);
        filterPanel.add(searchLabel);
        filterPanel.add(searchField);
        filterPanel.add(findButton);

        headerPanel.add(filterPanel, BorderLayout.SOUTH);

        return headerPanel;
    }


    // Create Table and Populate Data
    private JTable createTable() {
        String[] columns = {"ID", "Name", "Gender", "DOB", "Email", "Phone","Status", "Address", "Department", "Image Path"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disable editing for all cells
            }
        };

        JTable table = new JTable(tableModel);
        allStudents = new StudentDAO().getAllStudents();
        populateTable(allStudents);

        return table;
    }

    // Populate Table with Student Data
    private void populateTable(List<Student> students) {
        tableModel.setRowCount(0); // Clear existing rows
        for (Student s : students) {
            tableModel.addRow(new Object[]{
                    s.getId(),
                    s.getName(),
                    s.getGender(),
                    s.getDob(),
                    s.getEmail(),
                    s.getPhone(),
                
                    s.getStatus() != null ? s.getStatus() : "Active",
                    s.getAddress(),
                    s.getDepartmentId(),
                    s.getImagePath()
            });
        }
    }

    // Create Image Panel
    private JPanel createImagePanel(JTable table) {
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBorder(BorderFactory.createTitledBorder("Student Image"));
        imagePanel.setBackground(Color.WHITE);

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton viewButton = new JButton("View");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        viewButton.addActionListener(e -> handleViewAction(table));
        editButton.addActionListener(e -> handleEditAction(table));
        deleteButton.addActionListener(e -> handleDeleteAction(table));

        buttonsPanel.add(viewButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        imagePanel.add(buttonsPanel, BorderLayout.SOUTH);

        // Add Selection Listener to Table
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String imagePath = (String) tableModel.getValueAt(selectedRow, 9);
                    setImageLabel(imageLabel, imagePath);
                } else {
                    imageLabel.setIcon(null);
                }
            }
        });

        return imagePanel;
    }

    // Handle View Action
    private void handleViewAction(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a student to view.");
            return;
        }

        int studentId = (int) tableModel.getValueAt(selectedRow, 0);
        Student student = new StudentDAO().getStudentById(studentId);
        if (student == null) {
            JOptionPane.showMessageDialog(null, "Failed to fetch student details.");
            return;
        }

        showStudentDetailsDialog(student);
    }

    // Handle Edit Action
    private void handleEditAction(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a student to edit.");
            return;
        }

        int studentId = (int) tableModel.getValueAt(selectedRow, 0);
        Student student = new StudentDAO().getStudentById(studentId);
        if (student == null) {
            JOptionPane.showMessageDialog(null, "Failed to fetch student details.");
            return;
        }

        showEditStudentDialog(student);
    }

    // Handle Delete Action
    private void handleDeleteAction(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a student to delete.");
            return;
        }

        int studentId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this student?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = new StudentDAO().deleteStudent(studentId);
            if (success) {
                JOptionPane.showMessageDialog(null, "Student deleted successfully!");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(null, "Failed to delete student.");
            }
        }
    }

 // Show Student Details Dialog
    private void showStudentDetailsDialog(Student student) {
        JFrame viewFrame = new JFrame("View Student Details");
        viewFrame.setSize(1200, 800);
        viewFrame.setLocationRelativeTo(null);

        // Main container panel with two columns
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

     // ===== LEFT SIDE: Student Info Panel =====
        JPanel leftPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.CENTER; // Center alignment for the image

        // ===== Student Image at Top Center =====
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(150, 200)); // Adjust size as needed

        String imagePath = student.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            ImageIcon icon = new ImageIcon(imagePath);
            Image scaledImage = icon.getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        leftPanel.add(imageLabel, gbc);

        // Reset constraints for other fields
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;

        // ==== First show Name, then ID ====
        addTextField(leftPanel, gbc, "Name:", student.getName(), 1, true);
        addTextField(leftPanel, gbc, "ID:", student.getId() + "", 2, true);

        // Rest remain the same
        addTextField(leftPanel, gbc, "Gender:", student.getGender(), 3, true);
        addTextField(leftPanel, gbc, "Date of Birth:", student.getDob().toString(), 4, true);
        addTextField(leftPanel, gbc, "Email:", student.getEmail(), 5, true);
        addTextField(leftPanel, gbc, "Phone:", student.getPhone(), 6, true);
        addTextField(leftPanel, gbc, "Enrollment Date:", student.getEnrollmentDate().toString(), 7, true);
        addTextField(leftPanel, gbc, "Status:", student.getStatus() != null ? student.getStatus() : "Active", 8, true);

        // Address
        JTextArea txtAddress = new JTextArea(student.getAddress(), 3, 20);
        txtAddress.setLineWrap(true);
        txtAddress.setWrapStyleWord(true);
        txtAddress.setEditable(false);
        txtAddress.setBackground(leftPanel.getBackground());
        JScrollPane scroll = new JScrollPane(txtAddress);
        addLabel(leftPanel, gbc, "Address:", 9, false, 1);
        gbc.gridx = 1;
        gbc.gridy = 9;
        leftPanel.add(scroll, gbc);

        // Department and Image Path
        addTextField(leftPanel, gbc, "Department:", student.getDepartmentId(), 10, true);
        addTextField(leftPanel, gbc, "Image Path:", student.getImagePath(), 11, true);

        // ===== RIGHT SIDE: Grades Panel =====
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbcRight = new GridBagConstraints();
        gbcRight.insets = new Insets(15, 15, 15, 15);
        gbcRight.anchor = GridBagConstraints.NORTH;

        JLabel gradesTitle = new JLabel("ACADEMIC PERFORMANCE");
        gradesTitle.setFont(new Font("Arial", Font.BOLD, 20));
        gradesTitle.setForeground(new Color(0, 102, 204));
        gbcRight.gridx = 0;
        gbcRight.gridy = 0;
        gbcRight.gridwidth = 2;
        rightPanel.add(gradesTitle, gbcRight);

        List<Grade> grades = new GradeDAO().getGradesByStudent(student.getId());
        gbcRight.gridwidth = 1;

        if (grades != null && !grades.isEmpty()) {
            int row = 1;
            for (Grade grade : grades) {
                JPanel gpaPanel = new JPanel(new BorderLayout());
                gpaPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                        BorderFactory.createEmptyBorder(20, 30, 20, 30)
                ));
                gpaPanel.setBackground(new Color(240, 248, 255));

                JLabel gradeValue = new JLabel(grade.getGrade(), SwingConstants.CENTER);
                gradeValue.setFont(new Font("Arial", Font.BOLD, 36));
                gradeValue.setForeground(new Color(0, 102, 204));
                gpaPanel.add(gradeValue, BorderLayout.CENTER);

                gbcRight.gridx = 0;
                gbcRight.gridy = row++;
                gbcRight.gridwidth = 2;
                rightPanel.add(gpaPanel, gbcRight);

                JPanel detailsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
                detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

                detailsPanel.add(new JLabel("Semester:"));
                detailsPanel.add(new JLabel(grade.getSemester()));
                detailsPanel.add(new JLabel("Year:"));
                detailsPanel.add(new JLabel(grade.getAcademicYear() + ""));

                gbcRight.gridy = row++;
                gbcRight.gridwidth = 2;
                rightPanel.add(detailsPanel, gbcRight);
            }
        } else {
            gbcRight.gridy = 1;
            gbcRight.gridwidth = 2;
            rightPanel.add(new JLabel("No grades available."), gbcRight);
        }

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        viewFrame.add(mainPanel);
        viewFrame.setVisible(true);
    }


    // Show Edit Student Dialog
    private void showEditStudentDialog(Student student) {
        JFrame updateFrame = new JFrame("Update Student");
        updateFrame.setSize(1200, 800);
        updateFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        addLabel(panel, gbc, "Update Student Details", 0, true, 2);

        JTextField txtName = addTextField(panel, gbc, "Name:", student.getName(), 1, false);
        JComboBox<String> genderBox = addComboBox(panel, gbc, "Gender:", new String[]{"Male", "Female", "Other"}, student.getGender(), 2);

        JFormattedTextField txtDOB = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
        txtDOB.setColumns(10);
        txtDOB.setText(student.getDob() != null ? student.getDob().toString() : "");
        addLabel(panel, gbc, "Date of Birth (yyyy-MM-dd):", 3, false, 1);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(txtDOB, gbc);

        JTextField txtEmail = addTextField(panel, gbc, "Email:", student.getEmail(), 4, false);
        JTextField txtPhone = addTextField(panel, gbc, "Phone:", student.getPhone(), 5, false);
        
        String[] statusOptions = {"Active", "Graduated", "Withdrawn"};
        JComboBox<String> statusBox = addComboBox(panel, gbc, "Status:", statusOptions, student.getStatus() != null ? student.getStatus() : "Active", 7);

        JTextArea txtAddress = new JTextArea(student.getAddress(), 3, 20);
        txtAddress.setLineWrap(true);
        txtAddress.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(txtAddress);
        addLabel(panel, gbc, "Address:", 8, false, 1);
        gbc.gridx = 1;
        gbc.gridy = 8;
        panel.add(scroll, gbc);

        JComboBox<String> departmentBox = new JComboBox<>();
        populateDepartments(departmentBox);
        departmentBox.setSelectedItem(student.getDepartmentId());
        addLabel(panel, gbc, "Department:", 9, false, 1);
        gbc.gridx = 1;
        gbc.gridy = 9;
        panel.add(departmentBox, gbc);

        JButton btnSubmit = new JButton("Update");
        btnSubmit.addActionListener(e -> {
            try {
                student.setName(txtName.getText());
                student.setGender((String) genderBox.getSelectedItem());
                student.setDob(new SimpleDateFormat("yyyy-MM-dd").parse(txtDOB.getText()));
                student.setEmail(txtEmail.getText());
                student.setPhone(txtPhone.getText());
                
                student.setStatus((String) statusBox.getSelectedItem());
                student.setAddress(txtAddress.getText());
                student.setDepartmentId((String) departmentBox.getSelectedItem());

                boolean success = new StudentDAO().updateStudent(student);
                JOptionPane.showMessageDialog(null, success ? "Student updated successfully!" : "Error updating student.");
                if (success) {
                    refreshTable();
                    updateFrame.dispose();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Invalid input: " + ex.getMessage());
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        panel.add(btnSubmit, gbc);

        updateFrame.add(panel);
        updateFrame.setVisible(true);
    }

    // Helper Methods
    private void addLabel(JPanel panel, GridBagConstraints gbc, String text, int row, boolean bold, int gridWidth) {
        JLabel label = new JLabel(text);
        label.setFont(bold ? new Font("Arial", Font.BOLD, 20) : new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = gridWidth;
        panel.add(label, gbc);
    }

    private JTextField addTextField(JPanel panel, GridBagConstraints gbc, String labelText, String initialValue, int row, boolean readOnly) {
        addLabel(panel, gbc, labelText, row, false, 1);
        JTextField textField = new JTextField(initialValue, 20);
        textField.setEditable(!readOnly);
        gbc.gridx = 1;
        gbc.gridy = row;
        panel.add(textField, gbc);
        return textField;
    }

    private JComboBox<String> addComboBox(JPanel panel, GridBagConstraints gbc, String labelText, String[] options, String selectedItem, int row) {
        addLabel(panel, gbc, labelText, row, false, 1);
        JComboBox<String> comboBox = new JComboBox<>(options);
        comboBox.setSelectedItem(selectedItem);
        gbc.gridx = 1;
        gbc.gridy = row;
        panel.add(comboBox, gbc);
        return comboBox;
    }

    private void populateDepartments(JComboBox<String> departmentBox) {
        List<Department> departments = DepartmentDAO.getAllDepartments();
        if (departments != null) {
            for (Department dept : departments) {
                departmentBox.addItem(dept.getId());
            }
        }
    }

    private void setImageLabel(JLabel imageLabel, String path) {
        ImageIcon icon = new ImageIcon(path);
        if (icon == null || icon.getIconWidth() == -1) {
            JOptionPane.showMessageDialog(null, "Error: Invalid image path: " + path);
            imageLabel.setIcon(null);
        } else {
            Image img = icon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(img));
        }
    }

    private void filterStudents(String department, String gender, String query) {
        List<Student> filtered = allStudents.stream()
            .filter(s -> {
                boolean match = true;

                if (!"All Departments".equals(department)) {
                    match &= department.equals(s.getDepartmentId());
                }

                if (!"All Genders".equals(gender)) {
                    match &= gender.equalsIgnoreCase(s.getGender());
                }

                if (!query.isEmpty()) {
                    String lower = query.toLowerCase();
                    match &= String.valueOf(s.getId()).contains(lower)
                          || s.getName().toLowerCase().contains(lower)
                          || s.getEmail().toLowerCase().contains(lower);
                }

                return match;
            })
            .toList();

        populateTable(filtered);
    }


    public void refreshTable() {
        allStudents = new StudentDAO().getAllStudents();
        populateTable(allStudents);
    }
}