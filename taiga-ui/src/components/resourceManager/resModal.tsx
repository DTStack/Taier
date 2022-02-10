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
import { forwardRef, useMemo, useRef, useState } from 'react';
import type { FormInstance } from 'antd';
import { Form, Modal, Button, Input, Select, Spin, Upload } from 'antd';
import { ExclamationCircleOutlined } from '@ant-design/icons';
import ajax from '../../api';
import FolderPicker from '../../components/folderPicker';
import { CATELOGUE_TYPE, formItemLayout, RESOURCE_TYPE } from '@/constant';
import { useImperativeHandle } from 'react';
import { getResourceName } from '@/utils/enums';
import type { IFolderTreeNodeProps } from '@dtinsight/molecule/esm/model';
import type { CatalogueDataProps, IResourceProps } from '@/interface';
import type { RcFile } from 'antd/lib/upload';

export function getContainer(id: string) {
	const container = document.createElement('div');
	document.getElementById(id)?.appendChild(container);
	return container;
}

const FormItem = Form.Item;
const { Option } = Select;
const resourceType = getResourceName();

interface IResFormProps {
	defaultData: any;
	treeData: IFolderTreeNodeProps | undefined;
	/**
	 * 是否是编辑，编辑需要默认值
	 */
	isEditExist: boolean;
	/**
	 * 是否从目录树中新建
	 */
	isCreateFromMenu: boolean;
	isCreateNormal: boolean;
	/**
	 * 替换资源
	 */
	isCoverUpload: boolean;
}

interface IFormFieldProps {
	/**
	 * Only when editing
	 */
	id?: number;
	originFileName?: string;
	/**
	 * Only when adding
	 */
	resourceName: string;
	resourceType: RESOURCE_TYPE;
	file: RcFile;
	nodePid: number;
	resourceDesc?: string;
}

