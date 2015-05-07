package crawler.bsuir.by.feedparser.task;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import crawler.bsuir.by.feedparser.Util;
import crawler.bsuir.by.feedparser.db.DataSource;
import crawler.bsuir.by.feedparser.rss.RssFeed;
import crawler.bsuir.by.feedparser.rss.RssFeedAggregator;

import static crawler.bsuir.by.feedparser.db.FeedReaderContract.FeedEntry;

public class ListTask {

    private final static String all = "select " + FeedEntry.COLUMNS_STRING + " from " + FeedEntry.TABLE_NAME;

    private final static String inPeriod = all + " where " + FeedEntry.KEY + " >= ? and " + FeedEntry.KEY + " < ?;";

    private final static String top20 = all + " order by pubdate desc limit 20;";

    private final DataSource ds;

    public ListTask(DataSource ds) {
        this.ds = ds;
    }

    private Exception ex;

    public RssFeedAggregator fetch(Arg... params) {
        try {
            SQLiteDatabase db = ds.getReadableDatabase();
            Cursor c = params.length > 0
                    ? db.rawQuery(inPeriod, Util.period(params[0].from(), params[0].to()))
                    : db.rawQuery(top20, null);

            List<RssFeed> news = new ArrayList<>();
            if (c.moveToFirst()) {
                do {
                    news.add(map(c));
                } while (c.moveToNext());
            }
            c.close();
            db.close();

            return news.size() > 0
                    ? new RssFeedAggregator(news)
                    : RssFeedAggregator.empty();
        } catch (Exception ex) {
            this.ex = ex;
            return RssFeedAggregator.empty();
        }

    }

    private static RssFeed map(Cursor c) {
        return new RssFeed(c.getString(0), c.getString(1), c.getString(2), c.getLong(3));
    }

    public static class Arg {
        private final long from;
        private final long to;

        public Arg(long from, long to) {
            this.from = from;
            this.to = to;
        }

        public long from() {
            return from;
        }

        public long to() {
            return to;
        }
    }

    public Exception error() {
        return ex;
    }
}
