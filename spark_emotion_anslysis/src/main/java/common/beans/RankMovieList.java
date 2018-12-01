package common.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 封装某个类型下的电影基本信息结果
 *      {
 *         "rating":[
 *             "9.1",
 *             "45"
 *         ],
 *         "rank":41,
 *         "cover_url":"https://img3.doubanio.com/view/photo/s_ratio_poster/public/p2457983084.webp",
 *         "is_playable":true,
 *         "id":"26387939",
 *         "types":[
 *             "剧情",
 *             "传记",
 *             "运动",
 *             "家庭"
 *         ],
 *         "regions":[
 *             "印度"
 *         ],
 *         "title":"摔跤吧！爸爸",
 *         "url":"https://movie.douban.com/subject/26387939/",
 *         "release_date":"2017-05-05",
 *         "actor_count":9,
 *         "vote_count":644881,
 *         "score":"9.1",
 *         "actors":[
 *             "阿米尔·汗",
 *             "法缇玛·萨那·纱卡",
 *             "桑亚·玛荷塔",
 *             "阿帕尔夏克提·库拉那",
 *             "沙克希·坦沃",
 *             "塞伊拉·沃西",
 *             "苏哈妮·巴特纳格尔",
 *             "里特维克·萨霍里",
 *             "吉里什·库卡尼"
 *         ],
 *         "is_watched":false
 *     },
 */
public class RankMovieList implements Serializable {
    private ArrayList<String> rating;
    private int rank;
    private String cover_url;
    private boolean is_playable;
    private String id;
    private ArrayList<String> types;
    private ArrayList<String> regions;
    private String title;
    private String url;
    private String release_date;
    private String actor_count;
    private String vote_count;
    private String score;
    private ArrayList<String> actors;
    private String is_watched;

    public RankMovieList() {
    }

    public RankMovieList(ArrayList<String> rating, int rank, String cover_url, boolean is_playable, String id, ArrayList<String> types, ArrayList<String> regions, String title, String url, String release_date, String actor_count, String vote_count, String score, ArrayList<String> actors, String is_watched) {
        this.rating = rating;
        this.rank = rank;
        this.cover_url = cover_url;
        this.is_playable = is_playable;
        this.id = id;
        this.types = types;
        this.regions = regions;
        this.title = title;
        this.url = url;
        this.release_date = release_date;
        this.actor_count = actor_count;
        this.vote_count = vote_count;
        this.score = score;
        this.actors = actors;
        this.is_watched = is_watched;
    }

    public ArrayList<String> getRating() {
        return rating;
    }

    public void setRating(ArrayList<String> rating) {
        this.rating = rating;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getCover_url() {
        return cover_url;
    }

    public void setCover_url(String cover_url) {
        this.cover_url = cover_url;
    }

    public boolean isIs_playable() {
        return is_playable;
    }

    public void setIs_playable(boolean is_playable) {
        this.is_playable = is_playable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<String> types) {
        this.types = types;
    }

    public ArrayList<String> getRegions() {
        return regions;
    }

    public void setRegions(ArrayList<String> regions) {
        this.regions = regions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getActor_count() {
        return actor_count;
    }

    public void setActor_count(String actor_count) {
        this.actor_count = actor_count;
    }

    public String getVote_count() {
        return vote_count;
    }

    public void setVote_count(String vote_count) {
        this.vote_count = vote_count;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public ArrayList<String> getActors() {
        return actors;
    }

    public void setActors(ArrayList<String> actors) {
        this.actors = actors;
    }

    public String getIs_watched() {
        return is_watched;
    }

    public void setIs_watched(String is_watched) {
        this.is_watched = is_watched;
    }

    @Override
    public String toString() {
        return "RankMovieList{" +
                "rating=" + rating +
                ", rank=" + rank +
                ", cover_url='" + cover_url + '\'' +
                ", is_playable=" + is_playable +
                ", id='" + id + '\'' +
                ", types=" + types +
                ", regions=" + regions +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", release_date='" + release_date + '\'' +
                ", actor_count='" + actor_count + '\'' +
                ", vote_count='" + vote_count + '\'' +
                ", score='" + score + '\'' +
                ", actors=" + actors +
                ", is_watched='" + is_watched + '\'' +
                '}';
    }
}
