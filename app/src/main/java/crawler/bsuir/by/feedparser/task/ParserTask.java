package crawler.bsuir.by.feedparser.task;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

import crawler.bsuir.by.feedparser.rss.RssFeed;
import crawler.bsuir.by.feedparser.rss.RssFeedAggregator;

public class ParserTask {

    private final static String url = "http://www.bsuir.by/rss?rubid=102243&resid=100229";
    private Exception ex;

    public RssFeedAggregator get() {
        try {
            Connection connection = Jsoup.connect(url);
            Document doc = connection.get();
            Element root = doc.body().getElementsByTag("channel").first();
            List<RssFeed> feed = new ArrayList<>();
            for (Element item : root.getElementsByTag("item")) {
                try {
                    feed.add(new RssFeed(item));
                } catch (Exception e) {
                    // bsuir format is a piece of shit
                }
            }

            return new RssFeedAggregator(feed);
        } catch (Exception ex) {
            this.ex = ex;
            return RssFeedAggregator.empty();
        }
    }

    public Exception error() {
        return ex;
    }
}
