import { useContext, useEffect, useMemo, useState } from 'react';
import { CloudSyncOutlined, ConsoleSqlOutlined, FundViewOutlined, LoadingOutlined } from '@ant-design/icons';
import molecule from '@dtinsight/molecule';
import type { GlobalEvent } from '@dtinsight/molecule/esm/common/event';
import type { AutoCompleteProps, SelectProps } from 'antd';
import { AutoComplete, Form, Input,message, Modal, Select, Tooltip } from 'antd';
import type { InputProps,TextAreaProps } from 'antd/lib/input';
import { omit } from 'lodash';
import md5 from 'md5';

import api from '@/api';
import http from '@/api/http';
import { CATALOGUE_TYPE, DATA_SOURCE_ENUM, DATA_SYNC_MODE, DDL_IDE_PLACEHOLDER, TASK_LANGUAGE } from '@/constant';
import { Context } from '@/context/dataSync';
import type { IDataColumnsProps } from '@/interface';
import { EventKind } from '@/pages/editor/dataSync';
import viewStoreService from '@/services/viewStoreService';
import { convertParams, splitByKey } from '@/utils';
import Editor from '../editor';
import FolderPicker from '../folderPicker';
import PreviewTable from '../previewTable';

interface IBasicFormItemProps<T = any> {
    id: string;
    value: T;
    onChange: (value: T) => void;
    event: GlobalEvent;

    [key: string]: any;
}

interface IOptionsFromRequestFalsy {
    optionsFromRequest?: false;
    options: SelectProps['options'];
}

interface IOptionsFromRequestTruly {
    /**
     * Select 组件的 Options 是否从接口中获取
     */
    optionsFromRequest: true;
    /**
     * 若「是」，则 `url` 和 `method` 为必填项
     */
    url: string;
    method: 'get' | 'post';
    /**
     * 该值定义了接口返回的数据存储在 state 中的健值
     */
    name: string;
    /**
     * 请求的参数
     */
    params?: Record<string, any>;
    /**
     * 该值定义了接口返回的数据需要经过「转化」后，存入 state 中
     * @notice 通常来说，这个「转化」是一段代码逻辑
     */
    transformer?: string;
    /**
     * 定义某一些请求的参数存在的情况下才会去请求
     */
    required?: string[];
}

export type IOptionsFromRequest = IOptionsFromRequestFalsy | IOptionsFromRequestTruly;

export const SelectWithRequest = (props: SelectProps & IOptionsFromRequest) => {
    const form = Form.useFormInstance();
    const { optionCollections, dispatch, transformerFactory } = useContext(Context);
    const [errorMsg, setErrorMsg] = useState('');

    useEffect(() => {
        if (props.optionsFromRequest) {
            const params = convertParams(props.params || {}, form.getFieldsValue());
            const uniqueKey = md5(Object.values(params).join('.') + props.url);
            const currentKey = molecule.editor.getState().current!.activeTab!.toString();
            const storage = viewStoreService.getViewStorage<Record<string, any[]>>(currentKey) || {};

            if (Array.isArray(storage[uniqueKey])) {
                dispatch({
                    type: 'update',
                    payload: {
                        field: props.name,
                        collection: storage[uniqueKey],
                    },
                });
            } else {
                // 确保 required 数组里的每一个值都存在后，才会去请求
                const isRequest = (props.required || []).reduce((pre, cur) => {
                    if (!pre) return false;
                    return params[cur] !== undefined && params[cur] !== null;
                }, true);
                if (!isRequest) return;

                http[props.method]<any[]>(props.url, params).then((res) => {
                    if (res.code === 1) {
                        setErrorMsg('');
                        if (props.transformer && transformerFactory[props.transformer]) {
                            const collection = res.data.map(transformerFactory[props.transformer]);
                            viewStoreService.setViewStorage<Record<string, any>>(currentKey, (pre) => ({
                                ...pre,
                                [uniqueKey]: collection,
                            }));
                            dispatch({
                                type: 'update',
                                payload: {
                                    field: props.name,
                                    collection,
                                },
                            });
                        } else {
                            viewStoreService.setViewStorage<Record<string, any>>(currentKey, (pre) => ({
                                ...pre,
                                [uniqueKey]: res.data,
                            }));
                            dispatch({
                                type: 'update',
                                payload: {
                                    field: props.name,
                                    collection: res.data,
                                },
                            });
                        }
                    } else {
                        setErrorMsg(res.message);
                    }
                });
            }
        }
    }, []);

    const restProps = omit(props, ['name', 'method', 'url', 'optionsFromRequest', 'params', 'transformer']);

    const Content = (
        <Select
            style={{ width: '100%' }}
            getPopupContainer={(node) => node.parentNode}
            optionFilterProp="label"
            showSearch
            showArrow
            allowClear
            options={optionCollections[props.optionsFromRequest ? props.name : '']}
            status={errorMsg && 'warning'}
            {...restProps}
        />
    );

    return errorMsg ? (
        <Tooltip getPopupContainer={(node) => node.parentElement!} title={errorMsg} overlayStyle={{ maxWidth: 500 }}>
            {Content}
        </Tooltip>
    ) : (
        Content
    );
};

