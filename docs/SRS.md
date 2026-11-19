# SOFTWARE REQUIREMENTS SPECIFICATION (SRS)
## Smart Campus Event Management and Student Participation Analysis System

**Institution**: Velalar College of Engineering and Technology (Autonomous), Erode – 638012  
**Department**: Department of Information Technology  
**Academic Year**: 2026-27 (ODD Semester)  
**Course Code & Title**: 25ITT33 – Software Engineering  
**Class**: 25IT3A & 25IT3B | **Activity**: Project-Based Assessment  
**Scenario**: Scenario 6 (Assigned Register Numbers: 06, 36, 66, 96)  
**Standard**: IEEE Std 830-1998  

---

## 1. Introduction

### 1.1 Purpose
The purpose of this Software Requirements Specification (SRS) document is to provide a complete, rigorous description of the **Smart Campus Event Management and Student Participation Analysis System**. This document defines the functional and non-functional requirements, system constraints, user characteristics, and interface specifications for the software engineering assessment.

### 1.2 Scope
The Smart Campus Event Management System is an integrated software platform that digitizes and automates:
- Student enrollment profiles across academic departments and years of study.
- Event scheduling, category management (Technical, Workshop, Seminar, Cultural), and venue allotment with strict capacity thresholds.
- Conflict-free student event registrations enforcing mathematical uniqueness to eradicate duplicate registrations.
- Digital attendance recording, participation status tracking, and structured student feedback capture.
- Automated analytical reporting and data retrieval feeding directly into Python data science visualization pipelines.

### 1.3 Definitions, Acronyms, and Abbreviations
- **SRS**: Software Requirements Specification
- **DBMS**: Database Management System
- **RDBMS**: Relational Database Management System
- **PK / FK**: Primary Key / Foreign Key
- **JDBC**: Java Database Connectivity
- **EDA**: Exploratory Data Analysis
- **VCET**: Velalar College of Engineering and Technology
- **BCNF**: Boyce-Codd Normal Form

### 1.4 References
- IEEE Std 830-1998: *IEEE Recommended Practice for Software Requirements Specifications*.
- Course Syllabus: *25ITT31 – Database Management Systems*, *25ITT32 – Java Programming*, *25ITT33 – Software Engineering*, *25ITC31 – Foundations of Data Science*.

### 1.5 Overview
The subsequent sections provide an architectural overview of the system, detailed user characteristics, specific functional requirements with input/output behaviors, non-functional performance and security constraints, and external interface requirements.

---

## 2. Overall Description

### 2.1 Product Perspective
The Smart Campus Event Management System replaces error-prone, manual paper and spreadsheet-based campus records. It functions as a three-tier application:
1. **Presentation & Application Layer (Java)**: Interactive CLI and business rules execution module.
2. **Persistence Layer (SQLite / RDBMS)**: Acid-compliant relational database enforcing constraints and referential integrity.
3. **Analytics & Visualization Layer (Python)**: Pandas, NumPy, Matplotlib, and Seaborn pipeline for exploratory analytics.

### 2.2 Product Functions
- Maintain student demographic records (ID, name, department, year of study, contact details).
- Maintain event schedules, venues, types, and participant capacity caps.
- Prevent duplicate registrations via unique database constraints and service-layer validation.
- Enforce capacity check to prevent event over-subscription.
- Record attendance ('Present' / 'Absent') and feedback rating (1–5 scale).
- Produce event-wise summary reports and department-wise metrics.
- Export preprocessed dataset and generate statistical visualization figures.

### 2.3 User Classes and Characteristics
- **Student User**: Views upcoming campus events, checks available seat quotas, and registers for eligible events.
- **Faculty / Event Coordinator**: Creates new campus events, specifies capacity limits, records student attendance, and reviews feedback scores.
- **Department Head / Administrator**: Examines event-wise participation statistics, cross-department trends, and generated visual charts.

