package common.util;

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
        return new JiebaSegmenter();
    }

    @Override
    public PooledObject<JiebaSegmenter> wrap(JiebaSegmenter jiebaSegmenter) {
        return new DefaultPooledObject<>(jiebaSegmenter);
    }



}
