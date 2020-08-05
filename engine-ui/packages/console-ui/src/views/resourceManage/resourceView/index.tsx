
// 剩余资源
import * as React from 'react'
import { Table } from 'antd'
import Api from '../../../api/console'
import { cloneDeep } from 'lodash'
import Chart from '../../../components/chart'
import { pieOption, ALARM_DEFAULT, ALARM_HIGHT } from './constant'

interface ResouceProps {
    type: string;
    title: string;
    useNum: number;
    total: number;
    value: number;
}

const ResourceCard = (props: ResouceProps) => {
    function setOptions (value: number) {
        const option = cloneDeep(pieOption)
        option.series[1].data = [
            {
                ...option.series[1].data[0],
                value,
                itemStyle: {
                    normal: {
                        color: value >= ALARM_DEFAULT ? (value >= ALARM_HIGHT ? '#FF5F5C' : '#FFB310') : '#16DE9A'
                    }
                }
            },
            {
                ...option.series[1].data[1],
                value: 150 - value
            }
        ]
        return option
    }

    const { title, useNum, total, value = 0 } = props
    const option = setOptions(value)

    return (
        <div className="c-resourceCard__container">
            <Chart option={option} width={110} height={110} />
            <div className="c-resourceCard__container__title">
                <p>{title}</p>
                <p><span style={{ fontSize: 18 }}>{useNum || '-'}</span> / {total || '-'}</p>
            </div>
        </div>
    )
}

interface ResoruceTableProps {
    columns: any[];
    data: any[];
    title: string;
    desc?: string;
}

const RenderTable = (props: ResoruceTableProps) => {
    const { columns, data, title, desc = '' } = props
    return (
        <div className="c-resourceView__table__container">
            <p>{title}{desc && `（${desc}）`}</p>
            <Table
                className="dt-table-border dt-table-last-row-noborder"
                style={{ marginTop: '10px' }}
                columns={columns}
                dataSource={data}
                pagination={false}
            />
        </div>
    )
}

class Resource extends React.Component<any, any> {
    state: any = {
        nodesListSource: [],
        queuesListSource: [],
        target: '',
        resourceMetrics: {}
    }

    componentDidMount () {
        this.getClusterResources()
    }

    // 获取资源信息
    getClusterResources () {
        const { clusterName } = this.props;
        Api.getClusterResources({
            clusterName: clusterName
        }).then((res: any) => {
            if (res.code == 1) {
                this.setState({
                    nodesListSource: res.data.nodes || [],
                    queuesListSource: res.data.queues || [],
                    resourceMetrics: res.data.resourceMetrics || {}
                })
            }
        })
    }

    initNodesColumns () {
        return [
            {
                title: 'nodeName',
                dataIndex: 'nodeName',
                render (_, record: any) {
                    return record.nodeName || '-';
                }
            },
            {
                title: 'virtualCores',
                dataIndex: 'virtualCores',
                render (_, record: any) {
                    return record.virtualCores;
                }
            },
            {
                title: 'usedVirtualCores',
                dataIndex: 'usedVirtualCores',
                render (_, record: any) {
                    return record.usedVirtualCores;
                }
            },
            {
                title: 'memory (M)',
                dataIndex: 'memory',
                render (_, record: any) {
                    return record.memory;
                }
            },
            {
                title: 'usedMemory (M)',
                dataIndex: 'usedMemory',
                render (_, record: any) {
                    return record.usedMemory;
                }
            }
        ]
    }

    initQueuesColumns = () => {
        return [
            {
                title: '资源队列',
                dataIndex: 'queueName',
                render (_, record: any) {
                    return record.queueName;
                }
            },
            {
                title: '已使用容量',
                dataIndex: 'usedCapacity',
                render (_, record: any) {
                    return `${record.usedCapacity}%`;
                }
            },
            {
                title: '分配容量',
                dataIndex: 'capacity',
                render (_, record: any) {
                    return `${record.capacity}%`;
                }
            },
            {
                title: '最大容量',
                dataIndex: 'maxCapacity',
                render (_, record: any) {
                    return `${record.maxCapacity}%`;
                }
            },
            {
                title: '查看',
                dataIndex: 'action',
                render: (_, record: any) => {
                    return <a onClick={() => {
                        this.setState({
                            target: record
                        })
                    }}>资源详情</a>
                },
                width: 150
            }
        ]
    }

    initDetailtColumns = () => {
        return [
            {
                title: 'Username',
                dataIndex: 'username'
            },
            {
                title: 'Max Resource',
                dataIndex: 'maxResource',
                render (text: any) {
                    return (
                        <span>
                            memory:<span style={{ margin: 5 }}>{text.memory},</span>
                            vCores:<span style={{ margin: 5 }}>{text.vCores}</span>
                        </span>
                    )
                }
            },
            {
                title: 'Used Resource',
                dataIndex: 'resourcesUsed',
                render (text: any) {
                    return (
                        <span>
                            memory:<span style={{ margin: 5 }}>{text.memory},</span>
                            vCores:<span style={{ margin: 5 }}>{text.vCores}</span>
                        </span>
                    )
                }
            },
            {
                title: 'Max AM Resource',
                dataIndex: 'maxAMResource',
                render (text: any) {
                    return (
                        <span>
                            memory:<span style={{ margin: 5 }}>{text.memory},</span>
                            vCores:<span style={{ margin: 5 }}>{text.vCores}</span>
                        </span>
                    )
                }
            },
            {
                title: 'Used AM Resource',
                dataIndex: 'AMResourceUsed',
                render (text: any) {
                    return (
                        <span>
                            memory:<span style={{ margin: 5 }}>{text.memory},</span>
                            vCores:<span style={{ margin: 5 }}>{text.vCores}</span>
                        </span>
                    )
                }
            }
        ]
    }

    render () {
        const columnsNodes = this.initNodesColumns()
        const columnsQueues = this.initQueuesColumns()
        const columnsDetail = this.initDetailtColumns()
        const { nodesListSource, target, queuesListSource } = this.state;
        const { usedCores, totalCores, usedMem, totalMem,
            memRate, coresRate } = this.state.resourceMetrics
        return (
            <div>
                <div className="c-resourceView__container">
                    <div style={{ height: 110, width: '50%', marginRight: 10 }}>
                        <ResourceCard
                            type='cpu'
                            title='CPU（core）'
                            useNum={usedCores}
                            total={totalCores}
                            value={coresRate} />
                    </div>
                    <div style={{ height: 110, width: '50%', marginLeft: 10 }}>
                        <ResourceCard
                            type='memory'
                            title='内存（GB）'
                            useNum={usedMem}
                            total={totalMem}
                            value={memRate} />
                    </div>
                </div>
                <RenderTable
                    columns={columnsNodes}
                    data={nodesListSource}
                    title='Yarn-NodeManager资源使用' />
                <RenderTable
                    columns={columnsQueues}
                    data={queuesListSource}
                    title='各资源队列资源使用' />
                { target ? <RenderTable
                    columns={columnsDetail}
                    data={target.users}
                    title='资源详情'
                    desc={target.queueName} /> : null }
            </div>
        )
    }
}
export default Resource;
