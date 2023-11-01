import { useEffect, useMemo, useRef, useState } from 'react';
import { EditOutlined, ExclamationCircleOutlined, MinusOutlined } from '@ant-design/icons';
import molecule from '@dtinsight/molecule';
import { Button, Col, Form, message, Row, Space, Spin, Tooltip } from 'antd';
import type { ColumnType } from 'antd/lib/table';
import md5 from 'md5';

import api from '@/api';
import { useSize } from '@/components/customHooks';
import LintTo from '@/components/lineTo';
import { DATA_SOURCE_ENUM, DATA_SOURCE_TEXT } from '@/constant';
import type { IDataColumnsProps } from '@/interface';
import viewStoreService from '@/services/viewStoreService';
import { checkExist, isValidFormatType } from '@/utils';
import KeyModal from './modals/keyModal';
import TableFooter from './tableFooter';
import { event, EventKind, updateValuesInData } from '.';
import './keyMap.scss';

enum OperatorKind {
    ADD,
    REMOVE,
    EDIT,
    // replace is different from edit, which is change the whole columns
    REPLACE,
}

function getUniqueKey(data: IDataColumnsProps) {
    return `${getRecordKey(data)}-${data.type}`;
}

/**
 * Generally, the record key field is 「key」, but in FTP it's 「index」
 */
function getRecordKey(data: IDataColumnsProps) {
    return data.index ?? data.key;
}

enum QuickColumnKind {
    /**
     * 同行映射
     */
    ROW_MAP,
    /**
     * 同名映射
     */
    NAME_MAP,
    /**
     * 拷贝源字段
     */
    COPY_SOURCE,
    /**
     * 拷贝目标字段
     */
    COPY_TARGET,
    /**
     * 重置
     */
    RESET,
}

function getSourceColumn(
    type: DATA_SOURCE_ENUM,
    removeAction: (record: IDataColumnsProps) => JSX.Element,
    editAction: (record: IDataColumnsProps) => JSX.Element
): ColumnType<IDataColumnsProps>[] {
    switch (type) {
        case DATA_SOURCE_ENUM.HDFS:
        case DATA_SOURCE_ENUM.S3:
        case DATA_SOURCE_ENUM.FTP:
            return [
                {
                    title: '索引位',
                    dataIndex: 'index',
                    key: 'index',
                    ellipsis: true,
                    render(text, record) {
                        const formatVal = record.value ? `'${record.key}'` : record.key;
                        return <Tooltip title={text ?? formatVal}>{text ?? formatVal}</Tooltip>;
                    },
                },
                {
                    title: '类型',
                    dataIndex: 'type',
                    key: 'type',
                    ellipsis: true,
                    render(text, record) {
                        const val = record.value
                            ? `常量(${record.type})`
                            : `${text ? text.toUpperCase() : ''}${record.format ? `(${record.format})` : ''}`;
                        return <Tooltip title={val}>{val}</Tooltip>;
                    },
                },
                {
                    title: '操作',
                    key: 'action',
                    render(_, record) {
                        return (
                            <Space>
                                {removeAction(record)}
                                {editAction(record)}
                            </Space>
                        );
                    },
                },
            ];
        case DATA_SOURCE_ENUM.HBASE:
            return [
                {
                    title: '列名/行健',
                    dataIndex: 'value',
                    key: 'value',
                    ellipsis: true,
                    render(text, record) {
                        const val = text ? `'${record.key}'` : record.key;
                        return (
                            <Tooltip title={val}>
                                {val}({record.cf})
                            </Tooltip>
                        );
                    },
                },
                {
                    title: '类型',
                    dataIndex: 'type',
                    key: 'type',
                    ellipsis: true,
                    render(text, record) {
                        const val = record.value
                            ? `常量(${record.type})`
                            : `${text ? text.toUpperCase() : ''}${record.format ? `(${record.format})` : ''}`;
                        return <Tooltip title={val}>{val}</Tooltip>;
                    },
                },
                {
                    title: '操作',
                    key: 'action',
                    render(_, record) {
                        // 仅允许常量删除操作
                        return (
                            <Space>
                                {record.key !== 'rowKey' && removeAction(record)}
                                {editAction(record)}
                            </Space>
                        );
                    },
                },
            ];
        case DATA_SOURCE_ENUM.HIVE1X:
        case DATA_SOURCE_ENUM.HIVE:
        case DATA_SOURCE_ENUM.HIVE3X:
            return [
                {
                    title: '字段名称',
                    dataIndex: 'value',
                    key: 'value',
                    ellipsis: true,
                    render(text, record) {
                        return (
                            <Tooltip title={text ? `'${record.key}'` : record.key}>
                                {text ? `'${record.key}'` : record.key}
                            </Tooltip>
                        );
                    },
                },
                {
                    title: '类型',
                    dataIndex: 'type',
                    key: 'type',
                    ellipsis: true,
                    render(text, record) {
                        const title = record.value
                            ? `常量(${record.type})`
                            : `${text ? text.toUpperCase() : ''}${record.format ? `(${record.format})` : ''}`;
                        return (
                            <>
                                <Tooltip title={title}>{title}</Tooltip>
                                {record.isPart && <img src="images/primary-key.svg" />}
                            </>
                        );
                    },
                },
                {
                    title: '操作',
                    key: 'action',
                    render(_, record) {
                        return (
                            <Space>
                                {record.value && removeAction(record)}
                                {editAction(record)}
                            </Space>
                        );
                    },
                },
            ];
        default:
            return [
                {
                    title: '字段名称',
                    dataIndex: 'value',
                    key: 'value',
                    ellipsis: true,
                    render(text, record) {
                        return (
                            <Tooltip title={text ? `'${record.key}'` : record.key}>
                                {text ? `'${record.key}'` : record.key}
                            </Tooltip>
                        );
                    },
                },
                {
                    title: '类型',
                    dataIndex: 'type',
                    key: 'type',
                    ellipsis: true,
                    render(text, record) {
                        const title = record.value
                            ? `常量(${record.type})`
                            : `${text ? text.toUpperCase() : ''}${record.format ? `(${record.format})` : ''}`;
                        return (
                            <>
                                <Tooltip title={title}>{title}</Tooltip>
                                {record.isPart && <img src="images/primary-key.svg" />}
                            </>
                        );
                    },
                },
                {
                    title: '操作',
                    key: 'action',
                    render(_, record) {
                        // 常量都允许删除和编辑
                        return (
                            <Space>
                                {record.value && removeAction(record)}
                                {(isValidFormatType(record.type) || record.value) && editAction(record)}
                            </Space>
                        );
                    },
                },
            ];
    }
}

