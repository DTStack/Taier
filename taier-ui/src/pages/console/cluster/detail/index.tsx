import { useEffect, useMemo, useRef, useState } from 'react';
import { Empty, Form,Layout, message } from 'antd';
import type { RcFile } from 'antd/lib/upload';
import { history } from 'umi';

import api from '@/api';
import req from '@/api/request';
import notification from '@/components/notification';
import type { COMPONENT_TYPE_VALUE } from '@/constant';
import { FILE_TYPE } from '@/constant';
import context from '@/context/cluster';
import { taskRenderService } from '@/services';
import type { ILayoutData, ITemplateData } from './detail';
import Detail from './detail';
import type { ComponentKindType,ITreeNodeProps } from './sideBar';
import SideBar, { ComponentScheduleKind } from './sideBar';
import Toolbar from './toolbar';
import './index.scss';

const { Content, Footer } = Layout;

export interface IComponentProps {
    /**
     * 传给后端的值
     */
    componentCode: COMPONENT_TYPE_VALUE;
    allowKerberos: boolean;
    /**
     * 控制是否渲染上传配置文件
     */
    uploadConfigType?: 1;
    name: string;
    /**
     * 是否兼容多版本，与 versionDictionary 不挂钩
     */
    allowCoexistence: boolean;
    /**
     * 该组件是否具备前置条件
     */
    dependOn?: ComponentKindType[];
    /**
     * 当存在多版本的时候，多版本信息
     * key 是文案信息，value 是服务端需要的值
     */
    versionDictionary?: { key: string; value?: any }[];
    /**
     * 该组件所属的组件类型
     */
    owner: ComponentKindType;
}

interface IClusterDetailProps {
    clusterName: string;
    clusterId: number;
    componentVOS: {
        id?: number;
        gmtCreate?: number;
        gmtModified?: number;
        componentName?: string;
        componentTypeCode: COMPONENT_TYPE_VALUE;
        isDefault?: boolean;
        principal?: string;
        principals?: string[];
        componentTemplate?: string;
        /**
         * 支持 JSON.parse 的数据
         */
        componentConfig?: string;
        clusterId?: number;
        uploadFileName?: string;
        deployType?: string;
        kerberosFileName?: string;
        storeType?: number;
        versionName?: string;
    }[];
}

// 负责捞 Cascader 的值
function getTargetPath(tree: any[], targetKey: string) {
    const res: string[] = [];
    tree.forEach((node) => {
        if (Array.isArray(node.value)) {
            const target = node.value.find((child: any) => child.key === targetKey);
            if (target) {
                res.push(node.key, target.key);
                return res;
            }
        }
    });

    return res;
}

