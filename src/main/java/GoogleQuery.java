import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class GoogleQuery {

    public String searchKeyword;

    public String url;

    public String content;


    public GoogleQuery(String searchKeyword) {

        this.searchKeyword = searchKeyword;

        this.url = "http://www.google.com/search?q=" + searchKeyword + "&oe=utf8&num=10";

    }


    private String fetchContent() throws IOException {

        String retVal = "";

        URL u = new URL(url);

        URLConnection conn = u.openConnection();

        conn.setRequestProperty("User-agent", "Chrome/88.0.4324.182");

        InputStream in = conn.getInputStream();

        InputStreamReader inReader = new InputStreamReader(in, "utf-8");

        BufferedReader bufReader = new BufferedReader(inReader);
        String line = null;

        while ((line = bufReader.readLine()) != null) {
            //retVal += line;
            retVal = retVal + line + "\n";
        }
        return retVal;
    }


    public ArrayList<Result> query() throws IOException {

        if (content == null) {

            content = fetchContent();
//			System.out.println(content);
        }

        HashMap<String, String> searchResult = new HashMap<String, String>();

        Document doc = Jsoup.parse(content);
//		System.out.println(doc.text());
        Elements lis = doc.select("div.kCrYT");
//        lis = lis.select("tF2Cxc");
//		System.out.println(lis.size());


        for (Element li : lis) {
            try {
                String linkHref = li.select("a").get(0).attr("href").replace("/url?q=", "").split("&sa")[0];
//                System.out.println(linkHref);
                String linkText = li.select("h3").get(0).text();
                searchResult.put(linkHref, linkText);

            } catch (IndexOutOfBoundsException e) {
            }
        }

        List<ScoreData> scoreData = calculate(searchResult);
        ArrayList<Result> finalResult = new ArrayList<Result>();

        Collections.sort(scoreData, Comparator.comparingInt(ScoreData::getScore).reversed());

        scoreData.forEach(x -> {
            System.out.println(x.title + x.score);
            finalResult.add(new Result(x.title, x.url));
        });
        return finalResult;
    }


    public List<ScoreData> calculate(HashMap<String, String> searchResult) throws IOException {


        ArrayList<Keyword> keywords = new ArrayList<Keyword>();
        keywords.add(new Keyword("台灣", 5));
        keywords.add(new Keyword("研究所", 10));
        keywords.add(new Keyword("碩士", 8));
        keywords.add(new Keyword("推甄", 8));
        keywords.add(new Keyword("甄試", 8));
        keywords.add(new Keyword("入學考試", 8));
        keywords.add(new Keyword("大四", 5));
        keywords.add(new Keyword("正取", 5));
        keywords.add(new Keyword("筆試", 3));
        keywords.add(new Keyword("口試", 3));
        keywords.add(new Keyword("考研", 3));
        keywords.add(new Keyword("出路", -3));
        keywords.add(new Keyword("資工", -3));
        keywords.add(new Keyword("職涯", -5));
        keywords.add(new Keyword("工作", -5));


        List<ScoreData> scoreData = new ArrayList<ScoreData>();

        for (String key : searchResult.keySet()) {
            try {
                String value = searchResult.get(key);
                WebPage rootPage = new WebPage(key, value);

                rootPage.setScore(keywords);
                scoreData.add(new ScoreData(rootPage.name, key, rootPage.score));
            } catch (Exception e) {

            }
        }

        return scoreData;
    }

}

class ScoreData {
    public String title;
    public String url;
    public int score;

    public ScoreData(String title, String url, int score) {
        this.title = title;
        this.url = url;
        this.score = score;
    }

    public int getScore() {
        return this.score;
    }
}