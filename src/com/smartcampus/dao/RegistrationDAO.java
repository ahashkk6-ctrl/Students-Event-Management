package com.smartcampus.dao;

import com.smartcampus.model.Registration;
import com.smartcampus.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Event Registrations.
 * Enforces duplicate checks and associates students with events.
 */
public class RegistrationDAO {

    public boolean isAlreadyRegistered(String studentId, String eventId) {
        String sql = "SELECT COUNT(*) FROM registrations WHERE student_id = ? AND event_id = ? AND status = 'Confirmed'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, eventId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO Error] Duplicate check failed: " + e.getMessage());
        }
        return false;
    }

    public boolean registerStudent(String studentId, String eventId) {
        String sql = "INSERT INTO registrations (student_id, event_id, status) VALUES (?, ?, 'Confirmed')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, eventId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DAO Error] Registration insert failed: " + e.getMessage());
            return false;
        }
    }

    public List<Registration> getRegistrationsByEvent(String eventId) {
        List<Registration> list = new ArrayList<>();
        String sql = "SELECT r.registration_id, r.student_id, r.event_id, r.registration_date, r.status, " +
                     "s.name AS student_name, s.department, e.event_name " +
                     "FROM registrations r " +
                     "JOIN students s ON r.student_id = s.student_id " +
                     "JOIN events e ON r.event_id = e.event_id " +
                     "WHERE r.event_id = ? " +
                     "ORDER BY r.registration_date ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, eventId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Registration reg = new Registration(
                            rs.getInt("registration_id"),
                            rs.getString("student_id"),
                            rs.getString("event_id"),
                            rs.getString("registration_date"),
                            rs.getString("status")
                    );
                    reg.setStudentName(rs.getString("student_name"));
                    reg.setDepartment(rs.getString("department"));
                    reg.setEventName(rs.getString("event_name"));
                    list.add(reg);
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO Error] Failed to list event registrations: " + e.getMessage());
        }
        return list;
    }

    public Registration getRegistrationById(int registrationId) {
        String sql = "SELECT r.registration_id, r.student_id, r.event_id, r.registration_date, r.status, " +
                     "s.name AS student_name, s.department, e.event_name " +
                     "FROM registrations r " +
                     "JOIN students s ON r.student_id = s.student_id " +
                     "JOIN events e ON r.event_id = e.event_id " +
                     "WHERE r.registration_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, registrationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Registration reg = new Registration(
                            rs.getInt("registration_id"),
                            rs.getString("student_id"),
                            rs.getString("event_id"),
                            rs.getString("registration_date"),
                            rs.getString("status")
                    );
                    reg.setStudentName(rs.getString("student_name"));
                    reg.setDepartment(rs.getString("department"));
                    reg.setEventName(rs.getString("event_name"));
                    return reg;
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO Error] Failed to retrieve registration: " + e.getMessage());
        }
        return null;
    }
}
