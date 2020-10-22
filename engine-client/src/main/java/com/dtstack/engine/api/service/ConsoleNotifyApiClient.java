package com.dtstack.engine.api.service;

import com.dtstack.engine.api.dto.NotifyRecordReadDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.param.NotifyRecordPageQueryParam;
import com.dtstack.engine.api.param.NotifyRecordParam;
import com.dtstack.engine.api.param.SetAlarmNotifyRecordParam;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Headers;
import com.dtstack.sdk.core.feign.RequestLine;

/**
 * @author yuebai
 * @date 2019-05-17
 */
public interface ConsoleNotifyApiClient extends DtInsightServer {

    /**
     * 查询单条对应的消息记录
     *
     * @param param
     * @return
     */
    @RequestLine("POST /node/notifyRecord/getOne")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<NotifyRecordReadDTO> getOne(NotifyRecordParam param);


    /**
     * 查询消息列表信息
     *
     * @param param
     * @return
     */
    @RequestLine("POST /node/notifyRecord/pageQuery")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<PageResult<NotifyRecordReadDTO>> pageQuery(NotifyRecordPageQueryParam param);

    /**
     * 标记已读
     *
     * @param param
     * @return
     */
    @RequestLine("POST /node/notifyRecord/tabRead")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<Void> tabRead(NotifyRecordParam param);


    /**
     * 全部标记为已读
     *
     * @param param
     * @return
     */
    @RequestLine("POST /node/notifyRecord/allRead")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<Void> allRead(NotifyRecordParam param);

    /**
     * 删除
     *
     * @param param
     * @return
     */
    @RequestLine("POST /node/notifyRecord/delete")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<Void> delete(NotifyRecordParam param);


    /**
     * 生成默认内容
     *
     * @param param
     * @return
     */
    @RequestLine("POST /node/notifyRecord/generateContent")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<Long> generateContent(NotifyRecordParam param);


    /**
     * 发送消息
     *
     * @param param
     * @return
     */
    @RequestLine("POST /node/notifyRecord/sendAlarm")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<Void> setAlarm(SetAlarmNotifyRecordParam param);
}
