package crawler.bsuir.by.feedparser.rss;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.jsoup.nodes.Element;

public class RssFeed {

    private final static String descriptionStart = "<p align=\"left\"";
    private final static String descriptionStop = "</p>";

    private final String title;
    private final String link;
    private final String description;
    private final String guid;
    private final long pubDate;

    public RssFeed(Element rssElement) {
        if (!validate(rssElement))
            throw new IllegalArgumentException("invalid rss feed");

        this.title = text(rssElement, "title");
        this.link = text(rssElement, "link");
        this.description = description(text(rssElement, "description"));
        this.pubDate = date(rssElement, "dc:date");
        this.guid = text(rssElement, "guid");
    }

    private static long date(Element parent, String tagName) {
        String dateStr = parent.getElementsByTag(tagName).first().text();
        DateTime dt = new DateTime(dateStr, DateTimeZone.UTC);
        return dt.getMillis();
    }

    private static String text(Element parent, String tagName) {
        return parent.getElementsByTag(tagName).first().text();
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

    public String guid() {
        return guid;
    }

    public String link() {
        return link;
    }

    private boolean validate(Element rssElement) {
        return rssElement != null
                && nonempty(rssElement, "title")
                && nonempty(rssElement, "link")
                && nonempty(rssElement, "description")
                && nonempty(rssElement, "pubDate")
                && nonempty(rssElement, "guid")
                && nonempty(rssElement, "dc:date");
    }

    @Override
    public int hashCode() {
        return guid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof RssFeed)) return false;
        RssFeed that = (RssFeed) obj;

        return guid.equals(that.guid);
    }

    @Override
    public String toString() {
        return "[" + pubDate + "] - '" + title + "'" + link;
    }
}