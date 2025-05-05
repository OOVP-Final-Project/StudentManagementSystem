package view;

import dao.DepartmentDAO;
import dao.StudentDAO;
import model.Department;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class StudentDashboard {

    public JPanel getContentPanel() {
        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Total Statistics Panel
        JPanel statsPanel = createStatsPanel();
        mainPanel.add(statsPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    /**
     * Creates the header panel with the title.
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 192, 0)); // Orange color
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Student Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        return headerPanel;
    }

    /**
     * Creates the statistics panel with total students, total departments,
     * and a pie chart for department-wise student distribution.
     */
    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top Section: Total Students and Departments
        JPanel topStatsPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        topStatsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel totalStudentsPanel = createStatPanel("Total Students", Color.CYAN, getStudentCount());
        topStatsPanel.add(totalStudentsPanel);

        JPanel totalDepartmentsPanel = createStatPanel("Total Departments", Color.ORANGE, getDepartmentCount());
        topStatsPanel.add(totalDepartmentsPanel);

        statsPanel.add(topStatsPanel, BorderLayout.NORTH);

        // Bottom Section: Pie Chart for Department-wise Student Distribution
        JPanel pieChartPanel = createPieChartPanel();
        statsPanel.add(pieChartPanel, BorderLayout.CENTER);

        return statsPanel;
    }

    /**
     * Creates a panel to display a statistic (e.g., total students or departments).
     */
    private JPanel createStatPanel(String label, Color backgroundColor, int count) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Title Label
        JLabel title = new JLabel(label);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(title, BorderLayout.NORTH);

        // Count Label
        JLabel countLabel = new JLabel(String.valueOf(count));
        countLabel.setFont(new Font("Arial", Font.BOLD, 36));
        countLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(countLabel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates a pie chart panel showing department-wise student distribution.
     */
    private JPanel createPieChartPanel() {
        // Fetch department-wise student counts
        Map<String, Integer> departmentCounts = DepartmentDAO.getStudentCountByDepartment();

        // Create a dataset for the pie chart
        DefaultPieDataset dataset = new DefaultPieDataset();
        if (departmentCounts != null && !departmentCounts.isEmpty()) {
            for (Map.Entry<String, Integer> entry : departmentCounts.entrySet()) {
                dataset.setValue(entry.getKey(), entry.getValue());
            }
        } else {
            dataset.setValue("No Data", 1); // Placeholder if no data is available
        }

        // Create the pie chart
        JFreeChart pieChart = ChartFactory.createPieChart(
                "Department-wise Student Distribution", // Chart title
                dataset,                              // Dataset
                true,                                 // Include legend
                true,                                 // Tooltips
                false                                 // URLs
        );

        // Customize the chart appearance
        pieChart.getTitle().setFont(new Font("Arial", Font.BOLD, 18));
        pieChart.getLegend().setItemFont(new Font("Arial", Font.PLAIN, 14));

        // Customize Pie Section Labels to Show Both Number and Percentage
        org.jfree.chart.plot.PiePlot plot = (org.jfree.chart.plot.PiePlot) pieChart.getPlot();
        plot.setLabelGenerator(new org.jfree.chart.labels.StandardPieSectionLabelGenerator(
                "{0}: {1} ({2})", // Format: Department Name: Number of Students (Percentage)
                new java.text.DecimalFormat("0"), // Format for number of students
                new java.text.DecimalFormat("0.0%") // Format for percentage
        ));

        // Create a ChartPanel to display the pie chart
        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setPreferredSize(new Dimension(600, 400));
        chartPanel.setBorder(BorderFactory.createTitledBorder("Department-wise Student Count"));

        return chartPanel;
    }

    /**
     * Retrieves the total number of students from the database.
     */
    private int getStudentCount() {
        List<model.Student> students = new StudentDAO().getAllStudents();
        return students != null ? students.size() : 0;
    }

    /**
     * Retrieves the total number of departments from the database.
     */
    private int getDepartmentCount() {
        List<Department> departments = DepartmentDAO.getAllDepartments();
        return departments != null ? departments.size() : 0;
    }
}