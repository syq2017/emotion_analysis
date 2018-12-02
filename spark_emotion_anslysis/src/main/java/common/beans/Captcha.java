package common.beans;

import java.io.Serializable;

/**
 * Created by cage
 */
public class Captcha implements Serializable {
    private String url;
    private String token;
    private boolean r;

    public String getUrl() {
        return url;
    }

    public Captcha(String url, String token, boolean r) {
        this.url = url;
        this.token = token;
        this.r = r;
    }

    public Captcha() {
    }

    @Override
    public String toString() {
        return "Captcha{" +
                "url='" + url + '\'' +
                ", token='" + token + '\'' +
                ", r=" + r +
                '}';
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isR() {
        return r;
    }

    public void setR(boolean r) {
        this.r = r;
    }
}
