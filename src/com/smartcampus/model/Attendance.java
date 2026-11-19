package com.smartcampus.model;

/**
 * Model class representing attendance and feedback status for a registered participant.
 */
public class Attendance {
    private int attendanceId;
    private int registrationId;
    private String studentId;
    private String studentName;
    private String department;
    private String eventId;
    private String eventName;
    private String attendanceStatus; // Present or Absent
    private Integer feedbackScore;    // 1 to 5 (null if absent)
    private String remarks;
    private String markedAt;

    public Attendance() {}

    public Attendance(int attendanceId, int registrationId, String attendanceStatus, Integer feedbackScore, String remarks, String markedAt) {
        this.attendanceId = attendanceId;
        this.registrationId = registrationId;
        this.attendanceStatus = attendanceStatus;
        this.feedbackScore = feedbackScore;
        this.remarks = remarks;
        this.markedAt = markedAt;
    }

    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
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

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(String attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public Integer getFeedbackScore() {
        return feedbackScore;
    }

    public void setFeedbackScore(Integer feedbackScore) {
        this.feedbackScore = feedbackScore;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getMarkedAt() {
        return markedAt;
    }

    public void setMarkedAt(String markedAt) {
        this.markedAt = markedAt;
    }

    @Override
    public String toString() {
        return String.format("%-4d | Reg: %-3d | %-10s | %-18s | %-8s | Rating: %s | %s",
                attendanceId, registrationId, studentId, (studentName != null ? studentName : ""),
                attendanceStatus, (feedbackScore != null ? feedbackScore + "/5" : "N/A"),
                (remarks != null ? remarks : ""));
    }
}
