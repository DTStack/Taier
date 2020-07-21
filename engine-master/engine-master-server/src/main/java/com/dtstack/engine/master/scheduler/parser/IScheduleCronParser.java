package com.dtstack.engine.master.scheduler.parser;

import java.util.Map;

/**
 * Reason:
 * Date: 2017/5/4
 * Company: www.dtstack.com
 * @ahthor xuchao
 */
public interface IScheduleCronParser {

    String parse(Map<String, Object> param);
}
