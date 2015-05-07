package crawler.bsuir.by.feedparser;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.jsoup.Jsoup;

public class Util {

    public static String html2text(String html) {
        return Jsoup.parse(html).text();
    }

    public static String mul(String text, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(text);
        return sb.toString();
    }

    public static String[] repeat(String text, int n) {
        String[] strings = new String[n];
        for (int i = 0; i < n; i++) strings[i] = new String(text);
        return strings;
    }

    public static String[] period(long from, long to) {
        return new String[]{
                Long.toString(from),
                Long.toString(to)
        };
    }

    public static String[] lastWeek() {
        DateTime current = new DateTime(DateTimeZone.UTC);
        DateTime from = current.minusDays(7);
        DateTime to = current.plusDays(1);
        return period(from.getMillis(), to.getMillis());
    }
}
