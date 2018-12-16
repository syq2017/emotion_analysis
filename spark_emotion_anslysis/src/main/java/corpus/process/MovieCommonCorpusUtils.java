package corpus.process;

import com.huaban.analysis.jieba.JiebaSegmenter;
import common.beans.MovieCommon;
import common.object.pool.JiebaSegmenterPool;
import common.util.FileUtils;
import common.util.MySQLUtils;
import common.util.MyTestIgnore;
import javafx.scene.paint.Stop;
import org.apache.commons.lang3.time.StopWatch;
import org.nlp.util.Tokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.List;


/**
 * Created by cage
 */
public class MovieCommonCorpusUtils {
    private static Logger logger = LoggerFactory.getLogger(MovieCommonCorpusUtils.class.getName());
    private static String segsFilePath = "F:\\taobao-code\\nlp\\segs\\common_segs.txt";
    public static String commonAndLevelFilePath = "F:\\taobao-code\\nlp\\segs\\common_level_segs.txt";
    public static String mergeSegResultFilePath = "F:\\taobao-code\\nlp\\segs\\seg_result_merge.txt";


    /**
     * 从数据库下载短评并分词写入文件
     */
    public static void segMovieCommons(String dstFilePath) {
        MySQLUtils.storeAllSegMovieCommons(dstFilePath);
    }

    /**
     * 从数据库获取短评数据写入文件dstFilePath
     *      评级\t短评内容
     * @param dstFilePath
     */
    public static void storeMovieCommon(String dstFilePath) {
        MySQLUtils.storeAllMovieCommonAndLevel(dstFilePath);
    }

    /**
     * 电影短评分词filePath,然后写入dstFile
     * @param filePath
     * @param dstPath
     */
    public static void segMovieCommons(String filePath, String dstPath) {
        BufferedWriter bufferedWriter = FileUtils.buildWriter(dstPath, FileUtils.ENCODE_GB18030);
        BufferedReader bufferedReader = FileUtils.buildReader(filePath,FileUtils.ENCODE_GB18030);
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            JiebaSegmenter jiebaSegmenter = JiebaSegmenterPool.jiebaSegmenterPool.borrowObject();
            String line;
            int lineCnt = 0;
            while ((line = FileUtils.readLine(bufferedReader)) != null) {
                String[] split = line.split("\t");
                if (split.length < 2) {
                    continue;
                }
                List<String> segs = jiebaSegmenter.sentenceProcess(split[1]);
                String result = Tokenizer.filterStopWordAndSwap(segs);
                result = Tokenizer.filterStopWordAndSwap(result);
                FileUtils.write(result, bufferedWriter);
                if (lineCnt++ % 10000 == 0) {
                    logger.info("processed lines:{}", lineCnt);
                }
            }
            JiebaSegmenterPool.jiebaSegmenterPool.returnObject(jiebaSegmenter);
            FileUtils.releaseWriter(bufferedWriter);
            FileUtils.releaseReader(bufferedReader);
            logger.info("process cost:{}ms", stopWatch.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从数据下载数据到本地，然后进行分词
     * @param args
     */
    @MyTestIgnore
    public static void main(String[] args) {
        storeMovieCommon(commonAndLevelFilePath);
    }

}
