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

import { useState } from 'react';
import type { RcFile } from 'antd/lib/upload';
import { Select, message, Cascader, notification, Tooltip, Form } from 'antd';
import { DownloadOutlined, EditOutlined, QuestionCircleOutlined } from '@ant-design/icons';
import req from '@/api/request';
import Api from '@/api';
import UploadFile from './components/uploadFileBtn';
import KerberosModal from './components/kerberosModal';
import DataCheckbox from './components/dataCheckbox';
import DefaultVersionCheckbox from './components/defaultVersionCheckbox';
import {
	isOtherVersion,
	isSameVersion,
	handleComponentConfig,
	needZipFile,
	getOptions,
	getInitialValue,
	isMultiVersion,
	isYarn,
	showDataCheckBox,
	isFLink,
} from '../help';
import {
	COMPONENT_TYPE_VALUE,
	FILE_TYPE,
	DEFAULT_COMP_VERSION,
	CONFIG_FILE_DESC,
	COMPONENT_CONFIG_NAME,
} from '@/constant';
import { convertToStr } from '@/utils';
import { cloneDeep } from 'lodash';
import type {
	IComponentProps,
	IClusterInfo,
	IVersionData,
	ISaveCompsData,
	ISaveComp,
	IHandleCompVersion,
} from '../interface';
import { useContextForm } from '../context';
import './index.scss';

interface IProps {
	comp: IComponentProps;
	view: boolean;
	saveCompsData: ISaveCompsData[];
	versionData: IVersionData;
	clusterInfo: IClusterInfo;
	isCheckBoxs?: boolean;
	isSchedulings?: boolean;
	disabledMeta?: boolean;
	isDefault?: boolean;
	handleCompVersion?: IHandleCompVersion;
	saveComp: ISaveComp;
}

interface IRefreshQueueData {
	componentTypeCode: COMPONENT_TYPE_VALUE;
	componentVersion: string;
	errorMsg: string | null;
	result: boolean;
}

const FormItem = Form.Item;
const { Option } = Select;

