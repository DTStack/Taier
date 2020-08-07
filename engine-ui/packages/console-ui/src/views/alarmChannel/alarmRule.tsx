import React, { useState } from 'react';
import { Breadcrumb, Card, Form, Radio, Select,
    Input, Checkbox, Upload, Button, Tooltip, Icon,
    message
} from 'antd';
import { cloneDeep } from 'lodash';
import utils from 'dt-common/src/utils';
import Api from '../../api/console'
import { formItemCenterLayout, ALARM_TYPE_TEXT, ALARM_TYPE,
    CHANNEL_MODE_VALUE, CHANNEL_MODE, CHANNEL_CONF_TEXT
} from '../../consts';
import { canTestAlarm, showAlertTemplete } from './help';

const FormItem = Form.Item;
const Option = Select.Option;
const TextArea = Input.TextArea;
const AlarmRule: React.FC = (props: any) => {
    const [fileList, setFileList] = useState<any[]>([])
    const { getFieldDecorator, getFieldValue, validateFields } = props.form;
    const getChannelModeOpts = () => {
        const alertGateType = getFieldValue('alertGateType');
        switch (alertGateType) {
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
    const testAlarm = () => {
        validateFields((err, values) => {
            if (!err) {
                console.log('test', values)
            }
        })
    }
    const goBack = () => {
        props.router.push('/console/alarmChannel')
    }
    const handleSubmit = () => {
        const clusterId = utils.getParameterByName('clusterId');
        validateFields(async (err, values) => {
            console.log('values', values)
            if (!err) {
                let res = await Api.addOrUpdateAlarmRule(Object.assign({}, values, {
                    isDefault: values.isDefault ? 1 : 0,
                    clusterId
                }));
                if (res.code === 1) {
                    message.success('新增成功');
                    goBack();
                }
                console.log('submit', values)
            }
        })
    }
    const fileUploadChange = (info) => {
        let fileList = [...info.fileList];
        fileList = fileList.slice(-1);
        if (info.file.status !== 'uploading') {
            console.log(info.file)
        }
        if (info.file.status === 'done') {
            message.success(`${info.file.name} 上传成功`);
        } else if (info.file.status === 'error') {
            message.error(`${info.file.name} 上传失败`);
        }
        setFileList(fileList)
    }
    const uploadProp = {
        name: 'file',
        accept: '.jar',
        onChange: fileUploadChange
    };
    let testText: string = getFieldValue('alertGateType') === ALARM_TYPE.EMAIL ? '邮箱' : '手机号码';
    const isCreate = utils.getParameterByName('isCreate');
    return (
        <div className='alarm-rule__wrapper'>
            <Breadcrumb>
                <Breadcrumb.Item> <a onClick={() => {
                    props.router.push('/console/alarmChannel')
                }}>告警通道</a></Breadcrumb.Item>
                <Breadcrumb.Item>{`${isCreate ? '新增' : '编辑'}告警通道`}</Breadcrumb.Item>
            </Breadcrumb>
            <Card bordered={false}>
                <Form>
                    <FormItem {...formItemCenterLayout} label='告警类型'>
                        {getFieldDecorator('alertGateType', {
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
                                {getFieldDecorator('file', {
                                    rules: [{
                                        required: false
                                    }]
                                })(
                                    <Upload {...uploadProp} fileList={fileList}>
                                        <a href="javascript:;">选择jar文件</a>
                                    </Upload>
                                )}
                            </FormItem>
                        ) : null
                    }
                    {/* <FormItem {...formItemCenterLayout} label='使用场景'>
                        {getFieldDecorator('alertGateSource', {
                            rules: [{
                                required: true,
                                message: '请输入使用场景'
                            }, {
                                max: 128,
                                message: '使用场景不超过128个字符'
                            }, {
                                pattern: /^[^\s]*$/,
                                message: '不允许填写空格'
                            }]
                        })(
                            <Input />
                        )}
                    </FormItem> */}
                    <FormItem {...formItemCenterLayout} label='通道标识'>
                        {getFieldDecorator('alertGateSource', {
                            rules: [{
                                required: true,
                                message: '请输入通道标识'
                            }, {
                                max: 32,
                                message: '通道标识不超过32个字符'
                            }, {
                                pattern: /^[A-Za-z_]+$/,
                                message: '只支持英文字符、下划线'
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
                            }, {
                                max: 32,
                                message: '通道名称不超过32个字符'
                            }, {
                                pattern: /^[^\s]*$/,
                                message: '不允许填写空格'
                            }]
                        })(
                            <Input placeholder='请输入通道名称，不超过32个字符' />
                        )}
                    </FormItem>
                    <FormItem {...formItemCenterLayout} label={' '} colon={false}>
                        {getFieldDecorator('isDefault', {
                            valuePropName: 'checked',
                            initialValue: false
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
                    {
                        showAlertTemplete(getFieldValue('alertGateType'), getFieldValue('alertGateCode')) ? (
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
                        ) : null
                    }
                    {
                        canTestAlarm(getFieldValue('alertGateType')) ? (
                            <FormItem {...formItemCenterLayout} label=' ' colon={false}>
                                {getFieldDecorator('receiveMethod', {
                                    rules: [{
                                        required: false
                                    }]
                                })(
                                    <Input placeholder={`输入${testText}测试号码，多个${testText}用英文逗号隔开`} addonAfter={<span onClick={() => { testAlarm() }}>点击测试</span>} />
                                )}
                            </FormItem>
                        ) : null
                    }
                </Form>
                <footer>
                    <Button onClick={() => { goBack() }}>取消</Button>
                    <Button type='primary' onClick={() => { handleSubmit() }}>确定</Button>
                </footer>
            </Card>
        </div>
    )
}

export default Form.create({
    onFieldsChange (props, fields) {
        if (fields.hasOwnProperty('alertGateType')) {
        }
    },
    mapPropsToFields (props: any) {
        const ruleData = props.location.state?.ruleData;
        if (!ruleData) return;
        let keyValMap = {};
        let newRuleData = cloneDeep(ruleData);
        for (let [key, value] of Object.entries(newRuleData)) {
            keyValMap = Object.assign({}, keyValMap, {
                [key]: Form.createFormField({ value: value })
            })
        }
        console.log('keyValMap', keyValMap)
        return keyValMap
    }
})(AlarmRule);
