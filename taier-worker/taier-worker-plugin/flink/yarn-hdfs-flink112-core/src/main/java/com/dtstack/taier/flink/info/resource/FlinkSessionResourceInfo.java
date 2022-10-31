/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.flink.info.resource;

import com.dtstack.taier.pluginapi.pojo.JudgeResult;
import com.dtstack.taier.pluginapi.util.MathUtil;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.base.resource.AbstractFlinkResourceInfo;
import com.dtstack.taier.flink.constant.ConfigConstant;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 用于存储从flink上获取的资源信息
 * Date: 2017/11/24
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class FlinkSessionResourceInfo extends AbstractFlinkResourceInfo {

    private static final Logger logger = LoggerFactory.getLogger(FlinkSessionResourceInfo.class);

    private final static ObjectMapper OBJ_MAPPER = new ObjectMapper();

    /**
     * true if flink mode is standalone
     */
    private final boolean standalone;


    public FlinkSessionResourceInfo(boolean standalone){
        this.standalone = standalone;
    }

    @Override
    public JudgeResult judgeSlots(JobClient jobClient) {
        return judgeSessionResource(jobClient);
    }

    private JudgeResult judgeSessionResource(JobClient jobClient) {
        int sqlEnvParallel = 1;
        int mrParallel = 1;

        if (jobClient.getConfProperties().containsKey(ConfigConstant.SQL_ENV_PARALLELISM)) {
            sqlEnvParallel = MathUtil.getIntegerVal(jobClient.getConfProperties().get(ConfigConstant.SQL_ENV_PARALLELISM));
        }

        if (jobClient.getConfProperties().containsKey(ConfigConstant.MR_JOB_PARALLELISM)) {
            mrParallel = MathUtil.getIntegerVal(jobClient.getConfProperties().get(ConfigConstant.MR_JOB_PARALLELISM));
        }

        return judgeFlinkSessionResource(sqlEnvParallel, mrParallel);
    }

    @SuppressWarnings("unchecked")
    public void getFlinkSessionSlots(String message, int flinkSessionSlotCount){
        if(StringUtils.isNotBlank(message)){
            try{
                Map<String, Object> taskManagerInfo = OBJ_MAPPER.readValue(message, Map.class);
                if(taskManagerInfo.containsKey("taskmanagers")){
                    List<Map<String, Object>> taskManagerList = (List<Map<String, Object>>) taskManagerInfo.get("taskmanagers");
                    if (taskManagerList.size()==0){
                        this.addNodeResource(new NodeResourceDetail("1", flinkSessionSlotCount, flinkSessionSlotCount));
                    }else {
                        int totalUsedSlots = 0;
                        int totalFreeSlots = 0;
                        int totalSlotsNumber = 0;
                        for(Map<String, Object> tmp : taskManagerList){
                            int freeSlots = MapUtils.getIntValue(tmp,"freeSlots");
                            int slotsNumber = MapUtils.getIntValue(tmp, "slotsNumber");
                            totalUsedSlots += slotsNumber - freeSlots;
                            totalFreeSlots += freeSlots;
                            totalSlotsNumber += slotsNumber;
                        }
                        if(standalone){
                            this.addNodeResource(new NodeResourceDetail("1", totalFreeSlots, totalSlotsNumber));
                        }else{
                            this.addNodeResource(new NodeResourceDetail("1", flinkSessionSlotCount - totalUsedSlots, flinkSessionSlotCount));
                        }
                    }
                }
            }catch (Exception e){
                logger.error("", e);
            }
        }
    }

}
