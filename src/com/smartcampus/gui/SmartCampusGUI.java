package com.smartcampus.gui;

import com.smartcampus.model.Attendance;
import com.smartcampus.model.EventReport;
import com.smartcampus.model.Registration;
import com.smartcampus.model.Student;
import com.smartcampus.model.Event; // Explicit import
import com.smartcampus.service.EventService;
import com.smartcampus.util.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import java.util.List;

/**
 * Modern Java Swing Graphical User Interface for Smart Campus Event Management System.
 * Fulfills all functional requirements with rich validation and visual feedback.
 */
public class SmartCampusGUI extends JFrame {
    private final EventService service = new EventService();

    // Table Models
    private DefaultTableModel studentTableModel;
    private DefaultTableModel eventTableModel;
    private DefaultTableModel registrationTableModel;
    private DefaultTableModel attendanceTableModel;
    private DefaultTableModel reportTableModel;
    private DefaultTableModel deptTableModel;

    // Dropdowns for dynamic binding
    private JComboBox<String> regStudentCombo;
    private JComboBox<String> regEventCombo;
    private JComboBox<String> filterEventCombo;
    private JComboBox<String> attEventCombo;
    private JComboBox<String> attRegCombo;

    public SmartCampusGUI() {
        setTitle("Smart Campus Event Management & Student Participation System | VCET IT");
        setSize(1100, 750);
        setMinimumSize(new Dimension(950, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Ensure database tables exist
        DBConnection.initializeDatabaseIfMissing();

        initComponents();
        loadAllData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // 1. Top Header Banner
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(26, 54, 93)); // Dark Navy
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("VELALAR COLLEGE OF ENGINEERING AND TECHNOLOGY");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Smart Campus Event Management & Student Participation Analysis System (Scenario 6)");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(226, 232, 240));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Main Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabbedPane.addTab("  🎓 Students  ", createStudentPanel());
        tabbedPane.addTab("  📅 Events  ", createEventPanel());
        tabbedPane.addTab("  📝 Registration  ", createRegistrationPanel());
        tabbedPane.addTab("  ✅ Attendance  ", createAttendancePanel());
        tabbedPane.addTab("  📊 Reports & Visuals  ", createReportsPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // 3. Status Bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        JLabel statusLabel = new JLabel(" Ready | Connected to SQLite Database: smart_campus.db");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusBar.add(statusLabel);
        add(statusBar, BorderLayout.SOUTH);
    }

    // ========================================================================
    // TAB 1: STUDENT MANAGEMENT
    // ========================================================================
    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Left: Add Student Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)), " Register New Student "));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JTextField txtId = new JTextField(12);
        JTextField txtName = new JTextField(12);
        String[] depts = {"Information Technology", "Computer Science", "Artificial Intelligence & Data Science",
                "Electronics and Communication", "Mechanical Engineering", "Civil Engineering"};
        JComboBox<String> cmbDept = new JComboBox<>(depts);
        JComboBox<Integer> cmbYear = new JComboBox<>(new Integer[]{1, 2, 3, 4});
        JTextField txtEmail = new JTextField(12);
        JTextField txtPhone = new JTextField(12);

        int row = 0;
        addFormRow(formPanel, gbc, row++, "Student ID:", txtId);
        addFormRow(formPanel, gbc, row++, "Full Name:", txtName);
        addFormRow(formPanel, gbc, row++, "Department:", cmbDept);
        addFormRow(formPanel, gbc, row++, "Year of Study:", cmbYear);
        addFormRow(formPanel, gbc, row++, "Email:", txtEmail);
        addFormRow(formPanel, gbc, row++, "Phone:", txtPhone);

        JButton btnAdd = new JButton("Save Student");
        btnAdd.setBackground(new Color(40, 167, 69));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);

        JButton btnReset = new JButton("Reset");
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(btnReset);
        btnPanel.add(btnAdd);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        btnAdd.addActionListener(e -> {
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();
            String dept = (String) cmbDept.getSelectedItem();
            int year = (Integer) cmbYear.getSelectedItem();
            String email = txtEmail.getText().trim();
            String phone = txtPhone.getText().trim();

            if (id.isEmpty() || name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Student s = new Student(id, name, dept, year, email, phone);
            if (service.registerNewStudent(s)) {
                JOptionPane.showMessageDialog(this, "Student registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                txtId.setText("");
                txtName.setText("");
                txtEmail.setText("");
                txtPhone.setText("");
                loadStudentData();
                refreshStudentDropdown();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add student. Check if Student ID or Email already exists.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnReset.addActionListener(e -> {
            txtId.setText("");
            txtName.setText("");
            txtEmail.setText("");
            txtPhone.setText("");
        });

        // Right: Students Table
        String[] cols = {"Student ID", "Full Name", "Department", "Year", "Email", "Phone"};
        studentTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(studentTableModel);
        styleTable(table);

        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setBorder(BorderFactory.createTitledBorder(" Enrolled Students "));
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnRefresh = new JButton("🔄 Refresh List");
        btnRefresh.addActionListener(e -> loadStudentData());
        JPanel tblBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tblBottom.add(btnRefresh);
        tablePanel.add(tblBottom, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formPanel, tablePanel);
        splitPane.setDividerLocation(360);
        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    // ========================================================================
    // TAB 2: EVENT MANAGEMENT
    // ========================================================================
    private JPanel createEventPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(" Create Campus Event "));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtId = new JTextField(12);
        JTextField txtName = new JTextField(12);
        JComboBox<String> cmbType = new JComboBox<>(new String[]{"Technical", "Workshop", "Seminar", "Cultural"});
        JTextField txtDate = new JTextField("2026-12-01", 12);
        JTextField txtVenue = new JTextField(12);
        JSpinner spinCapacity = new JSpinner(new SpinnerNumberModel(20, 1, 500, 5));
        JTextArea txtDesc = new JTextArea(3, 12);
        txtDesc.setLineWrap(true);
        txtDesc.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        int row = 0;
        addFormRow(formPanel, gbc, row++, "Event ID:", txtId);
        addFormRow(formPanel, gbc, row++, "Event Name:", txtName);
        addFormRow(formPanel, gbc, row++, "Category:", cmbType);
        addFormRow(formPanel, gbc, row++, "Date (YYYY-MM-DD):", txtDate);
        addFormRow(formPanel, gbc, row++, "Venue:", txtVenue);
        addFormRow(formPanel, gbc, row++, "Max Capacity:", spinCapacity);
        addFormRow(formPanel, gbc, row++, "Description:", new JScrollPane(txtDesc));

        JButton btnAdd = new JButton("Create Event");
        btnAdd.setBackground(new Color(40, 167, 69));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);

        JButton btnReset = new JButton("Reset");
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(btnReset);
        btnPanel.add(btnAdd);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        btnAdd.addActionListener(e -> {
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();
            String type = (String) cmbType.getSelectedItem();
            String date = txtDate.getText().trim();
            String venue = txtVenue.getText().trim();
            int cap = (Integer) spinCapacity.getValue();
            String desc = txtDesc.getText().trim();

            if (id.isEmpty() || name.isEmpty() || date.isEmpty() || venue.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all mandatory fields!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Event ev = new Event(id, name, type, date, venue, cap, desc);
            if (service.createNewEvent(ev)) {
                JOptionPane.showMessageDialog(this, "Event created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                txtId.setText("");
                txtName.setText("");
                txtVenue.setText("");
                txtDesc.setText("");
                loadEventData();
                refreshEventDropdowns();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create event. Event ID may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnReset.addActionListener(e -> {
            txtId.setText("");
            txtName.setText("");
            txtVenue.setText("");
            txtDesc.setText("");
        });

        // Right: Event Table
        String[] cols = {"Event ID", "Name", "Type", "Date", "Venue", "Max Capacity"};
        eventTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(eventTableModel);
        styleTable(table);

        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setBorder(BorderFactory.createTitledBorder(" Scheduled Campus Events "));
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnRefresh = new JButton("🔄 Refresh List");
        btnRefresh.addActionListener(e -> loadEventData());
        JPanel tblBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tblBottom.add(btnRefresh);
        tablePanel.add(tblBottom, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formPanel, tablePanel);
        splitPane.setDividerLocation(360);
        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    // ========================================================================
    // TAB 3: REGISTRATION & DUPLICATE PREVENTING
    // ========================================================================
    private JPanel createRegistrationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Action Bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topBar.setBorder(BorderFactory.createTitledBorder(" Enroll Student for Event (Duplicate Protected) "));

        regStudentCombo = new JComboBox<>();
        regEventCombo = new JComboBox<>();
        regStudentCombo.setPreferredSize(new Dimension(280, 30));
        regEventCombo.setPreferredSize(new Dimension(300, 30));

        JButton btnRegister = new JButton("Register Student");
        btnRegister.setBackground(new Color(0, 123, 255));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRegister.setFocusPainted(false);

        topBar.add(new JLabel("Student:"));
        topBar.add(regStudentCombo);
        topBar.add(new JLabel("Event:"));
        topBar.add(regEventCombo);
        topBar.add(btnRegister);

        btnRegister.addActionListener(e -> {
            String stuItem = (String) regStudentCombo.getSelectedItem();
            String evtItem = (String) regEventCombo.getSelectedItem();
            if (stuItem == null || evtItem == null) return;

            String stuId = stuItem.split(" - ")[0].trim();
            String evtId = evtItem.split(" - ")[0].trim();

            String result = service.registerStudentForEvent(stuId, evtId);
            if (result.startsWith("SUCCESS")) {
                JOptionPane.showMessageDialog(this, result, "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
                loadRegistrationData();
                loadReportData();
            } else if (result.startsWith("DUPLICATE")) {
                JOptionPane.showMessageDialog(this, result, "Duplicate Registration Prevented", JOptionPane.WARNING_MESSAGE);
            } else if (result.startsWith("FULL")) {
                JOptionPane.showMessageDialog(this, result, "Event Capacity Full", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, result, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Center: Registered Participants Table
        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setBorder(BorderFactory.createTitledBorder(" Registered Participants for Selected Event "));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterEventCombo = new JComboBox<>();
        filterEventCombo.setPreferredSize(new Dimension(320, 28));
        filterPanel.add(new JLabel("View Participants for:"));
        filterPanel.add(filterEventCombo);

        filterEventCombo.addActionListener(e -> loadRegistrationData());

        tablePanel.add(filterPanel, BorderLayout.NORTH);

        String[] cols = {"Reg ID", "Student ID", "Student Name", "Department", "Registration Date", "Status"};
        registrationTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(registrationTableModel);
        styleTable(table);
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);
        return panel;
    }

    // ========================================================================
    // TAB 4: ATTENDANCE & FEEDBACK
    // ========================================================================
    private JPanel createAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder(" Mark Attendance & Participant Feedback "));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        attEventCombo = new JComboBox<>();
        attRegCombo = new JComboBox<>();
        attEventCombo.setPreferredSize(new Dimension(320, 28));
        attRegCombo.setPreferredSize(new Dimension(320, 28));

        JRadioButton radPresent = new JRadioButton("Present", true);
        JRadioButton radAbsent = new JRadioButton("Absent");
        ButtonGroup bg = new ButtonGroup();
        bg.add(radPresent);
        bg.add(radAbsent);
        JPanel radPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        radPanel.add(radPresent);
        radPanel.add(radAbsent);

        JComboBox<String> cmbRating = new JComboBox<>(new String[]{"5 - Outstanding", "4 - Very Good", "3 - Good", "2 - Fair", "1 - Poor"});
        JTextField txtRemarks = new JTextField("Great hands-on learning experience", 25);

        radAbsent.addActionListener(e -> {
            cmbRating.setEnabled(false);
            txtRemarks.setText("Absent / Sick leave");
        });
        radPresent.addActionListener(e -> {
            cmbRating.setEnabled(true);
            txtRemarks.setText("Active participant");
        });

        int row = 0;
        addFormRow(topPanel, gbc, row++, "Select Event:", attEventCombo);
        addFormRow(topPanel, gbc, row++, "Select Participant:", attRegCombo);
        addFormRow(topPanel, gbc, row++, "Attendance Status:", radPanel);
        addFormRow(topPanel, gbc, row++, "Feedback Score (1-5):", cmbRating);
        addFormRow(topPanel, gbc, row++, "Remarks:", txtRemarks);

        JButton btnSubmit = new JButton("Save Attendance Record");
        btnSubmit.setBackground(new Color(40, 167, 69));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSubmit.setFocusPainted(false);

        gbc.gridx = 1; gbc.gridy = row;
        topPanel.add(btnSubmit, gbc);

        attEventCombo.addActionListener(e -> {
            refreshAttendanceRegCombo();
            loadAttendanceTable();
        });

        btnSubmit.addActionListener(e -> {
            String regItem = (String) attRegCombo.getSelectedItem();
            if (regItem == null || regItem.isEmpty() || !regItem.contains("#")) {
                JOptionPane.showMessageDialog(this, "Please select a registered participant!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int regId = Integer.parseInt(regItem.split("#")[1].split(" - ")[0].trim());
            String status = radPresent.isSelected() ? "Present" : "Absent";
            Integer rating = radPresent.isSelected() ? (5 - cmbRating.getSelectedIndex()) : null;
            String remarks = txtRemarks.getText().trim();

            String res = service.recordStudentAttendance(regId, status, rating, remarks);
            if (res.startsWith("SUCCESS")) {
                JOptionPane.showMessageDialog(this, res, "Attendance Saved", JOptionPane.INFORMATION_MESSAGE);
                loadAttendanceTable();
                loadReportData();
            } else {
                JOptionPane.showMessageDialog(this, res, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Bottom Attendance Table
        String[] cols = {"Att ID", "Reg ID", "Student ID", "Student Name", "Status", "Rating", "Remarks", "Marked At"};
        attendanceTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(attendanceTableModel);
        styleTable(table);

        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setBorder(BorderFactory.createTitledBorder(" Verified Attendance Sheet "));
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);
        return panel;
    }

    // ========================================================================
    // TAB 5: REPORTS & VISUALIZATIONS
    // ========================================================================
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Action Bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        JButton btnRunPython = new JButton("⚡ Run Python Data Science Pipeline");
        btnRunPython.setBackground(new Color(111, 66, 193));
        btnRunPython.setForeground(Color.WHITE);
        btnRunPython.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRunPython.setFocusPainted(false);

        JButton btnOpenGallery = new JButton("🖼️ Open Generated Visualization Charts");
        btnOpenGallery.setBackground(new Color(23, 162, 184));
        btnOpenGallery.setForeground(Color.WHITE);
        btnOpenGallery.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnOpenGallery.setFocusPainted(false);

        topBar.add(btnRunPython);
        topBar.add(btnOpenGallery);

        btnRunPython.addActionListener(e -> {
            try {
                ProcessBuilder pb = new ProcessBuilder("python", "data_analysis/analysis.py");
                pb.inheritIO();
                Process p = pb.start();
                int code = p.waitFor();
                if (code == 0) {
                    JOptionPane.showMessageDialog(this,
                            "Python analysis script executed successfully!\nVisualizations updated in 'data_analysis/visualizations/'.",
                            "Data Science Pipeline Finished", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Python script returned error code: " + code, "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to run Python: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnOpenGallery.addActionListener(e -> openVisualizationsDialog());

        // Split view: Event Report (Top) and Department Report (Bottom)
        String[] repCols = {"Event ID", "Event Name", "Type", "Capacity", "Registered", "Present", "Absent", "Attendance Rate %", "Avg Rating (1-5)"};
        reportTableModel = new DefaultTableModel(repCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable reportTable = new JTable(reportTableModel);
        styleTable(reportTable);
        JPanel repPanel = new JPanel(new BorderLayout(5, 5));
        repPanel.setBorder(BorderFactory.createTitledBorder(" Event-Wise Participation & Attendance Performance "));
        repPanel.add(new JScrollPane(reportTable), BorderLayout.CENTER);

        String[] deptCols = {"Department", "Total Students", "Total Registrations", "Attended Events"};
        deptTableModel = new DefaultTableModel(deptCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable deptTable = new JTable(deptTableModel);
        styleTable(deptTable);
        JPanel deptPanel = new JPanel(new BorderLayout(5, 5));
        deptPanel.setBorder(BorderFactory.createTitledBorder(" Department-Wise Participation Distribution "));
        deptPanel.add(new JScrollPane(deptTable), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, repPanel, deptPanel);
        split.setDividerLocation(260);

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    private void openVisualizationsDialog() {
        JDialog dialog = new JDialog(this, "Campus Event Visualizations Gallery (Python Matplotlib & Seaborn)", true);
        dialog.setSize(950, 700);
        dialog.setLocationRelativeTo(this);

        JTabbedPane chartTabs = new JTabbedPane();
        String[][] charts = {
                {"Department Participation", "data_analysis/visualizations/01_department_participation_barchart.png"},
                {"Capacity vs Attendance", "data_analysis/visualizations/02_event_attendance_comparison.png"},
                {"Event Type Share", "data_analysis/visualizations/03_event_type_distribution_piechart.png"},
                {"Year of Study", "data_analysis/visualizations/04_year_wise_participation_countplot.png"},
                {"Feedback Ratings", "data_analysis/visualizations/05_feedback_rating_boxplot.png"},
                {"Correlation Matrix", "data_analysis/visualizations/06_participation_correlation_heatmap.png"}
        };

        for (String[] chart : charts) {
            File imgFile = new File(chart[1]);
            if (imgFile.exists()) {
                ImageIcon icon = new ImageIcon(chart[1]);
                Image scaled = icon.getImage().getScaledInstance(880, 560, Image.SCALE_SMOOTH);
                JLabel lbl = new JLabel(new ImageIcon(scaled));
                JScrollPane scroll = new JScrollPane(lbl);
                chartTabs.addTab(chart[0], scroll);
            } else {
                JLabel lbl = new JLabel("Chart not found at: " + chart[1] + ". Please run 'Run Python Pipeline' first.");
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                chartTabs.addTab(chart[0], lbl);
            }
        }
        dialog.add(chartTabs);
        dialog.setVisible(true);
    }

    // ========================================================================
    // HELPER DATA LOADERS
    // ========================================================================
    private void loadAllData() {
        loadStudentData();
        loadEventData();
        refreshStudentDropdown();
        refreshEventDropdowns();
        loadRegistrationData();
        loadAttendanceTable();
        loadReportData();
    }

    private void loadStudentData() {
        studentTableModel.setRowCount(0);
        List<Student> students = service.getAllStudents();
        for (Student s : students) {
            studentTableModel.addRow(new Object[]{
                    s.getStudentId(), s.getName(), s.getDepartment(), s.getYearOfStudy(), s.getEmail(), s.getPhone()
            });
        }
    }

    private void loadEventData() {
        eventTableModel.setRowCount(0);
        List<Event> events = service.getAllEvents();
        for (Event e : events) {
            eventTableModel.addRow(new Object[]{
                    e.getEventId(), e.getEventName(), e.getEventType(), e.getEventDate(), e.getVenue(), e.getMaxParticipants()
            });
        }
    }

    private void refreshStudentDropdown() {
        if (regStudentCombo == null) return;
        regStudentCombo.removeAllItems();
        for (Student s : service.getAllStudents()) {
            regStudentCombo.addItem(s.getStudentId() + " - " + s.getName() + " (" + s.getDepartment() + ")");
        }
    }

    private void refreshEventDropdowns() {
        if (regEventCombo == null || filterEventCombo == null || attEventCombo == null) return;
        regEventCombo.removeAllItems();
        filterEventCombo.removeAllItems();
        attEventCombo.removeAllItems();

        for (Event ev : service.getAllEvents()) {
            String item = ev.getEventId() + " - " + ev.getEventName();
            regEventCombo.addItem(item);
            filterEventCombo.addItem(item);
            attEventCombo.addItem(item);
        }
        refreshAttendanceRegCombo();
    }

    private void loadRegistrationData() {
        if (filterEventCombo == null || filterEventCombo.getSelectedItem() == null) return;
        registrationTableModel.setRowCount(0);
        String selected = (String) filterEventCombo.getSelectedItem();
        String eventId = selected.split(" - ")[0].trim();

        List<Registration> regs = service.getEventRegistrations(eventId);
        for (Registration r : regs) {
            registrationTableModel.addRow(new Object[]{
                    r.getRegistrationId(), r.getStudentId(), r.getStudentName(), r.getDepartment(), r.getRegistrationDate(), r.getStatus()
            });
        }
    }

    private void refreshAttendanceRegCombo() {
        if (attEventCombo == null || attRegCombo == null || attEventCombo.getSelectedItem() == null) return;
        attRegCombo.removeAllItems();
        String selected = (String) attEventCombo.getSelectedItem();
        String eventId = selected.split(" - ")[0].trim();

        List<Registration> regs = service.getEventRegistrations(eventId);
        for (Registration r : regs) {
            attRegCombo.addItem("#" + r.getRegistrationId() + " - " + r.getStudentId() + ": " + r.getStudentName());
        }
    }

    private void loadAttendanceTable() {
        if (attEventCombo == null || attEventCombo.getSelectedItem() == null) return;
        attendanceTableModel.setRowCount(0);
        String selected = (String) attEventCombo.getSelectedItem();
        String eventId = selected.split(" - ")[0].trim();

        List<Attendance> list = service.getEventAttendance(eventId);
        for (Attendance a : list) {
            attendanceTableModel.addRow(new Object[]{
                    a.getAttendanceId(), a.getRegistrationId(), a.getStudentId(), a.getStudentName(),
                    a.getAttendanceStatus(),
                    (a.getFeedbackScore() != null ? a.getFeedbackScore() + " / 5" : "N/A"),
                    a.getRemarks(), a.getMarkedAt()
            });
        }
    }

    private void loadReportData() {
        if (reportTableModel == null || deptTableModel == null) return;
        reportTableModel.setRowCount(0);
        List<EventReport> reports = service.generateEventSummaryReports();
        for (EventReport r : reports) {
            reportTableModel.addRow(new Object[]{
                    r.getEventId(), r.getEventName(), r.getEventType(), r.getMaxParticipants(),
                    r.getTotalRegistrations(), r.getTotalPresent(), r.getTotalAbsent(),
                    String.format("%.1f%%", r.getAttendanceRate()),
                    String.format("%.2f", r.getAvgFeedback())
            });
        }

        deptTableModel.setRowCount(0);
        List<String[]> deptData = service.generateDepartmentSummary();
        for (String[] row : deptData) {
            deptTableModel.addRow(row);
        }
    }

    private void addFormRow(JPanel p, GridBagConstraints gbc, int y, String label, Component comp) {
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 1;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        p.add(lbl, gbc);
        gbc.gridx = 1;
        p.add(comp, gbc);
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(240, 243, 246));
        table.setSelectionBackground(new Color(186, 215, 248));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        if (table.getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setPreferredWidth(70);
            table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            SmartCampusGUI gui = new SmartCampusGUI();
            gui.setVisible(true);
        });
    }
}
