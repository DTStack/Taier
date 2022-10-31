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

package com.dtstack.taier.develop.service.template;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.develop.common.template.Setting;

/**
 * @company: www.dtstack.com
 * @Author ：zhiChen
 * @Date ：Created in 17:49 2019-08-21
 */
public class DefaultSetting implements Setting {

    /* speed 流控*/
    /**
     * 控制并发数
     */
    private Integer channel = 1;

    /**
     * 读并行度
     */
    private Integer readerChannel;

    /**
     * 写并行度
     */
    private Integer writerChannel;

    /**
     * 控制同步的速度
     */
    private Double speed;

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

    private Long sourceId;

    private boolean isRestore = false;
    private boolean isStream  = false;

    private String restoreColumnName;

    private Integer restoreColumnIndex = -1;

    private long maxRowNumForCheckpoint;

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public Integer getReaderChannel() {
        return readerChannel;
    }

    public void setReaderChannel(Integer readerChannel) {
        this.readerChannel = readerChannel;
    }

    public Integer getWriterChannel() {
        return writerChannel;
    }

    public void setWriterChannel(Integer writerChannel) {
        this.writerChannel = writerChannel;
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

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public boolean isRestore() {
        return isRestore;
    }

    public void setRestore(boolean restore) {
        isRestore = restore;
    }

    public boolean isStream() {
        return isStream;
    }

    public void setStream(boolean stream) {
        isStream = stream;
    }

    public String getRestoreColumnName() {
        return restoreColumnName;
    }

    public void setRestoreColumnName(String restoreColumnName) {
        this.restoreColumnName = restoreColumnName;
    }

    public Integer getRestoreColumnIndex() {
        return restoreColumnIndex;
    }

    public void setRestoreColumnIndex(Integer restoreColumnIndex) {
        this.restoreColumnIndex = restoreColumnIndex;
    }

    public long getMaxRowNumForCheckpoint() {
        return maxRowNumForCheckpoint;
    }

    public void setMaxRowNumForCheckpoint(long maxRowNumForCheckpoint) {
        this.maxRowNumForCheckpoint = maxRowNumForCheckpoint;
    }

    @Override
    public JSONObject toSettingJson() {
        JSONObject setting = new JSONObject(true);

        JSONObject speed = new JSONObject(true);
        if (this.getChannel() != null) {
            speed.put("channel", this.getChannel());
            speed.put("readerChannel", readerChannel != null ? readerChannel : channel);
            speed.put("writerChannel", writerChannel != null ? writerChannel : channel);
        }
        if (this.getSpeed() != null) {
            speed.put("bytes", (long) (this.getSpeed() * 1024 * 1024));
        }
        JSONObject restore = new JSONObject();
        restore.put("isRestore", isRestore);
        restore.put("isStream", isStream);

        if (isRestore) {
            restore.put("restoreColumnName", restoreColumnName);
            restore.put("restoreColumnIndex",restoreColumnIndex);
        }


        setting.put("speed", speed);
        setting.put("restore", restore);
        return setting;
    }

    @Override
    public String toSettingJsonString() {
        return toSettingJson().toJSONString();
    }

}
