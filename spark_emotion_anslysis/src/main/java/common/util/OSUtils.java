package common.util;

/**
 * Created by cage
 */
public class OSUtils {

    /**
     * @return  逻辑cpu个数
     */
    public static int getCpuCores() {
        return Runtime.getRuntime().availableProcessors();
    }

}
