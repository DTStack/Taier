/*
* @Author: 12574
* @Date:   2018-09-18 16:58:30
* @Last Modified by:   12574
* @Last Modified time: 2018-09-18 17:45:02
*/
import React, { Component } from 'react';
import { Modal } from "antd";

class ViewDetail extends Component {
	
	render() {
		return (
			<Modal
				title="任务详情"
				onCancel={this.props.onCancel}
				visible={this.props.visible}
			>
				<p>Some contents...</p>
      			<p>Some contents...</p>
      			<p>Some contents...</p>
      			<p>Some contents...</p>
      			<p>Some contents...</p>
			</Modal>
		)
	}
}
export default ViewDetail;