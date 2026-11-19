-- ============================================================================
-- SMART CAMPUS EVENT MANAGEMENT AND STUDENT PARTICIPATION ANALYSIS SYSTEM
-- Sample Seed Data (DML)
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. Insert Students
-- Diverse dataset covering multiple departments and study years.
-- ----------------------------------------------------------------------------
INSERT INTO students (student_id, name, department, year_of_study, email, phone) VALUES
('25IT001', 'Aarav Sharma', 'Information Technology', 3, 'aarav.it@vcet.ac.in', '9876543210'),
('25IT002', 'Bhavana Ramesh', 'Information Technology', 3, 'bhavana.it@vcet.ac.in', '9876543211'),
('25IT003', 'Chandran Kumar', 'Information Technology', 2, 'chandran.it@vcet.ac.in', '9876543212'),
('25IT004', 'Deepika Sundar', 'Information Technology', 4, 'deepika.it@vcet.ac.in', '9876543213'),
('25IT005', 'Elango Muthu', 'Information Technology', 1, 'elango.it@vcet.ac.in', '9876543214'),
('25CS001', 'Farhan Ali', 'Computer Science', 3, 'farhan.cs@vcet.ac.in', '9876543215'),
('25CS002', 'Gayathri Priya', 'Computer Science', 2, 'gayathri.cs@vcet.ac.in', '9876543216'),
('25CS003', 'Harish Varma', 'Computer Science', 4, 'harish.cs@vcet.ac.in', '9876543217'),
('25CS004', 'Ishwarya Lakshmi', 'Computer Science', 3, 'ishwarya.cs@vcet.ac.in', '9876543218'),
('25EC001', 'Jagadeesh Paul', 'Electronics and Communication', 2, 'jagadeesh.ec@vcet.ac.in', '9876543219'),
('25EC002', 'Keerthana Devi', 'Electronics and Communication', 3, 'keerthana.ec@vcet.ac.in', '9876543220'),
('25EC003', 'Lokesh Nathan', 'Electronics and Communication', 4, 'lokesh.ec@vcet.ac.in', '9876543221'),
('25AD001', 'Meera Krishnan', 'Artificial Intelligence & Data Science', 2, 'meera.ad@vcet.ac.in', '9876543222'),
('25AD002', 'Naveen Prakash', 'Artificial Intelligence & Data Science', 3, 'naveen.ad@vcet.ac.in', '9876543223'),
('25AD003', 'Oviya Selvan', 'Artificial Intelligence & Data Science', 1, 'oviya.ad@vcet.ac.in', '9876543224'),
('25ME001', 'Pradeep Raj', 'Mechanical Engineering', 3, 'pradeep.me@vcet.ac.in', '9876543225'),
('25ME002', 'Qadir Basha', 'Mechanical Engineering', 2, 'qadir.me@vcet.ac.in', '9876543226'),
('25CE001', 'Roshini Balan', 'Civil Engineering', 3, 'roshini.ce@vcet.ac.in', '9876543227'),
('25CE002', 'Siddharth Mani', 'Civil Engineering', 4, 'siddharth.ce@vcet.ac.in', '9876543228'),
('25IT006', 'Tharun Prasad', 'Information Technology', 2, 'tharun.it@vcet.ac.in', '9876543229');

-- ----------------------------------------------------------------------------
-- 2. Insert Events
-- Range of Technical, Workshop, Seminar, and Cultural events.
-- ----------------------------------------------------------------------------
INSERT INTO events (event_id, event_name, event_type, event_date, venue, max_participants, description) VALUES
('EVT101', 'CodeQuest National Hackathon', 'Technical', '2026-09-15', 'Main Computing Lab 1', 12, '24-hour coding challenge on campus problem-solving'),
('EVT102', 'Hands-on Generative AI & LLMs', 'Workshop', '2026-09-22', 'Seminar Hall B', 15, 'Interactive workshop building intelligent agentic systems'),
('EVT103', 'Future of Cloud Infrastructure', 'Seminar', '2026-10-05', 'Auditorium 2', 20, 'Industry expert keynote on cloud-native computing and microservices'),
('EVT104', 'Tarang Inter-College Cultural Fest', 'Cultural', '2026-10-18', 'Open Air Amphitheatre', 30, 'Annual cultural extravaganza featuring dance, music, and theatre'),
('EVT105', 'Internet of Things with Embedded C', 'Workshop', '2026-11-02', 'IoT Innovation Lab', 10, 'Hands-on microcontrollers and sensor telemetry integration'),
('EVT106', 'Cyber Forensics & Threat Defense', 'Technical', '2026-11-12', 'Digital Security Lab', 12, 'Hands-on simulated incident response and blue-team tactics');

