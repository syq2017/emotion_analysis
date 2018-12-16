import common.util.DateUtils;
import corpus.train.TrainWord2Vec;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.BasicConfigurator;
import org.nlp.vec.Word2Vec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class Main {

    private static  Logger logger = LoggerFactory.getLogger(Main.class.getName());
    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        logger.info("task started............");
        //step1 爬虫
//        CookieStore cookieStore = LoginDouban.loginDouban(HttpUtils.getHttpGet(), HttpUtils.getHttpPost());
//        DownMovieBaseInfo.downLoadMovieBasicInfo(Constants.MOVIE_TYPE_ID, Constants.INTERVAL_ID, 1, cookieStore);
//        DownMovieDetailInfo.downloadMovieDetailInfo(cookieStore);

        //step2 搜狗新闻语料分词
//        List<String> datas = Lists.newArrayList();
//        datas.add("F:\\taobao-code\\nlp\\全网新闻数据(SogouCA)\\2012年6月—7月news_tensite_xml.full");
//        datas.add("F:\\taobao-code\\nlp\\全网新闻数据(SogouCA)\\SogouCA.tar\\SogouCA");
//        datas.add("F:\\taobao-code\\nlp\\搜狐新闻数据(SogouCS)\\news_sohusite_xml.full.tar\\news_sohusite_xml.full");
//        datas.add("F:\\taobao-code\\nlp\\搜狐新闻数据(SogouCS)\\SogouCS.tar\\SogouCS");
//        CorpusSegUtils.segSougouNews(datas);


        //step 3短评语料分词
//        MovieCommonCorpusUtils.storeMovieCommon(MovieCommonCorpusUtils.commonAndLevelFilePath);

        // step4 合并2 3 产生的结果
//        List<String> paths = Lists.newArrayList();
//        paths.add(CorpusSegUtils.segsFilePath1);
//        paths.add(MovieCommonCorpusUtils.commonAndLevelFilePath);
//        FileUtils.mergeFile(paths, MovieCommonCorpusUtils.mergeSegResultFilePath);

        //step5 word2vec训练
        String textFilePath = "F:\\taobao-code\\nlp\\segs\\seg_result_merge.txt";
        String modelFilePath = "F:\\taobao-code\\nlp\\segs\\seg_result_merge-%s.model";
        TrainWord2Vec.train(textFilePath, String.format(modelFilePath, DateUtils.dateToString(new Date(), DateUtils.FILE_PATTERN)),
                14, Word2Vec.Method.Skip_Gram);

        logger.info("task end........");
        stopWatch.stop();
        logger.info("cost:{} ms", stopWatch.getTime());
    }
}
