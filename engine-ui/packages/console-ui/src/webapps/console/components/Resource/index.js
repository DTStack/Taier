/*
* @Author: 12574
* @Date:   2018-09-25 10:23:07
* @Last Modified by:   12574
* @Last Modified time: 2018-09-25 10:52:39
*/

// 剩余资源
import React, { Component } from 'react';
import { Modal, Select, Form, Table } from "antd";
import { formItemLayout } from "../../consts"
class Resource extends Component {
	state = {
		selectHack: false,//select combobox自带bug
	}

	initColumns() {
		return [
			{
				title: "引擎",
				dataIndex: "engine",
			},
			{
				title: "已占用",
				dataIndex: "locked"
			},
			{
				title: "剩余资源",
				dataIndex: "resource"
			}
		]
	}

	render() {
		const { selectHack } = this.state;
		const columns = this.initColumns();
		return (
			<div className="contentBox">
				<Modal
				title="集群资源"
				visible={this.props.visible}
				onCancel={this.props.onCancel}
				className="m-card"
				>
					<Form.Item
	                        label="集群"
	                        {...formItemLayout}
	                    >
	                        {!selectHack && <Select
	                            mode="combobox"
	                            style={{ width: "100%" }}
	                            placeholder="请选择集群"
	                            // onSelect={this.selectUser.bind(this)}
	                            // onSearch={this.changeUserValue.bind(this)}
	                            // value={selectUser}
	                        >
	                        </Select>}
	                </Form.Item>
	                <Table
	                    className="m-table"
	                    // style={{ margin: "0px 20px", marginTop: "30px" }}
	                    columns={columns}
	                    pagination={false}
	                    // dataSource={this.getTableDataSource()}
	                    // scroll={{ y: 300 }}
	                />
                </Modal>
			</div>
		)
	}
}
export default Resource;