package com.iabdinur.repository;

import com.iabdinur.dao.NewsletterSubscriptionDao;
import com.iabdinur.model.NewsletterSubscription;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public class NewsletterSubscriptionJDBCDataAccessService implements NewsletterSubscriptionDao {

    private final JdbcTemplate jdbcTemplate;

    public NewsletterSubscriptionJDBCDataAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<NewsletterSubscription> selectSubscriptionByEmail(String email) {
        var sql = """
                SELECT id, email, status, frequency, categories, subscribed_at, unsubscribed_at, updated_at
                FROM newsletter_subscriptions
                WHERE email = ?
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            NewsletterSubscription subscription = new NewsletterSubscription();
            subscription.setId(rs.getLong("id"));
            subscription.setEmail(rs.getString("email"));
            subscription.setStatus(rs.getString("status"));
            subscription.setFrequency(rs.getString("frequency"));
            
            // Handle PostgreSQL array
            java.sql.Array categoriesArray = rs.getArray("categories");
            if (categoriesArray != null) {
                String[] categories = (String[]) categoriesArray.getArray();
                subscription.setCategories(Arrays.asList(categories));
            } else {
                subscription.setCategories(java.util.Collections.emptyList());
            }
            
            Timestamp subscribedAt = rs.getTimestamp("subscribed_at");
            subscription.setSubscribedAt(subscribedAt != null ? subscribedAt.toLocalDateTime() : LocalDateTime.now());
            
            Timestamp unsubscribedAt = rs.getTimestamp("unsubscribed_at");
            subscription.setUnsubscribedAt(unsubscribedAt != null ? unsubscribedAt.toLocalDateTime() : null);
            
            Timestamp updatedAt = rs.getTimestamp("updated_at");
            subscription.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : LocalDateTime.now());
            
            return subscription;
        }, email).stream().findFirst();
    }

    @Override
    public void insertSubscription(NewsletterSubscription subscription) {
        var sql = """
                INSERT INTO newsletter_subscriptions(email, status, frequency, categories, subscribed_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, subscription.getEmail());
            ps.setString(2, subscription.getStatus());
            ps.setString(3, subscription.getFrequency());
            
            // Handle PostgreSQL array
            if (subscription.getCategories() != null && !subscription.getCategories().isEmpty()) {
                ps.setArray(4, connection.createArrayOf("TEXT", subscription.getCategories().toArray()));
            } else {
                ps.setArray(4, connection.createArrayOf("TEXT", new String[0]));
            }
            
            ps.setTimestamp(5, Timestamp.valueOf(
                subscription.getSubscribedAt() != null ? subscription.getSubscribedAt() : LocalDateTime.now()));
            ps.setTimestamp(6, Timestamp.valueOf(
                subscription.getUpdatedAt() != null ? subscription.getUpdatedAt() : LocalDateTime.now()));
            return ps;
        }, keyHolder);
        
        Long id = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
        subscription.setId(id);
    }

    @Override
    public void updateSubscription(NewsletterSubscription subscription) {
        var sql = """
                UPDATE newsletter_subscriptions
                SET status = ?, frequency = ?, categories = ?, unsubscribed_at = ?, updated_at = ?
                WHERE email = ?
                """;
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, subscription.getStatus());
            ps.setString(2, subscription.getFrequency());
            
            // Handle PostgreSQL array
            if (subscription.getCategories() != null && !subscription.getCategories().isEmpty()) {
                ps.setArray(3, connection.createArrayOf("TEXT", subscription.getCategories().toArray()));
            } else {
                ps.setArray(3, connection.createArrayOf("TEXT", new String[0]));
            }
            
            if (subscription.getUnsubscribedAt() != null) {
                ps.setTimestamp(4, Timestamp.valueOf(subscription.getUnsubscribedAt()));
            } else {
                ps.setTimestamp(4, null);
            }
            
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(6, subscription.getEmail());
            return ps;
        });
    }

    @Override
    public boolean existsSubscriptionByEmail(String email) {
        var sql = """
                SELECT count(id)
                FROM newsletter_subscriptions
                WHERE email = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public List<NewsletterSubscription> selectActiveSubscriptions() {
        var sql = """
                SELECT id, email, status, frequency, categories, subscribed_at, unsubscribed_at, updated_at
                FROM newsletter_subscriptions
                WHERE status = 'active'
                ORDER BY subscribed_at DESC
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            NewsletterSubscription subscription = new NewsletterSubscription();
            subscription.setId(rs.getLong("id"));
            subscription.setEmail(rs.getString("email"));
            subscription.setStatus(rs.getString("status"));
            subscription.setFrequency(rs.getString("frequency"));
            
            // Handle PostgreSQL array
            java.sql.Array categoriesArray = rs.getArray("categories");
            if (categoriesArray != null) {
                String[] categories = (String[]) categoriesArray.getArray();
                subscription.setCategories(Arrays.asList(categories));
            } else {
                subscription.setCategories(java.util.Collections.emptyList());
            }
            
            Timestamp subscribedAt = rs.getTimestamp("subscribed_at");
            subscription.setSubscribedAt(subscribedAt != null ? subscribedAt.toLocalDateTime() : LocalDateTime.now());
            
            Timestamp unsubscribedAt = rs.getTimestamp("unsubscribed_at");
            subscription.setUnsubscribedAt(unsubscribedAt != null ? unsubscribedAt.toLocalDateTime() : null);
            
            Timestamp updatedAt = rs.getTimestamp("updated_at");
            subscription.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : LocalDateTime.now());
            
            return subscription;
        });
    }
}
