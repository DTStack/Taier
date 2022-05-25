import React from 'react';
import FlinkDimensionPanel from '@/pages/rightBar/flinkDimension';
import FlinkResultPanel from '@/pages/rightBar/flinkResult';
import FlinkSourcePanel from '@/pages/rightBar/flinkSource';
import EnvParams from '@/pages/rightBar/envParams';
import SchedulingConfig from '@/pages/rightBar/schedulingConfig';
import { CREATE_MODEL_TYPE, TASK_TYPE_ENUM } from '@/constant';
import TaskInfo from '@/pages/rightBar/taskInfo';
import TaskParams from '@/pages/rightBar/envParams';
import { isTaskTab } from '@/utils/is';
import molecule from '@dtinsight/molecule';
import { Component } from '@dtinsight/molecule/esm/react';
import type { FormInstance } from 'antd';
import { Form } from 'antd';
import classNames from 'classnames';
import { useEffect } from 'react';
import { singleton } from 'tsyringe';

interface IRightBarService {
	/**
	 * 根据任务类型获取相应的右侧栏
	 * @param isGuide 是否是向导模式
	 */
	getRightBarByType: (type?: TASK_TYPE_ENUM, isGuide?: boolean) => RightBarKind[];
	/**
	 * 根据枚举值获取对应的文本内容
	 */
	getTextByKind: (kind: RightBarKind) => string;
	/**
	 * 根据枚举值获取对应的组件
	 */
	createContent: (kind: RightBarKind) => React.ReactNode;
	/**
	 * 获取 form 组件对象
	 */
	getForm: () => FormInstance | null;
	setCurrent: (nextCurrent: RightBarKind | null) => void;
}

/**
 * 侧边栏组件的 props 类型
 */
export interface IRightBarComponentProps {
	current: molecule.model.IEditor['current'];
}

export interface IRightbarState {
	/**
	 * rightBar 的宽度
	 */
	width: number;
	/**
	 * 当前侧边栏选中项
	 */
	current: RightBarKind | null;
}

export enum RightBarKind {
	/**
	 * 任务属性
	 */
	TASK = 'task',
	/**
	 * 调度依赖
	 */
	DEPENDENCY = 'dependency',
	/**
	 * 任务参数
	 */
	TASK_PARAMS = 'task_params',
	/**
	 * 环境参数
	 */
	ENV_PARAMS = 'env_params',
	/**
	 * 源表
	 */
	FLINKSQL_SOURCE = 'flinksql_source',
	/**
	 * 结果表
	 */
	FLINKSQL_RESULT = 'flinksql_result',
	/**
	 * 维表
	 */
	FLINKSQL_DIMENSION = 'flinksql_dimension',
}

export const FormContext = React.createContext<{ form?: FormInstance }>({});

@singleton()
/**
 * 负责调度侧边栏
 */
export default class RightBarService extends Component<IRightbarState> implements IRightBarService {
	/**
	 * 侧边栏展开宽度
	 */
	static ACTIVE_WIDTH = 480;
	/**
	 * 侧边栏收起宽度
	 */
	static UNACTIVE_WIDTH = 30;

	protected state: IRightbarState;
	protected form: FormInstance | null = null;

	constructor() {
		super();
		this.state = {
			width: RightBarService.UNACTIVE_WIDTH,
			current: null,
		};
	}

	private WithForm = ({ children }: { children: React.ReactNode }) => {
		// eslint-disable-next-line react-hooks/rules-of-hooks
		const [form] = Form.useForm();

		// eslint-disable-next-line react-hooks/rules-of-hooks
		useEffect(() => {
			this.form = form;
			return () => {
				this.form = null;
			};
		}, []);
		return <FormContext.Provider value={{ form }}>{children}</FormContext.Provider>;
	};

	/**
	 * Form HOC
	 * @requires Children 需要赋 key 值
	 */
	private withForm = (Children: JSX.Element) => (
		<this.WithForm key={Children.key}>{Children}</this.WithForm>
	);

