package crawler.bsuir.by.feedparser.task;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import crawler.bsuir.by.feedparser.db.DataSource;
import crawler.bsuir.by.feedparser.rss.RssFeed;
import crawler.bsuir.by.feedparser.rss.RssFeedAggregator;

import static crawler.bsuir.by.feedparser.db.FeedReaderContract.FeedEntry;

public class DumpTask extends AsyncTask<RssFeedAggregator, Void, RssFeedAggregator> {
    private final DataSource ds;

    public DumpTask(DataSource ds) {
        this.ds = ds;
    }

    @Override
    protected RssFeedAggregator doInBackground(RssFeedAggregator... news) {
        List<RssFeed> fresh = new ArrayList<>();
        for (RssFeedAggregator aggregator : news) {
            for (RssFeed feed : aggregator) {
                if (!contains(feed.pubDate())) {
//                    insert(feed);
                    fresh.add(feed);
                }
            }
        }

        return new RssFeedAggregator(fresh);
    }

    private boolean contains(long date) {
        SQLiteDatabase db = ds.getReadableDatabase();
        String[] projection = {FeedEntry._ID};

        String whereClause = "pubdate = ?";
        String[] whereArgs = {Long.toString(date)};

        Cursor cursor = db.query(
                FeedEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                whereClause,                                // The columns for the WHERE clause
                whereArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        return cursor.moveToFirst();
    }

    private long insert(RssFeed feed) {
        SQLiteDatabase db = ds.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_TITLE, feed.title());
        values.put(FeedEntry.COLUMN_NAME_DESCRIPTION, feed.description());
        values.put(FeedEntry.COLUMN_NAME_GUID, feed.guid());
        values.put(FeedEntry.COLUMN_NAME_LINK, feed.link());
        values.put(FeedEntry.COLUMN_NAME_PUBDATE, feed.pubDate());

        return db.insert(
                FeedEntry.TABLE_NAME,
                FeedEntry.COLUMN_NULLABLE,
                values);
    }
}
