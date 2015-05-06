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

public class ListTask extends AsyncTask<ListTask.Arg, Void, RssFeedAggregator> {

    private final static String inPeriod = "select " + FeedEntry.COLUMNS_STRING + " from " + FeedEntry.TABLE_NAME
            + " where " + FeedEntry.KEY + " >= ? and " + FeedEntry.KEY + " < ?;";
    private final DataSource ds;

    public ListTask(DataSource ds) {
        this.ds = ds;
    }

    @Override
    protected RssFeedAggregator doInBackground(Arg... params) {
        SQLiteDatabase db = ds.getReadableDatabase();
        Cursor c = params.length > 0
                ? db.rawQuery(inPeriod, Util.period(params[0].from(), params[0].to()))
                : db.rawQuery(inPeriod, new String[]{"0", Long.toString(System.currentTimeMillis())});//Util.lastWeek());

        List<RssFeed> news = new ArrayList<>();
        if(c.moveToFirst()) {
            do {
                news.add(map(c));
            } while(c.moveToNext());
        }
        c.close();
        db.close();

        List<RssFeed> l = new ArrayList<>();
        l.addAll(news);
        l.addAll(news);
        l.addAll(news);

        return news.size() > 0
                ? new RssFeedAggregator(news)
                : RssFeedAggregator.empty();

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

}
