

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import Utils.*;

public class UrlInfo {
    private URL link;
    private String heading;
    private String description;
    private String source;
    private String requestName;

    private boolean youtube;
    private boolean blackList;

    private static HashSet<String> blackLists = getBlackList();

    UrlInfo(String source, String link, String heading, String description) {
        this.source = source;
        try {
            this.link = link.equals("") ? null : new URL(link);
            this.youtube = this.link != null && this.link.getHost().equals("www.youtube.com");
            this.blackList = this.link != null && blackLists.contains(this.link.getHost());
        } catch (MalformedURLException e) {
            System.out.println("Do not create link from: " + link);
            e.printStackTrace();
        }

        this.description = description;
        this.heading = heading;
    }

    public UrlInfo(String source, String link, String heading, String description, String requestName) {
        this(source, link, heading, description);
        this.requestName = requestName;
    }

    public String getRequestName() {
        return requestName;
    }

    public URL getLink() {
        return link;
    }

    public String getHeading() {
        return heading;
    }

    public String getDescription() {
        return description;
    }

    public String getSource() {
        return source;
    }

    public boolean isYoutube() {
        return youtube;
    }

    public boolean isBlackList() {
        return blackList;
    }

    private static HashSet<String> getBlackList() {
        ArrayList<String> list = Utilities.readResource("data/bedUrl.txt");
        return new HashSet<>(list);
    }
}
