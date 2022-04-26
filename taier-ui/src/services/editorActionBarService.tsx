import { SyntaxIcon } from '@/components/icon';
import {
	TASK_SAVE_ID,
	TASK_RUN_ID,
	TASK_STOP_ID,
	TASK_SUBMIT_ID,
	TASK_OPS_ID,
	TASK_CONVERT_SCRIPT,
	TASK_SYNTAX_ID,
	TASK_TYPE_ENUM,
	CREATE_MODEL_TYPE,
	TASK_FORMAT_ID,
} from '@/constant';
import type { CatalogueDataProps, IOfflineTaskProps } from '@/interface';
import {
	UploadOutlined,
	LoginOutlined,
	SwapOutlined,
	FormatPainterOutlined,
} from '@ant-design/icons';
import molecule from '@dtinsight/molecule';
import type { IEditorActionsProps } from '@dtinsight/molecule/esm/model';
import { Component } from '@dtinsight/molecule/esm/react';
import { cloneDeep } from 'lodash';
import type { IExecuteService } from './executeService';
import ExecuteService from './executeService';
import { container, singleton } from 'tsyringe';

/**
 * 保存按钮 for toolbar
 */
const SAVE_TASK: IEditorActionsProps = {
	id: TASK_SAVE_ID,
	name: 'Save Task',
	title: '保存',
	icon: 'save',
	place: 'outer',
};

/**
 * 运行任务按钮 for toolbar
 */
const RUN_TASK: IEditorActionsProps = {
	id: TASK_RUN_ID,
	name: 'Run Task',
	title: '运行',
	icon: 'play',
	place: 'outer',
	disabled: false,
};

/**
 * 停止任务按钮 for toolbar
 * @default disabled 默认是 disabled
 */
const STOP_TASK: IEditorActionsProps = {
	id: TASK_STOP_ID,
	name: 'Stop Task',
	title: '停止运行',
	icon: 'debug-pause',
	disabled: true,
	place: 'outer',
};

/**
 * 提交至调度按钮
 */
const SUBMIT_TASK: IEditorActionsProps = {
	id: TASK_SUBMIT_ID,
	name: '提交至调度',
	title: '提交至调度',
	icon: <UploadOutlined />,
	place: 'outer',
};

/**
 * 任务运维按钮
 */
const OPERATOR_TASK: IEditorActionsProps = {
	id: TASK_OPS_ID,
	name: '运维',
	title: '运维',
	icon: <LoginOutlined />,
	place: 'outer',
};

/**
 * 转换为脚本按钮
 */
const CONVERT_TASK: IEditorActionsProps = {
	id: TASK_CONVERT_SCRIPT,
	name: '转换为脚本',
	title: '转换为脚本',
	icon: <SwapOutlined />,
	place: 'outer',
};

/**
 * 导入模板按钮
 */
// const IMPORT_TASK: IEditorActionsProps = {
// 	id: TASK_IMPORT_ID,
// 	name: '引入数据源',
// 	title: '引入数据源',
// 	icon: <ImportOutlined />,
// 	place: 'outer',
// };

/**
 * 语法检查按钮
 */
const GRAMMAR_TASK: IEditorActionsProps = {
	id: TASK_SYNTAX_ID,
	name: '语法检查',
	title: '语法检查',
	icon: <SyntaxIcon />,
	place: 'outer',
};

/**
 * 运行中按钮，通常和运行按钮是互斥存在，所以和运行按钮 id 保持一致
 */
const RUNNING_TASK: IEditorActionsProps = {
	id: TASK_RUN_ID,
	name: 'Running',
	title: '运行中',
	icon: 'loading~spin',
	disabled: true,
};

/**
 * 格式化
 */
const FORMAT_TASK: IEditorActionsProps = {
	id: TASK_FORMAT_ID,
	name: '格式化',
	title: '格式化',
	icon: <FormatPainterOutlined />,
	place: 'outer'
};

interface IEditorActionBarState {
	runningTab: Set<number>;
}

