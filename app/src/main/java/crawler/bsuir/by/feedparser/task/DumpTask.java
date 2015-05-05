package crawler.bsuir.by.feedparser.task;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.text.TextUtils;

import crawler.bsuir.by.feedparser.Util;
import crawler.bsuir.by.feedparser.db.DataSource;
import crawler.bsuir.by.feedparser.rss.RssFeed;
import crawler.bsuir.by.feedparser.rss.RssFeedAggregator;

import static crawler.bsuir.by.feedparser.db.FeedReaderContract.FeedEntry;

public class DumpTask extends AsyncTask<RssFeedAggregator, Void, Void> {
    private final static String insertIf = "INSERT INTO " + FeedEntry.TABLE_NAME + " (" + FeedEntry.COLUMNS_STRING + ") " +
            "SELECT " + TextUtils.join(",", Util.repeat("?", FeedEntry.COLUMNS.length)) +
            "WHERE NOT EXISTS(SELECT 1 FROM " + FeedEntry.TABLE_NAME + " WHERE " + FeedEntry.KEY + " = ?);";

    private final DataSource ds;

    public DumpTask(DataSource ds) {
        this.ds = ds;
    }

    private static String[] stringValues(RssFeed feed) {
        return new String[]{
                feed.title(),
                feed.link(),
                feed.description(),
                Long.toString(feed.pubDate()),

        };
    }

    @Override
    protected Void doInBackground(RssFeedAggregator... news) {
        for (RssFeedAggregator aggregator : news) {
            putIfAbsent(aggregator);
        }

        return null;
    }

    private void putIfAbsent(RssFeedAggregator latest) {
        SQLiteDatabase db = ds.getWritableDatabase();
        db.beginTransaction();
        for (RssFeed feed : latest) {
            Cursor c = db.rawQuery(insertIf, stringValues(feed)); // TODO: status handling
//            c.close();
        }
        db.endTransaction();
    }
}
