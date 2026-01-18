package com.iabdinur.service;

import com.iabdinur.dao.NewsletterSubscriptionDao;
import com.iabdinur.dto.NewsletterSubscriptionDTO;
import com.iabdinur.dto.SubscribeNewsletterRequest;
import com.iabdinur.dto.UpdateNewsletterPreferencesRequest;
import com.iabdinur.model.NewsletterSubscription;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class NewsletterService {

    private final NewsletterSubscriptionDao newsletterSubscriptionDao;

    public NewsletterService(NewsletterSubscriptionDao newsletterSubscriptionDao) {
        this.newsletterSubscriptionDao = newsletterSubscriptionDao;
    }

    @Transactional
    public NewsletterSubscriptionDTO subscribe(SubscribeNewsletterRequest request) {
        // Check if subscription already exists
        Optional<NewsletterSubscription> existing = newsletterSubscriptionDao.selectSubscriptionByEmail(request.email());
        
        if (existing.isPresent()) {
            NewsletterSubscription subscription = existing.get();
            // If unsubscribed, reactivate
            if ("unsubscribed".equals(subscription.getStatus())) {
                subscription.setStatus("active");
                subscription.setUnsubscribedAt(null);
                subscription.setUpdatedAt(LocalDateTime.now());
                newsletterSubscriptionDao.updateSubscription(subscription);
            }
            // If already active, return existing
            return NewsletterSubscriptionDTO.fromEntity(subscription);
        }

        // Create new subscription
        NewsletterSubscription subscription = new NewsletterSubscription(
            request.email(),
            "active",
            "weekly"
        );
        newsletterSubscriptionDao.insertSubscription(subscription);
        return NewsletterSubscriptionDTO.fromEntity(subscription);
    }

    @Transactional
    public void unsubscribe(String email) {
        Optional<NewsletterSubscription> subscriptionOpt = newsletterSubscriptionDao.selectSubscriptionByEmail(email);
        if (subscriptionOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Newsletter subscription not found");
        }

        NewsletterSubscription subscription = subscriptionOpt.get();
        subscription.setStatus("unsubscribed");
        subscription.setUnsubscribedAt(LocalDateTime.now());
        subscription.setUpdatedAt(LocalDateTime.now());
        newsletterSubscriptionDao.updateSubscription(subscription);
    }

    @Transactional(readOnly = true)
    public Optional<NewsletterSubscriptionDTO> getSubscription(String email) {
        return newsletterSubscriptionDao.selectSubscriptionByEmail(email)
            .map(NewsletterSubscriptionDTO::fromEntity);
    }

    @Transactional
    public NewsletterSubscriptionDTO updatePreferences(String email, UpdateNewsletterPreferencesRequest request) {
        Optional<NewsletterSubscription> subscriptionOpt = newsletterSubscriptionDao.selectSubscriptionByEmail(email);
        if (subscriptionOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Newsletter subscription not found");
        }

        NewsletterSubscription subscription = subscriptionOpt.get();
        subscription.setFrequency(request.frequency());
        subscription.setCategories(request.categories());
        subscription.setUpdatedAt(LocalDateTime.now());
        newsletterSubscriptionDao.updateSubscription(subscription);
        
        return NewsletterSubscriptionDTO.fromEntity(subscription);
    }
}
