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

package com.dtstack.taier.develop.utils.develop.sync.template;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.develop.common.template.Setting;
import org.apache.commons.lang.StringUtils;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/15
 */
public class DefaultSetting implements Setting {

    public DefaultSetting() {
    }

    public DefaultSetting(Integer channel) {
        this.channel = channel;
    }

    public DefaultSetting(Integer channel, Double speed, Integer record, Double percentage) {
        this.channel = channel;
        this.speed = speed;
        this.record = record;
        this.percentage = percentage;
    }

    /* speed 流控*/
    /**
     * 控制并发数
     */
    private Integer channel = 1;
    /**
     * 控制同步的速度
     */
    private Double speed;

    /* 脏数据控制 */
    /**
     * 脏数据记录阈值
     */
    private Integer record;
    /**
     * 脏数据占比阈值
     */
    private Double percentage;

    private String hadoopConfig;

    private String path;

    private Integer isSaveDirty = 0;

    private boolean isRestore;

    private String restoreColumnName;

    private int restoreColumnIndex;

    private long maxRowNumForCheckpoint;

    public boolean getIsRestore() {
        return isRestore;
    }

    public void setIsRestore(boolean restore) {
        isRestore = restore;
    }

    public String getRestoreColumnName() {
        return restoreColumnName;
    }

    public void setRestoreColumnName(String restoreColumnName) {
        this.restoreColumnName = restoreColumnName;
    }

    public long getMaxRowNumForCheckpoint() {
        return maxRowNumForCheckpoint;
    }

    public void setMaxRowNumForCheckpoint(long maxRowNumForCheckpoint) {
        this.maxRowNumForCheckpoint = maxRowNumForCheckpoint;
    }

    public Integer getIsSaveDirty() {
        return isSaveDirty;
    }

    public void setIsSaveDirty(Integer isSaveDirty) {
        this.isSaveDirty = isSaveDirty;
    }

    public String getHadoopConfig() {
        return hadoopConfig;
    }

    public void setHadoopConfig(String hadoopConfig) {
        this.hadoopConfig = hadoopConfig;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Integer getRecord() {
        return record;
    }

    public void setRecord(Integer record) {
        this.record = record;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    @Override
    public JSONObject toSettingJson() {
        JSONObject setting = new JSONObject(true);

        if(this.isSaveDirty == 1){
            JSONObject dirty = new JSONObject(true);
            if(StringUtils.isNotEmpty(this.path)){
                dirty.put("path",this.path);
            }

            if(StringUtils.isNotEmpty(this.hadoopConfig)){
                JSONObject hadoopConfig = new JSONObject();
                JSONObject otherConfig = JSONObject.parseObject(this.hadoopConfig);
                for (String key : otherConfig.keySet()) {
                    hadoopConfig.put(key,otherConfig.getString(key));
                }
                dirty.put("hadoopConfig",hadoopConfig);
            }

            setting.put("dirty",dirty);
        }

        JSONObject speed = new JSONObject(true);
        if (this.getChannel() != null) {
            speed.put("channel", this.getChannel());
        }
        if (this.getSpeed() != null) {
            if (this.getSpeed()<0){
                speed.put("bytes", 0);
            }else {
                speed.put("bytes", (long)(this.getSpeed() * 1024 * 1024));
            }
        }

        JSONObject errorLimit = new JSONObject(true);
        if (this.getRecord() != null) {
            errorLimit.put("record", this.getRecord());
        }
        if (this.getPercentage() != null) {
            errorLimit.put("percentage", this.getPercentage());
        }

        JSONObject restore = new JSONObject();
        restore.put("isRestore", isRestore);
        restore.put("restoreColumnName", restoreColumnName == null ? "" : restoreColumnName);
        restore.put("maxRowNumForCheckpoint", maxRowNumForCheckpoint);
        restore.put("restoreColumnIndex", restoreColumnIndex);
        if(isRestore && StringUtils.isEmpty(restoreColumnName)){
            throw new RdosDefineException("开启断点续传时必须指定恢复字段");
        }

        setting.put("speed", speed);
        setting.put("errorLimit", errorLimit);
        setting.put("restore", restore);
        return setting;
    }

    @Override
    public String toSettingJsonString() {
        return toSettingJson().toJSONString();
    }

    public void setRestoreColumnIndex(int restoreColumnIndex) {
        this.restoreColumnIndex = restoreColumnIndex;
    }
}
