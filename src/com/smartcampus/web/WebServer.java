package com.smartcampus.web;

import com.smartcampus.model.*;
import com.smartcampus.service.EventService;
import com.smartcampus.util.DBConnection;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Embedded HTTP Web Server for the Smart Campus Event Management System.
 * Serves a modern web application dashboard at http://localhost:8080
 */
public class WebServer {
    private static final int PORT = 8080;
    private static final EventService service = new EventService();

    public static void main(String[] args) throws IOException {
        DBConnection.initializeDatabaseIfMissing();

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Web Dashboard UI
        server.createContext("/", new DashboardHandler());

        // REST API Endpoints
        server.createContext("/api/students", new StudentsApiHandler());
        server.createContext("/api/events", new EventsApiHandler());
        server.createContext("/api/registrations", new RegistrationsApiHandler());
        server.createContext("/api/register", new RegisterApiHandler());
        server.createContext("/api/attendance", new AttendanceApiHandler());
        server.createContext("/api/reports", new ReportsApiHandler());
        server.createContext("/api/run-analytics", new AnalyticsApiHandler());

        // Static Image File Server for Visualizations
        server.createContext("/visualizations/", new VisualizationsHandler());

        server.setExecutor(null); // default executor
        server.start();

        System.out.println("==================================================================");
        System.out.println("  SMART CAMPUS WEB SERVER STARTED");
        System.out.println("  URL: http://localhost:" + PORT);
        System.out.println("==================================================================");
    }

