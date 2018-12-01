package common.util;

import com.sun.org.apache.regexp.internal.RE;

import java.util.Random;

/**
 * Created by cage
 */
public class DateUtils {

    /**
     * 随机数：
     * @return [15 30]
     */
    public static int getSleepSeconds() {
        Random random = new Random();
        return random.nextInt(15) + 15;
    }

    public static void main(String[] args) {
        System.out.println(getSleepSeconds());
    }
}
