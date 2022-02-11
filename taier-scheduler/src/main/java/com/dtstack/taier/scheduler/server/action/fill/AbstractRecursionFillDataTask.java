package com.dtstack.taier.scheduler.server.action.fill;

import com.alibaba.fastjson.JSON;
import com.dtstack.taier.scheduler.dto.fill.FillDataInfoDTO;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Auther: dazhi
 * @Date: 2021/9/10 3:40 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public abstract class AbstractRecursionFillDataTask extends AbstractFillDataTask {

    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractRecursionFillDataTask.class);

    public AbstractRecursionFillDataTask(ApplicationContext applicationContext, FillDataInfoDTO fillDataInfoDTO) {
        super(applicationContext, fillDataInfoDTO);
    }

    @Override
    public List<DAGPath> fillDAGPaths(Long aimPath,
                                      DAGPath dagPath,
                                      Set<Long> validPathTaskKey,
                                      Map<Long, DAGNode> dagNodes,
                                      Set<Long> run,
                                      Map<Long, List<Long>> nodeSide) {
        LOGGER.info("{} start fillDAGPath ",aimPath);
        List<Long> childTaskKeys = nodeSide.get(aimPath);
        List<DAGPath> paths = Lists.newArrayList();
        if (CollectionUtils.isEmpty(childTaskKeys)) {
            // 遍历到子节点，说明该路径已经遍历结束，判断这条路径是否有效
            LOGGER.info("{} end dagPath:{}",aimPath, JSON.toJSONString(dagPath));
            if (dagPath.getREnd() != null) {
                paths.add(dagPath);
            }
        } else if (childTaskKeys.size() == 1) {
            // 一个节点，直接复用原来的path
            Long childTaskKey = childTaskKeys.get(0);
            fillPaths(dagPath, validPathTaskKey, dagNodes, run, nodeSide, childTaskKey, paths);
        } else {
            // 2个或者两个以上节点，复制path
            for (Long childTaskKey : childTaskKeys) {
                DAGPath cpDagPath = new DAGPath(dagPath);
                fillPaths(cpDagPath, validPathTaskKey, dagNodes, run, nodeSide, childTaskKey, paths);
            }
        }

        return paths;
    }

    private void fillPaths(DAGPath dagPath,
                           Set<Long> validPathTaskKey,
                           Map<Long, DAGNode> dagNodes,
                           Set<Long> run,
                           Map<Long, List<Long>> nodeSide,
                           Long childTaskKey,
                           List<DAGPath> paths) {
        if (run.contains(childTaskKey)) {
            // child属于R集合元素，
            // 第一点说明最外层遍历不需要遍历这个节点，且path有了Rend节点，
            // 第二点如果这个节点前面已经遍历过，那么久可以拿来直接用，而不需要遍历
            validPathTaskKey.add(childTaskKey);
            dagPath.addChildRTaskKey(childTaskKey);
            DAGNode childNode = dagNodes.get(childTaskKey);
            if (childNode == null) {
                // childNode是空的，说明前面没有对这个节点遍历过，那么需要继续递归遍历这个节点
                dagPath.setREnd(childTaskKey);
                dagPath.setEnd(childTaskKey);
                paths.addAll(fillDAGPaths(childTaskKey, dagPath, validPathTaskKey, dagNodes, run, nodeSide));
            } else {
                // 该节点的树已经遍历完了，合并树操作
                margePath(dagPath, paths, childNode);
            }
        } else {
            // child不属于R集合，继续往下递归
            dagPath.addChildTaskKey(childTaskKey);
            dagPath.setEnd(childTaskKey);
            paths.addAll(fillDAGPaths(childTaskKey, dagPath, validPathTaskKey, dagNodes, run, nodeSide));
        }
    }

    private void margePath(DAGPath dagPath, List<DAGPath> paths, DAGNode childNode) {
        List<DAGPath> pathsChildNodes = childNode.getPaths();
        if (CollectionUtils.isEmpty(pathsChildNodes)) {
            // 说明这棵树是单节点,可以直接停止
            dagPath.setEnd(childNode.getAimNode());
            dagPath.setREnd(childNode.getAimNode());
            paths.add(dagPath);
        } else {
            for (DAGPath pathsChildNode : pathsChildNodes) {
                pathsChildNode.setTop(dagPath.getTop());
                pathsChildNode.setRTop(dagPath.getRTop());
                pathsChildNode.addChildRTaskKeys(dagPath.getRTaskKeys());
            }
            paths.addAll(pathsChildNodes);
        }
    }

}
