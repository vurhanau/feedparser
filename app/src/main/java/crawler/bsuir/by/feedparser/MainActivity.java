package crawler.bsuir.by.feedparser;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

import crawler.bsuir.by.feedparser.db.DataSource;
import crawler.bsuir.by.feedparser.rss.RssFeed;
import crawler.bsuir.by.feedparser.rss.RssFeedAggregator;
import crawler.bsuir.by.feedparser.task.DumpTask;
import crawler.bsuir.by.feedparser.task.ListTask;
import crawler.bsuir.by.feedparser.task.ParserTask;


public class MainActivity extends ActionBarActivity {

    private DataSource ds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            this.ds = new DataSource(this);
            AsyncTask<Void, Void, RssFeedAggregator> parserTask = new ParserTask().execute();
            AsyncTask<RssFeedAggregator, Void, Void> dumpTask = new DumpTask(ds).execute(parserTask.get());
            dumpTask.get();
            AsyncTask<ListTask.Arg, Void, RssFeedAggregator> listTask = new ListTask(ds).execute();
            LinearLayout feedTable = (LinearLayout) findViewById(R.id.tableFeed);
            fill(feedTable, listTask.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void fill(LinearLayout feedTable, RssFeedAggregator news) {
        for (RssFeed feed : news) {
            feedTable.addView(row(feed));
        }
    }

    private TableRow row(RssFeed feed) {
        TableRow row = new TableRow(this);
        row.setGravity(Gravity.CENTER);
        TextView textView = new TextView(this);
        textView.setText(feed.toString());
        row.addView(textView);
        final Context self = this;
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(self)
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
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
