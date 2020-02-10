package com.dtstack.engine.master.data;

import com.dtstack.engine.common.util.TaskIdUtil;

import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 只用于zkTaskId做的map
 * Date: 2017/11/7
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class BrokerDataTreeMap<K, V> extends ConcurrentSkipListMap<K, V> {

    public BrokerDataTreeMap(Comparator<? super K> comparator) {
        super(comparator);
    }

    public BrokerDataTreeMap() {
        this(new Comparator<K>() {
            @Override
            public int compare(K o1, K o2) {
                if (o1.equals(o2)) {
                    return 0;
                }
                if (TaskIdUtil.getTaskId((String) o1).equals(TaskIdUtil.getTaskId((String) o2))) {
                    return 0;
                }

                return ((String) o1).compareTo((String) o2);
            }
        });
    }

    public static BrokerDataTreeMap<String, Byte> initBrokerDataTreeMap() {
        return new BrokerDataTreeMap<String, Byte>();
    }
}
