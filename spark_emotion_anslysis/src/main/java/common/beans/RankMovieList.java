package common.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by cage on 2018-12-01
 */
public class RankMovieList implements Serializable {
    private ArrayList<RankMovie> movies;

    public RankMovieList(ArrayList<RankMovie> movies) {
        this.movies = movies;
    }

    public ArrayList<RankMovie> getMovies() {
        return movies;
    }

    public void setMovies(ArrayList<RankMovie> movies) {
        this.movies = movies;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        movies.stream().forEach(ele -> stringBuilder.append(ele));
        return stringBuilder.toString();
    }
}
