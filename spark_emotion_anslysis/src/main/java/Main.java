import com.google.common.collect.Lists;
import common.util.DateUtils;
import common.util.FileUtils;
import corpus.process.CorpusSegUtils;
import corpus.process.MovieCommonCorpusUtils;
import corpus.train.TrainWord2Vec;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.BasicConfigurator;
import org.nlp.vec.VectorModel;
import org.nlp.vec.Word2Vec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;
import java.util.List;

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


        //step3-1 短评语料 查询所有的短评级及评写入本地文件
//        MovieCommonCorpusUtils.storeMovieCommon(MovieCommonCorpusUtils.commonAndLevelFilePath);
        //step3-2 分词，标签转换：五星（力荐） -> 5,以此类推
//        logger.info("common seg start....");
//        String rawFile = "F:\\taobao-code\\nlp\\segs\\common_level_segs.txt";
//        String commonSegDstFile = "F:\\taobao-code\\nlp\\segs\\common_level_segs_result_%s.txt";
//        commonSegDstFile = String.format(commonSegDstFile, DateUtils.dateToString(new Date(), DateUtils.FILE_PATTERN));
//        logger.info("rawFile:{}", rawFile);
//        logger.info("dstFile:{}", commonSegDstFile);
//        MovieCommonCorpusUtils.segMovieCommons(rawFile,commonSegDstFile);
//        logger.info("common seg end....");

        // step4 合并2 3-1 产生的结果
//        logger.info("merge all segs result start....");
//        List<String> paths = Lists.newArrayList();
//        paths.add(CorpusSegUtils.segsFilePath1);
//        paths.add(commonSegDstFile);
//        String allCorpusSegResultFile = "F:\\taobao-code\\nlp\\segs\\all_corpus_seg_result_file_%s.txt";
//        allCorpusSegResultFile = String.format(allCorpusSegResultFile,
//                DateUtils.dateToString(new Date(), DateUtils.FILE_PATTERN));
//
//        logger.info("allCorpusSegResultFile:{}", allCorpusSegResultFile);
//        FileUtils.mergeFile(paths, allCorpusSegResultFile);
//        logger.info("merge all segs result end....");

        //step5 word2vec训练
//        String textFilePath = allCorpusSegResultFile;
//        String textFilePath = "F:\\taobao-code\\nlp\\segs\\all_corpus_seg_result_file_2018_12_16_14_19_30.txt";
//        String modelFilePath = "F:\\taobao-code\\nlp\\segs\\seg_result_merge-%s.model";
//        modelFilePath = String.format(modelFilePath, DateUtils.dateToString(new Date(), DateUtils.FILE_PATTERN));
//        logger.info("modelFilePath:{}", modelFilePath);
//        TrainWord2Vec.train(textFilePath, modelFilePath, 16, Word2Vec.Method.Skip_Gram);

        //step6 字词联合训练(借用step5的结果，只需要做一些调整，无需全部再训练)
//        String modelPath = modelFilePath;
//        String modelPath = "F:\\taobao-code\\nlp\\segs\\seg_result_merge-2018_12_16_17_06_59.model";
//        String modelPathForCilin = "F:\\taobao-code\\nlp\\segs\\seg_result_merge-with-cilin-%s.model";
//        modelPathForCilin = String.format(modelPathForCilin, DateUtils.dateToString(new Date(), DateUtils.FILE_PATTERN));
//        FileUtils.fileCopy(modelPath, modelPathForCilin);

//        String modelPath = "F:\\taobao-code\\nlp\\segs\\seg_result_merge-2018_12_16_17_06_59.model";
//        String cilinPath = "F:\\taobao-code\\nlp\\哈工大社会计算与信息检索研究中心同义词词林扩展版.txt";
//        String cilinModelPath = "F:\\taobao-code\\nlp\\segs\\vector-with-cilin-%s.model";
//        cilinModelPath = String.format(cilinModelPath, DateUtils.dateToString(new Date(), DateUtils.FILE_PATTERN));
//        VectorModel.trainWithCilinFile(modelPath, cilinPath, cilinModelPath);



        logger.info("task end........");
        logger.info("cost:{} ms", stopWatch.getTime());
        stopWatch.stop();

    }
}
