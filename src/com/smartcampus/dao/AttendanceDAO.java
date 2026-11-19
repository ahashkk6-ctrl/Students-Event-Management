package com.smartcampus.dao;

import com.smartcampus.model.Attendance;
import com.smartcampus.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Attendance and Feedback records.
 */
public class AttendanceDAO {

    public boolean isAttendanceMarked(int registrationId) {
        String sql = "SELECT COUNT(*) FROM attendance WHERE registration_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, registrationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO Error] Check attendance marked failed: " + e.getMessage());
        }
        return false;
    }

    public boolean recordAttendance(int registrationId, String status, Integer feedbackScore, String remarks) {
        String sql = "INSERT INTO attendance (registration_id, attendance_status, feedback_score, remarks) VALUES (?, ?, ?, ?) " +
                     "ON CONFLICT(registration_id) DO UPDATE SET " +
                     "attendance_status = excluded.attendance_status, " +
                     "feedback_score = excluded.feedback_score, " +
                     "remarks = excluded.remarks, " +
                     "marked_at = CURRENT_TIMESTAMP";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, registrationId);
            pstmt.setString(2, status);
            if (feedbackScore != null) {
                pstmt.setInt(3, feedbackScore);
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            pstmt.setString(4, remarks);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DAO Error] Failed to record attendance: " + e.getMessage());
            return false;
        }
    }

    public List<Attendance> getAttendanceByEvent(String eventId) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.attendance_id, a.registration_id, a.attendance_status, a.feedback_score, a.remarks, a.marked_at, " +
                     "s.student_id, s.name AS student_name, s.department, e.event_id, e.event_name " +
                     "FROM attendance a " +
                     "JOIN registrations r ON a.registration_id = r.registration_id " +
                     "JOIN students s ON r.student_id = s.student_id " +
                     "JOIN events e ON r.event_id = e.event_id " +
                     "WHERE e.event_id = ? " +
                     "ORDER BY s.student_id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, eventId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Attendance att = new Attendance();
                    att.setAttendanceId(rs.getInt("attendance_id"));
                    att.setRegistrationId(rs.getInt("registration_id"));
                    att.setAttendanceStatus(rs.getString("attendance_status"));
                    int score = rs.getInt("feedback_score");
                    att.setFeedbackScore(rs.wasNull() ? null : score);
                    att.setRemarks(rs.getString("remarks"));
                    att.setMarkedAt(rs.getString("marked_at"));
                    att.setStudentId(rs.getString("student_id"));
                    att.setStudentName(rs.getString("student_name"));
                    att.setDepartment(rs.getString("department"));
                    att.setEventId(rs.getString("event_id"));
                    att.setEventName(rs.getString("event_name"));
                    list.add(att);
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO Error] Failed to retrieve event attendance: " + e.getMessage());
        }
        return list;
    }
}
