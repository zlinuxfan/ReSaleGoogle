package Utils;

import com.sun.deploy.util.StringUtils;
import org.apache.log4j.PropertyConfigurator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.net.idn.Punycode;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class Utilities {
    static {
        String log4jConfPath = "conf/log4j.properties";
        PropertyConfigurator.configure(log4jConfPath);
    }

    private static Logger log = LoggerFactory.getLogger(Utilities.class);

    public static String toTransliteration(String string) {
        final char[] ru = {'-',' ', 'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ч', 'Ц', 'Ш', 'Щ', 'Э', 'Ю', 'Я', 'Ы', 'Ъ', 'Ь', 'а', 'б', 'в', 'г', 'д', 'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ч', 'ц', 'ш', 'щ', 'э', 'ю', 'я', 'ы', 'ъ', 'ь'};
        final String[] en = {"-"," ", "a", "b", "v", "g", "d", "e", "jo", "zh", "z", "i", "j", "k", "l", "m", "n", "o", "p", "r", "s", "t", "u", "f", "h", "ch", "c", "sh", "csh", "e", "ju", "ja", "Y", "", "", "a", "b", "v", "g", "d", "e", "jo", "zh", "z", "i", "j", "k", "l", "m", "n", "o", "p", "r", "s", "t", "u", "f", "h", "ch", "c", "sh", "csh", "e", "ju", "ja", "y", "", ""};
        final StringBuilder response = new StringBuilder(string.length());
        final HashMap<Character, String> table = new HashMap<>();

        int i = 0;
        for (char ch : ru) {
            table.put(ch, en[i++]);
        }

        String element;

        for (i = 0; i < string.length(); i++) {
              element = table.get(string.charAt(i));
            response.append(element == null ? " " : element);
        }

        return response.toString();
    }

    public static Document getDocument(String url) throws IOException {
        Document document;

            document = Jsoup
                    .connect(url)
                    .get();
        return document;
    }

    public static Document connectUrl(String stringURL) throws Exception {
        URL url = new URL(stringURL);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("212.237.36.234", 3128)); // or whatever your proxy is
        HttpURLConnection uc = (HttpURLConnection) url.openConnection(proxy);

        uc.connect();

        String line;
        StringBuffer tmp = new StringBuffer();
        BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
        while ((line = in.readLine()) != null) {
            tmp.append(line);
        }

        return Jsoup.parse(String.valueOf(tmp));
    }

    public static void iPChange(String proxy, String port) {
        Properties systemProperties = System.getProperties();
        systemProperties.put("proxySet", "true");
        systemProperties.setProperty("http.proxyHost", proxy);
        systemProperties.setProperty("http.proxyPort", port);
    }

    public static ArrayList<String> readResource(String fileName) {
        BufferedReader fileReader = null;
        ArrayList<String> textsOfElements = new ArrayList<>();

        try {
            fileReader = new BufferedReader(new FileReader(fileName));
            fileReader.readLine();
            String line;

            while ((line = fileReader.readLine()) != null) {
                textsOfElements.add(line);
            }
        } catch (Exception e) {
            System.out.println("Error in FileReader !!!");
            e.printStackTrace();
        } finally {
            try {
                assert fileReader != null;
                fileReader.close();
            } catch (IOException e) {
                System.out.println("Error while closing fileReader !!!");
                e.printStackTrace();
            }
        }
        return textsOfElements;
    }

    public static void writeResource(ArrayList<String> textsOfElements, String fileName) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream("data/" + fileName), "cp1251"
                ));
        try {
            for (String str : textsOfElements) {
                bufferedWriter.write(str);
                bufferedWriter.write("\n");
            }
            System.out.println("File" + fileName + " rewrite successfully !!!");
        } catch (IOException e) {
            System.out.println("Error in CsvFileWriter_Page !!!");
            e.printStackTrace();
        } finally {
            try {
                bufferedWriter.flush();
                bufferedWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
            }
        }
    }

    public static String convertToTime(long millis) {

        long second = (millis / 1000) % 60;
        long minute = (millis / (1000 * 60)) % 60;
        long hour = (millis / (1000 * 60 * 60)) % 24;

        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    public static String convertDomainToPunycode(String domain) throws ParseException {
        boolean[] booleans;
        ArrayList<String> encodeDomainPart = new ArrayList<>();


            for (String str :  domain.split("\\.")) {
                if (str.startsWith("xn--")) {
                    String replace = str.replace("xn--", "");
                    booleans = new boolean[replace.length()];
                    encodeDomainPart.add((Punycode.decode(new StringBuffer(replace), booleans)).toString());
                } else {
                    encodeDomainPart.add(str);
                }
            }

        return StringUtils.join(encodeDomainPart, ".");
    }


}
