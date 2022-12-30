import React from 'react';
import FlinkDimensionPanel from '@/pages/rightBar/flinkDimension';
import FlinkResultPanel from '@/pages/rightBar/flinkResult';
import FlinkSourcePanel from '@/pages/rightBar/flinkSource';
import EnvParams from '@/pages/rightBar/envParams';
import SchedulingConfig from '@/pages/rightBar/schedulingConfig';
import TaskInfo from '@/pages/rightBar/taskInfo';
import TaskParams from '@/pages/rightBar/taskParams';
import { isTaskTab } from '@/utils/is';
import molecule from '@dtinsight/molecule';
import { Component } from '@dtinsight/molecule/esm/react';
import type { FormInstance } from 'antd';
import { Form } from 'antd';
import classNames from 'classnames';
import { useEffect } from 'react';
import { singleton } from 'tsyringe';
import { RightBarKind } from '@/interface';
import TaskConfig from '@/pages/rightBar/taskConfig';

interface IRightBarService {
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
}

/**
 * 侧边栏组件的 props 类型
 */
export interface IRightBarComponentProps {
	current: molecule.model.IEditor['current'];
}

export const FormContext = React.createContext<{ form?: FormInstance }>({});

@singleton()
/**
 * 负责调度侧边栏
 */
export default class RightBarService extends Component<void> implements IRightBarService {
	protected state: void | undefined;
	protected form: FormInstance | null = null;

	constructor() {
		super();
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

	public getForm = () => {
		return this.form;
	};

	/**
	 * 根据右侧栏的 key 值返回字符串作为标题
	 */
	public getTextByKind = (kind: string) => {
		switch (kind) {
			case RightBarKind.TASK:
				return '任务属性';
			case RightBarKind.DEPENDENCY:
				return '调度依赖';
			case RightBarKind.TASK_PARAMS:
				return '任务参数';
			case RightBarKind.TASK_CONFIG:
				return '任务配置';
			case RightBarKind.ENV_PARAMS:
				return '环境参数';
			case RightBarKind.FLINKSQL_SOURCE:
				return '源表';
			case RightBarKind.FLINKSQL_RESULT:
				return '结果表';
			case RightBarKind.FLINKSQL_DIMENSION:
				return '维表';
			case RightBarKind.QUEUE:
				return '队列管理';
			default:
				return '未知';
		}
	};

	/**
	 * 根据右侧栏的 key 值返回对应的 JSX 组件
	 */
	public createContent = (kind: string) => {
		const { current } = molecule.editor.getState();
		/**
		 * 当前的 tab 是否不合法，如不合法则展示 Empty
		 */
		const isInValidTab = !isTaskTab(current?.tab?.id);

		if (isInValidTab) {
			return (
				<div className={classNames('text-center', 'pt-10px')}>
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
			case RightBarKind.TASK_CONFIG:
				return this.withForm(<TaskConfig key="config" current={current} />);
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
