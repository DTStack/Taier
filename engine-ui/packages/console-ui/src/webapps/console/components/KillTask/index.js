/*
* @Author: 12574
* @Date:   2018-09-18 17:36:36
* @Last Modified by:   12574
* @Last Modified time: 2018-09-25 13:14:12
*/
import React, { Component } from 'react';
import { Modal } from "antd";
class KillTask extends Component {
	
	killtask() {
		this.setState({

		})
	}
	render() {
		return (
			<Modal
				title="杀任务"

				visible={this.props.visible}
				onCancel={this.props.onCancel}
				onOk={this.killtask.bind(this)}
			>
				<p>是否要删除此任务?</p>
			</Modal>
		)
	}
}
export default KillTask;