
// 剩余资源
import React, { Component } from 'react';
import { Modal, Select, Form, Table, Button } from 'antd';
import { formItemLayout } from '../../consts';
import Api from '../../api/console';

const Option = Select.Option;
class Resource extends Component {
    state = {
        // selectHack: false,//select combobox自带bug
        clusterName: undefined,
        // yarn
        yarnListSource: [],
        // flink
        flinkListSource: []
    }

    componentDidMount () {
        this.getClusterResources();
    }
    // 获取资源信息
    getClusterResources () {
        const { clusterName } = this.state;
        Api.getClusterResources({
            clusterName: clusterName
        }).then((res) => {
            const yarnList = res.data ? res.data.yarn : [];
            const flinkList = res.data ? res.data.flink : [];
            this.setState({
                yarnListSource: yarnList,
                flinkListSource: flinkList
            })
        })
    }
    // 改变集群
    changeCluster (value) {
        if (!value) {
            this.setState({
                yarnListSource: [],
                flinkListSource: [],
                clusterName: undefined
            }, this.getClusterResources.bind(this))
        } else {
            this.setState({
                clusterName: value
            }, this.getClusterResources.bind(this))
        }
    }

    // 集群下拉
    getClusterListOptionView () {
        const { clusterList } = this.props;
        return clusterList.map((item, index) => {
            return <Option key={item.id} value={item.clusterName}>{item.clusterName}</Option>
        })
    }
    // 资源不足提示信息
    yarnMessage = (data = []) => {
        // virtualCores总数
        const virtualCores = data.map(item => {
            return item.virtualCores
        })
        const getvirtualCores = (virtualCores) => {
            let sum = 0;
            virtualCores.forEach(val => {
                sum += val
            })
            return sum
        }
        const virtualCoresTotal = getvirtualCores(virtualCores);
        // usedVirtualCores总数
        const usedVirtualCores = data.map(item => {
            return item.usedVirtualCores
        })
        const getusedVirtualCores = (usedVirtualCores) => {
            let sum = 0;
            usedVirtualCores.forEach(val => {
                sum += val
            })
            return sum
        }
        const usedVirtualCoresTotal = getusedVirtualCores(usedVirtualCores);

        // memory总数
        const memory = data.map(item => {
            return item.memory
        })
        const getMemory = (memory) => {
            let sum = 0;
            memory.forEach(val => {
                sum += val
            })
            return sum
        }
        const memoryTotal = getMemory(memory);
        // usedMemory总数
        const usedMemory = data.map(item => {
            return item.usedMemory
        })
        const getUsedMemory = (usedMemory) => {
            let sum = 0;
            usedMemory.forEach(val => {
                sum += val
            })
            return sum
        }
        const usedMemoryTotal = getUsedMemory(usedMemory);
        const coresCompar = (virtualCoresTotal - data.length) <= usedVirtualCoresTotal;
        const memoryCompar = memoryTotal - (data.length) * 512 <= usedMemoryTotal;
        if ((coresCompar || memoryCompar) && data.length > 0) {
            return <p style={{ marginLeft: '40px', color: '#db5a5aed' }}>Yarn 集群计算资源不足（每个节点保留1vcore 512M）</p>
        } else {
            return null
        }
    }
    flinkMessage = (data = []) => {
        // freeSlots
        const freeSlots = data.map(item => {
            return item.freeSlots
        })
        const getfreeSlots = (freeSlots) => {
            let sum = 0;
            freeSlots.forEach(val => {
                sum += val
            })
            return sum
        }
        const freeSlotsTotal = getfreeSlots(freeSlots);
        console.log(freeSlotsTotal)
        if (freeSlotsTotal === 0 && data.length > 0) {
            return <p style={{ marginLeft: '40px', color: '#db5a5aed' }}>Flink 集群计算资源不足</p>
        } else {
            return null
        }
    }
    initYarnColumns () {
        return [
            {
                title: 'virtualCores',
                dataIndex: 'virtualCores',
                render (text, record) {
                    return record.virtualCores;
                },
                width: '150px'
            },
            {
                title: 'usedVirtualCores',
                dataIndex: 'usedVirtualCores',
                render (text, record) {
                    return record.usedVirtualCores;
                },
                width: '170px'
            },
            {
                title: 'memory (M)',
                dataIndex: 'memory',
                render (text, record) {
                    return record.memory;
                },
                width: '150px'
            },
            {
                title: 'usedMemory (M)',
                dataIndex: 'usedMemory',
                render (text, record) {
                    return record.usedMemory;
                },
                width: '160px'
            }
        ]
    }
    initFlinkColumns () {
        return [
            {
                title: 'freeSlots',
                dataIndex: 'freeSlots',
                render (text, record) {
                    return record.freeSlots;
                },
                width: '165px'
            },
            {
                title: 'cpuCores',
                dataIndex: 'cpuCores',
                render (text, record) {
                    return record.cpuCores;
                },
                width: '155px'
            },
            {
                title: 'slotsNumber',
                dataIndex: 'slotsNumber',
                render (text, record) {
                    return record.slotsNumber;
                },
                width: '175px'
            },
            {
                title: 'freeMemory (M)',
                dataIndex: 'freeMemory',
                render (text, record) {
                    return record.freeMemory;
                },
                width: '190px'
            },
            {
                title: 'physicalMemory (M)',
                dataIndex: 'physicalMemory',
                render (text, record) {
                    return record.physicalMemory;
                },
                width: '240px'
            }
        ]
    }
    render () {
        const columnsYarn = this.initYarnColumns();
        const columnsFlink = this.initFlinkColumns();
        const { flinkListSource, yarnListSource } = this.state;
        return (
            <div className="contentBox">
                <Modal
                    title="集群资源"
                    visible={this.props.visible}
                    onCancel={this.props.onCancel}
                    onOk={this.props.onCancel}
                    width="600"
                    className="m-card"
                    footer={[
                        <Button key="submit" type="primary" size="large" onClick={this.props.onCancel}>
                        关闭
                        </Button>
                    ]}
                >
                    <Form.Item
                        label="集群"
                        {...formItemLayout}
                    >
                        <Select
                            style={{ width: '100%' }}
                            onChange={this.changeCluster.bind(this)}
                            placeholder="请选择集群"
                            value={this.state.clusterName}
                            allowClear
                        >
                            {this.getClusterListOptionView()}
                        </Select>
                    </Form.Item>
                    {this.yarnMessage(yarnListSource)}
                    <Table
                        className="m-table"
                        style={{ margin: '0px 20px', marginTop: '10px', marginBottom: '20px' }}
                        columns={columnsYarn}
                        pagination={false}
                        dataSource={yarnListSource}
                        scroll={{ y: 200 }}
                    />
                    {this.flinkMessage(flinkListSource)}
                    <Table
                        className="m-table"
                        style={{ margin: '0px 20px', marginTop: '10px' }}
                        columns={columnsFlink}
                        pagination={false}
                        dataSource={flinkListSource}
                        scroll={{ y: 200 }}
                    />
                </Modal>
            </div>
        )
    }
}
export default Resource;
