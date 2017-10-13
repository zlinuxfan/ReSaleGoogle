import Utils.Utilities;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

import static java.lang.Thread.sleep;

class GoogleThread implements Find, Runnable { ;
    private int numInRequest;
    private boolean bypass = false;
    private static final String NAME = "google";
    private boolean running = false;

    private BlockingQueue<ArrayList<UrlInfo>> queue = null;
    private ArrayList<String> requestNames = null;

    private static Random random = new Random();

    public GoogleThread(int numInRequest, BlockingQueue<ArrayList<UrlInfo>> queue, ArrayList<String> requestNames) {
        this.numInRequest = numInRequest;
        this.queue = queue;
        this.requestNames = requestNames;
    }

    public GoogleThread(int numInRequest, BlockingQueue<ArrayList<UrlInfo>> queue, ArrayList<String> requestNames, boolean bypass) {
        this(numInRequest, queue, requestNames);
        this.bypass = bypass;
    }

    @Override
    public ArrayList<UrlInfo> find(String requestName) throws Exception {
        String url = "https://www.google.com.ua/search?q=" + requestName.replace(" ", "+") + "&num=" + numInRequest;
        Elements h3s;
        Elements h3Descriptions;
        Document doc = Utilities.getDocument(url); //connectUrl(url);  //getDocument(url);
        ArrayList<UrlInfo> urlInfoList = new ArrayList<>();

        h3s = doc.select("h3.r a");
        h3Descriptions = doc.select("span.st");

        for (int i = 0; i < h3s.size() && i < h3Descriptions.size(); i++) {
            urlInfoList.add(new UrlInfo(
                    NAME,
                    h3s.get(i).select("a").first().attr("abs:href"),
                    h3s.get(i).text(),
                    h3Descriptions.get(i).text(),
                    requestName
            ));
        }
        System.out.println("google find [" + requestName + "] ... ");
        return urlInfoList;
    }

    @Override
    public void run() {
        this.running = true;

        for (String requestName : requestNames) {
            try {
                this.queue.put(this.find(requestName));
                if (bypass) {
                    makeDelay();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.running = false;
    }

    private static void makeDelay() {
        long timeOut = random.nextInt(37);
        System.out.println("Timeout: " + timeOut + "sec ...");
        try {
            sleep(timeOut * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isRunning() {
        return running;
    }
}
