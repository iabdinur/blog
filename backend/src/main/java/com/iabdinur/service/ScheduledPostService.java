package com.iabdinur.service;

import com.iabdinur.dao.PostDao;
import com.iabdinur.dao.NewsletterSubscriptionDao;
import com.iabdinur.model.Post;
import com.iabdinur.model.NewsletterSubscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduledPostService {
    
    private static final Logger logger = LoggerFactory.getLogger(ScheduledPostService.class);
    
    private final PostDao postDao;
    private final NewsletterSubscriptionDao newsletterSubscriptionDao;
    private final EmailService emailService;
    private final PostService postService;
    
    public ScheduledPostService(
            PostDao postDao,
            NewsletterSubscriptionDao newsletterSubscriptionDao,
            EmailService emailService,
            PostService postService) {
        this.postDao = postDao;
        this.newsletterSubscriptionDao = newsletterSubscriptionDao;
        this.emailService = emailService;
        this.postService = postService;
    }
    
    /**
     * Check for scheduled posts every minute and publish them if their scheduled time has arrived.
     * Also sends email notifications to newsletter subscribers.
     */
    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    public void publishScheduledPosts() {
        try {
            List<Post> scheduledPosts = postDao.selectScheduledPostsReadyToPublish();
            
            if (scheduledPosts.isEmpty()) {
                return; // No posts to publish
            }
            
            logger.info("Found {} scheduled post(s) ready to publish", scheduledPosts.size());
            
            for (Post post : scheduledPosts) {
                try {
                    // Publish the post
                    postDao.updatePostPublishedStatus(
                        post.getId(),
                        true,
                        post.getScheduledAt() != null ? post.getScheduledAt() : LocalDateTime.now()
                    );
                    
                    logger.info("Published scheduled post: {} (slug: {})", post.getTitle(), post.getSlug());
                    
                    // Send email notifications to newsletter subscribers
                    sendPostNotificationEmails(post);
                    
                } catch (Exception e) {
                    logger.error("Failed to publish scheduled post: {} (slug: {})", 
                               post.getTitle(), post.getSlug(), e);
                }
            }
        } catch (Exception e) {
            logger.error("Error in scheduled post publishing task", e);
        }
    }
    
    private void sendPostNotificationEmails(Post post) {
        try {
            // Get all active newsletter subscribers
            List<NewsletterSubscription> subscribers = newsletterSubscriptionDao.selectActiveSubscriptions();
            
            if (subscribers.isEmpty()) {
                logger.info("No active newsletter subscribers to notify for post: {}", post.getSlug());
                return;
            }
            
            logger.info("Sending post notification emails to {} subscribers for post: {}", 
                       subscribers.size(), post.getSlug());
            
            int successCount = 0;
            int failureCount = 0;
            
            for (NewsletterSubscription subscriber : subscribers) {
                try {
                    emailService.sendPostNotification(
                        subscriber.getEmail(),
                        post.getTitle(),
                        post.getSlug(),
                        post.getExcerpt() != null ? post.getExcerpt() : ""
                    );
                    successCount++;
                } catch (Exception e) {
                    logger.error("Failed to send post notification email to: {}", 
                               subscriber.getEmail(), e);
                    failureCount++;
                }
            }
            
            logger.info("Post notification emails sent: {} successful, {} failed for post: {}", 
                       successCount, failureCount, post.getSlug());
            
        } catch (Exception e) {
            logger.error("Error sending post notification emails for post: {}", 
                        post.getSlug(), e);
        }
    }
}
