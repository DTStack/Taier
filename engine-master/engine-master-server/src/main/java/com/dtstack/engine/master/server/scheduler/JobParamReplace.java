package com.dtstack.engine.master.server.scheduler;

import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.schedule.common.enums.EParamType;
import com.dtstack.schedule.common.util.TimeParamOperator;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(JobParamReplace.class);


    private static final Pattern PARAM_PATTERN = Pattern.compile("\\$\\{(.*?)\\}|\\@@\\{(.*?)\\}");

    private final static String VAR_FORMAT = "${%s}";

    private final static String VAR_COMPONENT = "@@{%s}";


    public String paramReplace(String sql, List<ScheduleTaskParamShade> paramList, String cycTime) {

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
            type = ((ScheduleTaskParamShade) param).getType();
            paramName = ((ScheduleTaskParamShade) param).getParamName();
            paramCommand = ((ScheduleTaskParamShade) param).getParamCommand();

            String targetVal = convertParam(type, paramName, paramCommand, cycTime, ((ScheduleTaskParamShade) param).getTaskId());
            String parseSymbol = convertSymbol(type);
            String replaceStr = String.format(parseSymbol, paramName);
            sql = sql.replace(replaceStr, targetVal);
        }

        return sql;
    }

    private String convertSymbol(Integer type) {
        if (EParamType.COMPONENT.getType().equals(type)) {
            return VAR_COMPONENT;
        } else {
            return VAR_FORMAT;
        }
    }

    public String convertParam(Integer type, String paramName, String paramCommand, String cycTime, Long taskId) {

        String command = paramCommand;
        if (EParamType.SYS_TYPE.getType().equals(type)) {
            // 特殊处理 bdp.system.currenttime
            if ("bdp.system.runtime".equals(paramName)) {
                return TimeParamOperator.dealCustomizeTimeOperator(command, cycTime);
            }

            command = TimeParamOperator.transform(command, cycTime);
            return command;
        } else if (EParamType.COMPONENT.getType().equals(type)) {
            return command;
        } else {
            return TimeParamOperator.dealCustomizeTimeOperator(command, cycTime);
        }
    }
}
