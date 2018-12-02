package step0.corpus.crawl;

import com.google.gson.Gson;
import common.beans.MovieCommon;
import common.beans.MovieCommonPage;
import common.constants.Constants;
import common.util.DateUtils;
import common.util.HttpUtils;
import common.util.MySQLUtils;
import common.util.MyTestIgnore;
import org.apache.log4j.BasicConfigurator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.http.client.CookieStore;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by cage
 */
public class DownMovieDetailInfo{

    private static Logger logger = LoggerFactory.getLogger(DownMovieDetailInfo.class.getName());

    /**
     * 下载电影短评信息、解析、入库
     * @param cookieStore
     */
<<<<<<< HEAD
    public static void downloadMovieDetailInfo( CookieStore cookieStore) throws Exception {
        int sqlStart = 0;
        int sqlLimit = 100;
        HashSet<String> movieIdsFromInserttedDB = getMovieIdsFromInserttedDB();
=======
    public static void downloadMovieDetailInfo( CookieStore cookieStore) throws SQLException, ClassNotFoundException, InterruptedException {
        int sqlStart = 0;
        int sqlLimit = 100;
        boolean switchSleepSecond = false;
>>>>>>> 8888fe740d47ddc5c5df5a18fa0f376823c796ae
        for (; ; sqlStart += sqlLimit) {

            ArrayList<String> movieIds = getMovieIdsFromDB(sqlStart, sqlLimit);
            if (movieIds.size() == 0) {
                break;
            }
            logger.info("movieIds.size:{}", movieIds.size());
            for (String movieId : movieIds) {
                if (movieIdsFromInserttedDB.contains(movieId)) continue;
                for (int start = 0; ; start += 20) {
                    String detailurl = String.format(Constants.MOVIE_COLLECTIONS_URL, movieId, start);
                    logger.info("commons:{}", detailurl);

                    HttpUtils.ResponseBody responseBody = HttpUtils.doGet(detailurl, cookieStore);
                    if (responseBody == null) {
                        break;
                    }
                    String data = responseBody.getData();
                    if (data == null || data.length() == 0 || data.equals("")) {
                        break;
                    }
                    Gson gson = new Gson();
                    MovieCommonPage movieCommonPage = gson.fromJson(data, MovieCommonPage.class);
                    logger.info("data:{}", data);
                    data = movieCommonPage.getHtml();
                    cookieStore = responseBody.getCookieStore();
                    ArrayList<MovieCommon> movieCommons = getMovieCommons(data, movieId);
                    if (movieCommons != null) {
                        logger.info("movieCommons.size: {}", movieCommons.size());
                    }
                    MySQLUtils.insertMovieCommons(movieCommons);
<<<<<<< HEAD
                    int seconds = DateUtils.getShortSleepSeconds();
                    logger.info("sleep:{} seconds", seconds);
                    Thread.sleep(seconds * 1000);
=======
                    
                    if (switchSleepSecond) {
                        Thread.sleep(DateUtils.getShortSleepSeconds() * 1000);
                    } else {
                        Thread.sleep(DateUtils.getSleepSeconds() * 1000);
                    }
                    switchSleepSecond = !switchSleepSecond;
>>>>>>> 8888fe740d47ddc5c5df5a18fa0f376823c796ae
                }
            }
        }
    }

    /**
     * 从数据库获取已有电影的ID，
     * @param start  用于故障时继续作业
     * @param limit  每页查询数量
     */
    public static ArrayList<String> getMovieIdsFromDB(int start, int limit) throws SQLException, ClassNotFoundException {
        String sql = "select id from RankMovie limit " + start +"," + limit;
        logger.info("{}", sql);
        return MySQLUtils.getAllId(sql, "id");
    }

    /**
     * 得到已经插入 MovieCommon （电影评论中的电影ID），
     * 故障后重新时避免重复爬取数据
     * @return
     */
    public static HashSet<String> getMovieIdsFromInserttedDB() throws SQLException, ClassNotFoundException {
        HashSet<String> hashSet = new HashSet<String>();
        String sql = "select distinct(id) from MovieCommon";
        logger.info("{}", sql);
        ArrayList<String> ids = MySQLUtils.getAllId(sql, "id");
        ids.stream().forEach(ele -> hashSet.add(ele));
        return hashSet;
    }


    /**
     * 解析电影短评
     * @param html
     * @return
     */
    public static ArrayList<MovieCommon> getMovieCommons(String html, String movieId) {

        ArrayList<MovieCommon> result = new ArrayList<MovieCommon>();
        Document document = Jsoup.parse(html, "utf-8");
        if (document == null ) {
            return null;
        }

        Elements comments = document.getElementsByAttributeValue("class", "comment");
        if (comments == null || comments.size() == 0) {
            return null;
        }
        for (Element element : comments) {
            MovieCommon movieCommon = new MovieCommon();
            movieCommon.setMovieId(movieId);
            Elements elementsByAttributeValue = element.getElementsByTag("h3")
                    .first().getElementsByAttributeValue("class", "comment-info");
            for ( Element ele : elementsByAttributeValue) {

                Element commonAuthor = ele.getElementsByTag("a").first();
                movieCommon.setUserName(commonAuthor.text() == null ? "" : commonAuthor.text());

                String commonLevel = ele.getElementsByTag("span").get(2).attr("title");
                movieCommon.setCommonLevel(commonLevel == null ? "" : commonLevel);

                String commonTime = ele.getElementsByAttributeValue("class", "comment-time ").first().attr("title");
                movieCommon.setDate(commonTime == null ? "" : commonTime);
            }

            Element elementsByAttributeValue1 = element.getElementsByTag("p")
                    .first().getElementsByAttributeValue("class", "short").first();
            String common = elementsByAttributeValue1.getElementsByTag("span").text();
            movieCommon.setCommon(common);
            result.add(movieCommon);
        }
        return result;
    }

    @MyTestIgnore
<<<<<<< HEAD
    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        downloadMovieDetailInfo(null);
=======
    public static void main(String[] args) {

        boolean res = true;
        res = !res;
        System.out.println(res);
        res = !res;
        System.out.println(res);

//        try {
//            String html = "F:\\淘宝\\客户\\0001-阿驰在此12000程序论文\\代码\\独自等待短评.html";
//            Document document = Jsoup.parse(new File(html), "utf-8");
//            Elements comments = document.getElementsByAttributeValue("class", "comment");
//            for (Element element : comments) {
//                Elements elementsByAttributeValue = element.getElementsByTag("h3").first().getElementsByAttributeValue("class", "comment-info");
//                for ( Element ele : elementsByAttributeValue) {
//                    Element commonAuthor = ele.getElementsByTag("a").first();
//                    System.out.println(commonAuthor.text());
//                    String commonLevel = ele.getElementsByTag("span").get(2).attr("title");
//                    System.out.println(commonLevel);
//                    String commonTime = ele.getElementsByAttributeValue("class", "comment-time ").first().attr("title");
//                    System.out.println(commonTime);
//                }
//                Element elementsByAttributeValue1 = element.getElementsByTag("p").first().getElementsByAttributeValue("class", "short").first();
//                System.out.println(elementsByAttributeValue1.getElementsByTag("span").text());
//                System.out.println("-----------------------");
//            }




//            Elements elementsByAttributeValue = document.getElementsByAttributeValue("class", "comment-info");
//            System.out.println(elementsByAttributeValue.size());
//            System.out.println(">>>>>>>>>>");
//            for ( Element element : elementsByAttributeValue) {
//                System.out.println(element);
//                Element a = element.getElementsByTag("a").first();
//                System.out.println(a.text());
//
//                String span = element.getElementsByTag("span").get(2).attr("title");
//                System.out.println(span);
//
//                System.out.println("----------------------");
//            }


>>>>>>> 8888fe740d47ddc5c5df5a18fa0f376823c796ae
    }
}
