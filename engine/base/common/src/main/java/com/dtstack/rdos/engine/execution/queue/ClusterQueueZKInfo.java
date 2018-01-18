package com.dtstack.rdos.engine.execution.queue;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Reason:
 * Date: 2018/1/15
 * Company: www.dtstack.com
 * @author xuchao
 */

public class ClusterQueueZKInfo {

    /**key: engineType*/
    private Map<String, EngineTypeQueueZKInfo> infoMap = Maps.newHashMap();

    /**key1:address, key2:engineType, key3: groupName, value: priority*/
    public ClusterQueueZKInfo(Map<String, Map<String, Map<String, Integer>>> clusterQueueInfo){
        clusterQueueInfo.forEach((address, engineTypeZkInfo) -> {
            engineTypeZkInfo.forEach((engineType, groupQueueInfo) -> {
                infoMap.putIfAbsent(engineType, new EngineTypeQueueZKInfo(engineType));
                EngineTypeQueueZKInfo engineTypeQueueZKInfo = infoMap.get(engineType);

                engineTypeQueueZKInfo.put(address, groupQueueInfo);
            });
        });
    }

    public EngineTypeQueueZKInfo getEngineTypeQueueZkInfo(String engineType){
        return infoMap.get(engineType);
    }

    class EngineTypeQueueZKInfo{

        private String engineType;

        /**key:address*/
        private Map<String, GroupQueueZkInfo> groupQueueZkInfoMap = Maps.newHashMap();

        EngineTypeQueueZKInfo(String engineType){
            this.engineType = engineType;
        }

        public void put(String address, Map<String, Integer> priorityInfo){
            GroupQueueZkInfo groupQueueZkInfo = new GroupQueueZkInfo(address, priorityInfo);
            groupQueueZkInfoMap.put(address, groupQueueZkInfo);
        }

        public Map<String, GroupQueueZkInfo> getGroupQueueZkInfoMap() {
            return groupQueueZkInfoMap;
        }
    }

    class GroupQueueZkInfo{

        private String address;

        private Map<String, Integer> priorityInfo = Maps.newHashMap();

        GroupQueueZkInfo(String address, Map<String, Integer> priorityInfo){
            this.address = address;
            this.priorityInfo = priorityInfo;
        }

        public Map<String, Integer> getPriorityInfo() {
            return priorityInfo;
        }
    }
}
