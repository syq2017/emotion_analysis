package step0.corpus.crawl;

import com.google.gson.Gson;
import common.beans.Captcha;
import common.constants.Constants;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.util.parsing.combinator.PackratParsers;

import javax.xml.ws.http.HTTPBinding;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cage on 2018-11-25
 */
public class LoginDouban {

    private static Logger logger = LoggerFactory.getLogger(LoginDouban.class.getName());

    private static String redirUrl = "https://movie.douban.com/chart";    //  登录成功后要跳转的网页
    private static String loginSrc = "https://accounts.douban.com/login";
    private static String formEmail = "15209224695";    //  用户名
    private static String formPassword = "syq5201314";    //  密码
    private static String login ="登录";
    private static String captchaUrl = "https://www.douban.com/j/misc/captcha"; //验证码图片请求
    private static String captchaId = null;
    private static String captchaSolution = null;
    public static HttpClient httpClient = new DefaultHttpClient();
    /**
     * 登陆豆瓣,跳转到 电影排行榜，然后在跳转到 某个类型下的电影排名
     */
    public static void loginDouban(){

        //获取登录验证码图片的token(可理解位针对本次登陆的一个验证码图片id)
        captchaId = getLoginAuthCodeImgToken();
        System.out.println("请输入 验证码");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            captchaSolution = bufferedReader.readLine();
        } catch (Exception e){
            e.printStackTrace();
        }
        List<NameValuePair> list=new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("redir", redirUrl));
        list.add(new BasicNameValuePair("form_email", formEmail));
        list.add(new BasicNameValuePair("form_password", formPassword));
        list.add(new BasicNameValuePair("captcha-solution", captchaSolution));
        list.add(new BasicNameValuePair("captcha-id", captchaId));
        list.add(new BasicNameValuePair("login", login));

        try {
            //发起登录post请求
            HttpPost httpPost = new HttpPost(loginSrc);
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            httpClient.execute(httpPost);
            Header[] cookies = httpPost.getHeaders("Cookie");
            for (Header header : cookies){
                String value = header.getValue();
                System.out.println(value);
            }
            System.out.println("===============");
            httpPost.releaseConnection();
            //跳转到 电影排行榜 页面
            HttpGet httpGet = new HttpGet(redirUrl);
            httpClient.execute(httpGet);
            cookies = httpGet.getHeaders("Cookie");
            for (Header header : cookies){
                String value = header.getValue();
                System.out.println(value);
            }
            httpGet.releaseConnection();
            //开始抓取数据，每抓取一次，休眠

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * @return token
     */
    public static String getLoginAuthCodeImgToken() {
        HttpGet httpGet = new HttpGet(captchaUrl);
        Captcha captcha = null;
        try{
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity, "utf-8");
            Gson gson = new Gson();
            captcha = gson.fromJson(content, Captcha.class);
            downAuthCodeImg("https:" + captcha.getUrl(), Constants.AUTH_CODE_PATH);
        }catch (Exception e){
            e.printStackTrace();
        }
        if (captcha != null){
            return captcha.getToken();
        }else {
            return null;
        }
    }

    /**
     * @param downUrl 下载验证码图片的URL
     * @param storePath 验证码图片存储路径
     */
    private static void downAuthCodeImg(String downUrl, String storePath) {
        File storePathParent = new File(storePath);
        if (storePathParent.exists()){
            storePathParent.mkdirs();
        }
        File storeFile = new File(storePath + "authcode.jpg");
        if (storeFile.exists()){
            storeFile.delete();
        }
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        HttpGet httpGet = new HttpGet(downUrl);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            inputStream = entity.getContent();
            int i = -1;
            byte[] bytes = new byte[1024];
            outputStream = new FileOutputStream(storeFile);
            while ((i = inputStream.read(bytes)) != -1){
                outputStream.write(bytes);
            }
            inputStream.close();
            outputStream.flush();
            outputStream.close();
            System.out.println("验证码已经下载到：" + storeFile);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void addHeaderParams(HttpGet httpGet) {
        httpGet.addHeader("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        httpGet.addHeader("Accept-Encoding", "gzip, deflate, br");
        httpGet.addHeader("Accept-Language", "zh-CN,zh;q=0.9");
        httpGet.addHeader("Connection", "keep-alive");
        httpGet.addHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36");

    }

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

    public static void main(String[] args) {
        System.setProperty("log4j.configuration","file:/Users/shiyuquan/Downloads/spark_emotion_anslysis/src/main/resources/slf4j.properties");
        loginDouban();
    }
}
