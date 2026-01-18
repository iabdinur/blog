package com.iabdinur.controller;

import com.iabdinur.dto.NewsletterSubscriptionDTO;
import com.iabdinur.dto.SubscribeNewsletterRequest;
import com.iabdinur.dto.UpdateNewsletterPreferencesRequest;
import com.iabdinur.service.NewsletterService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/newsletter")
public class NewsletterController {

    private final NewsletterService newsletterService;

    public NewsletterController(NewsletterService newsletterService) {
        this.newsletterService = newsletterService;
    }

    @PostMapping("/subscribe")
    public ResponseEntity<NewsletterSubscriptionDTO> subscribe(@Valid @RequestBody SubscribeNewsletterRequest request) {
        NewsletterSubscriptionDTO subscription = newsletterService.subscribe(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<Void> unsubscribe(@Valid @RequestBody SubscribeNewsletterRequest request) {
        newsletterService.unsubscribe(request.email());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/subscription/{email}")
    public ResponseEntity<NewsletterSubscriptionDTO> getSubscription(@PathVariable String email) {
        return newsletterService.getSubscription(email)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/subscription/{email}")
    public ResponseEntity<NewsletterSubscriptionDTO> updatePreferences(
            @PathVariable String email,
            @RequestBody UpdateNewsletterPreferencesRequest request) {
        NewsletterSubscriptionDTO subscription = newsletterService.updatePreferences(email, request);
        return ResponseEntity.ok(subscription);
    }
}
