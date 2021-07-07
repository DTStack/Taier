package com.dtstack.batch.schedule;

import com.dtstack.batch.domain.BatchSysParameter;
import com.dtstack.batch.domain.BatchTaskParam;
import com.dtstack.batch.domain.BatchTaskParamShade;
import com.dtstack.batch.service.impl.BatchSysParamService;
import com.dtstack.dtcenter.common.enums.EParamType;
import com.dtstack.dtcenter.common.util.TimeParamOperator;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 变量替换
 * command--->${}格式的里面的参数需要计算,其他的直接替换
 * Date: 2017/6/6
 * Company: www.dtstack.com
 * @author xuchao
 */

@Component
public class JobParamReplace {

    @Autowired
    private BatchSysParamService batchSysParamService;

    private static final Pattern PARAM_PATTERN = Pattern.compile("\\$\\{(.*?)\\}");

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    private final static String VAR_FORMAT = "${%s}";

    public String paramReplace(String sql, List paramList, String cycTime){

        if(CollectionUtils.isEmpty(paramList)){
            return sql;
        }

        Matcher matcher = PARAM_PATTERN.matcher(sql);
        if(!matcher.find()){
            return sql;
        }

        for (Object param : paramList) {
            Integer type;
            String paramName;
            String paramCommand;
            if (param instanceof BatchTaskParamShade){
                type = ((BatchTaskParamShade) param).getType();
                paramName = ((BatchTaskParamShade) param).getParamName();
                paramCommand = ((BatchTaskParamShade) param).getParamCommand();
            } else {
                type = ((BatchTaskParam) param).getType();
                paramName = ((BatchTaskParam) param).getParamName();
                paramCommand = ((BatchTaskParam) param).getParamCommand();
            }

            String replaceStr = String.format(VAR_FORMAT, paramName);
            // 判断参数是否存在 SQL 中
            if (!sql.contains(replaceStr)) {
                continue;
            }

            String targetVal = convertParam(type,paramName,paramCommand,cycTime);
            sql = sql.replace(replaceStr, targetVal);
        }

        return sql;
    }

    /**
     * 转化对应字符串中的自定义参数和系统参数
     *
     * @param sql
     * @param paramList
     * @return
     */
    public String paramReplace(String sql, List paramList){
        return paramReplace(sql, paramList, sdf.format(new Date()));
    }

    /**
     * 批量替换参数
     * @param sqlList
     * @param paramList
     * @param cycTime
     * @return
     */
    public List<String> batchParamReplace(List<String> sqlList,List paramList, String cycTime){
        return sqlList.stream().map(t-> paramReplace(t,paramList,cycTime)).collect(Collectors.toList());

    }


    public String convertParam(Integer type,String paramName,String paramCommand,String cycTime) {

        String command = null;
        if (type == EParamType.SYS_TYPE.getType()) {
            BatchSysParameter sysParameter = batchSysParamService.getBatchSysParamByName(paramName);
            command = sysParameter.getParamCommand();

            // 特殊处理 bdp.system.currenttime
            if ("bdp.system.runtime".equals(sysParameter.getParamName())) {
                return TimeParamOperator.dealCustomizeTimeOperator(command, cycTime);
            }
        } else {
            command = paramCommand;
            return TimeParamOperator.dealCustomizeTimeOperator(command, cycTime);
        }

        command = TimeParamOperator.transform(command, cycTime);
        return command;
    }

}
