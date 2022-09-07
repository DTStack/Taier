import { useEffect, useMemo, useRef, useState } from 'react';
import classNames from 'classnames';
import { Button, Col, Form, message, Row, Space, Spin, Tooltip } from 'antd';
import api from '@/api';
import LintTo from '@/components/lineTo';
import { DATA_SOURCE_ENUM, DATA_SOURCE_TEXT } from '@/constant';
import { EditOutlined, ExclamationCircleOutlined, MinusOutlined } from '@ant-design/icons';
import { useConstant } from '@/hooks';
import { checkExist, isValidFormatType } from '@/utils';
import { event, EventKind, updateValuesInData } from '.';
import KeyModal from './modals/keyModal';
import ConstModal from './modals/constModal';
import viewStoreService from '@/services/viewStoreService';
import molecule from '@dtinsight/molecule';
import md5 from 'md5';
import { useSize } from '@/components/customHooks';
import type { IDataColumnsProps } from '@/interface';
import type { ColumnType } from 'antd/lib/table';
import './keyMap.scss';

enum OperatorKind {
	ADD,
	REMOVE,
	EDIT,
	// replace is different from edit, which is change the whole columns
	REPLACE,
}

function getUniqueKey(data: IDataColumnsProps) {
	return `${data.key}-${data.type}`;
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
	 * 重置
	 */
	RESET,
}

function getSourceColumn(
	type: DATA_SOURCE_ENUM,
	removeAction: (record: IDataColumnsProps) => JSX.Element,
	editAction: (record: IDataColumnsProps) => JSX.Element,
): ColumnType<IDataColumnsProps>[] {
	switch (type) {
		case DATA_SOURCE_ENUM.HDFS:
		case DATA_SOURCE_ENUM.S3:
			return [
				{
					title: '索引位',
					dataIndex: 'index',
					key: 'index',
					ellipsis: true,
					render(text, record) {
						const formatVal = record.value ? `'${record.key}'` : record.key;
						return <Tooltip title={text || formatVal}>{text || formatVal}</Tooltip>;
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
							: `${text ? text.toUpperCase() : ''}${
									record.format ? `(${record.format})` : ''
							  }`;
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
							: `${text ? text.toUpperCase() : ''}${
									record.format ? `(${record.format})` : ''
							  }`;
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
							: `${text ? text.toUpperCase() : ''}${
									record.format ? `(${record.format})` : ''
							  }`;
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
							: `${text ? text.toUpperCase() : ''}${
									record.format ? `(${record.format})` : ''
							  }`;
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
								{(isValidFormatType(record.type) || record.value) &&
									editAction(record)}
							</Space>
						);
					},
				},
			];
	}
}

