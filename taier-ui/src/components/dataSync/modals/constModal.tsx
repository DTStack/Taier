/* eslint-disable no-template-curly-in-string */
import { useState } from 'react';
import HelpDoc, { relativeStyle } from '@/components/helpDoc';
import { Modal, Input, Select, message } from 'antd';
import { Utils } from '@dtinsight/dt-utils/lib';
import type { IDataColumnsProps } from '../interface';
import './constModal.scss';

const { Option } = Select;

interface IConstModalProps {
	visible?: boolean;
	onOk?: (values: IDataColumnsProps) => void;
	onCancel?: () => void;
}

const systemVariable = [
	'${bdp.system.bizdate}',
	'${bdp.system.bizdate2}',
	'${bdp.system.cyctime}',
	'${bdp.system.premonth}',
	'${bdp.system.currmonth}',
	'${bdp.system.runtime}',
];

export default function ConstModal({ visible, onOk, onCancel }: IConstModalProps) {
	const [constName, setConstName] = useState('');
	const [constValue, setConstValue] = useState('');
	const [type, setType] = useState('STRING');
	const [constFormat, setFormat] = useState('');

	const submit = () => {
		const value = Utils.trim(constValue);
		const name = Utils.trim(constName);
		const format = Utils.trim(constFormat);

		if (name === '') {
			message.error('常量名称不可为空！');
			return;
		}

		if (systemVariable.includes(value) && type === 'TIMESTAMP') {
			message.error('常量的值中存在参数时类型不可选timestamp！');
			return;
		}

		if (value === '') {
			message.error('常量值不可为空！');
			return;
		}
		if (onOk) {
			onOk({
				type,
				key: name,
				value,
				format,
			});
			close();
		}
	};

	const close = () => {
		setConstValue('');
		setConstName('');
		setFormat('');
		onCancel?.();
	};

	return (
		<Modal title="添加常量" onOk={submit} onCancel={close} visible={visible}>
			<div className="flex batch-dataSync_form">
				<span>名称 :</span>
				<Input
					value={constName}
					onChange={(e) => setConstName(e.target.value)}
					placeholder="请输入常量名称"
				/>
			</div>
			<div className="flex batch-dataSync_form">
				<span>值 : </span>
				<Input
					style={{ width: '440px', marginLeft: 11 }}
					value={constValue}
					onChange={(e) => setConstValue(e.target.value)}
					placeholder="请输入常量值"
				/>
			</div>
			<div className="flex batch-dataSync_form">
				<span>类型 :</span>
				<Select
					style={{ width: '440px' }}
					placeholder="请选择类型"
					value={type}
					onChange={(t) => setType(t)}
				>
					<Option value="STRING">STRING</Option>
					<Option value="DATE">DATE</Option>
					<Option value="TIMESTAMP">TIMESTAMP</Option>
				</Select>
			</div>
			<div className="flex batch-dataSync_form">
				<span>格式 :</span>
				<Input
					value={constFormat}
					onChange={(e) => setFormat(e.target.value)}
					placeholder="格式化, 例如：yyyy-MM-dd"
				/>
			</div>
			<p style={{ marginTop: '10px' }}>
				1.输入的常量值将会被英文单引号包括，如'abc'、'123'等
			</p>
			<p>
				2.可以配合调度参数使用，如 ${`{bdp.system.bizdate}`}等{' '}
				<HelpDoc style={relativeStyle} doc="customSystemParams" />
			</p>
			<p>3.如果您输入的值无法解析，则类型显示为'未识别'</p>
		</Modal>
	);
}
