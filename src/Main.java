import Utils.Utilities;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class Main {

    private static Logger log = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) throws Exception {
        ArrayList<String> listRequests = Utilities.readResource("data/resource.csv");
        Google google = new Google();
        ArrayList<UrlInfo> urlInfos;
        Random random = new Random();

        ArrayList<String> answer = new ArrayList<>();

        int timeOut;
        int counter = listRequests.size();

        for (String request : listRequests) {
            urlInfos = google.find(request);
            System.out.println("(" + listRequests.size() + ") - " + counter-- + ": " +request);

            for (UrlInfo urlInfo: urlInfos) {
                if (! urlInfo.isYoutube() && ! urlInfo.isBlackList()) {
                    Document doc = getDoc(urlInfo.getLink().toString());
                    if (doc != null && doc.outerHtml().indexOf("adsbygoogle") > 0) {
                        answer.add(
                                request + "; " +
                                urlInfo.getLink().toString() + "; " +
                                Utilities.convertDomainToPunycode(urlInfo.getLink().getHost())
                        );
                    }
                }
            }
            answer.add(".............................................");
            timeOut = random.nextInt(120);
            System.out.println("Timeout: " + timeOut + "sec ...");
            Thread.sleep(timeOut * 1000);
        }

        Utilities.writeResource(answer, "result.csv");

    }

    private static Document getDoc(String url) {
        Document document = null;
        URL bedUrl;
        try {
            document = Utilities.getDocument(url);
        } catch (IOException e) {
            log.error("    error: \"" + url + "\" сan not be processed.");
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

    class Tmp {
        private ArrayList<UrlInfo> urlInfos;
        private String sources;

        public Tmp(ArrayList<UrlInfo> urlInfos, String sources) {
            this.urlInfos = urlInfos;
            this.sources = sources;
        }

        public ArrayList<UrlInfo> getUrlInfos() {
            return urlInfos;
        }

        public String getSources() {
            return sources;
        }
    }
}
