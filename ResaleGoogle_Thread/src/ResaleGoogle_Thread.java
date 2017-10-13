import Utils.Utilities;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ResaleGoogle_Thread {

    static BlockingQueue<ArrayList<UrlInfo>> queue = new ArrayBlockingQueue<>(100);
    static ArrayList<String> listRequests = Utilities.readResource("data/resource.csv");

    public static void main(String[] args) {
        GoogleThread googleThread = new GoogleThread(20, queue, listRequests, true);
        TheHandle handle = new TheHandle(queue, listRequests.size());

        new Thread(googleThread).start();
        new Thread(handle).start();

    }
}
