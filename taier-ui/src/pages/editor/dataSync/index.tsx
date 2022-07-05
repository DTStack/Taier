import { useEffect, useMemo, useState } from 'react';
import molecule from '@dtinsight/molecule';
import { Scrollable } from '@dtinsight/molecule/esm/components';
import { connect } from '@dtinsight/molecule/esm/react';
import API from '@/api';
import { message, Spin, Steps } from 'antd';
import { checkExist, getTenantId } from '@/utils';
import saveTask from '@/utils/saveTask';
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
import { getStepStatus } from './help';
import { EditorEvent } from '@dtinsight/molecule/esm/model';
import { throttle } from 'lodash';
import './index.scss';

const { Step } = Steps;

const throttleUpdateTab = throttle((nextTab: molecule.model.IEditorTab, isEmit: boolean) => {
	const { current } = molecule.editor.getState();
	if (current?.tab) {
		molecule.editor.updateTab(nextTab);

		// emit OnUpdateTab so taskParams could be updated
		if (isEmit) {
			molecule.editor.emit(EditorEvent.OnUpdateTab, nextTab);
		}
	}
}, 800);

function DataSync({ current }: molecule.model.IEditor) {
	const [currentStep, setCurrentStep] = useState(0);
	const [currentData, rawSetCurrentData] = useState<ISyncDataProps | null>(null);
	/**
	 * keymap 中用户自定义的字段
	 */
	const [userColumns, setUserColumns] = useState<IKeyMapProps>({ source: [], target: [] });
	const [loading, setLoading] = useState(false);
	const [dataSourceList, setDataSourceList] = useState<IDataSourceUsedInSyncProps[]>([]);

	/**
	 * 包装一层 setCurrentData，以便于区分是否触发 onUpdateTab 事件
	 */
	const setCurrentData = (
		nextValues: ISyncDataProps | ((prev: ISyncDataProps | null) => ISyncDataProps),
		isEmit: boolean = true,
	) => {
		if (typeof nextValues === 'function') {
			rawSetCurrentData((value) => {
				const nextValue = nextValues(value);
				setTimeout(() => {
					if (current?.tab) {
						const { sourceMap, targetMap, settingMap } = nextValue;
						const nextTab = {
							...current.tab,
							data: { ...current.tab.data, sourceMap, targetMap, settingMap },
						};
						throttleUpdateTab(nextTab, isEmit);
					}
				}, 0);

				return nextValue;
			});
		} else {
			rawSetCurrentData(nextValues);
			// sync to tab data
			if (current?.tab) {
				const { sourceMap, targetMap, settingMap } = nextValues;
				const nextTab = {
					...current.tab,
					data: { ...current.tab.data, sourceMap, targetMap, settingMap },
				};
				throttleUpdateTab(nextTab, isEmit);
			}
		}
	};

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

			// increment updates
			const nextData = d!;

			nextData.sourceMap = {
				...nextData.sourceMap,
				...values,
				sourceList,
				type: target?.dataTypeCode || nextData.sourceMap?.type,
			};

			// 以下属性的变更会引起 columns 的变更
			const PROPERTY_OF_EFFECTS = ['table', 'schema', 'sourceId'];

			if (PROPERTY_OF_EFFECTS.some((property) => Object.keys(values).includes(property))) {
				nextData.sourceMap.column = [];
				if (nextData.targetMap) {
					nextData.targetMap.column = [];
				}
			}

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
				...nextData.targetMap,
				...values,
				name: target?.dataName || nextData.targetMap?.name,
				type: target?.dataTypeCode || nextData.targetMap?.type,
			};

			// 以下属性的变更会引起 columns 的变更
			const PROPERTY_OF_EFFECTS = ['table', 'schema', 'sourceId'];

			if (PROPERTY_OF_EFFECTS.some((property) => Object.keys(values).includes(property))) {
				nextData.sourceMap!.column = [];
				nextData.targetMap.column = [];
			}
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
				// replace mode is to replace the whole column field
				setUserColumns((uCols) => {
					const nextCols = { ...uCols };
					nextCols[flag] = cols;
					return nextCols;
				});
				break;
			}
			default:
				break;
		}
	};

	// 编辑列
	const handleEditCol = (col: IDataColumnsProps, flag: 'source' | 'target') => {
		// const field = flag === 'source' ? 'sourceMap' : 'targetMap';
		setUserColumns((cols) => {
			const nextCols = { ...cols };
			const column = nextCols[flag];
			if (!column.includes(col)) return cols;
			const idx = column.indexOf(col);
			// 这里只做赋值，不做深拷贝
			// 因为字段映射的数组里的值和 column 字段的值是同一个引用，直接改这个值就可以做到都改了。如果做深拷贝则需要改两次值
			Object.assign(column[idx], col);
			return nextCols;
		});
	};

	// 添加列
	const handleAddCol = (col: IDataColumnsProps, flag: 'source' | 'target') => {
		setUserColumns((cols) => {
			const nextCols = { ...cols };
			const columns = nextCols[flag];
			if (checkExist(col.index) && columns.some((o) => o.index === col.index)) {
				message.error(`添加失败：索引值不能重复`);
				return cols;
			}
			if (checkExist(col.key) && columns.some((o) => o.key === col.key)) {
				message.error(`添加失败：字段名不能重复`);
				return cols;
			}
			columns.push(col);
			return nextCols;
		});
	};

	// 移除列
	const handleRemoveCol = (col: IDataColumnsProps, flag: 'source' | 'target') => {
		setUserColumns((cols) => {
			const nextCols = { ...cols };
			const columns = nextCols[flag];
			if (!columns || !columns.includes(col)) return cols;
			const idx = columns.indexOf(col);
			columns.splice(idx, 1);
			return nextCols;
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
		saveTask()
			.then((res) => res?.data?.id)
			.then((id) => {
				if (id !== undefined) {
					molecule.editor.updateTab({
						id: current!.tab!.id,
						status: undefined,
					});
				}
			});
	};

	// 获取当前任务的数据
	const getJobData = () => {
		const taskId = current?.tab?.data.id;
		if (typeof taskId === 'undefined') return;
		const [, step] = getStepStatus(current?.tab?.data);
		setCurrentStep(step);
		const { id, sourceMap, targetMap, settingMap } = current?.tab?.data || {};
		setCurrentData(
			{
				sourceMap,
				targetMap,
				settingMap,
				taskId: id,
			},
			false,
		);
	};

	const getDataSourceList = () => {
		setLoading(true);
		return API.queryByTenantId({ tenantId: getTenantId() })
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
		// Should first to get the datasource list
		// as there are lots of requests should get sourceType via sourceId and to check whether could request
		getDataSourceList().then(() => {
			getJobData();
		});
	}, [current?.activeTab]);

	// 是否是增量模式
	const isIncrementMode = useMemo(() => {
		if (current?.tab?.data?.sourceMap?.syncModel !== undefined) {
			return current?.tab?.data?.sourceMap?.syncModel === DATA_SYNC_MODE.INCREMENT;
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
					userColumns={userColumns}
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
					isIncrementMode={isIncrementMode}
					data={currentData}
					dataSourceList={dataSourceList}
					userColumns={userColumns}
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
