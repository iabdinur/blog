package com.iabdinur.dto;

import java.util.List;
import java.util.Map;

public record UpdateNewsletterPreferencesRequest(
    Map<String, Object> preferences
) {
    public String frequency() {
        if (preferences == null || !preferences.containsKey("frequency")) {
            return "weekly";
        }
        Object freq = preferences.get("frequency");
        return freq != null ? freq.toString() : "weekly";
    }

    @SuppressWarnings("unchecked")
    public List<String> categories() {
        if (preferences == null || !preferences.containsKey("categories")) {
            return List.of();
        }
        Object cats = preferences.get("categories");
        if (cats instanceof List) {
            return (List<String>) cats;
        }
        return List.of();
    }
}
