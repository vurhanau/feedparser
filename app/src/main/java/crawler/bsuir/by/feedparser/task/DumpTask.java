package crawler.bsuir.by.feedparser.task;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
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
            " WHERE NOT EXISTS(SELECT 1 FROM " + FeedEntry.TABLE_NAME + " WHERE " + FeedEntry.KEY + " = ?);";

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
                Long.toString(feed.pubDate())
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
        db.beginTransactionNonExclusive();

        SQLiteStatement stmt = db.compileStatement(insertIf);
        for (RssFeed feed : latest) {
            stmt.bindString(1, feed.title());
            stmt.bindString(2, feed.link());
            stmt.bindString(3, feed.description());
            stmt.bindLong(4, feed.pubDate());
            stmt.bindLong(5, feed.pubDate());
            long row = stmt.executeInsert();
            stmt.clearBindings();
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

    }
}
