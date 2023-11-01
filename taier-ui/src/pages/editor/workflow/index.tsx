import { useContext, useEffect, useRef, useState } from 'react';
import ReactDOMServer from 'react-dom/server';
import { ApartmentOutlined, CheckOutlined } from '@ant-design/icons';
import molecule from '@dtinsight/molecule';
import { connect } from '@dtinsight/molecule/esm/react';
import { Badge, Button, Form, Input, message, Modal, Select, Space, Tooltip } from 'antd';
import classNames from 'classnames';
import type { mxCell, mxGraph } from 'mxgraph';

import api from '@/api';
import type {
    IContainerProps,
    IContainerRef,
    IContextMenuConfig,
    IGeometryPosition,
    IKeyDownConfig,
} from '@/components/mxGraph/container';
import MxGraphContainer, { WIDGETS_PREFIX } from '@/components/mxGraph/container';
import { ICreateTaskFormFieldProps } from '@/components/task/create';
import { formItemLayout, TASK_TYPE_ENUM } from '@/constant';
import context from '@/context';
import type { IOfflineTaskProps } from '@/interface';
import { IComputeType } from '@/interface';
import { taskRenderService } from '@/services';
import taskSaveService from '@/services/taskSaveService';
import viewStoreService from '@/services/viewStoreService';
import { getTenantId, randomId } from '@/utils';
import './index.scss';

// 是否编辑状态未保存的标志符
export const isEditing = Symbol('editing');
const editingInputKey = 'taier__workflow__input--edit';

interface IWorkflowData {
    id: number | `workflow__${string}`;
    flowId: number;
    flowName: string;
    taskType: TASK_TYPE_ENUM;
    name: string;
    taskDesc: string;
    childNode: IWorkflowData[];
    [isEditing]: boolean;

    [key: string]: any;
}

// 支持在当前 dom 下生成展示错误信息
const renderErrorMessage = (dom: HTMLInputElement, errorMsg: string) => {
    const inputRef = dom;
    inputRef.style.outline = '1px solid #BE1100';
    if (!inputRef.parentElement!.querySelector('.workflow__vertex__input__validation')) {
        const msgDom = document.createElement('div');
        msgDom.innerText = errorMsg;
        msgDom.classList.add('workflow__vertex__input__validation');
        inputRef.parentElement!.appendChild(msgDom);
    } else {
        const msgDom = inputRef.parentElement!.querySelector<HTMLDivElement>('.workflow__vertex__input__validation');
        msgDom!.innerText = errorMsg;
    }
};

