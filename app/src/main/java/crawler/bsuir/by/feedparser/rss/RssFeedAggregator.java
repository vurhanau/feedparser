package crawler.bsuir.by.feedparser.rss;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RssFeedAggregator implements Iterable<RssFeed> {
    private final static RssFeedAggregator EMPTY = new RssFeedAggregator(new ArrayList<RssFeed>(0));
    private final List<RssFeed> news;
    private final Map<Long, RssFeed> map;

    public RssFeedAggregator(Collection<? extends RssFeed> news) {
        this.news = new ArrayList<>(news);
        this.map = new HashMap<>(news.size());

        for(int i = 0; i < news.size(); i++) {
            RssFeed item = this.news.get(i);
            map.put(item.pubDate(), item);
        }
    }

    public RssFeed get(int pos) {
        return news.get(pos);
    }

    public List<RssFeed> news() {
        return news;
    }

    public static RssFeedAggregator empty() {
        return EMPTY;
    }

    @Override
    public Iterator<RssFeed> iterator() {
        return news.iterator();
    }
}
