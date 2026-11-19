package com.smartcampus;

import com.smartcampus.model.*;
import com.smartcampus.service.EventService;
import com.smartcampus.util.ConsoleHelper;
import com.smartcampus.util.DBConnection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Main Application Runner.
 * Provides interactive command-line interface for the Smart Campus Event System.
 */
public class Main {
    private static final EventService service = new EventService();

    public static void main(String[] args) {
        // Ensure database is initialized
        DBConnection.initializeDatabaseIfMissing();

        boolean running = true;
        while (running) {
            ConsoleHelper.printHeader("SMART CAMPUS EVENT MANAGEMENT & PARTICIPATION SYSTEM\n   Velalar College of Engineering and Technology | Dept of IT");
            System.out.println("1. Student Management");
            System.out.println("2. Event Management");
            System.out.println("3. Event Registration (with Duplicate Prevention & Capacity Check)");
            System.out.println("4. Attendance & Participation Status Management");
            System.out.println("5. Event-Wise Reports & Department Analytics");
            System.out.println("6. Trigger Python Data Science Visualization Suite");
            System.out.println("7. Launch Graphical User Interface (Swing Desktop App)");
            System.out.println("0. Exit Application");
            ConsoleHelper.printDivider();

            int choice = ConsoleHelper.readInt("Select an option", 0, 7);
            switch (choice) {
                case 1 -> handleStudentMenu();
                case 2 -> handleEventMenu();
                case 3 -> handleRegistrationMenu();
                case 4 -> handleAttendanceMenu();
                case 5 -> handleReportsMenu();
                case 6 -> runPythonVisualizations();
                case 7 -> launchGUI();
                case 0 -> {
                    ConsoleHelper.printSuccess("Exiting Smart Campus System. Thank you!");
                    running = false;
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    // Sub-menus
    // ------------------------------------------------------------------------
    private static void handleStudentMenu() {
        ConsoleHelper.printSection("STUDENT MANAGEMENT");
        System.out.println("1. Add New Student");
        System.out.println("2. List All Students");
        System.out.println("3. Search Student by ID");
        System.out.println("0. Back to Main Menu");

        int choice = ConsoleHelper.readInt("Choice", 0, 3);
        switch (choice) {
            case 1 -> {
                String id = ConsoleHelper.readString("Enter Student ID (e.g. 25IT010)");
                String name = ConsoleHelper.readString("Enter Full Name");
                String dept = ConsoleHelper.readString("Enter Department (e.g. Information Technology)");
                int year = ConsoleHelper.readInt("Enter Year of Study", 1, 4);
                String email = ConsoleHelper.readString("Enter Email");
                String phone = ConsoleHelper.readString("Enter Phone Number");

                Student s = new Student(id, name, dept, year, email, phone);
                if (service.registerNewStudent(s)) {
                    ConsoleHelper.printSuccess("Student '" + name + "' registered successfully!");
                } else {
                    ConsoleHelper.printError("Could not add student. Check if ID or Email already exists.");
                }
                ConsoleHelper.pause();
            }
            case 2 -> {
                List<Student> students = service.getAllStudents();
                ConsoleHelper.printSection("ALL ENROLLED STUDENTS (" + students.size() + ")");
                System.out.println(String.format("%-10s | %-20s | %-32s | %-7s | %-25s | %s",
                        "ID", "Name", "Department", "Year", "Email", "Phone"));
                ConsoleHelper.printDivider();
                for (Student s : students) {
                    System.out.println(s);
                }
                ConsoleHelper.pause();
            }
            case 3 -> {
                String id = ConsoleHelper.readString("Enter Student ID to search");
                Student s = service.getStudent(id);
                if (s != null) {
                    ConsoleHelper.printSuccess("Student Found:\n" + s);
                } else {
                    ConsoleHelper.printWarning("Student with ID '" + id + "' not found.");
                }
                ConsoleHelper.pause();
            }
        }
    }

    private static void handleEventMenu() {
        ConsoleHelper.printSection("EVENT MANAGEMENT");
        System.out.println("1. Add New Campus Event");
        System.out.println("2. List All Events");
        System.out.println("3. Search Event by ID");
        System.out.println("0. Back to Main Menu");

        int choice = ConsoleHelper.readInt("Choice", 0, 3);
        switch (choice) {
            case 1 -> {
                String id = ConsoleHelper.readString("Enter Event ID (e.g. EVT107)");
                String name = ConsoleHelper.readString("Enter Event Name");
                System.out.println("Event Types: 1. Technical, 2. Workshop, 3. Seminar, 4. Cultural");
                int typeChoice = ConsoleHelper.readInt("Select Type", 1, 4);
                String type = switch (typeChoice) {
                    case 1 -> "Technical";
                    case 2 -> "Workshop";
                    case 3 -> "Seminar";
                    default -> "Cultural";
                };
                String date = ConsoleHelper.readString("Enter Event Date (YYYY-MM-DD)");
                String venue = ConsoleHelper.readString("Enter Venue");
                int maxCap = ConsoleHelper.readInt("Enter Maximum Participant Capacity", 1, 500);
                String desc = ConsoleHelper.readString("Enter Brief Description");

                Event event = new Event(id, name, type, date, venue, maxCap, desc);
                if (service.createNewEvent(event)) {
                    ConsoleHelper.printSuccess("Event '" + name + "' added successfully!");
                } else {
                    ConsoleHelper.printError("Could not create event. Check if Event ID already exists.");
                }
                ConsoleHelper.pause();
            }
            case 2 -> {
                List<Event> events = service.getAllEvents();
                ConsoleHelper.printSection("ALL CAMPUS EVENTS (" + events.size() + ")");
                System.out.println(String.format("%-8s | %-32s | %-12s | %-10s | %-24s | %s",
                        "Event ID", "Event Name", "Type", "Date", "Venue", "Capacity"));
                ConsoleHelper.printDivider();
                for (Event e : events) {
                    System.out.println(e);
                }
                ConsoleHelper.pause();
            }
            case 3 -> {
                String id = ConsoleHelper.readString("Enter Event ID to search");
                Event e = service.getEvent(id);
                if (e != null) {
                    ConsoleHelper.printSuccess("Event Found:\n" + e + "\nDescription: " + e.getDescription());
                } else {
                    ConsoleHelper.printWarning("Event with ID '" + id + "' not found.");
                }
                ConsoleHelper.pause();
            }
        }
    }

    private static void handleRegistrationMenu() {
        ConsoleHelper.printSection("EVENT REGISTRATION (DUPLICATE PREVENTING)");
        System.out.println("1. Register Student for an Event");
        System.out.println("2. View Registered Participants for an Event");
        System.out.println("0. Back to Main Menu");

        int choice = ConsoleHelper.readInt("Choice", 0, 2);
        switch (choice) {
            case 1 -> {
                String studentId = ConsoleHelper.readString("Enter Student ID (e.g. 25IT001)");
                String eventId = ConsoleHelper.readString("Enter Event ID (e.g. EVT101)");
                String result = service.registerStudentForEvent(studentId, eventId);
                if (result.startsWith("SUCCESS")) {
                    ConsoleHelper.printSuccess(result);
                } else if (result.startsWith("DUPLICATE")) {
                    ConsoleHelper.printWarning("[PREVENTED DUPLICATE] " + result);
                } else {
                    ConsoleHelper.printError(result);
                }
                ConsoleHelper.pause();
            }
            case 2 -> {
                String eventId = ConsoleHelper.readString("Enter Event ID (e.g. EVT101)");
                Event event = service.getEvent(eventId);
                if (event == null) {
                    ConsoleHelper.printError("Event not found.");
                    ConsoleHelper.pause();
                    return;
                }
                List<Registration> regs = service.getEventRegistrations(eventId);
                ConsoleHelper.printSection("REGISTERED PARTICIPANTS FOR: " + event.getEventName() + " (" + regs.size() + "/" + event.getMaxParticipants() + ")");
                System.out.println(String.format("%-6s | %-10s | %-22s | %-30s | %-10s",
                        "Reg ID", "Student ID", "Student Name", "Department", "Status"));
                ConsoleHelper.printDivider();
                for (Registration r : regs) {
                    System.out.println(String.format("%-6d | %-10s | %-22s | %-30s | %-10s",
                            r.getRegistrationId(), r.getStudentId(), r.getStudentName(), r.getDepartment(), r.getStatus()));
                }
                ConsoleHelper.pause();
            }
        }
    }

    private static void handleAttendanceMenu() {
        ConsoleHelper.printSection("ATTENDANCE & PARTICIPATION STATUS");
        System.out.println("1. Mark / Update Student Attendance & Feedback");
        System.out.println("2. View Attendance Sheet for an Event");
        System.out.println("0. Back to Main Menu");

        int choice = ConsoleHelper.readInt("Choice", 0, 2);
        switch (choice) {
            case 1 -> {
                int regId = ConsoleHelper.readInt("Enter Registration ID", 1, 100000);
                System.out.println("Attendance Status: 1. Present, 2. Absent");
                int statusChoice = ConsoleHelper.readInt("Select Status", 1, 2);
                String status = (statusChoice == 1) ? "Present" : "Absent";

                Integer feedback = null;
                String remarks = "";
                if ("Present".equalsIgnoreCase(status)) {
                    feedback = ConsoleHelper.readInt("Enter Feedback Rating", 1, 5);
                    remarks = ConsoleHelper.readString("Enter Participant Feedback Remarks");
                } else {
                    remarks = ConsoleHelper.readString("Enter Reason for Absence");
                }

                String res = service.recordStudentAttendance(regId, status, feedback, remarks);
                if (res.startsWith("SUCCESS")) {
                    ConsoleHelper.printSuccess(res);
                } else {
                    ConsoleHelper.printError(res);
                }
                ConsoleHelper.pause();
            }
            case 2 -> {
                String eventId = ConsoleHelper.readString("Enter Event ID (e.g. EVT101)");
                List<Attendance> list = service.getEventAttendance(eventId);
                ConsoleHelper.printSection("ATTENDANCE RECORDS FOR EVENT: " + eventId + " (" + list.size() + " marked)");
                System.out.println(String.format("%-8s | %-10s | %-20s | %-8s | %-8s | %s",
                        "Att ID", "Student ID", "Student Name", "Status", "Rating", "Remarks"));
                ConsoleHelper.printDivider();
                for (Attendance a : list) {
                    System.out.println(String.format("%-8d | %-10s | %-20s | %-8s | %-8s | %s",
                            a.getAttendanceId(), a.getStudentId(), a.getStudentName(),
                            a.getAttendanceStatus(),
                            (a.getFeedbackScore() != null ? a.getFeedbackScore() + "/5" : "N/A"),
                            (a.getRemarks() != null ? a.getRemarks() : "")));
                }
                ConsoleHelper.pause();
            }
        }
    }

    private static void handleReportsMenu() {
        ConsoleHelper.printSection("REPORTS & ANALYTICS");
        System.out.println("1. Event-Wise Summary Report (Requirement 5)");
        System.out.println("2. Department-Wise Participation Distribution");
        System.out.println("0. Back to Main Menu");

        int choice = ConsoleHelper.readInt("Choice", 0, 2);
        switch (choice) {
            case 1 -> {
                List<EventReport> reports = service.generateEventSummaryReports();
                ConsoleHelper.printSection("EVENT-WISE COMPREHENSIVE PARTICIPATION REPORT");
                System.out.println(String.format("%-8s | %-30s | %-10s | %-10s | %-7s | %-7s | %-8s | %s",
                        "Event ID", "Event Name", "Type", "Registered", "Present", "Absent", "Att Rate", "Avg Rating"));
                ConsoleHelper.printDivider();
                for (EventReport r : reports) {
                    System.out.println(String.format("%-8s | %-30s | %-10s | %2d / %-5d | %-7d | %-7d | %5.1f%%  | %.2f / 5",
                            r.getEventId(), r.getEventName(), r.getEventType(),
                            r.getTotalRegistrations(), r.getMaxParticipants(),
                            r.getTotalPresent(), r.getTotalAbsent(),
                            r.getAttendanceRate(), r.getAvgFeedback()));
                }
                ConsoleHelper.pause();
            }
            case 2 -> {
                List<String[]> deptData = service.generateDepartmentSummary();
                ConsoleHelper.printSection("DEPARTMENT-WISE PARTICIPATION DISTRIBUTION");
                System.out.println(String.format("%-38s | %-14s | %-16s | %s",
                        "Department", "Total Students", "Registrations", "Attended Events"));
                ConsoleHelper.printDivider();
                for (String[] row : deptData) {
                    System.out.println(String.format("%-38s | %-14s | %-16s | %s",
                            row[0], row[1], row[2], row[3]));
                }
                ConsoleHelper.pause();
            }
        }
    }

    private static void runPythonVisualizations() {
        ConsoleHelper.printSection("LAUNCHING PYTHON DATA SCIENCE PIPELINE");
        System.out.println("[INFO] Triggering data_analysis/analysis.py to query SQLite, clean data, and render charts...");
        try {
            ProcessBuilder pb = new ProcessBuilder("python", "data_analysis/analysis.py");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("  [PYTHON] " + line);
                }
            }
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                ConsoleHelper.printSuccess("Python Data Science visualizations generated successfully in 'data_analysis/visualizations/'!");
            } else {
                ConsoleHelper.printError("Python script finished with exit code " + exitCode);
            }
        } catch (Exception e) {
            ConsoleHelper.printError("Failed to execute Python analysis: " + e.getMessage());
        }
        ConsoleHelper.pause();
    }

    private static void launchGUI() {
        ConsoleHelper.printSection("LAUNCHING SWING GRAPHICAL USER INTERFACE");
        System.out.println("[INFO] Opening Smart Campus Graphical Window...");
        try {
            javax.swing.SwingUtilities.invokeLater(() -> {
                com.smartcampus.gui.SmartCampusGUI gui = new com.smartcampus.gui.SmartCampusGUI();
                gui.setVisible(true);
            });
            ConsoleHelper.printSuccess("Desktop GUI Window launched successfully.");
        } catch (Exception e) {
            ConsoleHelper.printError("Failed to launch GUI: " + e.getMessage());
        }
        ConsoleHelper.pause();
    }
}
