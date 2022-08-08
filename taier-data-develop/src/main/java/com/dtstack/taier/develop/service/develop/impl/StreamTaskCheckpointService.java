package com.dtstack.taier.develop.service.develop.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.util.MathUtil;
import com.dtstack.taier.develop.dto.devlop.CheckPointTimeRangeResultDTO;
import com.dtstack.taier.develop.dto.devlop.EngineJobCheckpoint;
import com.dtstack.taier.develop.dto.devlop.StreamTaskCheckpoint;
import com.dtstack.taier.develop.dto.devlop.StreamTaskCheckpointVO;
import com.dtstack.taier.develop.utils.DataSizeUtil;
import com.dtstack.taier.develop.utils.JsonUtils;
import com.dtstack.taier.develop.utils.ParamsCheck;
import com.dtstack.taier.scheduler.service.ClusterService;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 操作checkpoint相关
 */

@Service
public class StreamTaskCheckpointService {

    private static final Logger logger = LoggerFactory.getLogger(StreamTaskCheckpointService.class);

    private static final String TIME = "time";

    private static final String STORE_SIZE = "storeSize";

    private static final String STORE_PATH = "storePath";

    private static final String DURATION = "duration";

    private static final String KEY_SAVEPOINT = "state.checkpoints.dir";

    @Autowired
    private ClusterService clusterService;

    /**
     * 获取任务的checkpoint可选时间范围
     *
     * @param id
     * @return
     */
    public CheckPointTimeRangeResultDTO getCheckpointTimeRange(String jobId) {

        List<StreamTaskCheckpoint> checkpointList = listByTaskId(jobId);
        if (CollectionUtils.isEmpty(checkpointList)) {
            return new CheckPointTimeRangeResultDTO();
        }

        StreamTaskCheckpoint startNode = checkpointList.get(0);
        StreamTaskCheckpoint endNode = checkpointList.get(checkpointList.size() - 1);
        Timestamp startTrigger = startNode.getCheckpointTrigger();
        Timestamp endTrigger = endNode.getCheckpointTrigger();
        CheckPointTimeRangeResultDTO dto = new CheckPointTimeRangeResultDTO();
        dto.setStartTime(startTrigger.getTime());
        dto.setEndTime(endTrigger.getTime());
        return dto;
    }

    public JSONObject pageQuery(String jobId, Long startTime, Long endTime) {

        List<StreamTaskCheckpoint> streamTaskCheckpointList = listByRangeTime(jobId, startTime, endTime);

        List<JSONObject> simpleHistory = new ArrayList<>();
        for (StreamTaskCheckpoint checkpoint : streamTaskCheckpointList) {
            JSONObject json = new JSONObject();
            if (Objects.isNull(checkpoint.getCheckpointTrigger())) {
                continue;
            }
            json.put(TIME, checkpoint.getCheckpointTrigger().getTime());
            json.put(STORE_SIZE, DataSizeUtil.format(checkpoint.getCheckpointSize()));
            json.put(STORE_PATH, checkpoint.getCheckpointSavepath());
            json.put(DURATION, checkpoint.getCheckpointDuration());
            simpleHistory.add(json);
        }
        //按checkPoint生成时间desc
        if (CollectionUtils.isNotEmpty(simpleHistory)) {
            simpleHistory = simpleHistory.stream().sorted((obj1, obj2) -> {
                if (Optional.ofNullable(obj1.getLong(TIME)).orElse(0L) - Optional.ofNullable(obj2.getLong(TIME)).orElse(0L) > 0) {
                    return -1;
                }
                return 1;
            }).collect(Collectors.toList());
        }
        JSONObject result = new JSONObject();
        // todo
        // result.put("totalSize", DataSizeUtil.format(streamTaskServiceClient.totalSize(taskId).getData()));
        result.put("checkpointList", simpleHistory);
        return result;
    }


