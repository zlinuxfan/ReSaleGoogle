import Utils.Utilities;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class TheHandle extends Thread{

    private final BlockingQueue<ArrayList<UrlInfo>> queue;
    private final int listSize;
    ArrayList<String> answer = new ArrayList<>();

    public TheHandle(BlockingQueue<ArrayList<UrlInfo>> queue, int listSize) {
        this.queue = queue;
        this.listSize = listSize;
    }

    @Override
    public void run() {
        System.out.println("Handle start...");

        for (int i = listSize; i > 0; i--) {
            try {
                System.out.println("Left in queue: "+i);
                for (UrlInfo urlInfo : queue.take()) {
                    if (!urlInfo.isYoutube() && !urlInfo.isBlackList()) {
                        Document doc = getDoc(urlInfo.getLink().toString());
                        if (doc != null && doc.outerHtml().indexOf("adsbygoogle") > 0) {
                            try {
                                answer.add(
                                        urlInfo.getRequestName() + "; " +
                                                urlInfo.getLink().toString() + "; " +
                                                Utilities.convertDomainToPunycode(urlInfo.getLink().getHost())
                                );
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                answer.add(".............................................");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            Utilities.writeResource(answer, "result.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Handle stop.");
    }

    private static Document getDoc(String url) {
        Document document = null;
        URL bedUrl;
        try {
            document = Utilities.getDocument(url);
        } catch (IOException e) {
//            log.error("    error: \"" + url + "\" сan not be processed.");
            try {
                bedUrl = new URL(url);
//                if (bedUrl.getHost() != null) {
//                    bedUrls.add(bedUrl.getHost());
//                }
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
        }
        return document;
    }
}
