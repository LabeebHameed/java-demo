package com.college;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

/**
 * ============================================================================
 * EduTrack - College Student Management System
 * ============================================================================
 * Designed for teaching junior students modern Java Swing GUI & JDBC integration.
 * Features:
 *   - Sleek, modern FlatLaf UI with rounded cards & badges
 *   - Real-time search filtering & live dashboard KPI cards
 *   - Full CRUD: Create, Read, Update, Delete with database synchronization
 * ============================================================================
 */
public class StudentApp extends JFrame {

    // === UI Color Palette (Modern Indigo / Slate Theme) ===
    private static final Color PRIMARY_COLOR    = new Color(79, 70, 229);   // Indigo-600
    private static final Color PRIMARY_HOVER    = new Color(67, 56, 202);   // Indigo-700
    private static final Color BG_LIGHT         = new Color(248, 250, 252); // Slate-50
    private static final Color CARD_BG          = Color.WHITE;
    private static final Color TEXT_MAIN        = new Color(15, 23, 42);    // Slate-900
    private static final Color TEXT_MUTED       = new Color(100, 116, 139); // Slate-500
    private static final Color BORDER_COLOR     = new Color(226, 232, 240); // Slate-200
    private static final Color SUCCESS_COLOR    = new Color(16, 185, 129);  // Emerald-500
    private static final Color DANGER_COLOR     = new Color(239, 68, 68);   // Red-500

    // === Form Input Components ===
    private JTextField txtRollNo;
    private JTextField txtName;
    private JTextField txtEmail;
    private JComboBox<String> cbDepartment;
    private JComboBox<String> cbYear;
    private JTextField txtGpa;
    private JComboBox<String> cbStatus;

    // === Action Buttons ===
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnClear;
    private JButton btnDelete;
    private JButton btnRefresh;

    // === Search & Table Components ===
    private JTextField txtSearch;
    private JTable studentTable;
    private DefaultTableModel tableModel;

    // === Dashboard KPI Metric Labels ===
    private JLabel lblTotalCount;
    private JLabel lblActiveCount;
    private JLabel lblAvgGpa;

    // === Status / Notification Bar ===
    private JLabel lblStatusToast;

    // Currently selected student ID (-1 means none selected)
    private int selectedStudentId = -1;

    public StudentApp() {
        // 1. Window setup
        setTitle("EduTrack — College Student Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 760);
        setMinimumSize(new Dimension(980, 640));
        setLocationRelativeTo(null); // Center on screen
        getContentPane().setBackground(BG_LIGHT);

        // 2. Build UI Layout
        setLayout(new BorderLayout(0, 0));
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainContentPanel(), BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);

        // 3. Connect to Database & Load Initial Data
        initDatabaseConnection();
    }

