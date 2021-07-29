package com.dtstack.engine.master.pipeline.params;

import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.pipeline.IPipeline;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * @author yuebai
 * @date 2021-05-17
 */
public class FileParamPipeline extends IPipeline.AbstractPipeline {

    private static final String uploadFilePath = "uploadPath";
    private static final String fileKey = "file";
    public FileParamPipeline() {
        super(fileKey);
    }

    @Override
    public void pipeline(Map<String, Object> actionParam, Map<String, Object> pipelineParam) throws RdosDefineException {
       String uploadPath = (String) pipelineParam.get(uploadFilePath);
       if(StringUtils.isBlank(uploadPath) && !actionParam.containsKey(fileKey)){
           throw new RdosDefineException("file param pipe line must after uploadPath pipe line");
       }
       pipelineParam.computeIfAbsent(fileKey,k-> uploadPath.substring(StringUtils.lastIndexOf(uploadPath, "/") + 1));
    }
}
