package crawler.bsuir.by.feedparser.task;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

import crawler.bsuir.by.feedparser.rss.RssFeed;
import crawler.bsuir.by.feedparser.rss.RssFeedAggregator;

public class ParserTask extends AsyncTask<ParserArg, ParserProgress, RssFeedAggregator> {

    private final static String url = "http://www.bsuir.by/rss?rubid=102243&resid=100229";
    private Exception ex;

    @Override
    protected RssFeedAggregator doInBackground(ParserArg... params) {
        try {
            Document doc = Jsoup.connect(url).get();
            Element root = doc.body().getElementsByTag("channel").first();
            List<RssFeed> feed = new ArrayList<>();
            for (Element item : root.getElementsByTag("item"))
                feed.add(new RssFeed(item));

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
