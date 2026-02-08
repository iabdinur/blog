package com.iabdinur.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Utility class for testing email HTML content
 */
public class EmailTestUtil {

    /**
     * Assert that HTML contains the verification code
     */
    public static void assertEmailContainsCode(String html, String code) {
        Document doc = Jsoup.parse(html);
        String text = doc.text();
        assertThat(text).contains(code);
    }

    /**
     * Assert that HTML is valid email structure
     */
    public static void assertValidEmailHtml(String html) {
        Document doc = Jsoup.parse(html);
        assertThat(doc.select("html")).isNotEmpty();
        assertThat(doc.select("head")).isNotEmpty();
        assertThat(doc.select("body")).isNotEmpty();
        assertThat(doc.select("meta[charset]")).isNotEmpty();
    }

    /**
     * Assert that email has unsubscribe link
     */
    public static void assertEmailHasUnsubscribeLink(String html) {
        Document doc = Jsoup.parse(html);
        Elements links = doc.select("a[href*='unsubscribe']");
        assertThat(links).isNotEmpty();
    }

    /**
     * Assert that email contains specific text
     */
    public static void assertEmailContainsText(String html, String expectedText) {
        Document doc = Jsoup.parse(html);
        String text = doc.text();
        assertThat(text).contains(expectedText);
    }

    /**
     * Assert that HTML does not contain unsafe content
     */
    public static void assertEmailEscapesHtml(String html) {
        Document doc = Jsoup.parse(html);
        // Check that script tags are escaped and not executable
        Elements scripts = doc.select("script");
        assertThat(scripts).isEmpty(); // No executable script tags
    }

    /**
     * Assert that email has proper styling
     */
    public static void assertEmailHasInlineStyles(String html) {
        Document doc = Jsoup.parse(html);
        Elements styles = doc.select("style");
        assertThat(styles).isNotEmpty();
    }

    /**
     * Extract specific element text by CSS selector
     */
    public static String extractTextBySelector(String html, String cssSelector) {
        Document doc = Jsoup.parse(html);
        Element element = doc.selectFirst(cssSelector);
        return element != null ? element.text() : "";
    }

    /**
     * Assert that email contains a clickable link with specific text
     */
    public static void assertEmailHasLink(String html, String linkText) {
        Document doc = Jsoup.parse(html);
        Elements links = doc.select("a:contains(" + linkText + ")");
        assertThat(links).isNotEmpty();
    }

    /**
     * Get all href attributes from links
     */
    public static java.util.List<String> extractAllLinks(String html) {
        Document doc = Jsoup.parse(html);
        Elements links = doc.select("a[href]");
        return links.stream()
            .map(link -> link.attr("href"))
            .toList();
    }

    /**
     * Assert email footer contains current year
     */
    public static void assertEmailFooterHasCurrentYear(String html, int expectedYear) {
        Document doc = Jsoup.parse(html);
        String footerText = doc.select(".footer").text();
        assertThat(footerText).contains(String.valueOf(expectedYear));
    }
}