export default function ClusterDetail() {
    const [form] = Form.useForm();
    const container = useRef<HTMLElement>(null);
    // 左侧组件树的全部信息
    const [componentsData, setComponentData] = useState<IComponentProps[]>([]);
    const [loading, setLoading] = useState(false);
    // 当前左侧组件树选中的节点
    const [selectedKey, setSelectKey] = useState<string | undefined>(undefined);
    const [currentCluster, setCurrent] = useState<null | IClusterDetailProps>(null);
    // 组件编辑状态记录
    const [editedComponents, setEdited] = useState<Record<string, boolean>>({});
    // 组件连通性结果记录
    const [connectable, setConnectable] = useState<Record<string, true | string>>({});
    const [templateData, setTemplate] = useState<ILayoutData[]>([]);
    // Kerberos 文件上传后支持配置
    const [principals, setPrincipals] = useState<string[]>([]);
    const [detailLoading, setDetailLoading] = useState(false);
    // 已请求过详情的组件，防止重复请求
    const requestedList = useRef(new Set<number>());

    /**
     * 基于 key 获取对应的已保存或未保存的信息
     */
    const findComponentVOS = (key?: string) => {
        if (!key) return;
        const [major, ...rest] = key.split('-');
        const minor = rest.join('-');
        return currentCluster?.componentVOS.find((component) => {
            const isSubComponent = !!minor;
            return (
                component.componentTypeCode.toString() === major &&
                // 如果是多版本的子版本需要校验版本信息
                (isSubComponent ? component.versionName === minor : true)
            );
        });
    };

    const getComponentModels = async () => {
        const res = await api.getComponentModels();
        if (res.code === 1) {
            setComponentData(res.data);
        }
    };

    /**
     * 获取当前集群详情
     */
    const getClusterDetail = () => {
        const clusterId = history.location.query?.clusterId;
        if (!clusterId) return;
        api.getClusterInfo({ clusterId: clusterId as string }).then((res) => {
            if (res.code === 1) {
                setCurrent(res.data);
            }
        });
    };

    /**
     * 获取当前集群某一组件的具体详情
     */
    const getDetailValue = async (target: IClusterDetailProps['componentVOS'][number], id: number) => {
        if (!requestedList.current.has(id)) {
            const res = await api.getComponentInfo({ componentId: id });
            if (res.code === 1) {
                setCurrent((current) => {
                    if (current) {
                        Object.assign(target, res.data);
                        return { ...current };
                    }
                    return null;
                });

                setPrincipals(res.data.principals?.split(',') || []);

                requestedList.current.add(id);
                return res.data;
            }
        }

        return target;
    };

    /**
     * 获取当前组件的 layout 信息
     * @param code 当前组件的唯一 code
     * @param versionName 当前组件如果支持切换版本的话，则需要传该值
     */
    const loadTemplate = async (code: string, versionName?: string) => {
        const clusterId = history.location.query?.clusterId;
        const res = await api.getLoadTemplate<ITemplateData[]>({
            clusterId,
            componentType: code,
            storeType: 2,
            // TODO
            deployType: versionName?.endsWith('standalone') ? 0 : undefined,
            versionName,
        });
        if (res.code === 1) {
            const nextLayoutData: ILayoutData[] = [];

            const isHaveOptionsType = ['CHECKBOX', 'RADIO_LINKAGE', 'SELECT'];

            const stack = [...res.data];
            while (stack.length) {
                const template = stack.shift()!;

                let options: { label: string; value: string }[] = [];
                if (isHaveOptionsType.includes(template.type)) {
                    const optionComponents = stack.filter(
                        (temp) =>
                            temp.dependencyKey ===
                            `${template.dependencyKey ? `${template.dependencyKey}$` : ''}${template.key}`
                    );

                    options = optionComponents.map((comp) => {
                        const idx = stack.indexOf(comp);
                        stack.splice(idx, 1);
                        return {
                            label: comp.key,
                            value: comp.value,
                        };
                    });
                }

                if (!template.dependencyKey) {
                    nextLayoutData.push({
                        id: template.id,
                        name: template.key,
                        label: template.key,
                        type: template.type,
                        tooltip: template.keyDescribe,
                        initialValue: template.type === 'CHECKBOX' ? JSON.parse(template.value) : template.value,
                        required: template.required,
                        componentProps: options.length
                            ? {
                                  options,
                              }
                            : {},
                    });
                } else {
                    const [key, field] = template.dependencyKey.split('$');

                    const fieldComponent = res.data.find((temp) => temp.key === field);

                    const isObject = fieldComponent?.type === 'GROUP';

                    nextLayoutData.push({
                        id: template.id,
                        name: isObject ? [fieldComponent?.key, template.key] : template.key,
                        label: template.key,
                        type: template.type,
                        tooltip: template.keyDescribe,
                        dependencies: [key],
                        dependencyValue: fieldComponent?.value,
                        initialValue: template.type === 'CHECKBOX' ? JSON.parse(template.value) : template.value,
                        required: template.required,
                        componentProps: options.length
                            ? {
                                  options,
                              }
                            : {},
                    });
                }
            }

            const names = nextLayoutData.map((i) => (Array.isArray(i.name) ? i.name.join('.') : i.name));

            if (names.length !== Array.from(new Set(names)).length) {
                notification.error({
                    key: 'Duplicate_template',
                    message: `检测到 ${
                        componentsData.find((i) => i.componentCode.toString() === code.toString())?.name || '未知组件'
                    }${versionName || ''} 组件的 template 接口返回的数据中存在重复项，造成当前组件渲染问题`,
                });
            }
            setTemplate(nextLayoutData);
        } else {
            setTemplate([]);
        }
    };

    const handleSidebarSelect = async (key: string) => {
        const target = findComponentVOS(key);
        setDetailLoading(true);
        try {
            // 如果是已经保存过的组件信息，则获取组件信息详情
            if (typeof target?.id === 'number') {
                const detailVal = await getDetailValue(target, target.id);

                // 根据组件详细信息加载当前组件的界面信息
                if (detailVal) {
                    await loadTemplate(detailVal.componentTypeCode, detailVal.versionName);
                }
            } else {
                const [major] = key.split('-');
                await loadTemplate(major, target?.versionName);
            }
        } finally {
            setDetailLoading(false);
            setSelectKey(key);
        }
    };

    const handleAddComponent = (keys: string[]) => {
        if (!currentCluster) return;
        const nextCurrent = { ...currentCluster };
        keys.forEach((key) => {
            const [major, ...rest] = key.split('-');
            const minor = rest.join('-');

            const target = componentsData.find((layout) => layout.componentCode.toString() === major);
            if (target) {
                // 如果 minor 存在表示当前组件是支持多版本同时存在的组件
                const versionName =
                    minor ||
                    (() => {
                        // 否则，仍然有可能是多版本，只不过是不支持多版本兼容的组件
                        let nextVersionName = '';
                        let item = target?.versionDictionary;

                        // 默认选择 versionDictionary 的第一个值
                        while (Array.isArray(item)) {
                            nextVersionName = item[0].key;

                            // 需要区分 Cascader 组件的 value 情况
                            if (Array.isArray(item[0].value)) {
                                item = item[0].value;
                            } else {
                                item = undefined;
                            }
                        }

                        return nextVersionName;
                    })();

                nextCurrent.componentVOS?.push({
                    clusterId: nextCurrent.clusterId!,
                    componentName: target.name,
                    componentTypeCode: target.componentCode,
                    versionName,
                });
            }
        });

        setCurrent(nextCurrent);

        // 自动选择第一个
        handleSidebarSelect(keys[0]);
    };

    const handleRemoveComponent = async (node: ITreeNodeProps) => {
        const target = findComponentVOS(node.key);
        if (!target) return Promise.reject();

        // 是否是刚创建还未提交到后端的组件
        const isWaitSubmiting = typeof target?.id !== 'number';

        if (!isWaitSubmiting) {
            const res = await api.deleteComponent({ componentId: target.id! });
            if (res.code !== 1) return Promise.reject();
        }

        const idx = currentCluster!.componentVOS.indexOf(target);
        currentCluster?.componentVOS.splice(idx, 1);
        setCurrent({ ...currentCluster! });

        if (selectedKey === node.key) {
            setSelectKey(undefined);
        }
    };

    const handleTestConnectable = async (): Promise<true | string> => {
        const currentComponent = findComponentVOS(selectedKey);
        if (!currentComponent) {
            message.error('请选择组件');
            return Promise.reject();
        }

        if (editedComponents[selectedKey!]) {
            message.error('当前组件参数变更未保存，请先保存再测试组件连通性');
            return Promise.reject();
        }

        const res = await api.testConnect({
            clusterId: currentCluster!.clusterId,
            componentType: currentComponent.componentTypeCode,
            versionName: currentComponent.versionName ?? '',
        });
        if (res.code === 1) {
            if (res.data.result) {
                setConnectable((c) => ({ ...c, [selectedKey!]: true }));
                return Promise.resolve(true);
            } else {
                setConnectable((c) => ({ ...c, [selectedKey!]: res.data.errorMsg }));
                return Promise.resolve(res.data.errorMsg);
            }
        }

        return Promise.reject();
    };

    const handleSaveComponent = async () => {
        const currentComponent = findComponentVOS(selectedKey);
        if (!currentComponent) return Promise.resolve();
        const { kerberosFileName, principal, ...restValues } = await form.validateFields();
        const versionName = form.getFieldValue('versionName');

        // 上传配置文件所解析出来的配置项会放在 config 字段中，不存在于 values 里
        const xmlConfig = form.getFieldValue('config');

        const componentConfig = xmlConfig ? JSON.stringify(xmlConfig) : JSON.stringify(restValues);

        try {
            setDetailLoading(true);

            // 当修改了 uploadFileName 才需要传该值，否则不传
            const isUploadConfig = typeof form.getFieldValue('uploadFileName') === 'object';

            const res = await api.saveComponent({
                isDefault: currentComponent.isDefault ?? false,
                storeType: currentComponent.storeType ?? '',
                principal: principal ?? '',
                principals,
                versionName: Array.isArray(versionName) ? versionName[versionName.length - 1] : versionName,
                componentConfig,
                deployType: currentComponent.deployType,
                clusterId: currentComponent.clusterId,
                componentCode: currentComponent.componentTypeCode,
                kerberosFileName: kerberosFileName ?? '',
                resources1: isUploadConfig ? form.getFieldValue('uploadFileName') : '',
            });

            if (res.code === 1) {
                message.success('保存成功');

                setEdited((p) => ({ ...p, [selectedKey!]: false }));

                // 更新当前节点
                // 如果是新建的节点，则数据里面不携带 id 字段，所以需要额外赋值
                // 同时由于函数内部需要进行 immutable 的对比，所以需要进行一个不改变引用地址的赋值操作
                getDetailValue(currentComponent, res.data.id);

                // 当前更新的节点所属组件
                const currentComponentOwner = componentsData.find(
                    (component) => component.componentCode === currentComponent.componentTypeCode
                )?.owner;

                // 如果是计算组件的变更，会引起当前应用支持的任务类型的变动
                if (currentComponentOwner === ComponentScheduleKind.Compute) {
                    taskRenderService.getTaskTypes(true);
                }
            }
        } finally {
            setDetailLoading(false);
        }
    };

    const handleUpdateConfig = (params: Record<string, string>, file: RcFile) => {
		form.resetFields(['config']);
        form.setFieldsValue({ config: params, uploadFileName: file });

        setEdited((p) => ({ ...p, [selectedKey!]: true }));
    };

    const handleUpdateKerberos = (file: RcFile) => {
        api.uploadKerberos({
            kerberosFile: file,
            clusterId: currentCluster?.clusterId,
            componentCode: currentTreeNode?.componentCode,
            versionName: form.getFieldValue('versionName'),
        }).then((res) => {
            if (res.code === 1) {
                form.setFieldsValue({
                    kerberosFileName: file.name,
                });
            }
        });

        api.parseKerberos({ fileName: file }).then((res) => {
            if (res.code === 1) {
                // principal 默认选择第一个
                const defaultPrincipal = res.data[0];
                form.setFieldsValue({
                    principal: defaultPrincipal,
                });
                setPrincipals(res.data);
            }
        });
    };

    const handleDeleteKerberos = () => {
        const target = findComponentVOS(selectedKey);
        // 如果未保存到后端的组件，则不存在 id 直接删除文件即可
        if (target?.id) {
            api.closeKerberos({
                componentId: target.id,
            }).then((res) => {
                if (res.code === 1) {
                    form.setFieldsValue({
                        kerberosFileName: undefined,
                    });
                }
            });
        } else {
            form.setFieldsValue({
                kerberosFileName: undefined,
            });
        }
    };

    const handleDownload = (fileType: Valueof<typeof FILE_TYPE>) => {
        const target = findComponentVOS(selectedKey);
        if (!target?.id) {
            message.warn('无法下载，请先保存当前组件后下载');
            return;
        }
        const typeCode = target?.componentTypeCode ?? '';
        const a = document.createElement('a');

        const params = [
            {
                key: 'componentId',
                value: target?.id,
            },
            {
                key: 'type',
                value: fileType,
            },
            {
                key: 'componentType',
                value: typeCode,
            },
            {
                key: 'versionName',
                value: target?.versionName,
            },
            {
                key: 'clusterId',
                value: currentCluster?.clusterId,
            },
        ];

        a.href = `${req.DOWNLOAD_RESOURCE}?${params.map((p) => `${p.key}=${p.value}`).join('&')}`;
        a.click();
        a.remove();
    };

    const handleFormChanged = () => {
        setEdited((e) => ({ ...e, [selectedKey!]: true }));

        const target = findComponentVOS(selectedKey);
        if (target) {
            const { versionName, ...restComponentConfig } = form.getFieldsValue();
            target.componentConfig = JSON.stringify(restComponentConfig);
            if (versionName) {
                target.versionName = Array.isArray(versionName) ? versionName[versionName.length - 1] : versionName;
            }
            setCurrent({ ...currentCluster! });
        }
    };

    useEffect(() => {
        setLoading(true);
        Promise.all([getComponentModels(), getClusterDetail()]).finally(() => {
            setLoading(false);
        });
    }, []);

    useEffect(() => {
        if (selectedKey) {
            form.resetFields();

            const target = findComponentVOS(selectedKey);

            if (target?.versionName) {
                const isCascader = !!currentTreeNode?.versionDictionary?.some((version) =>
                    Array.isArray(version.value)
                );

                form.setFieldsValue({
                    versionName: isCascader
                        ? getTargetPath(currentTreeNode?.versionDictionary || [], target?.versionName)
                        : target?.versionName,
                });
            }

            if (!target?.componentConfig) {
                return;
            }

            let defaultValuesInForm: Record<string, any> = JSON.parse(target.componentConfig);

            if (currentTreeNode?.uploadConfigType) {
                defaultValuesInForm = {
                    config: defaultValuesInForm,
                };
            }

            form.setFieldsValue({
                ...defaultValuesInForm,
                uploadFileName: target?.uploadFileName,
                kerberosFileName: target?.kerberosFileName,
                principal: target?.principal,
            });
        }
    }, [selectedKey]);

    const currentTreeNode = useMemo(() => {
        if (!selectedKey) return undefined;
        return componentsData.find((i) => {
            const [major] = selectedKey.split('-');
            return i.componentCode.toString() === major;
        });
    }, [selectedKey]);

    return (
        <context.Provider
            value={{
                connectable,
                editedComponents,
                principals,
                setConnectable,
                setEdited,
                setPrincipals,
            }}
        >
            <Layout className="cluster-detail" ref={container}>
                <SideBar
                    loading={loading}
                    componentsData={componentsData}
                    selectedNode={selectedKey}
                    currentComponents={currentCluster?.componentVOS}
                    onSelect={handleSidebarSelect}
                    onRemove={handleRemoveComponent}
                    onAddComponent={handleAddComponent}
                />
                <Form form={form} className="detail-container" autoComplete="off" onValuesChange={handleFormChanged}>
                    <Layout key={currentTreeNode?.componentCode}>
                        <Content className="h-full">
                            {selectedKey ? (
                                <Detail
                                    loading={detailLoading}
                                    templateData={templateData}
                                    currentTreeNode={currentTreeNode}
                                    onUploadConfigSuccess={handleUpdateConfig}
                                    onUploadKerberos={handleUpdateKerberos}
                                    onDeleteKerberos={handleDeleteKerberos}
                                    onDownloadKerberos={() => handleDownload(FILE_TYPE.KERNEROS)}
                                    onDownloadConfig={() => handleDownload(FILE_TYPE.CONFIGS)}
                                />
                            ) : (
                                <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
                            )}
                        </Content>
                        <Footer>
                            <Toolbar
                                current={selectedKey}
                                disabled={!selectedKey}
                                onConnection={handleTestConnectable}
                                onSave={handleSaveComponent}
                            />
                        </Footer>
                    </Layout>
                </Form>
            </Layout>
        </context.Provider>
    );
}
