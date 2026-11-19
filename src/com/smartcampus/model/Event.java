package com.smartcampus.model;

/**
 * Model class representing a Campus Event (Workshop, Seminar, Technical, Cultural).
 */
public class Event {
    private String eventId;
    private String eventName;
    private String eventType;
    private String eventDate;
    private String venue;
    private int maxParticipants;
    private String description;

    public Event() {}

    public Event(String eventId, String eventName, String eventType, String eventDate, String venue, int maxParticipants, String description) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventType = eventType;
        this.eventDate = eventDate;
        this.venue = venue;
        this.maxParticipants = maxParticipants;
        this.description = description;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("%-8s | %-32s | %-12s | %-10s | %-24s | Max: %3d",
                eventId, eventName, eventType, eventDate, venue, maxParticipants);
    }
}
