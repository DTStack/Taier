package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.domain.BatchAlarm;
import com.dtstack.batch.vo.AlarmRecordVO;
import com.dtstack.batch.vo.AlarmSearchRecordVO;
import com.dtstack.batch.vo.AlarmSearchVO;
import com.dtstack.batch.vo.AlarmVO;
import com.dtstack.batch.vo.NotifySendTypeVO;
import com.dtstack.batch.web.alarm.vo.query.BatchAlarmSearchRecordVO;
import com.dtstack.batch.web.alarm.vo.query.BatchAlarmSearchVO;
import com.dtstack.batch.web.alarm.vo.query.BatchAlarmVO;
import com.dtstack.batch.web.alarm.vo.result.BatchAlarmListResultVO;
import com.dtstack.batch.web.alarm.vo.result.BatchAlarmRecordResultVO;
import com.dtstack.batch.web.alarm.vo.result.BatchAlarmResultVO;
import com.dtstack.batch.web.alarm.vo.result.BatchAlarmSelectAlarmResultVO;
import com.dtstack.batch.web.alarm.vo.result.BatchAlarmTypeListResultVO;
import com.dtstack.batch.web.pager.PageResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AlarmMapstructTransfer {
    AlarmMapstructTransfer INSTANCE = Mappers.getMapper(AlarmMapstructTransfer.class);

    /**
     * BatchAlarmSearchRecordVO  ->  AlarmSearchRecordVO
     *
     * @param vo
     * @return
     */
    AlarmSearchRecordVO newAlarmSearchRecordVoToAlarmSearchRecordVo(BatchAlarmSearchRecordVO vo);

    /**
     * BatchAlarmVO  ->  com.dtstack.batch.vo.BatchAlarmVO
     *
     * @param vo
     * @return
     */
    com.dtstack.batch.vo.BatchAlarmVO newAlarmVoToAlarmVo(BatchAlarmVO vo);

    /**
     * BatchAlarmSearchVO  ->  AlarmSearchVO
     *
     * @param vo
     * @return
     */
    AlarmSearchVO newAlarmSearchVoToAlarmSearchVo(BatchAlarmSearchVO vo);

    /**
     * PageResult<List<AlarmRecordVO>> ->  PageResult<List<BatchAlarmRecordResultVO>>
     *
     * @param vo
     * @return
     */
    PageResult<List<BatchAlarmRecordResultVO>> newAlarmRecordVoToAlarmRecordResultVo(PageResult<List<AlarmRecordVO>> vo);

    /**
     * List<NotifySendTypeVO>  ->  List<BatchAlarmTypeListResultVO>
     *
     * @param vo
     * @return
     */
    List<BatchAlarmTypeListResultVO> newNotifySendTypeVoToAlarmSendResultVo(List<NotifySendTypeVO> vo);

    /**
     * PageResult<List<AlarmVO>>  ->  PageResult<List<BatchAlarmListResultVO>>
     *
     * @param vo
     * @return
     */
    PageResult<List<BatchAlarmListResultVO>> newAlarmVoToAlarmListResultVo(PageResult<List<AlarmVO>> vo);

    /**
     * AlarmVO  ->  BatchAlarmListResultVO
     *
     * @param vo
     * @return
     */
    BatchAlarmListResultVO newProjectAlarmVoToAlarmResultVo(AlarmVO vo);

    /**
     * BatchAlarm  ->  BatchAlarmResultVO
     *
     * @param vo
     * @return
     */
    BatchAlarmResultVO newAlarmVoToAlarmResultVo(BatchAlarm vo);

    /**
     * AlarmVO -> BatchAlarmSelectAlarmResultVO
     *
     * @param alarmVO
     * @return
     */
    BatchAlarmSelectAlarmResultVO AlarmVOToBatchAlarmSelectAlarmResultVO(AlarmVO alarmVO);

}
