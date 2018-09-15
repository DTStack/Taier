package com.dtstack.rdos.engine.service.zk.data;

import com.dtstack.rdos.engine.service.util.TaskIdUtil;

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

    private final static String interval = "_";

    public BrokerDataTreeMap(Comparator<? super K> comparator) {
        super(comparator);
    }
    public BrokerDataTreeMap() {
        super();
    }

    private static Comparator<String> stringComparator = new Comparator<String>() {
        @Override//1flink_dfefef_0
        public int compare(String o1, String o2) {
            if (!TaskIdUtil.isMigrationJob(o1)) {
                return -1;
            }
            if (!TaskIdUtil.isMigrationJob(o2)) {
                return 1;
            }
            if (o1.equals(o2)) {
                return 0;
            }
            if (TaskIdUtil.getTaskId(o1).equals(TaskIdUtil.getTaskId(o1))) {
                return 0;
            }

            return o1.compareTo(o2);
        }
    };

    public static BrokerDataTreeMap<String, Byte> initBrokerDataTreeMap() {
        return new BrokerDataTreeMap<String, Byte>(stringComparator);
    }
}
