package common.object.factory;

import com.huaban.analysis.jieba.JiebaSegmenter;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * Created by cage on 2018-12-08
 */
public class JiebaSegmenterFactory extends BasePooledObjectFactory<JiebaSegmenter> {

    @Override
    public JiebaSegmenter create() {
        synchronized (this) {
            return new JiebaSegmenter();
        }
    }

    @Override
    public PooledObject<JiebaSegmenter> wrap(JiebaSegmenter jiebaSegmenter) {
        synchronized (this) {
            return new DefaultPooledObject<>(jiebaSegmenter);
        }
    }



}
