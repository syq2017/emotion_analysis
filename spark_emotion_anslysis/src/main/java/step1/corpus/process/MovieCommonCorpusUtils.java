package step1.corpus.process;

import common.util.MySQLUtils;


/**
 * Created by cage
 */
public class MovieCommonCorpusUtils {
    private static String segsFilePath = "F:\\taobao-code\\nlp\\segs\\common_segs.txt";

    /**
     * 对短评分词,并写入文件
     */
    public static void segMovieCommon(String dstFilePath) {
        MySQLUtils.storeAllSegMovieCommons(dstFilePath);
    }

    public static void storeMovieCommon(String dstFilePath) {

    }


    public static void segCommon() {
        segMovieCommon(segsFilePath);
    }

    public static void main(String[] args) {
        segCommon();
    }

}
