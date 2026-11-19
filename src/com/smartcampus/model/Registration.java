package com.smartcampus.model;

/**
 * Model class representing an event registration by a student.
 */
public class Registration {
    private int registrationId;
    private String studentId;
    private String eventId;
    private String registrationDate;
    private String status;
    
    // Auxiliary display properties
    private String studentName;
    private String department;
    private String eventName;

    public Registration() {}

    public Registration(int registrationId, String studentId, String eventId, String registrationDate, String status) {
        this.registrationId = registrationId;
        this.studentId = studentId;
        this.eventId = eventId;
        this.registrationDate = registrationDate;
        this.status = status;
    }

    public int getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(int registrationId) {
        this.registrationId = registrationId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    @Override
    public String toString() {
        return String.format("%-4d | %-10s | %-20s | %-8s | %-26s | %-10s",
                registrationId, studentId, (studentName != null ? studentName : ""),
                eventId, (eventName != null ? eventName : ""), status);
    }
}
