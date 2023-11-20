import { FormatPainterOutlined,LoginOutlined, SwapOutlined, UploadOutlined } from '@ant-design/icons';
import type { IEditorActionsProps } from '@dtinsight/molecule/esm/model';

import { ID_COLLECTIONS } from '@/constant';
import { SyntaxIcon } from '../icon';

/**
 * 保存按钮 for toolbar
 */
const SAVE_TASK: IEditorActionsProps = {
    id: ID_COLLECTIONS.TASK_SAVE_ID,
    name: 'Save Task',
    title: '保存',
    icon: 'save',
    place: 'outer',
};

/**
 * 运行任务按钮 for toolbar
 */
const RUN_TASK: IEditorActionsProps = {
    id: ID_COLLECTIONS.TASK_RUN_ID,
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
    id: ID_COLLECTIONS.TASK_STOP_ID,
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
    id: ID_COLLECTIONS.TASK_SUBMIT_ID,
    name: '提交至调度',
    title: '提交至调度',
    icon: <UploadOutlined />,
    place: 'outer',
};

/**
 * 任务运维按钮
 */
const OPERATOR_TASK: IEditorActionsProps = {
    id: ID_COLLECTIONS.TASK_OPS_ID,
    name: '运维',
    title: '运维',
    icon: <LoginOutlined />,
    place: 'outer',
};

/**
 * 转换为脚本按钮
 */
const CONVERT_TASK: IEditorActionsProps = {
    id: ID_COLLECTIONS.TASK_CONVERT_SCRIPT,
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
    id: ID_COLLECTIONS.TASK_SYNTAX_ID,
    name: '语法检查',
    title: '语法检查',
    icon: <SyntaxIcon />,
    place: 'outer',
};

/**
 * 运行中按钮，通常和运行按钮是互斥存在，所以和运行按钮 id 保持一致
 */
const RUNNING_TASK: IEditorActionsProps = {
    id: ID_COLLECTIONS.TASK_RUN_ID,
    name: 'Running',
    title: '运行中',
    icon: 'loading~spin',
    disabled: true,
};

/**
 * 格式化
 */
const FORMAT_TASK: IEditorActionsProps = {
    id: ID_COLLECTIONS.TASK_FORMAT_ID,
    name: '格式化',
    title: '格式化',
    icon: <FormatPainterOutlined />,
    place: 'outer',
};

export default {
    SAVE_TASK,
    RUN_TASK,
    STOP_TASK,
    SUBMIT_TASK,
    OPERATOR_TASK,
    CONVERT_TASK,
    GRAMMAR_TASK,
    RUNNING_TASK,
    FORMAT_TASK,
} as Record<string, IEditorActionsProps>;
