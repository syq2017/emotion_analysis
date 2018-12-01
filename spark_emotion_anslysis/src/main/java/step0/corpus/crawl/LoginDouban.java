package step0.corpus.crawl;

import com.google.gson.Gson;
import common.beans.Captcha;
import common.constants.Constants;
import common.util.HttpUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
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

    /**
     * 登陆豆瓣,跳转到 电影排行榜，然后在跳转到 某个类型下的电影排名
     */
    public static CookieStore loginDouban(HttpGet httpGet, HttpPost httpPost) {

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
        CookieStore cookieStore = null;
        try {
            //发起登录post请求
            httpPost.setURI(new URI(loginSrc));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            CloseableHttpResponse httpResponse = HttpUtils.httpClient.execute(httpPost);
            if(httpResponse.getStatusLine().getStatusCode() != 200) {
                return null;
            }
            cookieStore = HttpUtils.httpClient.getCookieStore();
//            httpPost.releaseConnection();
            //跳转到 电影排行榜 页面
            httpGet.setURI(new URI(redirUrl));
            HttpUtils.httpClient.setCookieStore(cookieStore);
            CloseableHttpResponse response = HttpUtils.httpClient.execute(httpGet);
            if(response.getStatusLine().getStatusCode() != 200) {
                return null;
            }
            response.close();
            httpGet.releaseConnection();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return cookieStore;
    }


    /**
     * @return token
     */
    public static String getLoginAuthCodeImgToken() {
        HttpGet httpGet = new HttpGet(captchaUrl);
        Captcha captcha = null;
        try{
            CloseableHttpResponse response = HttpUtils.httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity, "utf-8");
            response.close();
            Gson gson = new Gson();
            captcha = gson.fromJson(content, Captcha.class);
            downAuthCodeImg("https:" + captcha.getUrl(), Constants.AUTH_CODE_PATH);
        }catch (Exception e) {
            e.printStackTrace();
        }
        if (captcha != null) {
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
        if (storePathParent.exists()) {
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
            CloseableHttpResponse response = HttpUtils.httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            inputStream = entity.getContent();
            int i = -1;
            byte[] bytes = new byte[1024];
            outputStream = new FileOutputStream(storeFile);
            while ((i = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes);
            }
            inputStream.close();
            outputStream.flush();
            outputStream.close();
            response.close();
            System.out.println("验证码已经下载到：" + storeFile);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.setProperty("log4j.configuration","file:/Users/shiyuquan/Downloads/spark_emotion_anslysis/src/main/resources/slf4j.properties");
//        HttpUtils.getHttpGet();
        loginDouban(HttpUtils.getHttpGet(), HttpUtils.getHttpPost());
    }
}
