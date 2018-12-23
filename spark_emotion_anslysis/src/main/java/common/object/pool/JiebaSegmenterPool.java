package common.object.pool;

import com.huaban.analysis.jieba.JiebaSegmenter;
import common.object.factory.JiebaSegmenterFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * Created by cage
 */
public class JiebaSegmenterPool {
    //JiebaSegmenter 对象池
    public static ObjectPool<JiebaSegmenter> jiebaSegmenterPool =
            new GenericObjectPool<>(new JiebaSegmenterFactory());

    public static ObjectPool<JiebaSegmenter> getJiebaSegmenterPool() {
        return jiebaSegmenterPool;
    }
}
