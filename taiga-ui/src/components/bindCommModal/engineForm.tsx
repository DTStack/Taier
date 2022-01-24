import { useEffect, useMemo, useState } from 'react';
import type { FormInstance } from 'antd';
import { Select, Radio, message, Form } from 'antd';
import { ENGINE_SOURCE_TYPE_ENUM } from '@/constant';
import { isOracleEngine, isSparkEngine } from '@/utils';
import { PROJECT_CREATE_MODEL } from '@/constant';
import api from '../../api';
import CatalogueSelect from '../folderPicker';
import LifeCycleSelect from '../lifeCycleSelect';
import PreviewMetaData from '../previewMetaData';

const { Option } = Select;
const RadioGroup = Radio.Group;
const FormItem = Form.Item;

interface IConfigItemProps {
	/**
	 * 表单实例由父组件传入
	 */
	form: FormInstance;
	engineType: ENGINE_SOURCE_TYPE_ENUM;
	formParentField?: string;
	/**
	 * 父组件控制 layout
	 */
	formItemLayout: any;
	/**
	 * 当引擎类型为 hadoop 的时候，需要指定 hadoop 的名称
	 */
	hadoopName?: string;
	checked?: boolean;
}

/**
 * 引擎配置表单域
 */
export default ({
	engineType,
	form,
	formParentField,
	formItemLayout,
	checked,
	hadoopName = 'Hive2.x',
}: IConfigItemProps) => {
	const [targetDb, setTargetDb] = useState<string[]>([]);
	const [visible, setVisible] = useState(false);

	const getRetainDBList = (type: ENGINE_SOURCE_TYPE_ENUM) => {
		api.getRetainDBList({ engineType: type }).then((res) => {
			if (res.code === 1) {
				const data = res.data || [];
				setTargetDb([...data]);
			}
		});
	};

	const getEngineRadios = (
		type: number,
		engineTypeText: string,
	): {
		label: string;
		value: PROJECT_CREATE_MODEL;
		disabled?: boolean;
	}[] => {
		return [
			{
				label: '创建',
				value: PROJECT_CREATE_MODEL.NORMAL,
				disabled: type === ENGINE_SOURCE_TYPE_ENUM.ORACLE,
			},
			{
				label: `对接已有${engineTypeText}`,
				value: PROJECT_CREATE_MODEL.IMPORT,
			},
		];
	};

	const changeType = (value: PROJECT_CREATE_MODEL, type: ENGINE_SOURCE_TYPE_ENUM) => {
		if (value === PROJECT_CREATE_MODEL.IMPORT) {
			getRetainDBList(type);
		}
	};

	const onPreviewMetaData = () => {
		const dbName = form.getFieldValue(`${parentField}.database`);
		if (dbName) {
			setVisible(true);
		} else {
			message.error('请先选择对接目标！');
		}
	};

	const renderDbOptions = (dataBase: string[]) => {
		// 多引擎则是 Map 对象，需要根据引擎类型单独获取，否则就直取
		const dbList = Array.isArray(dataBase) ? dataBase : dataBase[engineType] || [];

		return dbList.map((item) => {
			return (
				<Option key={item} value={item}>
					{item}
				</Option>
			);
		});
	};

	useEffect(() => {
		if (engineType === ENGINE_SOURCE_TYPE_ENUM.ORACLE) {
			getRetainDBList(engineType);
		}
	}, []);

	const parentField = formParentField ? `${formParentField}` : '';
	const createModelInitialValue = isOracleEngine(engineType)
		? PROJECT_CREATE_MODEL.IMPORT
		: PROJECT_CREATE_MODEL.NORMAL;
	const isHadoop = isSparkEngine(engineType);

	const createModelOptions = useMemo<
		{
			label: string;
			value: PROJECT_CREATE_MODEL;
			disabled?: boolean;
		}[]
	>(() => {
		switch (engineType) {
			case ENGINE_SOURCE_TYPE_ENUM.HADOOP: {
				return getEngineRadios(engineType, hadoopName);
			}
			case ENGINE_SOURCE_TYPE_ENUM.LIBRA: {
				return getEngineRadios(engineType, 'LibrA Schema');
			}
			case ENGINE_SOURCE_TYPE_ENUM.TI_DB: {
				return getEngineRadios(engineType, 'TiDB Schema');
			}
			case ENGINE_SOURCE_TYPE_ENUM.ORACLE: {
				return getEngineRadios(engineType, 'Oracle Schema');
			}
			case ENGINE_SOURCE_TYPE_ENUM.GREEN_PLUM: {
				return getEngineRadios(engineType, 'Greenplum Schema');
			}
			case ENGINE_SOURCE_TYPE_ENUM.ADB: {
				return getEngineRadios(engineType, 'AnalyticDB PostgreSQL Schema');
			}
			default:
				return [
					{
						label: '对接未知类型',
						value: PROJECT_CREATE_MODEL.IMPORT,
					},
				];
		}
	}, [engineType]);

	return (
		<>
			<FormItem
				label="初始化方式"
				{...formItemLayout}
				name={`${parentField}.createModel`}
				initialValue={createModelInitialValue}
			>
				<RadioGroup
					onChange={(e) => changeType(e.target.value, engineType)}
					options={createModelOptions}
				/>
			</FormItem>
			<FormItem
				noStyle
				shouldUpdate={(pre, cur) =>
					pre[`${parentField}.createModel`] !== cur[`${parentField}.createModel`]
				}
			>
				{({ getFieldValue }) =>
					getFieldValue(`${parentField}.createModel`) === PROJECT_CREATE_MODEL.IMPORT ||
					isOracleEngine(engineType) ? (
						<>
							<FormItem {...formItemLayout} label="对接目标" required={checked}>
								<FormItem
									noStyle
									name={`${parentField}.database`}
									rules={[
										{
											required: checked,
											message: '请选择对接目标',
										},
									]}
								>
									<Select style={{ width: '80%' }} placeholder="请选择对接目标">
										{renderDbOptions(targetDb)}
									</Select>
								</FormItem>
								<a
									onClick={() => onPreviewMetaData()}
									style={{ marginLeft: 10, fontSize: 12 }}
								>
									预览元数据
								</a>
							</FormItem>
							{isHadoop && (
								<>
									<FormItem
										label="表类目"
										{...formItemLayout}
										name={`${parentField}.catalogueId`}
										rules={[
											{
												required: checked,
												message: '请选择表类目',
											},
										]}
									>
										<CatalogueSelect
											showFile={false}
											dataType="task"
											style={{
												width: '100%',
											}}
											placeholder="请选择表类目"
										/>
									</FormItem>
									<FormItem
										label="生命周期"
										{...formItemLayout}
										name={`${parentField}.lifecycle`}
										rules={[
											{
												required: checked,
												message: '生命周期不可为空',
											},
										]}
									>
										<LifeCycleSelect width="40%" inputWidth="50%" />
									</FormItem>
								</>
							)}
						</>
					) : null
				}
			</FormItem>
			<PreviewMetaData
				visible={visible}
				dbName={form?.getFieldValue(`${parentField}.database`)}
				engineType={engineType}
				onCancel={() => {
					setVisible(false);
				}}
			/>
		</>
	);
};