interface IEditorActionBarService {
	/**
	 * 根据不同的任务类型及是否是向导模式获取不同任务具有的 actionBar
	 * @param taskType 任务类型
	 * @param isGuide 是否是向导模式
	 */
	getActionBar: (taskType: TASK_TYPE_ENUM, isGuide?: boolean) => void;
	/**
	 * 针对不同的任务类型，渲染不同的 `toolbar`
	 * @notice 需要确保 `current` 是数据是正确的
	 */
	performSyncTaskActions: () => void;
}

@singleton()
export default class EditorActionBarService
	extends Component<IEditorActionBarState>
	implements IEditorActionBarService
{
	protected state: IEditorActionBarState;
	private executeService: IExecuteService;
	constructor() {
		super();
		this.state = {
			runningTab: new Set(),
		};

		this.executeService = container.resolve(ExecuteService);

		this.executeService.onStartRun((currentTabId) => {
			const { current } = molecule.editor.getState();
			this.setState({
				runningTab: this.state.runningTab.add(currentTabId),
			});
			if (current?.activeTab === currentTabId.toString()) {
				molecule.editor.updateActions([
					RUNNING_TASK,
					{
						id: TASK_STOP_ID,
						disabled: false,
					},
				]);
			}
		});

		this.executeService.onEndRun((currentTabId) => {
			const { current } = molecule.editor.getState();
			this.state.runningTab.delete(currentTabId);
			this.setState({
				runningTab: this.state.runningTab,
			});
			if (current?.activeTab === currentTabId.toString()) {
				molecule.editor.updateActions([RUN_TASK, STOP_TASK]);
			}
		});

		this.executeService.onStopTab((currentTabId) => {
			const { current } = molecule.editor.getState();
			this.state.runningTab.delete(currentTabId);
			this.setState({
				runningTab: this.state.runningTab,
			});
			if (current?.activeTab === currentTabId.toString()) {
				molecule.editor.updateActions([RUN_TASK, STOP_TASK]);
			}
		});
	}

	public getActionBar = (taskType: TASK_TYPE_ENUM, isGuide?: boolean) => {
		switch (taskType) {
			case TASK_TYPE_ENUM.SYNC: {
				if (isGuide) {
					return [
						CONVERT_TASK,
						SAVE_TASK,
						RUN_TASK,
						STOP_TASK,
						SUBMIT_TASK,
						OPERATOR_TASK,
					];
				}
				return [
					// IMPORT_TASK,
					SAVE_TASK,
					RUN_TASK,
					STOP_TASK,
					SUBMIT_TASK,
					OPERATOR_TASK,
				];
			}
			case TASK_TYPE_ENUM.SQL:
				if (isGuide) {
					return [
						CONVERT_TASK,
						FORMAT_TASK,
						GRAMMAR_TASK,
						SAVE_TASK,
						SUBMIT_TASK,
						OPERATOR_TASK,
					];
				}
				return [
					// IMPORT_TASK,
					SAVE_TASK,
					SUBMIT_TASK,
					OPERATOR_TASK,
				];
			case TASK_TYPE_ENUM.SPARK_SQL:
			case TASK_TYPE_ENUM.HIVE_SQL:
				return [SAVE_TASK, RUN_TASK, STOP_TASK, SUBMIT_TASK, OPERATOR_TASK];
			default:
				return [];
		}
	};

	public performSyncTaskActions = () => {
		const { current } = molecule.editor.getState();
		if (current?.tab?.data) {
			const currentTabData: CatalogueDataProps & IOfflineTaskProps = current?.tab?.data;
			const taskToolbar = cloneDeep(
				this.getActionBar(
					currentTabData.taskType,
					currentTabData.createModel === CREATE_MODEL_TYPE.GUIDE,
				),
			);

			molecule.editor.updateGroup(current.id, {
				actions: [...taskToolbar, ...molecule.editor.getDefaultActions()],
			});
			if (this.state.runningTab.has(currentTabData.id)) {
				molecule.editor.updateActions([
					RUNNING_TASK,
					{
						id: TASK_STOP_ID,
						disabled: false,
					},
				]);
			}
		} else if (current) {
			molecule.editor.updateGroup(current.id, {
				actions: [...molecule.editor.getDefaultActions()],
			});
		}
	};
}