-- ----------------------------------------------------------------------------
-- 3. Insert Registrations
-- Simulating registrations across various events.
-- ----------------------------------------------------------------------------
INSERT INTO registrations (student_id, event_id, registration_date, status) VALUES
-- Registrations for EVT101 (CodeQuest Hackathon)
('25IT001', 'EVT101', '2026-09-01 10:00:00', 'Confirmed'),
('25IT002', 'EVT101', '2026-09-01 10:15:00', 'Confirmed'),
('25CS001', 'EVT101', '2026-09-01 11:30:00', 'Confirmed'),
('25CS003', 'EVT101', '2026-09-02 09:45:00', 'Confirmed'),
('25AD001', 'EVT101', '2026-09-02 14:20:00', 'Confirmed'),
('25AD002', 'EVT101', '2026-09-03 16:00:00', 'Confirmed'),
('25EC002', 'EVT101', '2026-09-04 11:10:00', 'Confirmed'),
('25IT004', 'EVT101', '2026-09-05 13:00:00', 'Confirmed'),

-- Registrations for EVT102 (Generative AI Workshop)
('25IT001', 'EVT102', '2026-09-10 09:30:00', 'Confirmed'),
('25IT003', 'EVT102', '2026-09-10 10:00:00', 'Confirmed'),
('25CS002', 'EVT102', '2026-09-10 11:15:00', 'Confirmed'),
('25CS004', 'EVT102', '2026-09-11 12:45:00', 'Confirmed'),
('25AD001', 'EVT102', '2026-09-11 15:30:00', 'Confirmed'),
('25AD002', 'EVT102', '2026-09-12 10:00:00', 'Confirmed'),
('25AD003', 'EVT102', '2026-09-12 11:20:00', 'Confirmed'),
('25EC001', 'EVT102', '2026-09-13 14:00:00', 'Confirmed'),
('25IT006', 'EVT102', '2026-09-14 09:00:00', 'Confirmed'),

-- Registrations for EVT103 (Cloud Seminar)
('25CS001', 'EVT103', '2026-09-25 10:00:00', 'Confirmed'),
('25CS004', 'EVT103', '2026-09-25 11:30:00', 'Confirmed'),
('25IT002', 'EVT103', '2026-09-26 09:15:00', 'Confirmed'),
('25IT004', 'EVT103', '2026-09-26 14:00:00', 'Confirmed'),
('25EC003', 'EVT103', '2026-09-27 10:20:00', 'Confirmed'),
('25ME001', 'EVT103', '2026-09-28 16:40:00', 'Confirmed'),
('25CE002', 'EVT103', '2026-09-29 11:10:00', 'Confirmed'),

-- Registrations for EVT104 (Tarang Cultural Fest)
('25IT002', 'EVT104', '2026-10-01 10:00:00', 'Confirmed'),
('25IT005', 'EVT104', '2026-10-01 10:45:00', 'Confirmed'),
('25CS002', 'EVT104', '2026-10-01 11:30:00', 'Confirmed'),
('25EC001', 'EVT104', '2026-10-02 12:00:00', 'Confirmed'),
('25EC002', 'EVT104', '2026-10-02 14:15:00', 'Confirmed'),
('25AD003', 'EVT104', '2026-10-03 15:30:00', 'Confirmed'),
('25ME001', 'EVT104', '2026-10-03 16:00:00', 'Confirmed'),
('25ME002', 'EVT104', '2026-10-04 09:20:00', 'Confirmed'),
('25CE001', 'EVT104', '2026-10-04 10:50:00', 'Confirmed'),
('25CE002', 'EVT104', '2026-10-04 11:40:00', 'Confirmed'),

-- Registrations for EVT105 (IoT Workshop)
('25EC001', 'EVT105', '2026-10-20 10:00:00', 'Confirmed'),
('25EC002', 'EVT105', '2026-10-20 11:00:00', 'Confirmed'),
('25EC003', 'EVT105', '2026-10-21 09:30:00', 'Confirmed'),
('25IT003', 'EVT105', '2026-10-21 14:00:00', 'Confirmed'),
('25ME002', 'EVT105', '2026-10-22 15:10:00', 'Confirmed'),

-- Registrations for EVT106 (Cyber Forensics)
('25IT001', 'EVT106', '2026-11-01 09:00:00', 'Confirmed'),
('25IT004', 'EVT106', '2026-11-01 10:30:00', 'Confirmed'),
('25CS001', 'EVT106', '2026-11-01 11:15:00', 'Confirmed'),
('25CS003', 'EVT106', '2026-11-02 14:20:00', 'Confirmed'),
('25AD002', 'EVT106', '2026-11-02 16:00:00', 'Confirmed');

