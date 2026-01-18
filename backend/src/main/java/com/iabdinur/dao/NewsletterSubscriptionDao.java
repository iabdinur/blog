package com.iabdinur.dao;

import com.iabdinur.model.NewsletterSubscription;

import java.util.List;
import java.util.Optional;

public interface NewsletterSubscriptionDao {
    Optional<NewsletterSubscription> selectSubscriptionByEmail(String email);
    void insertSubscription(NewsletterSubscription subscription);
    void updateSubscription(NewsletterSubscription subscription);
    boolean existsSubscriptionByEmail(String email);
    List<NewsletterSubscription> selectActiveSubscriptions();
}
