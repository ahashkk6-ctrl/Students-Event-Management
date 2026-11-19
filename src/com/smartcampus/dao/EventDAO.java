package com.smartcampus.dao;

import com.smartcampus.model.Event;
import com.smartcampus.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Event entity.
 */
public class EventDAO {

    public boolean addEvent(Event event) {
        String sql = "INSERT INTO events (event_id, event_name, event_type, event_date, venue, max_participants, description) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, event.getEventId());
            pstmt.setString(2, event.getEventName());
            pstmt.setString(3, event.getEventType());
            pstmt.setString(4, event.getEventDate());
            pstmt.setString(5, event.getVenue());
            pstmt.setInt(6, event.getMaxParticipants());
            pstmt.setString(7, event.getDescription());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DAO Error] Failed to insert event: " + e.getMessage());
            return false;
        }
    }

    public List<Event> getAllEvents() {
        List<Event> list = new ArrayList<>();
        String sql = "SELECT event_id, event_name, event_type, event_date, venue, max_participants, description FROM events ORDER BY event_date ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Event(
                        rs.getString("event_id"),
                        rs.getString("event_name"),
                        rs.getString("event_type"),
                        rs.getString("event_date"),
                        rs.getString("venue"),
                        rs.getInt("max_participants"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[DAO Error] Failed to retrieve events: " + e.getMessage());
        }
        return list;
    }

    public Event getEventById(String eventId) {
        String sql = "SELECT event_id, event_name, event_type, event_date, venue, max_participants, description FROM events WHERE event_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, eventId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Event(
                            rs.getString("event_id"),
                            rs.getString("event_name"),
                            rs.getString("event_type"),
                            rs.getString("event_date"),
                            rs.getString("venue"),
                            rs.getInt("max_participants"),
                            rs.getString("description")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO Error] Failed to find event: " + e.getMessage());
        }
        return null;
    }

    public int getCurrentRegistrationCount(String eventId) {
        String sql = "SELECT COUNT(*) FROM registrations WHERE event_id = ? AND status = 'Confirmed'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, eventId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO Error] Failed to count registrations: " + e.getMessage());
        }
        return 0;
    }
}
