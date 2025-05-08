package view;


import javax.swing.*;
import java.awt.*;

public class MainPage extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public MainPage() {
        initialize();
    }

    private void initialize() {
        // Frame setup
        setTitle("Student Management Dashboard");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar Panel
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(7, 1, 5, 5));
        sidebar.setPreferredSize(new Dimension(200, getHeight()));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Buttons for Sidebar
        JButton btnDashboard = createSidebarButton("Dashboard");
        JButton btnRegister = createSidebarButton("Register Student");
        JButton btnViewStudents = createSidebarButton("View Students");
        JButton btnMarkAttendance = createSidebarButton("Mark Attendance");
        JButton btnAttendanceList = createSidebarButton("Attendance List");
        JButton btnManageGrades = createSidebarButton("Manage Grades"); // New Button

        // Add buttons to sidebar
        sidebar.add(btnDashboard);
        sidebar.add(btnRegister);
        sidebar.add(btnViewStudents);
        sidebar.add(btnMarkAttendance);
        sidebar.add(btnAttendanceList);
        sidebar.add(btnManageGrades);

        // Card Layout for Panels
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Dashboard Panel
        StudentDashboard dashboard = new StudentDashboard(); // Create the dashboard panel
        JPanel dashboardPanel = dashboard.getContentPanel();

        // Create a single instance of StudentListView
        StudentListView studentListView = new StudentListView();

        // Pass the same StudentListView instance to StudentForm
        StudentForm studentForm = new StudentForm(studentListView);

        // Mark Attendance Panel
        AttendancePanel attendancePanel = new AttendancePanel();

        // Attendance List Panel
        AttendanceListView attendanceListView = new AttendanceListView();

        // Grades Panel
        GradesPanel gradesPanel = new GradesPanel();

        // Add panels to card layout
        cardPanel.add(dashboardPanel, "dashboard");
        cardPanel.add(studentForm.getContentPanel(), "register");
        cardPanel.add(studentListView.getContentPanel(), "view");
        cardPanel.add(attendancePanel.getContentPanel(), "attendance");
        cardPanel.add(attendanceListView.getContentPanel(), "attendanceList");
        cardPanel.add(gradesPanel, "grades");

        // Add action listeners to buttons
        btnDashboard.addActionListener(e -> {
            // Remove the old dashboard panel (if it exists)
            cardPanel.remove(0); // Assuming dashboard is always the first panel

            // Create a new refreshed dashboard panel
            JPanel refreshedDashboard = new StudentDashboard().getContentPanel();

            // Add it back to card layout at index 0 with the same name
            cardPanel.add(refreshedDashboard, "dashboard");
            
            // Show the refreshed dashboard
            cardLayout.show(cardPanel, "dashboard");

            // Revalidate and repaint to apply changes
            cardPanel.revalidate();
            cardPanel.repaint();
        });

        btnRegister.addActionListener(e -> cardLayout.show(cardPanel, "register"));
        btnViewStudents.addActionListener(e -> cardLayout.show(cardPanel, "view"));
        btnMarkAttendance.addActionListener(e -> cardLayout.show(cardPanel, "attendance"));
        btnAttendanceList.addActionListener(e -> cardLayout.show(cardPanel, "attendanceList"));
        btnManageGrades.addActionListener(e -> cardLayout.show(cardPanel, "grades"));

        // Add components to the frame
        add(sidebar, BorderLayout.WEST);
        add(cardPanel, BorderLayout.CENTER);

        // Make the frame visible
        setVisible(true);
    }

    // Helper method to create styled buttons for the sidebar
    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        return button;
    }

    public static void main(String[] args) {
        // Run the application
        SwingUtilities.invokeLater(MainPage::new);
    }
}
