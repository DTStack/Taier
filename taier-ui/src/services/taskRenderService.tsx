import 'reflect-metadata';
import { singleton } from 'tsyringe';
import { DATA_SYNC_MODE } from '@/constant';
import { Modal } from 'antd';
import api from '@/api';
import { TASK_TYPE_ENUM } from '@/constant';
import type { FormInstance } from 'antd';
import {
	DataCollectionIcon,
	FlinkIcon,
	FlinkSQLIcon,
	HiveSQLIcon,
	OceanBaseIcon,
	SparkSQLIcon,
} from '@/components/icon';
import scaffolds from '@/components/scaffolds/create';
import editorActionsScaffolds from '@/components/scaffolds/editorActions';
import { RightBarKind } from '@/interface';
import { mappingTaskTypeToLanguage } from '@/utils/enums';
import { prettierJSONstring } from '@/utils';
import notification from '@/components/notification';
import type { ISupportJobTypes } from '@/context';
import type molecule from '@dtinsight/molecule';
import { breadcrumbService } from '.';
import type { IOfflineTaskProps } from '@/interface';

interface ICreateFormField {
	taskType: TASK_TYPE_ENUM;
	/**
	 * 渲染方式条件，仅当 renderKind 为 customize 时生效
	 */
	renderCondition?: {
		key: keyof IOfflineTaskProps;
		value: any;
	};
	/**
	 * 当前任务在编辑器中的渲染方式，分为 editor 和 customize
	 */
	renderKind: string;
	/**
	 * 定义当前任务在新建的时候支持的字段
	 */
	formField: (keyof typeof scaffolds)[];
}

interface IRightBarField {
	taskType: TASK_TYPE_ENUM;
	/**
	 * 渲染方式条件，其中 barItemCondition.barItem 为当条件成立时渲染的侧边栏
	 */
	barItemCondition?: { key: keyof IOfflineTaskProps; value: any; barItem: RightBarKind[] };
	/**
	 * 默认渲染方式，若存在 barItemCondition 则该值为条件判断为假值时的侧边栏
	 */
	barItem: RightBarKind[];
}

interface IEditorActionField {
	taskType: TASK_TYPE_ENUM;
	/**
	 * 渲染方式条件
	 */
	actionsCondition?: {
		key: keyof IOfflineTaskProps;
		value: any;
		actions: (keyof typeof editorActionsScaffolds)[];
	};
	actions: (keyof typeof editorActionsScaffolds)[];
}

@singleton()
class TaskRenderService {
	/**
	 * 不同任务在新建任务的表单值域
	 */
	public createFormField: ICreateFormField[] = [];
	/**
	 * 不同任务在侧边栏的定义
	 */
	public rightBarField: IRightBarField[] = [];
	/**
	 * 不同任务在编辑器 actions 的定义
	 */
	public editorActionField: IEditorActionField[] = [];
	/**
	 * 当前支持的全部任务列表
	 */
	public supportTaskList: ISupportJobTypes[] = [];

	constructor() {
		fetch('./layout/create.json', { method: 'GET' })
			.then<ICreateFormField[]>((res) => res.json())
			.then((res) => {
				this.createFormField = res;
			});

		fetch('./layout/rightBar.json', { method: 'GET' })
			.then<IRightBarField[]>((res) => res.json())
			.then((res) => {
				this.rightBarField = res;
			});

		fetch('./layout/editorActions.json', { method: 'GET' })
			.then<IEditorActionField[]>((res) => res.json())
			.then((res) => {
				this.editorActionField = res;
			});
	}

	/**
	 * 根据任务类型获取创建任务所需要的 UI 界面
	 */
	public renderCreateForm = (
		key: TASK_TYPE_ENUM,
		record?: Record<string, any>,
		form?: Omit<FormInstance, 'scrollToField' | '__INTERNAL__' | 'getFieldInstance'>,
	) => {
		const field = this.createFormField.find((i) => i.taskType === key);
		if (!field) {
			return null;
		}

		return (
			<>
				{field.formField.map((f) => {
					const Compoennt = scaffolds[f];
					return (
						<Compoennt
							key={f}
							disabled={!!record}
							validator={async (_, value) => {
								if (record && value === DATA_SYNC_MODE.INCREMENT) {
									// 当编辑同步任务，且改变同步模式为增量模式时，需要检测任务是否满足增量同步的条件
									const res = await api.checkSyncMode({ id: record.id });
									if (res.code === 1) {
										return res.data ? Promise.resolve() : Promise.reject();
									}

									return Promise.reject(
										new Error('当前同步任务不支持增量模式！'),
									);
								}
								return Promise.resolve();
							}}
							onChange={() => {
								Modal.confirm({
									title: '正在切换引擎版本',
									content: (
										<>
											<span style={{ color: 'red' }}>
												切换引擎版本后将重置环境参数
											</span>
											，请确认是否继续？
										</>
									),
									onCancel: () => {
										form?.resetFields(['componentVersion']);
									},
								});
							}}
						/>
					);
				})}
			</>
		);
	};

