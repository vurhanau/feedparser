package crawler.bsuir.by.feedparser.rss;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class RssFeedAggregator implements Iterable<RssFeed> {
    private final static RssFeedAggregator EMPTY = new RssFeedAggregator(new ArrayList<RssFeed>(0));
    private final Collection<RssFeed> news;

    public RssFeedAggregator(Collection<? extends RssFeed> news) {
        this.news = new ArrayList<>(news);
    }

    public static RssFeedAggregator empty() {
        return EMPTY;
    }

    @Override
    public Iterator<RssFeed> iterator() {
        return news.iterator();
    }
}
