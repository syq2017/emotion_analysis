package common.util;

import scala.collection.parallel.immutable.ParRange;
import scala.util.parsing.combinator.testing.Str;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 读取配置文件工具类
 */
public class PropertyUtil {

    private static final Properties properties = new Properties();
    private static final String ConfigurationPath = "F:\\software\\IDEA\\workspace\\spark_emotion_anslysis\\src\\main\\resources\\app.property";
    private static FileReader fileReader = null;
    private static boolean isPropertyOpened = false;
    private static boolean isPropertyClosed = false;

    private static synchronized void open(){
        if(isPropertyOpened){
            return;
        }
        try {
            fileReader = new FileReader(ConfigurationPath);
            properties.load(fileReader);
            isPropertyOpened = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static synchronized void close(){
        if(isPropertyClosed){
           return;
        }
        try {
            fileReader.close();
            isPropertyClosed = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String key){
        if(!isPropertyOpened){
            open();
        }
        return (String) properties.get(key);
    }

    public static List<String> getAllMovieTypes(){
        String movieType = get("movie_type");
        String[] movieTypes = movieType.split(",");
        List<String> types = new ArrayList<>(29);
        for(String type : movieTypes){
            types.add(type);
        }
        return types;
    }

    public static void main(String[] args) throws IOException {
        String form_email = get("form_email");
        String form_password = get("form_password");
        System.out.println(form_email);
        System.out.println(form_password);
        List<String> allMovieTypes = getAllMovieTypes();
        System.out.println(allMovieTypes.size());
        allMovieTypes.stream().forEach(x -> System.out.println(x));
    }
}
