package common.beans;

/**
 * Created by cage on 2018-11-25
 */
public class MovieDetail extends MovieBase {
    private String director; // 导演
    private String scriptwriter; // 编剧
    private String protagonist; // 主演 ，多个主演以"#"分割
    private String type;//类型：以#分割
    private String area; //制片国家或者地区
    private String language;
    private String releaseDate; //上映日期
    private int duration;//片长：分钟
    private String alternateName;//别名
    private float score;//豆瓣评分
    private int scoreCnt;//评分人数
    private float fiveStarPercent;//五星占比
    private float fourStarPercent;
    private float threeStarPercent;
    private float twoStarPercent;
    private float oneStarPercent;

    public MovieDetail() {
    }


    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getScriptwriter() {
        return scriptwriter;
    }

    public void setScriptwriter(String scriptwriter) {
        this.scriptwriter = scriptwriter;
    }

    public String getProtagonist() {
        return protagonist;
    }

    public void setProtagonist(String protagonist) {
        this.protagonist = protagonist;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getAlternateName() {
        return alternateName;
    }

    public void setAlternateName(String alternateName) {
        this.alternateName = alternateName;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public int getScoreCnt() {
        return scoreCnt;
    }

    public void setScoreCnt(int scoreCnt) {
        this.scoreCnt = scoreCnt;
    }

    public float getFiveStarPercent() {
        return fiveStarPercent;
    }

    public void setFiveStarPercent(float fiveStarPercent) {
        this.fiveStarPercent = fiveStarPercent;
    }

    public float getFourStarPercent() {
        return fourStarPercent;
    }

    public void setFourStarPercent(float fourStarPercent) {
        this.fourStarPercent = fourStarPercent;
    }

    public float getThreeStarPercent() {
        return threeStarPercent;
    }

    public void setThreeStarPercent(float threeStarPercent) {
        this.threeStarPercent = threeStarPercent;
    }

    public float getTwoStarPercent() {
        return twoStarPercent;
    }

    public void setTwoStarPercent(float twoStarPercent) {
        this.twoStarPercent = twoStarPercent;
    }

    public float getOneStarPercent() {
        return oneStarPercent;
    }

    public void setOneStarPercent(float oneStarPercent) {
        this.oneStarPercent = oneStarPercent;
    }
}
