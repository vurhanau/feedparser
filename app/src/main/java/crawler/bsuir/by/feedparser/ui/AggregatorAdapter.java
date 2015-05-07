package crawler.bsuir.by.feedparser.ui;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import crawler.bsuir.by.feedparser.R;
import crawler.bsuir.by.feedparser.rss.RssFeed;
import crawler.bsuir.by.feedparser.rss.RssFeedAggregator;

public class AggregatorAdapter extends ArrayAdapter<RssFeed> {
    private final RssFeedAggregator aggregator;
    private final Context context;


    public AggregatorAdapter(Context context, RssFeedAggregator aggregator) {
        super(context, R.layout.feed_item, aggregator.news());
        this.context = context;
        this.aggregator = aggregator;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.feed_item, parent, false);
        RssFeed feed = aggregator.get(position);
        TextView titleView = (TextView) rowView.findViewById(R.id.title);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        imageView.setImageResource(position % 2 == 0 ? R.drawable.feed0 : R.drawable.feed1);
        titleView.setText(feed.title());

        TextView descriptionView = (TextView) rowView.findViewById(R.id.description);
        descriptionView.setText(feed.description());

        return rowView;
    }

    @Override
    public RssFeed getItem(int position) {
        return aggregator.get(position);
    }
}
