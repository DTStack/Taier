import React, { useState, useEffect } from 'react';
import { Table, Select, Button, Form, Pagination } from 'antd';
import Api from '../../api/console';
import { ALARM_TYPE_TEXT } from '../../consts';
const Option = Select.Option;

const AlarmChannel: React.FC = (props: any) => {
    const [ clusterData, setClusterData ] = useState<any[]>([]);
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
    const initColumns = () => {
        return [{
            title: '通道名称',
            dataIndex: 'name'
        }, {
            title: '告警类型',
            dataIndex: 'alarmType',
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
            dataIndex: 'scenes'
        }, {
            title: '操作',
            dataIndex: 'opera',
            render: () => {
                return <span>
                    <a>编辑</a>
                    <a>删除</a>
                </span>
            }
        }, {
            title: '默认通道',
            dataIndex: 'defaultChannel'
        }]
    }
    const renderClusterOpts = () => {
        return clusterData.map(item => {
            return <Option key={item.id} value={`${item.id}`} data-item={item}>{item.clusterName}</Option>
        })
    }
    const pagination = {
        current: 1,
        pageSize: 10,
        size: 'small',
        total: 10,
        showTotal: (total) => <span>
            共<span style={{ color: '#3F87FF' }}>{total}</span>条数据，每页显示15条
        </span>
    };
    return (
        <div className='alarm__wrapper'>
            <Form layout='inline'>
                <Form.Item label='集群'>
                    <Select className='dt-form-shadow-bg'>
                        {renderClusterOpts()}
                    </Select>
                </Form.Item>
                <Form.Item>
                    <Button type='primary' onClick={() => {
                        props.router.push('/console/alarmChannel/alarmRule')
                    }}>新增告警通道</Button>
                </Form.Item>
            </Form>
            <Table
                className='dt-table-fixed-contain-footer'
                scroll={{ y: true }}
                style={{ height: 'calc(100vh - 154px)' }}
                columns={initColumns()}
                pagination={false}
                footer={() => {
                    return <Pagination
                        {...pagination}
                    />
                }}
            />
        </div>
    )
}
export default AlarmChannel;
