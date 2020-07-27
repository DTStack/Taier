import React from 'react';
import { Breadcrumb, Card, Form, Radio, Select,
    Input, Checkbox, Upload, Button, Tooltip, Icon
} from 'antd';

import { formItemCenterLayout, ALARM_TYPE_TEXT, ALARM_TYPE,
    CHANNEL_MODE_VALUE, CHANNEL_MODE, CHANNEL_CONF_TEXT
} from '../../consts';
const FormItem = Form.Item;
const Option = Select.Option;
const TextArea = Input.TextArea;
const AlarmRule: React.FC = (props: any) => {
    const { getFieldDecorator, getFieldValue } = props.form;
    const getChannelModeOpts = () => {
        const alarmType = getFieldValue('alarmType')
        switch (alarmType) {
            case ALARM_TYPE.MSG: {
                return CHANNEL_MODE.sms.map((item, index) => {
                    return <Option value={item.value} key={`${index}`}>{item.title}</Option>
                })
            }
            case ALARM_TYPE.EMAIL: {
                return CHANNEL_MODE.mail.map((item, index) => {
                    return <Option value={item.value} key={`${index}`}>{item.title}</Option>
                })
            }
            case ALARM_TYPE.DING: {
                return CHANNEL_MODE.dingTalk.map((item, index) => {
                    return <Option value={item.value} key={`${index}`}>{item.title}</Option>
                })
            }
            default: return []
        }
    }
    const getChannelConfText = (): string => {
        const alertGateCode = getFieldValue('alertGateCode') || '';
        let text = '';
        if (alertGateCode === CHANNEL_MODE_VALUE.SMS_YP) {
            text = CHANNEL_CONF_TEXT.SMS_YP
        } else if (alertGateCode === CHANNEL_MODE_VALUE.MAIL_DT) {
            text = CHANNEL_CONF_TEXT.MAIL_DT
        } else if (alertGateCode.includes('jar')) {
            text = CHANNEL_CONF_TEXT.JAR
        } else if (alertGateCode.includes('api')) {
            text = CHANNEL_CONF_TEXT.API
        } else {
            text = ''
        }
        return text;
    }
    const uploadProp = {
        name: 'file',
        action: '/gate/alert/jarUpload'
        // onChange: this.fileUploadChange
    };
    return (
        <div className='alarm-rule__wrapper'>
            <Breadcrumb>
                <Breadcrumb.Item> <a onClick={() => {
                    props.router.push('/console/alarmChannel')
                }}>新增告警通道</a></Breadcrumb.Item>
                <Breadcrumb.Item>{'新增告警通道'}</Breadcrumb.Item>
            </Breadcrumb>
            <Card bordered={false}>
                <Form>
                    <FormItem {...formItemCenterLayout} label='告警类型'>
                        {getFieldDecorator('alarmType', {
                            rules: [{
                                required: true,
                                message: '请选择告警类型'
                            }],
                            initialValue: ALARM_TYPE.MSG
                        })(
                            <Radio.Group name='channelMode'>
                                {
                                    Object.entries(ALARM_TYPE_TEXT).map(([key, value]) => {
                                        return <Radio key={key} value={Number(key)}>{value}</Radio>
                                    })
                                }
                            </Radio.Group>
                        )}
                    </FormItem>
                    <FormItem {...formItemCenterLayout} label='通道模式'>
                        {getFieldDecorator('alertGateCode', {
                            rules: [{
                                required: true,
                                message: '请选择通道模式'
                            }]
                        })(
                            <Select>
                                {getChannelModeOpts()}
                            </Select>
                        )}
                    </FormItem>
                    {
                        getFieldValue('alertGateCode')?.includes('jar') ? (
                            <FormItem {...formItemCenterLayout} label='上传文件'>
                                {getFieldDecorator('alertGateCode', {
                                    rules: [{
                                        required: false
                                    }]
                                })(
                                    <Upload {...uploadProp}>
                                        <a href="javascript:;">选择jar文件</a>
                                    </Upload>
                                )}
                            </FormItem>
                        ) : null
                    }
                    <FormItem {...formItemCenterLayout} label='使用场景'>
                        {getFieldDecorator('alertGateSource', {
                            rules: [{
                                required: true,
                                message: '请输入使用场景'
                            }]
                        })(
                            <Input />
                        )}
                    </FormItem>
                    <FormItem {...formItemCenterLayout} label='通道名称'>
                        {getFieldDecorator('alertGateName', {
                            rules: [{
                                required: true,
                                message: '请输入通道名称'
                            }]
                        })(
                            <Input placeholder='请输入通道名称，不超过32个字符' />
                        )}
                    </FormItem>
                    <FormItem {...formItemCenterLayout} label={' '} colon={false}>
                        {getFieldDecorator('defaultChannel', {
                        })(
                            <Checkbox>设置为默认通道</Checkbox>
                        )}
                        <Tooltip title='各应用的告警走默认通道，故默认通道需谨慎设置，支持用户后续更改，每个通道类有且仅有一个默认通道。' arrowPointAtCenter>
                            <Icon type="info-circle" />
                        </Tooltip>
                    </FormItem>
                    <FormItem {...formItemCenterLayout} label='通道配置信息'>
                        {getFieldDecorator('alertGateJson', {
                            rules: [{
                                required: true,
                                message: '请输入通道配置信息'
                            }]
                        })(
                            <TextArea placeholder={getChannelConfText()} rows={6} />
                        )}
                    </FormItem>
                    <FormItem {...formItemCenterLayout} label='通知消息模版'>
                        {getFieldDecorator('alertTemplate', {
                            rules: [{
                                required: true,
                                message: '请输入通知消息模版'
                            }]
                        })(
                            <TextArea
                                placeholder={`请按照此格式填写："【企业名称】$` + `{message}，请及时处理`}
                                rows={4}
                            />
                        )}
                    </FormItem>
                    <FormItem {...formItemCenterLayout} label=' ' colon={false}>
                        {getFieldDecorator('phoneNum', {
                            rules: [{
                                required: false
                            }]
                        })(
                            <Input placeholder='输入手机测试号码，多个号码用英文逗号隔开' addonAfter={<span>点击测试</span>} />
                        )}
                    </FormItem>
                </Form>
                <footer>
                    <Button>取消</Button>
                    <Button type='primary'>确定</Button>
                </footer>
            </Card>
        </div>
    )
}

export default Form.create({
    onFieldsChange (props, fields) {
        if (fields.hasOwnProperty('alarmType')) {
        }
    },
    mapPropsToFields (props) {

    }
})(AlarmRule);
