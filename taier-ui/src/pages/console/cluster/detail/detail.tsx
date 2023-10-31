import { useContext, useMemo, useState } from 'react';
import {
    CaretRightOutlined,
    DeleteOutlined,
    DownloadOutlined,
    LeftOutlined,
    PaperClipOutlined,
    RightOutlined,
    UploadOutlined,
} from '@ant-design/icons';
import type { FormItemProps } from 'antd';
import {
    Button,
    Cascader,
    Checkbox,
    Collapse,
    Form,
    Input,
    Layout,
    message,
    Popconfirm,
    Radio,
    Select,
    Space,
    Spin,
    Tooltip,
    Upload,
} from 'antd';
import type { RcFile } from 'antd/lib/upload';

import api from '@/api';
import { COMPONENT_TYPE_VALUE } from '@/constant';
import context from '@/context/cluster';
import type { IComponentProps } from '.';
import './detail.scss';

const { Sider, Content } = Layout;
const { Panel } = Collapse;

interface IDetailProps {
    templateData: ILayoutData[];
    currentTreeNode?: IComponentProps;
    loading?: boolean;
    onUploadConfigSuccess?: (params: Record<string, string>, file: RcFile) => void;
    onUploadKerberos?: (file: RcFile) => void;
    onDeleteKerberos?: () => void;
    onDownloadKerberos?: () => void;
    onDownloadConfig?: () => void;
}

export interface ILayoutData extends Omit<FormItemProps<any>, 'id'> {
    id: number;
    type: ITemplateData['type'];
    componentProps?: any;
    /**
     * 当存在 dependencies 时，需要指定当 dependencies === [dependencyValue] 的时候渲染当前组件
     */
    dependencyValue?: string;
}

/**
 * 服务端接口类型
 */
export interface ITemplateData {
    /**
     * 当前字段依赖的字段的 key 值
     */
    dependencyKey?: string;
    /**
     * 当前字段依赖字段的 value 值
     */
    dependencyValue?: string;
    id: number;
    key: string;
    /**
     * Tooltips 内容
     */
    keyDescribe?: string;
    required: boolean;
    /**
     * XML 表示该值作为 config 渲染
     */
    type: 'INPUT' | 'RADIO_LINKAGE' | 'CHECKBOX' | 'XML' | 'GROUP';
    value: string;
}