	private setWidth = (collapse: boolean) => {
		return collapse ? RightBarService.ACTIVE_WIDTH : RightBarService.UNACTIVE_WIDTH;
	};

	public setCurrent = (nextCurrent: RightBarKind | null) => {
		this.setState({
			current: nextCurrent,
			width: this.setWidth(!!nextCurrent),
		});
	};

	public getForm = () => {
		return this.form;
	};

	public getTextByKind = (kind: RightBarKind) => {
		switch (kind) {
			case RightBarKind.TASK:
				return '任务属性';
			case RightBarKind.DEPENDENCY:
				return '调度依赖';
			case RightBarKind.TASK_PARAMS:
				return '任务参数';
			case RightBarKind.ENV_PARAMS:
				return '环境参数';
			case RightBarKind.FLINKSQL_SOURCE:
				return '源表';
			case RightBarKind.FLINKSQL_RESULT:
				return '结果表';
			case RightBarKind.FLINKSQL_DIMENSION:
				return '维表';
			default:
				return '未知';
		}
	};

	public getRightBarByType = (type?: TASK_TYPE_ENUM, isGuide: boolean = false) => {
		const defaultBar = [RightBarKind.TASK, RightBarKind.ENV_PARAMS];
		switch (type) {
			case TASK_TYPE_ENUM.SPARK_SQL:
			case TASK_TYPE_ENUM.HIVE_SQL:
				return [
					RightBarKind.TASK,
					RightBarKind.DEPENDENCY,
					RightBarKind.TASK_PARAMS,
					RightBarKind.ENV_PARAMS,
				];
			case TASK_TYPE_ENUM.SYNC:
				return [
					RightBarKind.TASK,
					RightBarKind.DEPENDENCY,
					RightBarKind.TASK_PARAMS,
					RightBarKind.ENV_PARAMS,
				];
			case TASK_TYPE_ENUM.DATA_ACQUISITION:
				return defaultBar;
			case TASK_TYPE_ENUM.SQL:
				if (isGuide) {
					return [
						RightBarKind.TASK,
						RightBarKind.FLINKSQL_SOURCE,
						RightBarKind.FLINKSQL_RESULT,
						RightBarKind.FLINKSQL_DIMENSION,
						RightBarKind.ENV_PARAMS,
					];
				}
				return defaultBar;
			default:
				return [RightBarKind.TASK];
		}
	};

	public createContent = (kind: RightBarKind) => {
		const { current } = molecule.editor.getState();
		/**
		 * 当前的 tab 是否不合法，如不合法则展示 Empty
		 */
		const isInValidTab = !isTaskTab(current?.tab?.id);

		// 判断当前的 tab 是否支持该 kind
		const supportBars = this.getRightBarByType(
			current?.tab?.data?.taskType,
			current?.tab?.data?.createModel === CREATE_MODEL_TYPE.GUIDE,
		);

		const isSupportThisKind = supportBars.includes(kind);
		if (!isSupportThisKind) {
			// 不支持则重置 current
			this.setCurrent(null);
		}

		if (isInValidTab || !isSupportThisKind) {
			return (
				<div className={classNames('text-center', 'mt-10px')}>
					无法获取{this.getTextByKind(kind)}
				</div>
			);
		}

		switch (kind) {
			case RightBarKind.TASK:
				return <TaskInfo current={current} />;
			case RightBarKind.DEPENDENCY:
				return <SchedulingConfig current={current} />;
			case RightBarKind.TASK_PARAMS:
				return <TaskParams current={current} />;
			case RightBarKind.ENV_PARAMS:
				return <EnvParams current={current} />;
			case RightBarKind.FLINKSQL_SOURCE:
				return this.withForm(<FlinkSourcePanel key="source" current={current} />);
			case RightBarKind.FLINKSQL_RESULT:
				return this.withForm(<FlinkResultPanel key="result" current={current} />);
			case RightBarKind.FLINKSQL_DIMENSION:
				return this.withForm(<FlinkDimensionPanel key="dimension" current={current} />);
			default:
				return null;
		}
	};
}
