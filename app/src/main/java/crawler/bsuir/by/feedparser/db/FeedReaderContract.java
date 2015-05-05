package crawler.bsuir.by.feedparser.db;

import android.provider.BaseColumns;
import android.text.TextUtils;

public final class FeedReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public FeedReaderContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "feed";

        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_LINK = "link";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_PUBDATE = "pubdate";

        public static final String KEY = COLUMN_NAME_PUBDATE;

        public static final String[] COLUMNS = {
                COLUMN_NAME_TITLE,
                COLUMN_NAME_LINK,
                COLUMN_NAME_DESCRIPTION,
                COLUMN_NAME_PUBDATE
        };

        public static final String COLUMNS_STRING = TextUtils.join(",", COLUMNS);
    }
}