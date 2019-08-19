import * as React from 'react';
import { connect } from 'react-redux';
import { hashHistory } from 'react-router';
import moment from 'moment';
import {
    Button,
    Form,
    Select,
    Row,
    Col,
    Input,
    Radio,
    TimePicker,
    DatePicker,
    Checkbox,
    message
} from 'antd';

import { formItemLayout, ALARM_TYPE } from '../../../consts';
import DCApi from '../../../api/dataCheck';

const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const Option = Select.Option;

const initialSchedule: any = {
    beginDate: moment().format('YYYY-MM-DD'),
    hour: 0,
    min: 0,
    endDate: moment()
        .add(100, 'years')
        .format('YYYY-MM-DD'),
    periodType: '2'
};

const mapStateToProps = (state: any) => {
    const { common } = state;
    return { common };
};

@(connect(mapStateToProps) as any)
class StepFour extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            isInform: false,
            scheduleConfObj: initialSchedule
        };
    }

    componentDidMount () {
        const { scheduleConf, notifyVO } = this.props.editParams;
        const { scheduleConfObj } = this.state;

        if (scheduleConf) {
            this.setState({ scheduleConfObj: JSON.parse(scheduleConf) });
        } else {
            this.props.changeParams({
                scheduleConf: JSON.stringify(scheduleConfObj)
            });
        }

        if (notifyVO) {
            this.setState({ isInform: true });
        }
    }

    // 重置schedule
    resetScheduleConf = () => {
        let scheduleConfObj = initialSchedule;

        this.setState({ scheduleConfObj });
        this.props.changeParams({
            scheduleConf: JSON.stringify(scheduleConfObj)
        });
    };

    // 执行方式回调
    onExecuteTypeChange = (e: any) => {
        if (e.target.value === 0) {
            this.resetScheduleConf();
        }

        this.props.changeParams({
            executeType: e.target.value
        });
    };

    // 是否发送通知
    onInformChange = (e: any) => {
        this.setState({ isInform: e.target.checked });

        if (!e.target.checked) {
            this.props.changeParams({ notifyVO: null });
        }
    };

    // 通知方式
    onInformTypeChange = (value: any) => {
        const { notifyVO } = this.props.editParams;

        this.props.changeParams({
            notifyVO: { ...notifyVO, sendTypes: value }
        });
    };

    onWebHookChange = (e: any) => {
        const { notifyVO } = this.props.editParams;

        this.props.changeParams({
            notifyVO: { ...notifyVO, webhook: e.target.value }
        });
    };

    // 执行时间变化
    changeScheduleConfTime = (type: any, date: any, dateString: any) => {
        const { scheduleConfObj } = this.state;

        let newParams: any = {};
        newParams[type] = dateString;

        this.setState({
            scheduleConfObj: { ...scheduleConfObj, ...newParams }
        });
        this.props.changeParams({
            scheduleConf: JSON.stringify({ ...scheduleConfObj, ...newParams })
        });
    };

    // 通知用户变化
    onInformUserChange = (value: any) => {
        const { notifyVO } = this.props.editParams;

        this.props.changeParams({
            notifyVO: { ...notifyVO, receivers: value }
        });
    };

    // 通知人下拉框
    renderUserList = (data: any) => {
        return data.map((item: any) => {
            return (
                <Option key={item.id} value={item.id.toString()}>
                    {item.userName}
                </Option>
            );
        });
    };

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

    // 不能选取当天以前的时间
    disabledDate = (current: any) => {
        return (
            current &&
            current.valueOf() <
                moment()
                    .startOf('day')
                    .valueOf()
        );
    };

    prev = () => {
        const { currentStep, navToStep } = this.props;
        navToStep(currentStep - 1);
    };

    save = () => {
        const { form, editParams } = this.props;

        form.validateFields({ force: true }, (err: any, values: any) => {
            if (err && (err.beginDate || err.hour || err.min)) {
                message.error('执行时间不能为空');
            }

            if (!err) {
                if (editParams.id) {
                    DCApi.updateCheck(editParams).then((res: any) => {
                        if (res.code === 1) {
                            message.success('操作成功');
                            setTimeout(() => {
                                hashHistory.push('/dq/dataCheck');
                            }, 1000);
                        }
                    });
                } else {
                    DCApi.addCheck(editParams).then((res: any) => {
                        if (res.code === 1) {
                            message.success('操作成功');
                            setTimeout(() => {
                                hashHistory.push('/dq/dataCheck');
                            }, 1000);
                        }
                    });
                }
            }
        });
    };

    render () {
        const { form, common, editParams, editStatus } = this.props;
        const { userList, allDict } = common;
        const { getFieldDecorator } = form;
        const { executeType, notifyVO } = editParams;
        const { isInform, scheduleConfObj } = this.state;

        let sendTypes = notifyVO ? notifyVO.sendTypes : undefined;

        let receivers = notifyVO ? notifyVO.receivers : undefined;

        let webhook = notifyVO ? notifyVO.webhook : undefined;

        let notifyType = allDict.notifyType ? allDict.notifyType : [];

        const alarmTypes = sendTypes
            ? sendTypes.map((item: any) => item.toString())
            : [];

        // 钉钉告警
        const hasDDAlarm = alarmTypes.indexOf(ALARM_TYPE.DINGDING) > -1;

        return (
            <div>
                <div className="steps-content">
                    <Form>
                        <FormItem {...formItemLayout} label="执行时间">
                            {getFieldDecorator('executeType', {
                                rules: [
                                    {
                                        required: true,
                                        message: '不能为空'
                                    }
                                ],
                                initialValue: executeType
                            })(
                                <RadioGroup onChange={this.onExecuteTypeChange}>
                                    <Radio value={0}>立即执行</Radio>
                                    <Radio value={1}>定时执行</Radio>
                                </RadioGroup>
                            )}
                        </FormItem>

                        {executeType === 1 && (
                            <Row style={{ marginBottom: 24 }}>
                                <Col span={12} offset={6}>
                                    {getFieldDecorator('beginDate', {
                                        rules: [{ required: true }],
                                        initialValue: moment(
                                            scheduleConfObj.beginDate
                                        )
                                    })(
                                        <DatePicker
                                            format="YYYY-MM-DD"
                                            placeholder="开始时间"
                                            style={{
                                                width: 150,
                                                marginRight: 15
                                            }}
                                            disabledDate={this.disabledDate}
                                            onChange={this.changeScheduleConfTime.bind(
                                                this,
                                                'beginDate'
                                            )}
                                        />
                                    )}
                                    {getFieldDecorator('hour', {
                                        rules: [{ required: true }],
                                        initialValue: moment(
                                            scheduleConfObj.hour,
                                            'HH'
                                        )
                                    })(
                                        <TimePicker
                                            format="HH"
                                            placeholder="小时"
                                            style={{ marginRight: 15 }}
                                            onChange={this.changeScheduleConfTime.bind(
                                                this,
                                                'hour'
                                            )}
                                        />
                                    )}
                                    {getFieldDecorator('min', {
                                        rules: [{ required: true }],
                                        initialValue: moment(
                                            scheduleConfObj.min,
                                            'mm'
                                        )
                                    })(
                                        <TimePicker
                                            format="mm"
                                            placeholder="分钟"
                                            onChange={this.changeScheduleConfTime.bind(
                                                this,
                                                'min'
                                            )}
                                        />
                                    )}
                                </Col>
                            </Row>
                        )}

                        <FormItem {...formItemLayout} label="通知设置">
                            <Checkbox
                                value={isInform}
                                onChange={this.onInformChange}
                            >
                                执行完成后发送通知
                            </Checkbox>
                        </FormItem>

                        {isInform === true && (
                            <div>
                                <FormItem {...formItemLayout} label="通知方式">
                                    {getFieldDecorator('sendTypes', {
                                        rules: [
                                            {
                                                required: true,
                                                message: '选择通知方式'
                                            }
                                        ],
                                        initialValue: sendTypes
                                            ? sendTypes.map((item: any) =>
                                                item.toString()
                                            )
                                            : []
                                    })(
                                        <Checkbox.Group
                                            onChange={this.onInformTypeChange}
                                        >
                                            {this.renderSendTypeList(
                                                notifyType
                                            )}
                                        </Checkbox.Group>
                                    )}
                                </FormItem>
                                {hasDDAlarm && (
                                    <FormItem
                                        {...formItemLayout}
                                        label="webhook"
                                    >
                                        {getFieldDecorator('webhook', {
                                            rules: [
                                                {
                                                    required: true,
                                                    message: 'webhook不能为空'
                                                }
                                            ],
                                            initialValue: webhook || ''
                                        })(
                                            <Input
                                                onChange={this.onWebHookChange}
                                            />
                                        )}
                                    </FormItem>
                                )}

                                <FormItem
                                    {...formItemLayout}
                                    label="通知接收人"
                                >
                                    {getFieldDecorator('receivers', {
                                        rules: [
                                            {
                                                required: true,
                                                message: '选择接收人'
                                            }
                                        ],
                                        initialValue: receivers
                                            ? receivers.map((item: any) =>
                                                item.toString()
                                            )
                                            : []
                                    })(
                                        <Select
                                            allowClear
                                            mode="multiple"
                                            onChange={this.onInformUserChange}
                                        >
                                            {this.renderUserList(userList)}
                                        </Select>
                                    )}
                                </FormItem>
                            </div>
                        )}
                    </Form>
                </div>

                <div className="steps-action">
                    <Button onClick={this.prev}>上一步</Button>
                    <Button
                        type="primary"
                        className="m-l-8"
                        onClick={this.save}
                    >
                        {editStatus === 'edit' ? '保存' : '新建'}
                    </Button>
                </div>
            </div>
        );
    }
}
export default Form.create<any>()(StepFour);
