import * as React from 'react';
import { connect } from 'react-redux';
import {
    Form,
    Select,
    DatePicker,
    Checkbox,
    Modal,
    message,
    Input
} from 'antd';
import moment from 'moment';

import { formItemLayout, ALARM_TYPE } from '../../../consts';
import RCApi from '../../../api/ruleConfig';
import utils from 'utils';

const FormItem = Form.Item;
const Option = Select.Option;

const initialSchedule: any = {
    beginDate: moment().format('YYYY-MM-DD'),
    endDate: moment()
        .add(100, 'years')
        .format('YYYY-MM-DD'),
    periodType: '2',
    day: undefined,
    weekDay: undefined,
    hour: 0,
    min: 0,
    beginHour: 0,
    beginMin: 0,
    gapHour: undefined,
    endHour: 0,
    endMin: 0
};

const mapStateToProps = (state: any) => {
    const { common } = state;
    return { common };
};

@(connect(mapStateToProps) as any)
class ExecuteForm extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            scheduleConfObj: initialSchedule,
            params: {
                monitorId: undefined,
                sendTypes: [],
                notifyUser: [],
                periodType: undefined,
                scheduleConf: undefined,
                webhook: undefined
            }
        };
    }

    componentDidMount () {
        this.initState(this.props.data);
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps(nextProps: any) {
        let oldData = this.props.data;

        let newData = nextProps.data;

        if (oldData.monitorId != newData.monitorId) {
            this.initState(newData);
        }
    }

    initState = (data: any) => {
        if (data.scheduleConf) {
            this.setState({
                scheduleConfObj: JSON.parse(data.scheduleConf),
                params: {
                    monitorId: data.monitorId,
                    sendTypes: data.sendTypes,
                    webhook: data.webhook,
                    notifyUser: data.notifyUser.map((item: any) => item.id),
                    periodType: data.periodType,
                    scheduleConf: data.scheduleConf
                }
            });
        }
    };

    // 重置执行信息
    resetScheduleConf = (type: any) => {
        let scheduleConfObj: any = { ...initialSchedule, periodType: type };

        this.setState({
            scheduleConfObj,
            params: {
                ...this.state.params,
                periodType: type,
                scheduleConf: JSON.stringify(scheduleConfObj)
            }
        });
    };

    // 调度周期下拉框
    renderPeriodType = (data: any) => {
        return data.map((item: any) => {
            return (
                <Option key={item.value} value={item.value.toString()}>
                    {item.name}
                </Option>
            );
        });
    };

    // 调度周期回调
    onPeriodTypeChange = (type: any) => {
        this.resetScheduleConf(type);
    };

    // 通知方式列表
    renderSendTypeList = (data: any) => {
        return (
            data &&
            data.map((item: any) => {
                return (
                    <Checkbox key={item.value} value={item.value.toString()}>
                        {item.name}
                    </Checkbox>
                );
            })
        );
    };

    // 通知方式回调
    onSendTypeChange = (value: any) => {
        const { form } = this.props;
        let notifyUser = form.getFieldValue('notifyUser');

        if (value.length === 0 && notifyUser.length === 0) {
            form.setFieldsValue({ notifyUser: [] });
        }

        this.setState({
            params: { ...this.state.params, sendTypes: value }
        });
    };

    onWebHookChange = (e: any) => {
        this.setState({
            params: { ...this.state.params, webhook: e.target.value }
        });
    };

    onBeginDateChange = (date: any, dateString: any) => {
        this.changeScheduleConfTime('beginDate', dateString);
    };

    onEndDateChange = (date: any, dateString: any) => {
        this.changeScheduleConfTime('endDate', dateString);
    };

    // 通知人下拉框
    renderUserList = (data: any) => {
        return data.map((item: any) => {
            return (
                <Option
                    key={item.id}
                    value={item.id.toString()}
                    {...{ name: item.userName, optionFilterProp: 'name' }}
                >
                    {item.userName}
                </Option>
            );
        });
    };

    // 通知人回调
    onNotifyUserChange = (value: any) => {
        const { form } = this.props;
        let sendTypes = form.getFieldValue('sendTypes');

        if (value.length === 0 && sendTypes.length === 0) {
            form.setFieldsValue({ sendTypes: [] });
        }

        this.setState({
            params: { ...this.state.params, notifyUser: value }
        });
    };

    // 调度日期回调
    changeScheduleConfTime = (type: any, value: any) => {
        const { scheduleConfObj, params } = this.state;

        let newParams: any = {};
        newParams[type] = value;
        let newConfObj: any = { ...scheduleConfObj, ...newParams };

        this.setState({
            scheduleConfObj: newConfObj,
            params: {
                ...params,
                scheduleConf: JSON.stringify(newConfObj)
            }
        });
    };

    // 根据调度类型的不同返回不同的调度配置
    renderDynamic () {
        // const { form, common } = this.props;
        // const { allDict } = common;
        const { form } = this.props;
        const { getFieldDecorator } = form;
        const { scheduleConfObj } = this.state;

        // let periodType = allDict.periodType ? allDict.periodType : [];

        // 小时选择框
        const generateHours = (type: any) => {
            let options: any = [];

            for (let i = 0; i <= 23; i++) {
                options.push(
                    <Option key={i} value={`${i}`}>
                        {i < 10 ? `0${i}` : i}
                    </Option>
                );
            }

            return (
                <Select
                    style={{ width: 150 }}
                    onChange={this.changeScheduleConfTime.bind(this, type)}
                >
                    {options}
                </Select>
            );
        };

        // 分钟选择框
        const generateMins = (type: any) => {
            let options: any = [];

            for (let i = 0; i <= 59; i++) {
                options.push(
                    <Option key={i} value={`${i}`}>
                        {i < 10 ? `0${i}` : i}
                    </Option>
                );
            }

            return (
                <Select
                    style={{ width: 150 }}
                    onChange={this.changeScheduleConfTime.bind(this, type)}
                >
                    {options}
                </Select>
            );
        };

        // 间隔时间选择框
        const generateGapHour = () => {
            let options: any = [];

            for (let i = 1; i <= 23; i++) {
                options.push(
                    <Option key={i} value={`${i}`}>
                        {i}小时
                    </Option>
                );
            }

            return (
                <Select
                    style={{ width: 150 }}
                    onChange={this.changeScheduleConfTime.bind(this, 'gapHour')}
                >
                    {options}
                </Select>
            );
        };

        // 月份内天数选择框
        const generateDate = () => {
            let options: any = [];

            for (let i = 1; i <= 31; i++) {
                options.push(
                    <Option key={i} value={`${i}`}>
                        {`每月${i}号`}
                    </Option>
                );
            }

            return (
                <Select
                    mode="multiple"
                    style={{ width: 328 }}
                    onChange={this.changeScheduleConfTime.bind(this, 'day')}
                >
                    {options}
                </Select>
            );
        };

        // 周内天数选择框
        const generateDays = () => {
            return (
                <Select
                    mode="multiple"
                    style={{ width: 328 }}
                    onChange={this.changeScheduleConfTime.bind(this, 'weekDay')}
                >
                    <Option key={1} value="1">
                        星期一
                    </Option>
                    <Option key={2} value="2">
                        星期二
                    </Option>
                    <Option key={3} value="3">
                        星期三
                    </Option>
                    <Option key={4} value="4">
                        星期四
                    </Option>
                    <Option key={5} value="5">
                        星期五
                    </Option>
                    <Option key={6} value="6">
                        星期六
                    </Option>
                    <Option key={7} value="7">
                        星期天
                    </Option>
                </Select>
            );
        };

        switch (scheduleConfObj.periodType) {
            case '1': {
                return (
                    <div>
                        <FormItem {...formItemLayout} label="开始时间">
                            {getFieldDecorator('beginHour', {
                                rules: [
                                    {
                                        required: true,
                                        message: '开始时间不能为空'
                                    },
                                    {
                                        validator: this.checkTime.bind(this)
                                    }
                                ],
                                initialValue: `${scheduleConfObj.beginHour}`
                            })(generateHours('beginHour'))}
                            <span className="m-8">时</span>
                            {getFieldDecorator('beginMin', {
                                rules: [
                                    {
                                        required: true,
                                        message: '开始时间不能为空'
                                    },
                                    {
                                        validator: this.checkTime.bind(this)
                                    }
                                ],
                                initialValue: `${scheduleConfObj.beginMin}`
                            })(generateMins('beginMin'))}
                            <span className="m-8">分</span>
                        </FormItem>

                        <FormItem {...formItemLayout} label="间隔时间">
                            {getFieldDecorator('gapHour', {
                                rules: [
                                    {
                                        required: true,
                                        message: '间隔时间不能为空'
                                    }
                                ],
                                initialValue: scheduleConfObj.gapHour
                                    ? scheduleConfObj.gapHour
                                    : ''
                            })(generateGapHour())}
                        </FormItem>

                        <FormItem {...formItemLayout} label="结束时间">
                            {getFieldDecorator('endHour', {
                                rules: [
                                    {
                                        required: true,
                                        message: '结束时间不能为空'
                                    },
                                    {
                                        validator: this.checkTime.bind(this)
                                    }
                                ],
                                initialValue: `${scheduleConfObj.endHour}`
                            })(generateHours('endHour'))}
                            <span className="m-8">时</span>
                            {getFieldDecorator('endMin', {
                                rules: [
                                    {
                                        required: true,
                                        message: '结束时间不能为空'
                                    },
                                    {
                                        validator: this.checkTime.bind(this)
                                    }
                                ],
                                initialValue: `${scheduleConfObj.endMin}`
                            })(generateMins('endMin'))}
                            <span className="m-8">分</span>
                        </FormItem>
                    </div>
                );
            }

            case '2': {
                return (
                    <FormItem {...formItemLayout} label="起调周期">
                        {getFieldDecorator('hour', {
                            rules: [
                                {
                                    required: true
                                }
                            ],
                            initialValue: `${scheduleConfObj.hour}`
                        })(generateHours('hour'))}
                        <span className="m-8">时</span>
                        {getFieldDecorator('min', {
                            rules: [
                                {
                                    required: true
                                }
                            ],
                            initialValue: `${scheduleConfObj.min}`
                        })(generateMins('min'))}
                        <span className="m-8">分</span>
                    </FormItem>
                );
            }

            case '3': {
                return (
                    <div>
                        <FormItem {...formItemLayout} label="选择时间">
                            {getFieldDecorator('weekDay', {
                                rules: [
                                    {
                                        required: true,
                                        message: '周内天数不能为空'
                                    }
                                ],
                                initialValue: scheduleConfObj.weekDay
                                    ? scheduleConfObj.weekDay
                                    : []
                            })(generateDays())}
                        </FormItem>

                        <FormItem {...formItemLayout} label="起调周期">
                            {getFieldDecorator('hour1', {
                                rules: [
                                    {
                                        required: true
                                    }
                                ],
                                initialValue: `${scheduleConfObj.hour}`
                            })(generateHours('hour'))}
                            <span className="m-8">时</span>
                            {getFieldDecorator('min1', {
                                rules: [
                                    {
                                        required: true
                                    }
                                ],
                                initialValue: `${scheduleConfObj.min}`
                            })(generateMins('min'))}
                            <span className="m-8">分</span>
                        </FormItem>
                    </div>
                );
            }

            case '4': {
                return (
                    <div>
                        <FormItem {...formItemLayout} label="选择时间">
                            {getFieldDecorator('day', {
                                rules: [
                                    {
                                        required: true,
                                        message: '月内天数不能为空'
                                    }
                                ],
                                initialValue: scheduleConfObj.day
                                    ? scheduleConfObj.day
                                    : []
                            })(generateDate())}
                        </FormItem>

                        <FormItem {...formItemLayout} label="起调周期">
                            {getFieldDecorator('hour2', {
                                rules: [
                                    {
                                        required: true
                                    }
                                ],
                                initialValue: `${scheduleConfObj.hour}`
                            })(generateHours('hour'))}
                            <span className="m-8">时</span>
                            {getFieldDecorator('min2', {
                                rules: [
                                    {
                                        required: true
                                    }
                                ],
                                initialValue: `${scheduleConfObj.min}`
                            })(generateMins('min'))}
                            <span className="m-8">分</span>
                        </FormItem>
                    </div>
                );
            }

            case '5':
            default: {
                break;
            }
        }
    }

    // 取消编辑，关闭弹窗
    cancel = () => {
        const { form, data } = this.props;

        form.resetFields();
        this.initState(data);
        this.props.closeModal(false);
    };

    // 检查调度开始时间
    checkTime = (rule: any, value: any, callback: any) => {
        const { form } = this.props;
        let beginHour = form.getFieldValue('beginHour');

        let beginMin = form.getFieldValue('beginMin');

        let endHour = form.getFieldValue('endHour');

        let endMin = form.getFieldValue('endMin');

        let beginTime = parseInt(beginHour) * 60 + parseInt(beginMin);

        let endTime = parseInt(endHour) * 60 + parseInt(endMin);

        if (beginTime >= endTime) {
            let t = `开始时间不能晚于结束时间`
            callback(t);
        } else {
            form.setFieldsValue({
                beginHour,
                beginMin,
                endHour,
                endMin
            });
        }

        callback();
    };

    // 检查生效日期
    checkDate = (rule: any, value: any, callback: any) => {
        const { form } = this.props;
        let beginDate = form.getFieldValue('beginDate');

        let endDate = form.getFieldValue('endDate');

        if (!beginDate || !endDate) {
            callback();
        } else {
            if (beginDate.valueOf() > endDate.valueOf()) {
                let t = `生效日期的开始时间不能晚于结束时间`
                callback(t);
            } else {
                form.setFieldsValue({
                    beginDate,
                    endDate
                });
            }
        }

        callback();
    };

    prev = () => {
        const { currentStep, navToStep } = this.props;
        navToStep(currentStep - 1);
    };

    save = () => {
        const { form } = this.props;
        const { params } = this.state;

        form.validateFields((err: any, values: any) => {
            console.log(err, values);
            if (err && err.endDate) {
                message.error(err.endDate.errors[0].message);
            }

            if (!err) {
                RCApi.updateMonitor(params).then((res: any) => {
                    if (res.code === 1) {
                        message.success('更新成功！');
                        this.props.closeModal(true);
                    }
                });
            }
        });
    };

    render () {
        const { form, common, visible } = this.props;
        const { getFieldDecorator } = form;
        const { allDict, userList } = common;
        const { scheduleConfObj, params } = this.state;
        const { notifyUser, sendTypes, webhook } = params;

        let periodType = allDict.periodType ? allDict.periodType : [];

        let notifyType = allDict.notifyType ? allDict.notifyType : [];

        // 钉钉告警
        const alarmTypes = sendTypes
            ? sendTypes.map((item: any) => item.toString())
            : [];
        const hasDDAlarm = alarmTypes.indexOf(ALARM_TYPE.DINGDING) > -1;

        return (
            <Modal
                title="编辑调度属性"
                wrapClassName="editExecuteModal"
                maskClosable={false}
                visible={visible}
                width={'50%'}
                okText="保存"
                cancelText="取消"
                onOk={this.save}
                onCancel={this.cancel}
            >
                <Form>
                    <FormItem {...formItemLayout} label="调度周期">
                        {getFieldDecorator('periodType', {
                            rules: [
                                {
                                    required: true,
                                    message: '执行周期不能为空'
                                }
                            ],
                            initialValue: `${scheduleConfObj.periodType}`
                        })(
                            <Select onChange={this.onPeriodTypeChange}>
                                {this.renderPeriodType(periodType)}
                            </Select>
                        )}
                    </FormItem>

                    {scheduleConfObj.periodType != 5 && (
                        <FormItem {...formItemLayout} label="生效日期">
                            {getFieldDecorator('beginDate', {
                                rules: [
                                    {
                                        required: true,
                                        message: '生效日期不能为空'
                                    },
                                    {
                                        validator: this.checkDate.bind(this)
                                    }
                                ],
                                initialValue: moment(scheduleConfObj.beginDate)
                            })(
                                <DatePicker
                                    style={{ width: 150 }}
                                    format="YYYY-MM-DD"
                                    placeholder="开始日期"
                                    onChange={this.onBeginDateChange}
                                />
                            )}
                            <span className="m-8">到</span>
                            {getFieldDecorator('endDate', {
                                rules: [
                                    {
                                        required: true,
                                        message: '生效日期不能为空'
                                    },
                                    {
                                        validator: this.checkDate.bind(this)
                                    }
                                ],
                                initialValue: moment(scheduleConfObj.endDate)
                            })(
                                <DatePicker
                                    style={{ width: 150 }}
                                    format="YYYY-MM-DD"
                                    placeholder="结束日期"
                                    onChange={this.onEndDateChange}
                                />
                            )}
                        </FormItem>
                    )}

                    {this.renderDynamic()}

                    <FormItem {...formItemLayout} label="告警方式">
                        {getFieldDecorator('sendTypes', {
                            rules: [
                                {
                                    required: notifyUser.length,
                                    message: '选择告警方式'
                                }
                            ],
                            initialValue: alarmTypes
                        })(
                            <Checkbox.Group onChange={this.onSendTypeChange}>
                                {this.renderSendTypeList(notifyType)}
                            </Checkbox.Group>
                        )}
                    </FormItem>
                    {hasDDAlarm && (
                        <FormItem {...formItemLayout} label="webhook">
                            {getFieldDecorator('webhook', {
                                rules: [
                                    {
                                        required: true,
                                        message: 'webhook不能为空'
                                    }
                                ],
                                initialValue: webhook || ''
                            })(<Input onChange={this.onWebHookChange} />)}
                        </FormItem>
                    )}
                    <FormItem {...formItemLayout} label="告警接收人">
                        {getFieldDecorator('notifyUser', {
                            rules: [
                                {
                                    required: sendTypes.length,
                                    message: '选择告警接收人'
                                }
                            ],
                            initialValue: notifyUser.map((item: any) =>
                                item.toString()
                            )
                        })(
                            <Select
                                allowClear
                                mode="multiple"
                                onChange={this.onNotifyUserChange}
                                filterOption={(inputValue: any, option: any) => {
                                    const val = utils.trim(inputValue);
                                    return option.props.name.toLowerCase().indexOf(val.toLowerCase()) > -1
                                }}
                            >
                                {this.renderUserList(userList)}
                            </Select>
                        )}
                    </FormItem>
                </Form>
            </Modal>
        );
    }
}

export default Form.create<any>()(ExecuteForm);
