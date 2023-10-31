import { useContext, useMemo, useRef, useState } from 'react';
import {
    CloseCircleOutlined,
    DownOutlined,
    LeftOutlined,
    MinusSquareOutlined,
    PlusSquareOutlined,
    RightOutlined,
    WarningOutlined,
} from '@ant-design/icons';
import { Badge, Button, Layout, Modal,Popover, Space, Spin, Tooltip, Tree } from 'antd';
import { history } from 'umi';

import api from '@/api';
import Editor from '@/components/editor';
import {
    CommonComponentIcon,
    ComputeComponentIcon,
    SchedulingComponentIcon,
    StoreComponentIcon,
} from '@/components/icon';
import type { COMPONENT_TYPE_VALUE } from '@/constant';
import context from '@/context/cluster';
import type { IComponentProps } from '.';
import './sideBar.scss';

interface ISideBarProps {
    /**
     * 当前支持的全部组件信息
     */
    componentsData?: IComponentProps[];
    /**
     * 当前已配置的组件
     */
    currentComponents?: {
        versionName?: string;
        componentTypeCode: COMPONENT_TYPE_VALUE;
        [key: string]: any;
    }[];
    loading?: boolean;
    /**
     * 当前组件树选中的节点
     */
    selectedNode?: string;
    onSelect?: (key: string) => void;
    onRemove?: (node: ITreeNodeProps) => Promise<void>;
    onAddComponent?: (keys: string[]) => void;
}

/**
 * key 值拼接规则为
 * 1. 当当前组件为不支持多版本共存的组件时，key 为 componentCode.toString 的值
 * 2. 当当前组件支持多版本共存时，key 为 joinVersions 该函数返回结果值
 */
type TreeNodeKey = string;

export interface ITreeNodeProps {
    title: string;
    key: TreeNodeKey;
    isLeaf: boolean;
    data?: any;
    icon?: JSX.Element;
    selectable?: boolean;
    children?: ITreeNodeProps[];
}

/**
 * 组件类型
 */
export const ComponentScheduleKind = {
    /**
     * 公共组件
     */
    Common: 0,
    /**
     * 资源调度组件
     */
    Resource: 1,
    /**
     * 存储组件
     */
    Store: 2,
    /**
     * 计算组件
     */
    Compute: 3,
};
const componentTitle = ['公共组件', '资源调度组件', '存储组件', '计算组件'] as const;

export type ComponentKindType = Valueof<typeof ComponentScheduleKind>;

/**
 * container 组件唯一 code
 */
const joinContainer = (code: number) => `container-${code}`;

/**
 * 多版本组件唯一 code
 * @param component 组件唯一 code
 * @param value 当前组件版本
 */
const joinVersions = (component: COMPONENT_TYPE_VALUE, value?: string) => {
    return `${component}-${value}`;
};

