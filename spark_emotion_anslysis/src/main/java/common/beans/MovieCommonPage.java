package common.beans;

import java.io.Serializable;

/**
 * Created by cage
 *      "r":0,
 *     "html":"<div class="comment-item" data-cid="980076980">............
 */
public class MovieCommonPage implements Serializable {

    private int r;
    private String html;

    public MovieCommonPage(int r, String html) {
        this.r = r;
        this.html = html;
    }

    public MovieCommonPage() {
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }
}
