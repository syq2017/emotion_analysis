import common.constants.Constants;
import common.util.HttpUtils;
import org.apache.http.client.CookieStore;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import step0.corpus.crawl.DownMovieBaseInfo;
import step0.corpus.crawl.LoginDouban;

public class Main {

    private static  Logger logger = LoggerFactory.getLogger(Main.class.getName());
    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        logger.info("start task............");
        CookieStore cookieStore = LoginDouban.loginDouban(HttpUtils.getHttpGet(), HttpUtils.getHttpPost());
        DownMovieBaseInfo.downLoadMovieBasicInfo(Constants.MOVIE_TYPE_ID, Constants.INTERVAL_ID, 0, cookieStore);
    }
}
