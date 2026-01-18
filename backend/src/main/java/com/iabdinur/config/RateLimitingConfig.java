package com.iabdinur.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class RateLimitingConfig implements WebMvcConfigurer {

    @Bean
    public RateLimitingInterceptor rateLimitingInterceptor() {
        return new RateLimitingInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitingInterceptor())
            .addPathPatterns("/api/v1/**")
            .excludePathPatterns("/api/v1/posts/**/views"); // Exclude view tracking from rate limiting
    }

    public static class RateLimitingInterceptor implements HandlerInterceptor {
        // Store request counts per IP address
        private final Map<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();
        
        // Rate limit configuration
        private static final int MAX_REQUESTS_PER_MINUTE = 60;
        private static final int MAX_REQUESTS_PER_HOUR = 1000;
        private static final long MINUTE_WINDOW = 60 * 1000; // 1 minute in milliseconds
        private static final long HOUR_WINDOW = 60 * 60 * 1000; // 1 hour in milliseconds

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            String clientIp = getClientIp(request);
            RequestCounter counter = requestCounts.computeIfAbsent(clientIp, k -> new RequestCounter());
            
            long currentTime = System.currentTimeMillis();
            
            // Clean up old entries
            counter.cleanup(currentTime);
            
            // Check rate limits
            if (counter.getMinuteCount() >= MAX_REQUESTS_PER_MINUTE) {
                response.setStatus(429); // 429 Too Many Requests
                response.setHeader("X-RateLimit-Limit", String.valueOf(MAX_REQUESTS_PER_MINUTE));
                response.setHeader("X-RateLimit-Remaining", "0");
                response.setHeader("Retry-After", "60");
                return false;
            }
            
            if (counter.getHourCount() >= MAX_REQUESTS_PER_HOUR) {
                response.setStatus(429); // 429 Too Many Requests
                response.setHeader("X-RateLimit-Limit", String.valueOf(MAX_REQUESTS_PER_HOUR));
                response.setHeader("X-RateLimit-Remaining", "0");
                response.setHeader("Retry-After", "3600");
                return false;
            }
            
            // Increment counters
            counter.increment(currentTime);
            
            // Set rate limit headers
            response.setHeader("X-RateLimit-Limit-Minute", String.valueOf(MAX_REQUESTS_PER_MINUTE));
            response.setHeader("X-RateLimit-Remaining-Minute", String.valueOf(MAX_REQUESTS_PER_MINUTE - counter.getMinuteCount()));
            response.setHeader("X-RateLimit-Limit-Hour", String.valueOf(MAX_REQUESTS_PER_HOUR));
            response.setHeader("X-RateLimit-Remaining-Hour", String.valueOf(MAX_REQUESTS_PER_HOUR - counter.getHourCount()));
            
            return true;
        }

        private String getClientIp(HttpServletRequest request) {
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                return xForwardedFor.split(",")[0].trim();
            }
            String xRealIp = request.getHeader("X-Real-IP");
            if (xRealIp != null && !xRealIp.isEmpty()) {
                return xRealIp;
            }
            return request.getRemoteAddr();
        }

        private static class RequestCounter {
            private final AtomicInteger minuteCount = new AtomicInteger(0);
            private final AtomicInteger hourCount = new AtomicInteger(0);
            private long minuteWindowStart = System.currentTimeMillis();
            private long hourWindowStart = System.currentTimeMillis();

            public void increment(long currentTime) {
                // Reset if window expired
                if (currentTime - minuteWindowStart >= MINUTE_WINDOW) {
                    minuteCount.set(0);
                    minuteWindowStart = currentTime;
                }
                if (currentTime - hourWindowStart >= HOUR_WINDOW) {
                    hourCount.set(0);
                    hourWindowStart = currentTime;
                }
                
                minuteCount.incrementAndGet();
                hourCount.incrementAndGet();
            }

            public int getMinuteCount() {
                return minuteCount.get();
            }

            public int getHourCount() {
                return hourCount.get();
            }

            public void cleanup(long currentTime) {
                // Reset expired windows
                if (currentTime - minuteWindowStart >= MINUTE_WINDOW) {
                    minuteCount.set(0);
                    minuteWindowStart = currentTime;
                }
                if (currentTime - hourWindowStart >= HOUR_WINDOW) {
                    hourCount.set(0);
                    hourWindowStart = currentTime;
                }
            }
        }
    }
}