export const AutoCompleteWithRequest = (props: AutoCompleteProps & IOptionsFromRequest) => {
    const form = Form.useFormInstance();
    const { transformerFactory, optionCollections, dispatch } = useContext(Context);

    useEffect(() => {
        if (props.optionsFromRequest) {
            const params = convertParams(props.params || {}, form.getFieldsValue());
            const uniqueKey = md5(Object.values(params).join('.') + props.url);
            const currentKey = molecule.editor.getState().current!.activeTab!.toString();
            const storage = viewStoreService.getViewStorage<Record<string, any[]>>(currentKey) || {};

            if (Array.isArray(storage[uniqueKey])) {
                dispatch({
                    type: 'update',
                    payload: {
                        field: props.name,
                        collection: storage[uniqueKey],
                    },
                });
            } else {
                // 确保 required 数组里的每一个值都存在后，才会去请求
                const isRequest = (props.required || []).reduce((pre, cur) => {
                    if (!pre) return false;
                    return params[cur] !== undefined && params[cur] !== null;
                }, true);
                if (!isRequest) return;

                http[props.method]<any[]>(props.url, params).then((res) => {
                    if (res.code === 1) {
                        if (props.transformer && transformerFactory[props.transformer]) {
                            const collection = res.data.map(transformerFactory[props.transformer]);
                            viewStoreService.setViewStorage<Record<string, any>>(currentKey, (pre) => ({
                                ...pre,
                                [uniqueKey]: collection,
                            }));
                            dispatch({
                                type: 'update',
                                payload: {
                                    field: props.name,
                                    collection,
                                },
                            });
                        } else {
                            viewStoreService.setViewStorage<Record<string, any>>(currentKey, (pre) => ({
                                ...pre,
                                [uniqueKey]: res.data,
                            }));
                            dispatch({
                                type: 'update',
                                payload: {
                                    field: props.name,
                                    collection: res.data,
                                },
                            });
                        }
                    }
                });
            }
        }
    }, []);

    const restProps = omit(props, ['method', 'url', 'optionsFromRequest', 'params', 'transformer']);

    const { obj1: inputProps, obj2: autoCompleteProps } = splitByKey<any>(restProps, ['placeholder', 'suffix']);

    return (
        <AutoComplete
            style={{ width: '100%' }}
            getPopupContainer={(node) => node.parentNode}
            optionFilterProp="label"
            showSearch
            showArrow
            options={optionCollections[props.optionsFromRequest ? props.name : '']}
            {...autoCompleteProps}
        >
            <Input {...inputProps} />
        </AutoComplete>
    );
};

/**
 * Select With Create Table Button
 */
