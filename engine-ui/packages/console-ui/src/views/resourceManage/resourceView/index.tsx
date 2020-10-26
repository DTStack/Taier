
// 剩余资源
import * as React from 'react'
import { Table } from 'antd'
import Api from '../../../api/console'
import { cloneDeep, findKey } from 'lodash'
import Chart from '../../../components/chart'
import { pieOption, ALARM_DEFAULT, ALARM_HIGHT,
    SCHEDULE_TYPE } from './constant'

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
        resourceMetrics: {},
        type: SCHEDULE_TYPE.Capacity
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
                let type = SCHEDULE_TYPE.Capacity;
                let queuesListSource = res.data.queues || [];
                switch (res.data.scheduleInfo.type) {
                    case SCHEDULE_TYPE.Fair:
                        queuesListSource = res.data?.scheduleInfo?.rootQueue?.childQueues
                        type = SCHEDULE_TYPE.Fair
                        break
                    case SCHEDULE_TYPE.FIFO:
                        queuesListSource = [{ ...res.data?.scheduleInfo }]
                        type = SCHEDULE_TYPE.FIFO
                        break
                    default:
                        break
                }
                this.setState({
                    queuesListSource,
                    type,
                    nodesListSource: res.data.nodes || [],
                    resourceMetrics: res.data.resourceMetrics || {}
                })
            }
        })
    }

    handleShowDetailt (record: any) {
        const { target } = this.state
        let newRecord = cloneDeep(record)
        if (target?.queueName === record.queueName && target?.queueName) {
            newRecord = ''
        }
        this.setState({ target: newRecord })
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
        const { type } = this.state
        let colums = [
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
                        this.handleShowDetailt(record)
                    }}>资源详情</a>
                },
                width: 150
            }
        ]
        switch (type) {
            case SCHEDULE_TYPE.Fair:
                colums = [
                    {
                        title: '资源队列',
                        dataIndex: 'queueName',
                        render (_, record: any) {
                            return record.queueName;
                        }
                    },
                    {
                        title: '已使资源数',
                        dataIndex: 'usedResources',
                        render (text: any) {
                            return (
                                <span>
                                    memory:<span style={{ margin: 5 }}>{text.memory},&nbsp;</span>
                                    vCores:<span style={{ margin: 5 }}>{text.vCores}</span>
                                </span>
                            )
                        }
                    },
                    {
                        title: '最大资源数',
                        dataIndex: 'maxResources',
                        render (text: any) {
                            return (
                                <span>
                                    memory:<span style={{ margin: 5 }}>{text.memory},&nbsp;</span>
                                    vCores:<span style={{ margin: 5 }}>{text.vCores}</span>
                                </span>
                            )
                        }
                    },
                    {
                        title: '最小资源数',
                        dataIndex: 'minResources',
                        render (text: any) {
                            return (
                                <span>
                                    memory:<span style={{ margin: 5 }}>{text.memory},&nbsp;</span>
                                    vCores:<span style={{ margin: 5 }}>{text.vCores}</span>
                                </span>
                            )
                        }
                    }
                ]
                break
            case SCHEDULE_TYPE.FIFO:
                colums = [
                    {
                        title: '容量',
                        dataIndex: 'capacity',
                        render (_, record: any) {
                            return record.capacity;
                        }
                    },
                    {
                        title: '已使用容量',
                        dataIndex: 'usedCapacity',
                        render (_, record: any) {
                            return record.usedCapacity;
                        }
                    },
                    {
                        title: '节点数量',
                        dataIndex: 'numNodes',
                        render (_, record: any) {
                            return record.numNodes;
                        }
                    }
                ]
                break
            default:
                break
        }
        return colums;
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
                            memory:<span style={{ margin: 5 }}>{text.memory},&nbsp;</span>
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
                            memory:<span style={{ margin: 5 }}>{text.memory},&nbsp;</span>
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
                            memory:<span style={{ margin: 5 }}>{text.memory},&nbsp;</span>
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
                            memory:<span style={{ margin: 5 }}>{text.memory},&nbsp;</span>
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
        const { nodesListSource, target, queuesListSource, type } = this.state;
        const { usedCores, totalCores, usedMem, totalMem,
            memRate, coresRate } = this.state.resourceMetrics
        console.log(this.state)
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
                    key={type}
                    columns={columnsQueues}
                    data={queuesListSource}
                    title={`各资源队列资源使用（调度方式：${findKey(SCHEDULE_TYPE, (val) => val === type)}）`} />
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
