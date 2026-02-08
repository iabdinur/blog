package com.iabdinur.service;

import com.iabdinur.dao.SentEmailDao;
import com.iabdinur.util.EmailTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailTemplateRenderingTest {

    private SesEmailService underTest;
    
    @Mock
    private SentEmailDao sentEmailDao;
    
    private TemplateEngine templateEngine;

    @BeforeEach
    void setUp() {
        // Create service with email disabled to test template rendering
        underTest = new SesEmailService(
            "noreply@test.com",
            "",
            "",
            "us-east-2",
            false, // disabled - so we can capture template output
            sentEmailDao
        );
        underTest.init();
        
        // Get the template engine using reflection
        templateEngine = (TemplateEngine) ReflectionTestUtils.getField(underTest, "templateEngine");
    }

    @Test
    void shouldRenderVerificationEmailWithCode() {
        // Given
        String code = "123456";
        int expiresInMinutes = 10;
        
        Context context = new Context();
        context.setVariable("code", code);
        context.setVariable("expiresInMinutes", expiresInMinutes);

        // When
        String html = templateEngine.process("verification", context);

        // Then
        EmailTestUtil.assertValidEmailHtml(html);
        EmailTestUtil.assertEmailContainsCode(html, code);
        EmailTestUtil.assertEmailContainsText(html, "10 minutes");
        EmailTestUtil.assertEmailContainsText(html, "Verification Code");
        EmailTestUtil.assertEmailHasInlineStyles(html);
    }

    @Test
    void shouldRenderVerificationEmailWithSpecialCharacters() {
        // Given
        String code = "A&B<C>123";
        
        Context context = new Context();
        context.setVariable("code", code);
        context.setVariable("expiresInMinutes", 10);

        // When
        String html = templateEngine.process("verification", context);

        // Then
        EmailTestUtil.assertValidEmailHtml(html);
        EmailTestUtil.assertEmailEscapesHtml(html);
        // Code should be displayed (Thymeleaf escapes HTML by default)
    }

    @Test
    void shouldRenderPostNotificationWithExcerpt() {
        // Given
        String postTitle = "My Amazing Blog Post";
        String postExcerpt = "This is a great introduction to the post";
        String postUrl = "https://blog.iabdinur.com/post/my-amazing-blog-post";
        String unsubscribeUrl = "https://blog.iabdinur.com/newsletter?unsubscribe=true";
        
        Context context = new Context();
        context.setVariable("postTitle", postTitle);
        context.setVariable("postExcerpt", postExcerpt);
        context.setVariable("postUrl", postUrl);
        context.setVariable("unsubscribeUrl", unsubscribeUrl);

        // When
        String html = templateEngine.process("post-notification", context);

        // Then
        EmailTestUtil.assertValidEmailHtml(html);
        EmailTestUtil.assertEmailContainsText(html, postTitle);
        EmailTestUtil.assertEmailContainsText(html, postExcerpt);
        EmailTestUtil.assertEmailHasUnsubscribeLink(html);
        EmailTestUtil.assertEmailHasLink(html, "Read Full Post");
    }

    @Test
    void shouldRenderPostNotificationWithoutExcerpt() {
        // Given
        String postTitle = "My Post";
        String postUrl = "https://blog.iabdinur.com/post/my-post";
        String unsubscribeUrl = "https://blog.iabdinur.com/newsletter?unsubscribe=true";
        
        Context context = new Context();
        context.setVariable("postTitle", postTitle);
        context.setVariable("postExcerpt", null);
        context.setVariable("postUrl", postUrl);
        context.setVariable("unsubscribeUrl", unsubscribeUrl);

        // When
        String html = templateEngine.process("post-notification", context);

        // Then
        EmailTestUtil.assertValidEmailHtml(html);
        EmailTestUtil.assertEmailContainsText(html, postTitle);
        assertThat(html).doesNotContain("null");
    }

    @Test
    void shouldEscapeHtmlInPostExcerpt() {
        // Given
        String postTitle = "Security Post";
        String maliciousExcerpt = "<script>alert('xss')</script><img src=x onerror=alert(1)>";
        String postUrl = "https://blog.iabdinur.com/post/security";
        String unsubscribeUrl = "https://blog.iabdinur.com/newsletter?unsubscribe=true";
        
        Context context = new Context();
        context.setVariable("postTitle", postTitle);
        context.setVariable("postExcerpt", maliciousExcerpt);
        context.setVariable("postUrl", postUrl);
        context.setVariable("unsubscribeUrl", unsubscribeUrl);

        // When
        String html = templateEngine.process("post-notification", context);

        // Then
        EmailTestUtil.assertValidEmailHtml(html);
        EmailTestUtil.assertEmailEscapesHtml(html); // No executable scripts
        // Thymeleaf should escape HTML by default - verify escaped entities are present
        assertThat(html).contains("&lt;script&gt;"); // <script> is escaped
        assertThat(html).contains("&lt;img"); // <img is escaped
        assertThat(html).doesNotContain("<script>"); // No executable script tag
    }

    @Test
    void shouldHandleLongPostTitle() {
        // Given
        String longTitle = "A".repeat(200);
        String postUrl = "https://blog.iabdinur.com/post/long-title";
        String unsubscribeUrl = "https://blog.iabdinur.com/newsletter?unsubscribe=true";
        
        Context context = new Context();
        context.setVariable("postTitle", longTitle);
        context.setVariable("postExcerpt", "Excerpt");
        context.setVariable("postUrl", postUrl);
        context.setVariable("unsubscribeUrl", unsubscribeUrl);

        // When
        String html = templateEngine.process("post-notification", context);

        // Then
        EmailTestUtil.assertValidEmailHtml(html);
        EmailTestUtil.assertEmailContainsText(html, longTitle);
    }

    @Test
    void shouldRenderCurrentYearInFooter() {
        // Given
        int currentYear = Year.now().getValue();
        
        Context context = new Context();
        context.setVariable("code", "123456");
        context.setVariable("expiresInMinutes", 10);

        // When
        String html = templateEngine.process("verification", context);

        // Then
        EmailTestUtil.assertEmailFooterHasCurrentYear(html, currentYear);
    }

    @Test
    void shouldIncludeInlineStyles() {
        // Given
        Context context = new Context();
        context.setVariable("code", "123456");
        context.setVariable("expiresInMinutes", 10);

        // When
        String html = templateEngine.process("verification", context);

        // Then
        EmailTestUtil.assertEmailHasInlineStyles(html);
        assertThat(html).contains("<style>");
        assertThat(html).contains("font-family:");
        assertThat(html).contains("color:");
    }

    @Test
    void shouldRenderVerificationEmailWithDifferentExpirationTimes() {
        // Test with 5, 10, 15, 30 minutes
        int[] expirationTimes = {5, 10, 15, 30};
        
        for (int expiration : expirationTimes) {
            Context context = new Context();
            context.setVariable("code", "123456");
            context.setVariable("expiresInMinutes", expiration);

            // When
            String html = templateEngine.process("verification", context);

            // Then
            EmailTestUtil.assertEmailContainsText(html, expiration + " minutes");
        }
    }
}
