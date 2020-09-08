package com.dtstack.engine.flink.entity;

import org.apache.flink.api.java.tuple.Tuple2;

import java.util.List;
import java.util.stream.Collectors;

public class LatencyMarkerInfo {
    private String jobVertexName;
    private String jobVertexId;
    private List<String> inputs;
    private List<String> output;
    private List<SubJobVertices> subJobVertices;

    private LatencyMarkerInfo(String jobVertexName, String jobVertexId, List<String> inputs, List<String> output) {
        this.jobVertexName = jobVertexName;
        this.jobVertexId = jobVertexId;
        this.inputs = inputs;
        this.output = output;
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

    public static LatencyMarkerInfo.Builder builder() {
        return new LatencyMarkerInfo.Builder();
    }

    public static class Builder {
        private String jobVertexName;
        private String jobVertexId;
        private List<String> inputs;
        private List<String> output;
        private List<Tuple2> subJobVertices;

        public LatencyMarkerInfo.Builder setJobVertexName(String jobVertexName) {
            this.jobVertexName = jobVertexName;
            return this;
        }

        public LatencyMarkerInfo.Builder setJobVertexId(String jobVertexId) {
            this.jobVertexId = jobVertexId;
            return this;
        }

        public LatencyMarkerInfo.Builder setInputs(List<String> inputs) {
            this.inputs = inputs;
            return this;
        }

        public LatencyMarkerInfo.Builder setOutput(List<String> output) {
            this.output = output;
            return this;
        }

        public LatencyMarkerInfo.Builder setSubJobVertex(List<Tuple2> subJobVertices) {
            this.subJobVertices = subJobVertices;
            return this;
        }

        public LatencyMarkerInfo build() {
            LatencyMarkerInfo latencyMarkerInfo = new LatencyMarkerInfo(jobVertexName, jobVertexId, inputs, output);
            List<SubJobVertices> subJobVerticesList = this.subJobVertices.stream()
                    .map(e -> latencyMarkerInfo.new SubJobVertices(e.f0.toString(), e.f1.toString()))
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
                '}';
    }
}
