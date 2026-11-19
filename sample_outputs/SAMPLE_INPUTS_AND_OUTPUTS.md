# SAMPLE INPUTS AND OUTPUTS
## Smart Campus Event Management and Student Participation Analysis System

**Institution**: Velalar College of Engineering and Technology (Autonomous), Erode – 638012  
**Department**: Department of Information Technology  
**Scenario**: Scenario 6 (Smart Campus Event Management and Participation Analysis)  

---

## 1. Sample Input 1: Adding a New Student (FR 1)
```text
Select an option (0-6): 1
Choice (0-3): 1
Enter Student ID (e.g. 25IT010): 25IT007
Enter Full Name: Kavitha S
Enter Department (e.g. Information Technology): Information Technology
Enter Year of Study (1-4): 3
Enter Email: kavitha.it@vcet.ac.in
Enter Phone Number: 9876543230
```
### Output:
```text
[SUCCESS] Student 'Kavitha S' registered successfully!
```

---

## 2. Sample Output 2: Listing All Students
```text
--- ALL ENROLLED STUDENTS (21) ---
ID         | Name                 | Department                       | Year    | Email                     | Phone
---------------------------------------------------------------------------------------------------------------------
25AD001    | Meera Krishnan       | Artificial Intelligence & DS     | Year 2  | meera.ad@vcet.ac.in       | 9876543222
25AD002    | Naveen Prakash       | Artificial Intelligence & DS     | Year 3  | naveen.ad@vcet.ac.in      | 9876543223
25AD003    | Oviya Selvan         | Artificial Intelligence & DS     | Year 1  | oviya.ad@vcet.ac.in       | 9876543224
25CE001    | Roshini Balan        | Civil Engineering                | Year 3  | roshini.ce@vcet.ac.in     | 9876543227
25CE002    | Siddharth Mani       | Civil Engineering                | Year 4  | siddharth.ce@vcet.ac.in   | 9876543228
25CS001    | Farhan Ali           | Computer Science                 | Year 3  | farhan.cs@vcet.ac.in      | 9876543215
25CS002    | Gayathri Priya       | Computer Science                 | Year 2  | gayathri.cs@vcet.ac.in    | 9876543216
25CS003    | Harish Varma         | Computer Science                 | Year 4  | harish.cs@vcet.ac.in      | 9876543217
25CS004    | Ishwarya Lakshmi     | Computer Science                 | Year 3  | ishwarya.cs@vcet.ac.in    | 9876543218
25EC001    | Jagadeesh Paul       | Electronics and Communication    | Year 2  | jagadeesh.ec@vcet.ac.in   | 9876543219
25EC002    | Keerthana Devi       | Electronics and Communication    | Year 3  | keerthana.ec@vcet.ac.in   | 9876543220
25EC003    | Lokesh Nathan        | Electronics and Communication    | Year 4  | lokesh.ec@vcet.ac.in      | 9876543221
25IT001    | Aarav Sharma         | Information Technology           | Year 3  | aarav.it@vcet.ac.in       | 9876543210
25IT002    | Bhavana Ramesh       | Information Technology           | Year 3  | bhavana.it@vcet.ac.in     | 9876543211
25IT003    | Chandran Kumar       | Information Technology           | Year 2  | chandran.it@vcet.ac.in    | 9876543212
25IT004    | Deepika Sundar       | Information Technology           | Year 4  | deepika.it@vcet.ac.in     | 9876543213
25IT005    | Elango Muthu         | Information Technology           | Year 1  | elango.it@vcet.ac.in      | 9876543214
25IT006    | Tharun Prasad        | Information Technology           | Year 2  | tharun.it@vcet.ac.in      | 9876543229
25IT007    | Kavitha S            | Information Technology           | Year 3  | kavitha.it@vcet.ac.in     | 9876543230
25ME001    | Pradeep Raj          | Mechanical Engineering           | Year 3  | pradeep.me@vcet.ac.in     | 9876543225
25ME002    | Qadir Basha          | Mechanical Engineering           | Year 2  | qadir.me@vcet.ac.in       | 9876543226
```

---

