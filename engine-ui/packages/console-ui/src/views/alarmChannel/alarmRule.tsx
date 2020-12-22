import React, { useState } from 'react';
import { Breadcrumb, Card, Form, Radio, Select,
    Input, Checkbox, Upload, Button, Tooltip, Icon,
    message
} from 'antd';
import { cloneDeep } from 'lodash';
import utils from 'dt-common/src/utils';
import Api from '../../api/console'
import { formItemCenterLayout, ALARM_TYPE_TEXT, ALARM_TYPE,
    CHANNEL_MODE_VALUE, CHANNEL_MODE, CHANNEL_CONF_TEXT,
    NUM_COMMA, PHONE_REG, EMAIL_COMMA, EMAIL_REG
} from '../../consts';
import { canTestAlarm, showAlertTemplete, textAlertKey,
    showAlertGateJson, showAlertGateCode, showIsDefault,
    showConfigFile } from './help';

const FormItem = Form.Item;
const Option = Select.Option;
const TextArea = Input.TextArea;
const wrapperCol = {
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 9 }
    }
}
const AlarmRule: React.FC = (props: any) => {
    const [fileList, setFileList] = useState<any[]>([])
    const { getFieldDecorator, getFieldValue, validateFields, setFieldsValue } = props.form;
    const id = props.location.state?.id;
    const ruleData = props.location.state?.ruleData;
    const isEmail: boolean = getFieldValue('alertGateType') === ALARM_TYPE.EMAIL;
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
            text = CHANNEL_CONF_TEXT.CUSTOM
        }
        return text;
    }
    const testAlarm = () => {
        validateFields(async (err, values) => {
            if (!err) {
                const testKey = textAlertKey(values.alertGateType);
                let testValue = values.alertGateType !== ALARM_TYPE.CUSTOM ? values[testKey].split(',') : ''
                let res = await Api.testAlert(Object.assign({}, values, {
                    filePath: ruleData?.filePath || '',
                    isDefault: values.isDefault ? 1 : 0,
                    file: values?.file?.file ?? values?.file ?? '',
                    [testKey]: testValue
                }));
                if (res.code === 1) {
                    message.success('消息已发送')
                }
            }
        })
    }
    const goBack = () => {
        props.router.push('/console/alarmChannel')
    }
    const handleSubmit = () => {
        validateFields(async (err, values) => {
            if (!err) {
                let res = await Api.addOrUpdateAlarmRule(Object.assign({}, values, {
                    id: id || '',
                    isDefault: values.isDefault ? 1 : 0,
                    file: values?.file?.file ?? values?.file ?? ''
                }));
                if (res.code === 1) {
                    const msg = id ? '编辑成功' : '新增成功'
                    message.success(msg);
                    goBack();
                }
            }
        })
    }
    const validAlertKey = (rule: any, value: string, callBack: Function) => {
        if (value && !isEmail) {
            if (!NUM_COMMA.test(value)) callBack('请输入正确格式的手机号码')
            const phone = value.split(',')
            phone.forEach((p: string) => {
                if (!PHONE_REG.test(p) && p.length) callBack('请输入正确格式的手机号码')
            })
        } else if (value && isEmail) {
            if (!EMAIL_COMMA.test(value)) callBack('请输入正确格式的邮箱账号')
            const email = value.split(',')
            email.forEach((e: string) => {
                if (!EMAIL_REG.test(e) && e.length) callBack('请输入正确格式的邮箱账号')
            })
        }
        callBack()
    }

    const handleAlertGateType = () => {
        getFieldValue('file') && setFieldsValue({
            [`file`]: ''
        })
        setFileList([])
    }

    const uploadProp = {
        name: 'file',
        accept: '.jar',
        beforeUpload: (file: any) => {
            console.log(file);
            let fileList = [file];
            fileList = fileList.slice(-1);
            setFileList(fileList)
            return false;
        },
        onRemove: () => {
            setFileList([])
            setFieldsValue({ file: undefined })
        },
        fileList
    };
    const uploadConfigProp = {
        name: 'configFile',
        accept: '.jar',
        beforeUpload: (file: any) => {
            setFieldsValue({
                [`file`]: file
            })
            return false;
        },
        fileList: []
    }
    let testText: string = isEmail ? '邮箱' : '手机号码';
    let alertKey: string = textAlertKey(getFieldValue('alertGateType'));
    const isCreate = utils.getParameterByName('isCreate');
    const alertGateType = getFieldValue('alertGateType');
    const alertGateCode = getFieldValue('alertGateCode');

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
                    <FormItem
                        {...{ ...formItemCenterLayout, ...wrapperCol }}
                        label='通道类型'>
                        {getFieldDecorator('alertGateType', {
                            rules: [{
                                required: true,
                                message: '请选择通道类型'
                            }],
                            initialValue: ALARM_TYPE.MSG
                        })(
                            <Radio.Group name='channelMode' onChange={handleAlertGateType} disabled={!isCreate}>
                                {
                                    Object.entries(ALARM_TYPE_TEXT).map(([key, value]) => {
                                        return <Radio key={key} value={Number(key)}>{value}</Radio>
                                    })
                                }
                            </Radio.Group>
                        )}
                    </FormItem>
                    {showAlertGateCode(alertGateType) && <FormItem {...formItemCenterLayout} label='通道模式'>
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
                    </FormItem>}
                    {
                        alertGateCode?.includes('jar') && !showConfigFile(alertGateType) ? (
                            <FormItem {...formItemCenterLayout} label='上传文件'>
                                {getFieldDecorator('file', {
                                    rules: [{
                                        required: false
                                    }]
                                })(
                                    <Upload {...uploadProp}>
                                        <a href="javascript:;">选择jar文件</a>
                                    </Upload>
                                )}
                                {
                                    ruleData?.filePath && !getFieldValue('file') && <span>{ruleData?.filePath}</span>
                                }
                            </FormItem>
                        ) : null
                    }
                    <FormItem {...formItemCenterLayout} label='通道标识'>
                        {getFieldDecorator('alertGateSource', {
                            rules: [{
                                required: true,
                                message: '请输入通道标识'
                            }, {
                                max: 32,
                                message: '通道标识不超过32个字符'
                            }, {
                                pattern: /^[A-Za-z0-9_]+$/,
                                message: '只支持英文、数字、下划线'
                            }]
                        })(
                            <Input disabled={!!id} />
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
                    {showIsDefault(alertGateType) && <FormItem {...formItemCenterLayout} label={' '} colon={false}>
                        {getFieldDecorator('isDefault', {
                            valuePropName: 'checked',
                            initialValue: false
                        })(
                            <Checkbox>设置为默认通道</Checkbox>
                        )}
                        <Tooltip title='各应用的告警走默认通道，故默认通道需谨慎设置，支持用户后续更改，每个通道类有且仅有一个默认通道。' arrowPointAtCenter>
                            <Icon type="info-circle" />
                        </Tooltip>
                    </FormItem>}
                    {showConfigFile(alertGateType) && <FormItem {...{ ...formItemCenterLayout, ...wrapperCol }} label='配置文件'>
                        {getFieldDecorator('file', {
                            rules: [{
                                required: true, message: '文件不可为空！'
                            }],
                            initialValue: ruleData?.filePath
                        })(<div />)}
                        <div className="c-alarmRule__config">
                            <Upload {...uploadConfigProp}>
                                <Button style={{ width: 164 }} icon="upload">点击上传</Button>
                            </Upload>
                            <span className="config-desc">
                                仅支持jar格式，
                                <a href={`/api/console/service/alert/downloadJar?alertGateType=${alertGateType}`}>
                                查看配置文件说明
                                </a>
                            </span>
                        </div>
                        {getFieldValue('file') && <span className="config-file">
                            <Icon type="paper-clip" />
                            {getFieldValue('file')?.name ?? getFieldValue('file') ?? '' }
                            <Icon type="delete" onClick={() => {
                                setFieldsValue({
                                    [`file`]: ''
                                })
                            }} />
                        </span>}
                    </FormItem>}
                    {
                        showAlertGateJson(alertGateCode, alertGateType) ? <FormItem {...formItemCenterLayout} label='通道配置信息'>
                            {getFieldDecorator('alertGateJson', {
                                rules: [{
                                    required: true,
                                    message: '请输入通道配置信息'
                                }]
                            })(
                                <TextArea placeholder={getChannelConfText()} rows={6} />
                            )}
                        </FormItem> : null
                    }
                    {
                        showAlertTemplete(alertGateType, alertGateCode) ? (
                            <FormItem {...formItemCenterLayout} label='通知消息模版'>
                                {getFieldDecorator('alertTemplate', {
                                    rules: [{
                                        required: true,
                                        message: '请输入通知消息模版'
                                    }]
                                })(
                                    <TextArea
                                        placeholder={`请按照此格式填写：<企业名称>$` + `{message}，请及时处理`}
                                        rows={4}
                                    />
                                )}
                            </FormItem>
                        ) : null
                    }
                    {
                        canTestAlarm(alertGateType) ? (
                            <FormItem {...formItemCenterLayout} label=' ' colon={false}>
                                {getFieldDecorator(`${alertKey}`, {
                                    rules: [{
                                        required: false
                                    }, {
                                        validator: validAlertKey
                                    }],
                                    initialValue: ''
                                })(
                                    alertGateType !== ALARM_TYPE.CUSTOM ? <Input
                                        placeholder={`输入${testText}测试号码，多个${testText}用英文逗号隔开`}
                                        addonAfter={<span onClick={() => { testAlarm() }}>点击测试</span>}
                                    /> : <Button ghost onClick={() => { testAlarm() }}>消息发送测试</Button>
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
    onValuesChange (props: any, fields: any) {
        if (fields.hasOwnProperty('alertGateType')) {
            props.form.setFieldsValue({
                alertGateCode: undefined,
                alertGateJson: undefined
            })
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
