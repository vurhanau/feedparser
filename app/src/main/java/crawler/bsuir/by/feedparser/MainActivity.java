package crawler.bsuir.by.feedparser;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;

import org.joda.time.DateTime;

import crawler.bsuir.by.feedparser.db.DataSource;
import crawler.bsuir.by.feedparser.rss.RssFeed;
import crawler.bsuir.by.feedparser.rss.RssFeedAggregator;
import crawler.bsuir.by.feedparser.task.DumpTask;
import crawler.bsuir.by.feedparser.task.ListTask;
import crawler.bsuir.by.feedparser.task.ParserTask;


public class MainActivity extends ActionBarActivity {

    private final static String TITLE = "Bsuir Feed - List";

    private final static int FEED_PREVIEW_LENGTH = 120;

    private static String feedHeader(RssFeed feed) {
        DateTime dt = new DateTime(feed.pubDate());
        return dt.toString("dd, MMM") + "  -  " + feed.title();
    }

    private static String feedBody(RssFeed feed) {
        String text = feed.description();
        return (text.length() > FEED_PREVIEW_LENGTH
                ? text.substring(0, FEED_PREVIEW_LENGTH) + "..."
                : text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(TITLE);
        setContentView(R.layout.activity_main);

        ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setIndeterminate(true);
        new BackgroundTask().execute();
        progress.setVisibility(View.GONE);
    }

    class BackgroundTask extends AsyncTask<Void, Void, RssFeedAggregator> {

        @Override
        protected RssFeedAggregator doInBackground(Void... params) {
            return load();
        }

        @Override
        protected void onPostExecute(RssFeedAggregator feed) {
            ProgressBar bar = (ProgressBar) MainActivity.this.findViewById(R.id.progressBar);
            bar.setVisibility(View.GONE);

            if (!hasInternet()) {
                alert("Oops", "You have no internet access");
            }

            if (feed == null) {
                alert("Error", "Shit happens");
                LinearLayout feedTable = (LinearLayout) findViewById(R.id.tableFeed);
                feedTable.addView(emptyTableRow());
                return;
            }

            LinearLayout feedTable = (LinearLayout) findViewById(R.id.tableFeed);
            fill(feedTable, feed);
        }
    }

    private RssFeedAggregator load() {
        try {
            DataSource ds = new DataSource(this);
            if (hasInternet()) {
                RssFeedAggregator news = new ParserTask().get();
                new DumpTask(ds).flush(news);
            }
            return new ListTask(ds).fetch();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean hasInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null;
    }

    private void fill(LinearLayout feedTable, RssFeedAggregator news) {
        if (news.isEmpty()) {
            feedTable.addView(emptyTableRow());
        }

        for (RssFeed feed : news) {
            feedTable.addView(row(feed));
            feedTable.addView(rowSeparator());
        }
    }

    private TableRow emptyTableRow() {
        TableRow row = new TableRow(this);
        row.setGravity(Gravity.CENTER);
        TextView textView = new TextView(this);
        textView.setText("No data");
        textView.setGravity(Gravity.CENTER);
        row.addView(textView);

        return row;
    }


    private TableRow row(final RssFeed feed) {
        TableRow row = new TableRow(MainActivity.this);
        row.setGravity(Gravity.CENTER);

        TextView headerText = new TextView(MainActivity.this);
        headerText.setText(feedHeader(feed));
        row.addView(headerText);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, InfoActivity.class);
                myIntent.putExtra("link", feed.link()); //Optional parameters
                MainActivity.this.startActivity(myIntent);
            }
        });

        return row;
    }

    private View rowSeparator() {
        View v = new View(this);
        v.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, 1));
        v.setBackgroundColor(Color.rgb(51, 51, 51));
        return v;
    }

    public AlertDialog alert(String title, String message) {
        return new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
