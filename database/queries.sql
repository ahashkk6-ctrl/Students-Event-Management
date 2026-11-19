-- ============================================================================
-- SMART CAMPUS EVENT MANAGEMENT AND STUDENT PARTICIPATION ANALYSIS SYSTEM
-- Comprehensive SQL Queries Script (DDL / DML / DQL / Aggregations)
-- Course: 25ITT31 - Database Management Systems (Units 1, 2, 3)
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. FUNCTIONAL REQUIREMENT QUERIES
-- ----------------------------------------------------------------------------

-- 1.1 Maintain & List Students
SELECT student_id, name, department, year_of_study, email, phone 
FROM students 
ORDER BY department, year_of_study, name;

-- 1.2 Add & Retrieve Event Details
SELECT event_id, event_name, event_type, event_date, venue, max_participants 
FROM events 
ORDER BY event_date ASC;

-- 1.3 Check for Duplicate Registration (Requirement 3: Prevent Duplicates)
-- Returns 1 if student already registered for event EVT101, 0 otherwise
SELECT COUNT(*) AS is_already_registered 
FROM registrations 
WHERE student_id = '25IT001' AND event_id = 'EVT101';

-- 1.4 Check Event Capacity Before Registering
SELECT 
    e.max_participants,
    COUNT(r.registration_id) AS current_registrations,
    (e.max_participants - COUNT(r.registration_id)) AS available_seats
FROM events e
LEFT JOIN registrations r ON e.event_id = r.event_id AND r.status = 'Confirmed'
WHERE e.event_id = 'EVT101'
GROUP BY e.event_id, e.max_participants;

-- 1.5 List Registered Participants for a Given Event (Requirement 5)
SELECT 
    r.registration_id,
    s.student_id,
    s.name AS student_name,
    s.department,
    s.year_of_study,
    r.registration_date,
    r.status AS registration_status,
    COALESCE(a.attendance_status, 'Not Marked') AS attendance_status,
    a.feedback_score
FROM registrations r
JOIN students s ON r.student_id = s.student_id
LEFT JOIN attendance a ON r.registration_id = a.registration_id
WHERE r.event_id = 'EVT101'
ORDER BY r.registration_date ASC;

-- 1.6 Event-Wise Summary Report (Requirement 5)
SELECT 
    e.event_id,
    e.event_name,
    e.event_type,
    e.event_date,
    e.venue,
    e.max_participants,
    COUNT(r.registration_id) AS total_registrations,
    SUM(CASE WHEN a.attendance_status = 'Present' THEN 1 ELSE 0 END) AS total_present,
    SUM(CASE WHEN a.attendance_status = 'Absent' THEN 1 ELSE 0 END) AS total_absent,
    ROUND(
        CASE 
            WHEN COUNT(r.registration_id) = 0 THEN 0.0
            ELSE (CAST(SUM(CASE WHEN a.attendance_status = 'Present' THEN 1 ELSE 0 END) AS REAL) / COUNT(r.registration_id)) * 100 
        END, 2
    ) AS attendance_rate_pct,
    ROUND(AVG(a.feedback_score), 2) AS avg_feedback_rating
FROM events e
LEFT JOIN registrations r ON e.event_id = r.event_id AND r.status = 'Confirmed'
LEFT JOIN attendance a ON r.registration_id = a.registration_id
GROUP BY e.event_id, e.event_name, e.event_type, e.event_date, e.venue, e.max_participants
ORDER BY e.event_date ASC;

-- ----------------------------------------------------------------------------
-- 2. ADVANCED ANALYTICAL & NESTED QUERIES (DBMS Syllabus Unit 2 & 3)
-- ----------------------------------------------------------------------------

-- 2.1 Department-Wise Participation Distribution (Joins & Group By)
SELECT 
    s.department,
    COUNT(DISTINCT s.student_id) AS active_students,
    COUNT(r.registration_id) AS total_event_registrations,
    SUM(CASE WHEN a.attendance_status = 'Present' THEN 1 ELSE 0 END) AS total_attended
FROM students s
JOIN registrations r ON s.student_id = r.student_id
LEFT JOIN attendance a ON r.registration_id = a.registration_id
GROUP BY s.department
ORDER BY total_event_registrations DESC;

-- 2.2 Year-of-Study Participation Analysis
SELECT 
    s.year_of_study,
    COUNT(DISTINCT s.student_id) AS unique_participants,
    COUNT(r.registration_id) AS total_registrations,
    SUM(CASE WHEN a.attendance_status = 'Present' THEN 1 ELSE 0 END) AS attended_count
FROM students s
JOIN registrations r ON s.student_id = r.student_id
LEFT JOIN attendance a ON r.registration_id = a.registration_id
GROUP BY s.year_of_study
ORDER BY s.year_of_study ASC;

-- 2.3 Highly Active Students (Subquery with Aggregate Filter - HAVING)
SELECT 
    s.student_id,
    s.name,
    s.department,
    COUNT(r.registration_id) AS events_registered,
    SUM(CASE WHEN a.attendance_status = 'Present' THEN 1 ELSE 0 END) AS events_attended
FROM students s
JOIN registrations r ON s.student_id = r.student_id
LEFT JOIN attendance a ON r.registration_id = a.registration_id
GROUP BY s.student_id, s.name, s.department
HAVING COUNT(r.registration_id) >= 2
ORDER BY events_attended DESC, events_registered DESC;

-- 2.4 Events with Capacity Utilization Greater than 50% (Nested Subquery)
SELECT 
    event_id,
    event_name,
    event_type,
    max_participants,
    registered_count,
    ROUND((CAST(registered_count AS REAL) / max_participants) * 100, 2) AS capacity_utilization_pct
FROM (
    SELECT 
        e.event_id,
        e.event_name,
        e.event_type,
        e.max_participants,
        COUNT(r.registration_id) AS registered_count
    FROM events e
    LEFT JOIN registrations r ON e.event_id = r.event_id
    GROUP BY e.event_id, e.event_name, e.event_type, e.max_participants
) sub
WHERE (CAST(registered_count AS REAL) / max_participants) >= 0.50
ORDER BY capacity_utilization_pct DESC;

-- 2.5 Correlated Subquery: Students who registered for events but were absent
SELECT s.student_id, s.name, s.department, e.event_name
FROM students s
JOIN registrations r ON s.student_id = r.student_id
JOIN events e ON r.event_id = e.event_id
WHERE EXISTS (
    SELECT 1 
    FROM attendance a 
    WHERE a.registration_id = r.registration_id 
      AND a.attendance_status = 'Absent'
);
