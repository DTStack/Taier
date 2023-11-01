/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { forwardRef,useEffect, useImperativeHandle, useState } from 'react';
import {
    CloseOutlined,
    CopyOutlined,
    LinkOutlined,
    PlusCircleOutlined,
    QuestionCircleOutlined,
    UploadOutlined,
} from '@ant-design/icons';
import { Button, Form, Input, InputNumber, message, Radio, Select, Space, Spin,Switch, Tooltip, Upload } from 'antd';
import type { RcFile } from 'antd/lib/upload';
import Base64 from 'base-64';
import copy from 'copy-to-clipboard';
import moment from 'moment';

import api from '@/api';
import { formItemLayout,HDFSCONG } from '@/constant';
import { utf8to16 } from '@/utils';
import { getRules } from './formRules';
import HDFSTooltips from './tooltips';
import './index.scss';

const { TextArea } = Input;
const { Option } = Select;

interface IProps {
    record: {
        dataType: string;
        /**
         * Will be existed in edit operation
         */
        dataInfoId?: number;
    };
    version?: string;
    onValuesChange?: (values: Record<string, any>) => void;
}

enum FORM_FIELD_VISIBLE_ENUM {
    visible = 0,
    invisible = 1,
}

enum FORM_FIELD_REQUIRED_ENUM {
    unrequired = 0,
    required = 1,
}

/**
 * The Radio Component default values
 */
enum RADIO_VALUE {
    default = 1,
    custom,
}

/**
 * The protocol types
 */
enum PROTOCOL_VALUE {
    FTP = 'FTP',
    SFTP = 'SFTP',
}

/**
 * The protocol connection mode values
 * Only works in FTP
 */
enum PROTOCOL_MODE_VALUE {
    PORT = 'PORT',
    PASSIVE = 'PASV',
}

/**
 * The protocol auth mode values
 * Only works in SFTP
 */
enum PROTOCOL_AUTH_MODE_VALUE {
    PASSWORD = 1,
    SSH = 2,
}

/**
 * The config mode for HDFS
 */
enum HDFS_CONFIG_VALUE {
    DEFAULT = 'default',
    CUSTOM = 'custom',
}

/**
 * The connection type values for Kafka
 */
enum KAFKA_CONNECTION_TYPE {
    CLUSTER = 1,
    BROKER = 2,
}

/**
 * The authentication for Karka
 */
enum KAFKA_AUTH_VALUES {
    SASL_PLAINTEXT = 'SASL_PLAINTEXT',
    KERBEROS = 'kerberos',
    NULL = '无',
}

const KAFKA_OPTION = [
    {
        key: 'SASL_PLAINTEXT',
        name: KAFKA_AUTH_VALUES.SASL_PLAINTEXT,
    },
    {
        key: 'kerberos',
        name: KAFKA_AUTH_VALUES.KERBEROS,
    },
    {
        key: '无',
        name: KAFKA_AUTH_VALUES.NULL,
    },
];

/**
 * The Redis type
 */
enum REDIS_TYPE_VALUE {
    STANDALONE = 1,
    SENTINEL = 2,
    CLUSTER = 3,
}

export interface IFormFieldVoList {
    defaultValue: any;
    invisible: FORM_FIELD_VISIBLE_ENUM;
    label: string;
    name: string;
    /**
     * Only used it when widget is select
     */
    options: { key: string; value: string; label: string }[] | null;
    placeHold: string | null;
    tooltip: string | null;
    /**
     * Used for validation
     */
    regex: string | null;
    required: FORM_FIELD_REQUIRED_ENUM;
    validInfo: string | null;
    widget: string;

    // Set these properties by Front-end
    disabled?: boolean | null;
    initialValue?: any;
}

