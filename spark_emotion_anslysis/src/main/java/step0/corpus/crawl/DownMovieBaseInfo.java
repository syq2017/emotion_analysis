package step0.corpus.crawl;

import common.constants.Constants;
import common.util.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;

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
    public static void downLoadMovieBasicInfo(int[] typeIds, int[] intervalIds, int previous) {

        for (int i = previous; i < typeIds.length; i ++) {
            for (int j = 0; j < intervalIds.length - 1; j++) {
                for (int start = 0; start< Constants.UPPER_LIMIT; start += 20) {
                    int intervalHigh = intervalIds[j];
                    int intervalLow = intervalIds[j-1];
                    String url = String.format(Constants.RANK_BY_TYPE_URL, typeIds[i], intervalHigh, intervalLow, start);
                    HttpGet httpGet = HttpUtils.getHttpGet();

                }
            }

        }
    }

    public static void main(String[] args) {
        HttpGet httpGet = HttpUtils.getHttpGet();
        HttpPost httpPost = HttpUtils.getHttpPost();
        CookieStore cookieStore = LoginDouban.loginDouban(httpGet, httpPost);
        String url = String.format(Constants.RANK_BY_TYPE_URL, 11, 100, 90, 20);
        HttpResponse response = HttpUtils.doGet(httpGet, url, cookieStore);
        String s = response.getEntity().toString();
        logger.info(s);
    }



}
