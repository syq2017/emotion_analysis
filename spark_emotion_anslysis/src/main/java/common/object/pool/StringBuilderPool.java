package common.object.pool;

import common.object.factory.StringBuilderFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * Created by cage
 */
public class StringBuilderPool {
    //StringBuilder对象池
    public static ObjectPool<StringBuilder> stringBuilderPool =
            new GenericObjectPool<>(new StringBuilderFactory());
}
