package com.smartcampus.model;

/**
 * DTO / Model class representing aggregated event analytics.
 */
public class EventReport {
    private String eventId;
    private String eventName;
    private String eventType;
    private String eventDate;
    private String venue;
    private int maxParticipants;
    private int totalRegistrations;
    private int totalPresent;
    private int totalAbsent;
    private double attendanceRate;
    private double avgFeedback;

    public EventReport() {}

    public EventReport(String eventId, String eventName, String eventType, String eventDate, 
                       String venue, int maxParticipants, int totalRegistrations, 
                       int totalPresent, int totalAbsent, double attendanceRate, double avgFeedback) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventType = eventType;
        this.eventDate = eventDate;
        this.venue = venue;
        this.maxParticipants = maxParticipants;
        this.totalRegistrations = totalRegistrations;
        this.totalPresent = totalPresent;
        this.totalAbsent = totalAbsent;
        this.attendanceRate = attendanceRate;
        this.avgFeedback = avgFeedback;
    }

    public String getEventId() { return eventId; }
    public String getEventName() { return eventName; }
    public String getEventType() { return eventType; }
    public String getEventDate() { return eventDate; }
    public String getVenue() { return venue; }
    public int getMaxParticipants() { return maxParticipants; }
    public int getTotalRegistrations() { return totalRegistrations; }
    public int getTotalPresent() { return totalPresent; }
    public int getTotalAbsent() { return totalAbsent; }
    public double getAttendanceRate() { return attendanceRate; }
    public double getAvgFeedback() { return avgFeedback; }

    @Override
    public String toString() {
        return String.format("%-8s | %-28s | %-10s | Reg: %2d/%2d | Pres: %2d | Abs: %2d | Rate: %5.1f%% | Rating: %.1f/5",
                eventId, eventName, eventType, totalRegistrations, maxParticipants,
                totalPresent, totalAbsent, attendanceRate, avgFeedback);
    }
}
