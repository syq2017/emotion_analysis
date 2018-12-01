package common.beans;

/**
 * Created by cage on 2018-11-25
 */
public class MovieBase {
    private int id; //在插入数据库时有db生成
    private String movieName;
    private String movieUrl;
    private String rankNum;//在某个类型内部的排名

    public MovieBase(String movieName, String movieUrl, String rankNum) {
        this.movieName = movieName;
        this.movieUrl = movieUrl;
        this.rankNum = rankNum;
    }

    public MovieBase() {
    }


    @Override
    public String toString() {
        return "MovieBase{" +
                "movieName='" + movieName + '\'' +
                ", movieUrl='" + movieUrl + '\'' +
                ", rankNum='" + rankNum + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getMovieUrl() {
        return movieUrl;
    }

    public void setMovieUrl(String movieUrl) {
        this.movieUrl = movieUrl;
    }

    public String getRankNum() {
        return rankNum;
    }

    public void setRankNum(String rankNum) {
        this.rankNum = rankNum;
    }
}
