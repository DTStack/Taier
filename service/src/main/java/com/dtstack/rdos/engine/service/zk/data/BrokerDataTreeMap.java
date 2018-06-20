package com.dtstack.rdos.engine.service.zk.data;

import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * 只用于zkTaskId做的map
 * Date: 2017/11/7
 * Company: www.dtstack.com
 * @author xuchao
 */

public class BrokerDataTreeMap extends TreeMap<String, Byte> {

    private final static String interval = "_";

    public BrokerDataTreeMap(){

        super(new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {

                if(o1 == null){
                    return -1;
                }

                if(o2 == null){
                    return 1;
                }

                String[] o1Arr = o1.split(interval);
                String[] o2Arr = o2.split(interval);

                if(o1Arr.length < 2){
                    return -1;
                }

                if(o2Arr.length < 2){
                    return 1;
                }

                if(String.join(interval, o1Arr).equals(String.join(interval, o2Arr))){
                    return 0;
                }

                if(StringUtils.join(o1Arr, interval, 0, 2).equals(StringUtils.join(o2Arr, interval, 0, 2))){
                    return 0;
                }

                return o1.compareTo(o2);
            }
        });
    }

    @Override
    public Byte put(String key, Byte value) {
        remove(key);
        return super.put(key, value);
    }


}
