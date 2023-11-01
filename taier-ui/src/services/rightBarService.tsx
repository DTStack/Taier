import React, { useEffect } from 'react';
import molecule from '@dtinsight/molecule';
import { Component, connect } from '@dtinsight/molecule/esm/react';
import type { FormInstance } from 'antd';
import { Form } from 'antd';
import classNames from 'classnames';
import { singleton } from 'tsyringe';

import { RightBarKind } from '@/interface';
import EnvParams from '@/pages/rightBar/envParams';
import FlinkDimensionPanel from '@/pages/rightBar/flinkDimension';
import FlinkResultPanel from '@/pages/rightBar/flinkResult';
import FlinkSourcePanel from '@/pages/rightBar/flinkSource';
import SchedulingConfig from '@/pages/rightBar/schedulingConfig';
import TaskConfig from '@/pages/rightBar/taskConfig';
import TaskInfo from '@/pages/rightBar/taskInfo';
import TaskParams from '@/pages/rightBar/taskParams';
import { isTaskTab } from '@/utils/is';

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
        const [form] = Form.useForm();

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
    private withForm = (Children: JSX.Element) => <this.WithForm key={Children.key}>{Children}</this.WithForm>;

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
        const Container = connect(molecule.editor, ({ current }: molecule.model.IEditor) => {
            /**
             * 当前的 tab 是否不合法，如不合法则展示 Empty
             */
            const isInValidTab = !isTaskTab(current?.tab?.id);
            if (isInValidTab)
                return <div className={classNames('text-center', 'pt-10px')}>无法获取{this.getTextByKind(kind)}</div>;

            switch (kind) {
                case RightBarKind.TASK:
                    return <TaskInfo key={current?.activeTab} current={current} />;
                case RightBarKind.DEPENDENCY:
                    return <SchedulingConfig key={current?.activeTab} current={current} />;
                case RightBarKind.TASK_PARAMS:
                    return <TaskParams key={current?.activeTab} current={current} />;
                case RightBarKind.ENV_PARAMS:
                    return <EnvParams key={current?.activeTab} current={current} />;
                case RightBarKind.TASK_CONFIG:
                    return this.withForm(<TaskConfig key={`${current?.activeTab}_config`} current={current} />);
                case RightBarKind.FLINKSQL_SOURCE:
                    return this.withForm(<FlinkSourcePanel key={`${current?.activeTab}_source`} current={current} />);
                case RightBarKind.FLINKSQL_RESULT:
                    return this.withForm(<FlinkResultPanel key={`${current?.activeTab}_result`} current={current} />);
                case RightBarKind.FLINKSQL_DIMENSION:
                    return this.withForm(
                        <FlinkDimensionPanel key={`${current?.activeTab}_dimension`} current={current} />
                    );
                default:
                    return null;
            }
        });

        return <Container />;
    };
}
