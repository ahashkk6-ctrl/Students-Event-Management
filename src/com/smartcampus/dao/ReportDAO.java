package com.smartcampus.dao;

import com.smartcampus.model.EventReport;
import com.smartcampus.util.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Analytical Reports and Summaries.
 */
public class ReportDAO {

    public List<EventReport> getEventWiseReports() {
        List<EventReport> list = new ArrayList<>();
        String sql = "SELECT " +
                     "    e.event_id, " +
                     "    e.event_name, " +
                     "    e.event_type, " +
                     "    e.event_date, " +
                     "    e.venue, " +
                     "    e.max_participants, " +
                     "    COUNT(r.registration_id) AS total_registered, " +
                     "    SUM(CASE WHEN a.attendance_status = 'Present' THEN 1 ELSE 0 END) AS total_present, " +
                     "    SUM(CASE WHEN a.attendance_status = 'Absent' THEN 1 ELSE 0 END) AS total_absent, " +
                     "    ROUND( " +
                     "        CASE WHEN COUNT(r.registration_id) = 0 THEN 0.0 " +
                     "             ELSE (CAST(SUM(CASE WHEN a.attendance_status = 'Present' THEN 1 ELSE 0 END) AS REAL) / COUNT(r.registration_id)) * 100 " +
                     "        END, 2 " +
                     "    ) AS attendance_rate, " +
                     "    COALESCE(ROUND(AVG(a.feedback_score), 2), 0.0) AS avg_feedback " +
                     "FROM events e " +
                     "LEFT JOIN registrations r ON e.event_id = r.event_id AND r.status = 'Confirmed' " +
                     "LEFT JOIN attendance a ON r.registration_id = a.registration_id " +
                     "GROUP BY e.event_id, e.event_name, e.event_type, e.event_date, e.venue, e.max_participants " +
                     "ORDER BY e.event_date ASC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new EventReport(
                        rs.getString("event_id"),
                        rs.getString("event_name"),
                        rs.getString("event_type"),
                        rs.getString("event_date"),
                        rs.getString("venue"),
                        rs.getInt("max_participants"),
                        rs.getInt("total_registered"),
                        rs.getInt("total_present"),
                        rs.getInt("total_absent"),
                        rs.getDouble("attendance_rate"),
                        rs.getDouble("avg_feedback")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[DAO Error] Failed to generate event reports: " + e.getMessage());
        }
        return list;
    }

    public List<String[]> getDepartmentParticipationSummary() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT " +
                     "    s.department, " +
                     "    COUNT(DISTINCT s.student_id) AS total_students, " +
                     "    COUNT(r.registration_id) AS total_registrations, " +
                     "    SUM(CASE WHEN a.attendance_status = 'Present' THEN 1 ELSE 0 END) AS attended_events " +
                     "FROM students s " +
                     "LEFT JOIN registrations r ON s.student_id = r.student_id " +
                     "LEFT JOIN attendance a ON r.registration_id = a.registration_id " +
                     "GROUP BY s.department " +
                     "ORDER BY total_registrations DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{
                        rs.getString("department"),
                        String.valueOf(rs.getInt("total_students")),
                        String.valueOf(rs.getInt("total_registrations")),
                        String.valueOf(rs.getInt("attended_events"))
                });
            }
        } catch (SQLException e) {
            System.err.println("[DAO Error] Failed to retrieve department summary: " + e.getMessage());
        }
        return list;
    }
}
