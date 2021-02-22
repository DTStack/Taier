package com.dtstack.engine.alert.send;


import com.dtstack.engine.alert.domian.Notice;

/**
 * @Auther: dazhi
 * @Date: 2020/10/10 2:38 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface NoticeSenderApi {

    boolean sendNoticeNow(Notice notice);

    void sendNoticeAsync(Notice notice);
}