/**
 * Render the columns for target
 */
function getTargetColumn(
    type: DATA_SOURCE_ENUM,
    removeAction: (record: IDataColumnsProps) => JSX.Element,
    editAction: (record: IDataColumnsProps) => JSX.Element
): ColumnType<IDataColumnsProps>[] {
    switch (type) {
        // Shows the 「key」 in FTP but NOT shows the 「index」
        case DATA_SOURCE_ENUM.FTP:
        case DATA_SOURCE_ENUM.HDFS:
        case DATA_SOURCE_ENUM.S3:
            return [
                {
                    title: '字段名称',
                    dataIndex: 'key',
                    key: 'key',
                    ellipsis: true,
                    render(text) {
                        return <Tooltip title={text}>{text}</Tooltip>;
                    },
                },
                {
                    title: '类型',
                    dataIndex: 'type',
                    key: 'type',
                    ellipsis: true,
                    render(text, record) {
                        const val = `${text.toUpperCase()}${record.isPart ? '(分区字段)' : ''}`;
                        return <Tooltip title={val}>{val}</Tooltip>;
                    },
                },
                {
                    title: '操作',
                    key: 'action',
                    render(_, record) {
                        return (
                            <Space>
                                {removeAction(record)}
                                {editAction(record)}
                            </Space>
                        );
                    },
                },
            ];
        case DATA_SOURCE_ENUM.HBASE:
            return [
                {
                    title: '列名',
                    dataIndex: 'key',
                    key: 'key',
                    ellipsis: true,
                    render(text, record) {
                        const val = `${text}(${record.cf})`;
                        return <Tooltip title={val}>{val}</Tooltip>;
                    },
                },
                {
                    title: '类型',
                    dataIndex: 'type',
                    key: 'type',
                    ellipsis: true,
                    render(text, record) {
                        const val = `${text.toUpperCase()}${record.isPart ? '(分区字段)' : ''}`;
                        return <Tooltip title={val}>{val}</Tooltip>;
                    },
                },
                {
                    title: '操作',
                    key: 'action',
                    render(_, record) {
                        return (
                            <Space>
                                {removeAction(record)}
                                {editAction(record)}
                            </Space>
                        );
                    },
                },
            ];
        default:
            return [
                {
                    title: '字段名称',
                    dataIndex: 'key',
                    key: 'key',
                    ellipsis: true,
                    render(text) {
                        return <Tooltip title={text}>{text}</Tooltip>;
                    },
                },
                {
                    title: '类型',
                    dataIndex: 'type',
                    key: 'type',
                    ellipsis: true,
                    render(text, record) {
                        const val = `${text.toUpperCase()}${record.isPart ? '(分区字段)' : ''}`;
                        return <Tooltip title={val}>{val}</Tooltip>;
                    },
                },
                {
                    title: '操作',
                    key: 'action',
                    render() {
                        return null;
                    },
                },
            ];
    }
}