	/**
	 * 根据任务类型获取不同的目录树中的图标
	 */
	public renderTaskIcon = (key: TASK_TYPE_ENUM) => {
		switch (key) {
			case TASK_TYPE_ENUM.SPARK_SQL:
				return <SparkSQLIcon style={{ color: '#519aba' }} />;
			case TASK_TYPE_ENUM.SYNC:
				return 'sync';
			case TASK_TYPE_ENUM.HIVE_SQL:
				return <HiveSQLIcon style={{ color: '#4291f0' }} />;
			case TASK_TYPE_ENUM.SQL:
				return <FlinkSQLIcon style={{ color: '#5655d8' }} />;
			case TASK_TYPE_ENUM.DATA_ACQUISITION:
				return <DataCollectionIcon style={{ color: '#3F87FF' }} />;
			case TASK_TYPE_ENUM.FLINK:
				return <FlinkIcon />;
			case TASK_TYPE_ENUM.OCEANBASE:
				return <OceanBaseIcon />;
			default:
				return 'file';
		}
	};

	/**
	 * 根据任务类型获取不同的 tabData，用以渲染不同的编辑器内容
	 */
	public renderTabOnEditor = async (
		key: TASK_TYPE_ENUM,
		record: IOfflineTaskProps,
		props: Record<string, any> = {},
	): Promise<molecule.model.IEditorTab> => {
		const fields = this.createFormField.find((i) => i.taskType === key);
		const renderKind = fields?.renderKind || 'editor';

		const tabData: molecule.model.IEditorTab = {
			id: record.id.toString(),
			name: record.name,
			data: (() => {
				// 针对不同任务，data 中的值不一样
				switch (key) {
					case TASK_TYPE_ENUM.FLINK: {
						return {
							...record,
							nodePid: `${record.nodePid}-folder`,
						};
					}
					case TASK_TYPE_ENUM.DATA_ACQUISITION:
					case TASK_TYPE_ENUM.SYNC:
						return {
							...record,
							value: prettierJSONstring(record.sqlText),
						};
					default:
						return {
							...record,
							value: record.sqlText,
						};
				}
			})(),
			icon: this.renderTaskIcon(record.taskType),
			breadcrumb: breadcrumbService.getBreadcrumb(record.id),
		};

		// 判断自定义渲染组件的渲染条件是否生效
		const isWork = fields?.renderCondition
			? record[fields.renderCondition.key] === fields.renderCondition.value
			: true;

		// 如果是 editor 渲染，则需要声明 editor 的语言
		if (renderKind === 'editor' || !isWork) {
			tabData.data!.language = mappingTaskTypeToLanguage(record.taskType);
		} else {
			try {
				// 自定义渲染需要声明 renderPane 组件
				const Component = (await import(`@/pages/editor/${renderKind}`)).default;
				tabData.renderPane = () => <Component key={tabData.id} {...props} />;
			} catch (err) {
				notification.error({
					key: 'ModuleNotFound',
					message: `${renderKind} 无法加载，请确认 pages/editor 目录下是否存在该模块`,
				});
			}
		}

		return tabData;
	};

	/**
	 * 根据任务类型定义侧边栏
	 */
	public renderRightBar = (key: TASK_TYPE_ENUM, record: IOfflineTaskProps): RightBarKind[] => {
		const rightBarField = this.rightBarField.find((i) => i.taskType === key);

		if (rightBarField) {
			const isConditionTrue = rightBarField.barItemCondition
				? record[rightBarField.barItemCondition.key] ===
				  rightBarField.barItemCondition.value
				: false;

			if (isConditionTrue) {
				return rightBarField.barItemCondition?.barItem || [];
			}

			return rightBarField.barItem || [];
		}

		return [RightBarKind.TASK];
	};

	/**
	 * 根据任务类型渲染编辑器 actions
	 */
	public renderEditorActions = (key: TASK_TYPE_ENUM, record: IOfflineTaskProps) => {
		const actionsField = this.editorActionField.find((i) => i.taskType === key);
		if (actionsField) {
			const isConditionTrue = actionsField.actionsCondition
				? record[actionsField.actionsCondition.key] === actionsField.actionsCondition.value
				: false;

			if (isConditionTrue) {
				return (
					actionsField.actionsCondition?.actions.map(
						(action) => editorActionsScaffolds[action],
					) || []
				);
			}

			return actionsField.actions.map((action) => editorActionsScaffolds[action]) || [];
		}

		return [];
	};
}

export default new TaskRenderService();