    // ------------------------------------------------------------------------
    // Static HTML Web Dashboard Handler
    // ------------------------------------------------------------------------
    static class DashboardHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestURI().getPath().equals("/")) {
                exchange.sendResponseHeaders(404, -1);
                return;
            }

            String html;
            File indexFile = new File("index.html");
            if (indexFile.exists()) {
                html = Files.readString(indexFile.toPath(), StandardCharsets.UTF_8);
            } else {
                html = getDashboardHtml();
            }
            byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    // ------------------------------------------------------------------------
    // API: Students Handler (GET & POST)
    // ------------------------------------------------------------------------
    static class StudentsApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                List<Student> list = service.getAllStudents();
                StringBuilder sb = new StringBuilder("[");
                for (int i = 0; i < list.size(); i++) {
                    Student s = list.get(i);
                    sb.append(String.format(
                            "{\"id\":\"%s\",\"name\":\"%s\",\"dept\":\"%s\",\"year\":%d,\"email\":\"%s\",\"phone\":\"%s\"}",
                            escapeJson(s.getStudentId()), escapeJson(s.getName()), escapeJson(s.getDepartment()),
                            s.getYearOfStudy(), escapeJson(s.getEmail()), escapeJson(s.getPhone())));
                    if (i < list.size() - 1) sb.append(",");
                }
                sb.append("]");
                sendResponse(exchange, 200, sb.toString());
            } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                Map<String, String> params = parseFormData(exchange);
                Student s = new Student(
                        params.get("id"),
                        params.get("name"),
                        params.get("dept"),
                        Integer.parseInt(params.getOrDefault("year", "1")),
                        params.get("email"),
                        params.get("phone")
                );
                boolean ok = service.registerNewStudent(s);
                if (ok) {
                    sendResponse(exchange, 200, "{\"success\":true,\"message\":\"Student added successfully\"}");
                } else {
                    sendResponse(exchange, 400, "{\"success\":false,\"message\":\"Student ID or Email already exists\"}");
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    // API: Events Handler (GET & POST)
    // ------------------------------------------------------------------------
    static class EventsApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                List<Event> list = service.getAllEvents();
                StringBuilder sb = new StringBuilder("[");
                for (int i = 0; i < list.size(); i++) {
                    Event e = list.get(i);
                    int currentRegs = service.getEventRegistrations(e.getEventId()).size();
                    sb.append(String.format(
                            "{\"id\":\"%s\",\"name\":\"%s\",\"type\":\"%s\",\"date\":\"%s\",\"venue\":\"%s\",\"max\":%d,\"registered\":%d,\"desc\":\"%s\"}",
                            escapeJson(e.getEventId()), escapeJson(e.getEventName()), escapeJson(e.getEventType()),
                            escapeJson(e.getEventDate()), escapeJson(e.getVenue()), e.getMaxParticipants(),
                            currentRegs, escapeJson(e.getDescription())));
                    if (i < list.size() - 1) sb.append(",");
                }
                sb.append("]");
                sendResponse(exchange, 200, sb.toString());
            } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                Map<String, String> params = parseFormData(exchange);
                Event ev = new Event(
                        params.get("id"),
                        params.get("name"),
                        params.get("type"),
                        params.get("date"),
                        params.get("venue"),
                        Integer.parseInt(params.getOrDefault("max", "20")),
                        params.get("desc")
                );
                boolean ok = service.createNewEvent(ev);
                if (ok) {
                    sendResponse(exchange, 200, "{\"success\":true,\"message\":\"Event created successfully\"}");
                } else {
                    sendResponse(exchange, 400, "{\"success\":false,\"message\":\"Event ID already exists\"}");
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    // API: Registrations Handler (GET by eventId)
    // ------------------------------------------------------------------------
    static class RegistrationsApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

            String query = exchange.getRequestURI().getQuery();
            String eventId = "EVT101";
            if (query != null && query.contains("eventId=")) {
                eventId = query.split("eventId=")[1].split("&")[0];
            }

            List<Registration> list = service.getEventRegistrations(eventId);
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                Registration r = list.get(i);
                sb.append(String.format(
                        "{\"regId\":%d,\"studentId\":\"%s\",\"studentName\":\"%s\",\"dept\":\"%s\",\"date\":\"%s\",\"status\":\"%s\"}",
                        r.getRegistrationId(), escapeJson(r.getStudentId()), escapeJson(r.getStudentName()),
                        escapeJson(r.getDepartment()), escapeJson(r.getRegistrationDate()), escapeJson(r.getStatus())));
                if (i < list.size() - 1) sb.append(",");
            }
            sb.append("]");
            sendResponse(exchange, 200, sb.toString());
        }
    }

    // ------------------------------------------------------------------------
    // API: Register Handler (POST with Duplicate Prevention)
    // ------------------------------------------------------------------------
    static class RegisterApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                Map<String, String> params = parseFormData(exchange);
                String studentId = params.get("studentId");
                String eventId = params.get("eventId");

                String result = service.registerStudentForEvent(studentId, eventId);
                boolean success = result.startsWith("SUCCESS");
                boolean duplicate = result.startsWith("DUPLICATE");
                boolean full = result.startsWith("FULL");

                String json = String.format("{\"success\":%b,\"duplicate\":%b,\"full\":%b,\"message\":\"%s\"}",
                        success, duplicate, full, escapeJson(result));
                sendResponse(exchange, 200, json);
            }
        }
    }

    // ------------------------------------------------------------------------
    // API: Attendance Handler (GET by eventId & POST)
    // ------------------------------------------------------------------------
    static class AttendanceApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                String eventId = "EVT101";
                if (query != null && query.contains("eventId=")) {
                    eventId = query.split("eventId=")[1].split("&")[0];
                }

                List<Attendance> list = service.getEventAttendance(eventId);
                StringBuilder sb = new StringBuilder("[");
                for (int i = 0; i < list.size(); i++) {
                    Attendance a = list.get(i);
                    sb.append(String.format(
                            "{\"attId\":%d,\"regId\":%d,\"studentId\":\"%s\",\"studentName\":\"%s\",\"status\":\"%s\",\"rating\":%s,\"remarks\":\"%s\",\"date\":\"%s\"}",
                            a.getAttendanceId(), a.getRegistrationId(), escapeJson(a.getStudentId()),
                            escapeJson(a.getStudentName()), escapeJson(a.getAttendanceStatus()),
                            (a.getFeedbackScore() != null ? a.getFeedbackScore() : "null"),
                            escapeJson(a.getRemarks()), escapeJson(a.getMarkedAt())));
                    if (i < list.size() - 1) sb.append(",");
                }
                sb.append("]");
                sendResponse(exchange, 200, sb.toString());
            } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                Map<String, String> params = parseFormData(exchange);
                int regId = Integer.parseInt(params.get("regId"));
                String status = params.get("status");
                String ratingStr = params.get("rating");
                Integer rating = (ratingStr != null && !ratingStr.isEmpty() && !"null".equals(ratingStr))
                        ? Integer.parseInt(ratingStr) : null;
                String remarks = params.get("remarks");

                String result = service.recordStudentAttendance(regId, status, rating, remarks);
                boolean success = result.startsWith("SUCCESS");
                String json = String.format("{\"success\":%b,\"message\":\"%s\"}", success, escapeJson(result));
                sendResponse(exchange, 200, json);
            }
        }
    }

    // ------------------------------------------------------------------------
    // API: Reports Handler (GET)
    // ------------------------------------------------------------------------
    static class ReportsApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

            List<EventReport> reports = service.generateEventSummaryReports();
            List<String[]> deptData = service.generateDepartmentSummary();

            StringBuilder sb = new StringBuilder("{\"events\":[");
            for (int i = 0; i < reports.size(); i++) {
                EventReport r = reports.get(i);
                sb.append(String.format(
                        "{\"id\":\"%s\",\"name\":\"%s\",\"type\":\"%s\",\"max\":%d,\"reg\":%d,\"present\":%d,\"absent\":%d,\"rate\":%.2f,\"rating\":%.2f}",
                        escapeJson(r.getEventId()), escapeJson(r.getEventName()), escapeJson(r.getEventType()),
                        r.getMaxParticipants(), r.getTotalRegistrations(), r.getTotalPresent(), r.getTotalAbsent(),
                        r.getAttendanceRate(), r.getAvgFeedback()));
                if (i < reports.size() - 1) sb.append(",");
            }
            sb.append("],\"departments\":[");
            for (int i = 0; i < deptData.size(); i++) {
                String[] d = deptData.get(i);
                sb.append(String.format(
                        "{\"dept\":\"%s\",\"students\":%s,\"registrations\":%s,\"attended\":%s}",
                        escapeJson(d[0]), d[1], d[2], d[3]));
                if (i < deptData.size() - 1) sb.append(",");
            }
            sb.append("]}");
            sendResponse(exchange, 200, sb.toString());
        }
    }

    // ------------------------------------------------------------------------
    // API: Run Analytics Pipeline
    // ------------------------------------------------------------------------
    static class AnalyticsApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

            try {
                ProcessBuilder pb = new ProcessBuilder("python", "data_analysis/analysis.py");
                Process p = pb.start();
                int exit = p.waitFor();
                if (exit == 0) {
                    sendResponse(exchange, 200, "{\"success\":true,\"message\":\"Visualizations generated successfully!\"}");
                } else {
                    sendResponse(exchange, 500, "{\"success\":false,\"message\":\"Python script failed with code " + exit + "\"}");
                }
            } catch (Exception e) {
                sendResponse(exchange, 500, "{\"success\":false,\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
        }
    }

    // ------------------------------------------------------------------------
    // Static Visualization Images Handler
    // ------------------------------------------------------------------------
    static class VisualizationsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String filename = path.replace("/visualizations/", "");
            File file = new File("data_analysis/visualizations/" + filename);

            if (!file.exists() || !file.isFile()) {
                exchange.sendResponseHeaders(404, -1);
                return;
            }

            exchange.getResponseHeaders().set("Content-Type", "image/png");
            exchange.sendResponseHeaders(200, file.length());
            try (OutputStream os = exchange.getResponseBody();
                 FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    // Utility Methods
    // ------------------------------------------------------------------------
    private static void sendResponse(HttpExchange exchange, int code, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static Map<String, String> parseFormData(HttpExchange exchange) throws IOException {
        Map<String, String> map = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String raw = sb.toString();
            String[] pairs = raw.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=");
                if (kv.length == 2) {
                    String k = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
                    String v = URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                    map.put(k, v);
                }
            }
        }
        return map;
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }

    // ------------------------------------------------------------------------
    // Single Page Application (SPA) HTML5 Dashboard
    // ------------------------------------------------------------------------
    private static String getDashboardHtml() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\" />\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
                "  <title>Smart Campus Event Management & Participation System | VCET</title>\n" +
                "  <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css\" rel=\"stylesheet\">\n" +
                "  <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css\">\n" +
                "  <style>\n" +
                "    body { background-color: #f8fafc; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }\n" +
                "    .header-banner { background: linear-gradient(135deg, #1e3a8a 0%, #0f172a 100%); color: white; padding: 25px 30px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1); }\n" +
                "    .nav-tabs .nav-link { color: #475569; font-weight: 600; padding: 12px 20px; border-radius: 8px 8px 0 0; }\n" +
                "    .nav-tabs .nav-link.active { color: #1e3a8a; border-bottom: 3px solid #2563eb; font-weight: 700; background: white; }\n" +
                "    .card { border: none; border-radius: 12px; box-shadow: 0 1px 3px 0 rgba(0,0,0,0.1); }\n" +
                "    .badge-cap { font-size: 0.85rem; padding: 6px 10px; border-radius: 6px; }\n" +
                "    .chart-card { transition: transform 0.2s; cursor: pointer; }\n" +
                "    .chart-card:hover { transform: translateY(-4px); box-shadow: 0 10px 15px -3px rgba(0,0,0,0.1); }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <!-- Header -->\n" +
                "  <div class=\"header-banner mb-4\">\n" +
                "    <div class=\"container-fluid\">\n" +
                "      <div class=\"d-flex justify-content-between align-items-center\">\n" +
                "        <div>\n" +
                "          <h3 class=\"fw-bold mb-1\"><i class=\"fa-solid fa-graduation-cap me-2\"></i>VELALAR COLLEGE OF ENGINEERING AND TECHNOLOGY</h3>\n" +
                "          <p class=\"mb-0 text-slate-300\">Department of Information Technology | Scenario 6: Smart Campus Event Management & Participation Analysis</p>\n" +
                "        </div>\n" +
                "        <div>\n" +
                "          <span class=\"badge bg-success px-3 py-2 fs-6\"><i class=\"fa-solid fa-circle-check me-1\"></i> System Active: localhost:8080</span>\n" +
                "        </div>\n" +
                "      </div>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "\n" +
                "  <div class=\"container-fluid px-4\">\n" +
                "    <!-- Nav Tabs -->\n" +
                "    <ul class=\"nav nav-tabs mb-4\" id=\"mainTabs\" role=\"tablist\">\n" +
                "      <li class=\"nav-item\"><button class=\"nav-link active\" id=\"students-tab\" data-bs-toggle=\"tab\" data-bs-target=\"#students\" type=\"button\"><i class=\"fa-solid fa-users me-2\"></i>Students</button></li>\n" +
                "      <li class=\"nav-item\"><button class=\"nav-link\" id=\"events-tab\" data-bs-toggle=\"tab\" data-bs-target=\"#events\" type=\"button\"><i class=\"fa-solid fa-calendar-days me-2\"></i>Campus Events</button></li>\n" +
                "      <li class=\"nav-item\"><button class=\"nav-link\" id=\"reg-tab\" data-bs-toggle=\"tab\" data-bs-target=\"#registration\" type=\"button\"><i class=\"fa-solid fa-clipboard-check me-2\"></i>Event Registration</button></li>\n" +
                "      <li class=\"nav-item\"><button class=\"nav-link\" id=\"att-tab\" data-bs-toggle=\"tab\" data-bs-target=\"#attendance\" type=\"button\"><i class=\"fa-solid fa-user-check me-2\"></i>Attendance & Feedback</button></li>\n" +
                "      <li class=\"nav-item\"><button class=\"nav-link\" id=\"rep-tab\" data-bs-toggle=\"tab\" data-bs-target=\"#reports\" type=\"button\"><i class=\"fa-solid fa-chart-pie me-2\"></i>Reports & Analytics</button></li>\n" +
                "    </ul>\n" +
                "\n" +
                "    <!-- Tab Panes -->\n" +
                "    <div class=\"tab-content\" id=\"tabContent\">\n" +
                "      <!-- TAB 1: STUDENTS -->\n" +
                "      <div class=\"tab-pane fade show active\" id=\"students\" role=\"tabpanel\">\n" +
                "        <div class=\"row g-4\">\n" +
                "          <div class=\"col-lg-4\">\n" +
                "            <div class=\"card p-4\">\n" +
                "              <h5 class=\"fw-bold mb-3 text-primary\"><i class=\"fa-solid fa-user-plus me-2\"></i>Add New Student</h5>\n" +
                "              <form id=\"studentForm\">\n" +
                "                <div class=\"mb-2\"><label class=\"form-label fw-semibold\">Student ID</label><input type=\"text\" class=\"form-control\" id=\"stuId\" placeholder=\"e.g. 25IT010\" required></div>\n" +
                "                <div class=\"mb-2\"><label class=\"form-label fw-semibold\">Full Name</label><input type=\"text\" class=\"form-control\" id=\"stuName\" placeholder=\"Student Name\" required></div>\n" +
                "                <div class=\"mb-2\"><label class=\"form-label fw-semibold\">Department</label>\n" +
                "                  <select class=\"form-select\" id=\"stuDept\">\n" +
                "                    <option>Information Technology</option><option>Computer Science</option>\n" +
                "                    <option>Artificial Intelligence & Data Science</option><option>Electronics and Communication</option>\n" +
                "                    <option>Mechanical Engineering</option><option>Civil Engineering</option>\n" +
                "                  </select>\n" +
                "                </div>\n" +
                "                <div class=\"mb-2\"><label class=\"form-label fw-semibold\">Year of Study</label>\n" +
                "                  <select class=\"form-select\" id=\"stuYear\"><option value=\"1\">1st Year</option><option value=\"2\">2nd Year</option><option value=\"3\" selected>3rd Year</option><option value=\"4\">4th Year</option></select>\n" +
                "                </div>\n" +
                "                <div class=\"mb-2\"><label class=\"form-label fw-semibold\">Email Address</label><input type=\"email\" class=\"form-control\" id=\"stuEmail\" placeholder=\"name@vcet.ac.in\" required></div>\n" +
                "                <div class=\"mb-3\"><label class=\"form-label fw-semibold\">Phone Number</label><input type=\"text\" class=\"form-control\" id=\"stuPhone\" placeholder=\"9876543210\" required></div>\n" +
                "                <button type=\"submit\" class=\"btn btn-primary w-100 fw-bold\"><i class=\"fa-solid fa-save me-1\"></i> Register Student</button>\n" +
                "              </form>\n" +
                "            </div>\n" +
                "          </div>\n" +
                "          <div class=\"col-lg-8\">\n" +
                "            <div class=\"card p-4\">\n" +
                "              <div class=\"d-flex justify-content-between align-items-center mb-3\">\n" +
                "                <h5 class=\"fw-bold mb-0\"><i class=\"fa-solid fa-address-book me-2\"></i>Enrolled Students List</h5>\n" +
                "                <button class=\"btn btn-outline-secondary btn-sm\" onclick=\"loadStudents()\"><i class=\"fa-solid fa-rotate me-1\"></i>Refresh</button>\n" +
                "              </div>\n" +
                "              <div class=\"table-responsive\" style=\"max-height: 520px;\">\n" +
                "                <table class=\"table table-hover align-middle\">\n" +
                "                  <thead class=\"table-light\"><tr><th>ID</th><th>Name</th><th>Department</th><th>Year</th><th>Email</th><th>Phone</th></tr></thead>\n" +
                "                  <tbody id=\"studentTableBody\"></tbody>\n" +
                "                </table>\n" +
                "              </div>\n" +
                "            </div>\n" +
                "          </div>\n" +
                "        </div>\n" +
                "      </div>\n" +
                "\n" +
                "      <!-- TAB 2: EVENTS -->\n" +
                "      <div class=\"tab-pane fade\" id=\"events\" role=\"tabpanel\">\n" +
                "        <div class=\"row g-4\">\n" +
                "          <div class=\"col-lg-4\">\n" +
                "            <div class=\"card p-4\">\n" +
                "              <h5 class=\"fw-bold mb-3 text-primary\"><i class=\"fa-solid fa-calendar-plus me-2\"></i>Create Campus Event</h5>\n" +
                "              <form id=\"eventForm\">\n" +
                "                <div class=\"mb-2\"><label class=\"form-label fw-semibold\">Event ID</label><input type=\"text\" class=\"form-control\" id=\"evId\" placeholder=\"e.g. EVT108\" required></div>\n" +
                "                <div class=\"mb-2\"><label class=\"form-label fw-semibold\">Event Name</label><input type=\"text\" class=\"form-control\" id=\"evName\" placeholder=\"Event Title\" required></div>\n" +
                "                <div class=\"mb-2\"><label class=\"form-label fw-semibold\">Category</label>\n" +
                "                  <select class=\"form-select\" id=\"evType\">\n" +
                "                    <option>Technical</option><option>Workshop</option><option>Seminar</option><option>Cultural</option>\n" +
                "                  </select>\n" +
                "                </div>\n" +
                "                <div class=\"mb-2\"><label class=\"form-label fw-semibold\">Event Date</label><input type=\"date\" class=\"form-control\" id=\"evDate\" value=\"2026-12-05\" required></div>\n" +
                "                <div class=\"mb-2\"><label class=\"form-label fw-semibold\">Venue</label><input type=\"text\" class=\"form-control\" id=\"evVenue\" placeholder=\"Hall / Lab\" required></div>\n" +
                "                <div class=\"mb-2\"><label class=\"form-label fw-semibold\">Max Capacity</label><input type=\"number\" class=\"form-control\" id=\"evMax\" min=\"1\" max=\"500\" value=\"20\" required></div>\n" +
                "                <div class=\"mb-3\"><label class=\"form-label fw-semibold\">Description</label><textarea class=\"form-control\" id=\"evDesc\" rows=\"2\"></textarea></div>\n" +
                "                <button type=\"submit\" class=\"btn btn-primary w-100 fw-bold\"><i class=\"fa-solid fa-plus-circle me-1\"></i> Add Event</button>\n" +
                "              </form>\n" +
                "            </div>\n" +
                "          </div>\n" +
                "          <div class=\"col-lg-8\">\n" +
                "            <div class=\"card p-4\">\n" +
                "              <div class=\"d-flex justify-content-between align-items-center mb-3\">\n" +
                "                <h5 class=\"fw-bold mb-0\"><i class=\"fa-solid fa-calendar-check me-2\"></i>Scheduled Events</h5>\n" +
                "                <button class=\"btn btn-outline-secondary btn-sm\" onclick=\"loadEvents()\"><i class=\"fa-solid fa-rotate me-1\"></i>Refresh</button>\n" +
                "              </div>\n" +
                "              <div class=\"table-responsive\" style=\"max-height: 520px;\">\n" +
                "                <table class=\"table table-hover align-middle\">\n" +
                "                  <thead class=\"table-light\"><tr><th>ID</th><th>Event Name</th><th>Category</th><th>Date</th><th>Venue</th><th>Capacity Utilization</th></tr></thead>\n" +
                "                  <tbody id=\"eventTableBody\"></tbody>\n" +
                "                </table>\n" +
                "              </div>\n" +
                "            </div>\n" +
                "          </div>\n" +
                "        </div>\n" +
                "      </div>\n" +
                "\n" +
                "      <!-- TAB 3: REGISTRATION -->\n" +
                "      <div class=\"tab-pane fade\" id=\"registration\" role=\"tabpanel\">\n" +
                "        <div class=\"card p-4 mb-4\">\n" +
                "          <h5 class=\"fw-bold text-primary mb-3\"><i class=\"fa-solid fa-shield-halved me-2\"></i>Student Event Registration (Duplicate-Protected)</h5>\n" +
                "          <form id=\"regForm\" class=\"row g-3 align-items-end\">\n" +
                "            <div class=\"col-md-5\"><label class=\"form-label fw-semibold\">Select Student</label><select class=\"form-select\" id=\"regStuSelect\" required></select></div>\n" +
                "            <div class=\"col-md-5\"><label class=\"form-label fw-semibold\">Select Event</label><select class=\"form-select\" id=\"regEvSelect\" required></select></div>\n" +
                "            <div class=\"col-md-2\"><button type=\"submit\" class=\"btn btn-success w-100 fw-bold\"><i class=\"fa-solid fa-user-plus me-1\"></i> Register</button></div>\n" +
                "          </form>\n" +
                "          <div id=\"regAlert\" class=\"mt-3 d-none\"></div>\n" +
                "        </div>\n" +
                "        <div class=\"card p-4\">\n" +
                "          <div class=\"d-flex justify-content-between align-items-center mb-3\">\n" +
                "            <div class=\"d-flex align-items-center gap-2\">\n" +
                "              <h5 class=\"fw-bold mb-0\">Registered Participants for:</h5>\n" +
                "              <select class=\"form-select form-select-sm\" id=\"filterEventSelect\" style=\"width: 320px;\" onchange=\"loadRegistrations()\"></select>\n" +
                "            </div>\n" +
                "            <button class=\"btn btn-outline-secondary btn-sm\" onclick=\"loadRegistrations()\"><i class=\"fa-solid fa-rotate me-1\"></i>Refresh</button>\n" +
                "          </div>\n" +
                "          <div class=\"table-responsive\" style=\"max-height: 400px;\">\n" +
                "            <table class=\"table table-hover align-middle\">\n" +
                "              <thead class=\"table-light\"><tr><th>Reg ID</th><th>Student ID</th><th>Name</th><th>Department</th><th>Date</th><th>Status</th></tr></thead>\n" +
                "              <tbody id=\"regTableBody\"></tbody>\n" +
                "            </table>\n" +
                "          </div>\n" +
                "        </div>\n" +
                "      </div>\n" +
                "\n" +
                "      <!-- TAB 4: ATTENDANCE -->\n" +
                "      <div class=\"tab-pane fade\" id=\"attendance\" role=\"tabpanel\">\n" +
                "        <div class=\"card p-4 mb-4\">\n" +
                "          <h5 class=\"fw-bold text-primary mb-3\"><i class=\"fa-solid fa-clipboard-user me-2\"></i>Record Student Attendance & Feedback</h5>\n" +
                "          <form id=\"attForm\" class=\"row g-3 align-items-end\">\n" +
                "            <div class=\"col-md-3\"><label class=\"form-label fw-semibold\">Select Event</label><select class=\"form-select\" id=\"attEvSelect\" onchange=\"onAttEventChange()\" required></select></div>\n" +
                "            <div class=\"col-md-3\"><label class=\"form-label fw-semibold\">Participant</label><select class=\"form-select\" id=\"attRegSelect\" required></select></div>\n" +
                "            <div class=\"col-md-2\"><label class=\"form-label fw-semibold\">Status</label><select class=\"form-select\" id=\"attStatusSelect\"><option value=\"Present\">Present</option><option value=\"Absent\">Absent</option></select></div>\n" +
                "            <div class=\"col-md-2\"><label class=\"form-label fw-semibold\">Rating</label><select class=\"form-select\" id=\"attRatingSelect\"><option value=\"5\">5 - Outstanding</option><option value=\"4\">4 - Very Good</option><option value=\"3\">3 - Good</option><option value=\"2\">2 - Fair</option><option value=\"1\">1 - Poor</option></select></div>\n" +
                "            <div class=\"col-md-2\"><button type=\"submit\" class=\"btn btn-primary w-100 fw-bold\"><i class=\"fa-solid fa-check me-1\"></i> Save</button></div>\n" +
                "            <div class=\"col-12\"><input type=\"text\" class=\"form-control\" id=\"attRemarks\" placeholder=\"Optional feedback remarks or reason for absence\"></div>\n" +
                "          </form>\n" +
                "          <div id=\"attAlert\" class=\"mt-3 d-none\"></div>\n" +
                "        </div>\n" +
                "        <div class=\"card p-4\">\n" +
                "          <h5 class=\"fw-bold mb-3\"><i class=\"fa-solid fa-list-check me-2\"></i>Attendance Verification Sheet</h5>\n" +
                "          <div class=\"table-responsive\" style=\"max-height: 400px;\">\n" +
                "            <table class=\"table table-hover align-middle\">\n" +
                "              <thead class=\"table-light\"><tr><th>Att ID</th><th>Reg ID</th><th>Student ID</th><th>Name</th><th>Status</th><th>Rating</th><th>Remarks</th><th>Marked At</th></tr></thead>\n" +
                "              <tbody id=\"attTableBody\"></tbody>\n" +
                "            </table>\n" +
                "          </div>\n" +
                "        </div>\n" +
                "      </div>\n" +
                "\n" +
                "      <!-- TAB 5: REPORTS & VISUALS -->\n" +
                "      <div class=\"tab-pane fade\" id=\"reports\" role=\"tabpanel\">\n" +
                "        <div class=\"d-flex justify-content-between align-items-center mb-4\">\n" +
                "          <h5 class=\"fw-bold mb-0\"><i class=\"fa-solid fa-chart-line me-2\"></i>Institutional Participation Reports & Visual Analytics</h5>\n" +
                "          <button class=\"btn btn-purple text-white fw-bold\" style=\"background-color: #7c3aed;\" onclick=\"triggerAnalytics()\"><i class=\"fa-solid fa-bolt me-1\"></i> Run Python Data Science Pipeline</button>\n" +
                "        </div>\n" +
                "        <div class=\"row g-4 mb-4\">\n" +
                "          <div class=\"col-lg-8\">\n" +
                "            <div class=\"card p-4 h-100\">\n" +
                "              <h6 class=\"fw-bold mb-3 text-secondary\">Event-Wise Participation Performance</h6>\n" +
                "              <div class=\"table-responsive\">\n" +
                "                <table class=\"table table-sm table-bordered align-middle\">\n" +
                "                  <thead class=\"table-light\"><tr><th>Event ID</th><th>Event Name</th><th>Category</th><th>Registered</th><th>Present</th><th>Absent</th><th>Att Rate</th><th>Avg Rating</th></tr></thead>\n" +
                "                  <tbody id=\"repEventTableBody\"></tbody>\n" +
                "                </table>\n" +
                "              </div>\n" +
                "            </div>\n" +
                "          </div>\n" +
                "          <div class=\"col-lg-4\">\n" +
                "            <div class=\"card p-4 h-100\">\n" +
                "              <h6 class=\"fw-bold mb-3 text-secondary\">Department-Wise Distribution</h6>\n" +
                "              <div class=\"table-responsive\">\n" +
                "                <table class=\"table table-sm table-hover align-middle\">\n" +
                "                  <thead class=\"table-light\"><tr><th>Department</th><th>Students</th><th>Registrations</th><th>Attended</th></tr></thead>\n" +
                "                  <tbody id=\"repDeptTableBody\"></tbody>\n" +
                "                </table>\n" +
                "              </div>\n" +
                "            </div>\n" +
                "          </div>\n" +
                "        </div>\n" +
                "        <!-- Python Visualizations Gallery -->\n" +
                "        <h5 class=\"fw-bold mb-3 text-primary\"><i class=\"fa-solid fa-chart-column me-2\"></i>Python Generated Visualization Suite (Matplotlib & Seaborn)</h5>\n" +
                "        <div class=\"row g-4\" id=\"visGallery\">\n" +
                "          <div class=\"col-md-6\"><div class=\"card p-2\"><h6 class=\"fw-semibold text-center mt-2\">1. Department-Wise Participation</h6><img src=\"/visualizations/01_department_participation_barchart.png\" class=\"img-fluid rounded\" alt=\"Chart 1\"></div></div>\n" +
                "          <div class=\"col-md-6\"><div class=\"card p-2\"><h6 class=\"fw-semibold text-center mt-2\">2. Capacity vs Attendance</h6><img src=\"/visualizations/02_event_attendance_comparison.png\" class=\"img-fluid rounded\" alt=\"Chart 2\"></div></div>\n" +
                "          <div class=\"col-md-6\"><div class=\"card p-2\"><h6 class=\"fw-semibold text-center mt-2\">3. Event Type Distribution</h6><img src=\"/visualizations/03_event_type_distribution_piechart.png\" class=\"img-fluid rounded\" alt=\"Chart 3\"></div></div>\n" +
                "          <div class=\"col-md-6\"><div class=\"card p-2\"><h6 class=\"fw-semibold text-center mt-2\">4. Year of Study Participation</h6><img src=\"/visualizations/04_year_wise_participation_countplot.png\" class=\"img-fluid rounded\" alt=\"Chart 4\"></div></div>\n" +
                "          <div class=\"col-md-6\"><div class=\"card p-2\"><h6 class=\"fw-semibold text-center mt-2\">5. Feedback Ratings by Event Type</h6><img src=\"/visualizations/05_feedback_rating_boxplot.png\" class=\"img-fluid rounded\" alt=\"Chart 5\"></div></div>\n" +
                "          <div class=\"col-md-6\"><div class=\"card p-2\"><h6 class=\"fw-semibold text-center mt-2\">6. Participation Correlation Matrix</h6><img src=\"/visualizations/06_participation_correlation_heatmap.png\" class=\"img-fluid rounded\" alt=\"Chart 6\"></div></div>\n" +
                "        </div>\n" +
                "      </div>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "\n" +
                "  <script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js\"></script>\n" +
                "  <script>\n" +
                "    let cachedStudents = [];\n" +
                "    let cachedEvents = [];\n" +
                "\n" +
                "    async function loadStudents() {\n" +
                "      const res = await fetch('/api/students');\n" +
                "      cachedStudents = await res.json();\n" +
                "      const tbody = document.getElementById('studentTableBody');\n" +
                "      tbody.innerHTML = '';\n" +
                "      const sel = document.getElementById('regStuSelect');\n" +
                "      sel.innerHTML = '';\n" +
                "      cachedStudents.forEach(s => {\n" +
                "        tbody.innerHTML += `<tr><td class=\"fw-bold\">${s.id}</td><td>${s.name}</td><td>${s.dept}</td><td><span class=\"badge bg-secondary\">Year ${s.year}</span></td><td>${s.email}</td><td>${s.phone}</td></tr>`;\n" +
                "        sel.innerHTML += `<option value=\"${s.id}\">${s.id} - ${s.name} (${s.dept})</option>`;\n" +
                "      });\n" +
                "    }\n" +
                "\n" +
                "    async function loadEvents() {\n" +
                "      const res = await fetch('/api/events');\n" +
                "      cachedEvents = await res.json();\n" +
                "      const tbody = document.getElementById('eventTableBody');\n" +
                "      tbody.innerHTML = '';\n" +
                "      const regEvSel = document.getElementById('regEvSelect');\n" +
                "      const filterEvSel = document.getElementById('filterEventSelect');\n" +
                "      const attEvSel = document.getElementById('attEvSelect');\n" +
                "      regEvSel.innerHTML = ''; filterEvSel.innerHTML = ''; attEvSel.innerHTML = '';\n" +
                "      cachedEvents.forEach(e => {\n" +
                "        const pct = Math.min(100, Math.round((e.registered / e.max) * 100));\n" +
                "        tbody.innerHTML += `<tr><td class=\"fw-bold\">${e.id}</td><td>${e.name}</td><td><span class=\"badge bg-info text-dark\">${e.type}</span></td><td>${e.date}</td><td>${e.venue}</td><td><div class=\"d-flex align-items-center gap-2\"><div class=\"progress flex-grow-1\" style=\"height: 8px;\"><div class=\"progress-bar bg-success\" style=\"width: ${pct}%\"></div></div><small class=\"fw-bold\">${e.registered}/${e.max}</small></div></td></tr>`;\n" +
                "        const opt = `<option value=\"${e.id}\">${e.id} - ${e.name}</option>`;\n" +
                "        regEvSel.innerHTML += opt;\n" +
                "        filterEvSel.innerHTML += opt;\n" +
                "        attEvSel.innerHTML += opt;\n" +
                "      });\n" +
                "      loadRegistrations();\n" +
                "      onAttEventChange();\n" +
                "    }\n" +
                "\n" +
                "    async function loadRegistrations() {\n" +
                "      const evId = document.getElementById('filterEventSelect').value || 'EVT101';\n" +
                "      const res = await fetch('/api/registrations?eventId=' + evId);\n" +
                "      const regs = await res.json();\n" +
                "      const tbody = document.getElementById('regTableBody');\n" +
                "      tbody.innerHTML = '';\n" +
                "      regs.forEach(r => {\n" +
                "        tbody.innerHTML += `<tr><td>${r.regId}</td><td class=\"fw-bold\">${r.studentId}</td><td>${r.studentName}</td><td>${r.dept}</td><td>${r.date}</td><td><span class=\"badge bg-success\">${r.status}</span></td></tr>`;\n" +
                "      });\n" +
                "    }\n" +
                "\n" +
                "    async function onAttEventChange() {\n" +
                "      const evId = document.getElementById('attEvSelect').value || 'EVT101';\n" +
                "      const res = await fetch('/api/registrations?eventId=' + evId);\n" +
                "      const regs = await res.json();\n" +
                "      const sel = document.getElementById('attRegSelect');\n" +
                "      sel.innerHTML = '';\n" +
                "      regs.forEach(r => {\n" +
                "        sel.innerHTML += `<option value=\"${r.regId}\">#${r.regId} - ${r.studentId}: ${r.studentName}</option>`;\n" +
                "      });\n" +
                "      loadAttendance();\n" +
                "    }\n" +
                "\n" +
                "    async function loadAttendance() {\n" +
                "      const evId = document.getElementById('attEvSelect').value || 'EVT101';\n" +
                "      const res = await fetch('/api/attendance?eventId=' + evId);\n" +
                "      const atts = await res.json();\n" +
                "      const tbody = document.getElementById('attTableBody');\n" +
                "      tbody.innerHTML = '';\n" +
                "      atts.forEach(a => {\n" +
                "        const badge = a.status === 'Present' ? '<span class=\"badge bg-success\">Present</span>' : '<span class=\"badge bg-danger\">Absent</span>';\n" +
                "        const star = a.rating ? `<span class=\"text-warning\">${'★'.repeat(a.rating)}</span> (${a.rating}/5)` : 'N/A';\n" +
                "        tbody.innerHTML += `<tr><td>${a.attId}</td><td>${a.regId}</td><td>${a.studentId}</td><td>${a.studentName}</td><td>${badge}</td><td>${star}</td><td>${a.remarks || ''}</td><td><small>${a.date}</small></td></tr>`;\n" +
                "      });\n" +
                "    }\n" +
                "\n" +
                "    async function loadReports() {\n" +
                "      const res = await fetch('/api/reports');\n" +
                "      const data = await res.json();\n" +
                "      const evTbody = document.getElementById('repEventTableBody');\n" +
                "      evTbody.innerHTML = '';\n" +
                "      data.events.forEach(e => {\n" +
                "        evTbody.innerHTML += `<tr><td class=\"fw-bold\">${e.id}</td><td>${e.name}</td><td><span class=\"badge bg-primary\">${e.type}</span></td><td>${e.reg} / ${e.max}</td><td class=\"text-success fw-bold\">${e.present}</td><td class=\"text-danger fw-bold\">${e.absent}</td><td><strong>${e.rate}%</strong></td><td>${e.rating} / 5</td></tr>`;\n" +
                "      });\n" +
                "      const deptTbody = document.getElementById('repDeptTableBody');\n" +
                "      deptTbody.innerHTML = '';\n" +
                "      data.departments.forEach(d => {\n" +
                "        deptTbody.innerHTML += `<tr><td class=\"fw-bold\">${d.dept}</td><td>${d.students}</td><td>${d.registrations}</td><td class=\"text-success fw-bold\">${d.attended}</td></tr>`;\n" +
                "      });\n" +
                "    }\n" +
                "\n" +
                "    // Forms\n" +
                "    document.getElementById('studentForm').addEventListener('submit', async (e) => {\n" +
                "      e.preventDefault();\n" +
                "      const data = new URLSearchParams({\n" +
                "        id: document.getElementById('stuId').value,\n" +
                "        name: document.getElementById('stuName').value,\n" +
                "        dept: document.getElementById('stuDept').value,\n" +
                "        year: document.getElementById('stuYear').value,\n" +
                "        email: document.getElementById('stuEmail').value,\n" +
                "        phone: document.getElementById('stuPhone').value\n" +
                "      });\n" +
                "      const res = await fetch('/api/students', { method: 'POST', body: data });\n" +
                "      const out = await res.json();\n" +
                "      alert(out.message);\n" +
                "      if (out.success) { document.getElementById('studentForm').reset(); loadStudents(); }\n" +
                "    });\n" +
                "\n" +
                "    document.getElementById('eventForm').addEventListener('submit', async (e) => {\n" +
                "      e.preventDefault();\n" +
                "      const data = new URLSearchParams({\n" +
                "        id: document.getElementById('evId').value,\n" +
                "        name: document.getElementById('evName').value,\n" +
                "        type: document.getElementById('evType').value,\n" +
                "        date: document.getElementById('evDate').value,\n" +
                "        venue: document.getElementById('evVenue').value,\n" +
                "        max: document.getElementById('evMax').value,\n" +
                "        desc: document.getElementById('evDesc').value\n" +
                "      });\n" +
                "      const res = await fetch('/api/events', { method: 'POST', body: data });\n" +
                "      const out = await res.json();\n" +
                "      alert(out.message);\n" +
                "      if (out.success) { document.getElementById('eventForm').reset(); loadEvents(); }\n" +
                "    });\n" +
                "\n" +
                "    document.getElementById('regForm').addEventListener('submit', async (e) => {\n" +
                "      e.preventDefault();\n" +
                "      const stuId = document.getElementById('regStuSelect').value;\n" +
                "      const evId = document.getElementById('regEvSelect').value;\n" +
                "      const data = new URLSearchParams({ studentId: stuId, eventId: evId });\n" +
                "      const res = await fetch('/api/register', { method: 'POST', body: data });\n" +
                "      const out = await res.json();\n" +
                "      const alertDiv = document.getElementById('regAlert');\n" +
                "      alertDiv.className = 'mt-3 alert ' + (out.success ? 'alert-success' : (out.duplicate ? 'alert-warning' : 'alert-danger'));\n" +
                "      alertDiv.innerHTML = `<strong>${out.success ? 'Success!' : (out.duplicate ? 'Duplicate Prevented!' : 'Error!')}</strong> ${out.message}`;\n" +
                "      alertDiv.classList.remove('d-none');\n" +
                "      if (out.success) { loadRegistrations(); loadEvents(); loadReports(); }\n" +
                "    });\n" +
                "\n" +
                "    document.getElementById('attForm').addEventListener('submit', async (e) => {\n" +
                "      e.preventDefault();\n" +
                "      const data = new URLSearchParams({\n" +
                "        regId: document.getElementById('attRegSelect').value,\n" +
                "        status: document.getElementById('attStatusSelect').value,\n" +
                "        rating: document.getElementById('attRatingSelect').value,\n" +
                "        remarks: document.getElementById('attRemarks').value\n" +
                "      });\n" +
                "      const res = await fetch('/api/attendance', { method: 'POST', body: data });\n" +
                "      const out = await res.json();\n" +
                "      const alertDiv = document.getElementById('attAlert');\n" +
                "      alertDiv.className = 'mt-3 alert ' + (out.success ? 'alert-success' : 'alert-danger');\n" +
                "      alertDiv.innerHTML = out.message;\n" +
                "      alertDiv.classList.remove('d-none');\n" +
                "      if (out.success) { loadAttendance(); loadReports(); }\n" +
                "    });\n" +
                "\n" +
                "    async function triggerAnalytics() {\n" +
                "      alert('Running Python Data Science pipeline...');\n" +
                "      const res = await fetch('/api/run-analytics', { method: 'POST' });\n" +
                "      const out = await res.json();\n" +
                "      alert(out.message);\n" +
                "      document.querySelectorAll('#visGallery img').forEach(img => {\n" +
                "        const src = img.src.split('?')[0];\n" +
                "        img.src = src + '?t=' + new Date().getTime();\n" +
                "      });\n" +
                "    }\n" +
                "\n" +
                "    // Init\n" +
                "    window.onload = () => {\n" +
                "      loadStudents();\n" +
                "      loadEvents();\n" +
                "      loadReports();\n" +
                "    };\n" +
                "  </script>\n" +
                "</body>\n" +
                "</html>";
    }
}
