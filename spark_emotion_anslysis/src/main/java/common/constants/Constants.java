package common.constants;

import javax.naming.ldap.HasControls;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by cage
 */
public class Constants {

    public static final String[]  MOVIE_TYPE = {"剧情", "喜剧", "动作", "爱情", "科幻", "动画", "悬疑", "惊悚",
                "恐怖", "纪录片", "短片", "情色", "同性", "音乐", "歌舞", "家庭", "儿童", "传记", "历史", "战争",
                "犯罪", "西部", "奇幻", "冒险", "灾难", "武侠", "古装", "运动", "黑色电影"};

    public static final int[] MOVIE_TYPE_ID = {11, 24, 5, 13, 17, 25, 10, 19, 20, 1, 23, 6,
            26, 14, 7, 28, 8, 2, 4, 22, 3, 27, 16, 15, 12, 29, 30, 18, 31};

    public static final String DELEMITER = "*&^-_";

    public static final int[] INTERVAL_ID = {100, 90, 80, 70, 60, 50, 40, 30, 20, 10, 0};

    public static final Map<String, Integer> MOVIE_TYPE_MAP = new HashMap<String, Integer>();

    public static final int UPPER_LIMIT = 5000; //某个类型下（type） 一段间隔内(interval_id)的电影下载数量上限

    static {
        for (int i = 0; i < MOVIE_TYPE.length; i++) {
            MOVIE_TYPE_MAP.put(MOVIE_TYPE[i], MOVIE_TYPE_ID[i]);
        }
    }

    //五星 ---> 一星
    public static final String[] COMMENT_LEVEL = {"力荐", "推荐", "还行", "较差", "很差"};
    public static final Set<String> COMMENT_LEVEL_SET = new HashSet<String>();
    static {
        Arrays.asList(COMMENT_LEVEL).stream().forEach(ele -> COMMENT_LEVEL_SET.add(ele));
    }

    //存储验证码图片的路径
    public static final String AUTH_CODE_PATH = "D:\\";

    //某个类型下的电影列表url
    public static String RANK_BY_TYPE_URL =
            "https://movie.douban.com/j/chart/top_list?type=%d&interval_id=%d:%d&action=&start=%d&limit=20";

    //某个电影详情页   https://movie.douban.com/subject/1308741/   --->  1308741:movie id
    public static String MOVIE_DETAIL_URL = "https://movie.douban.com/subject/%s/";

    //某个电影的 短评 url，注意不是评论，因为评论最多只显示200条,且多数无文字说明
    // https://movie.douban.com/subject/1308741/comments?status=P
    public static String MOVIE_COLLECTIONS_URL =
            "https://movie.douban.com/subject/%s/comments?start=%d&limit=20&sort=new_score&status=P&comments_only=1";

    //分词中保留符号训练
    public static final String EXCLAMATION_CN= "叹号";
    public static final String QUESTION_MARK_CN = "问号";
    public static final String ELLIPSIS_CN = "省略号";

    public static final String EXCLAMATION = "!"; //中英文叹号
    public static final String EXCLAMATION_EN = "！"; //中英文叹号

    public static final String QUESTION_MARK = "？"; //
    public static final String QUESTION_MARK_EN = "?"; //
    public static final Set<String> ELLIPSIS = new HashSet<>();
    static {
        ELLIPSIS.add("..");
        ELLIPSIS.add("...");
        ELLIPSIS.add("....");
        ELLIPSIS.add(".....");
        ELLIPSIS.add("......");
        ELLIPSIS.add("。。");
        ELLIPSIS.add("。。。");
        ELLIPSIS.add("。。。。");
        ELLIPSIS.add("。。。。。");
        ELLIPSIS.add("。。。。。。");
    }

    //需要过滤的符号
    public static String[] PUNCTUATIONS = {"`", "!", "@", "#", "$", "%", "^", "&", "*", "(",
            ")", "_", "-", "=", "+", "·", "！", "￥", "（", "）",
            "【", "{", "}", "】", "、", "|", "[", "]", ",", "，","",
            ".", "。", "？", "?", "/", "、","<", ">", "《", "》", ":", "：", "'", "‘", "；", ";"};
    public static final String COMMA = "，";
    public static final String EMPTY = "";
    public static HashSet<String> PUNCTUATIONS_SET = new HashSet<>();
    static {
        Arrays.asList(PUNCTUATIONS).stream().forEach(PUNCTUATIONS_SET::add);
    }
}
