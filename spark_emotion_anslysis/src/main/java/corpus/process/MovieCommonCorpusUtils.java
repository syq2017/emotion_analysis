package corpus.process;

import common.util.MySQLUtils;
import common.util.MyTestIgnore;


/**
 * Created by cage
 */
public class MovieCommonCorpusUtils {
    private static String segsFilePath = "F:\\taobao-code\\nlp\\segs\\common_segs.txt";
    public static String commonAndLevelFilePath = "F:\\taobao-code\\nlp\\segs\\common_level_segs.txt";
    public static String mergeSegResultFilePath = "F:\\taobao-code\\nlp\\segs\\seg_result_merge.txt";


    /**
     * 对短评分词,并写入文件
     */
    public static void segMovieCommon(String dstFilePath) {
        MySQLUtils.storeAllSegMovieCommons(dstFilePath);
    }

    public static void storeMovieCommon(String dstFilePath) {
        MySQLUtils.storeAllMovieCommonAndLevel(dstFilePath);
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
