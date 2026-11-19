package com.smartcampus.dao;

import com.smartcampus.model.Student;
import com.smartcampus.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Student entity.
 */
public class StudentDAO {

    public boolean addStudent(Student student) {
        String sql = "INSERT INTO students (student_id, name, department, year_of_study, email, phone) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getStudentId());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getDepartment());
            pstmt.setInt(4, student.getYearOfStudy());
            pstmt.setString(5, student.getEmail());
            pstmt.setString(6, student.getPhone());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DAO Error] Failed to insert student: " + e.getMessage());
            return false;
        }
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT student_id, name, department, year_of_study, email, phone FROM students ORDER BY student_id ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Student(
                        rs.getString("student_id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getInt("year_of_study"),
                        rs.getString("email"),
                        rs.getString("phone")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[DAO Error] Failed to retrieve students: " + e.getMessage());
        }
        return list;
    }

    public Student getStudentById(String studentId) {
        String sql = "SELECT student_id, name, department, year_of_study, email, phone FROM students WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                            rs.getString("student_id"),
                            rs.getString("name"),
                            rs.getString("department"),
                            rs.getInt("year_of_study"),
                            rs.getString("email"),
                            rs.getString("phone")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO Error] Failed to find student: " + e.getMessage());
        }
        return null;
    }

    public boolean studentExists(String studentId) {
        return getStudentById(studentId) != null;
    }
}