const ResForm = forwardRef(
	(
		{
			defaultData,
			treeData,
			isEditExist,
			isCreateFromMenu,
			isCreateNormal,
			isCoverUpload,
		}: IResFormProps,
		ref,
	) => {
		const [form] = Form.useForm<IFormFieldProps>();
		const [loading, setLoading] = useState(false);

		useImperativeHandle(ref, () => ({
			...form,
		}));

		const handleSelectTreeChange = (value: number) => {
			form.setFieldsValue({ nodePid: value });
		};

		const validateFileType = (_: any, value: RcFile) => {
			if (!value) {
				return Promise.resolve();
			}
			const { resourceType: fileType } = form.getFieldsValue();
			const fileSuffix = resourceType[fileType]!;
			if (fileType === RESOURCE_TYPE.OTHER) {
				return Promise.resolve();
			}
			const suffix = value.name.split('.').slice(1).pop();
			if (fileSuffix.toLocaleLowerCase() !== suffix) {
				return Promise.reject(new Error(`资源文件只能是${fileSuffix}文件!`));
			}
			return Promise.resolve();
		};

		/**
		 * @description 检查所选是否为文件夹
		 */
		const checkNotDir = (_: any, value: number) => {
			let nodeType: string = 'folder';

			const loop = (arr: CatalogueDataProps[]) => {
				arr.forEach((node) => {
					if (node.id.toString() === value.toString()) {
						nodeType = node.type;
					} else {
						loop(node.children || []);
					}
				});
			};

			if (treeData) {
				loop([treeData.data]);
			}

			if (nodeType === 'folder') {
				return Promise.resolve(new Error('请选择具体文件, 而非文件夹'));
			}
			return Promise.resolve();
		};

		const handleCoverTargetChange = (value: any) => {
			setLoading(true);
			getResDetail(value);
		};

		const getResDetail = (resId: number) => {
			if (!resId) return;
			form.setFieldsValue({ id: resId });

			ajax.getOfflineRes({
				resourceId: resId,
			})
				.then((res) => {
					if (res.code === 1) {
						const data = res.data as IResourceProps;
						form.setFieldsValue({
							originFileName: data.originFileName || '',
							resourceType: data.resourceType,
						});
						form.validateFields(['originFileName']);
					}
				})
				.finally(() => {
					setLoading(false);
				});
		};

		const getTreeValue = () => {
			if (isCreateNormal) return treeData?.id || undefined;
			return isCreateFromMenu ? defaultData.parentId : undefined;
		};

		const getCoverTreeValue = () => {
			if (isCreateNormal) return treeData?.id || undefined;
			if (isCreateFromMenu) return defaultData.parentId;
			return isEditExist ? defaultData.id : undefined;
		};

		const renderFormItem = () => {
			if (!isCoverUpload) {
				return [
					<FormItem
						{...formItemLayout}
						label="资源名称"
						key="resourceName"
						name="resourceName"
						rules={[
							{
								required: true,
								message: '资源名称不可为空!',
							},
							{
								pattern: /^[A-Za-z0-9_-]+$/,
								message: '资源名称只能由字母、数字、下划线组成!',
							},
							{
								max: 20,
								message: '资源名称不得超过20个字符!',
							},
						]}
					>
						<Input autoComplete="off" placeholder="请输入资源名称" />
					</FormItem>,
					<FormItem
						{...formItemLayout}
						label="资源类型"
						key="resourceType"
						name="resourceType"
						rules={[
							{
								required: true,
								message: '资源类型不可为空!',
							},
						]}
						initialValue={defaultData?.resourceType || RESOURCE_TYPE.JAR}
					>
						<Select
							onChange={() => {
								form.resetFields(['file']);
							}}
						>
							<Option value={RESOURCE_TYPE.JAR} key={RESOURCE_TYPE.JAR}>
								{resourceType[RESOURCE_TYPE.JAR]}
							</Option>
							<Option value={RESOURCE_TYPE.PY} key={RESOURCE_TYPE.PY}>
								{resourceType[RESOURCE_TYPE.PY]}
							</Option>
							<Option value={RESOURCE_TYPE.EGG} key={RESOURCE_TYPE.EGG}>
								{resourceType[RESOURCE_TYPE.EGG]}
							</Option>
							<Option value={RESOURCE_TYPE.ZIP} key={RESOURCE_TYPE.ZIP}>
								{resourceType[RESOURCE_TYPE.ZIP]}
							</Option>
							<Option value={RESOURCE_TYPE.OTHER} key={RESOURCE_TYPE.OTHER}>
								其它
							</Option>
						</Select>
					</FormItem>,
					<FormItem
						{...formItemLayout}
						key="file"
						label="上传"
						required
						shouldUpdate={(pre, cur) =>
							pre.resourceType !== cur.resourceType || pre.file !== cur.file
						}
					>
						{({ getFieldValue }) => (
							<>
								<FormItem
									noStyle
									name="file"
									rules={[
										{
											required: true,
											message: '请选择上传文件',
										},
										{
											validator: validateFileType,
										},
									]}
									valuePropName="file"
									getValueFromEvent={(e) => e.file}
								>
									<Upload
										accept={
											getFieldValue('resourceType') !== RESOURCE_TYPE.OTHER
												? `.${
														resourceType[
															getFieldValue(
																'resourceType',
															) as RESOURCE_TYPE
														]
												  }`
												: undefined
										}
										beforeUpload={() => false}
										showUploadList={false}
									>
										<Button>选择文件</Button>
									</Upload>
								</FormItem>
								<span className="ml-5px">{getFieldValue('file')?.name}</span>
							</>
						)}
					</FormItem>,
					<FormItem {...formItemLayout} required label="选择存储位置" key="nodePid">
						<FormItem
							noStyle
							name="nodePid"
							rules={[
								{
									required: true,
									message: '存储位置必选！',
								},
							]}
							initialValue={getTreeValue()}
						>
							<Input type="hidden" />
						</FormItem>
						<FolderPicker
							dataType={CATELOGUE_TYPE.RESOURCE}
							showFile={false}
							onChange={handleSelectTreeChange}
							defaultValue={getTreeValue()}
						/>
					</FormItem>,
					<FormItem
						{...formItemLayout}
						label="描述"
						key="resourceDesc"
						name="resourceDesc"
						rules={[
							{
								max: 200,
								message: '描述请控制在200个字符以内！',
							},
						]}
						initialValue=""
					>
						<Input.TextArea rows={4} />
					</FormItem>,
					<FormItem
						key="computeType"
						style={{ display: 'none' }}
						name="computeType"
						initialValue={1}
					>
						<Input type="hidden" />
					</FormItem>,
				];
			}

			return [
				<FormItem {...formItemLayout} label="选择目标替换资源" key="id" required>
					<FormItem
						noStyle
						name="id"
						rules={[
							{
								required: true,
								message: '替换资源为必选！',
							},
							{
								validator: checkNotDir,
							},
						]}
						initialValue={getCoverTreeValue()}
					>
						<Input type="hidden" />
					</FormItem>
					<FolderPicker
						dataType={CATELOGUE_TYPE.RESOURCE}
						showFile
						onChange={handleCoverTargetChange}
						defaultValue={getCoverTreeValue()}
					/>
				</FormItem>,
				<FormItem
					{...formItemLayout}
					label="文件名"
					key="originFileName"
					name="originFileName"
					initialValue={defaultData.originFileName}
				>
					<Input disabled readOnly />
				</FormItem>,
				<FormItem
					{...formItemLayout}
					label="资源类型"
					key="resourceType"
					name="resourceType"
					rules={[
						{
							required: true,
							message: '资源类型不可为空!',
						},
					]}
					initialValue={defaultData?.resourceType || RESOURCE_TYPE.JAR}
				>
					<Select
						onChange={() => {
							form.resetFields(['file']);
						}}
					>
						<Option value={RESOURCE_TYPE.JAR} key={RESOURCE_TYPE.JAR}>
							{resourceType[RESOURCE_TYPE.JAR]}
						</Option>
						<Option value={RESOURCE_TYPE.PY} key={RESOURCE_TYPE.PY}>
							{resourceType[RESOURCE_TYPE.PY]}
						</Option>
						<Option value={RESOURCE_TYPE.EGG} key={RESOURCE_TYPE.EGG}>
							{resourceType[RESOURCE_TYPE.EGG]}
						</Option>
						<Option value={RESOURCE_TYPE.ZIP} key={RESOURCE_TYPE.ZIP}>
							{resourceType[RESOURCE_TYPE.ZIP]}
						</Option>
						<Option value={RESOURCE_TYPE.OTHER} key={RESOURCE_TYPE.OTHER}>
							其它
						</Option>
					</Select>
				</FormItem>,
				<FormItem
					{...formItemLayout}
					key="file"
					label="上传"
					required
					shouldUpdate={(pre, cur) =>
						pre.resourceType !== cur.resourceType || pre.file !== cur.file
					}
				>
					{({ getFieldValue }) => (
						<>
							<FormItem
								noStyle
								name="file"
								rules={[
									{
										required: true,
										message: '请选择上传文件',
									},
									{
										validator: validateFileType,
									},
								]}
								valuePropName="file"
								getValueFromEvent={(e) => e.file}
							>
								<Upload
									accept={
										getFieldValue('resourceType') !== RESOURCE_TYPE.OTHER
											? `.${
													resourceType[
														getFieldValue(
															'resourceType',
														) as RESOURCE_TYPE
													]
											  }`
											: undefined
									}
									beforeUpload={() => false}
									showUploadList={false}
								>
									<Button>选择文件</Button>
								</Upload>
							</FormItem>
							<span className="ml-5px">{getFieldValue('file')?.name}</span>
						</>
					)}
				</FormItem>,
				<FormItem
					{...formItemLayout}
					label="描述"
					key="resourceDesc"
					name="resourceDesc"
					rules={[
						{
							max: 200,
							message: '描述请控制在200个字符以内！',
						},
					]}
					initialValue={defaultData.resourceDesc}
				>
					<Input.TextArea rows={4} />
				</FormItem>,
			];
		};

		return (
			<Spin spinning={loading}>
				<Form form={form}>{renderFormItem()}</Form>
			</Spin>
		);
	},
);

