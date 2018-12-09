import com.google.common.collect.Lists;
import common.constants.Constants;
import common.util.HttpUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.http.client.CookieStore;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import step0.corpus.crawl.DownMovieBaseInfo;
import step0.corpus.crawl.DownMovieDetailInfo;
import step0.corpus.crawl.LoginDouban;
import step1.corpus.process.CorpusSegUtils;

import java.util.List;

public class Main {

    private static  Logger logger = LoggerFactory.getLogger(Main.class.getName());
    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        logger.info("task started............");
//        CookieStore cookieStore = LoginDouban.loginDouban(HttpUtils.getHttpGet(), HttpUtils.getHttpPost());
//        DownMovieBaseInfo.downLoadMovieBasicInfo(Constants.MOVIE_TYPE_ID, Constants.INTERVAL_ID, 1, cookieStore);
//        DownMovieDetailInfo.downloadMovieDetailInfo(cookieStore);

        List<String> datas = Lists.newArrayList();
        datas.add("F:\\taobao-code\\nlp\\全网新闻数据(SogouCA)\\2012年6月—7月news_tensite_xml.full");
        datas.add("F:\\taobao-code\\nlp\\全网新闻数据(SogouCA)\\SogouCA.tar\\SogouCA");
        datas.add("F:\\taobao-code\\nlp\\搜狐新闻数据(SogouCS)\\news_sohusite_xml.full.tar\\news_sohusite_xml.full");
        datas.add("F:\\taobao-code\\nlp\\搜狐新闻数据(SogouCS)\\SogouCS.tar\\SogouCS");
        CorpusSegUtils.segSougouNews(datas);
        logger.info("task end........");
        stopWatch.stop();
        logger.info("cost :{} ms", stopWatch.getTime());
    }
}
