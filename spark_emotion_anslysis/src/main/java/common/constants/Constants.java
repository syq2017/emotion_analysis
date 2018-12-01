package common.constants;

import javax.naming.ldap.HasControls;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cage on 2018-11-25
 */
public class Constants {

    public static final String[]  MOVIE_TYPE = {"剧情", "喜剧", "动作", "爱情", "科幻", "动画", "悬疑", "惊悚",
                "恐怖", "纪录片", "短片", "情色", "同性", "音乐", "歌舞", "家庭", "儿童", "传记", "历史", "战争",
                "犯罪", "西部", "奇幻", "冒险", "灾难", "武侠", "古装", "运动", "黑色电影"};

    public static final int[] MOVIE_TYPE_ID = {11, 24, 5, 13, 17, 25, 10, 19, 20, 1, 23, 6,
            26, 14, 7, 28, 8, 2, 4, 22, 3, 27, 16, 15, 12, 29, 30, 18, 31};

    public static final Map<String, Integer> MOVIE_TYPE_MAP = new HashMap<String, Integer>();

    static {
        for (int i = 0; i < MOVIE_TYPE.length; i++) {
            MOVIE_TYPE_MAP.put(MOVIE_TYPE[i], MOVIE_TYPE_ID[i]);
        }
    }

    //五星 ---> 一星
    public static final String[] COMMENT_LEVEL = {"力荐", "推荐", "还行", "较差", "很差"};

    //存储验证码图片的路径
    public static final String AUTH_CODE_PATH = "/Users/shiyuquan/Downloads/";

    //某个类型下的电影列表url
    public static String RANK_BY_TYPE_URL =
            "https://movie.douban.com/j/chart/top_list?type=%d&interval_id=%d:%d&action=&start=%d&limit=50";
    //某个电影详情页
    public static String MOVIE_DETAIL_URL = "https://movie.douban.com/subject/%s/";

    //某个电影的 短评 url，注意不是评论，因为评论最多只显示200条
    public static String MOVIE_COLLECTIONS_URL =
            "https://movie.douban.com/subject/%s/comments?start=%d&limit=20&sort=new_score&status=P";

}
