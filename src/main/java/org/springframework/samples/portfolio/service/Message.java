package org.springframework.samples.portfolio.service;

/**
 * Created by RWorsnop on 1/9/14.
 */
public class Message {

    private String message;
    private String recipient;

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Message{" +
                "message='" + message + '\'' +
                ", recipient='" + recipient + '\'' +
                '}';
    }
}