interface IResModalProps {
	isModalShow: boolean;
	resourceTreeData: IFolderTreeNodeProps | undefined;
	defaultData: any;
	/**
	 * 是否是覆盖数据源模式
	 */
	isCoverUpload: boolean;
	toggleUploadModal: () => void;
	replaceResource: (values: IFormFieldProps) => Promise<boolean>;
	addResource: (values: IFormFieldProps) => Promise<boolean>;
}

let dtcount = 0;

export default function ResModal(props: IResModalProps) {
	const {
		isModalShow,
		resourceTreeData,
		defaultData,
		isCoverUpload,
		toggleUploadModal,
		replaceResource,
		addResource,
	} = props;
	const [loading, setLoading] = useState(false);
	const form = useRef<FormInstance>(null);

	const handleSubmit = () => {
		form.current?.validateFields().then((values) => {
			const params = { ...values };
			params.resourceDesc = values.resourceDesc || '';
			setLoading(true);
			if (isCoverUpload) {
				replaceResource(values)
					.then((res) => {
						if (res) {
							closeModal();
							form.current?.resetFields();
						}
					})
					.finally(() => {
						setLoading(false);
					});
			} else {
				addResource(values)
					.then((res) => {
						if (res) {
							closeModal();
							form.current?.resetFields();
						}
					})
					.finally(() => {
						setLoading(false);
					});
			}
		});
	};

	const handleCancel = () => {
		closeModal();
	};

	const closeModal = () => {
		dtcount += 1;
		toggleUploadModal?.();
	};

	const isCreateNormal = typeof defaultData === 'undefined';
	const isCreateFromMenu = !isCreateNormal && typeof defaultData.id === 'undefined';
	const isEditExist = !isCreateNormal && !isCreateFromMenu;

	const title = useMemo(() => {
		if (isCoverUpload) return '替换资源';
		return isEditExist ? '编辑资源' : '上传资源';
	}, [isCoverUpload, isEditExist]);

	return (
		<div id="JS_upload_modal">
			<Modal
				title={title}
				visible={isModalShow}
				footer={[
					<Button key="back" size="large" onClick={handleCancel}>
						取消
					</Button>,
					<Button
						key="submit"
						loading={loading}
						type="primary"
						size="large"
						onClick={handleSubmit}
					>
						确认
					</Button>,
				]}
				key={dtcount}
				onCancel={handleCancel}
				getContainer={() => getContainer('JS_upload_modal')}
			>
				{isCoverUpload && (
					<div className="task_offline_message">
						<ExclamationCircleOutlined className="mr-5px" />
						替换资源时，如果资源的新文件与现有文件名称保持一致，那么替换后关联函数对应任务可立即生效，否则关联函数对应任务需重新提交才可生效。
					</div>
				)}
				<ResForm
					ref={form}
					treeData={resourceTreeData}
					defaultData={defaultData || {}}
					isCreateNormal={isCreateNormal}
					isCreateFromMenu={isCreateFromMenu}
					isCoverUpload={isCoverUpload}
					isEditExist={isEditExist}
				/>
			</Modal>
		</div>
	);
}
