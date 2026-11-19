package com.smartcampus.service;

import com.smartcampus.dao.*;
import com.smartcampus.model.*;

import java.util.List;

/**
 * Service Layer enforcing all core business rules and validations.
 */
public class EventService {
    private final StudentDAO studentDAO = new StudentDAO();
    private final EventDAO eventDAO = new EventDAO();
    private final RegistrationDAO registrationDAO = new RegistrationDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final ReportDAO reportDAO = new ReportDAO();

    // ------------------------------------------------------------------------
    // Student Management
    // ------------------------------------------------------------------------
    public boolean registerNewStudent(Student student) {
        if (studentDAO.studentExists(student.getStudentId())) {
            System.err.println("[VALIDATION ERROR] Student ID '" + student.getStudentId() + "' already exists in system.");
            return false;
        }
        return studentDAO.addStudent(student);
    }

    public List<Student> getAllStudents() {
        return studentDAO.getAllStudents();
    }

    public Student getStudent(String studentId) {
        return studentDAO.getStudentById(studentId);
    }

    // ------------------------------------------------------------------------
    // Event Management
    // ------------------------------------------------------------------------
    public boolean createNewEvent(Event event) {
        if (eventDAO.getEventById(event.getEventId()) != null) {
            System.err.println("[VALIDATION ERROR] Event ID '" + event.getEventId() + "' already exists.");
            return false;
        }
        if (event.getMaxParticipants() <= 0) {
            System.err.println("[VALIDATION ERROR] Maximum participant count must be greater than zero.");
            return false;
        }
        return eventDAO.addEvent(event);
    }

    public List<Event> getAllEvents() {
        return eventDAO.getAllEvents();
    }

    public Event getEvent(String eventId) {
        return eventDAO.getEventById(eventId);
    }

    // ------------------------------------------------------------------------
    // Registration Management (Enforcing Duplicate Prevention & Capacity)
    // ------------------------------------------------------------------------
    public String registerStudentForEvent(String studentId, String eventId) {
        // 1. Validate Student existence
        Student student = studentDAO.getStudentById(studentId);
        if (student == null) {
            return "FAILURE: Student with ID '" + studentId + "' not found.";
        }

        // 2. Validate Event existence
        Event event = eventDAO.getEventById(eventId);
        if (event == null) {
            return "FAILURE: Event with ID '" + eventId + "' not found.";
        }

        // 3. Prevent duplicate registration (Functional Requirement 3)
        if (registrationDAO.isAlreadyRegistered(studentId, eventId)) {
            return "DUPLICATE: Student '" + student.getName() + " (" + studentId + ")' is already registered for '" + event.getEventName() + "'.";
        }

        // 4. Verify maximum participant capacity limit
        int currentCount = eventDAO.getCurrentRegistrationCount(eventId);
        if (currentCount >= event.getMaxParticipants()) {
            return "FULL: Event '" + event.getEventName() + "' has reached its maximum capacity of " + event.getMaxParticipants() + " participants.";
        }

        // 5. Execute registration
        boolean success = registrationDAO.registerStudent(studentId, eventId);
        if (success) {
            return "SUCCESS: Successfully registered " + student.getName() + " for " + event.getEventName() + ".";
        } else {
            return "ERROR: Failed to save registration due to a database error.";
        }
    }

    public List<Registration> getEventRegistrations(String eventId) {
        return registrationDAO.getRegistrationsByEvent(eventId);
    }

    // ------------------------------------------------------------------------
    // Attendance Management
    // ------------------------------------------------------------------------
    public String recordStudentAttendance(int registrationId, String status, Integer feedbackScore, String remarks) {
        Registration reg = registrationDAO.getRegistrationById(registrationId);
        if (reg == null) {
            return "FAILURE: Registration ID #" + registrationId + " does not exist.";
        }

        if (!"Present".equalsIgnoreCase(status) && !"Absent".equalsIgnoreCase(status)) {
            return "FAILURE: Attendance status must be either 'Present' or 'Absent'.";
        }

        String normalizedStatus = "Present".equalsIgnoreCase(status) ? "Present" : "Absent";
        
        if ("Absent".equals(normalizedStatus)) {
            feedbackScore = null; // No feedback rating if absent
        } else if (feedbackScore != null && (feedbackScore < 1 || feedbackScore > 5)) {
            return "FAILURE: Feedback score must be between 1 and 5.";
        }

        boolean success = attendanceDAO.recordAttendance(registrationId, normalizedStatus, feedbackScore, remarks);
        if (success) {
            return "SUCCESS: Attendance recorded as '" + normalizedStatus + "' for Student ID: " + reg.getStudentId() + " (" + reg.getStudentName() + ").";
        } else {
            return "ERROR: Failed to record attendance.";
        }
    }

    public List<Attendance> getEventAttendance(String eventId) {
        return attendanceDAO.getAttendanceByEvent(eventId);
    }

    // ------------------------------------------------------------------------
    // Reports & Analytics
    // ------------------------------------------------------------------------
    public List<EventReport> generateEventSummaryReports() {
        return reportDAO.getEventWiseReports();
    }

    public List<String[]> generateDepartmentSummary() {
        return reportDAO.getDepartmentParticipationSummary();
    }
}
