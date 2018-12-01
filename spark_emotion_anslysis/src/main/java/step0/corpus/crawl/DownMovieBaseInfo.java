package step0.corpus.crawl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import common.beans.RankMovie;
import common.beans.RankMovieList;
import common.constants.Constants;
import common.util.DateUtils;
import common.util.HttpUtils;
import common.util.MySQLUtils;
import common.util.MyTestIgnore;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 * 从电影排行榜下载电影信息
 */
public class DownMovieBaseInfo {

    private static Logger logger = LoggerFactory.getLogger(DownMovieBaseInfo.class.getName());
    /**
     * 按照类型下载电影基本信息
     * example：https://movie.douban.com/j/chart/top_list?type=%d&interval_id=%d:%d&action=&start=%d&limit=20
     * type=11
     * interval_id=100:90   （间隔）
     * action=
     * start=20
     * @param previous 上次下载的电影类型index，用于意外终止后重新启动时续传
     */
    public static void downLoadMovieBasicInfo(int[] typeIds, int[] intervalIds, int previous, CookieStore cookieStore)
            throws SQLException, ClassNotFoundException, InterruptedException {
        for (int i = previous; i < typeIds.length; i ++) {
            for (int j = 1; j < intervalIds.length - 1; j ++) {
                for (int start = 0; start< Constants.UPPER_LIMIT; start += 20) {
                    int intervalHigh = intervalIds[j-1];
                    int intervalLow = intervalIds[j];
                    String url = String.format(Constants.RANK_BY_TYPE_URL, typeIds[i], intervalHigh, intervalLow, start);
                    logger.info("downloading movie info from: {}", url);
                    HttpUtils.ResponseBody responseBody = HttpUtils.doGet(url, cookieStore);
                    String data = responseBody.getData();
                    cookieStore = responseBody.getCookieStore();
                    Gson gson = new Gson();
                    JsonParser jsonParser = new JsonParser();
                    JsonArray jsonArray = jsonParser.parse(data).getAsJsonArray();
                    if (jsonArray.size() == 0) {
                        break;
                    }
                    ArrayList<RankMovie> rankMovies = new ArrayList<RankMovie>();
                    for (JsonElement element : jsonArray) {
                        RankMovie rankMovie = gson.fromJson(element, RankMovie.class);
                        rankMovies.add(rankMovie);
                    }
                    RankMovieList rankMovieList = new RankMovieList(rankMovies);
                    MySQLUtils.insertRankMovies(rankMovieList);
                    int seconds = DateUtils.getSleepSeconds();
                    logger.info("sleep: {} s", seconds);
                    Thread.sleep(seconds * 1000);
                }
            }

        }
    }

    @MyTestIgnore
    public static void testDownloadMovieInofs() {
        System.setProperty("log4j.configuration","file:/Users/shiyuquan/Downloads/spark_emotion_anslysis/src/main/resources/slf4j.properties");
        HttpGet httpGet = HttpUtils.getHttpGet();
        HttpPost httpPost = HttpUtils.getHttpPost();
        CookieStore cookieStore = LoginDouban.loginDouban(httpGet, httpPost);
        String url = String.format(Constants.RANK_BY_TYPE_URL, 11, 100, 90, 20);
        HttpUtils.ResponseBody responseBody = HttpUtils.doGet(url, cookieStore);
        String data = responseBody.getData();
        Gson gson = new Gson();
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonArray = jsonParser.parse(data).getAsJsonArray();
        ArrayList<RankMovie> rankMovies = new ArrayList<RankMovie>();
        for (JsonElement element : jsonArray) {
            RankMovie rankMovie = gson.fromJson(element, RankMovie.class);
            rankMovies.add(rankMovie);
        }
        RankMovieList rankMovieList = new RankMovieList(rankMovies);
        System.out.println("---=====----");
        System.out.println(rankMovieList);
    }

}
