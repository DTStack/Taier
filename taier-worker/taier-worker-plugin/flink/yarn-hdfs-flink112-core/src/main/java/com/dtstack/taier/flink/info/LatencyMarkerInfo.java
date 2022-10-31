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

package com.dtstack.taier.flink.info;

import org.apache.flink.api.java.tuple.Tuple2;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * rebuild topographic map for display
 * @author yuebai
 */
public class LatencyMarkerInfo {
    private String jobVertexName;
    private String jobVertexId;
    private List<String> inputs;
    private List<String> output;
    private List<SubJobVertices> subJobVertices;

    private int maxParallelism;
    private int parallelism;

    private LatencyMarkerInfo(String jobVertexName, String jobVertexId, List<String> inputs, List<String> output, int parallelism, int maxParallelism) {
        this.jobVertexName = jobVertexName;
        this.jobVertexId = jobVertexId;
        this.inputs = inputs;
        this.output = output;
        this.parallelism = parallelism;
        this.maxParallelism = maxParallelism;
    }

    class SubJobVertices {
        private String id;
        private String name;

        public SubJobVertices(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "SubJobVertices{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    public String getJobVertexName() {
        return jobVertexName;
    }

    public void setJobVertexName(String jobVertexName) {
        this.jobVertexName = jobVertexName;
    }

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

    public List<SubJobVertices> getSubJobVertices() {
        return subJobVertices;
    }

    public void setSubJobVertices(List<SubJobVertices> subJobVertices) {
        this.subJobVertices = subJobVertices;
    }

    public List<String> getOutput() {
        return output;
    }

    public void setOutput(List<String> output) {
        this.output = output;
    }

    public int getMaxParallelism() {
        return maxParallelism;
    }

    public void setMaxParallelism(int maxParallelism) {
        this.maxParallelism = maxParallelism;
    }

    public int getParallelism() {
        return parallelism;
    }

    public void setParallelism(int parallelism) {
        this.parallelism = parallelism;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String jobVertexName;
        private String jobVertexId;
        private List<String> inputs;
        private List<String> output;
        private List<Tuple2<String, String>> subJobVertices;
        private int maxParallelism;
        private int parallelism;
        private Map<String,String> inputShipStrategyName;

        public Builder setParallelism(int parallelism) {
            this.parallelism = parallelism;
            return this;
        }

        public Builder setMaxParallelism(int maxParallelism) {
            this.maxParallelism = maxParallelism;
            return this;
        }

        public Builder setJobVertexName(String jobVertexName) {
            this.jobVertexName = jobVertexName;
            return this;
        }

        public Builder setJobVertexId(String jobVertexId) {
            this.jobVertexId = jobVertexId;
            return this;
        }

        public Builder setInputs(List<String> inputs) {
            this.inputs = inputs;
            return this;
        }

        public Builder setOutput(List<String> output) {
            this.output = output;
            return this;
        }

        public Builder setSubJobVertex(List<Tuple2<String, String>> subJobVertices) {
            this.subJobVertices = subJobVertices;
            return this;
        }

        public Builder setInputShipStrategyName(Map<String,String> inputShipStrategyName) {
            this.inputShipStrategyName = inputShipStrategyName;
            return this;
        }

        public LatencyMarkerInfo build() {
            LatencyMarkerInfo latencyMarkerInfo = new LatencyMarkerInfo(jobVertexName, jobVertexId, inputs, output,parallelism,maxParallelism);
            List<SubJobVertices> subJobVerticesList = this.subJobVertices.stream()
                    .map(tuple2 -> latencyMarkerInfo.new SubJobVertices(tuple2.f0, tuple2.f1))
                    .collect(Collectors.toList());
            latencyMarkerInfo.setSubJobVertices(subJobVerticesList);
            return latencyMarkerInfo;
        }
    }

    @Override
    public String toString() {
        return "LatencyMarkerInfo{" +
                "jobVertexName='" + jobVertexName + '\'' +
                ", jobVertexId='" + jobVertexId + '\'' +
                ", inputs=" + inputs +
                ", output=" + output +
                ", subJobVertices=" + subJobVertices +
                ", maxParallelism=" + maxParallelism +
                ", parallelism=" + parallelism +
                '}';
    }
}
