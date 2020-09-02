import React, { Component } from 'react'
import PropTypes from 'prop-types';
import { Upload, Button, message } from 'antd';
export default class Uploader extends Component {
	static defaultProps = {
		title: '上传',
		action: '#',
		name: 'file',
		accept: '',
		showUploadList: false,
		multiple: false,
	}
	static propTypes = {
		title: PropTypes.string,/* button的名字 */
		onChange: PropTypes.func,
		beforeUpload: PropTypes.func,
		multiple: PropTypes.bool,/* 是否支持多选 */
		action: PropTypes.string,/* 上传的目标地址 */
		showUploadList: PropTypes.bool,/* 是否展示上传列表 */
		name: PropTypes.string,/* 上传的参数名字 */
		accept: PropTypes.string,/* 所接受的参数类型 */
	}
	constructor(props) {
		super(props);
		this.state = {
			btnLoading: false
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
	 * 上传之前的钩子函数
	 * @param file-文件类型
	 * @param fileList-文件列表
	 */
	beforeUpload = (file, fileList) => {
		if (this.props.beforeUpload) {
			this.props.beforeUpload();
		} else {
			this.setState({
				btnLoading: true
			})
		}
	}
	/**
	 * 上传
	 * @param info-上传信息具体参见ant-design
	 */
	handleUploadChange = (info) => {
		if (this.props.onChange) {
			this.props.onChange(info);
		} else {
			if (info.file.status !== 'uploading') {
				console.group('info');
				console.log(info.file, 'infoFile');
				console.log(info.fileList, 'infoFileList');
				console.groupEnd();
			}
			if (info.file.status === 'done') {
				message.success(`${info.file.name} file uploaded successfully`);
			} else if (info.file.status === 'error') {
				message.error(`${info.file.name} file upload failed.`);
			}
			this.setState({
				btnLoading: false,
			})
		}
	}
	render() {
		const { btnLoading } = this.state;
		const { title, action, accept, showUploadList, multiple } = this.props;
		const props = {
			name,
			action,
			headers: {
				authorization: 'authorization-text',
			},
			accept,
			multiple,
			showUploadList,
			beforeUpload: this.beforeUpload,
			onChange: this.handleUploadChange,
		};
		return (
			<Upload {...props}>
				<Button icon="upload" loading={btnLoading}>{title}</Button>
			</Upload>
		)
	}
}

