//package com.dtstack.engine.common.queue;
//
//import com.google.common.collect.Maps;
//
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//
///**
// * Reason:
// * Date: 2018/1/15
// * Company: www.dtstack.com
// *
// * @author xuchao
// */
//
//public class ClusterQueueInfo {
//
//    private static ClusterQueueInfo clusterQueueInfo = new ClusterQueueInfo();
//
//    public static ClusterQueueInfo getInstance() {
//        return clusterQueueInfo;
//    }
//
//    /**
//     * key: engineType
//     */
//    private volatile Map<String, EngineTypeQueueInfo> infoMap = Maps.newHashMap();
//
//    private ClusterQueueInfo() {
//    }
//
//    public boolean isEmpty() {
//        return infoMap.isEmpty();
//    }
//
//    /**
//     * key1:address, key2:engineType, key3: groupName, value: GroupInfo
//     */
//    public void updateClusterQueueInfo(Map<String, Map<String, Map<String, GroupInfo>>> clusterQueueInfo) {
//        Set<String> engineTypes = new HashSet<>();
//        clusterQueueInfo.values().forEach(engineTypeInfo -> engineTypes.addAll(engineTypeInfo.keySet()));
//        Map<String, EngineTypeQueueInfo> newInfoMap = Maps.newHashMap();
//        clusterQueueInfo.forEach((address, engineTypeInfo) -> {
//            engineTypes.forEach(engineType->{
//                EngineTypeQueueInfo engineTypeQueueInfo = newInfoMap.computeIfAbsent(engineType, k -> new EngineTypeQueueInfo(engineType));
//                Map<String, GroupInfo> groupQueueInfo = engineTypeInfo.getOrDefault(engineType,  new HashMap<String,GroupInfo>());
//                engineTypeQueueInfo.put(address, groupQueueInfo);
//            });
//        });
//        infoMap = newInfoMap;
//    }
//
//    public EngineTypeQueueInfo getEngineTypeQueueInfo(String engineType) {
//        return infoMap.get(engineType);
//    }
//
//    public class EngineTypeQueueInfo {
//
//        private String engineType;
//
//        /**
//         * key:address
//         */
//        private Map<String, GroupQueueInfo> groupQueueInfoMap = Maps.newHashMap();
//
//        EngineTypeQueueInfo(String engineType) {
//            this.engineType = engineType;
//        }
//
//        public void put(String address, Map<String, GroupInfo> groupInfo) {
//            GroupQueueInfo groupQueueInfo = new GroupQueueInfo(address, groupInfo);
//            groupQueueInfoMap.put(address, groupQueueInfo);
//        }
//
//        public Map<String, GroupQueueInfo> getGroupQueueInfoMap() {
//            return groupQueueInfoMap;
//        }
//
//        public String getEngineType() {
//            return engineType;
//        }
//    }
//
//    public class GroupQueueInfo {
//
//        private String address;
//
//        private Map<String, GroupInfo> groupInfo = Maps.newHashMap();
//
//        GroupQueueInfo(String address, Map<String, GroupInfo> groupInfo) {
//            this.address = address;
//            this.groupInfo = groupInfo;
//        }
//
//        public Map<String, GroupInfo> getGroupInfo() {
//            return groupInfo;
//        }
//
//        public String getAddress() {
//            return address;
//        }
//    }
//}
