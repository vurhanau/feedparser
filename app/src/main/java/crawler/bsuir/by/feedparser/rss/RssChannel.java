package crawler.bsuir.by.feedparser.rss;

import org.jsoup.nodes.Element;

public class RssChannel {

    private final String title;
    private final String link;
    private final String description;

    public RssChannel(String title, String link, String description) {
        this.title = title;
        this.link = link;
        this.description = description;
    }

    public RssChannel(Element rssElement) {
        if(!validate(rssElement))
            throw new IllegalArgumentException("invalid rss feed");

        this.title = rssElement.getElementsByTag("title").first().text();
        this.link = rssElement.getElementsByTag("link").first().text();
        this.description = rssElement.getElementsByTag("description").first().text();
    }

    private boolean validate(Element xmlElement) {
        return xmlElement != null && xmlElement.getElementsByTag("title").size() > 0
                && xmlElement.getElementsByTag("link").size() > 0
                && xmlElement.getElementsByTag("description").size() > 0;
    }
}
