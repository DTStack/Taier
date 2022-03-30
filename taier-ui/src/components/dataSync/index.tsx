import { useEffect, useMemo, useState } from 'react';
import molecule from '@dtinsight/molecule';
import { Scrollable } from '@dtinsight/molecule/esm/components';
import { connect } from '@dtinsight/molecule/esm/react';
import { API } from '@/api/dataSource';
import { message, Spin, Steps } from 'antd';
import { cloneDeep } from 'lodash';
import { checkExist, getTenantId } from '@/utils';
import type {
	IDataSourceUsedInSyncProps,
	ISyncDataProps,
	IChannelFormProps,
	IDataColumnsProps,
	ISourceFormField,
	ITargetFormField,
} from '@/interface';
import { DATA_SYNC_MODE, SUPPROT_SUB_LIBRARY_DB_ARRAY } from '@/constant';
import { Utils } from '@dtinsight/dt-utils/lib';
import type { IKeyMapProps } from './keymap';
import Keymap, { OPERATOR_TYPE } from './keymap';
import Target from './target';
import Channel, { UnlimitedSpeed } from './channel';
import Source from './source';
import Preview from './preview';
import { getStepStatus, saveTask } from './help';
import './index.scss';

const { Step } = Steps;

function DataSync({ current }: molecule.model.IEditor) {
	const [currentStep, setCurrentStep] = useState(0);
	const [currentData, setCurrentData] = useState<ISyncDataProps | null>(null);
	const [loading, setLoading] = useState(false);
	const [dataSourceList, setDataSourceList] = useState<IDataSourceUsedInSyncProps[]>([]);

	const handleSourceChanged = (values: Partial<ISourceFormField>) => {
		setCurrentData((d) => {
			const currentDataSourceId = d?.sourceMap?.sourceId || values.sourceId;
			const target = dataSourceList.find((l) => l.dataInfoId === currentDataSourceId);

			const isSupportSub = SUPPROT_SUB_LIBRARY_DB_ARRAY.includes(target?.dataTypeCode || -1);
			// Only the dataSource which has sub library like mySQL need this
			const sourceList = isSupportSub
				? [
						{
							key: 'main',
							tables: values.table || d?.sourceMap?.table,
							type: target!.dataTypeCode,
							name: target!.dataName,
							sourceId: values.sourceId || d?.sourceMap?.sourceId,
						},
				  ]
				: [];

			if (!d) {
				if (!target) return null;
				return {
					taskId: current!.tab!.data!.id,
					sourceMap: {
						name: target.dataName,
						sourceList,
						type: target.dataTypeCode,
						...values,
					},
				};
			}

			// increment updates
			const nextData = d;

			nextData.sourceMap = {
				...nextData.sourceMap,
				...values,
				sourceList,
				type: target?.dataTypeCode || nextData.sourceMap?.type,
			};

			return nextData;
		});
	};

	const handleTargetChanged = (values: Partial<ITargetFormField>) => {
		const nextValue = values;
		const SHOULD_TRIM_FIELD: (keyof ITargetFormField)[] = [
			'partition',
			'path',
			'fileName',
			'fileName',
		];
		SHOULD_TRIM_FIELD.forEach((field) => {
			if (nextValue.hasOwnProperty(field)) {
				nextValue[field] = Utils.trimAll(nextValue[field] as string) as any;
			}
		});

		const target = dataSourceList.find((l) => l.dataInfoId === values.sourceId);

		// increment updates
		setCurrentData((d) => {
			const nextData = { ...d! };
			nextData.targetMap = {
				name: target?.dataName || nextData.targetMap?.name,
				type: target?.dataTypeCode || nextData.targetMap?.type,
				...nextData.targetMap,
				...values,
			};
			return nextData;
		});
	};

	const handleLinesChange = (lines: IKeyMapProps) => {
		setCurrentData((d) => {
			const nextData = { ...d! };
			nextData.sourceMap!.column = lines.source;
			nextData.targetMap!.column = lines.target;
			return nextData;
		});
	};

	const handleColChanged = (
		col: IDataColumnsProps | IDataColumnsProps[],
		operation: Valueof<typeof OPERATOR_TYPE>,
		flag: 'source' | 'target',
	) => {
		const cols = Array.isArray(col) ? col : [col];
		switch (operation) {
			case OPERATOR_TYPE.ADD: {
				cols.forEach((c) => {
					handleAddCol(c, flag);
				});
				break;
			}
			case OPERATOR_TYPE.REMOVE: {
				cols.forEach((c) => {
					handleRemoveCol(c, flag);
				});
				break;
			}
			case OPERATOR_TYPE.EDIT: {
				cols.forEach((c) => {
					handleEditCol(c, flag);
				});
				break;
			}
			case OPERATOR_TYPE.REPLACE: {
				const field = flag === 'source' ? 'sourceMap' : 'targetMap';
				// replace mode is to replace the whole column field
				setCurrentData((d) => {
					const nextData = { ...d! };
					if (nextData[field]) {
						nextData[field]!.column = cols;
					}
					return nextData;
				});
				break;
			}
			default:
				break;
		}
	};

	// 编辑列
	const handleEditCol = (col: IDataColumnsProps, flag: 'source' | 'target') => {
		const field = flag === 'source' ? 'sourceMap' : 'targetMap';
		setCurrentData((d) => {
			const nextData = { ...d! };
			const column = nextData[field]?.column || [];
			if (column.includes(col)) {
				const idx = column.indexOf(col);
				// 这里只做赋值，不做深拷贝
				// 因为字段映射的数组里的值和 column 字段的值是同一个引用，直接改这个值就可以做到都改了。如果做深拷贝则需要改两次值
				Object.assign(column[idx], col);
				return nextData;
			}
			return d;
		});
	};

	// 添加列
	const handleAddCol = (col: IDataColumnsProps, flag: 'source' | 'target') => {
		const field = flag === 'source' ? 'sourceMap' : 'targetMap';
		setCurrentData((d) => {
			const nextData = { ...d! };
			if (nextData[field]?.column) {
				const { column = [] } = nextData[field]!;
				if (checkExist(col.index) && column.some((o) => o.index === col.index)) {
					message.error(`添加失败：索引值不能重复`);
					return d;
				}
				if (checkExist(col.key) && column.some((o) => o.key === col.key)) {
					message.error(`添加失败：字段名不能重复`);
					return d;
				}
				column.push(col);
			} else {
				nextData[field]!.column = [col];
			}

			return cloneDeep(nextData);
		});
	};

	// 移除列
	const handleRemoveCol = (col: IDataColumnsProps, flag: 'source' | 'target') => {
		const field = flag === 'source' ? 'sourceMap' : 'targetMap';
		setCurrentData((d) => {
			const nextData = { ...d! };
			const columns = nextData[field]?.column;
			if (!columns || !columns.includes(col)) {
				return d;
			}
			const idx = columns.indexOf(col);
			columns.splice(idx, 1);
			return nextData;
		});
	};

	const handleSettingChanged = (values: IChannelFormProps) => {
		const nextSettings = { ...values };
		const isUnlimited = nextSettings.speed === UnlimitedSpeed;
		if (isUnlimited) {
			nextSettings.speed = '-1';
		}
		if (nextSettings.isRestore === false) {
			nextSettings.restoreColumnName = undefined;
		}
		// 增量模式下，开启断点续传字段默认与增量字段保存一致
		if (isIncrementMode && nextSettings.isRestore === true) {
			nextSettings.restoreColumnName = currentData!.sourceMap!.increColumn;
		}
		setCurrentData((d) => {
			const nextData = { ...d! };
			nextData.settingMap = nextSettings;
			return nextData;
		});
	};

	const handleChannelSubmit = (next: boolean, values?: IChannelFormProps) => {
		if (!next) {
			setCurrentStep((s) => s - 1);
			return;
		}
		if (values) {
			// 存在值的话，则保存当前值
			handleSettingChanged(values);
		}
		setCurrentStep((s) => s + 1);
	};

	const handleSaveTab = () => {
		saveTask();
	};

	// 获取当前任务的数据
	const getJobData = () => {
		const taskId = current?.tab?.data.id;
		if (typeof taskId === 'undefined') return;
		const [isLoaded, step] = getStepStatus(current?.tab?.data);
		setCurrentStep(step);
		// 未加载过则加载数据
		if (!isLoaded) {
			if (step === 0) {
				// the task opened first time or never saved before
				setCurrentData({ taskId });
			}
		} else {
			const { id, sourceMap, targetMap, settingMap } = current?.tab?.data || {};
			setCurrentData({
				sourceMap,
				targetMap,
				settingMap,
				taskId: id,
			});
		}
	};

	const getDataSourceList = () => {
		setLoading(true);
		API.queryByTenantId({ tenantId: getTenantId() })
			.then((res) => {
				if (res.code === 1) {
					setDataSourceList(res.data || []);
				}
			})
			.finally(() => {
				setLoading(false);
			});
	};

	useEffect(() => {
		getJobData();
		getDataSourceList();
	}, [current]);

	useEffect(() => {
		if (currentData && current?.tab) {
			const { sourceMap, targetMap, settingMap } = currentData;
			molecule.editor.updateTab({
				...current.tab,
				data: { ...current.tab.data, sourceMap, targetMap, settingMap },
			});
		}
	}, [currentData]);

	// 是否是增量模式
	const isIncrementMode = useMemo(() => {
		if (current?.tab?.data.syncModel !== undefined) {
			return current?.tab?.data.syncModel === DATA_SYNC_MODE.INCREMENT;
		}
		return false;
	}, [current]);

	const steps = [
		{
			key: 'source',
			title: '数据来源',
			content: (
				<Source
					isIncrementMode={isIncrementMode}
					sourceMap={currentData?.sourceMap}
					dataSourceList={dataSourceList}
					onFormValuesChanged={handleSourceChanged}
					onNext={() => setCurrentStep((s) => s + 1)}
				/>
			),
		},
		{
			key: 'target',
			title: '选择目标',
			content: (
				<Target
					isIncrementMode={isIncrementMode}
					sourceMap={currentData?.sourceMap}
					targetMap={currentData?.targetMap}
					dataSourceList={dataSourceList}
					onFormValuesChanged={handleTargetChanged}
					onNext={(next) => setCurrentStep((s) => (next ? s + 1 : s - 1))}
				/>
			),
		},
		{
			key: 'keymap',
			title: '字段映射',
			content: currentData?.sourceMap && currentData.targetMap && (
				<Keymap
					sourceMap={currentData.sourceMap}
					targetMap={currentData.targetMap}
					onColsChanged={handleColChanged}
					onLinesChanged={handleLinesChange}
					onNext={(next) => setCurrentStep((s) => (next ? s + 1 : s - 1))}
				/>
			),
		},
		{
			key: 'setting',
			title: '通道控制',
			content: currentData?.sourceMap && currentData.targetMap && (
				<Channel
					isIncrementMode={isIncrementMode}
					sourceMap={currentData.sourceMap}
					targetMap={currentData.targetMap}
					setting={currentData.settingMap}
					onNext={handleChannelSubmit}
					onFormValuesChanged={handleSettingChanged}
				/>
			),
		},
		{
			key: 'preview',
			title: '预览保存',
			content: currentData && (
				<Preview
					data={currentData}
					dataSourceList={dataSourceList}
					onStepTo={(step) =>
						setCurrentStep((s) => (typeof step === 'number' ? step : s - 1))
					}
					onSave={handleSaveTab}
				/>
			),
		},
	];

	return (
		<Scrollable isShowShadow>
			<div className="dt-datasync">
				<Spin spinning={loading}>
					<Steps size="small" current={currentStep}>
						{steps.map((item) => (
							<Step key={item.title} title={item.title} />
						))}
					</Steps>
					<div className="dt-datasync-content">
						{currentData && steps[currentStep].content}
					</div>
				</Spin>
			</div>
		</Scrollable>
	);
}

export default connect(molecule.editor, DataSync);