## 3. Sample Input 3: Event Registration & Duplicate Prevention (FR 3)
### Case A: Attempting Duplicate Registration
```text
Select an option (0-6): 3
Choice (0-2): 1
Enter Student ID (e.g. 25IT001): 25IT001
Enter Event ID (e.g. EVT101): EVT101
```
#### Output (Duplicate Intercepted & Prevented):
```text
[WARNING] [PREVENTED DUPLICATE] DUPLICATE: Student 'Aarav Sharma (25IT001)' is already registered for 'CodeQuest National Hackathon'.
```

### Case B: Successful Registration
```text
Select an option (0-6): 3
Choice (0-2): 1
Enter Student ID (e.g. 25IT001): 25IT007
Enter Event ID (e.g. EVT101): EVT107
```
#### Output:
```text
[SUCCESS] SUCCESS: Successfully registered Kavitha S for Web3 & Smart Contracts Summit.
```

---

## 4. Sample Output 4: Registered Participants for an Event (FR 5)
```text
--- REGISTERED PARTICIPANTS FOR: CodeQuest National Hackathon (8/12) ---
Reg ID | Student ID | Student Name           | Department                     | Status    
------------------------------------------------------------------------------------------
1      | 25IT001    | Aarav Sharma           | Information Technology         | Confirmed 
2      | 25IT002    | Bhavana Ramesh         | Information Technology         | Confirmed 
3      | 25CS001    | Farhan Ali             | Computer Science               | Confirmed 
4      | 25CS003    | Harish Varma           | Computer Science               | Confirmed 
5      | 25AD001    | Meera Krishnan         | Artificial Intelligence & DS   | Confirmed 
6      | 25AD002    | Naveen Prakash         | Artificial Intelligence & DS   | Confirmed 
7      | 25EC002    | Keerthana Devi         | Electronics and Communication  | Confirmed 
8      | 25IT004    | Deepika Sundar         | Information Technology         | Confirmed 
```

---

## 5. Sample Input 5: Marking Attendance & Recording Feedback (FR 4)
```text
Select an option (0-6): 4
Choice (0-2): 1
Enter Registration ID: 1
Select Status (1. Present, 2. Absent): 1
Enter Feedback Rating (1-5): 5
Enter Participant Feedback Remarks: Outstanding coding session
```
### Output:
```text
[SUCCESS] SUCCESS: Attendance recorded as 'Present' for Student ID: 25IT001 (Aarav Sharma).
```

---

## 6. Sample Output 6: Event-Wise Comprehensive Report (FR 5)
```text
--- EVENT-WISE COMPREHENSIVE PARTICIPATION REPORT ---
Event ID | Event Name                     | Type       | Registered | Present | Absent  | Att Rate | Avg Rating
-----------------------------------------------------------------------------------------------------------------
EVT101   | CodeQuest National Hackathon   | Technical  |  8 / 12    | 6       | 2       |  75.0%   | 4.50 / 5
EVT102   | Hands-on Generative AI & LLMs  | Workshop   |  9 / 15    | 8       | 1       |  88.9%   | 4.62 / 5
EVT103   | Future of Cloud Infrastructure | Seminar    |  7 / 20    | 5       | 2       |  71.4%   | 4.00 / 5
EVT104   | Tarang Inter-College Cultural  | Cultural   | 10 / 30    | 9       | 1       |  90.0%   | 4.67 / 5
EVT105   | Internet of Things Workshop    | Workshop   |  5 / 10    | 4       | 1       |  80.0%   | 4.50 / 5
EVT106   | Cyber Forensics & Threat Def   | Technical  |  5 / 12    | 4       | 1       |  80.0%   | 4.75 / 5
EVT107   | Web3 & Smart Contracts Summit  | Workshop   |  1 / 15    | 0       | 0       |   0.0%   | 0.00 / 5
```

---

## 7. Sample Output 7: Department-Wise Participation Distribution
```text
--- DEPARTMENT-WISE PARTICIPATION DISTRIBUTION ---
Department                             | Total Students | Registrations    | Attended Events
------------------------------------------------------------------------------------------
Information Technology                 | 7              | 16               | 14
Computer Science                       | 4              | 9                | 7
Artificial Intelligence & Data Science | 3              | 7                | 6
Electronics and Communication          | 3              | 7                | 6
Mechanical Engineering                 | 2              | 4                | 3
Civil Engineering                      | 2              | 3                | 2
```
