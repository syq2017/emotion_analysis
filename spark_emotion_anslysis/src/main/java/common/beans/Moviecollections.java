package common.beans;

/**
 *
 * 电影评论
 */
public class Moviecollections {

    private int movieId; //与MovieBase中一致
    private String collection; //单个用户对电影的评论

    public Moviecollections(int movieId, String collection) {
        this.movieId = movieId;
        this.collection = collection;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }
}