### 2.4 Operating Environment
- **Operating System**: Windows 10/11, Linux, or macOS.
- **Java Runtime**: OpenJDK / Oracle JDK 21+
- **Database Engine**: SQLite 3.x (embedded) / ANSI SQL RDBMS
- **Python Environment**: Python 3.11+ with Pandas, NumPy, Matplotlib, and Seaborn.

### 2.5 Design and Implementation Constraints
- Relational schema must satisfy 3NF and BCNF normalization standards.
- Java application must utilize Object-Oriented principles (Encapsulation, Inheritance, Interfaces, DAO Pattern).
- Concurrency and duplicate prevention must be guaranteed at both database level (`UNIQUE(student_id, event_id)`) and application service layer.
- Python data retrieval must execute via standard SQL connectors without hardcoding analysis data.

---

## 3. Specific Functional Requirements

### 3.1 Functional Requirement 1: Student Information Management
- **FR 1.1**: The system shall record and maintain student ID, name, department, year of study (1 to 4), official college email, and contact number.
- **FR 1.2**: The system shall validate that student ID and email are globally unique.
- **FR 1.3**: The system shall allow querying student details by student ID.

### 3.2 Functional Requirement 2: Event Details & Capacity Management
- **FR 2.1**: The system shall record event ID, event name, category (`Technical`, `Workshop`, `Seminar`, `Cultural`), event date (`YYYY-MM-DD`), venue, maximum participant capacity, and description.
- **FR 2.2**: The system shall enforce that maximum participants is a strictly positive integer ($> 0$).
- **FR 2.3**: The system shall dynamically compute remaining seat quotas for any chosen event.

### 3.3 Functional Requirement 3: Event Registration & Duplicate Prevention
- **FR 3.1**: The system shall allow any registered student to enroll in an event.
- **FR 3.2**: **Duplicate Prevention**: The system shall verify whether the student is already registered for the specified event. If an existing registration exists, the transaction must be blocked with an explicit error message.
- **FR 3.3**: **Capacity Enforcement**: The system shall verify current registration count against `max_participants`. If capacity is exhausted, the system shall reject registration.

### 3.4 Functional Requirement 4: Attendance and Feedback Status Tracking
- **FR 4.1**: The system shall record student attendance status as either `Present` or `Absent`.
- **FR 4.2**: For students marked `Present`, the system shall record an optional feedback score (integer 1 to 5) and qualitative remarks.
- **FR 4.3**: For students marked `Absent`, feedback score shall be nullified and absence justification recorded.

### 3.5 Functional Requirement 5: Participant Listing & Event-Wise Reporting
- **FR 5.1**: The system shall display all registered participants for a given event ID.
- **FR 5.2**: The system shall compute and display event-wise metrics: Total Registered, Total Present, Total Absent, Attendance Rate Percentage, and Average Feedback Rating.
- **FR 5.3**: The system shall compute department-wise participation distribution.

### 3.6 Functional Requirement 6: Data Science Retrieval and Visualization
- **FR 6.1**: The system shall programmatically query the database and export clean analytical DataFrames via Python.
- **FR 6.2**: The system shall handle missing values and perform data type transformations.
- **FR 6.3**: The system shall generate and persist charts (Bar Charts, Pie Charts, Count Plots, Box Plots, and Correlation Heatmaps).

---

## 4. Non-Functional Requirements

### 4.1 Performance Requirements
- Query execution and report rendering shall complete in under 500 milliseconds for datasets up to 100,000 records.
- Python visual analytics script shall generate all 6 charts in under 5 seconds.

### 4.2 Reliability & Availability
- Database state shall remain ACID-compliant with foreign key constraints enforced (`PRAGMA foreign_keys = ON`).
- Zero data corruption on abnormal application termination.

### 4.3 Security & Data Integrity
- Referential integrity guaranteed via foreign key relationships (`ON DELETE CASCADE ON UPDATE CASCADE`).
- Domain integrity verified using SQL `CHECK` constraints on year of study, attendance status, and feedback scores.

### 4.4 Maintainability & Extensibility
- Clean separation of concerns following DAO and Service Layer architectural patterns.
- Clear code comments, modular classes, and self-documenting method signatures.
