package com.iabdinur.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record NewsletterSubscriptionDTO(
    String id,
    String email,
    String status,
    String subscribedAt,
    Map<String, Object> preferences
) {
    public static NewsletterSubscriptionDTO fromEntity(com.iabdinur.model.NewsletterSubscription subscription) {
        Map<String, Object> preferences = Map.of(
            "frequency", subscription.getFrequency() != null ? subscription.getFrequency() : "weekly",
            "categories", subscription.getCategories() != null ? subscription.getCategories() : List.of()
        );

        return new NewsletterSubscriptionDTO(
            subscription.getId().toString(),
            subscription.getEmail(),
            subscription.getStatus(),
            subscription.getSubscribedAt() != null ? subscription.getSubscribedAt().toString() : null,
            preferences
        );
    }
}
