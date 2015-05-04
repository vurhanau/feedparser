package crawler.bsuir.by.feedparser;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

import crawler.bsuir.by.feedparser.db.DataSource;
import crawler.bsuir.by.feedparser.rss.RssFeed;
import crawler.bsuir.by.feedparser.rss.RssFeedAggregator;
import crawler.bsuir.by.feedparser.task.DumpTask;
import crawler.bsuir.by.feedparser.task.ParserArg;
import crawler.bsuir.by.feedparser.task.ParserProgress;
import crawler.bsuir.by.feedparser.task.ParserTask;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AsyncTask<ParserArg, ParserProgress, RssFeedAggregator> loadTask = new ParserTask().execute();

        LinearLayout feedTable = (LinearLayout) findViewById(R.id.tableFeed);
        try {
            AsyncTask<RssFeedAggregator, Void, RssFeedAggregator> dt = new DumpTask(new DataSource(this)).execute(loadTask.get());
            fill(feedTable, dt.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void fill(LinearLayout feedTable, RssFeedAggregator news) {
        for(RssFeed feed : news) {
            feedTable.addView(row(feed));
        }
    }

    private TableRow row(RssFeed feed) {
        TableRow row = new TableRow(this);
        row.setGravity(Gravity.CENTER);
        TextView textView = new TextView(this);
        textView.setText(feed.toString());
        row.addView(textView);
        return row;
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
