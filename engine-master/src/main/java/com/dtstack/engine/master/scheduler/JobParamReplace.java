package com.dtstack.engine.master.scheduler;

import com.dtstack.dtcenter.common.enums.EParamType;
import com.dtstack.dtcenter.common.util.TimeParamOperator;
import com.dtstack.engine.dto.BatchTaskParamShade;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 变量替换
 * command--->${}格式的里面的参数需要计算,其他的直接替换
 * Date: 2017/6/6
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

@Component
public class JobParamReplace {

    private static final Logger logger = LoggerFactory.getLogger(JobParamReplace.class);


    private static final Pattern PARAM_PATTERN = Pattern.compile("\\$\\{(.*?)\\}");

    private final static String VAR_FORMAT = "${%s}";

    public final static List<String> NOTEBOOK_FEATURE_PARAMETERS = new ArrayList<>();

    static {
        NOTEBOOK_FEATURE_PARAMETERS.add("bdp.system.localPath");
        NOTEBOOK_FEATURE_PARAMETERS.add("bdp.system.remotePath");
        NOTEBOOK_FEATURE_PARAMETERS.add("bdp.system.hdfsServer");

//        SYS_PARAMETER.put("bdp.system.bizdate", "yyyyMMdd-1");
//        SYS_PARAMETER.put("bdp.system.cyctime", "yyyyMMddHHmmss");
//        SYS_PARAMETER.put("bdp.system.currmonth", "yyyyMM-0");
//        SYS_PARAMETER.put("bdp.system.premonth", "yyyyMM-1");
    }


    public String paramReplace(String sql, List<BatchTaskParamShade> paramList, String cycTime) {

        if (CollectionUtils.isEmpty(paramList)) {
            return sql;
        }

        Matcher matcher = PARAM_PATTERN.matcher(sql);
        if (!matcher.find()) {
            return sql;
        }

        for (Object param : paramList) {
            Integer type;
            String paramName;
            String paramCommand;
            type = ((BatchTaskParamShade) param).getType();
            paramName = ((BatchTaskParamShade) param).getParamName();
            paramCommand = ((BatchTaskParamShade) param).getParamCommand();

            String targetVal = convertParam(type, paramName, paramCommand, cycTime, ((BatchTaskParamShade) param).getTaskId());
            String replaceStr = String.format(VAR_FORMAT, paramName);
            sql = sql.replace(replaceStr, targetVal);
        }

        return sql;
    }

    public String convertParam(Integer type, String paramName, String paramCommand, String cycTime, Long taskId) {

        String command = paramCommand;
        if (type == EParamType.SYS_TYPE.getType()) {
            if (NOTEBOOK_FEATURE_PARAMETERS.contains(paramName)) {
                if (paramName.equals(NOTEBOOK_FEATURE_PARAMETERS.get(1))) {
                    return paramCommand.concat("/") + taskId + "/";
                } else {
                    return paramCommand;
                }
            } else {
                command = TimeParamOperator.transform(command, cycTime);
                return command;
            }
        } else {
            return TimeParamOperator.dealCustomizeTimeOperator(command, cycTime);
        }
    }
}