    public List<StreamTaskCheckpointVO> getCheckpointListVo(String jobId, Long startTime, Long endTime) {
        List<StreamTaskCheckpoint> taskCheckpointList = getCheckpointList(jobId, startTime, endTime);
        if (CollectionUtils.isEmpty(taskCheckpointList)) {
            return new ArrayList<>();
        }

        List<StreamTaskCheckpointVO> voList = Lists.newArrayList();
        for (StreamTaskCheckpoint checkpoint : taskCheckpointList) {
            if (StringUtils.isBlank(checkpoint.getCheckpointSavepath())) {
                continue;
            }

            Long triggerTime = checkpoint.getCheckpointTrigger().getTime();
            Long cpId = MathUtil.getLongVal(checkpoint.getCheckpointID());
            String extPath = MathUtil.getString(checkpoint.getCheckpointSavepath());

            //判断checkpoint 是否在查询范围内
            if (triggerTime >= startTime && triggerTime <= endTime) {
                StreamTaskCheckpointVO vo = new StreamTaskCheckpointVO(checkpoint.getId(), cpId, triggerTime, extPath);
                voList.add(vo);
            }
        }

        if (CollectionUtils.isEmpty(voList)) {
            return new ArrayList<>();
        }

        return voList.stream().sorted((vo1, vo2) -> vo2.getTime().compareTo(vo1.getTime())).limit(1000).collect(Collectors.toList());
    }

    /**
     * FIXME 添加权限控制
     * 返回指定范围内的可选checkpoint的列表
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public List<StreamTaskCheckpoint> getCheckpointList(String taskId, Long startTime, Long endTime) {

        ParamsCheck.checkNotNull(taskId);
        ParamsCheck.checkNotNull(startTime);
        ParamsCheck.checkNotNull(endTime);
        if (startTime > endTime) {
            throw new DtCenterDefException(ErrorCode.INVALID_PARAMETERS);
        }

        return listByRangeTime(taskId, startTime, endTime);
    }

    public StreamTaskCheckpointVO getSavePoint(String taskId) {

        ParamsCheck.checkNotNull(taskId);
        //todo
        EngineJobCheckpoint apiResponse = new EngineJobCheckpoint(); //streamTaskServiceClient.getSavePoint(streamTask.getTaskId());
        if (apiResponse == null) {
            return new StreamTaskCheckpointVO();
        }
        StreamTaskCheckpoint streamTaskCheckpoint = null;
        streamTaskCheckpoint = JsonUtils.objectToObject(apiResponse, StreamTaskCheckpoint.class);
        return new StreamTaskCheckpointVO(streamTaskCheckpoint.getId(), MathUtil.getLongVal(streamTaskCheckpoint.getCheckpointID()), streamTaskCheckpoint.getCheckpointTrigger().getTime(), streamTaskCheckpoint.getCheckpointSavepath());
    }

    /**
     * 从engine端获取checkpoint
     */
    public List<StreamTaskCheckpoint> listByRangeTime(String taskId, Long triggerStart, Long triggerEnd) {
        List<StreamTaskCheckpoint> checkpoints = new ArrayList<>();
        try {
            //todo
            List<EngineJobCheckpoint> apiResponse = new ArrayList<>();//=  streamTaskServiceClient.getCheckPoint(taskId,triggerStart,triggerEnd);
            if (apiResponse != null) {
                if (CollectionUtils.isNotEmpty(apiResponse)) {
                    for (EngineJobCheckpoint object : apiResponse) {
                        checkpoints.add(JsonUtils.objectToObject(object, StreamTaskCheckpoint.class));
                    }
                }
            }
        } catch (Exception e) {
            throw new DtCenterDefException("get checkpoint by taskId:" + taskId + " error:" + e.getMessage(), e);
        }

        return checkpoints;
    }

    public List<StreamTaskCheckpoint> listByTaskId(String taskId) {
        return listByRangeTime(taskId, null, null);
    }

    /**
     * 获取flink任务checkpoint的存储路径
     *
     * @param tenantId 租户id
     * @return checkpoint存储路径
     */
    public String getSavepointPath(Long tenantId) {
        JSONObject flinkConf = clusterService.getConfigByKey(tenantId, EComponentType.FLINK.getConfName(), null);
        if (flinkConf != null && flinkConf.containsKey(KEY_SAVEPOINT)) {
            String savepointPath = flinkConf.getString(KEY_SAVEPOINT);
            logger.info("savepoint path:{}", savepointPath);

            if (StringUtils.isEmpty(savepointPath)) {
                throw new DtCenterDefException("savepoint path can not be null");
            }

            return savepointPath;
        }
        return null;
    }
}
