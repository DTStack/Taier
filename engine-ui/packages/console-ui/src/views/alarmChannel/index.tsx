import React, { useState, useEffect } from 'react';
import { Table, Select, Button, Form, Pagination, message } from 'antd';
import Api from '../../api/console';
import { ALARM_TYPE_TEXT } from '../../consts';
const Option = Select.Option;
interface PaginationTypes {
    currentPage: number;
    pageSize: number;
    total?: number;
}

const AlarmChannel: React.FC = (props: any) => {
    const [clusterData, setClusterData] = useState<any[]>([]);
    const [pagination, setPagination] = useState<PaginationTypes>({ currentPage: 1, total: 0, pageSize: 15 });
    const [params, setParams] = useState<any>({ clusterId: '' });
    const getClusterList = async () => {
        let res = await Api.getAllCluster();
        if (res.code === 1) setClusterData(res.data || [])
    }
    useEffect(() => {
        getClusterList()
        return () => {
            // cleanup
        }
    }, [])

    const useAlarmList = (query, pagination) => {
        const [loading, setLoading] = useState<boolean>(false);
        const [alarmList, setAlarmList] = useState<any[]>([])
        useEffect(() => {
            const getAlarmRuleList = async () => {
                setLoading(true);
                const { currentPage, pageSize } = pagination;
                const { clusterId } = params;
                let res = await Api.getAlarmRuleList({
                    currentPage: currentPage,
                    pageSize: pageSize,
                    clusterId: clusterId
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
    const handeChangeCluster = (value) => {
        setParams(state => ({ ...state, clusterId: value }))
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
                query: {
                    clusterId: params.clusterId
                },
                state: {
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
            title: '使用场景',
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
            render: () => {
                return (
                    <Button type='primary'>短信默认通道</Button>
                )
            }
        }]
    }
    const renderClusterOpts = () => {
        return clusterData.map(item => {
            return <Option key={item.id} value={`${item.id}`} data-item={item}>{item.clusterName}</Option>
        })
    }
    const [{ loading, alarmList }] = useAlarmList(params, pagination)
    return (
        <div className='alarm__wrapper'>
            <Form layout='inline'>
                <Form.Item label='集群'>
                    <Select className='dt-form-shadow-bg' onChange={handeChangeCluster}>
                        {renderClusterOpts()}
                    </Select>
                </Form.Item>
                <Form.Item>
                    <Button type='primary' onClick={() => {
                        props.router.push({
                            pathname: '/console/alarmChannel/alarmRule',
                            query: {
                                clusterId: params.clusterId,
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
