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

import { useRef,useState } from 'react';
import { SyncOutlined } from '@ant-design/icons';
import molecule from '@dtinsight/molecule';
import { Scrollbar } from '@dtinsight/molecule/esm/components';
import type { FormInstance } from 'antd';
import { Button, message,Spin, Steps } from 'antd';
import Base64 from 'base-64';

import API from '@/api';
import { ID_COLLECTIONS } from '@/constant';
import type { IDataSourceProps } from '@/interface';
import { utf16to8 } from '@/utils';
import InfoConfig from './InfoConfig';
import SelectSource from './selectSource';
import Version from './version';
import './add.scss';

const { Step } = Steps;

const STEPS = ['选择数据源', '版本选择', '信息配置'];

export interface IDataSourceType {
    dataType: string;
    haveVersion: boolean;
    imgUrl: string;
    typeId: number;
}

interface IDataSource {
    selectedMenu?: string;
    currentDataSource?: IDataSourceType;
    dataVersion?: string;
    configInfo?: Record<string, any>;
}

interface IAddProps {
    record?: IDataSourceProps;
    onSubmit?: () => void;
}

export default function Add({ record, onSubmit }: IAddProps) {
    const isEdit = !!record;
    const [current, setCurrent] = useState<number>(isEdit ? STEPS.length - 1 : 0);
    const [submitBtnStatus, setSubmitBtnStatus] = useState(false);
    const [dataSource, setDataSource] = useState<IDataSource>({});
    const [loading, setLoading] = useState<boolean>(false);
    const form = useRef<FormInstance>(null);

    const handleCancel = () => {
        const groupId = molecule.editor.getGroupIdByTab(ID_COLLECTIONS.CREATE_DATASOURCE_PREFIX);
        molecule.editor.closeTab(ID_COLLECTIONS.CREATE_DATASOURCE_PREFIX, groupId!);
    };

    const handleSelected = (item: IDataSourceType, menuId: string) => {
        setDataSource({
            currentDataSource: item,
            selectedMenu: menuId,
        });
        if (item.haveVersion) {
            setCurrent((c) => c + 1);
        } else {
            setCurrent((c) => c + 2);
        }
    };

    const handleVersion = (version: string) => {
        setDataSource((d) => ({ ...d, dataVersion: version }));
    };

    const request = (handelParams: any, name: 'testCon' | 'testConWithKerberos') => {
        return new Promise<any>((resolve, reject) => {
            let status = 0; // 0 等待 1 完成 2 超时
            let timer: NodeJS.Timeout | undefined = setTimeout(() => {
                if (status === 0) {
                    status = 2;
                    timer = undefined;
                    reject(new Error('测试连通性请求超时'));
                }
            }, 60000);

            API[name](handelParams).then((res) => {
                if (status !== 2) {
                    if (timer) {
                        clearTimeout(timer);
                    }
                    resolve(res);
                    timer = undefined;
                    status = 1;
                }
            });
        });
    };

    const submitForm = (handelParams: Record<string, any>, infoMsg: string) => {
        form.current
            ?.validateFields()
            .then(async (values) => {
                if (values.kerberosFile) {
                    const { success, message: msg, data } = await API.addOrUpdateSourceWithKerberos(handelParams);

                    const edit = infoMsg.startsWith('修改');
                    if (success && data) {
                        message.success(`${infoMsg}`);
                        setTimeout(() => {
                            const groupId = molecule.editor.getGroupIdByTab(
                                edit ? ID_COLLECTIONS.EDIT_DATASOURCE_PREFIX : ID_COLLECTIONS.CREATE_DATASOURCE_PREFIX
                            );
                            molecule.editor.closeTab(
                                edit ? ID_COLLECTIONS.EDIT_DATASOURCE_PREFIX : ID_COLLECTIONS.CREATE_DATASOURCE_PREFIX,
                                groupId!
                            );
                            onSubmit?.();
                        }, 500);
                    } else {
                        message.error(`${msg}` || '保存失败');
                    }
                } else {
                    const { success, message: msg, data } = await API.addDatasource(handelParams);

                    if (success && data) {
                        message.success(`${infoMsg}`);
                        const edit = infoMsg.startsWith('修改');
                        setTimeout(() => {
                            const groupId = molecule.editor.getGroupIdByTab(
                                edit ? ID_COLLECTIONS.EDIT_DATASOURCE_PREFIX : ID_COLLECTIONS.CREATE_DATASOURCE_PREFIX
                            );
                            molecule.editor.closeTab(
                                edit ? ID_COLLECTIONS.EDIT_DATASOURCE_PREFIX : ID_COLLECTIONS.CREATE_DATASOURCE_PREFIX,
                                groupId!
                            );
                            onSubmit?.();
                        }, 500);
                    } else {
                        message.error(`${msg}` || '保存失败');
                    }
                }
            })
            .finally(() => {
                setLoading(false);
                setSubmitBtnStatus(false);
            });
    };

    /**
     * 提交和测试连通性复用同一个方法
     */
    const handleTestConnection = (submit = false) => {
        form.current?.validateFields().then((rawValues) => {
            setLoading(true);
            setSubmitBtnStatus(true);
            const values = rawValues;
            const handelParams: any = {
                dataType: isEdit ? record.dataType : dataSource.currentDataSource?.dataType,
                dataVersion: isEdit ? record.dataVersion : dataSource.dataVersion,
                dataName: values.dataName,
                dataDesc: values?.dataDesc || '',
            };

            delete values.dataName;
            delete values.dataDesc;
            delete values.dataType;

            const infoMsg = `${isEdit ? '修改' : '添加'}数据源成功`;
            if (isEdit) {
                // edit need id
                handelParams.id = record.dataInfoId;
            }

            // webSocket定制化
            if (values.webSocketParams?.length) {
                handelParams.webSocketParams = values.webSocketParams.reduce((pre: any, cur: any) => {
                    const next = pre;
                    next[cur.key] = cur.value;
                    return next;
                }, {});
            }

            if (values.kerberosFile) {
                handelParams.file = values.kerberosFile;
                delete values.openKerberos;
                delete values.kerberosFile;

                try {
                    handelParams.dataJsonString = Base64.encode(JSON.stringify(values));
                } catch (error) {
                    // 捕获错误但不处理
                }

                if (submit) {
                    // 确定按钮
                    submitForm(handelParams, infoMsg);
                } else {
                    // 测试连通性按钮
                    request(handelParams, 'testConWithKerberos')
                        .then((res: any) => {
                            if (res.success && res.data) {
                                message.success('连接成功');
                            }
                        })
                        .finally(() => {
                            setLoading(false);
                            setSubmitBtnStatus(false);
                        });
                }
            } else {
                try {
                    handelParams.dataJsonString = Base64.encode(utf16to8(JSON.stringify(values)));
                } catch (error) {
                    // setSubmitBtnStatus(false);
                    // setLoading(false);
                }

                // 将未添加到请求参数中的字段值添加进去
                Object.keys(values).forEach((key) => {
                    if (handelParams[key] === undefined) {
                        handelParams[key] = values[key];
                    }
                });

                if (submit) {
                    // 确定按钮
                    submitForm(handelParams, infoMsg);
                } else {
                    // 测试连通性按钮
                    request(handelParams, 'testCon')
                        .then((res) => {
                            if (res.success && res.data) {
                                message.success('连接成功');
                            }
                        })
                        .finally(() => {
                            setSubmitBtnStatus(false);
                            setLoading(false);
                        });
                }
            }
        });
    };

    const switchContent = (step: number) => {
        const STEPS_CONTENT = [
            <SelectSource
                key="SelectSource"
                defaultMenu={dataSource.selectedMenu}
                defaultDataSource={dataSource.currentDataSource?.dataType}
                onSelectDataSource={handleSelected}
            />,
            <Version key="Version" dataSource={dataSource.currentDataSource!} onSelectVersion={handleVersion} />,
            <Spin spinning={loading} key="InfoConfig">
                <InfoConfig
                    ref={form}
                    record={isEdit ? record : dataSource.currentDataSource!}
                    version={isEdit ? record.dataVersion : dataSource.dataVersion}
                />
            </Spin>,
        ];
        return STEPS_CONTENT[step];
    };

    const PrevStepBtn = (
        <Button
            style={{ marginRight: 8, width: 80 }}
            disabled={isEdit}
            onClick={() => {
                setCurrent((c) => {
                    if (c === STEPS.length - 1 && !dataSource.currentDataSource?.haveVersion) {
                        return c - 2;
                    }
                    return c - 1;
                });
            }}
        >
            上一步
        </Button>
    );

    const NextStepBtn = (
        <Button
            style={{ width: 80 }}
            type="primary"
            disabled={isEdit}
            onClick={() => {
                setCurrent((c) => c + 1);
            }}
        >
            下一步
        </Button>
    );

    const switchFooter = (step: number) => {
        const FOOTER_STEPS = [
            <Button style={{ width: 80 }} onClick={handleCancel} key="cancel">
                取消
            </Button>,
            <>
                {PrevStepBtn}
                {NextStepBtn}
            </>,
            <>
                <Button
                    type="primary"
                    icon={<SyncOutlined />}
                    style={{ marginRight: 60 }}
                    onClick={() => handleTestConnection(false)}
                >
                    <span>测试连通性</span>
                </Button>
                {PrevStepBtn}
                <Button type="primary" onClick={() => handleTestConnection(true)} disabled={submitBtnStatus}>
                    确定
                </Button>
            </>,
        ];

        return FOOTER_STEPS[step];
    };

    return (
        <Scrollbar>
            <div className="source">
                <div className="content">
                    <div className="top-steps">
                        <Steps current={current} size="small">
                            {STEPS.map((title) => (
                                <Step title={title} key={title} disabled={isEdit && title !== '信息配置'} />
                            ))}
                        </Steps>
                    </div>
                    <div className="step-info">{switchContent(current)}</div>
                    <div className="footer-select">{switchFooter(current)}</div>
                </div>
            </div>
        </Scrollbar>
    );
}