export default function KeyMap() {
    const form = Form.useFormInstance();
    const container = useRef<HTMLDivElement>(null);
    const selection = useRef<LintTo<IDataColumnsProps>>();
    // 后端接口获取到的表格列
    const source = useColumns('sourceMap');
    const target = useColumns('targetMap');
    // 连线的数据
    const [sourceCol, targetCol] = useFormColumns();
    const { width } = useSize('taier__keyMap__container');

    const [keyModal, setKeyModal] = useState<{
        visible: boolean;
        isReader: boolean;
        editField: IDataColumnsProps | undefined;
        operation: OperatorKind;
    }>({
        visible: false,
        // 区分源表还是目标表
        isReader: false,
        editField: undefined,
        operation: OperatorKind.ADD,
    });

    const fetching = useMemo(() => source.fetching || target.fetching, [source.fetching, target.fetching]);

    /**
     * Make sure the source of reader or writer has been selected
     */
    const assertSource = (isReader: boolean) => {
        const type = form.getFieldValue([isReader ? 'sourceMap' : 'targetMap', 'type']);
        if (type === undefined) {
            message.warn('请先选择数据源');
            return false;
        }

        return true;
    };

    const handleOpenKeyModal = (record: IDataColumnsProps, isReader: boolean) => {
        setKeyModal({
            visible: true,
            isReader,
            editField: record,
            operation: OperatorKind.EDIT,
        });
    };

    const handleAddVariable = (isReader: boolean) => {
        if (assertSource(isReader)) {
            setKeyModal({
                visible: true,
                isReader,
                editField: undefined,
                operation: OperatorKind.ADD,
            });
        }
    };

    const handleSetColumns = (kind: QuickColumnKind) => {
        switch (kind) {
            case QuickColumnKind.ROW_MAP: {
                const length = Math.min(sourceColumns.length, targetColumns.length);

                form.setFieldsValue({
                    sourceMap: {
                        column: sourceColumns.slice(0, length),
                    },
                    targetMap: {
                        column: targetColumns.slice(0, length),
                    },
                });
                updateValuesInData(form.getFieldsValue());
                break;
            }

            case QuickColumnKind.COPY_SOURCE: {
                if (assertSource(false)) {
                    const targetCols = sourceColumns.filter(
                        (col) => !targetColumns.find((tCol) => getUniqueKey(tCol) === getUniqueKey(col))
                    );
                    target.dispatch({
                        type: OperatorKind.ADD,
                        payload: targetCols,
                    });
                }
                break;
            }

            case QuickColumnKind.COPY_TARGET: {
                if (assertSource(true)) {
                    const sourceCols = targetColumns.filter(
                        (col) => !sourceColumns.find((tCol) => getUniqueKey(tCol) === getUniqueKey(col))
                    );
                    source.dispatch({
                        type: OperatorKind.ADD,
                        payload: sourceCols,
                    });
                }
                break;
            }

            case QuickColumnKind.NAME_MAP: {
                const sourceCols: IDataColumnsProps[] = [];
                const targetCols: IDataColumnsProps[] = [];
                sourceColumns.forEach((o) => {
                    const name = getRecordKey(o).toString().toUpperCase();
                    const idx = targetColumns.findIndex((col) => {
                        const sourceName = getRecordKey(col).toString().toUpperCase();
                        return sourceName === name;
                    });
                    if (idx !== -1) {
                        sourceCols.push(o);
                        targetCols.push(targetColumns[idx]);
                    }
                });
                if (sourceCols.length) {
                    form.setFieldsValue({
                        sourceMap: {
                            column: sourceCols,
                        },
                        targetMap: {
                            column: targetCols,
                        },
                    });
                    updateValuesInData(form.getFieldsValue());
                } else {
                    message.warning('未找到同名字段');
                }
                break;
            }

            case QuickColumnKind.RESET: {
                form.setFieldsValue({
                    sourceMap: {
                        column: [],
                    },
                    targetMap: {
                        column: [],
                    },
                });
                updateValuesInData(form.getFieldsValue());
                break;
            }

            default:
                break;
        }
    };

    const handleColChanged = (action: OperatorKind, columns: IDataColumnsProps[], kind: 'source' | 'target') => {
        (kind === 'source' ? source : target).dispatch({
            type: action,
            payload: columns,
        });
    };

    useEffect(() => {
        selection.current = new LintTo(container.current!, {
            rowKey: getRecordKey,
            onRenderColumns(s) {
                return s
                    ? getSourceColumn(
                          form.getFieldValue(['sourceMap', 'type']),
                          (record: IDataColumnsProps) => (
                              <Tooltip title="删除当前列">
                                  <MinusOutlined
                                      onClick={() => handleColChanged(OperatorKind.REMOVE, [record], 'source')}
                                  />
                              </Tooltip>
                          ),
                          (record: IDataColumnsProps) => (
                              <Tooltip title="编辑当前列">
                                  <EditOutlined onClick={() => handleOpenKeyModal(record, true)} />
                              </Tooltip>
                          )
                      )
                    : getTargetColumn(
                          form.getFieldValue(['targetMap', 'type']),
                          (record: IDataColumnsProps) => (
                              <Tooltip title="删除当前列">
                                  <MinusOutlined
                                      onClick={() => handleColChanged(OperatorKind.REMOVE, [record], 'target')}
                                  />
                              </Tooltip>
                          ),
                          (record: IDataColumnsProps) => (
                              <Tooltip title="编辑当前列">
                                  <EditOutlined onClick={() => handleOpenKeyModal(record, false)} />
                              </Tooltip>
                          )
                      );
            },
            onRenderFooter(s) {
                return (
                    <TableFooter
                        type={form.getFieldValue([s ? 'sourceMap' : 'targetMap', 'type'])}
                        source={s}
                        onConstModalConfirm={(col) =>
                            assertSource(s) && handleColChanged(OperatorKind.ADD, [col], s ? 'source' : 'target')
                        }
                        onAddFieldClick={() => handleAddVariable(s)}
                    />
                );
            },
            onDragStart(data) {
                const column: IDataColumnsProps[] = form.getFieldValue(['sourceMap', 'column']) || [];

                return !column.find((i) => getUniqueKey(i) === getUniqueKey(data));
            },

            onDrop(data) {
                const column: IDataColumnsProps[] = form.getFieldValue(['targetMap', 'column']) || [];

                return !column.find((i) => getUniqueKey(i) === getUniqueKey(data));
            },

            onLineChanged(sourceLine, targetLine) {
                const sCol = form.getFieldValue(['sourceMap', 'column']) || [];
                const tCol = form.getFieldValue(['targetMap', 'column']) || [];

                form.setFieldsValue({
                    sourceMap: {
                        column: [...sCol, sourceLine],
                    },
                    targetMap: {
                        column: [...tCol, targetLine],
                    },
                });

                updateValuesInData(form.getFieldsValue());
            },

            onLineClick(sourceLine, targetLine) {
                const sCol: IDataColumnsProps[] = form.getFieldValue(['sourceMap', 'column']) || [];
                const tCol: IDataColumnsProps[] = form.getFieldValue(['targetMap', 'column']) || [];

                const nextSourceCol = sCol.filter((col) => getUniqueKey(col) !== getUniqueKey(sourceLine));
                const nextTargetCol = tCol.filter((col) => getUniqueKey(col) !== getUniqueKey(targetLine));
                form.setFieldsValue({
                    sourceMap: {
                        column: nextSourceCol,
                    },
                    targetMap: {
                        column: nextTargetCol,
                    },
                });

                updateValuesInData(form.getFieldsValue());
            },
        });
    }, []);

    // Current columns are comprised of the columns from request.tablelist and the columns from form.xxxMap.column
    // The reason is when there are some user-defined columns only could get from form.xxxMap.column
    const sourceColumns = useMemo(() => {
        return sourceCol.reduce<IDataColumnsProps[]>((pre, cur) => {
            if (!pre.find((i) => getUniqueKey(i) === getUniqueKey(cur))) {
                pre.push(cur);
            }
            return pre;
        }, source.data.concat());
    }, [sourceCol, source.data]);

    const targetColumns = useMemo(() => {
        return targetCol.reduce<IDataColumnsProps[]>((pre, cur) => {
            if (!pre.find((i) => getUniqueKey(i) === getUniqueKey(cur))) {
                pre.push(cur);
            }
            return pre;
        }, target.data.concat());
    }, [targetCol, target.data]);

    // Re-render svg when columns changed
    useEffect(() => {
        selection.current?.setSourceData(sourceColumns);
        selection.current?.setTargetData(targetColumns);
        selection.current?.render();
    }, [sourceColumns, targetColumns]);

    // Re-render svg when form.xxxMap.column changed
    useEffect(() => {
        selection.current?.setLine(sourceCol.map((s, idx) => ({ from: s, to: targetCol[idx] })));
        selection.current?.render();
    }, [sourceCol, targetCol]);

    // Re-render svg when width changed
    useEffect(() => {
        selection.current?.render();
    }, [width]);

    // The table field changed no matter in sourceMap or in targetMap will reset column's value
    // And cause the re-render of svg indirectly
    useEffect(() => {
        function listener(field: string[]) {
            if (field.join('.') === 'sourceMap.table' || field.join('.') === 'targetMap.table') {
                form.setFieldsValue({
                    sourceMap: {
                        column: [],
                    },
                    targetMap: {
                        column: [],
                    },
                });
            }
        }
        event.subscribe(EventKind.Changed, listener);

        return () => {
            event.unsubscribe(EventKind.Changed, listener);
        };
    }, []);

    const { keyModalTitle, keyModalType } = useMemo(() => {
        const type: DATA_SOURCE_ENUM = form.getFieldValue([keyModal.isReader ? 'sourceMap' : 'targetMap', 'type']);

        if (keyModal.visible && type) {
            return {
                keyModalTitle: `${keyModal.operation === OperatorKind.ADD ? '添加' : '编辑'}${
                    DATA_SOURCE_TEXT[type]
                }字段`,
                keyModalType: type,
            };
        }

        return {
            keyModalTitle: '',
            keyModalType: type,
        };
    }, [keyModal]);

    const errorMsg = useMemo(() => source.errorMsg || target.errorMsg, [source.errorMsg, target.errorMsg]);

    return (
        <div className="taier__keyMap__container">
            <p className="text-center">
                {errorMsg && (
                    <Tooltip title={errorMsg}>
                        <ExclamationCircleOutlined
                            style={{
                                color: 'var(--problemsWarningIcon-foreground)',
                                marginRight: 5,
                            }}
                        />
                    </Tooltip>
                )}
                您要配置来源表与目标表的字段映射关系，通过连线将待同步的字段左右相连，也可以通过同行映射、同名映射批量完成映射
            </p>
            <Form.Item noStyle name={['sourceMap', 'column']} rules={[{ required: true }]} />
            <Form.Item noStyle name={['targetMap', 'column']} rules={[{ required: true }]} />
            <Spin spinning={fetching}>
                <Row gutter={12}>
                    <Col span={21}>
                        <div ref={container} />
                    </Col>
                    <Col span={3}>
                        <Space direction="vertical" size={5} className="taier__dataSync__keyMap__buttonGroups">
                            <Button onClick={() => handleSetColumns(QuickColumnKind.ROW_MAP)} type="primary" block>
                                同行映射
                            </Button>
                            <Button onClick={() => handleSetColumns(QuickColumnKind.NAME_MAP)} type="primary" block>
                                同名映射
                            </Button>
                            {[
                                DATA_SOURCE_ENUM.HDFS,
                                DATA_SOURCE_ENUM.FTP,
                                DATA_SOURCE_ENUM.S3,
                                DATA_SOURCE_ENUM.ES,
                                DATA_SOURCE_ENUM.ES6,
                                DATA_SOURCE_ENUM.ES7,
                                DATA_SOURCE_ENUM.RESTFUL,
                            ].includes(form.getFieldValue(['targetMap', 'type'])) && (
                                <Button
                                    onClick={() => handleSetColumns(QuickColumnKind.COPY_SOURCE)}
                                    type="default"
                                    block
                                >
                                    拷贝源字段
                                </Button>
                            )}
                            {[
                                DATA_SOURCE_ENUM.HDFS,
                                DATA_SOURCE_ENUM.FTP,
                                DATA_SOURCE_ENUM.S3,
                                DATA_SOURCE_ENUM.ES,
                                DATA_SOURCE_ENUM.ES6,
                                DATA_SOURCE_ENUM.ES7,
                                DATA_SOURCE_ENUM.RESTFUL,
                            ].includes(form.getFieldValue(['sourceMap', 'type'])) && (
                                <Button
                                    onClick={() => handleSetColumns(QuickColumnKind.COPY_TARGET)}
                                    type="default"
                                    block
                                >
                                    拷贝目标字段
                                </Button>
                            )}
                            <Button type="default" onClick={() => handleSetColumns(QuickColumnKind.RESET)} block>
                                重置
                            </Button>
                        </Space>
                    </Col>
                </Row>
            </Spin>
            <KeyModal
                title={keyModalTitle}
                visible={keyModal.visible}
                keyModal={keyModal}
                dataType={keyModalType}
                onOk={(values: IDataColumnsProps) => {
                    (keyModal.isReader ? source : target).dispatch({
                        type: keyModal.operation,
                        payload: [
                            keyModal.operation === OperatorKind.ADD
                                ? values
                                : // Put the values into record for EDIT operation
                                  Object.assign(keyModal.editField!, values),
                        ],
                    });
                    setKeyModal((m) => ({ ...m, visible: false }));
                }}
                onCancel={() => setKeyModal((m) => ({ ...m, visible: false }))}
            />
        </div>
    );
}

