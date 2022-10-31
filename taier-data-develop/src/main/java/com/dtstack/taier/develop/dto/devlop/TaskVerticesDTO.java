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

package com.dtstack.taier.develop.dto.devlop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskVerticesDTO {

    /**
     * task id
     */
    private String jobVertexId;

    /**
     * 输入节点的task id
     */
    private List<String> inputs;

    /**
     * 输出节点的task id
     */
    private List<String> output;

    /**
     * 该dag名称
     */
    private String jobVertexName;

    /**
     * 包含的多个operator
     */
    private List<SubJobVerticeDTO> subJobVertices;

    /**
     * 并行度
     */
    private Integer parallelism;

    /**
     * 输入记录数
     */
    private Long recordsReceived;

    /**
     * 发送记录数
     */
    private Long recordsSent;

    /**
     * 接收字节数
     */
    private String bytesReceived;

    /**
     * 发送字节数
     */
    private String bytesSent;

    /**
     * 延迟
     */
    private Double delay;

    /**
     * 背压
     */
    private Double backPressure;

    /**
     * 延迟集合
     */
    private Map<String,Double> delayMap = new HashMap<>();

    /**
     * 背压集合
     */
    private HashMap<Integer,Double> backPressureMap = new HashMap<>();

    /**
     * 输入字节数集合
     */
    private Map<Integer,Long> inBytes = new HashMap<>();

    /**
     * 输出字节数集合
     */
    private Map<Integer,Long> outBytes = new HashMap<>();

    private Integer indexLevel;

    public String getJobVertexId() {
        return jobVertexId;
    }

    public void setJobVertexId(String jobVertexId) {
        this.jobVertexId = jobVertexId;
    }

    public List<String> getInputs() {
        return inputs;
    }

    public void setInputs(List<String> inputs) {
        this.inputs = inputs;
    }

    public List<String> getOutput() {
        return output;
    }

    public void setOutput(List<String> output) {
        this.output = output;
    }

    public String getJobVertexName() {
        return jobVertexName;
    }

    public void setJobVertexName(String jobVertexName) {
        this.jobVertexName = jobVertexName;
    }

    public List<SubJobVerticeDTO> getSubJobVertices() {
        return subJobVertices;
    }

    public void setSubJobVertices(List<SubJobVerticeDTO> subJobVertices) {
        this.subJobVertices = subJobVertices;
    }

    public Integer getParallelism() {
        return parallelism;
    }

    public void setParallelism(Integer parallelism) {
        this.parallelism = parallelism;
    }

    public Long getRecordsReceived() {
        return recordsReceived;
    }

    public void setRecordsReceived(Long recordsReceived) {
        this.recordsReceived = recordsReceived;
    }

    public Long getRecordsSent() {
        return recordsSent;
    }

    public void setRecordsSent(Long recordsSent) {
        this.recordsSent = recordsSent;
    }

    public String getBytesReceived() {
        return bytesReceived;
    }

    public void setBytesReceived(String bytesReceived) {
        this.bytesReceived = bytesReceived;
    }

    public String getBytesSent() {
        return bytesSent;
    }

    public void setBytesSent(String bytesSent) {
        this.bytesSent = bytesSent;
    }

    public Double getDelay() {
        return delay;
    }

    public void setDelay(Double delay) {
        this.delay = delay;
    }

    public Double getBackPressure() {
        return backPressure;
    }

    public void setBackPressure(Double backPressure) {
        this.backPressure = backPressure;
    }

    public Map<String, Double> getDelayMap() {
        return delayMap;
    }

    public void setDelayMap(Map<String, Double> delayMap) {
        this.delayMap = delayMap;
    }

    public HashMap<Integer, Double> getBackPressureMap() {
        return backPressureMap;
    }

    public void setBackPressureMap(HashMap<Integer, Double> backPressureMap) {
        this.backPressureMap = backPressureMap;
    }

    public Map<Integer, Long> getInBytes() {
        return inBytes;
    }

    public void setInBytes(Map<Integer, Long> inBytes) {
        this.inBytes = inBytes;
    }

    public Map<Integer, Long> getOutBytes() {
        return outBytes;
    }

    public void setOutBytes(Map<Integer, Long> outBytes) {
        this.outBytes = outBytes;
    }

    public Integer getIndexLevel() {
        return indexLevel;
    }

    public void setIndexLevel(Integer indexLevel) {
        this.indexLevel = indexLevel;
    }

}
