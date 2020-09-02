import React, { Component } from 'react'
import PropTypes from 'prop-types';
import { Button } from 'antd';
import { isEmpty } from 'lodash';

export default class Downloader extends Component {
	static defaultProps = {
		title: '下载',
		renderNode: 'a',
		onClick: () => { },
		exports: false,
		action:'#',
		param:{},
		style: '',
		type:'primary',
	}
	static propTypes = {
		title: PropTypes.string,/* 名字 */
		renderNode: PropTypes.string,/* 渲染节点 */
		onClick: PropTypes.func,/* 点击函数，若export为true，则onClick失效 */
		exports: PropTypes.bool,/* 是否为导出 */
		action: PropTypes.string,/* 当export为true，则需要传请求地址 */
		param: PropTypes.object,/* 当export为true，则需要传参数 */
		style: PropTypes.any,/* css样式 */
		type: PropTypes.string,/* 按钮类型 */
	}
	constructor(props) {
		super(props);
		this.state = {
		};
	}
	componentDidMount() {
	}
	componentWillReceiveProps(nextProps) {
	}
	shouldComponentUpdate(nextProps, nextState) {
		return this.props != nextProps || this.state != nextState;
	}
	/**
	 * 下载操作
	  */
	handleClickDown = (e) => {
		e.preventDefault();
		const { exports, action, param } = this.props;
		if (exports) {
			var ExportForm = document.createElement("FORM");
			document.body.appendChild(ExportForm);
			ExportForm.method = "GET";

			for (let key in param) {
				let newElement = document.createElement("input");
				newElement.setAttribute("name", key);
				newElement.setAttribute("type", "hidden");
				ExportForm.appendChild(newElement);
				let value = param[key];
				newElement.value = value;
			}

			ExportForm.action = action;
			ExportForm.submit();
		} else {
			this.props.onClick();
		}
	}
	render() {
		const { title, renderNode, style, type } = this.props;
		return (
			renderNode == 'a' ?
				<a href="#" onClick={this.handleClickDown} style={{ ...style, display: 'inline-block' }}>{title}</a>
				:
				<Button icon="download" type={type} onClick={this.handleClickDown} style={{...style}}>{title}</Button>
		)
	}
}
