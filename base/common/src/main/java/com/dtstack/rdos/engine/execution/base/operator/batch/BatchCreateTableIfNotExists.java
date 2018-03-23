package com.dtstack.rdos.engine.execution.base.operator.batch;

import java.util.Map;
import java.util.regex.Pattern;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.util.GrokUtil;
import com.dtstack.rdos.engine.execution.base.operator.Operator;


/**
 * 
 * @author sishu.yss
 *
 */
public class BatchCreateTableIfNotExists extends BatchCreateTable{

	protected static String pattern = "BATCHCREATETABLEIFNOTEXISTS";

}
