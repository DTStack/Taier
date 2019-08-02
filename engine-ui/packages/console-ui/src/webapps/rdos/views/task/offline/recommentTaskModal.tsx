import * as React from 'react';

import { Table, Modal, message } from 'antd';

class RecommentTaskModal extends React.Component<any, any> {
    state: any = {
        choosetask: [], // 选择的任务
        selectedRows: []
    }
    resetState () {
        this.setState({
            choosetask: [],
            selectedRows: []
        })
    }
    onOk () {
        const { selectedRows } = this.state;
        if (selectedRows.length == 0) {
            message.warning('请选择依赖');
            return;
        }
        this.props.onOk(selectedRows);
        this.resetState();
    }
    onCancel () {
        this.resetState();
        this.props.onCancel();
    }
    initColumns () {
        return [{
            title: '表名',
            dataIndex: 'tableName',
            width: '200px'
        }, {
            title: '任务名称',
            dataIndex: 'name'
        }]
    }
    rowSelection () {
        const { existTask } = this.props;
        return {
            selectedRowKeys: this.state.choosetask,
            onChange: (selectedRowKeys: any, selectedRows: any) => {
                this.setState({
                    choosetask: selectedRowKeys,
                    selectedRows: selectedRows
                })
            },
            getCheckboxProps: (record: any) => {
                const id = record.id;
                let isExist = false;
                existTask && existTask.map(
                    (item: any) => {
                        if (item.id == id) {
                            isExist = true;
                        }
                    }
                )
                if (isExist) {
                    return { disabled: true }
                }
                return {};
            }
        }
    }
    render () {
        const {
            visible,
            taskList
        } = this.props;
        return (
            <Modal
                title="推荐上游依赖"
                maskClosable={false}
                visible={visible}

                onCancel={this.onCancel.bind(this)}
                onOk={this.onOk.bind(this)}
            >
                <p style={{ margin: '10px 10px' }}>提示：该分析仅基于您已发布过的任务进行分析</p>
                <Table
                    className="dt-ant-table dt-ant-table--border select-all-table"
                    columns={this.initColumns()}
                    dataSource={taskList}
                    pagination={false}
                    rowSelection={this.rowSelection()}
                    scroll={{ y: 400 }}
                />
            </Modal>
        )
    }
}
export default RecommentTaskModal;
