package common.beans;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by cage
 * 某个用户对电影的评论详情（短评）
 *   Panda的阴影
 *   推荐
 *   2012-01-06 21:50:26
 *   错过的爱情是否可以弥补回来，还是像傻瓜一样“独自等待”？
 */
public class MovieCommon implements Serializable {

    private String movieId;
    private String userName;
    private String commonLevel;
    private String date;
    private String common;

    public MovieCommon(String movieId, String userName, String commonLevel, String date, String common) {
        this.movieId = movieId;
        this.userName = userName;
        this.commonLevel = commonLevel;
        this.date = date;
        this.common = common;
    }

    public MovieCommon() {
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCommonLevel() {
        return commonLevel;
    }

    public void setCommonLevel(String commonLevel) {
        this.commonLevel = commonLevel;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCommon() {
        return common;
    }

    public void setCommon(String common) {
        this.common = common;
    }

    @Override
    public String toString() {
        return "MovieCommon{" +
                "movieId='" + movieId + '\'' +
                ", userName='" + userName + '\'' +
                ", commonLevel='" + commonLevel + '\'' +
                ", date='" + date + '\'' +
                ", common='" + common + '\'' +
                '}';
    }
}