export function SelectWithCreate(props: SelectProps & IOptionsFromRequest) {
    const form = Form.useFormInstance();
    const [loading, setLoading] = useState(false);

    const [renderKey, forceRender] = useState(0);
    const [modalInfo, setModalInfo] = useState({ loading: false, visible: false });
    const [editorInfo, setEditorInfo] = useState({ textSql: '', sync: false });

    const handleCreateTable = () => {
        setLoading(true);
        const table = form.getFieldValue(['sourceMap', 'table']);

        const targetTableName = form.getFieldValue(['targetMap', 'table']);

        api.getCreateTargetTable({
            originSourceId: form.getFieldValue(['sourceMap', 'sourceId']),
            tableName: Array.isArray(table) ? table[0] : table,
            partition: form.getFieldValue(['sourceMap', 'partition']),
            originSchema: form.getFieldValue(['sourceMap', 'schema']),
            targetSourceId: form.getFieldValue(['targetMap', 'sourceId']),
            targetSchema: form.getFieldValue(['targetMap', 'schema']),
        })
            .then((res) => {
                if (res.code === 1) {
                    let textSql: string = res.data;
                    if (targetTableName) {
                        const reg = /create\s+table\s+`(.*)`\s*\(/i;
                        textSql = textSql.replace(reg, (match, p1) => match.replace(p1, targetTableName));
                    }
                    setEditorInfo({
                        textSql,
                        sync: true,
                    });
                    setModalInfo({
                        visible: true,
                        loading: false,
                    });
                }
            })
            .finally(() => {
                setLoading(false);
            });
    };

    const createTable = () => {
        setModalInfo({ loading: true, visible: true });
        api.createDdlTable({
            sql: editorInfo.textSql,
            sourceId: form.getFieldValue(['targetMap', 'sourceId']),
        })
            .then((res) => {
                if (res.code === 1) {
                    message.success('表创建成功!');
                    form.setFieldsValue({
                        targetMap: {
                            table: res.data,
                        },
                    });
                    setModalInfo((i) => ({ ...i, visible: false }));
                    // Force re-render select to request options again
                    forceRender((i) => i + 1);
                }
            })
            .finally(() => {
                setModalInfo((i) => ({ ...i, loading: false }));
            });
    };

    const suffixIcon = (
        <Tooltip title="一键生成目标表">
            {loading ? <LoadingOutlined /> : <ConsoleSqlOutlined onClick={handleCreateTable} />}
        </Tooltip>
    );

    return (
        <>
            <SelectWithRequest key={renderKey} {...props} allowClear={false} suffixIcon={suffixIcon} />
            <Modal
                title="建表语句"
                confirmLoading={modalInfo.loading}
                destroyOnClose
                maskClosable={false}
                style={{ height: 424 }}
                width={500}
                visible={modalInfo.visible}
                onCancel={() => setModalInfo({ visible: false, loading: false })}
                onOk={createTable}
            >
                <Editor
                    style={{ height: 400, marginTop: 1 }}
                    language={TASK_LANGUAGE.MYSQL}
                    value={editorInfo.textSql}
                    sync={editorInfo.sync}
                    placeholder={DDL_IDE_PLACEHOLDER}
                    options={{
                        minimap: { enabled: false },
                    }}
                    onChange={(val) =>
                        setEditorInfo({
                            textSql: val,
                            sync: false,
                        })
                    }
                />
            </Modal>
        </>
    );
}

/**
 * Select With Previewer
 */
export function SelectWithPreviewer(props: SelectProps & IOptionsFromRequest) {
    const form = Form.useFormInstance();
    const type: DATA_SOURCE_ENUM = Form.useWatch(['sourceMap', 'type'], form);
    const syncModel: DATA_SYNC_MODE = Form.useWatch(['sourceMap', 'syncModel'], form);
    const table: string[] | string = Form.useWatch(['sourceMap', 'table'], form);

    const [disabled, setDisabled] = useState(false);
    const [loading, setLoading] = useState(false);

    const handlePreviewTable = () => {
        const tableName = Array.isArray(table) ? table[0] : table;
        setLoading(true);
        api.getDataSourcePreview({
            sourceId: form.getFieldValue(['sourceMap', 'sourceId']),
            tableName,
            schema: form.getFieldValue(['sourceMap', 'schema']),
        })
            .then((res) => {
                if (res.code === 1) {
                    const panel = molecule.panel.getPanel(tableName);

                    // Visible the panel
                    if (molecule.layout.getState().panel.hidden) {
                        molecule.layout.togglePanelVisibility();
                    }

                    if (panel) {
                        molecule.panel.setActive(tableName);
                    } else {
                        molecule.panel.add({
                            id: tableName,
                            name: `${tableName} 数据预览`,
                            closable: true,
                            data: {
                                ...res.data,
                            },
                            renderPane: (item) => <PreviewTable data={item.data} />,
                        });
                        molecule.panel.setActive(tableName);
                    }
                }
            })
            .finally(() => {
                setLoading(false);
            });
    };

    const isIncrement = useMemo(() => syncModel === DATA_SYNC_MODE.INCREMENT, [syncModel]);
    const isSupportSubLibrary = useMemo(
        () => [DATA_SOURCE_ENUM.MYSQL, DATA_SOURCE_ENUM.SQLSERVER].includes(type),
        [type]
    );

    const mode = useMemo(() => {
        if (!isIncrement && isSupportSubLibrary) return 'multiple';
        return undefined;
    }, [isIncrement, isSupportSubLibrary]);

    useEffect(() => {
        setDisabled(Array.isArray(table) ? !table.length : !table);
    }, [table]);

    const suffixIcon = (
        <Tooltip title="数据预览">
            {loading ? (
                <LoadingOutlined />
            ) : (
                <FundViewOutlined
                    style={{ pointerEvents: disabled ? 'none' : 'auto' }}
                    onClick={() => handlePreviewTable()}
                />
            )}
        </Tooltip>
    );

    return <SelectWithRequest {...props} mode={mode} allowClear={false} suffixIcon={suffixIcon} />;
}

export function TextareaWithJSONValidator(props: TextAreaProps) {
    return <Input.TextArea {...props} />;
}

export function ResourcePicker(props: IBasicFormItemProps) {
    return <FolderPicker dataType={CATALOGUE_TYPE.RESOURCE} showFile {...props} />;
}

/**
 * Input with get columns
 * @notice used in ftp path field
 */
export function InputWithColumns(props: InputProps & IBasicFormItemProps) {
    const form = Form.useFormInstance();
    const [loading, setLoading] = useState(false);

    const currentType = form.getFieldValue(['sourceMap', 'type']);

    const handleSyncFTPColumns = () => {
        if (props.value) {
            setLoading(true);
            api.getFTPColumns<{ column: IDataColumnsProps[] }>({
                filepath: props.value,
                columnSeparator: form.getFieldValue(['sourceMap', 'fieldDelimiter|FTP']),
                firstColumnName: form.getFieldValue(['sourceMap', 'isFirstLineHeader']),
                sourceId: form.getFieldValue(['sourceMap', 'sourceId']),
                encoding: form.getFieldValue(['sourceMap', 'encoding']),
            })
                .then((res) => {
                    if (res.code === 1) {
                        props.event.emit(EventKind.SourceKeyChange, res.data.column || []);
                    }
                })
                .finally(() => {
                    setLoading(false);
                });
        } else {
            // for calling the validator
            props.onChange(undefined);
        }
    };

    const suffix = (
        <Tooltip title="获取 FTP 文件列">
            {loading ? <LoadingOutlined /> : <CloudSyncOutlined onClick={handleSyncFTPColumns} />}
        </Tooltip>
    );

    // Only FTP source has suffix
    return <Input {...props} suffix={currentType === DATA_SOURCE_ENUM.FTP && suffix} />;
}