function getTargetColumn(
	type: DATA_SOURCE_ENUM,
	removeAction: (record: IDataColumnsProps) => JSX.Element,
	editAction: (record: IDataColumnsProps) => JSX.Element,
): ColumnType<IDataColumnsProps>[] {
	switch (type) {
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

	const [visibleConst, setConstVisible] = useState(false);
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

	const fetching = useMemo(
		() => source.fetching || target.fetching,
		[source.fetching, target.fetching],
	);

	const disabled = useMemo(
		() => source.disabled || target.disabled,
		[source.disabled, target.disabled],
	);
	// Put disabled into ref so we could get the latest value in closure function
	const disabledRef = useConstant(disabled);

	const handleOpenKeyModal = (record: IDataColumnsProps, isReader: boolean) => {
		if (!disabledRef.current) {
			setKeyModal({
				visible: true,
				isReader,
				editField: record,
				operation: OperatorKind.EDIT,
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

			case QuickColumnKind.NAME_MAP: {
				const sourceCols: IDataColumnsProps[] = [];
				const targetCols: IDataColumnsProps[] = [];
				sourceColumns.forEach((o) => {
					const name = o.key.toString().toUpperCase();
					const idx = targetColumns.findIndex((col) => {
						const sourceName = col.key.toString().toUpperCase();
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

	const handleColChanged = (
		action: OperatorKind,
		columns: IDataColumnsProps[],
		kind: 'source' | 'target',
	) => {
		if (!disabledRef.current) {
			(kind === 'source' ? source : target).dispatch({
				type: action,
				payload: columns,
			});
		}
	};

	useEffect(() => {
		selection.current = new LintTo(container.current!, {
			onRenderColumns(s) {
				return s
					? getSourceColumn(
							form.getFieldValue(['sourceMap', 'type']),
							(record: IDataColumnsProps) => (
								<Tooltip title="删除当前列">
									<MinusOutlined
										className={classNames(
											disabledRef.current && 'taier__dataSync--disabled',
										)}
										onClick={() =>
											handleColChanged(
												OperatorKind.REMOVE,
												[record],
												'source',
											)
										}
									/>
								</Tooltip>
							),
							(record: IDataColumnsProps) => (
								<Tooltip title="编辑当前列">
									<EditOutlined
										className={classNames(
											disabledRef.current && 'taier__dataSync--disabled',
										)}
										onClick={() => handleOpenKeyModal(record, true)}
									/>
								</Tooltip>
							),
					  )
					: getTargetColumn(
							form.getFieldValue(['targetMap', 'type']),
							(record: IDataColumnsProps) => (
								<Tooltip title="删除当前列">
									<MinusOutlined
										className={classNames(
											disabledRef.current && 'taier__dataSync--disabled',
										)}
										onClick={() =>
											handleColChanged(
												OperatorKind.REMOVE,
												[record],
												'target',
											)
										}
									/>
								</Tooltip>
							),
							(record: IDataColumnsProps) => (
								<Tooltip title="编辑当前列">
									<EditOutlined
										className={classNames(
											disabledRef.current && 'taier__dataSync--disabled',
										)}
										onClick={() => handleOpenKeyModal(record, false)}
									/>
								</Tooltip>
							),
					  );
			},
			onRenderFooter(s) {
				return s ? (
					<>
						<Button
							type="text"
							block
							onClick={() => setConstVisible(true)}
							disabled={disabledRef.current}
						>
							+添加常量
						</Button>
					</>
				) : null;
			},
			onDragStart(data) {
				const column: IDataColumnsProps[] =
					form.getFieldValue(['sourceMap', 'column']) || [];

				return !column.find((i) => getUniqueKey(i) === getUniqueKey(data));
			},

			onDrop(data) {
				const column: IDataColumnsProps[] =
					form.getFieldValue(['targetMap', 'column']) || [];

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

				const nextSourceCol = sCol.filter(
					(col) => getUniqueKey(col) !== getUniqueKey(sourceLine),
				);
				const nextTargetCol = tCol.filter(
					(col) => getUniqueKey(col) !== getUniqueKey(targetLine),
				);
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
	}, [disabled, sourceCol, targetCol]);

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
		const type: DATA_SOURCE_ENUM = form.getFieldValue([
			keyModal.isReader ? 'sourceMap' : 'targetMap',
			'type',
		]);

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

	const errorMsg = useMemo(
		() => source.errorMsg || target.errorMsg,
		[source.errorMsg, target.errorMsg],
	);

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
						<Space
							direction="vertical"
							size={5}
							className="taier__dataSync__keyMap__buttonGroups"
						>
							<Button
								disabled={disabled}
								onClick={() => handleSetColumns(QuickColumnKind.ROW_MAP)}
								type="primary"
								block
							>
								同行映射
							</Button>
							<Button
								disabled={disabled}
								onClick={() => handleSetColumns(QuickColumnKind.NAME_MAP)}
								type="primary"
								block
							>
								同名映射
							</Button>
							<Button
								disabled={disabled}
								type="default"
								onClick={() => handleSetColumns(QuickColumnKind.RESET)}
								block
							>
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
						payload: [values],
					});
					setKeyModal((m) => ({ ...m, visible: false }));
				}}
				onCancel={() => setKeyModal((m) => ({ ...m, visible: false }))}
			/>
			<ConstModal
				visible={visibleConst}
				onOk={(col) => {
					handleColChanged(OperatorKind.ADD, [col], 'source');
					setConstVisible(false);
				}}
				onCancel={() => setConstVisible(false)}
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

		const tableName = Array.isArray(formWatchField?.table)
			? formWatchField.table[0]
			: formWatchField?.table;

		if (tableName !== undefined) {
			const params = {
				sourceId: formWatchField.sourceId,
				schema: formWatchField.schema,
				tableName,
				isIncludePart: sourcePart,
			};
			const currentKey = molecule.editor.getState().current!.activeTab!.toString();
			const uniqueKey = md5(Object.values(params).join('.'));
			const storage =
				viewStoreService.getViewStorage<Record<string, any[]>>(currentKey) || {};
			if (Array.isArray(storage[uniqueKey])) {
				setColumns(storage[uniqueKey]);
			} else {
				setFetching(true);
				api.getOfflineTableColumn(params)
					.then((res) => {
						if (res.code === 1) {
							setErrorMsg('');
							setColumns(res.data);
							viewStoreService.setViewStorage<Record<string, any>>(
								currentKey,
								(pre) => ({
									...pre,
									[uniqueKey]: res.data,
								}),
							);
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

	const dispatch = (next: { type: OperatorKind; payload: IDataColumnsProps[] }) => {
		switch (next.type) {
			case OperatorKind.ADD: {
				setColumns((cols) => {
					const nextCols = [...cols];
					next.payload.forEach((col) => {
						if (checkExist(col.index) && nextCols.some((o) => o.index === col.index)) {
							message.error(`添加失败：索引值不能重复`);
						} else if (checkExist(col.key) && nextCols.some((o) => o.key === col.key)) {
							message.error(`添加失败：字段名不能重复`);
						} else {
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
						(formWatchField.column as IDataColumnsProps[])?.findIndex(
							(col) => getUniqueKey(col) === getUniqueKey(cur),
						) || -1;
					if (idx >= 0) {
						pre.push(idx);
					}
					return pre;
				}, []);

				if (linedColumnsIndex.length) {
					// Remove all lined columns
					const nextSourceCol: IDataColumnsProps[] = form
						.getFieldValue(['sourceMap', 'column'])
						.concat();
					const nextTargetCol: IDataColumnsProps[] = form
						.getFieldValue(['targetMap', 'column'])
						.concat();

					form.setFieldsValue({
						sourceMap: {
							column: nextSourceCol.filter(
								(_, idx) => !linedColumnsIndex.includes(idx),
							),
						},
						targetMap: {
							column: nextTargetCol.filter(
								(_, idx) => !linedColumnsIndex.includes(idx),
							),
						},
					});
					updateValuesInData(form.getFieldsValue());
				}

				setColumns((cols) => {
					const nextCols = [...cols];
					next.payload.forEach((col) => {
						const idx = nextCols.findIndex(
							(c) => getUniqueKey(c) === getUniqueKey(col),
						);
						if (idx !== -1) {
							nextCols.splice(idx, 1);
						}
					});
					return nextCols;
				});

				break;
			}

			case OperatorKind.EDIT: {
				const nextValue: IDataColumnsProps[] = formWatchField.column ?? [];
				// Check if columns were lined
				next.payload.forEach((col) => {
					const obj = nextValue.find((val) => getUniqueKey(val) === getUniqueKey(col));
					if (obj) {
						Object.assign(obj, { ...col });
					}
				});

				form.setFieldValue([mapKind, 'column'], nextValue);
				updateValuesInData(form.getFieldsValue());

				setColumns((cols) => {
					const nextCols = [...cols];
					next.payload.forEach((col) => {
						const idx = nextCols.findIndex(
							(c) => getUniqueKey(c) === getUniqueKey(col),
						);
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
