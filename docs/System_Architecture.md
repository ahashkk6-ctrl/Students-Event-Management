# SYSTEM ARCHITECTURE AND DESIGN DIAGRAMS
## Smart Campus Event Management and Student Participation Analysis System

**Institution**: Velalar College of Engineering and Technology (Autonomous), Erode – 638012  
**Department**: Department of Information Technology  
**Courses Integrated**: 25ITT32 (Java Programming) & 25ITT33 (Software Engineering)  
**Activity**: Activity-Project-Based Assessment  

---

## 1. Overall System Architecture

The Smart Campus Event Management System is architected as an enterprise multi-tier decoupled system comprising a **Presentation Layer**, a **Business Logic & Service Layer**, a **Persistence / Database Layer**, and a **Data Science & Analytics Layer**.

```mermaid
graph TD
    subgraph Presentation_Layer ["Presentation & UI Layer"]
        CLI["Java Interactive Terminal CLI (Main.java)"]
        Helper["ConsoleHelper (ANSI Formatted Tables & Menus)"]
        Jupyter["Jupyter Notebook (EDA & Live Graphs)"]
    end

    subgraph Business_Layer ["Application & Business Logic Layer (Java 21)"]
        Service["EventService (Business Logic Coordinator)"]
        Val1["Duplicate Prevention Validator"]
        Val2["Event Capacity & Limit Checker"]
        Val3["Attendance & Feedback Verifier"]
    end

    subgraph Data_Access_Layer ["Data Access Layer (DAO Pattern)"]
        SDAO["StudentDAO"]
        EDAO["EventDAO"]
        RDAO["RegistrationDAO"]
        ADAO["AttendanceDAO"]
        RPDAO["ReportDAO"]
        DBC["DBConnection Manager (JDBC)"]
    end

    subgraph Database_Layer ["Persistence Layer (DBMS)"]
        DB[(smart_campus.db / SQLite RDBMS)]
        T1[("students")]
        T2[("events")]
        T3[("registrations")]
        T4[("attendance")]
        V1[("vw_event_participation_summary")]
    end

    subgraph Analytics_Layer ["Data Science & Analytics Engine (Python 3.11)"]
        PyScript["analysis.py (Data Preprocessor)"]
        PandasEngine["Pandas & NumPy Wrangling"]
        VisEngine["Matplotlib & Seaborn Visualizations"]
        PNGs["visualizations/*.png (High-Res Charts)"]
    end

    CLI --> Service
    Helper --> CLI
    Service --> Val1
    Service --> Val2
    Service --> Val3
    Service --> SDAO
    Service --> EDAO
    Service --> RDAO
    Service --> ADAO
    Service --> RPDAO
    SDAO --> DBC
    EDAO --> DBC
    RDAO --> DBC
    ADAO --> DBC
    RPDAO --> DBC
    DBC --> DB
    DB --> T1
    DB --> T2
    DB --> T3
    DB --> T4
    DB --> V1
    DB -.-> PyScript
    PyScript --> PandasEngine
    PandasEngine --> VisEngine
    VisEngine --> PNGs
    Jupyter -.-> DB
```

---

## 2. UML Use Case Diagram

The Use Case Diagram defines the interactions between the system actors (**Student**, **Event Coordinator / Faculty**, and **Department Head / Administrator**) and the system functions.

```mermaid
flowchart LR
    Student((Student))
    Coordinator((Event Coordinator / Faculty))
    Admin((HOD / Administrator))

    subgraph System_Boundary ["Smart Campus Event System"]
        UC1(["Browse Upcoming Events & Capacity"])
        UC2(["Register for Event"])
        UC3(["Prevent Duplicate Registration <<include>>"])
        UC4(["Enforce Maximum Capacity <<include>>"])
        UC5(["Add New Campus Event"])
        UC6(["Mark Student Attendance & Feedback"])
        UC7(["Generate Event-Wise Summary Report"])
        UC8(["View Department-Wise Analytics"])
        UC9(["Run Python Data Science Visualizations"])
    end

    Student --> UC1
    Student --> UC2
    UC2 ..> UC3 : includes
    UC2 ..> UC4 : includes

    Coordinator --> UC5
    Coordinator --> UC6
    Coordinator --> UC7

    Admin --> UC7
    Admin --> UC8
    Admin --> UC9
```

---

## 3. UML Class Diagram