    /**
     * Initializes Database and loads table records with error safety.
     */
    private void initDatabaseConnection() {
        try {
            DatabaseHelper.initDatabase();
            loadStudentData();
            updateDashboardStats();
            showStatus("✓ Connected to MySQL database successfully.", SUCCESS_COLOR);
        } catch (SQLException e) {
            showStatus("⚠ Database Error: " + e.getMessage(), DANGER_COLOR);
            JOptionPane.showMessageDialog(this,
                    "Could not connect to MySQL.\n\n"
                    + "1. Ensure MySQL server is running on localhost:3306.\n"
                    + "2. Check username/password in DatabaseHelper.java.\n\n"
                    + "Error details: " + e.getMessage(),
                    "Database Connection Notice",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    // =========================================================================
    // UI BUILDER METHODS
    // =========================================================================

    /**
     * Creates the top Header containing the App Brand and 3 KPI Stat Cards.
     */
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setBackground(CARD_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                new EmptyBorder(16, 24, 16, 24)
        ));

        // Brand Info (Left)
        JPanel brandPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        brandPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("🎓 EduTrack");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(PRIMARY_COLOR);

        JLabel lblSubtitle = new JLabel("College Student Management System");
        lblSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSubtitle.setForeground(TEXT_MUTED);

        brandPanel.add(lblTitle);
        brandPanel.add(lblSubtitle);

        // Metrics KPI Cards (Right)
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        statsPanel.setOpaque(false);

        lblTotalCount  = new JLabel("0");
        lblActiveCount = new JLabel("0");
        lblAvgGpa      = new JLabel("0.00");

        statsPanel.add(createKpiCard("Total Students", lblTotalCount, new Color(241, 245, 249), TEXT_MAIN));
        statsPanel.add(createKpiCard("Active Enrolled", lblActiveCount, new Color(209, 250, 229), new Color(6, 95, 70)));
        statsPanel.add(createKpiCard("Average GPA", lblAvgGpa, new Color(254, 243, 199), new Color(146, 64, 14)));

        header.add(brandPanel, BorderLayout.WEST);
        header.add(statsPanel, BorderLayout.EAST);

        return header;
    }

    /**
     * Helper to create a clean metric KPI pill card.
     */
    private JPanel createKpiCard(String title, JLabel valueLabel, Color bg, Color textColor) {
        JPanel card = new JPanel(new BorderLayout(0, 2));
        card.setBackground(bg);
        card.setBorder(new EmptyBorder(8, 16, 8, 16));
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 12");

        JLabel lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblTitle.setForeground(TEXT_MUTED);

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        valueLabel.setForeground(textColor);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    /**
     * Main Split View: Left (Student Form) and Right (Data Table).
     */
    private JPanel createMainContentPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 0));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(20, 24, 20, 24));

        mainPanel.add(createStudentFormCard(), BorderLayout.WEST);
        mainPanel.add(createTableCard(), BorderLayout.CENTER);

        return mainPanel;
    }

    /**
     * Left Column: Student Details Input Form Card.
     */
    private JPanel createStudentFormCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setPreferredSize(new Dimension(360, 0));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 16");

        // Form Title
        JLabel lblFormHeader = new JLabel("Student Information");
        lblFormHeader.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblFormHeader.setForeground(TEXT_MAIN);
        lblFormHeader.setBorder(new EmptyBorder(0, 0, 16, 0));

        // Form Fields Container
        JPanel fieldsPanel = new JPanel(new GridLayout(14, 1, 0, 4));
        fieldsPanel.setOpaque(false);

        // 1. Roll Number
        txtRollNo = createStyledTextField("e.g. CS-2024-001");
        fieldsPanel.add(createFieldLabel("Roll / Student ID *"));
        fieldsPanel.add(txtRollNo);

        // 2. Full Name
        txtName = createStyledTextField("e.g. John Doe");
        fieldsPanel.add(createFieldLabel("Full Name *"));
        fieldsPanel.add(txtName);

        // 3. Email
        txtEmail = createStyledTextField("e.g. john@college.edu");
        fieldsPanel.add(createFieldLabel("Email Address *"));
        fieldsPanel.add(txtEmail);

        // 4. Department
        cbDepartment = new JComboBox<>(new String[]{
                "Computer Science", "Data Science", "Information Tech",
                "Electronics", "Mechanical", "Civil", "Business Admin"
        });
        cbDepartment.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        fieldsPanel.add(createFieldLabel("Department"));
        fieldsPanel.add(cbDepartment);

        // 5. Year Level
        cbYear = new JComboBox<>(new String[]{"1st Year", "2nd Year", "3rd Year", "4th Year"});
        cbYear.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        fieldsPanel.add(createFieldLabel("Year Level"));
        fieldsPanel.add(cbYear);

        // 6. GPA
        txtGpa = createStyledTextField("e.g. 3.85 (0.00 - 4.00)");
        fieldsPanel.add(createFieldLabel("GPA / Score *"));
        fieldsPanel.add(txtGpa);

        // 7. Status
        cbStatus = new JComboBox<>(new String[]{"Active", "Graduated", "On Leave"});
        cbStatus.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        fieldsPanel.add(createFieldLabel("Enrollment Status"));
        fieldsPanel.add(cbStatus);

        // Form Action Buttons (Add, Update, Clear)
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 8, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(16, 0, 0, 0));

        btnAdd = new JButton("Add Student");
        styleButton(btnAdd, PRIMARY_COLOR, Color.WHITE);
        btnAdd.addActionListener(e -> handleAddStudent());

        btnUpdate = new JButton("Update");
        styleButton(btnUpdate, new Color(51, 65, 85), Color.WHITE);
        btnUpdate.setEnabled(false);
        btnUpdate.addActionListener(e -> handleUpdateStudent());

        btnClear = new JButton("Clear");
        styleButton(btnClear, new Color(241, 245, 249), TEXT_MAIN);
        btnClear.addActionListener(e -> clearForm());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnClear);

        // Assemble Form Card
        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.setOpaque(false);
        topWrapper.add(lblFormHeader, BorderLayout.NORTH);
        topWrapper.add(fieldsPanel, BorderLayout.CENTER);

        card.add(topWrapper, BorderLayout.NORTH);
        card.add(buttonPanel, BorderLayout.SOUTH);

        return card;
    }

    /**
     * Right Column: Student Table Card with Search & Quick Actions.
     */
    private JPanel createTableCard() {
        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(16, 20, 16, 20)
        ));
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 16");

        // Toolbar: Search Bar + Action Buttons
        JPanel toolbar = new JPanel(new BorderLayout(12, 0));
        toolbar.setOpaque(false);

        // Search Field with Live Typing Filter
        txtSearch = new JTextField();
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "🔍  Search by Name, Roll No, Email, Dept...");
        txtSearch.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 4,10,4,10");
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                handleSearch(txtSearch.getText().trim());
            }
        });

        // Right action buttons (Delete & Refresh)
        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionButtons.setOpaque(false);

        btnDelete = new JButton("Delete");
        styleButton(btnDelete, new Color(254, 226, 226), DANGER_COLOR);
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(e -> handleDeleteStudent());

        btnRefresh = new JButton("↻ Refresh");
        styleButton(btnRefresh, new Color(241, 245, 249), TEXT_MAIN);
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadStudentData();
            updateDashboardStats();
            showStatus("Table refreshed.", TEXT_MUTED);
        });

        actionButtons.add(btnDelete);
        actionButtons.add(btnRefresh);

        toolbar.add(txtSearch, BorderLayout.CENTER);
        toolbar.add(actionButtons, BorderLayout.EAST);

        // Table Setup
        String[] columns = {"ID", "Roll No", "Full Name", "Email", "Department", "Year", "GPA", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make read-only in table, edit via form
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(36);
        studentTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setShowVerticalLines(false);
        studentTable.setGridColor(new Color(241, 245, 249));

        // Hide the internal Database ID column from UI view
        studentTable.removeColumn(studentTable.getColumnModel().getColumn(0));

        // Table Header Styling
        JTableHeader header = studentTable.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(TEXT_MUTED);
        header.setPreferredSize(new Dimension(0, 38));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        // Custom Status Badge Renderer
        studentTable.getColumnModel().getColumn(6).setCellRenderer(new StatusBadgeRenderer());

        // Centered Renderer for GPA & Year
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        studentTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Year
        studentTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // GPA

        // Table Click Listener: Select Row -> Populate Form
        studentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                populateFormFromSelectedRow();
            }
        });

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
        scrollPane.getViewport().setBackground(CARD_BG);

        card.add(toolbar, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    /**
     * Bottom Status / Notification Snackbar.
     */
    private JPanel createStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(CARD_BG);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
                new EmptyBorder(8, 24, 8, 24)
        ));

        lblStatusToast = new JLabel("System Ready");
        lblStatusToast.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblStatusToast.setForeground(TEXT_MUTED);

        JLabel lblTip = new JLabel("Tip: Click any student row to edit or delete");
        lblTip.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblTip.setForeground(TEXT_MUTED);

        bar.add(lblStatusToast, BorderLayout.WEST);
        bar.add(lblTip, BorderLayout.EAST);

        return bar;
    }

    // =========================================================================
    // EVENT HANDLERS & CRUD OPERATIONS
    // =========================================================================

    /**
     * Handles Adding a New Student record to MySQL.
     */
    private void handleAddStudent() {
        if (!validateFormInputs()) return;

        try {
            Student student = new Student(
                    txtRollNo.getText().trim(),
                    txtName.getText().trim(),
                    txtEmail.getText().trim(),
                    (String) cbDepartment.getSelectedItem(),
                    (String) cbYear.getSelectedItem(),
                    Double.parseDouble(txtGpa.getText().trim()),
                    (String) cbStatus.getSelectedItem()
            );

            boolean success = DatabaseHelper.addStudent(student);
            if (success) {
                clearForm();
                loadStudentData();
                updateDashboardStats();
                showStatus("✓ Student " + student.getName() + " added successfully!", SUCCESS_COLOR);
            }
        } catch (SQLException ex) {
            showStatus("⚠ Error adding student: " + ex.getMessage(), DANGER_COLOR);
            JOptionPane.showMessageDialog(this, "Failed to add student:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles Updating an Existing Student record.
     */
    private void handleUpdateStudent() {
        if (selectedStudentId == -1) {
            showStatus("⚠ Please select a student from the table first.", DANGER_COLOR);
            return;
        }

        if (!validateFormInputs()) return;

        try {
            Student student = new Student(
                    selectedStudentId,
                    txtRollNo.getText().trim(),
                    txtName.getText().trim(),
                    txtEmail.getText().trim(),
                    (String) cbDepartment.getSelectedItem(),
                    (String) cbYear.getSelectedItem(),
                    Double.parseDouble(txtGpa.getText().trim()),
                    (String) cbStatus.getSelectedItem()
            );

            boolean success = DatabaseHelper.updateStudent(student);
            if (success) {
                clearForm();
                loadStudentData();
                updateDashboardStats();
                showStatus("✓ Student updated successfully!", SUCCESS_COLOR);
            }
        } catch (SQLException ex) {
            showStatus("⚠ Error updating student: " + ex.getMessage(), DANGER_COLOR);
            JOptionPane.showMessageDialog(this, "Failed to update student:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles Deleting a selected Student with Confirmation.
     */
    private void handleDeleteStudent() {
        if (selectedStudentId == -1) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this student record?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = DatabaseHelper.deleteStudent(selectedStudentId);
                if (success) {
                    clearForm();
                    loadStudentData();
                    updateDashboardStats();
                    showStatus("✓ Student deleted successfully.", SUCCESS_COLOR);
                }
            } catch (SQLException ex) {
                showStatus("⚠ Error deleting student: " + ex.getMessage(), DANGER_COLOR);
                JOptionPane.showMessageDialog(this, "Failed to delete student:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Live search filtering as the user types.
     */
    private void handleSearch(String keyword) {
        if (keyword.isEmpty()) {
            loadStudentData();
            return;
        }

        try {
            List<Student> results = DatabaseHelper.searchStudents(keyword);
            renderTableData(results);
            showStatus("Found " + results.size() + " matching student(s).", TEXT_MUTED);
        } catch (SQLException e) {
            showStatus("Search error: " + e.getMessage(), DANGER_COLOR);
        }
    }

    /**
     * Loads all records from MySQL into the JTable.
     */
    private void loadStudentData() {
        try {
            List<Student> list = DatabaseHelper.getAllStudents();
            renderTableData(list);
        } catch (SQLException e) {
            showStatus("⚠ Could not load records: " + e.getMessage(), DANGER_COLOR);
        }
    }

    /**
     * Populates the JTable rows from a List of Students.
     */
    private void renderTableData(List<Student> students) {
        tableModel.setRowCount(0); // Clear current rows
        for (Student s : students) {
            tableModel.addRow(new Object[]{
                    s.getId(),
                    s.getRollNo(),
                    s.getName(),
                    s.getEmail(),
                    s.getDepartment(),
                    s.getYearLevel(),
                    String.format("%.2f", s.getGpa()),
                    s.getStatus()
            });
        }
    }

    /**
     * Refreshes the 3 top KPI summary metrics.
     */
    private void updateDashboardStats() {
        try {
            DatabaseHelper.DashboardStats stats = DatabaseHelper.getDashboardStats();
            lblTotalCount.setText(String.valueOf(stats.totalStudents));
            lblActiveCount.setText(String.valueOf(stats.activeStudents));
            lblAvgGpa.setText(String.format("%.2f", stats.averageGpa));
        } catch (SQLException ignored) {}
    }

    /**
     * Populates form inputs when a table row is clicked.
     */
    private void populateFormFromSelectedRow() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) return;

        // Retrieve the hidden ID from model column 0
        selectedStudentId = (int) tableModel.getValueAt(selectedRow, 0);

        txtRollNo.setText((String) tableModel.getValueAt(selectedRow, 1));
        txtName.setText((String) tableModel.getValueAt(selectedRow, 2));
        txtEmail.setText((String) tableModel.getValueAt(selectedRow, 3));
        cbDepartment.setSelectedItem(tableModel.getValueAt(selectedRow, 4));
        cbYear.setSelectedItem(tableModel.getValueAt(selectedRow, 5));
        txtGpa.setText((String) tableModel.getValueAt(selectedRow, 6));
        cbStatus.setSelectedItem(tableModel.getValueAt(selectedRow, 7));

        // Enable Update & Delete buttons, Disable Add button
        btnAdd.setEnabled(false);
        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);

        showStatus("Editing student ID: " + selectedStudentId, TEXT_MUTED);
    }

    /**
     * Clears all form fields and resets selection state.
     */
    private void clearForm() {
        selectedStudentId = -1;
        txtRollNo.setText("");
        txtName.setText("");
        txtEmail.setText("");
        txtGpa.setText("");
        cbDepartment.setSelectedIndex(0);
        cbYear.setSelectedIndex(0);
        cbStatus.setSelectedIndex(0);

        studentTable.clearSelection();
        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    /**
     * Validates form inputs before saving.
     */
    private boolean validateFormInputs() {
        String roll = txtRollNo.getText().trim();
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String gpaStr = txtGpa.getText().trim();

        if (roll.isEmpty() || name.isEmpty() || email.isEmpty() || gpaStr.isEmpty()) {
            showStatus("⚠ Please fill in all required fields marked with *", DANGER_COLOR);
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Input Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showStatus("⚠ Please enter a valid email address.", DANGER_COLOR);
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Input Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            double gpa = Double.parseDouble(gpaStr);
            if (gpa < 0.0 || gpa > 4.0) {
                showStatus("⚠ GPA must be between 0.00 and 4.00.", DANGER_COLOR);
                JOptionPane.showMessageDialog(this, "GPA must be between 0.00 and 4.00.", "Input Validation", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            showStatus("⚠ GPA must be a valid number.", DANGER_COLOR);
            JOptionPane.showMessageDialog(this, "Please enter a valid number for GPA.", "Input Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    /**
     * Displays a toast / banner message at the bottom.
     */
    private void showStatus(String message, Color color) {
        lblStatusToast.setText(message);
        lblStatusToast.setForeground(color);
    }

    // =========================================================================
    // UI HELPER STYLING METHODS
    // =========================================================================

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(TEXT_MAIN);
        return label;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        tf.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 4,8,4,8");
        return tf;
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 6,14,6,14");
    }

    /**
     * Custom Table Cell Renderer to draw clean, colored Status badges.
     */
    private static class StatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setOpaque(true);

            String status = value != null ? value.toString() : "";

            if (!isSelected) {
                switch (status) {
                    case "Active":
                        label.setBackground(new Color(209, 250, 229)); // Soft Green
                        label.setForeground(new Color(6, 95, 70));
                        break;
                    case "Graduated":
                        label.setBackground(new Color(224, 231, 255)); // Soft Blue
                        label.setForeground(new Color(55, 48, 163));
                        break;
                    case "On Leave":
                        label.setBackground(new Color(254, 243, 199)); // Soft Amber
                        label.setForeground(new Color(146, 64, 14));
                        break;
                    default:
                        label.setBackground(Color.WHITE);
                        label.setForeground(TEXT_MAIN);
                }
            }
            return label;
        }
    }

    // =========================================================================
    // MAIN ENTRY POINT
    // =========================================================================
    public static void main(String[] args) {
        // Initialize modern FlatLaf Look and Feel
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("ScrollBar.showButtons", false);
            UIManager.put("ScrollBar.width", 10);
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf theme: " + e.getMessage());
        }

        // Launch UI on Event Dispatch Thread (Swing best practice)
        SwingUtilities.invokeLater(() -> {
            StudentApp app = new StudentApp();
            app.setVisible(true);
        });
    }
}
