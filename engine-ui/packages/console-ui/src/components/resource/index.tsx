
// 剩余资源
import * as React from 'react';
import { Table, Row, Col } from 'antd';
import Api from '../../api/console';

class Resource extends React.Component<any, any> {
    state: any = {
        yarnListSource: []
    }

    componentDidMount () {
        this.getClusterResources();
    }

    // 获取资源信息
    getClusterResources () {
        const { clusterName } = this.props;
        Api.getClusterResources({
            clusterName: clusterName
        }).then((res: any) => {
            const yarnList = res.data ? res.data.yarn : [];
            this.setState({
                yarnListSource: yarnList
            })
        })
    }

    initYarnColumns () {
        return [
            {
                title: 'NameManager',
                dataIndex: 'nameManager',
                render (text: any, record: any) {
                    return record.nameManager || '-';
                }
            },
            {
                title: 'virtualCores',
                dataIndex: 'virtualCores',
                render (text: any, record: any) {
                    return record.virtualCores;
                }
            },
            {
                title: 'usedVirtualCores',
                dataIndex: 'usedVirtualCores',
                render (text: any, record: any) {
                    return record.usedVirtualCores;
                }
            },
            {
                title: 'memory (M)',
                dataIndex: 'memory',
                render (text: any, record: any) {
                    return record.memory;
                }
            },
            {
                title: 'usedMemory (M)',
                dataIndex: 'usedMemory',
                render (text: any, record: any) {
                    return record.usedMemory;
                }
            }
        ]
    }

    initSourceColumns = () => {
        return [
            {
                title: '资源队列',
                dataIndex: 'name',
                render (text: any, record: any) {
                    return record.virtualCores;
                }
            },
            {
                title: '已使用容量',
                dataIndex: 'freeSlots',
                render (text: any, record: any) {
                    return record.freeSlots;
                }
            },
            {
                title: '分配容量',
                dataIndex: 'cpuCores',
                render (text: any, record: any) {
                    return record.cpuCores;
                }
            },
            {
                title: '最大容量',
                dataIndex: 'slotsNumber',
                render (text: any, record: any) {
                    return record.slotsNumber;
                }
            },
            {
                title: '最小容量',
                dataIndex: 'freeMemory',
                render (text: any, record: any) {
                    return record.freeMemory;
                }
            },
            {
                title: '查看',
                dataIndex: 'action',
                render (_, record: any) {
                    return <a>资源详情</a>
                },
                width: 150
            }
        ]
    }

    initDefaultColumns = () => {
        return [
            {
                title: 'Max Resource',
                dataIndex: 'maxResource',
                render (_, record: any) {
                    return `mermory: ${record.mermory}, vCores: ${record.vCores}`;
                }
            },
            {
                title: 'Used Resource',
                dataIndex: 'usedResource',
                render (_, record: any) {
                    return `mermory: ${record.mermory}, vCores: ${record.vCores}`;
                }
            },
            {
                title: 'Max AM Resource',
                dataIndex: 'maxAmResource',
                render (_, record: any) {
                    return `mermory: ${record.mermory}, vCores: ${record.vCores}`;
                }
            },
            {
                title: 'Used AM Resource',
                dataIndex: 'usedAmResource',
                render (_, record: any) {
                    return `mermory: ${record.mermory}, vCores: ${record.vCores}`;
                }
            }
        ]
    }

    render () {
        const columnsYarn = this.initYarnColumns();
        const columnsSource = this.initSourceColumns();
        const columnsDefault = this.initDefaultColumns()
        const { yarnListSource } = this.state;
        return (
            <div style={{ padding: 20 }}>
                <Row>
                    <Col span={12} style={{ border: '1px solid #999', height: 150, marginRight: 20 }}>
                    </Col>
                    <Col span={11} style={{ border: '1px solid #999', height: 150 }}>
                    </Col>
                </Row>
                <p style={{ fontSize: 16, fontWeight: 500, marginTop: 10 }}>Yarn-NameManager资源使用</p>
                <Table
                    className="dt-table-border dt-table-last-row-noborder"
                    style={{ marginTop: '10px' }}
                    columns={columnsYarn}
                    pagination={false}
                    dataSource={yarnListSource}
                />
                <p style={{ fontSize: 16, fontWeight: 500, marginTop: 10 }}>各资源队列资源使用</p>
                <Table
                    className="dt-table-border dt-table-last-row-noborder"
                    style={{ marginTop: '10px' }}
                    columns={columnsSource}
                    pagination={false}
                    dataSource={[]}
                />
                <p style={{ fontSize: 16, fontWeight: 500, marginTop: 10 }}>资源详情（default）</p>
                <Table
                    className="dt-table-border dt-table-last-row-noborder"
                    style={{ marginTop: '10px' }}
                    columns={columnsDefault}
                    pagination={false}
                    dataSource={[]}
                />
            </div>
        )
    }
}
export default Resource;
