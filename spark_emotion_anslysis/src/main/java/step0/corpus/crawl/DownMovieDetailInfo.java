package step0.corpus.crawl;

import common.beans.MovieCommon;
import common.constants.Constants;
import common.util.DateUtils;
import common.util.HttpUtils;
import common.util.MySQLUtils;
import common.util.MyTestIgnore;
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

/**
 * Created by cage on 2018-12-01
 */
public class DownMovieDetailInfo {

    private static Logger logger = LoggerFactory.getLogger(DownMovieBaseInfo.class.getName());

    /**
     * 下载电影短评信息、解析、入库
     * @param cookieStore
     */
    public static void downloadMovieDetailInfo( CookieStore cookieStore) throws SQLException, ClassNotFoundException, InterruptedException {
        int sqlStart = 0;
        int sqlLimit = 100;
        boolean switchSleepSecond = false;
        for (; ; sqlStart += sqlLimit) {
            ArrayList<String> movieIds = getMovieIdsFromDB(sqlStart, sqlLimit);
            if (movieIds.size() == 0) {
                break;
            }
            for (String movieId : movieIds) {
                for (int start = 0; ; start += 20) {
                    String detailurl = String.format(Constants.MOVIE_COLLECTIONS_URL, movieId, start);
                    logger.info("commons:{}", detailurl);
                    HttpUtils.ResponseBody responseBody = HttpUtils.doGet(detailurl, cookieStore);
                    if (responseBody == null) {
                        break;
                    }
                    String data = responseBody.getData();
                    cookieStore = responseBody.getCookieStore();
                    ArrayList<MovieCommon> movieCommons = getMovieCommons(data, movieId);
                    MySQLUtils.insertMovieCommons(movieCommons);
                    
                    if (switchSleepSecond) {
                        Thread.sleep(DateUtils.getShortSleepSeconds() * 1000);
                    } else {
                        Thread.sleep(DateUtils.getSleepSeconds() * 1000);
                    }
                    switchSleepSecond = !switchSleepSecond;
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


    }
}