export default function Detail({
    loading,
    currentTreeNode,
    templateData = [],
    onUploadConfigSuccess,
    onUploadKerberos,
    onDeleteKerberos,
    onDownloadKerberos,
    onDownloadConfig,
}: IDetailProps) {
    const { principals } = useContext(context);
    const [collapsed, setCollapsed] = useState(false);

    const handleUploadConfig = (file: RcFile) => {
        api.uploadResource({
            fileName: file,
            componentType: currentTreeNode!.componentCode,
        }).then((res) => {
            if (res.code === 1) {
                message.success('文件上传成功');
                onUploadConfigSuccess?.(res.data[0], file);
            }
        });
        return false;
    };

    const hanldeUploadKerberos = (file: RcFile) => {
        onUploadKerberos?.(file);
        return false;
    };

    const renderContent = (type: ILayoutData['type'], componentProps?: any) => {
        switch (type.toLocaleUpperCase()) {
            case 'INPUT':
                return <Input />;
            case 'PASSWORD':
                return <Input.Password />;
            case 'RADIO_LINKAGE':
                return <Radio.Group {...componentProps} />;
            case 'CHECKBOX':
                return <Checkbox.Group {...componentProps} />;
            case 'SELECT':
                return <Select {...componentProps} />;
            default:
                break;
        }
    };

    const renderDescription = (code?: COMPONENT_TYPE_VALUE) => {
        switch (code) {
            case COMPONENT_TYPE_VALUE.HDFS:
                return 'zip格式，至少包括yarn-site.xml、hdfs-site.xml和core-site.xml';
            default:
                return 'zip格式，至少包括yarn-site.xml和core-site.xml';
        }
    };

    const renderVersionPicker = () => {
        const isCascader = !!currentTreeNode?.versionDictionary?.some((version) => Array.isArray(version.value));
        if (!currentTreeNode) return;

        return (
            <Form.Item
                className="sider-formItem"
                name="versionName"
                label="组件版本"
                colon={false}
                labelCol={{ span: 24 }}
                wrapperCol={{ span: 24 }}
            >
                {isCascader ? (
                    <Cascader
                        displayRender={(label) => label[label.length - 1]}
                        allowClear={false}
                        options={currentTreeNode.versionDictionary!}
                        fieldNames={{ label: 'key', value: 'key', children: 'value' }}
                        placeholder="请选择组件版本"
                    />
                ) : (
                    <Select
                        options={currentTreeNode.versionDictionary!.map((item) => ({
                            label: item.key,
                            value: item.key,
                        }))}
                        placeholder="请选择组件版本"
                        allowClear={false}
                    />
                )}
            </Form.Item>
        );
    };

    const configFormItem = Boolean(currentTreeNode?.uploadConfigType) && (
        <Form.Item
            className="sider-formItem"
            label="配置文件"
            colon={false}
            labelCol={{ span: 24 }}
            wrapperCol={{ span: 24 }}
        >
            <Upload accept=".zip" fileList={[]} beforeUpload={handleUploadConfig}>
                <Button block icon={<UploadOutlined />}>
                    点击上传
                </Button>
                <span className="description">{renderDescription(currentTreeNode?.componentCode)}</span>
            </Upload>
            <Form.Item noStyle name="uploadFileName">
                <FileCol actionSlot={['download']} onDownload={onDownloadConfig} />
            </Form.Item>
        </Form.Item>
    );

    const kerberosFormItem = Boolean(currentTreeNode?.allowKerberos) && (
        <>
            <Form.Item
                className="sider-formItem"
                label="Hadoop Kerberos认证文件"
                colon={false}
                labelCol={{ span: 24 }}
                wrapperCol={{ span: 24 }}
            >
                <Upload accept=".zip" fileList={[]} beforeUpload={hanldeUploadKerberos}>
                    <Button block icon={<UploadOutlined />}>
                        点击上传
                    </Button>
                    <span className="description">仅支持.zip格式</span>
                </Upload>
                <Form.Item noStyle name="kerberosFileName">
                    <FileCol
                        actionSlot={['download', 'delete']}
                        onDelete={onDeleteKerberos}
                        onDownload={onDownloadKerberos}
                    />
                </Form.Item>
            </Form.Item>
            {Boolean(principals.length) && (
                <Form.Item
                    className="sider-formItem"
                    colon={false}
                    labelCol={{ span: 24 }}
                    wrapperCol={{ span: 24 }}
                    name="principal"
                    label="principal"
                >
                    <Select
                        options={principals.map((p) => ({
                            label: p,
                            value: p,
                        }))}
                    />
                </Form.Item>
            )}
        </>
    );

    const versionFormItem =
        currentTreeNode?.allowCoexistence === false &&
        currentTreeNode.versionDictionary !== null &&
        renderVersionPicker();

    const isEmptySider = !configFormItem && !kerberosFormItem && !versionFormItem;

    /**
     * 把 templateData 的数据做一次 Group 收集
     */
    const templateCollections = useMemo(() => {
        const res: (
            | { type: 'NON_GROUP'; children: ILayoutData[] }
            | {
                  type: 'GROUP';
                  groupName: string;
                  children: ILayoutData[];
              }
        )[] = [];
        templateData.forEach((template) => {
            if (Array.isArray(template.name)) {
                const groupName = template.name[0]!.toString();

                const target = res.find((i) => i.type === 'GROUP' && i.groupName === groupName);
                if (target) {
                    target.children.push(template);
                } else {
                    res.push({
                        type: 'GROUP',
                        groupName,
                        children: [template],
                    });
                }
            } else {
                res.push({ type: 'NON_GROUP', children: [template] });
            }
        });

        return res;
    }, [templateData]);

    return (
        <Spin spinning={loading}>
            <Layout className="detail-content">
                {!isEmptySider && (
                    <Sider collapsed={collapsed} className="detail-sider">
                        <div className="detail-sider-container">
                            {versionFormItem}
                            {configFormItem}
                            {kerberosFormItem}
                        </div>
                        <Button className="cluster-component-collapse" onClick={() => setCollapsed((c) => !c)}>
                            {collapsed ? <RightOutlined /> : <LeftOutlined />}
                        </Button>
                    </Sider>
                )}
                <Content className="detail-content">
                    {templateCollections.map((template) => {
                        if (template.type === 'NON_GROUP') {
                            const { id, componentProps, type, label, ...restProps } = template.children[0];
                            return (
                                <Form.Item
                                    key={id}
                                    labelCol={{ span: 8 }}
                                    wrapperCol={{ span: 12 }}
                                    label={
                                        <Tooltip title={label}>
                                            <span className="formitem-config-text">{label}</span>
                                        </Tooltip>
                                    }
                                    {...restProps}
                                >
                                    {renderContent(type, componentProps)}
                                </Form.Item>
                            );
                        }

                        return (
                            <Collapse
                                key={template.groupName}
                                bordered={false}
                                className="cluster__detail__collapse"
                                defaultActiveKey={[template.groupName]}
                                expandIcon={({ isActive }) => <CaretRightOutlined rotate={isActive ? 90 : 0} />}
                            >
                                <Panel header={template.groupName} key={template.groupName}>
                                    {template.children.map(
                                        ({
                                            id,
                                            componentProps,
                                            type,
                                            dependencyValue,
                                            label,
                                            dependencies,
                                            ...formItemProps
                                        }) => {
                                            return (
                                                <Form.Item
                                                    key={id}
                                                    noStyle
                                                    requiredMark={formItemProps.required}
                                                    dependencies={dependencies}
                                                >
                                                    {({ getFieldValue }) => {
                                                        const value = getFieldValue(dependencies![0]);
                                                        // 如果是多选框的话，value 是一个数组，判断数组是否 include 关键值
                                                        const isShow = Array.isArray(value)
                                                            ? value.includes(dependencyValue)
                                                            : value === dependencyValue;
                                                        return (
                                                            isShow && (
                                                                <Form.Item
                                                                    labelCol={{ span: 8 }}
                                                                    wrapperCol={{ span: 12 }}
                                                                    label={
                                                                        <Tooltip title={label}>
                                                                            <span className="formitem-config-text">
                                                                                {label}
                                                                            </span>
                                                                        </Tooltip>
                                                                    }
                                                                    {...formItemProps}
                                                                >
                                                                    {renderContent(type, componentProps)}
                                                                </Form.Item>
                                                            )
                                                        );
                                                    }}
                                                </Form.Item>
                                            );
                                        }
                                    )}
                                </Panel>
                            </Collapse>
                        );
                    })}

                    <Form.Item dependencies={['config']} noStyle>
                        {({ getFieldValue }) => {
                            const fieldValue = getFieldValue('config');
                            if (!fieldValue) return null;
                            return Object.keys(fieldValue).map((key) => {
                                return (
                                    <Form.Item
                                        label={
                                            <Tooltip title={key}>
                                                <span className="formitem-config-text">{key}</span>
                                            </Tooltip>
                                        }
                                        key={key}
                                        labelCol={{ span: 8 }}
                                        wrapperCol={{ span: 12 }}
                                    >
                                        <Input value={fieldValue[key]} />
                                    </Form.Item>
                                );
                            });
                        }}
                    </Form.Item>
                </Content>
            </Layout>
        </Spin>
    );
}

function FileCol({
    value,
    actionSlot = [],
    onDelete,
    onDownload,
}: {
    value?: string | RcFile;
    actionSlot?: ('delete' | 'download')[];
    onDelete?: () => void;
    onDownload?: () => void;
}) {
    const isFileName = typeof value === 'string';

    const slots = {
        delete: (
            <Popconfirm
                title={`确认删除 ${isFileName ? value : value?.name} 文件?`}
                onConfirm={onDelete}
                okText="确认"
                cancelText="取消"
                key="delete"
            >
                <DeleteOutlined />
            </Popconfirm>
        ),
        download: <DownloadOutlined onClick={onDownload} key="download" />,
    };

    return value ? (
        <div className="cluster-file-col">
            <PaperClipOutlined />
            <Tooltip title={isFileName ? value : value.name} placement="topLeft">
                <span className="cluster-file-name">{isFileName ? value : value.name}</span>
            </Tooltip>
            <div className="cluster-file-col-actions">
                <Space size={2}>{actionSlot.map((slot) => slots[slot])}</Space>
            </div>
        </div>
    ) : null;
}
