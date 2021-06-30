package com.dtstack.engine.master.pipeline;

import java.util.Map;

/**
 * @author yuebai
 * @date 2021-05-17
 */
public interface IPipeline {

    void pipeline(Map<String, Object> actionParam, Map<String, Object> pipelineParam) throws Exception;

    IPipeline getNextPipeline();

    void setNextPipeline(IPipeline nextPipeline);

    void execute(Map<String, Object> actionParam, Map<String, Object> pipelineParam) throws Exception;

    abstract class AbstractPipeline implements IPipeline {
        /**
         * 下一个处理的pipeline
         */
        private IPipeline nextPipeline;
        /**
         * pipeline处理的字符串key
         */
        public String pipelineKey;

        public static final String taskShadeKey = "taskShade";
        public static final String scheduleJobKey = "scheduleJob";
        public static final String taskParamsToReplaceKey = "taskParamsToReplace";

        public AbstractPipeline(String pipelineKey) {
            this.pipelineKey = pipelineKey;
        }

        @Override
        public IPipeline getNextPipeline() {
            return nextPipeline;
        }

        public void setNextPipeline(IPipeline nextPipeline) {
            this.nextPipeline = nextPipeline;
        }

        @Override
        public void execute(Map<String, Object> actionParam, Map<String, Object> pipelineParam) throws Exception {
            IPipeline current = this;
            while (null != current) {
                current.pipeline(actionParam, pipelineParam);
                current = current.getNextPipeline();
            }
        }

        public Object getExecuteValue(Map<String, Object> actionParam, Map<String, Object> pipelineParam){
            return pipelineParam.getOrDefault(pipelineKey,actionParam.get(pipelineKey));
        }
    }

}