export default forwardRef(({ record, version = '', onValuesChange }: IProps, ref) => {
    const [form] = Form.useForm();
    const [templateData, setTemplateData] = useState<IFormFieldVoList[]>([]);
    const [principalsList, setPrincipalsList] = useState<any[]>([]);
    const [detailData, setDetailData] = useState<Record<string, any>>({});
    const [loading, setLoading] = useState(false);

    useImperativeHandle(ref, () => form);

    const templateForm = async (): Promise<{
        fromFieldVoList: IFormFieldVoList[];
        dataType: string;
        dataVersion: string;
    }> => {
        const { data } = await api.findTemplateByTypeVersion({
            dataType: record?.dataType,
            dataVersion: version,
        });
        return data || {};
    };

    const getDetail = async () => {
        const { data } = await api.detail({
            dataInfoId: record?.dataInfoId,
        });
        return data || {};
    };

    const getAllData = async () => {
        setLoading(true);
        const { fromFieldVoList = [] } = await templateForm();

        const formFieldVoList = fromFieldVoList.filter((item) => item.invisible !== FORM_FIELD_VISIBLE_ENUM.invisible);

        // Distinguish in the edit or add way
        if (record?.dataInfoId) {
            const nextDetailData = await getDetail();
            if (nextDetailData) {
                fromFieldVoList.forEach((item) => {
                    if (item.label === '数据源名称') {
                        item.disabled = true;
                    }
                    try {
                        item.initialValue =
                            nextDetailData[item.name] || JSON.parse(Base64.decode(nextDetailData.dataJson))[item.name];
                    } catch {}

                    let data: any = {};
                    try {
                        data = JSON.parse(utf8to16(Base64.decode(nextDetailData.dataJson)));
                    } catch {}
                    setDetailData(data);

                    // webSocket定制化
                    // setWebSocketParams(data?.webSocketParams || {});
                });
            }
        }

        setLoading(false);
        setTemplateData(formFieldVoList || []);
    };

    const handleCopy = (item: Partial<IFormFieldVoList>) => {
        if (item.placeHold && copy(item.placeHold)) {
            message.success('复制成功');
        } else {
            message.error('复制失败，请手动复制');
        }
    };

    const getPrincipalsWithConf = async (
        kerberosFile: RcFile & { modifyTime?: moment.Moment },
        callBack?: (res: { success: boolean; code: number; data: any[] }) => void
    ) => {
        kerberosFile.modifyTime = moment();
        const res = await api.uploadCode({
            file: kerberosFile,
            dataType: record.dataType,
            dataVersion: version,
        });
        if (res.success) {
            message.success('上传成功');
        } else {
            message.error('上传失败!');
        }
        if (callBack) {
            callBack(res);
        }
    };

    const handleBeforeUpload = (file: RcFile) => {
        getPrincipalsWithConf(file, (res) => {
            if (res.code !== 1) {
                form.setFieldsValue({
                    kerberosFile: '',
                });
                return;
            }
            // 上传文件前清空 masterKer、regionserverKer
            form.setFieldsValue({ principal: res.data[0] });
            setPrincipalsList(res.data || []);
        });
        return false;
    };

    const handleAddws = (callback: () => void) => {
        const params: (Record<string, string | undefined> | undefined)[] | undefined =
            form.getFieldValue('webSocketParams');
        if (params) {
            if (params.some((p) => !p)) {
                message.warning('请先完整填写参数!');
                return;
            }
            if (params.some((param) => param && !param.value)) {
                message.warning('请先完整填写参数!');
                return;
            }
            if (params.length === 20) {
                message.warning('最多可添加20行鉴权参数!');
                return;
            }
        }
        callback();
    };

    useEffect(() => {
        if (record) {
            getAllData();
        }
    }, []);

    const principalsOptions = principalsList.map((item) => (
        <Option key={item} value={item}>
            {item}
        </Option>
    ));

    const uploadForm = () => {
        return (
            <Form.Item noStyle>
                <Form.Item
                    key="kerberosFile"
                    className="file-row"
                    wrapperCol={{
                        offset: formItemLayout.labelCol.sm.span,
                        span: formItemLayout.wrapperCol.sm.span,
                    }}
                    name="kerberosFile"
                    rules={[
                        {
                            required: true,
                            message: '文件不可为空！',
                        },
                    ]}
                    valuePropName="file"
                    getValueFromEvent={(e) => e.file}
                    initialValue={detailData?.kerberosFile || ''}
                >
                    <Upload accept=".zip" name="file" showUploadList={false} beforeUpload={handleBeforeUpload}>
                        <Button style={{ color: '#999' }}>
                            <UploadOutlined /> 上传文件
                        </Button>
                        <Tooltip title="仅支持Zip格式，压缩包需包含xxx.keytab、krb5.config文件。上传文件前，请在控制台开启SFTP服务。">
                            <QuestionCircleOutlined
                                style={{
                                    fontSize: '14px',
                                    marginTop: '11px',
                                    marginLeft: '10px',
                                    color: '#999',
                                }}
                            />
                        </Tooltip>
                        <p
                            style={{
                                marginTop: 8,
                                color: '#666',
                                fontSize: 12,
                            }}
                        >
                            上传单个文件，支持扩展格式：.zip
                        </p>
                    </Upload>
                </Form.Item>
                <Form.Item
                    wrapperCol={{
                        offset: formItemLayout.labelCol.sm.span,
                        span: formItemLayout.wrapperCol.sm.span,
                    }}
                    style={{ marginBottom: 0 }}
                    shouldUpdate={(pre, cur) => pre.kerberosFile !== cur.kerberosFile}
                >
                    {({ getFieldValue, setFieldsValue }) =>
                        !!getFieldValue('kerberosFile') && (
                            <div
                                style={{
                                    display: 'flex',
                                    alignItems: 'center',
                                }}
                            >
                                <LinkOutlined />
                                <Input
                                    style={{
                                        border: 0,
                                        paddingRight: 20,
                                        background: 'transparent',
                                        paddingLeft: 20,
                                        flex: 1,
                                    }}
                                    autoComplete="off"
                                    value={`${utf8to16(getFieldValue('kerberosFile').name)}   ${moment(
                                        getFieldValue(`kerberosFile`).modifyTime
                                    ).format('YYYY-MM-DD HH:mm:ss')}`}
                                />
                                <CloseOutlined
                                    style={{
                                        cursor: 'pointer',
                                        color: '#999',
                                    }}
                                    onClick={() => {
                                        setFieldsValue({
                                            kerberosFile: '',
                                        });
                                    }}
                                />
                            </div>
                        )
                    }
                </Form.Item>
            </Form.Item>
        );
    };

    // 渲染表单方法
    const formItem = templateData.map((item, index) => {
        switch (item.widget) {
            case 'Input':
                return (
                    <Form.Item
                        tooltip={item.tooltip}
                        key={index}
                        label={item.label}
                        required={!!item.required}
                        name={item.name}
                        {...getRules(item)}
                    >
                        <Input
                            autoComplete="off"
                            className="w-full"
                            placeholder={item.placeHold || `请输入${item.label}`}
                            disabled={!!item.disabled}
                        />
                    </Form.Item>
                );
            case 'InputWithCopy':
                return (
                    <Form.Item label={item.label} required={!!item.required} key={index}>
                        <Form.Item noStyle name={item.name} {...getRules(item)}>
                            <Input
                                autoComplete="off"
                                placeholder={item.placeHold || `请输入${item.label}`}
                                disabled={!!item.disabled}
                            />
                        </Form.Item>
                        <CopyOutlined onClick={() => handleCopy(item)} />
                    </Form.Item>
                );
            case 'Select':
                return (
                    <Form.Item
                        label={item.label}
                        key={index}
                        name={item.name}
                        required={!!item.required}
                        {...getRules(item)}
                    >
                        <Select placeholder={item.placeHold || `请输入${item.label}`}>
                            {(item.options || []).map((option) => (
                                <Option key={option.key} value={option.value}>
                                    {option.label}
                                </Option>
                            ))}
                        </Select>
                    </Form.Item>
                );
            case 'TextArea':
                return (
                    <Form.Item
                        label={item.label}
                        key={index}
                        name={item.name}
                        required={!!item.required}
                        {...getRules(item)}
                    >
                        <TextArea rows={4} placeholder={item.placeHold || `请输入${item.label}`} />
                    </Form.Item>
                );
            case 'TextAreaWithCopy':
                return (
                    <Form.Item label={item.label} tooltip={<HDFSTooltips />} key={index} required={!!item.required}>
                        <Form.Item noStyle name={item.name} {...getRules(item)}>
                            <TextArea id="copy" rows={4} placeholder={item.placeHold || ''} />
                        </Form.Item>
                        <div className="help-module">
                            <span onClick={() => handleCopy(item)}>点击复制模板</span>
                        </div>
                    </Form.Item>
                );
            case 'RichText':
                return (
                    <Form.Item
                        label={item.label}
                        key={index}
                        name={item.name}
                        required={!!item.required}
                        {...getRules(item)}
                    >
                        <p>{item.defaultValue}</p>
                    </Form.Item>
                );
            case 'Password':
                return (
                    <Form.Item
                        label={item.label}
                        key={index}
                        required={!!item.required}
                        name={item.name}
                        {...getRules(item)}
                    >
                        <Input.Password
                            visibilityToggle={false}
                            placeholder={item.placeHold || `请输入${item.label}`}
                        />
                    </Form.Item>
                );
            case 'Radio':
                return (
                    <Form.Item
                        label={item.label}
                        key={index}
                        name={item.name}
                        required={!!item.required}
                        {...getRules(item)}
                    >
                        <Radio.Group>
                            <Radio value={RADIO_VALUE.default}>默认</Radio>
                            <Radio value={RADIO_VALUE.custom}>自定义</Radio>
                        </Radio.Group>
                    </Form.Item>
                );
            case 'Integer':
                return (
                    <Form.Item
                        label={item.label}
                        key={index}
                        name={item.name}
                        required={!!item.required}
                        {...getRules(item)}
                    >
                        <InputNumber autoComplete="off" className="w-full" />
                    </Form.Item>
                );
            case 'Switch':
                return (
                    <Form.Item
                        label={item.label}
                        key={index}
                        name={item.name}
                        required={!!item.required}
                        valuePropName="checked"
                        {...getRules(item)}
                    >
                        <Switch />
                    </Form.Item>
                );
            // 定制化内容
            case 'Kerberos':
                return (
                    <div key={index}>
                        <Form.Item
                            label={item.label}
                            key={index}
                            className="file-row"
                            required={!!item.required}
                            name={item.name}
                            initialValue={detailData?.openKerberos || false}
                            valuePropName="checked"
                        >
                            <Switch />
                        </Form.Item>

                        <Form.Item
                            noStyle
                            shouldUpdate={(prevValues, curValues) => prevValues.openKerberos !== curValues.openKerberos}
                        >
                            {({ getFieldValue }) => getFieldValue('openKerberos') && uploadForm()}
                        </Form.Item>

                        <Form.Item
                            noStyle
                            shouldUpdate={(prevValues, curValues) =>
                                prevValues.kerberosFile !== curValues.kerberosFile ||
                                prevValues.openKerberos !== curValues.openKerberos
                            }
                        >
                            {({ getFieldValue }) =>
                                getFieldValue('kerberosFile') &&
                                getFieldValue('openKerberos') && (
                                    <Form.Item
                                        label="Kerberos Principal"
                                        name="principal"
                                        required
                                        rules={[
                                            {
                                                required: true,
                                                message: 'Kerberos Principal不可为空',
                                            },
                                        ]}
                                        initialValue={detailData?.principal || principalsList[0] || ''}
                                    >
                                        <Select>{principalsOptions}</Select>
                                    </Form.Item>
                                )
                            }
                        </Form.Item>
                    </div>
                );
            case 'HbaseKerberos':
                return (
                    <div key={index}>
                        <Form.Item
                            label="开启Kerberos认证"
                            className="file-row"
                            name="openKerberos"
                            valuePropName="checked"
                            required={!!item.required}
                            initialValue={detailData?.openKerberos || false}
                        >
                            <Switch />
                        </Form.Item>
                        <Form.Item
                            noStyle
                            shouldUpdate={(prevValues, curValues) => prevValues.openKerberos !== curValues.openKerberos}
                        >
                            {({ getFieldValue }) => getFieldValue('openKerberos') && uploadForm()}
                        </Form.Item>

                        <Form.Item
                            noStyle
                            shouldUpdate={(prevValues, curValues) =>
                                prevValues.kerberosFile !== curValues.kerberosFile ||
                                prevValues.openKerberos !== curValues.openKerberos
                            }
                        >
                            {({ getFieldValue }) =>
                                getFieldValue('kerberosFile') &&
                                getFieldValue('openKerberos') && (
                                    <>
                                        <Form.Item
                                            label="client.principal"
                                            name="principal"
                                            required
                                            initialValue={detailData?.principal || principalsList[0] || ''}
                                            rules={[
                                                {
                                                    required: true,
                                                    message: 'client.principal不能为空',
                                                },
                                            ]}
                                        >
                                            <Select>{principalsOptions}</Select>
                                        </Form.Item>
                                        <Form.Item
                                            label="master.kerberos"
                                            name="hbase_master_kerberos_principal"
                                            required
                                            initialValue={detailData?.hbase_master_kerberos_principal}
                                            rules={[
                                                {
                                                    required: true,
                                                    message: 'master.kerberos 不能为空',
                                                },
                                                {
                                                    max: 128,
                                                    message: 'master.kerberos 不可超过128个字符',
                                                },
                                            ]}
                                        >
                                            <Input autoComplete="off" />
                                        </Form.Item>
                                        <Form.Item
                                            label="regioserver.kerberos"
                                            name="hbase_regionserver_kerberos_principal"
                                            required
                                            initialValue={detailData?.hbase_regionserver_kerberos_principal}
                                            rules={[
                                                {
                                                    required: true,
                                                    message: 'regioserver.kerberos 不能为空',
                                                },
                                                {
                                                    max: 128,
                                                    message: 'regioserver.kerberos 不可超过128个字符',
                                                },
                                            ]}
                                        >
                                            <Input autoComplete="off" />
                                        </Form.Item>
                                    </>
                                )
                            }
                        </Form.Item>
                    </div>
                );
            case 'FtpReact':
                return (
                    <>
                        <Form.Item
                            label="协议"
                            key={index}
                            required
                            name="protocol"
                            initialValue={detailData?.protocol || PROTOCOL_VALUE.FTP}
                            rules={[
                                {
                                    required: true,
                                    message: '协议不能为空',
                                },
                            ]}
                        >
                            <Radio.Group>
                                <Radio value={PROTOCOL_VALUE.FTP}>FTP</Radio>
                                <Radio value={PROTOCOL_VALUE.SFTP}>SFTP</Radio>
                            </Radio.Group>
                        </Form.Item>
                        <Form.Item
                            noStyle
                            shouldUpdate={(prevValues, curValues) => prevValues.protocol !== curValues.protocol}
                        >
                            {({ getFieldValue }) =>
                                getFieldValue('protocol') === PROTOCOL_VALUE.FTP && (
                                    <Form.Item
                                        label="连接模式"
                                        name="connectMode"
                                        required
                                        initialValue={detailData?.connectMode || PROTOCOL_MODE_VALUE.PORT}
                                        rules={[
                                            {
                                                required: true,
                                                message: '连接模式不能为空',
                                            },
                                        ]}
                                    >
                                        <Radio.Group>
                                            <Radio value={PROTOCOL_MODE_VALUE.PORT}>Port (主动)</Radio>
                                            <Radio value={PROTOCOL_MODE_VALUE.PASSIVE}>Pasv（被动）</Radio>
                                        </Radio.Group>
                                    </Form.Item>
                                )
                            }
                        </Form.Item>

                        <Form.Item
                            noStyle
                            shouldUpdate={(prevValues, curValues) => prevValues.protocol !== curValues.protocol}
                        >
                            {({ getFieldValue }) =>
                                getFieldValue('protocol') === PROTOCOL_VALUE.SFTP && (
                                    <>
                                        <Form.Item
                                            label="认证方式"
                                            required
                                            name="auth"
                                            initialValue={detailData?.auth || PROTOCOL_AUTH_MODE_VALUE.PASSWORD}
                                            rules={[
                                                {
                                                    required: true,
                                                    message: '认证方式不能为空',
                                                },
                                            ]}
                                        >
                                            <Radio.Group>
                                                <Radio value={PROTOCOL_AUTH_MODE_VALUE.PASSWORD}>密码</Radio>
                                                <Radio value={PROTOCOL_AUTH_MODE_VALUE.SSH}>私钥</Radio>
                                            </Radio.Group>
                                        </Form.Item>
                                        <Form.Item
                                            noStyle
                                            shouldUpdate={(prevValues, curValues) => prevValues.auth !== curValues.auth}
                                        >
                                            {({ getFieldValue: otherGetFieldValue }) =>
                                                otherGetFieldValue('auth') === PROTOCOL_AUTH_MODE_VALUE.SSH && (
                                                    <Form.Item
                                                        label="私钥地址"
                                                        required
                                                        tooltip="用户的私钥储存路径，默认为~/.ssh/id_rsa"
                                                        name="rsaPath"
                                                        initialValue="~/.ssh/id_rsa"
                                                        rules={[
                                                            {
                                                                required: true,
                                                                message: '私钥地址不能为空',
                                                            },
                                                        ]}
                                                    >
                                                        <Input autoComplete="off" />
                                                    </Form.Item>
                                                )
                                            }
                                        </Form.Item>
                                    </>
                                )
                            }
                        </Form.Item>
                    </>
                );
            case 'CarbonReact':
                return (
                    <>
                        <Form.Item
                            label="HDFS配置"
                            key={index}
                            required
                            name="hdfsCustomConfig"
                            initialValue={detailData?.hdfsCustomConfig || HDFS_CONFIG_VALUE.DEFAULT}
                        >
                            <Radio.Group>
                                <Radio value={HDFS_CONFIG_VALUE.DEFAULT}>默认</Radio>
                                <Radio value={HDFS_CONFIG_VALUE.CUSTOM}>custom</Radio>
                            </Radio.Group>
                        </Form.Item>

                        <Form.Item
                            noStyle
                            shouldUpdate={(prevValues, curValues) =>
                                prevValues.hdfsCustomConfig !== curValues.hdfsCustomConfig
                            }
                        >
                            {({ getFieldValue }) =>
                                getFieldValue('hdfsCustomConfig') === HDFS_CONFIG_VALUE.CUSTOM && (
                                    <>
                                        <Form.Item
                                            label="defaultFS"
                                            name="defaultFS"
                                            required
                                            initialValue={detailData?.defaultFS || ''}
                                            rules={[
                                                {
                                                    required: true,
                                                    message: 'defaultFS不能为空',
                                                },
                                            ]}
                                        >
                                            <Input placeholder="hdfs://host:port" autoComplete="off" />
                                        </Form.Item>
                                        <Form.Item label="高可用配置">
                                            <Form.Item
                                                tooltip={<HDFSTooltips />}
                                                name="hadoopConfig"
                                                noStyle
                                                initialValue={detailData?.hadoopConfig || ''}
                                            >
                                                <TextArea id="copy" rows={4} placeholder={HDFSCONG} />
                                            </Form.Item>
                                            <div className="help-module">
                                                <span
                                                    onClick={() =>
                                                        handleCopy({
                                                            label: '高可用配置',
                                                            placeHold: HDFSCONG,
                                                        })
                                                    }
                                                >
                                                    点击复制模板
                                                </span>
                                            </div>
                                        </Form.Item>
                                    </>
                                )
                            }
                        </Form.Item>
                    </>
                );
            case 'KafkaReact':
                return (
                    <>
                        <Form.Item
                            label="连接方式"
                            key="kafkaType"
                            name="kafkaType"
                            initialValue={detailData?.kafkaType || KAFKA_CONNECTION_TYPE.CLUSTER}
                            required
                            rules={[
                                {
                                    required: true,
                                    message: '模式不能为空',
                                },
                            ]}
                        >
                            <Radio.Group>
                                <Radio value={KAFKA_CONNECTION_TYPE.CLUSTER}>集群地址</Radio>
                                <Radio value={KAFKA_CONNECTION_TYPE.BROKER}>Broker地址</Radio>
                            </Radio.Group>
                        </Form.Item>
                        <Form.Item
                            noStyle
                            shouldUpdate={(prevValues, curValues) => prevValues.kafkaType !== curValues.kafkaType}
                        >
                            {({ getFieldValue }) =>
                                getFieldValue('kafkaType') === KAFKA_CONNECTION_TYPE.CLUSTER ? (
                                    <Form.Item
                                        label="集群地址"
                                        key="address"
                                        name="address"
                                        initialValue={detailData?.address || ''}
                                        required
                                        rules={[
                                            {
                                                required: true,
                                                message: '集群地址不能为空',
                                            },
                                        ]}
                                    >
                                        <TextArea
                                            rows={4}
                                            placeholder="请填写Kafka对应的ZooKeeper集群地址，例如：IP1:Port,IP2：Port,IP3：Port/子目录"
                                        />
                                    </Form.Item>
                                ) : getFieldValue('kafkaType') === KAFKA_CONNECTION_TYPE.BROKER ? (
                                    <Form.Item
                                        label="broker地址"
                                        key="brokerList"
                                        name="brokerList"
                                        initialValue={detailData?.brokerList || ''}
                                        required
                                        rules={[
                                            {
                                                required: true,
                                                message: 'broker地址不能为空',
                                            },
                                        ]}
                                    >
                                        <TextArea
                                            rows={4}
                                            placeholder="Broker地址，例如IP1:Port,IP2:Port,IP3:Port/子目录"
                                        />
                                    </Form.Item>
                                ) : null
                            }
                        </Form.Item>
                        <Form.Item
                            label="认证方式"
                            key="authentication"
                            name="authentication"
                            initialValue={detailData?.authentication || KAFKA_AUTH_VALUES.NULL}
                            required
                            rules={[
                                {
                                    required: true,
                                    message: '认证方式不能为空',
                                },
                            ]}
                        >
                            <Select>
                                {KAFKA_OPTION.map((o) => (
                                    <Option key={o.key} value={o.key}>
                                        {o.name}
                                    </Option>
                                ))}
                            </Select>
                        </Form.Item>
                        <Form.Item
                            noStyle
                            shouldUpdate={(prevValues, curValues) =>
                                prevValues.authentication !== curValues.authentication
                            }
                        >
                            {({ getFieldValue }) =>
                                getFieldValue('authentication') === KAFKA_AUTH_VALUES.SASL_PLAINTEXT && (
                                    <>
                                        <Form.Item
                                            label="用户名"
                                            key="username"
                                            name="username"
                                            initialValue={detailData?.username}
                                            required
                                            rules={[
                                                {
                                                    required: true,
                                                    message: '用户名不能为空',
                                                },
                                            ]}
                                        >
                                            <Input type="text" />
                                        </Form.Item>
                                        <Form.Item
                                            label="密码"
                                            key="password"
                                            name="password"
                                            initialValue={detailData?.password}
                                            required={!!item.required}
                                        >
                                            <Input type="password" />
                                        </Form.Item>
                                    </>
                                )
                            }
                        </Form.Item>
                        <Form.Item
                            noStyle
                            shouldUpdate={(prevValues, curValues) =>
                                prevValues.authentication !== curValues.authentication ||
                                prevValues.kerberosFile !== curValues.kerberosFile
                            }
                        >
                            {({ getFieldValue }) =>
                                getFieldValue('authentication') === KAFKA_AUTH_VALUES.KERBEROS && (
                                    <>
                                        <Form.Item
                                            noStyle
                                            shouldUpdate={(prevValues, curValues) =>
                                                prevValues.openKerberos !== curValues.openKerberos
                                            }
                                        >
                                            {uploadForm()}
                                        </Form.Item>

                                        {getFieldValue('kerberosFile') && (
                                            <Form.Item
                                                label="Kerberos Principal"
                                                key="principal"
                                                name="principal"
                                                required
                                                rules={[
                                                    {
                                                        required: true,
                                                        message: 'Kerberos Principal不可为空',
                                                    },
                                                ]}
                                                initialValue={detailData?.principal || principalsList[0] || ''}
                                            >
                                                <Select>{principalsOptions}</Select>
                                            </Form.Item>
                                        )}
                                    </>
                                )
                            }
                        </Form.Item>
                    </>
                );
            case 'RedisReact':
                return (
                    <>
                        <Form.Item
                            label="模式"
                            key="redisType"
                            name="redisType"
                            initialValue={detailData?.redisType || REDIS_TYPE_VALUE.STANDALONE}
                            required
                            rules={[
                                {
                                    required: true,
                                    message: '模式不能为空',
                                },
                            ]}
                        >
                            <Radio.Group>
                                <Radio value={REDIS_TYPE_VALUE.STANDALONE}>单机</Radio>
                                <Radio value={REDIS_TYPE_VALUE.SENTINEL}>哨兵</Radio>
                                <Radio value={REDIS_TYPE_VALUE.CLUSTER}>集群</Radio>
                            </Radio.Group>
                        </Form.Item>
                        <Form.Item
                            noStyle
                            shouldUpdate={(prevValues, curValues) => prevValues.redisType !== curValues.redisType}
                        >
                            {({ getFieldValue }) => (
                                <Form.Item
                                    label="地址"
                                    key="hostPort"
                                    name="hostPort"
                                    initialValue={detailData?.hostPort || ''}
                                    required
                                    rules={[
                                        {
                                            required: true,
                                            message: '地址不能为空',
                                        },
                                    ]}
                                >
                                    <TextArea
                                        rows={4}
                                        placeholder={
                                            getFieldValue('redisType') === REDIS_TYPE_VALUE.STANDALONE
                                                ? 'Redis地址，例如：IP1:Port'
                                                : 'Redis地址，例如：IP1:Port，多个地址以英文逗号分开'
                                        }
                                    />
                                </Form.Item>
                            )}
                        </Form.Item>
                        <Form.Item
                            noStyle
                            shouldUpdate={(prevValues, curValues) => prevValues.redisType !== curValues.redisType}
                        >
                            {({ getFieldValue }) =>
                                getFieldValue('redisType') === REDIS_TYPE_VALUE.SENTINEL && (
                                    <Form.Item
                                        label="master名称"
                                        key="masterName"
                                        name="masterName"
                                        initialValue={detailData?.masterName || ''}
                                        required
                                        rules={[
                                            {
                                                required: true,
                                                message: 'master名称不能为空',
                                            },
                                        ]}
                                    >
                                        <Input placeholder="请输入master名称" autoComplete="off" />
                                    </Form.Item>
                                )
                            }
                        </Form.Item>
                        <Form.Item
                            noStyle
                            shouldUpdate={(prevValues, curValues) => prevValues.redisType !== curValues.redisType}
                        >
                            {({ getFieldValue }) =>
                                (getFieldValue('redisType') === REDIS_TYPE_VALUE.STANDALONE ||
                                    getFieldValue('redisType') === REDIS_TYPE_VALUE.SENTINEL) && (
                                    <Form.Item label="数据库" name="database" initialValue={detailData?.database || ''}>
                                        <Input autoComplete="off" />
                                    </Form.Item>
                                )
                            }
                        </Form.Item>
                        <Form.Item label="密码" name="password" initialValue={detailData?.password || ''}>
                            <Input.Password visibilityToggle={false} />
                        </Form.Item>
                    </>
                );
            case 'WebSocketSub':
                return (
                    <Form.Item label="鉴权参数" key="webSocketParams">
                        <Form.List name="webSocketParams">
                            {(fields, { add, remove }) => (
                                <>
                                    {fields.map((field) => (
                                        <Space
                                            key={field.key}
                                            style={{
                                                display: 'flex',
                                                marginBottom: 8,
                                            }}
                                            align="baseline"
                                        >
                                            <Form.Item name={[field.name, 'key']} noStyle>
                                                <Input autoComplete="off" placeholder="请输入key值" />
                                            </Form.Item>
                                            <Form.Item name={[field.name, 'value']} noStyle>
                                                <Input.Password
                                                    autoComplete="off"
                                                    placeholder="请输入value值"
                                                    visibilityToggle={false}
                                                />
                                            </Form.Item>
                                            <a
                                                onClick={() => {
                                                    remove(field.name);
                                                }}
                                            >
                                                删除
                                            </a>
                                        </Space>
                                    ))}
                                    <span className="ws-add" onClick={() => handleAddws(add)}>
                                        <PlusCircleOutlined />
                                        <span>新增参数</span>
                                    </span>
                                </>
                            )}
                        </Form.List>
                    </Form.Item>
                );

            default:
                return null;
        }
    });

    return (
        <div className="info-config">
            <Form
                form={form}
                {...formItemLayout}
                autoComplete="off"
                preserve={false}
                onValuesChange={(_, allValues) => onValuesChange?.(allValues)}
                className="info-config-form"
                initialValues={{
                    // some dataSources don't have version
                    dataType: record.dataType + (version || ''),
                }}
            >
                <Form.Item
                    label="数据源类型"
                    name="dataType"
                    rules={[
                        {
                            required: true,
                            message: '数据源类型不能为空',
                        },
                    ]}
                >
                    <Input disabled />
                </Form.Item>
                <Spin spinning={loading}>{formItem}</Spin>
            </Form>
        </div>
    );
});
