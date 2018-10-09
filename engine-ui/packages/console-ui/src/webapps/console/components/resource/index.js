/*
* @Author: 12574
* @Date:   2018-09-25 10:23:07
* @Last Modified by:   12574
* @Last Modified time: 2018-09-30 16:56:08
*/

// 剩余资源
import React, { Component } from 'react';
import { Modal, Select, Form, Table } from "antd";
import { formItemLayout } from "../../consts";
import Api from "../../api/console";

const Option = Select.Option;
class Resource extends Component {
    state = {
        selectHack: false,//select combobox自带bug
        clusterName: undefined,
        // yarn
        yarnListSource: undefined,
        // flink
        flinkListSource: undefined
    }

    componentDidMount() {
        this.getClusterResources();
    }
    // 获取资源信息
    getClusterResources() {
        const { clusterName } = this.state;
        Api.getClusterResources({
            clusterName: clusterName
        }).then((res) => {
            const yarnList = res.data ? res.data.yarn : undefined;
            const flinkList = res.data ? res.data.flink : undefined;
            this.setState({
                yarnListSource: yarnList,
                flinkListSource: flinkList
            })
        })
    }
    // 改变集群
    changeCluster(value) {
        if (!value) {
            this.setState({
                yarnListSource: undefined,
                flinkListSource: undefined,
                clusterName: undefined
            }, this.getClusterResources.bind(this))
        } else {
            this.setState({
                clusterName: value
            }, this.getClusterResources.bind(this))
        }

    }

    // 集群下拉
    getClusterListOptionView() {
        const { clusterList } = this.props;
        return clusterList.map((item, index) => {
            return <Option key={item.id} value={item.clusterName}>{item.clusterName}</Option>
        })
    }
    closeCluster() {

    }
    initYarnColumns() {
        return [
            {
                title: "virtualCores",
                dataIndex: "virtualCores",
                render(text, record) {
                    return record.virtualCores;
                }
            },
            {
                title: "usedVirtualCores",
                dataIndex: "usedVirtualCores",
                render(text, record) {
                    return record.usedVirtualCores;
                }
            },
            {
                title: "memory",
                dataIndex: "memory",
                render(text, record) {
                    return record.memory;
                }
            },
            {
                title: "usedMemory",
                dataIndex: "usedMemory",
                render(text, record) {
                    return record.usedMemory;
                }
            }
        ]
    }
    initFlinkColumns() {
        return [
            {
                title: "freeSlots",
                dataIndex: "freeSlots",
                render(text, record) {
                    return record.freeSlots;
                }
            },
            {
                title: "cpuCores",
                dataIndex: "cpuCores",
                render(text, record) {
                    return record.cpuCores;
                }
            },
            {
                title: "slotsNumber",
                dataIndex: "slotsNumber",
                render(text, record) {
                    return record.slotsNumber;
                }
            },
            {
                title: "freeMemory",
                dataIndex: "freeMemory",
                render(text, record) {
                    return record.freeMemory;
                }
            },
            {
                title: "physicalMemory",
                dataIndex: "physicalMemory",
                render(text, record) {
                    return record.physicalMemory;
                }
            }
        ]
    }
    render() {
        const { selectHack } = this.state;
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
                    className="m-card"
                >
                    <Form.Item
                        label="集群"
                        {...formItemLayout}
                    >
                        {!selectHack && <Select
                            mode="combobox"
                            style={{ width: "100%" }}
                            onChange={this.changeCluster.bind(this)}
                            placeholder="请选择集群"
                            value={this.state.clusterName}
                            allowClear
                        // onSelect={this.changeCluster.bind(this)}
                        // // onSearch={this.changeCluster.bind(this)}
                        // value={selectUser}
                        >
                            {this.getClusterListOptionView()}
                        </Select>}
                    </Form.Item>
                    <Table
                        className="m-table"
                        style={{ margin: "0px 20px", marginTop: "30px" }}
                        columns={columnsYarn}
                        pagination={false}
                        dataSource={yarnListSource}
                        scroll={{ y: 200 }}
                    />
                    <Table
                        className="m-table"
                        style={{ margin: "0px 20px", marginTop: "30px" }}
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