package common.util;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by cage
 */
public class HttpUtils {
    private static Logger logger = LoggerFactory.getLogger(HttpUtils.class.getName());

    /**
     * doGet
     * @param url
     * @return
     */
    public static ResponseBody doGet(String url) {
        logger.info("httpUrl:{}", url);
        CookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        CloseableHttpResponse response = null;
        HttpGet httpGet = new HttpGet(url);
        HttpEntity entity = null;
        try {
            response = httpClient.execute(httpGet);
            if (response != null) {
                entity = response.getEntity();
                if (entity != null){
                    String data = EntityUtils.toString(entity);
                    ResponseBody responseBody = new ResponseBody(data, cookieStore);
                    return responseBody;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static ResponseBody doGet(String url, CookieStore cookieStore) {
        logger.info("httpUrl:{}", url);
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        CloseableHttpResponse response = null;
        HttpGet httpGet = new HttpGet(url);
        HttpEntity entity = null;
        try {
            response = httpClient.execute(httpGet);
            if (response != null) {
                entity = response.getEntity();
                if (entity != null){
                    String data = EntityUtils.toString(entity);
                    ResponseBody responseBody = new ResponseBody(data, cookieStore);
                    return responseBody;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * doPost
     * @return
     */
    public static ResponseBody doPost(String url) {
        logger.info("{}", url);
        CookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        CloseableHttpResponse response = null;
        HttpPost httpPost = new HttpPost(url);
        HttpEntity entity = null;
        try {
            response = httpClient.execute(httpPost);
            httpPost.setHeaders(response.getAllHeaders());
            if (response != null) {
                String data = EntityUtils.toString(entity);
                ResponseBody responseBody = new ResponseBody(data, cookieStore);
                return responseBody;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     *  获得httpget 客户端
     */
    public static HttpGet getHttpGet() {
        return new HttpGet();
    }

    /**
     *  获得httppost 客户端
     */
    public static HttpPost getHttpPost() {
        return new HttpPost();
    }

    /**
     * 添加请求头
     * @param httpGet
     */
    @Deprecated
    private static void addHeaderParams(HttpGet httpGet) {
        logger.info("addHeaderParams in httpGet");
        httpGet.addHeader("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        httpGet.addHeader("Accept-Encoding", "gzip, deflate, br");
        httpGet.addHeader("Accept-Language", "zh-CN,zh;q=0.9");
        httpGet.addHeader("Connection", "keep-alive");
        httpGet.addHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36");

    }

    /**
     * 添加请求头
     * @param httpPost
     */
    @Deprecated
    private static void addHeaderParams(HttpPost httpPost) {
        logger.info("addHeaderParams in httpPost");
        httpPost.addHeader("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        httpPost.addHeader("Accept-Encoding", "gzip, deflate, br");

        httpPost.addHeader("Accept-Language", "zh-CN,zh;q=0.9");
        httpPost.addHeader("Connection", "keep-alive");
        httpPost.addHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36");

    }


    /**
     * get post 请求结果包装
     */
    public static class ResponseBody {
        // 请求返回的结果数据
        private String data;
        // 本次请求的cookie
        private CookieStore cookieStore;
        public ResponseBody(String data, CookieStore cookieStore) {
            this.data = data;
            this.cookieStore = cookieStore;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public CookieStore getCookieStore() {
            return cookieStore;
        }

        public void setCookieStore(CookieStore cookieStore) {
            this.cookieStore = cookieStore;
        }
    }

}
