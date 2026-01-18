package com.iabdinur.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(
    name = "sent_emails",
    indexes = {
        @Index(name = "idx_sent_emails_recipient", columnList = "recipient_email"),
        @Index(name = "idx_sent_emails_type", columnList = "email_type"),
        @Index(name = "idx_sent_emails_sent_at", columnList = "sent_at"),
        @Index(name = "idx_sent_emails_status", columnList = "status")
    }
)
public class SentEmail {

    @Id
    @SequenceGenerator(
        name = "sent_emails_id_seq",
        sequenceName = "sent_emails_id_seq",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "sent_emails_id_seq"
    )
    private Long id;

    @Column(name = "recipient_email", nullable = false, columnDefinition = "TEXT")
    private String recipientEmail;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String subject;

    @Column(name = "email_type", nullable = false, columnDefinition = "TEXT")
    private String emailType;

    @Column(name = "ses_message_id", columnDefinition = "TEXT")
    private String sesMessageId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String status;

    @Column(name = "sent_at", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime sentAt;

    @Column(name = "delivered_at", columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime deliveredAt;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    public SentEmail() {
    }

    public SentEmail(String recipientEmail, String subject, String emailType, 
                     String sesMessageId, String status) {
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.emailType = emailType;
        this.sesMessageId = sesMessageId;
        this.status = status;
        this.sentAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getEmailType() {
        return emailType;
    }

    public void setEmailType(String emailType) {
        this.emailType = emailType;
    }

    public String getSesMessageId() {
        return sesMessageId;
    }

    public void setSesMessageId(String sesMessageId) {
        this.sesMessageId = sesMessageId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SentEmail sentEmail = (SentEmail) o;
        return Objects.equals(id, sentEmail.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
