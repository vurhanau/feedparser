package crawler.bsuir.by.feedparser.rss;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.jsoup.nodes.Element;

import crawler.bsuir.by.feedparser.Util;

public class RssFeed {

    private final static String descriptionStart = "<p align=\"left\"";
    private final static String descriptionStop = "</p>";

    private final String title;
    private final String link;
    private final String description;
    private final long pubDate;

    public RssFeed(Element rssElement) {
        if (!validate(rssElement))
            throw new IllegalArgumentException("invalid rss feed");

        this.title = Util.html2text(text(rssElement, "title"));
        this.description = Util.html2text(description(text(rssElement, "description")));
        this.pubDate = date(rssElement, "dc:date");
        this.link = text(rssElement, "guid");
    }

    public RssFeed(String title, String link, String description, long pubDate) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.pubDate = pubDate;
    }

    private static long date(Element parent, String tagName) {
        String dateStr = parent.getElementsByTag(tagName).first().text();
        DateTime dt = new DateTime(dateStr, DateTimeZone.UTC);
        return dt.getMillis();
    }

    private static String text(Element parent, String tagName) {
        return parent.getElementsByTag(tagName).first().text().trim();
    }

    private static boolean nonempty(Element parent, String tagName) {
        return parent.getElementsByTag(tagName).size() > 0;
    }

    private static String description(String str) {
        int offset = str.indexOf(descriptionStart) + descriptionStart.length() + 1;
        return str.substring(offset, str.indexOf(descriptionStop)).trim();
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public long pubDate() {
        return pubDate;
    }

    public String link() {
        return link;
    }

    private boolean validate(Element rssElement) {
        return rssElement != null
                && nonempty(rssElement, "title")
                && nonempty(rssElement, "description")
                && nonempty(rssElement, "guid")
                && nonempty(rssElement, "dc:date");
    }

    @Override
    public int hashCode() {
        return (int) (pubDate ^ (pubDate >>> 32));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof RssFeed)) return false;
        RssFeed that = (RssFeed) obj;

        return pubDate == that.pubDate;
    }

    @Override
    public String toString() {
        return "[" + pubDate + "] - '" + title + "'" + link;
    }
}
