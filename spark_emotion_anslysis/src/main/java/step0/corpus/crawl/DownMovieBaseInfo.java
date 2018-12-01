package step0.corpus.crawl;

import common.constants.Constants;

import java.util.Iterator;
import java.util.Map;

/**
 * 从电影排行榜下载电影信息
 */
public class DownMovieBaseInfo {

    public static void downLoadMovieInfo() {
        Iterator<Map.Entry<String, Integer>> iterator = Constants.MOVIE_TYPE_MAP.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            String type = entry.getKey();
            int typeId = entry.getValue();

        }
    }



}