function useFormColumns(): [IDataColumnsProps[], IDataColumnsProps[]] {
    const form = Form.useFormInstance();
    const sourceCol = Form.useWatch(['sourceMap', 'column'], form);
    const targetCol = Form.useWatch(['targetMap', 'column'], form);

    return [sourceCol || [], targetCol || []];
}

function useColumns(mapKind: 'sourceMap' | 'targetMap') {
    const form = Form.useFormInstance();
    const formWatchField = Form.useWatch(mapKind, form);
    const [fetching, setFetching] = useState(false);
    const [disabled, setDisabled] = useState(true);
    const [columns, setColumns] = useState<IDataColumnsProps[]>([]);
    const [errorMsg, setErrorMsg] = useState('');

    useEffect(() => {
        const sourceType = formWatchField?.type;
        const sourcePart = [
            DATA_SOURCE_ENUM.HIVE1X,
            DATA_SOURCE_ENUM.HIVE,
            DATA_SOURCE_ENUM.HIVE3X,
            DATA_SOURCE_ENUM.SPARKTHRIFT,
        ].includes(Number(sourceType));

        const tableName = Array.isArray(formWatchField?.table) ? formWatchField.table[0] : formWatchField?.table;

        if (tableName !== undefined) {
            const params = {
                sourceId: formWatchField.sourceId,
                schema: formWatchField.schema,
                tableName,
                isIncludePart: sourcePart,
            };
            const currentKey = molecule.editor.getState().current!.activeTab!.toString();
            const uniqueKey = md5(Object.values(params).join('.'));
            const storage = viewStoreService.getViewStorage<Record<string, any[]>>(currentKey) || {};
            if (Array.isArray(storage[uniqueKey])) {
                setColumns(storage[uniqueKey]);
            } else {
                setFetching(true);
                api.getOfflineTableColumn(params)
                    .then((res) => {
                        if (res.code === 1) {
                            setErrorMsg('');
                            setColumns(res.data);
                            viewStoreService.setViewStorage<Record<string, any>>(currentKey, (pre) => ({
                                ...pre,
                                [uniqueKey]: res.data,
                            }));
                        } else {
                            setErrorMsg(res.message);
                        }
                    })
                    .finally(() => {
                        setFetching?.(false);
                    });
            }
        } else {
            setColumns([]);
        }
    }, [formWatchField?.type, formWatchField?.table, formWatchField?.schema]);

    useEffect(() => {
        setColumns([]);
    }, [formWatchField?.sourceId]);

    useEffect(() => {
        setDisabled(!columns.length);
    }, [columns]);

    // Support to change the columns from outside
    useEffect(() => {
        function handler(keyMaps: IDataColumnsProps[]) {
            if (Array.isArray(keyMaps)) {
                dispatch({ type: OperatorKind.REPLACE, payload: keyMaps });
                // reset form fields
                form.resetFields([
                    ['sourceMap', 'column'],
                    ['targetMap', 'column'],
                ]);
            }
        }

        const eventKind = mapKind === 'sourceMap' ? EventKind.SourceKeyChange : EventKind.TargetKeyChange;
        event.subscribe(eventKind, handler);

        return () => {
            event.unsubscribe(eventKind, handler);
        };
    }, []);

    const dispatch = (next: { type: OperatorKind; payload: IDataColumnsProps[] }) => {
        switch (next.type) {
            case OperatorKind.ADD: {
                const rules = [
                    {
                        field: 'index',
                        message: '添加失败：索引值不能重复',
                    },
                    {
                        field: 'key',
                        message: '添加失败：字段名不能重复',
                    },
                ] as const;
                setColumns((cols) => {
                    const nextCols = [...cols];
                    // Generally, the linedCols are subset of columns, but if there is an exception.
                    // In FTP, the columns are empty and the linedCols are lined
                    const linedCols: IDataColumnsProps[] = form.getFieldValue(mapKind)?.column || [];

                    next.payload.forEach((col) => {
                        if (
                            rules.every((rule) => {
                                const isDuplicated =
                                    checkExist(col[rule.field]) &&
                                    (nextCols.some((o) => o[rule.field] === col[rule.field]) ||
                                        linedCols.some((o) => o[rule.field] === col[rule.field]));

                                if (isDuplicated) {
                                    message.error(rule.message);
                                }

                                return !isDuplicated;
                            })
                        ) {
                            nextCols.push(col);
                        }
                    });
                    return nextCols;
                });
                break;
            }
            case OperatorKind.REMOVE: {
                // Check if columns were already be lined
                const linedColumnsIndex = next.payload.reduce<number[]>((pre, cur) => {
                    const idx =
                        (form.getFieldValue(mapKind)?.column as IDataColumnsProps[])?.findIndex(
                            (col) => getUniqueKey(col) === getUniqueKey(cur)
                        ) ?? -1;
                    if (idx >= 0) {
                        pre.push(idx);
                    }
                    return pre;
                }, []);

                if (linedColumnsIndex.length) {
                    // Remove all lined columns
                    const nextSourceCol: IDataColumnsProps[] = form.getFieldValue(['sourceMap', 'column']).concat();
                    const nextTargetCol: IDataColumnsProps[] = form.getFieldValue(['targetMap', 'column']).concat();

                    form.setFieldsValue({
                        sourceMap: {
                            column: nextSourceCol.filter((_, idx) => !linedColumnsIndex.includes(idx)),
                        },
                        targetMap: {
                            column: nextTargetCol.filter((_, idx) => !linedColumnsIndex.includes(idx)),
                        },
                    });
                    updateValuesInData(form.getFieldsValue());
                }

                setColumns((cols) => {
                    const nextCols = [...cols];
                    next.payload.forEach((col) => {
                        const idx = nextCols.findIndex((c) => getUniqueKey(c) === getUniqueKey(col));
                        if (idx !== -1) {
                            nextCols.splice(idx, 1);
                        }
                    });
                    return nextCols;
                });

                break;
            }

            case OperatorKind.EDIT: {
                // here get the value and set directly since the EDIT in KeyModal is copy
                // refer to L860
                const nextValue: IDataColumnsProps[] = form.getFieldValue(mapKind).column ?? [];
                form.setFieldValue([mapKind, 'column'], nextValue);
                updateValuesInData(form.getFieldsValue());

                setColumns((cols) => {
                    const nextCols = [...cols];
                    next.payload.forEach((col) => {
                        const idx = nextCols.findIndex((c) => getUniqueKey(c) === getUniqueKey(col));
                        if (idx !== -1) {
                            // 这里只做赋值，不做深拷贝
                            // 因为字段映射的数组里的值和 column 字段的值是同一个引用，直接改这个值就可以做到都改了。如果做深拷贝则需要改两次值
                            Object.assign(nextCols[idx], col);
                        }
                    });
                    return nextCols;
                });

                break;
            }
            case OperatorKind.REPLACE: {
                // replace mode is to replace the whole column field
                setColumns(next.payload.concat());
                break;
            }
            default:
                break;
        }
    };

    return { data: columns, fetching, disabled, dispatch, errorMsg };
}
