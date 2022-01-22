package com.dtstack.taiga.scheduler.server.action.fill;

import com.dtstack.taiga.common.env.EnvironmentContext;
import com.dtstack.taiga.dao.domain.ScheduleTaskTaskShade;
import com.dtstack.taiga.scheduler.dto.fill.FillDataInfoDTO;
import com.dtstack.taiga.scheduler.service.ScheduleTaskTaskService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2021/9/10 3:40 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public abstract class AbstractFillDataTask implements FillDataTask {

    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractFillDataTask.class);
    protected final ApplicationContext applicationContext;

    protected final FillDataInfoDTO fillDataInfoBO;

    protected final ScheduleTaskTaskService scheduleTaskTaskService;

    protected final EnvironmentContext environmentContext;

    public AbstractFillDataTask(ApplicationContext applicationContext, FillDataInfoDTO fillDataInfoDTO) {
        this.applicationContext = applicationContext;
        this.fillDataInfoBO = fillDataInfoDTO;
        this.scheduleTaskTaskService = applicationContext.getBean(ScheduleTaskTaskService.class);
        this.environmentContext = applicationContext.getBean(EnvironmentContext.class);

    }

    @Override
    public Set<Long> getAllList(Set<Long> run) {
        Set<Long> all = Sets.newHashSet(run);

        if (run.size() ==1) {
            // R集合只有一个元素，其实也不用遍历计算有效路径
            LOGGER.info("run size 1,end fillList method");
            return all;
        }

        // 获得R集合所在的dag图的所有边
        Map<Long, List<Long>> nodeSide = getNodeSideByRun(run);

        LOGGER.info("run:{} nodeSide:{} ",run,nodeSide);
        // 开始查询有效路径
        Collection<DAGNode> nodes = getDAGNodeByRun(run,nodeSide);

        // 封装节点信息
        for (DAGNode node : nodes) {
            List<DAGPath> paths = node.getPaths();
            for (DAGPath dagPath : paths) {
                all.addAll(dagPath.RTaskKeys);
            }
        }
        return all;
    }

    /**
     * 查询R集合所在的dag图的所有边
     *
     * @param run R集合
     * @return nodeSide
     */
    private Map<Long, List<Long>> getNodeSideByRun(Set<Long> run) {
        Map<Long, List<Long>> nodeSide = Maps.newHashMap();
        Integer fillDataLimitSize = environmentContext.getFillDataLimitSize();

        List<Long> needFindChildTaskKeyList = Lists.newArrayList(run);
        while (CollectionUtils.isNotEmpty(needFindChildTaskKeyList)) {
            List<Long> childTaskKeys = Lists.newArrayList();
            // 切割,防止runList太大
            List<List<Long>> needFindChildTaskKeyListPartitions = Lists.partition(needFindChildTaskKeyList, fillDataLimitSize);
            needFindChildTaskKeyListPartitions.forEach(needFindChildTaskKeyListPartition->childTaskKeys.addAll(getListChildTaskKeyAndFillNodeSide(needFindChildTaskKeyListPartition,nodeSide)));
            needFindChildTaskKeyList = childTaskKeys;
        }

        return nodeSide;
    }

    /**
     * 获得RunList的下游taskKey和填充nodeSide
     *
     * @param needFindChildTaskKeyList 需要查询下游的节点的集合列表
     * @param nodeSide 边的关系映射
     * @return
     */
    private List<Long> getListChildTaskKeyAndFillNodeSide(List<Long> needFindChildTaskKeyList,Map<Long, List<Long>> nodeSide) {
        List<Long> childTaskKeys = Lists.newArrayList();
        List<ScheduleTaskTaskShade> scheduleTaskTaskShades = getScheduleTaskTaskShades(needFindChildTaskKeyList);

        if (CollectionUtils.isNotEmpty(scheduleTaskTaskShades)) {
            Map<Long, List<ScheduleTaskTaskShade>> keyMaps = scheduleTaskTaskShades.stream().collect(Collectors.groupingBy(ScheduleTaskTaskShade::getParentTaskId));

            for (Map.Entry<Long, List<ScheduleTaskTaskShade>> entry : keyMaps.entrySet()) {
                Long key = entry.getKey();
                if (!nodeSide.containsKey(key)) {
                    List<ScheduleTaskTaskShade> value = entry.getValue();
                    List<Long> childValueTaskKeys = value.stream().map(ScheduleTaskTaskShade::getTaskId).collect(Collectors.toList());
                    nodeSide.put(key, childValueTaskKeys);
                    childTaskKeys.addAll(childValueTaskKeys);
                }
            }
        }
        return childTaskKeys;
    }

    /**
     * 查询下游节点
     *
     * @param taskIds
     * @return
     */
    protected List<ScheduleTaskTaskShade> getScheduleTaskTaskShades(List<Long> taskIds) {
        return CollectionUtils.isNotEmpty(taskIds) ?
                scheduleTaskTaskService.lambdaQuery().in(ScheduleTaskTaskShade::getParentTaskId, taskIds).list() :
                Lists.newArrayList();
    }

    /**
     * 初始化路径
     * @param aimNode
     * @return
     */
    protected DAGPath initPath(Long aimNode) {
        DAGPath dagPath = new DAGPath();
        dagPath.setTop(aimNode);
        dagPath.setRTop(aimNode);
        dagPath.setRTaskKeys(Lists.newArrayList(aimNode));
        return dagPath;
    }

    /**
     * 查询有效路径
     *
     * @param run 运行集合
     * @param nodeSide 边集合
     * @return
     */
    protected Collection<DAGNode> getDAGNodeByRun(Set<Long> run, Map<Long, List<Long>> nodeSide) {
        Set<Long> validPathTaskKey = Sets.newHashSet();
        Map<Long,DAGNode> dagNodes = Maps.newHashMap();
        for (Long aimNode : run) {
            if (!validPathTaskKey.contains(aimNode)) {
                DAGNode dagNode = new DAGNode();
                dagNode.setAimNode(aimNode);
                DAGPath dagPath = initPath(aimNode);
                dagNode.setPaths(fillDAGPaths(aimNode,dagPath,validPathTaskKey,dagNodes,run,nodeSide));
                validPathTaskKey.add(aimNode);
                dagNodes.put(aimNode,dagNode);
            }
        }

        return dagNodes.values();
    }

    /**
     * 查询有效路径
     *
     * @param aimNode   目标节点
     * @param dagPath   当前路径
     * @param validPathTaskKey 完成的节点
     * @param dagNodes  节点映射
     * @param run       R集合
     * @param nodeSide  边
     * @return 有效路径
     */
    protected abstract List<DAGPath> fillDAGPaths(Long aimNode,
                                                  DAGPath dagPath,
                                                  Set<Long> validPathTaskKey,
                                                  Map<Long, DAGNode> dagNodes,
                                                  Set<Long> run,
                                                  Map<Long, List<Long>> nodeSide);

    protected static class DAGNode {
        // 目标节点（R集合里面的节点）
        private Long aimNode;
        // 经过该节点的所有路径
        private List<DAGPath> paths;

        public Long getAimNode() {
            return aimNode;
        }

        public void setAimNode(Long aimNode) {
            this.aimNode = aimNode;
        }

        public List<DAGPath> getPaths() {
            return paths;
        }

        public void setPaths(List<DAGPath> paths) {
            this.paths = paths;
        }
    }

    protected static class DAGPath {
        // 运行中的路径头
        private Long top;
        // 运行中的路径尾
        private Long end;
        // R集合在路径中的头
        private Long RTop;
        // R集合在路径中的未
        private Long REnd;
        // 路径的元素
        private List<Long> taskKeys = Lists.newArrayList();
        // R集合的路径元素
        private List<Long> RTaskKeys = Lists.newArrayList();

        public DAGPath() {
        }

        public DAGPath(DAGPath dagPath) {
            this.top = dagPath.getTop();
            this.end = dagPath.getEnd();
            this.RTop = dagPath.getRTop();
            this.REnd = dagPath.getREnd();
            this.taskKeys = Lists.newArrayList(dagPath.getTaskKeys());
            this.RTaskKeys = Lists.newArrayList(dagPath.getRTaskKeys());
        }

        public Long getTop() {
            return top;
        }

        public void setTop(Long top) {
            this.top = top;
        }

        public Long getEnd() {
            return end;
        }

        public void setEnd(Long end) {
            this.end = end;
        }

        public Long getRTop() {
            return RTop;
        }

        public void setRTop(Long RTop) {
            this.RTop = RTop;
        }

        public Long getREnd() {
            return REnd;
        }

        public void setREnd(Long REnd) {
            this.REnd = REnd;
        }

        public List<Long> getTaskKeys() {
            return taskKeys;
        }

        public void setTaskKeys(List<Long> taskKeys) {
            this.taskKeys = taskKeys;
        }

        public List<Long> getRTaskKeys() {
            return RTaskKeys;
        }

        public void setRTaskKeys(List<Long> RTaskKeys) {
            this.RTaskKeys = RTaskKeys;
        }

        public void addChildTaskKey(Long childTaskKey) {
            List<Long> taskKeys = this.getTaskKeys();
            taskKeys.add(childTaskKey);
        }

        public void addChildRTaskKey(Long childTaskKey) {
            addChildTaskKey(childTaskKey);
            RTaskKeys.addAll(taskKeys);
            taskKeys.clear();
        }

        public void addChildRTaskKeys(List<Long> rTaskKeys) {
            RTaskKeys.addAll(rTaskKeys);
        }
    }


}
