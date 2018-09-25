/*
* @Author: 12574
* @Date:   2018-09-18 17:36:36
* @Last Modified by:   12574
* @Last Modified time: 2018-09-20 11:53:09
*/
import React, { Component } from 'react';
import { Modal } from "antd";
class KillTask extends Component {
	render() {
		return (
			<Modal
				title="杀任务"
				visible={this.props.visible}
				onCancel={this.props.onCancel}
			>
				<p>是否要删除此任务?</p>
			</Modal>
		)
	}
}
export default KillTask;