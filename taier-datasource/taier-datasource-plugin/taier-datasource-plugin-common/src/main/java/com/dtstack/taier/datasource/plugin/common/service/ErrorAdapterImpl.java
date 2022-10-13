package com.dtstack.taier.datasource.plugin.common.service;

import com.dtstack.taier.datasource.plugin.common.exception.ConnErrorCode;
import com.dtstack.taier.datasource.plugin.common.exception.IErrorPattern;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据库错误分析实现类
 *
 * @author ：wangchuan
 * date：Created in 上午10:54 2020/11/6
 * company: www.dtstack.com
 */
public class ErrorAdapterImpl implements IErrorAdapter {

    @Override
    public String connAdapter(String errorMsg, IErrorPattern errorPattern) {
        for (ConnErrorCode errorCode : ConnErrorCode.values()) {
            if (StringUtils.isBlank(errorMsg) || Objects.isNull(errorPattern)) {
                break;
            }
            Pattern connErrorPattern = errorPattern.getConnErrorPattern(errorCode.getCode());
            if (Objects.isNull(connErrorPattern)) {
                continue;
            }
            Matcher matcher = connErrorPattern.matcher(errorMsg);
            if (matcher.find()) {
                return errorCode.getDesc();
            }
        }
        // 未匹配到该异常则返回原异常信息
        return String.format("%s: %s", ConnErrorCode.UNDEFINED_ERROR.getDesc(), errorMsg);
    }

    @Override
    public String sqlAdapter(String errorMsg, IErrorPattern errorPattern) {
        // TODO 预留，后期优化
        return "";
    }
}
