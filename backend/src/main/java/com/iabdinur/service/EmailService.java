package com.iabdinur.service;

public interface EmailService {
    /**
     * Sends a verification code email to the specified recipient.
     *
     * @param to      The recipient email address
     * @param code    The verification code to send
     * @param expiresInMinutes The number of minutes until the code expires
     */
    void sendVerificationCode(String to, String code, int expiresInMinutes);
    
    /**
     * Sends a post notification email to newsletter subscribers when a new post is published.
     *
     * @param to      The recipient email address
     * @param postTitle The title of the published post
     * @param postSlug The slug of the published post (for URL generation)
     * @param postExcerpt The excerpt of the post
     */
    void sendPostNotification(String to, String postTitle, String postSlug, String postExcerpt);
}
