package step1.corpus.process;

import common.util.MySQLUtils;


/**
 * Created by cage
 */
public class MovieCommonCorpusUtils {
    private static String segsFilePath = "F:\\taobao-code\\nlp\\segs\\common_segs.txt";
    private static String commonAndLevelFilePath = "F:\\taobao-code\\nlp\\segs\\common_level_segs.txt";
    private static String[] punctuation = {"`", "!", "@", "#", "$", "%", "^", "&", "*", "(",
            ")", "_", "-", "=", "+", "·", "！", "￥", "（", "）",
            "【", "{", "}", "】", "、", "|", "[", "]", ",", "，",
            ".", "。", "？", "?", "/", "、","<", ">", "《", "》", ":", "：", "'", "‘", "；", ";"};
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
    public static void main(String[] args) {
        storeMovieCommon(commonAndLevelFilePath);
    }

}