export default function SideBar({
    loading,
    selectedNode,
    componentsData = [],
    currentComponents = [],
    onSelect,
    onRemove,
    onAddComponent,
}: ISideBarProps) {
    const { editedComponents, connectable, setConnectable, setEdited } = useContext(context);
    const container = useRef<HTMLDivElement>(null);
    const [popoverVisibles, setPopoverVisibles] = useState<Record<string, boolean>>({});
    const [collapsed, setCollapsed] = useState(false);
    const [connectionLoading, setConnectionLoading] = useState(false);
    const [popoverValues, setPopoverValues] = useState<string[]>([]);
    const [errorMessage, setErrorMessage] = useState({
        visible: false,
        message: '',
    });

    const findTreeNode = (key: TreeNodeKey) => {
        const stack = [...treeData];
        while (stack.length) {
            const item = stack.pop();

            if (item?.children?.length) {
                stack.push(...item.children);
            }

            if (item?.key === key) {
                return item;
            }
        }
    };

    const handleRemoveComponent = (node: ITreeNodeProps) => {
        Modal.confirm({
            title: '确认要删除组件？',
            content: '此操作执行后不可逆，是否确认将当前组件删除？',
            icon: <CloseCircleOutlined color="#FF5F5C" />,
            okText: '删除',
            okType: 'danger',
            cancelText: '取消',
            onOk: () => {
                onRemove?.(node).then(() => {
                    if (selectedNode) {
                        setConnectable((c) => {
                            Reflect.deleteProperty(c, selectedNode);
                            return { ...c };
                        });
                    }
                });
            },
        });
    };

    const handlePopoverVisibleChange = (visible: boolean, key: string) => {
        setPopoverVisibles((p) => ({ ...p, [key]: visible }));
        if (visible) {
            // 获取当前分类下的组件信息
            const currentLayoutComponent = componentsData.filter((component) => joinContainer(component.owner) === key);
            const nextPopoverValues: string[] = [];
            currentLayoutComponent.forEach((component) => {
                if (!component.allowCoexistence) {
                    const uniqueCode: TreeNodeKey = component.componentCode.toString();
                    const isExistInTree = !!findTreeNode(uniqueCode);
                    if (isExistInTree) {
                        nextPopoverValues.push(uniqueCode);
                    }
                } else {
                    component.versionDictionary?.forEach((version) => {
                        const uniqueCode: TreeNodeKey = joinVersions(component.componentCode, version.key);
                        const isExistInTree = !!findTreeNode(uniqueCode);
                        if (isExistInTree) {
                            nextPopoverValues.push(uniqueCode);
                        }
                    });
                }
            });
            setPopoverValues(nextPopoverValues);
        }
    };

    const handleSubmit = () => {
        const nextValues = popoverValues.filter((val) => !findTreeNode(val));
        if (nextValues.length) {
            onAddComponent?.(nextValues);

            setEdited((e) => ({
                ...e,
                ...nextValues.reduce((pre, cur) => ({ ...pre, [cur]: true }), {}),
            }));
        }
        setPopoverVisibles({});
    };

    const handleConnectionsAll = () => {
        setConnectionLoading(true);
        api.testConnects<
            {
                componentTypeCode: COMPONENT_TYPE_VALUE;
                result: boolean;
                errorMsg?: string;
                versionName?: string;
            }[]
        >({
            clusterId: Number(history.location.query?.clusterId),
        })
            .then((res) => {
                if (res.code === 1) {
                    const status = res.data.reduce((pre, cur) => {
                        const target = componentsData.find((c) => c.componentCode === cur.componentTypeCode);
                        if (!target) return { ...pre };
                        if (target.allowCoexistence) {
                            return {
                                ...pre,
                                [joinVersions(cur.componentTypeCode, cur.versionName)]: cur.result || cur.errorMsg!,
                            };
                        }

                        return {
                            ...pre,
                            [cur.componentTypeCode.toString()]: cur.result || cur.errorMsg!,
                        };
                    }, {} as Record<string, true | string>);

                    setConnectable((c) => ({ ...c, ...status }));
                }
            })
            .finally(() => {
                setConnectionLoading(false);
            });
    };

    const getPopoverContent = (key: TreeNodeKey) => {
        // 获取当前分类下支持的组件类型
        const currentLayoutComponent = componentsData.filter((component) => joinContainer(component.owner) === key);

        const popoverTreeData = currentLayoutComponent.map((i) => {
            const componentKey = i.componentCode.toString();
            // 可以通过 currentComponents 判断，但是会加强逻辑，这里通过增加复杂度来减少逻辑
            // 所以直接通过判断 treeNode 中是否存在当前 key 的节点区分是否可点击
            const disabled = !!findTreeNode(componentKey);

            const tooltipContent =
                i.dependOn && `当前组件配置依赖于${i.dependOn.map((idx) => componentTitle[idx]).join('和')}`;

            return {
                title: i.name,
                selectable: !i.allowCoexistence,
                key: componentKey,
                disabled,
                disableCheckbox: disabled,
                isLeaf: !i.allowCoexistence,
                checkable: !i.allowCoexistence,
                tooltip: !i.allowCoexistence && tooltipContent,
                children: i.allowCoexistence
                    ? i.versionDictionary?.map((v) => {
                          const subVersionKey = joinVersions(i.componentCode, v.key);
                          const subVersionDisabled = !!findTreeNode(subVersionKey);

                          const isRenderVersionTag = /^\d/.test(v.key);

                          return {
                              title: `${i.name}[${isRenderVersionTag ? 'v' : ''}${v.key}]`,
                              key: subVersionKey,
                              selectable: true,
                              tooltip: tooltipContent,
                              disabled: subVersionDisabled,
                              disableCheckbox: subVersionDisabled,
                              isLeaf: true,
                              checkable: true,
                              children: [],
                          };
                      })
                    : [],
            };
        });

        return (
            <>
                <Tree
                    checkedKeys={popoverValues}
                    className="component-config"
                    checkable
                    treeData={popoverTreeData}
                    showLine={{ showLeafIcon: false }}
                    switcherIcon={<DownOutlined />}
                    titleRender={(node) =>
                        node.tooltip ? (
                            <Tooltip
                                destroyTooltipOnHide
                                title={
                                    <>
                                        <WarningOutlined style={{ color: 'var(--editorWarning-foreground)' }} />
                                        {node.tooltip}
                                    </>
                                }
                            >
                                {node.title}
                            </Tooltip>
                        ) : (
                            node.title
                        )
                    }
                    defaultExpandAll
                    blockNode
                    onCheck={(checked) => setPopoverValues(checked as string[])}
                    onSelect={(_, { node }) =>
                        setPopoverValues((values) =>
                            values.includes(node.key.toString())
                                ? values.filter((val) => val !== node.key.toString())
                                : [...values, node.key.toString()]
                        )
                    }
                />
                <Space className="component-popover-btns">
                    <Button size="small" type="primary" onClick={() => handleSubmit()}>
                        确认
                    </Button>
                    <Button size="small" onClick={() => setPopoverVisibles({})}>
                        取消
                    </Button>
                </Space>
            </>
        );
    };

    const renderConnectionStatus = (key: TreeNodeKey) => {
        const status = connectable[key];
        if (typeof status === 'undefined') return null;

        if (typeof status === 'string') {
            return (
                <Tooltip
                    title={
                        <div
                            className="cursor-pointer"
                            onClick={(e) => {
                                e.stopPropagation();
                                setErrorMessage({ visible: true, message: status });
                            }}
                        >
                            {status.substring(0, 200)}...
                        </div>
                    }
                >
                    <Badge status="error" />
                </Tooltip>
            );
        }

        return (
            <Tooltip title="组件连通性测试通过">
                <Badge status="success" />
            </Tooltip>
        );
    };

    const renderTreeNode = (node: ITreeNodeProps) => {
        if (node.isLeaf) {
            return (
                <div title={node.title} className="component-node">
                    <Space>
                        {renderConnectionStatus(node.key)}
                        {editedComponents[node.key] && (
                            <Tooltip title="当前组件未保存">
                                <Badge status="processing" />
                            </Tooltip>
                        )}
                        <Tooltip title={node.title}>
                            <span className="component-name">{node.title}</span>
                        </Tooltip>
                        <Tooltip title={`删除 ${node.title} 组件`}>
                            <MinusSquareOutlined
                                className="component-remove"
                                onClick={(e) => {
                                    e.stopPropagation();
                                    handleRemoveComponent(node);
                                }}
                            />
                        </Tooltip>
                    </Space>
                </div>
            );
        }
        // 是否是分类组件，比如「计算组件」这个节点
        const isWrapComponent = !node.data;

        return (
            <span title={node.title} className="component-node">
                <Space>
                    {node.icon}
                    <span className="component-name">{node.title}</span>
                    {isWrapComponent && (
                        <Tooltip title="组件配置">
                            <Popover
                                content={getPopoverContent(node.key)}
                                destroyTooltipOnHide
                                placement="bottom"
                                title={`${node.title}配置`}
                                trigger="click"
                                visible={!!popoverVisibles[node.key]}
                                getPopupContainer={() => container.current || document.body}
                                onVisibleChange={(visible) => handlePopoverVisibleChange(visible, node.key)}
                            >
                                <PlusSquareOutlined />
                            </Popover>
                        </Tooltip>
                    )}
                </Space>
            </span>
        );
    };

    /**
     * 当前组件 tree 的数据
     */
    const treeData = useMemo<ITreeNodeProps[]>(() => {
        const componentIcon = [
            <CommonComponentIcon key="CommonComponentIcon" />,
            <SchedulingComponentIcon key="SchedulingComponentIcon" />,
            <StoreComponentIcon key="StoreComponentIcon" />,
            <ComputeComponentIcon key="ComputeComponentIcon" />,
        ];

        // 需要去重，因为 currentComponents 是当前配置的组件
        // 可能包含具有相同 code，不同 versionName 的组件，比如 Spark2.1 Spark2.2
        const currentComponentKeys = Array.from(
            new Set(currentComponents.map((component) => component.componentTypeCode))
        );

        const res = Object.keys(ComponentScheduleKind).map((key) => {
            const value = ComponentScheduleKind[key as keyof typeof ComponentScheduleKind];

            // 获取组件分类下支持的全部组件
            let childrenNode = componentsData.filter((layout) => layout.owner === value);

            // 获取当前集群中已经配置的组件
            childrenNode = childrenNode.filter((node) => currentComponentKeys.includes(node.componentCode));

            return {
                id: joinContainer(value),
                title: componentTitle[value],
                key: joinContainer(value),
                icon: componentIcon[value],
                selectable: false,
                isLeaf: false,
                children: childrenNode.map((child) => ({
                    title: child.name,
                    key: child.componentCode.toString(),
                    isLeaf: !child.allowCoexistence,
                    selectable: !child.allowCoexistence,
                    children: child.allowCoexistence
                        ? child.versionDictionary
                              ?.filter((version) =>
                                  currentComponents.some(
                                      (c) =>
                                          `${c.componentTypeCode}-${c.versionName}` ===
                                          `${child.componentCode}-${version.key}`
                                  )
                              )
                              ?.map((versions) => {
                                  const isRenderVersionTag = /^\d/.test(versions.key);

                                  return {
                                      title: `${child.name}[${isRenderVersionTag ? 'v' : ''}${versions.key}]`,
                                      // 多版本的组件 id 用当前的 versionName 和 componentCode 拼接字符串
                                      key: joinVersions(child.componentCode, versions.key),
                                      isLeaf: true,
                                      data: versions,
                                  };
                              })
                        : [],
                    data: child,
                })),
            };
        });

        return res;
    }, [componentsData, currentComponents.length]);

    const selectedKeys = useMemo(() => (selectedNode ? [selectedNode] : []), [selectedNode]);

    return (
        <Layout.Sider collapsed={collapsed} className="cluster-sider" ref={container}>
            <Layout className="h-full">
                <Layout.Content className="h-full">
                    <Spin spinning={loading}>
                        {!loading && (
                            <Tree<ITreeNodeProps>
                                className="cluster-component-tree"
                                treeData={treeData}
                                selectedKeys={selectedKeys}
                                titleRender={renderTreeNode}
                                showLine={{ showLeafIcon: false }}
                                switcherIcon={<DownOutlined />}
                                defaultExpandAll
                                blockNode
                                onSelect={(_, { node }) => selectedNode !== node.key && onSelect?.(node.key as string)}
                            />
                        )}
                    </Spin>
                    <Button className="cluster-component-collapse" onClick={() => setCollapsed((c) => !c)}>
                        {collapsed ? <RightOutlined /> : <LeftOutlined />}
                    </Button>
                </Layout.Content>
                <Layout.Footer>
                    <Button
                        className="cluster-component-btns"
                        type="text"
                        block
                        loading={connectionLoading}
                        onClick={handleConnectionsAll}
                    >
                        <Tooltip title="测试组件连通性需要先保存组件">测试所有组件连通性</Tooltip>
                    </Button>
                </Layout.Footer>
            </Layout>
            <Modal
                width={800}
                title="错误信息"
                visible={errorMessage.visible}
                onCancel={() => setErrorMessage({ visible: false, message: '' })}
                footer={null}
                maskClosable
                destroyOnClose
            >
                <Editor
                    style={{ height: 500 }}
                    sync
                    value={errorMessage.message}
                    language="jsonlog"
                    options={{
                        readOnly: true,
                        minimap: {
                            enabled: false,
                        },
                    }}
                />
            </Modal>
        </Layout.Sider>
    );
}
