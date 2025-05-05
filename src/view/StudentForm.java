package view;

import dao.DepartmentDAO;
import dao.StudentDAO;
import model.Department;
import model.Student;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StudentForm {
    private JTextField txtName, txtEmail, txtPhone;
    private JTextArea txtAddress;
    private JComboBox<String> genderBox, departmentBox;
    private JFormattedTextField txtDOB;
    private JLabel imageLabel;
    private String imagePath = "images/placeholder.jpg";
    private StudentListView studentListView; // Reference to StudentListView

    // Constructor to accept a StudentListView instance
    public StudentForm(StudentListView studentListView) {
        this.studentListView = studentListView; // Initialize the reference
    }

    public JPanel getContentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // Title Label
        JLabel lblTitle = new JLabel("Register New Student");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);

        // Name Field
        addLabel(panel, gbc, "Name:", 1);
        txtName = addTextField(panel, gbc, 1);

        // Gender Field
        addLabel(panel, gbc, "Gender:", 2);
        genderBox = addComboBox(panel, gbc, new String[]{"Male", "Female", "Other"}, 2);

        // Date of Birth Field
        addLabel(panel, gbc, "Date of Birth (yyyy-MM-dd):", 3);
        txtDOB = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
        txtDOB.setColumns(10);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(txtDOB, gbc);

        // Email Field
        addLabel(panel, gbc, "Email:", 4);
        txtEmail = addTextField(panel, gbc, 4);

        // Phone Field
        addLabel(panel, gbc, "Phone:", 5);
        txtPhone = addTextField(panel, gbc, 5);

        // Address Field
        addLabel(panel, gbc, "Address:", 6);
        txtAddress = new JTextArea(3, 20);
        txtAddress.setLineWrap(true);
        txtAddress.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(txtAddress);
        gbc.gridx = 1;
        gbc.gridy = 6;
        panel.add(scroll, gbc);

        // Department Field
        addLabel(panel, gbc, "Department:", 7);
        departmentBox = new JComboBox<>();
        populateDepartments();
        gbc.gridx = 1;
        gbc.gridy = 7;
        panel.add(departmentBox, gbc);

        // Image Upload Button
        JButton btnUpload = new JButton("Upload Image");
        btnUpload.addActionListener(_ -> chooseImage());
        gbc.gridx = 0;
        gbc.gridy = 8;
        panel.add(btnUpload, gbc);

        // Image Label
        imageLabel = new JLabel();
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        setImageLabel(imagePath);
        gbc.gridx = 1;
        gbc.gridy = 8;
        panel.add(imageLabel, gbc);

        // Submit Button
        JButton btnSubmit = new JButton("Register");
        btnSubmit.addActionListener(_ -> submitStudent());
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        panel.add(btnSubmit, gbc);

        return panel;
    }

    // Populate Departments in ComboBox
    private void populateDepartments() {
        List<Department> departments = DepartmentDAO.getAllDepartments();
        if (departments != null) {
            for (Department dept : departments) {
                departmentBox.addItem(dept.getId()); // Showing ID like "Comp.Sci"
            }
        }
    }

    // Choose Image File
    private void chooseImage() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            String destPath = "images/" + selected.getName();
            try {
                java.nio.file.Files.copy(selected.toPath(), new File(destPath).toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                imagePath = destPath;
                setImageLabel(imagePath);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error uploading image: " + ex.getMessage());
            }
        }
    }

    // Set Image to Label
    private void setImageLabel(String path) {
        ImageIcon icon = new ImageIcon(path);
        if (icon.getIconWidth() == -1) {
            JOptionPane.showMessageDialog(null, "Error: Invalid image path.");
            return;
        }
        Image img = icon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(img));
    }

    // Submit Student Data
    private void submitStudent() {
        try {
            Student student = new Student();
            student.setName(txtName.getText());
            student.setGender((String) genderBox.getSelectedItem());
            student.setDob(new SimpleDateFormat("yyyy-MM-dd").parse(txtDOB.getText()));
            student.setEmail(txtEmail.getText());
            student.setPhone(txtPhone.getText());
            student.setAddress(txtAddress.getText());
            student.setDepartmentId((String) departmentBox.getSelectedItem());
            student.setImagePath(imagePath);

            // Set the current date as the enrollment date
            student.setEnrollmentDate(new Date());

            boolean success = new StudentDAO().addStudent(student);
            JOptionPane.showMessageDialog(null,
                    success ? "Student registered successfully!" : "Error registering student.");

            if (success && studentListView != null) {
                studentListView.refreshTable(); // Refresh the table in StudentListView
                clearForm(); // Clear the form for the next registration
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Invalid input: " + e.getMessage());
        }
    }

    // Clear Form Fields
    private void clearForm() {
        txtName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        genderBox.setSelectedIndex(0);
        departmentBox.setSelectedIndex(0);
        txtDOB.setValue(null);
        imagePath = "images/placeholder.jpg";
        setImageLabel(imagePath);
    }

    // Helper Methods
    private JTextField addTextField(JPanel panel, GridBagConstraints gbc, int row) {
        JTextField textField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = row;
        panel.add(textField, gbc);
        return textField;
    }

    private JComboBox<String> addComboBox(JPanel panel, GridBagConstraints gbc, String[] options, int row) {
        JComboBox<String> comboBox = new JComboBox<>(options);
        gbc.gridx = 1;
        gbc.gridy = row;
        panel.add(comboBox, gbc);
        return comboBox;
    }

    private void addLabel(JPanel panel, GridBagConstraints gbc, String text, int row) {
        JLabel label = new JLabel(text);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(label, gbc);
    }
}