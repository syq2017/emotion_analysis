package common.object.factory;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * Created by cage
 */
public class StringBuilderFactory extends BasePooledObjectFactory<StringBuilder> {

    @Override
    public StringBuilder create() {
        synchronized (this) {
            return new StringBuilder();
        }
    }

    @Override
    public PooledObject<StringBuilder> wrap(StringBuilder stringBuilder) {
        synchronized (this) {
            return new DefaultPooledObject<>(stringBuilder);
        }
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
