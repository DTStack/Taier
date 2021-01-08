package com.dtstack.channel;

import com.dtstack.lang.data.R;

import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2020/12/7 10:02 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface ICustomizeChannel {

    /**
     * 发送自定义告警
     *
     * @param data 自定义告警数据
     * @param extMap 扩展参数， 由控制台配置
     * @return 发送结果
     */
    public R sendCustomizeAlert(Object data, Map<String, Object> extMap);
}
