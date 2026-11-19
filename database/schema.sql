-- ============================================================================
-- SMART CAMPUS EVENT MANAGEMENT AND STUDENT PARTICIPATION ANALYSIS SYSTEM
-- Institution: Velalar College of Engineering and Technology (Autonomous)
-- Department : Information Technology
-- Course     : 25ITT31 - Database Management Systems
-- Scenario 6 : Smart Campus Event Management & Student Participation Analysis
-- Database Dialect: SQLite / Standard ANSI SQL compatible
-- ============================================================================

-- Enable Foreign Key constraints enforcement
PRAGMA foreign_keys = ON;

-- Drop tables in reverse order of dependencies to avoid constraint violations
DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS registrations;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS students;

-- ----------------------------------------------------------------------------
-- Table 1: students
-- Entity representing enrolled college students participating in campus events.
-- Normalization: 3NF / BCNF compliant.
-- ----------------------------------------------------------------------------
CREATE TABLE students (
    student_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(50) NOT NULL,
    year_of_study INTEGER NOT NULL CHECK (year_of_study BETWEEN 1 AND 4),
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- Table 2: events
-- Entity representing campus activities (Workshops, Seminars, Technical, Cultural).
-- Normalization: 3NF / BCNF compliant.
-- ----------------------------------------------------------------------------
CREATE TABLE events (
    event_id VARCHAR(20) PRIMARY KEY,
    event_name VARCHAR(150) NOT NULL,
    event_type VARCHAR(50) NOT NULL CHECK (event_type IN ('Technical', 'Workshop', 'Seminar', 'Cultural')),
    event_date DATE NOT NULL,
    venue VARCHAR(100) NOT NULL,
    max_participants INTEGER NOT NULL CHECK (max_participants > 0),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- Table 3: registrations
-- Associative entity capturing student registrations for specific events.
-- Prevents duplicate registrations via UNIQUE(student_id, event_id).
-- ----------------------------------------------------------------------------
CREATE TABLE registrations (
    registration_id INTEGER PRIMARY KEY AUTOINCREMENT,
    student_id VARCHAR(20) NOT NULL,
    event_id VARCHAR(20) NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'Confirmed' CHECK (status IN ('Confirmed', 'Cancelled', 'Waitlisted')),
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT uq_student_event UNIQUE (student_id, event_id)
);

-- ----------------------------------------------------------------------------
-- Table 4: attendance
-- Entity tracking actual participation, attendance marking, and student feedback.
-- Normalization: 3NF / BCNF compliant.
-- ----------------------------------------------------------------------------
CREATE TABLE attendance (
    attendance_id INTEGER PRIMARY KEY AUTOINCREMENT,
    registration_id INTEGER NOT NULL UNIQUE,
    attendance_status VARCHAR(20) NOT NULL CHECK (attendance_status IN ('Present', 'Absent')),
    feedback_score INTEGER CHECK (feedback_score BETWEEN 1 AND 5),
    remarks TEXT,
    marked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (registration_id) REFERENCES registrations(registration_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------------------------------------------------------
-- Indexes for Query Optimization (DBMS Unit 3)
-- ----------------------------------------------------------------------------
CREATE INDEX idx_student_department ON students(department);
CREATE INDEX idx_student_year ON students(year_of_study);
CREATE INDEX idx_event_date ON events(event_date);
CREATE INDEX idx_event_type ON events(event_type);
CREATE INDEX idx_registration_event ON registrations(event_id);
CREATE INDEX idx_registration_student ON registrations(student_id);
CREATE INDEX idx_attendance_status ON attendance(attendance_status);

-- ----------------------------------------------------------------------------
-- View: vw_event_participation_summary
-- Aggregated view for quick reporting and dashboards
-- ----------------------------------------------------------------------------
DROP VIEW IF EXISTS vw_event_participation_summary;
CREATE VIEW vw_event_participation_summary AS
SELECT 
    e.event_id,
    e.event_name,
    e.event_type,
    e.event_date,
    e.venue,
    e.max_participants,
    COUNT(r.registration_id) AS total_registered,
    SUM(CASE WHEN a.attendance_status = 'Present' THEN 1 ELSE 0 END) AS total_present,
    SUM(CASE WHEN a.attendance_status = 'Absent' THEN 1 ELSE 0 END) AS total_absent,
    ROUND(
        CASE 
            WHEN COUNT(r.registration_id) = 0 THEN 0.0
            ELSE (CAST(SUM(CASE WHEN a.attendance_status = 'Present' THEN 1 ELSE 0 END) AS REAL) / COUNT(r.registration_id)) * 100 
        END, 2
    ) AS attendance_percentage
FROM events e
LEFT JOIN registrations r ON e.event_id = r.event_id AND r.status = 'Confirmed'
LEFT JOIN attendance a ON r.registration_id = a.registration_id
GROUP BY e.event_id, e.event_name, e.event_type, e.event_date, e.venue, e.max_participants;
