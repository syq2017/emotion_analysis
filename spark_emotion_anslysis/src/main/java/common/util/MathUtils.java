package common.util;

/**
 * Created by cage on 2018-12-08
 */
public class MathUtils {
    /**
     * 判断奇偶数
     * @param a
     * @return 奇数，返回true;反之
     */
    public static boolean isOdd(int a){
        if((a & 1) == 1){
            return true;
        }
        return false;
    }

    /**
     * 判断奇偶数
     * @param a
     * @return 奇数，返回true;反之
     */
    public static boolean isOdd(long a){
        if((a & 1) == 1){
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        long a = 1l;
        System.out.println(isOdd(a));
    }

}