export default function FileConfig({
	comp,
	view,
	saveCompsData,
	versionData,
	clusterInfo,
	isCheckBoxs,
	isSchedulings,
	disabledMeta,
	isDefault,
	handleCompVersion,
	saveComp,
}: IProps) {
	const form = useContextForm();
	const [loading, setLoading] = useState({
		[FILE_TYPE.KERNEROS]: false,
		[FILE_TYPE.PARAMES]: false,
		[FILE_TYPE.CONFIGS]: false,
	});
	const [visible, setVisible] = useState(false);
	const [principals, setPrincipals] = useState<string[]>([]);

	/** hdfs 和 yarn 组件版本一致，version提取至上层 */
	const handleVersion = (version: any) => {
		const typeCode = comp?.componentTypeCode ?? '';
		handleCompVersion?.(typeCode, version);
	};

	const renderCompsVersion = () => {
		const typeCode: keyof typeof DEFAULT_COMP_VERSION = comp?.componentTypeCode ?? '';
		const version = isOtherVersion(typeCode)
			? versionData[COMPONENT_CONFIG_NAME[typeCode]]
			: versionData.hadoopVersion;
		let initialValue: string | (string | undefined)[] = isOtherVersion(typeCode)
			? DEFAULT_COMP_VERSION[typeCode]
			: [version[0]?.key, version[0]?.values?.[0]?.key];
		initialValue = comp?.versionName || initialValue;
		let versionValue = initialValue;
		if (isSameVersion(typeCode)) {
			versionValue = comp?.versionName || version[0]?.values?.[0]?.key || '';
			initialValue = comp?.versionName
				? getInitialValue(version, comp?.versionName)
				: initialValue;
		}

		return (
			<>
				<FormItem
					label={
						<span>
							组件版本
							{isSameVersion(typeCode) && (
								<Tooltip
									overlayClassName="big-tooltip"
									title="切换组件版本HDFS和YARN组件将同步切换至相同版本，Spark/Flink/DtScript的插件路径将同步自动变更"
								>
									<QuestionCircleOutlined style={{ marginLeft: 4 }} />
								</Tooltip>
							)}
						</span>
					}
					colon={false}
					name={`${typeCode}.hadoopVersionSelect`}
					initialValue={initialValue}
				>
					{isOtherVersion(typeCode) ? (
						<Select style={{ width: 172 }} disabled={view} onChange={handleVersion}>
							{version.map((ver) => {
								return (
									<Option value={ver.value} key={ver.key}>
										{ver.key}
									</Option>
								);
							})}
						</Select>
					) : (
						<Cascader
							options={getOptions(version)}
							disabled={view}
							expandTrigger="click"
							allowClear={false}
							displayRender={(label) => {
								return label[label.length - 1];
							}}
							onChange={handleVersion}
							style={{ width: '100%' }}
						/>
					)}
				</FormItem>
				<FormItem name={`${typeCode}.versionName`} initialValue={versionValue} noStyle>
					<span style={{ display: 'none' }} />
				</FormItem>
			</>
		);
	};

	const renderSchedulingVersion = () => {
		const typeCode = comp?.componentTypeCode ?? '';
		if (isSchedulings && typeCode === COMPONENT_TYPE_VALUE.HDFS) return null;
		return renderCompsVersion();
	};

	const getPrincipalsList = async (file: RcFile) => {
		const typeCode = comp?.componentTypeCode ?? '';
		const versionName = comp?.versionName ?? '';
		const res = await Api.parseKerberos({ fileName: file });
		if (res.code === 1) {
			const principal = {
				principal: res?.data[0] ?? '',
				principals: res.data,
			};
			const fieldValue = isMultiVersion(typeCode)
				? { [versionName]: principal }
				: { ...principal };
			form.setFieldsValue({ [typeCode]: fieldValue });
			setPrincipals(res.data ?? []);
		}
	};

	const refreshYarnQueue = () => {
		const { clusterName } = clusterInfo;
		Api.refreshQueue({ clusterName }).then(
			(res: { code: number; data: IRefreshQueueData[] }) => {
				if (res.code === 1) {
					const target = res.data.find(
						(v) => v.componentTypeCode === COMPONENT_TYPE_VALUE.YARN,
					);
					if (target?.result || res.data.length === 0) {
						message.success('刷新成功');
					} else {
						notification.error({
							message: '刷新失败',
							description: `${target?.errorMsg}`,
							style: { wordBreak: 'break-word' },
						});
					}
				}
			},
		);
	};

	// 下载配置文件
	const downloadFile = (type: number) => {
		const typeCode = comp?.componentTypeCode ?? '';
		let version = form.getFieldValue(`${typeCode}.versionName`) || '';
		if (isMultiVersion(typeCode)) version = comp?.versionName ?? '';

		const a = document.createElement('a');
		let param = comp?.id ? `?componentId=${comp.id}&` : '?';
		param += `type=${type}&componentType=${typeCode}&versionName=${version}&clusterId=${clusterInfo?.clusterId}`;

		a.href = `${req.DOWNLOAD_RESOURCE}${param}`;
		a.click();
		a.remove();
	};

	const validateFileType = (val: string) => {
		const result = /\.(zip)$/.test(val.toLocaleLowerCase());
		if (val && !result) {
			message.warning('配置文件只能是zip文件!');
		}
		return result;
	};

	const uploadFile = async (file: RcFile, loadingType: number, callBack: () => void) => {
		const typeCode = comp?.componentTypeCode ?? '';
		const versionName = isMultiVersion(typeCode) ? comp?.versionName : '';
		const deployType = comp?.deployType ?? '';
		setLoading((loadings) => ({
			...loadings,
			[loadingType]: true,
		}));
		let res: any;
		if (needZipFile(loadingType) && !validateFileType(file?.name)) {
			setLoading((loadings) => ({
				...loadings,
				[loadingType]: false,
			}));
			return;
		}
		if (loadingType === FILE_TYPE.KERNEROS) {
			const params = {
				kerberosFile: file,
				deployType,
				clusterId: clusterInfo?.clusterId ?? '',
				componentCode: typeCode,
				versionName,
			};
			res = await Api.uploadKerberos(params);
			getPrincipalsList(file);
		} else {
			res = await Api.uploadResource({
				fileName: file,
				componentType: typeCode,
			});
		}
		function setValue() {
			const componentConfig = handleComponentConfig(
				{
					componentConfig: res.data[0],
				},
				true,
			);
			const fieldValue = isMultiVersion(typeCode)
				? { [versionName]: { componentConfig } }
				: { componentConfig };
			const formField = convertToStr({ [typeCode]: fieldValue });
			form.setFieldsValue(formField);
		}
		if (res.code === 1) {
			switch (loadingType) {
				case FILE_TYPE.KERNEROS: {
					setKrbConfig(res.data);
					break;
				}
				case FILE_TYPE.PARAMES:
					setValue();
					break;
				case FILE_TYPE.CONFIGS: {
					// use setFields instead of setFieldsValue because there is a merge action in setFieldsValue
					form.setFields([
						{
							name: `${typeCode}.specialConfig`,
							value: cloneDeep(res.data[0]),
						},
					]);
					break;
				}
				default:
					break;
			}
			callBack?.();
			message.success('文件上传成功');
		}
		setLoading((loadings) => ({
			...loadings,
			[loadingType]: false,
		}));
	};

	const deleteKerFile = () => {
		if (!comp.id) return;
		Api.closeKerberos({
			componentId: comp.id,
		});
	};

	// Hadoop Kerberos认证文件
	const renderKerberosFile = () => {
		const typeCode = comp?.componentTypeCode ?? '';
		return (
			<UploadFile
				label="Hadoop Kerberos认证文件"
				fileInfo={{
					typeCode,
					name: 'kerberosFileName',
					value: comp.kerberosFileName,
					desc: '仅支持.zip格式',
					loading: loading[FILE_TYPE.KERNEROS],
					versionName: comp?.versionName ?? '',
					uploadProps: {
						name: 'kerberosFile',
						accept: '.zip',
						type: FILE_TYPE.KERNEROS,
					},
				}}
				view={view}
				uploadFile={uploadFile}
				icons={
					<>
						{!view && (
							<EditOutlined
								style={{ right: !comp?.id ? 20 : 40 }}
								onClick={() => setVisible(true)}
							/>
						)}
						{comp?.id && (
							<DownloadOutlined
								style={{ right: view ? 0 : 20 }}
								onClick={() => downloadFile(FILE_TYPE.KERNEROS)}
							/>
						)}
					</>
				}
				deleteFile={deleteKerFile}
			/>
		);
	};

	// 参数批量上传文件
	const renderParamsFile = () => {
		const typeCode = comp?.componentTypeCode ?? '';
		return (
			<UploadFile
				fileInfo={{
					typeCode,
					name: 'paramsFile',
					value: comp.paramsFile,
					desc: '仅支持json格式',
					loading: loading[FILE_TYPE.PARAMES],
					versionName: comp?.versionName ?? '',
					uploadProps: {
						name: 'paramsFile',
						accept: '.json',
						type: FILE_TYPE.PARAMES,
					},
				}}
				view={view}
				uploadFile={uploadFile}
				notDesc={true}
				label={
					<span>
						参数批量上传
						<span
							className="c-fileConfig__downloadTemp"
							onClick={() => downloadFile(FILE_TYPE.PARAMES)}
						>
							{comp?.id ? '下载参数' : '下载模板'}
						</span>
					</span>
				}
			/>
		);
	};

	// 配置文件
	const renderConfigsFile = () => {
		const typeCode: keyof typeof CONFIG_FILE_DESC = comp?.componentTypeCode ?? '';
		return (
			<UploadFile
				label={
					<span>
						配置文件
						{isYarn(typeCode) && (
							<a style={{ marginLeft: 66 }} onClick={refreshYarnQueue}>
								刷新队列
							</a>
						)}
					</span>
				}
				deleteIcon={true}
				fileInfo={{
					typeCode,
					name: 'uploadFileName',
					value: comp.uploadFileName,
					desc: CONFIG_FILE_DESC[typeCode],
					loading: loading[FILE_TYPE.CONFIGS],
					versionName: comp?.versionName ?? '',
					uploadProps: {
						name: 'uploadFileName',
						accept: '.zip',
						type: FILE_TYPE.CONFIGS,
					},
				}}
				view={view}
				uploadFile={uploadFile}
				rules={[{ required: true, message: `配置文件为空` }]}
				icons={
					comp?.id ? (
						<DownloadOutlined
							style={{ right: 0 }}
							onClick={() => downloadFile(FILE_TYPE.CONFIGS)}
						/>
					) : undefined
				}
			/>
		);
	};

	const renderStorageComponents = () => {
		const typeCode = comp?.componentTypeCode ?? '';
		const versionName = comp?.versionName ?? '';
		let formField: number | string = typeCode;
		if (isMultiVersion(typeCode)) formField = `${formField}.${versionName}`;
		formField = `${formField}.storeType`;

		if (saveCompsData.length === 0) {
			return null;
		}
		let storeTypeFlag = false;
		// eslint-disable-next-line
		for (const item in saveCompsData) {
			if (saveCompsData[item].key === COMPONENT_TYPE_VALUE.HDFS) {
				storeTypeFlag = true;
				break;
			}
		}
		const storeType =
			comp?.storeType ||
			(storeTypeFlag ? COMPONENT_TYPE_VALUE.HDFS : saveCompsData?.[0]?.key);

		return (
			<FormItem
				label="存储组件"
				colon={false}
				key={formField}
				name={formField}
				initialValue={storeType}
			>
				<Select style={{ width: 172 }} disabled={view}>
					{saveCompsData.map((ver: any) => {
						return (
							<Option value={ver.key} key={ver.key}>
								{ver.value}
							</Option>
						);
					})}
				</Select>
			</FormItem>
		);
	};

	const renderPrincipal = () => {
		let principalsList = principals;
		const typeCode = comp?.componentTypeCode ?? '';
		const versionName = comp?.versionName ?? '';

		let formField: number | string = typeCode;
		if (isMultiVersion(typeCode)) formField = `${formField}.${versionName}`;

		const kerberosFile =
			form.getFieldValue(`${formField}.kerberosFileName`) ?? comp?.kerberosFileName;

		if (!principals.length && !Array.isArray(comp?.principals) && comp?.principals) {
			principalsList = comp?.principals.split(',');
		}

		if (principalsList?.length === 0 || !kerberosFile) return;

		return (
			<>
				<FormItem
					label="principal"
					colon={false}
					key={`${formField}.principal`}
					name={`${formField}.principal`}
					initialValue={comp?.principal ?? principals[0] ?? ''}
				>
					<Select style={{ width: 172 }} disabled={view}>
						{principalsList.map((ver, key) => {
							return (
								<Option value={ver} key={key}>
									{ver}
								</Option>
							);
						})}
					</Select>
				</FormItem>
				<FormItem
					noStyle
					name={`${formField}.principals`}
					initialValue={comp?.principals ?? ''}
				>
					<></>
				</FormItem>
			</>
		);
	};

	const renderMeta = () => {
		const typeCode = comp?.componentTypeCode ?? '';
		if (!showDataCheckBox(typeCode)) return null;
		return (
			<DataCheckbox
				comp={comp}
				view={view}
				disabledMeta={disabledMeta}
				isCheckBoxs={isCheckBoxs}
			/>
		);
	};

	const renderDefaultVersion = () => {
		const typeCode = comp?.componentTypeCode ?? '';
		if (!isFLink(typeCode)) return null;
		return <DefaultVersionCheckbox comp={comp} view={view} isDefault={isDefault} />;
	};

	const setKrbConfig = (krbconfig: string) => {
		const typeCode = comp?.componentTypeCode ?? '';
		const versionName = comp?.versionName ?? '';
		saveComp({
			mergeKrb5Content: krbconfig,
			componentTypeCode: typeCode,
			versionName,
		});
	};

	const hanleVisible = (krbconfig: string) => {
		setVisible(false);
		setKrbConfig(krbconfig);
	};

	const renderFileConfig = () => {
		const typeCode = comp?.componentTypeCode ?? '';
		switch (typeCode) {
			case COMPONENT_TYPE_VALUE.YARN:
			case COMPONENT_TYPE_VALUE.HDFS: {
				return (
					<>
						{renderSchedulingVersion()}
						{renderConfigsFile()}
						{renderKerberosFile()}
						{renderPrincipal()}
					</>
				);
			}
			case COMPONENT_TYPE_VALUE.SFTP: {
				return renderParamsFile();
			}
			case COMPONENT_TYPE_VALUE.HIVE_SERVER:
			case COMPONENT_TYPE_VALUE.SPARK_THRIFT: {
				return (
					<>
						{renderMeta()}
						{renderCompsVersion()}
						{renderKerberosFile()}
						{renderPrincipal()}
						{renderParamsFile()}
						{renderStorageComponents()}
					</>
				);
			}
			case COMPONENT_TYPE_VALUE.SPARK:
			case COMPONENT_TYPE_VALUE.FLINK: {
				return (
					<>
						{renderDefaultVersion()}
						{renderKerberosFile()}
						{renderPrincipal()}
						{renderParamsFile()}
						{renderStorageComponents()}
					</>
				);
			}
			default: {
				return null;
			}
		}
	};

	return (
		<div className="c-fileConfig__container">
			{renderFileConfig()}
			<KerberosModal
				key={`${visible}`}
				visible={visible}
				krbconfig={comp?.mergeKrb5Content || ''}
				onCancel={hanleVisible}
			/>
		</div>
	);
}
