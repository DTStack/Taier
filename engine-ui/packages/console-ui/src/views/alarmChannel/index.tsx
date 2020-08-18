import React, { useState, useEffect } from 'react';
import { Table, Button, Form, Pagination, message, Modal } from 'antd';
import Api from '../../api/console';
import { ALARM_TYPE_TEXT } from '../../consts';
interface PaginationTypes {
    currentPage: number;
    pageSize: number;
    total?: number;
}
const { confirm } = Modal;

const AlarmChannel: React.FC = (props: any) => {
    const [pagination, setPagination] = useState<PaginationTypes>({ currentPage: 1, total: 0, pageSize: 15 });
    const [params, setParams] = useState<any>({ alertGateType: [] });
    const useAlarmList = (query, pagination) => {
        const [loading, setLoading] = useState<boolean>(false);
        const [alarmList, setAlarmList] = useState<any[]>([])
        useEffect(() => {
            const getAlarmRuleList = async () => {
                setLoading(true);
                const { currentPage, pageSize } = pagination;
                const { alertGateType } = params;
                let res = await Api.getAlarmRuleList({
                    currentPage: currentPage,
                    pageSize: pageSize,
                    alertGateType
                });
                if (res && res.code == 1) {
                    setAlarmList(res.data?.data || []);
                    setPagination(state => ({ ...state, total: res.data.totalCount }));
                }
                setLoading(false);
            }
            getAlarmRuleList().then();
        }, [query])
        return [{ loading, alarmList }]
    }
    const deleteRule = async (id: number) => {
        let res = await Api.deleteAlarmRule({ id })
        if (res.code === 1) {
            message.success('删除成功！')
        }
    }
    const editAlarm = async (id: number) => {
        let res = await Api.getByAlertId({ id });
        if (res.code === 1) {
            props.router.push({
                pathname: '/console/alarmChannel/alarmRule',
                state: {
                    id,
                    ruleData: res.data || {}
                }
            })
        }
    }
    const initColumns = () => {
        return [{
            title: '通道名称',
            dataIndex: 'alertGateName'
        }, {
            title: '告警类型',
            dataIndex: 'alertGateType',
            filters: Object.entries(ALARM_TYPE_TEXT).map(([key, value]) => {
                return {
                    text: value,
                    value: key
                }
            }),
            render: (text: number) => {
                return ALARM_TYPE_TEXT[text]
            }
        }, {
            title: '通道标识',
            dataIndex: 'alertGateSource'
        }, {
            title: '操作',
            dataIndex: 'opera',
            render: (text: any, record) => {
                const { alertId } = record
                return <span>
                    <a onClick={() => { editAlarm(alertId) }}>编辑</a>
                    <span className="ant-divider" ></span>
                    <a onClick={() => { deleteRule(alertId) }}>删除</a>
                </span>
            }
        }, {
            title: '默认通道',
            dataIndex: 'isDefault',
            render: (text, record: any) => {
                const showText = `${ALARM_TYPE_TEXT[record.alertGateType].slice(0, 2)}默认通道`;
                if (text) {
                    return <Button disabled type='primary'>{showText}</Button>
                } else {
                    return <Button type='primary' onClick={() => { setDefaultChannel(record) }}>{`设为${showText}`}</Button>
                }
            }
        }]
    }
    const setDefaultChannel = (record) => {
        const { alertId, alertGateType, alertGateName } = record;
        const showText = `${ALARM_TYPE_TEXT[alertGateType].slice(0, 2)}默认通道`;
        confirm({
            title: `确定将“${alertGateName}(通道名称)” 设为${showText}吗`,
            content: '设置为默认告警通道后，各应用的告警信息将走此通道',
            onOk () {
                Api.setDefaultAlert({ alertId, alertGateType }).then(res => {
                    if (res.code === 1) {
                        message.success('操作成功')
                    }
                })
            },
            onCancel () {
                console.log('Cancel');
            }
        });
    }
    const handleTableChange = (paginations: any, filters: any, sorter: any) => {
        setParams(state => ({ ...state, alertGateType: filters.alertGateType || [] }));
        setPagination(state => ({ ...state, currentPage: paginations.current || 1 }));
    }
    const [{ loading, alarmList }] = useAlarmList(params, pagination)
    return (
        <div className='alarm__wrapper'>
            <Form layout='inline'>
                <Form.Item>
                    <Button type='primary' onClick={() => {
                        props.router.push({
                            pathname: '/console/alarmChannel/alarmRule',
                            query: {
                                isCreate: true
                            }
                        })
                    }}>新增告警通道</Button>
                </Form.Item>
            </Form>
            <Table
                className='dt-table-fixed-contain-footer'
                scroll={{ y: true }}
                style={{ height: 'calc(100vh - 154px)' }}
                loading={loading}
                columns={initColumns()}
                dataSource={alarmList}
                pagination={false}
                onChange={handleTableChange}
                footer={() => {
                    return <Pagination
                        {...{
                            current: pagination.currentPage,
                            pageSize: pagination.pageSize,
                            size: 'small',
                            total: pagination.total,
                            showTotal: (total) => <span>
                                共<span style={{ color: '#3F87FF' }}>{total}</span>条数据，每页显示15条
                            </span>
                        }}
                    />
                }}
            />
        </div>
    )
}
export default AlarmChannel;
