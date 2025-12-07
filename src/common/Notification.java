package common;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String title;
    private final String message;
    private final LocalDateTime at;

    public Notification(String title, String message) {
        this.title = title; this.message = message; this.at = LocalDateTime.now();
    }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public LocalDateTime getAt() { return at; }
    @Override
    public String toString() {
        return "["+at+"] "+title+" - "+message;
    }
}
