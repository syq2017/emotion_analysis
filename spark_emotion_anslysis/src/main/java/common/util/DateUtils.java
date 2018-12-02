package common.util;

import com.sun.org.apache.regexp.internal.RE;

import java.util.Random;

/**
 * Created by cage
 */
public class DateUtils {

    /**
     * 随机数：休眠时间
<<<<<<< HEAD
     * @return [5 10]
=======
     * @return [15 30]
>>>>>>> 8888fe740d47ddc5c5df5a18fa0f376823c796ae
     */
    public static int getSleepSeconds() {
        Random random = new Random();
        return random.nextInt(6) + 5;
    }

    /**
     *随机数：休眠时间-短
     * @return [3 5]
     */
    public static int getShortSleepSeconds() {
        Random random = new Random();
        return random.nextInt(3) + 3;
    }

    /**
     *随机数：休眠时间-短
     * @return [5 15]
     */
    public static int getShortSleepSeconds() {
        Random random = new Random();
        return random.nextInt(10) + 5;
    }

    public static void main(String[] args) {
        System.out.println(getShortSleepSeconds());
        System.out.println(getShortSleepSeconds());
        System.out.println(getShortSleepSeconds());
        System.out.println(getShortSleepSeconds());
        System.out.println(getShortSleepSeconds());
    }
}
