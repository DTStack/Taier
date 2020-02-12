package com.dtstack.engine.master.send;

import com.dtstack.engine.common.RootUrls;

/**
 * 
 *
 * Date: 2017年1月2日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public interface Urls extends RootUrls {

    String MASTER_SEND_JOBS = String.format("%s/%s",ACTION,"masterSendJobs");

    String SUBMIT = String.format("%s/%s", ACTION, "submit");

    String WORK_SEND_STOP = String.format("%s/%s", ACTION, "workSendStop");

}
