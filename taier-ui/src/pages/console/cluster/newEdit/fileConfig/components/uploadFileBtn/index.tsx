/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import type { FormInstance } from 'antd';
import { Form, Upload, Button, Tooltip } from 'antd';
import { PaperClipOutlined, DeleteOutlined, UploadOutlined } from '@ant-design/icons';
import { isMultiVersion } from '../../../help';

interface IProp {
	label: any;
	form: FormInstance;
	icons?: any;
	fileInfo: any;
	deleteIcon?: boolean;
	view?: boolean;
	rules?: any;
	notDesc?: boolean;
	deleteFile?: Function;
	uploadFile: Function;
}

const FormItem = Form.Item;

export default function UploadFile({
	label,
	form,
	icons,
	fileInfo,
	deleteIcon,
	view,
	rules,
	notDesc,
	deleteFile,
	uploadFile,
}: IProp) {
	const { typeCode, hadoopVersion, name } = fileInfo;

	let formField = typeCode;
	if (isMultiVersion(typeCode) && hadoopVersion) {
		formField = `${formField}.${hadoopVersion}`;
	}
	formField = `${formField}.${name}`;

	const fileName = form.getFieldValue(formField)?.name ?? fileInfo?.value;
	const uploadFileProps = {
		name: fileInfo.uploadProps.name,
		accept: fileInfo.uploadProps.accept,
		beforeUpload: (file: any) => {
			uploadFile(file, fileInfo.uploadProps.type, () => {
				form.setFieldsValue({
					[formField]: file,
				});
			});
			return false;
		},
		fileList: [],
	};

	return (
		<FormItem label={label ?? '参数上传'} colon={false}>
			<FormItem
				noStyle
				name={formField}
				initialValue={fileInfo?.value || ''}
				rules={rules ?? []}
				hidden
			>
				<div />
			</FormItem>
			<FormItem noStyle>
				{!view && (
					<div className="c-fileConfig__config">
						<Upload {...uploadFileProps}>
							<Button
								style={{ width: 172 }}
								icon={<UploadOutlined />}
								loading={fileInfo.loading}
							>
								点击上传
							</Button>
						</Upload>
						<span className="config-desc">{fileInfo.desc}</span>
					</div>
				)}
			</FormItem>
			<FormItem noStyle shouldUpdate={(pre, cur) => pre[formField] !== cur[formField]}>
				{({ getFieldValue }) => {
					const field = getFieldValue(formField);
					return (
						getFieldValue(formField) &&
						!notDesc && (
							<span className="config-file">
								<PaperClipOutlined />
								<Tooltip
									title={typeof field === 'string' ? field : field.name}
									placement="topLeft"
								>
									{typeof field === 'string' ? field : field.name}
								</Tooltip>
								{icons ?? icons}
								{!deleteIcon
									? !view && (
											<DeleteOutlined
												onClick={() => {
													form.setFieldsValue({
														[formField]: '',
													});
													deleteFile?.();
												}}
											/>
									  )
									: null}
							</span>
						)
					);
				}}
			</FormItem>
		</FormItem>
	);
}
