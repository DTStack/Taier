/*
* @Author: 12574
* @Date:   2018-09-19 19:24:01
* @Last Modified by:   12574
* @Last Modified time: 2018-09-19 19:25:03
*/

// 顺序调整

import React, { Component } from 'react';
import { Modal } from "antd";
class Reorder extends Component {
	
	render() {
		return (
			<Modal
				title="执行顺序"
				visible={this.props.visible}
				onCancel={this.props.onCancel}
			>
				<p>选中一个group之后才可以进行顺序调整</p>
			</Modal>
		)
	}
}
export default Reorder;