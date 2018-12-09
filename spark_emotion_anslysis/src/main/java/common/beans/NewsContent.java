package common.beans;

import java.util.ArrayList;

/**
 * Created by cage on 2018-12-08
 */
public class NewsContent {
    private String newsTitle;
    private String  newsContent;

    public NewsContent() {}

    public NewsContent(String newsTitle, String newsContent) {
        this.newsTitle = newsTitle;
        this.newsContent = newsContent;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsContent() {
        return newsContent;
    }

    public void setNewsContent(String newsContent) {
        this.newsContent = newsContent;
    }
}