The Class Diagram illustrates the object-oriented structure, class attributes, methods, encapsulation, and relationships.

```mermaid
classDiagram
    class Student {
        -String studentId
        -String name
        -String department
        -int yearOfStudy
        -String email
        -String phone
        +getStudentId() String
        +getName() String
        +getDepartment() String
        +getYearOfStudy() int
        +toString() String
    }

    class Event {
        -String eventId
        -String eventName
        -String eventType
        -String eventDate
        -String venue
        -int maxParticipants
        -String description
        +getEventId() String
        +getEventName() String
        +getMaxParticipants() int
        +toString() String
    }

    class Registration {
        -int registrationId
        -String studentId
        -String eventId
        -String registrationDate
        -String status
        -String studentName
        -String eventName
        +getRegistrationId() int
        +getStudentId() String
        +getEventId() String
        +toString() String
    }

    class Attendance {
        -int attendanceId
        -int registrationId
        -String studentId
        -String studentName
        -String attendanceStatus
        -Integer feedbackScore
        -String remarks
        +getAttendanceId() int
        +getAttendanceStatus() String
        +getFeedbackScore() Integer
        +toString() String
    }

    class EventReport {
        -String eventId
        -String eventName
        -String eventType
        -int totalRegistrations
        -int totalPresent
        -int totalAbsent
        -double attendanceRate
        -double avgFeedback
        +toString() String
    }

    class EventService {
        -StudentDAO studentDAO
        -EventDAO eventDAO
        -RegistrationDAO registrationDAO
        -AttendanceDAO attendanceDAO
        -ReportDAO reportDAO
        +registerNewStudent(Student) boolean
        +createNewEvent(Event) boolean
        +registerStudentForEvent(String, String) String
        +recordStudentAttendance(int, String, Integer, String) String
        +generateEventSummaryReports() List~EventReport~
        +generateDepartmentSummary() List~String[]~
    }

    class DBConnection {
        -String DB_FILE$
        -String JDBC_URL$
        +getConnection()$ Connection
        +initializeDatabaseIfMissing()$ void
    }

    EventService ..> Student : uses
    EventService ..> Event : uses
    EventService ..> Registration : uses
    EventService ..> Attendance : uses
    EventService ..> EventReport : generates
    Student "1" -- "0..*" Registration : registers for
    Event "1" -- "0..*" Registration : receives
    Registration "1" -- "0..1" Attendance : tracks
```

---

## 4. Sequence Diagram: Student Registration with Duplicate & Capacity Validation

This sequence diagram depicts the chronological step-by-step verification flow when a student requests an event registration.

```mermaid
sequenceDiagram
    autonumber
    actor User as User / Student
    participant Main as Main (CLI)
    participant Service as EventService
    participant RegDAO as RegistrationDAO
    participant EventDAO as EventDAO
    participant DB as SQLite Database

    User->>Main: Enter Student ID ('25IT001') and Event ID ('EVT101')
    Main->>Service: registerStudentForEvent("25IT001", "EVT101")
    
    Service->>RegDAO: isAlreadyRegistered("25IT001", "EVT101")
    RegDAO->>DB: SELECT COUNT(*) FROM registrations WHERE student_id=? AND event_id=?
    DB-->>RegDAO: Count Result (e.g. 1)
    RegDAO-->>Service: true (Duplicate Found)
    
    alt If Duplicate Detected
        Service-->>Main: "DUPLICATE: Student already registered"
        Main-->>User: Display Warning Banner [Duplicate Registration Prevented]
    else If No Duplicate
        Service->>EventDAO: getCurrentRegistrationCount("EVT101")
        EventDAO->>DB: SELECT COUNT(*) FROM registrations WHERE event_id=?
        DB-->>EventDAO: currentCount (8)
        EventDAO-->>Service: 8 (Max: 12)
        
        alt If Capacity Available
            Service->>RegDAO: registerStudent("25IT001", "EVT101")
            RegDAO->>DB: INSERT INTO registrations VALUES (...)
            DB-->>RegDAO: 1 row affected
            RegDAO-->>Service: true
            Service-->>Main: "SUCCESS: Successfully registered"
            Main-->>User: Display Green Success Banner
        else If Capacity Exceeded
            Service-->>Main: "FULL: Event reached maximum capacity"
            Main-->>User: Display Error Banner [Event Full]
        end
    end
```