// 工作流
function Workflow({ current }: molecule.model.IEditor) {
    const { supportJobTypes } = useContext(context);
    const [loading, setLoading] = useState(false);
    const [modalInfo, setModalInfo] = useState<{
        visible: boolean;
        create: boolean;
        editData?: IWorkflowData;
    }>({
        visible: false,
        create: true,
        editData: undefined,
    });
    const [graphData, setGraphData] = useState<IWorkflowData[]>([]);
    const [form] = Form.useForm<ICreateTaskFormFieldProps>();
    const container = useRef<IContainerRef<IWorkflowData>>(null);
    const dragStage = useRef<{
        x: number;
        y: number;
    }>({
        x: -1,
        y: -1,
    });
    // It's a flag remark the wether in editing
    const isInEdit = useRef(false);

    /**
     * 修改当前 tab 的状态为编辑态
     */
    const updateCurrentTab = () => {
        molecule.editor.updateTab({
            id: current!.tab!.id,
            status: 'edited',
        });
    };

    const validateTaskName = async (value: string, cellId: string | number | undefined) => {
        if (!value) return Promise.reject(new Error('节点名称不可为空!'));
        if (value.length > 128) return Promise.reject(new Error('节点名称不得超过128个字符!'));
        const reg = /^[A-Za-z0-9_\u4e00-\u9fa5]+$/;
        if (!reg.test(value)) return Promise.reject(new Error('节点名称只能由字母、数字、中文、下划线组成!'));

        const selfCell = container.current?.getCells().find((cell) => cell.vertex && cell.value?.id === cellId);

        const isUpdatedName = selfCell?.value.name !== value;

        if (isUpdatedName) {
            // Validate duplicated name in back-end
            const res = await api.validateRepeatTaskName({
                taskName: value,
                tenantId: getTenantId(),
            });
            if (res.code !== 1) return Promise.reject(new Error('子节点名称已存在!'));

            // Validate duplicated name in front-end,
            // since there were some nodes just added and still not be given to back-end
            if (container.current?.getCells().some((cell) => cell.vertex && cell.value?.name === value))
                return Promise.reject(new Error('子节点名称已存在!'));
        }

        return Promise.resolve();
    };

    // 在某位置插入节点
    const handleDrop: IContainerProps<IWorkflowData>['onDropWidgets'] = (node, graph, target, x, y) => {
        dragStage.current = { x, y };
        setTimeout(() => {
            const taskType = Number(node.dataset.type);
            form.setFieldsValue({
                taskType,
            });
            setModalInfo({ visible: true, create: true });
        }, 0);
    };

    // 渲染拖拽的预览节点
    const handleRenderPreview = (node: HTMLElement) => {
        const taskType = Number(node.dataset.type);
        const text = supportJobTypes.find((type) => type.key === taskType)?.value || '';
        const previewDragTarget = document.createElement('div');
        previewDragTarget.className = 'workflow__preview__vertex';
        previewDragTarget.setAttribute('data-item', text);

        previewDragTarget.innerHTML = ReactDOMServer.renderToString(
            <>
                <span className="workflow__preview__title">新节点</span>
                <span className="workflow__preview__desc">{text}</span>
            </>
        );
        return previewDragTarget;
    };

    const handleInsertCell = () => {
        form.validateFields().then((values) => {
            if (modalInfo.create) {
                const { x, y } = dragStage.current;
                container.current?.insertCell(
                    {
                        ...values,
                        [isEditing]: true,
                        id: `workflow__${randomId()}`,
                        childNode: [],
                        flowId: current?.tab?.data.id,
                        flowName: current?.tab?.data.name,
                    },
                    x,
                    y
                );

                setModalInfo({ visible: false, create: true });
                form.resetFields();
            } else {
                container.current?.updateCell(modalInfo.editData!.id.toString(), {
                    ...values,
                    [isEditing]: true,
                });

                setModalInfo({ visible: false, create: false, editData: undefined });
                updateCurrentTab();
                form.resetFields();
            }
        });
    };

    const toggleEditInputDom = (dom: HTMLInputElement, domVisible: boolean) => {
        const vertexInput = dom;
        if (domVisible) {
            vertexInput.parentElement!.style.display = 'block';
            (vertexInput.parentElement!.nextElementSibling as HTMLDivElement).style.display = 'none';
            vertexInput.focus();
            // place the cursor at the end of text
            vertexInput.selectionEnd = vertexInput.value.length;
            vertexInput.selectionStart = vertexInput.value.length;
            isInEdit.current = true;
        } else {
            vertexInput.blur();
            vertexInput.parentElement!.style.display = 'none';
            (vertexInput.parentElement!.nextElementSibling as HTMLDivElement).style.display = 'block';
            isInEdit.current = false;
        }
    };

    const handleDoubleClick = (data: IWorkflowData) => {
        if (!isInEdit.current) {
            setLoading(true);
            taskRenderService.openTask(
                { ...data },
                {
                    // workflow task don't need to getTaskById again
                    // since already request getTaskById for each vertex in workflow's useEffect
                    create: data.id.toString().startsWith('workflow__'),
                }
            );
        }
    };

    const handleContextMenu = (data: IWorkflowData, cell: mxCell, graph: mxGraph): IContextMenuConfig[] => {
        return [
            {
                title: '保存节点',
                callback: () => {
                    taskSaveService.saveTab(cell.value);
                },
            },
            {
                title: '编辑名称',
                callback: () => {
                    const vertexInput = document.querySelector<HTMLInputElement>(`.${editingInputKey}--${cell.id}`);
                    if (vertexInput) {
                        toggleEditInputDom(vertexInput, true);

                        // eslint-disable-next-line no-inner-declarations
                        function handleSaveTaskName(this: HTMLInputElement) {
                            validateTaskName(this.value, cell.id)
                                .then(() => {
                                    cell.setValue({
                                        ...data,
                                        name: this.value,
                                        [isEditing]: true,
                                    });
                                    updateCurrentTab();
                                    toggleEditInputDom(this, false);
                                    graph.view.refresh();
                                    this.removeEventListener('blur', handleSaveTaskName);
                                    document.querySelector<HTMLDivElement>('#molecule')?.focus();
                                })
                                .catch((error: Error) => {
                                    renderErrorMessage(this, error.message);
                                    this.focus();
                                });
                        }

                        vertexInput.addEventListener('blur', handleSaveTaskName);

                        vertexInput.onkeydown = (event) => {
                            if (event.keyCode === 13) {
                                vertexInput.blur();
                            }
                        };
                    }
                },
            },
            {
                title: '编辑节点属性',
                callback: () => {
                    form.setFieldsValue({
                        taskType: data.taskType,
                        name: data.name,
                        taskDesc: data.taskDesc,
                    });
                    setModalInfo({ visible: true, create: false, editData: data });
                },
            },
            {
                title: '查看节点内容',
                callback: () => {
                    // 查看节点内容和双击的交互是一致的
                    handleDoubleClick(data);
                },
            },
            {
                // 不需要声明 callback，保证 id 和键盘删除事件一致即可
                id: 'removeCell',
                title: '删除节点',
            },
        ];
    };

    const handleEdgeContextMenu = () => {
        return [
            {
                // 不需要声明 callback，保证 id 和键盘删除事件一致即可
                id: 'removeCell',
                title: '删除连线',
            },
        ];
    };

    const renderCell = (cell: mxCell) => {
        const { value } = cell;

        return ReactDOMServer.renderToString(
            <div className="workflow__vertex" tabIndex={-1}>
                <div className="workflow__vertex__suffixInput">
                    <input
                        className={classNames('workflow__vertex__input', `${editingInputKey}--${cell.id}`)}
                        defaultValue={value.name}
                    />
                    <CheckOutlined title="保存" className="workflow__vertex__input__icon" />
                </div>
                <div className="workflow__vertex__title">
                    {value[isEditing] && <Badge status="processing" />}
                    {value.name || '-'}
                </div>
                <div className="workflow__vertex__taskType">
                    {supportJobTypes.find((i) => i.key === value.taskType)?.value || '未知'}
                </div>
            </div>
        );
    };

    const handleClickVertex = (cell: mxCell, graph: mxGraph) => {
        graph.container.focus();
        if (
            document.activeElement?.nodeName === 'INPUT' &&
            document.activeElement.classList.contains(`${editingInputKey}--${cell.id}`)
        ) {
            (document.activeElement as HTMLInputElement).blur();
        }
    };

    const handleRegisterKeyboardEvent = (): IKeyDownConfig[] => {
        return [
            {
                id: 'removeCell',
                method: 'bindControlKey',
                keyCode: 8,
                func: () => {
                    const cell = container.current?.getSelectedCell();
                    if (cell) {
                        if (cell.vertex) {
                            Modal.confirm({
                                title: '注意',
                                okText: '确认',
                                okType: 'danger',
                                cancelText: '取消',
                                content: (
                                    <>
                                        确定是否要删除节点:
                                        <Button type="link">{cell.value.name}</Button>
                                    </>
                                ),
                                onOk() {
                                    // 还未保存至服务端的节点，直接删除不需要调用服务端的删除接口
                                    if (cell.value.id.toString().startsWith('workflow__')) {
                                        container.current?.removeCell(cell.id);
                                        updateCurrentTab();
                                    } else {
                                        api.delOfflineTask({ taskId: cell.value.id }).then((res) => {
                                            if (res.code === 1) {
                                                const id = cell.value.id.toString();
                                                container.current?.removeCell(cell.id);
                                                message.success('删除成功');
                                                // Close the opened tab
                                                const isOpened = molecule.editor.isOpened(id);
                                                if (isOpened) {
                                                    const groupId = molecule.editor.getGroupIdByTab(id);
                                                    if (groupId) {
                                                        molecule.editor.closeTab(id, groupId);
                                                    }
                                                }
                                                updateCurrentTab();
                                            }
                                        });
                                    }
                                },
                            });
                        } else {
                            // 直接删除连线
                            container.current?.removeCell(cell.id);
                            updateCurrentTab();
                        }
                    }
                },
            },
        ];
    };

    const handleSaveGraph = (geo?: IGeometryPosition) => {
        if (current?.tab?.id) {
            const defaultValue = viewStoreService.getViewStorage<{
                cells: mxCell[];
                geometry: IGeometryPosition;
            }>(current.tab.id.toString());

            const cells = container.current?.getCells();
            viewStoreService.setViewStorage(current.tab.id.toString(), {
                cells,
                geometry: geo || defaultValue?.geometry,
            });

            // Prevent update tab in first time
            if (defaultValue) {
                updateCurrentTab();
            }
        }
    };

    useEffect(() => {
        if (current?.tab?.id && viewStoreService.getViewStorage(current?.tab?.id.toString())) {
            const view = viewStoreService.getViewStorage<{
                cells: mxCell[];
                geometry: IGeometryPosition;
            }>(current?.tab?.id.toString());
            container.current?.setCells(view.cells);
            container.current?.setView(view.geometry);
        } else {
            try {
                const nodeMap: Record<string, number[]> = JSON.parse(current?.tab?.data.sqlText) || {};

                const nextGraphData: IWorkflowData[] = [];
                const vertexIds = Object.keys(nodeMap);

                // 树结构的节点引用对象缓存，用于快速定位
                const childrenNodeReference: Record<number, IWorkflowData> = {};

                setLoading(true);
                Promise.all(vertexIds.map((id) => api.getOfflineTaskByID<IOfflineTaskProps>({ id })))
                    .then((results) => {
                        if (results.every((res) => res.code === 1)) {
                            const tasks = results.map((res) => res.data);
                            // 根节点集合
                            const rootTaskList = vertexIds.filter((key) => !nodeMap[key].length);

                            const stack = [...rootTaskList];

                            while (stack.length) {
                                const taskId = stack.shift()!;

                                const task = tasks.find((t) => t.id.toString() === taskId.toString());

                                if (task) {
                                    const node = { ...task, childNode: [], [isEditing]: false };

                                    //  获取 parent 节点的 id 集合
                                    const parentTaskIds: number[] = nodeMap[node.id] || [];

                                    if (parentTaskIds.length) {
                                        parentTaskIds.forEach((parentTaskId) => {
                                            // 根节点添加的时候，childrenNodeReference 上不存在对象缓存
                                            const referenceHandler =
                                                childrenNodeReference[parentTaskId]?.childNode || nextGraphData;

                                            referenceHandler.push(node);
                                        });
                                    } else {
                                        // 根节点不存在 parent 节点
                                        nextGraphData.push(node);
                                    }

                                    childrenNodeReference[node.id] = node;

                                    const depsTaskIds = vertexIds.filter((key) => {
                                        return nodeMap[key].includes(Number(taskId));
                                    });

                                    depsTaskIds.forEach((id) => {
                                        if (!stack.includes(id)) {
                                            stack.push(id);
                                        }
                                    });
                                }
                            }
                        }

                        setGraphData(nextGraphData);
                    })
                    .finally(() => {
                        setLoading(false);
                    });
            } catch {
                setGraphData([]);
            }
        }

        viewStoreService.onStorageChange((tabId) => {
            if (tabId === current?.tab?.id) {
                const viewStorage = viewStoreService.getViewStorage<{ cells: mxCell[] }>(tabId);
                container.current?.updateCell(viewStorage.cells[0].id, viewStorage.cells[0].value);
            }
        });
    }, []);

    if (!current?.activeTab) {
        return <div>当前任务未找到</div>;
    }

    return (
        <>
            <MxGraphContainer<IWorkflowData>
                ref={container}
                enableDrag
                graphData={graphData}
                loading={loading}
                config={{
                    tooltips: false,
                    connectable: true,
                }}
                vertexKey="id"
                onGetPreview={handleRenderPreview}
                onDropWidgets={handleDrop}
                onRenderCell={renderCell}
                onClick={handleClickVertex}
                onKeyDown={handleRegisterKeyboardEvent}
                onCellsChanged={() => handleSaveGraph()}
                onContainerChanged={handleSaveGraph}
                onDoubleClick={handleDoubleClick}
                onRenderActions={(graph) => (
                    <>
                        <Tooltip title="布局">
                            <ApartmentOutlined
                                onClick={() => {
                                    graph?.center(true, true, 0.55, 0.4);
                                }}
                            />
                        </Tooltip>
                    </>
                )}
                onContextMenu={handleContextMenu}
                onEdgeContextMenu={handleEdgeContextMenu}
                onRenderWidgets={() => {
                    return (
                        <>
                            <div className="workflow__widgets__title">节点组件</div>
                            <div className="workflow__widgets__content">
                                <Space direction="vertical" size={15}>
                                    {supportJobTypes
                                        .filter(
                                            (t) =>
                                                t.key !== TASK_TYPE_ENUM.WORK_FLOW &&
                                                t.computeType === IComputeType.BATCH
                                        )
                                        .map(({ key, value }) => (
                                            <div
                                                key={key}
                                                data-type={key}
                                                className={classNames(
                                                    'workflow__widgets__componentName',
                                                    `${WIDGETS_PREFIX}__${key}`
                                                )}
                                            >
                                                {value}
                                            </div>
                                        ))}
                                </Space>
                            </div>
                        </>
                    );
                }}
            />
            <Modal
                title={modalInfo.create ? '新建节点' : '编辑节点'}
                visible={modalInfo.visible}
                onCancel={() => {
                    form.resetFields();
                    setModalInfo((p) => ({ ...p, visible: false, editData: undefined }));
                }}
                onOk={handleInsertCell}
            >
                <Form<ICreateTaskFormFieldProps> form={form} autoComplete="off" {...formItemLayout}>
                    <Form.Item
                        label="节点名称"
                        name="name"
                        validateTrigger="onBlur"
                        rules={[
                            {
                                validator: (_, value) => validateTaskName(value, modalInfo.editData?.id),
                            },
                        ]}
                    >
                        <Input placeholder="请输入节点名称" />
                    </Form.Item>
                    <Form.Item
                        label="节点类型"
                        name="taskType"
                        rules={[
                            {
                                required: true,
                                message: `请选择节点类型`,
                            },
                        ]}
                    >
                        <Select
                            placeholder="请选择任务类型"
                            disabled
                            options={supportJobTypes.map((t) => ({
                                label: t.value,
                                value: t.key,
                            }))}
                        />
                    </Form.Item>
                    <Form.Item noStyle dependencies={['taskType']}>
                        {(currentForm) =>
                            taskRenderService.renderCreateForm(
                                currentForm.getFieldValue('taskType'),
                                undefined,
                                currentForm
                            )
                        }
                    </Form.Item>
                    <Form.Item
                        label="描述"
                        name="taskDesc"
                        rules={[
                            {
                                max: 200,
                                message: '描述请控制在200个字符以内！',
                            },
                        ]}
                    >
                        <Input.TextArea placeholder="请输入描述" disabled={false} rows={4} />
                    </Form.Item>
                </Form>
            </Modal>
        </>
    );
}

export default connect(molecule.editor, Workflow);