-- ----------------------------------------------------------------------------
-- 4. Insert Attendance and Feedback
-- Recording real event attendance and evaluation ratings.
-- ----------------------------------------------------------------------------
-- EVT101 Attendance (Hackathon - 8 registrations)
INSERT INTO attendance (registration_id, attendance_status, feedback_score, remarks) VALUES
(1, 'Present', 5, 'Exceptional experience, solved hard algorithmic problems'),
(2, 'Present', 5, 'Great team collaboration and mentorship support'),
(3, 'Present', 4, 'Very well organized hackathon'),
(4, 'Absent', NULL, 'Medical leave notified'),
(5, 'Present', 5, 'Loved the machine learning problem track'),
(6, 'Present', 4, 'Good food and great judging panel'),
(7, 'Present', 4, 'Learned hardware-software co-design'),
(8, 'Absent', NULL, 'Unforeseen conflict with semester test');

-- EVT102 Attendance (AI Workshop - 9 registrations)
INSERT INTO attendance (registration_id, attendance_status, feedback_score, remarks) VALUES
(9, 'Present', 5, 'Hands-on prompt engineering and LangChain was awesome'),
(10, 'Present', 4, 'Clear step-by-step guidance from the speaker'),
(11, 'Present', 5, 'Very insightful practical session'),
(12, 'Present', 4, 'Good concepts, wished for slightly more GPU quota'),
(13, 'Present', 5, 'Top notch practical demos on transformers'),
(14, 'Present', 4, 'Understood fine-tuning fundamentals'),
(15, 'Absent', NULL, 'Family emergency'),
(16, 'Present', 4, 'Useful for my final year project ideation'),
(17, 'Present', 5, 'Excellent hands-on session');

-- EVT103 Attendance (Cloud Seminar - 7 registrations)
INSERT INTO attendance (registration_id, attendance_status, feedback_score, remarks) VALUES
(18, 'Present', 4, 'Great industry perspective on Kubernetes and GCP'),
(19, 'Present', 5, 'Excellent speaker from cloud engineering background'),
(20, 'Present', 4, 'Good introduction to serverless computing'),
(21, 'Absent', NULL, 'Placement interview schedule overlap'),
(22, 'Present', 3, 'Theoretical session, more code examples would help'),
(23, 'Present', 4, 'Broadened understanding of modern DevOps pipelines'),
(24, 'Absent', NULL, 'Absent without prior intimation');

-- EVT104 Attendance (Cultural Fest - 10 registrations)
INSERT INTO attendance (registration_id, attendance_status, feedback_score, remarks) VALUES
(25, 'Present', 5, 'Vibrant stage performances and atmosphere'),
(26, 'Present', 5, 'Amazing musical bands and dance competitions'),
(27, 'Present', 5, 'Wonderful participation from all departments'),
(28, 'Present', 4, 'Great sound setup and lighting arrangements'),
(29, 'Present', 5, 'Memorable cultural evening for students'),
(30, 'Present', 4, 'Well managed crowd and hospitality'),
(31, 'Present', 4, 'Enjoyed inter-college dance face-offs'),
(32, 'Absent', NULL, 'Participating in external sports meet'),
(33, 'Present', 5, 'Fantastic creative talent displayed'),
(34, 'Present', 5, 'Super fun and memorable event');

-- EVT105 Attendance (IoT Workshop - 5 registrations)
INSERT INTO attendance (registration_id, attendance_status, feedback_score, remarks) VALUES
(35, 'Present', 5, 'NodeMCU and MQTT telemetry setup was practical'),
(36, 'Present', 4, 'Good hardware kit provided per student bench'),
(37, 'Present', 4, 'Helpful instructor assistance'),
(38, 'Present', 5, 'Real-time dashboard creation worked smoothly'),
(39, 'Absent', NULL, 'Illness');

-- EVT106 Attendance (Cyber Forensics - 5 registrations)
INSERT INTO attendance (registration_id, attendance_status, feedback_score, remarks) VALUES
(40, 'Present', 5, 'Capture The Flag style forensic challenge was exciting'),
(41, 'Present', 5, 'Deep dive into memory dump analysis with Volatility'),
(42, 'Present', 4, 'Great hands-on network packet inspection with Wireshark'),
(43, 'Present', 5, 'Hands-on reverse engineering was top quality'),
(44, 'Absent', NULL, 'Lab exam scheduled on same afternoon');
