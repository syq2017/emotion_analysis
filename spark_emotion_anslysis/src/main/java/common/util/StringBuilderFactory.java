package common.util;

import com.huaban.analysis.jieba.JiebaSegmenter;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * Created by cage on 2018-12-08
 */
public class StringBuilderFactory extends BasePooledObjectFactory<StringBuilder> {

    @Override
    public StringBuilder create() {
        return new StringBuilder();
    }

    @Override
    public PooledObject<StringBuilder> wrap(StringBuilder stringBuilder) {
        return new DefaultPooledObject<>(stringBuilder);
    }

    /**
     * StringBuilder对象归还时，需要进行清理
     * @param p
     * @throws Exception
     */
    @Override
    public void passivateObject(PooledObject<StringBuilder> p) throws Exception {
        p.getObject().setLength(0);
    }
}
