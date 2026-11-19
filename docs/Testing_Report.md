# SOFTWARE TESTING REPORT & TEST CASES SPECIFICATION
## Smart Campus Event Management and Student Participation Analysis System

**Institution**: Velalar College of Engineering and Technology (Autonomous), Erode – 638012  
**Department**: Department of Information Technology  
**Course Code & Title**: 25ITT33 – Software Engineering (Unit 4: Software Testing)  
**Academic Year**: 2026-27 (ODD Semester)  
**Scenario**: Scenario 6 (Smart Campus Event Management and Participation Analysis)  

---

## 1. Testing Strategy and Methodologies

The system underwent verification and validation following modern software engineering testing levels:
1. **Unit Testing**: Testing individual methods, input validation helpers, and DAO query operations in isolation.
2. **Integration Testing**: Testing interface communication between `EventService`, Data Access Objects (`StudentDAO`, `EventDAO`, `RegistrationDAO`, `AttendanceDAO`), and the underlying SQLite database.
3. **Validation & System Testing**: End-to-end evaluation verifying functional adherence to college scenario requirements.

### 1.1 Black-Box Testing Techniques Applied
- **Equivalence Partitioning (EP)**:
  - Year of study partitioned into Valid $[1, 4]$ and Invalid $(\le 0, \ge 5)$ classes.
  - Feedback score partitioned into Valid $[1, 5]$ and Invalid $(< 1, > 5)$.
- **Boundary Value Analysis (BVA)**:
  - Event capacity tested at boundary values: capacity limit $C$, $C-1$, and $C+1$ (overflow condition).
- **Error Guessing**:
  - Submitting duplicate registration for an already registered student.
  - Registering non-existent student IDs.

### 1.2 White-Box Testing Techniques Applied
- **Statement & Branch Coverage**: Validating that all branches in `EventService.registerStudentForEvent` (Student Exists? -> Event Exists? -> Already Registered? -> Capacity Full? -> DB Success?) are systematically traversed.
- **Data Flow Testing**: Tracing registration IDs from insertion through attendance marking and report generation.

---

## 2. Comprehensive Test Cases Table

| Test Case ID | Test Category | Feature / Requirement Under Test | Input Data / Preconditions | Expected Result | Actual Result | Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TC-STU-01** | Unit / Black-Box | Add Valid Student (FR 1) | ID: `25IT007`, Name: `Kavitha S`, Dept: `Information Technology`, Year: `3`, Email: `kavitha.it@vcet.ac.in`, Phone: `9876543230` | Student saved to database; returns success message. | Student record inserted successfully into `students` table. | **PASS** |
| **TC-STU-02** | Unit / EP | Student Year Invalid Boundary | Year: `5` (outside 1–4 range) | Rejected by input validation and SQL `CHECK` constraint. | Input validation rejects input with warning prompt. | **PASS** |
| **TC-STU-03** | Unit / Error Guessing | Add Student with Duplicate ID | ID: `25IT001` (already exists in database) | Rejection with message: `Student ID already exists`. | System prevents duplicate primary key insertion. | **PASS** |
| **TC-EVT-01** | Unit / Black-Box | Add New Campus Event (FR 2) | ID: `EVT107`, Name: `Web3 Summit`, Type: `Workshop`, Date: `2026-11-28`, Venue: `CCF Lab 3`, Capacity: `15` | Event inserted into database with capacity threshold. | Event created successfully in `events` table. | **PASS** |
| **TC-EVT-02** | Unit / BVA | Event Capacity $\le 0$ Boundary | Max Capacity: `0` or `-5` | Validation rejection: `Capacity must be > 0`. | Rejected by service layer and database CHECK constraint. | **PASS** |
| **TC-REG-01** | Integration / White-Box | Register Eligible Student for Event (FR 3) | Student: `25IT007`, Event: `EVT107` (Seats available) | Registration accepted with status `Confirmed`. | New record created in `registrations` table. | **PASS** |
| **TC-REG-02** | Functional / BVA | **Prevent Duplicate Registration** (FR 3) | Student: `25IT001`, Event: `EVT101` (Student already registered) | **Blocked**: `Student is already registered for this event`. | Duplicate intercepted by service check and `UNIQUE` constraint. | **PASS** |
| **TC-REG-03** | Functional / BVA | Enforce Maximum Capacity Limit (FR 3) | Event with Max: `10`, Current Regs: `10`. Register 11th student. | **Blocked**: `Event has reached maximum capacity`. | Registration rejected with capacity overflow notification. | **PASS** |
| **TC-REG-04** | Integration / Error Guessing | Register Non-Existent Student ID | Student: `99XX999`, Event: `EVT101` | **Blocked**: `Student ID not found`. | Validated by foreign key check and service layer. | **PASS** |
| **TC-ATT-01** | Functional / Black-Box | Mark Student Present with Feedback (FR 4) | Reg ID: `1`, Status: `Present`, Rating: `5`, Remarks: `Outstanding coding session` | Attendance saved; feedback rating recorded. | Attendance record inserted/updated in `attendance` table. | **PASS** |
| **TC-ATT-02** | Functional / EP | Mark Student Absent (FR 4) | Reg ID: `4`, Status: `Absent`, Remarks: `Medical leave` | Attendance recorded as `Absent`; feedback set to `NULL`. | Attendance record saved with NULL feedback rating. | **PASS** |
| **TC-ATT-03** | Unit / EP | Invalid Feedback Rating Score | Status: `Present`, Rating: `7` (outside 1–5 range) | Rejection: `Feedback score must be between 1 and 5`. | Input validation prevents invalid rating submission. | **PASS** |
| **TC-REP-01** | System / SQL | Event-Wise Summary Report (FR 5) | Query event report view for all events | Tabular output showing Registered, Present, Absent, Attendance Rate %, Avg Rating. | Accurate metrics matching underlying table data. | **PASS** |
| **TC-REP-02** | System / Aggregation | Department Participation Analytics (FR 5) | Query department distribution | Correct counts of distinct students and attended events grouped by department. | Matches raw counts in `students` and `registrations`. | **PASS** |
| **TC-DAT-01** | System / Python Integration | Python Data Science Pipeline (FR 6) | Run `analysis.py` connecting to SQLite database | Preprocesses dataset, generates 6 high-resolution charts in `visualizations/`. | All 6 `.png` charts rendered with zero runtime errors. | **PASS** |

---

## 3. Test Execution Summary

- **Total Test Cases Executed**: 15
- **Test Cases Passed**: 15 (100%)
- **Test Cases Failed**: 0 (0%)
- **Conclusion**: The Smart Campus Event Management System satisfies all functional and non-functional requirements specified in Scenario 6 with zero defect leakage.
