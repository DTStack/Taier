import type { CSSProperties } from 'react';
import { useEffect, useMemo, useRef, useState } from 'react';
import { Button, Col, message, Modal, Row, Space, Spin, Tooltip } from 'antd';
import { select, mouse } from 'd3-selection';
import { DATA_SOURCE_ENUM, HBASE_FIELD_TYPES, HDFS_FIELD_TYPES } from '@/constant';
import Resize from '../resize';
import { MinusOutlined, EditOutlined } from '@ant-design/icons';
import type { IDataColumnsProps, ISourceMapProps, ITargetMapProps } from '@/interface';
import { isNumber, isObject, isUndefined } from 'lodash';
import Api from '@/api';
import ScrollText from '../scrollText';
import { isHdfsType } from '@/utils/is';
import ConstModal from './modals/constModal';
import KeyModal from './modals/keyModal';
import BatchModal from './modals/batchModal';
import { Utils } from '@dtinsight/dt-utils/lib';
import './keymap.scss';

export const OPERATOR_TYPE = {
	ADD: 'add',
	REMOVE: 'remove',
	EDIT: 'edit',
	// replace is different from edit, which is change the whole columns
	REPLACE: 'replace',
} as const;

export interface IKeyMapProps {
	source: IDataColumnsProps[];
	target: IDataColumnsProps[];
}
interface IKeyMapComponentProps {
	/**
	 * 第一步所填写的信息
	 */
	sourceMap: ISourceMapProps;
	/**
	 * 第二步所填写的信息
	 */
	targetMap: ITargetMapProps;
	/**
	 * 用户自定义添加的列
	 */
	userColumns?: IKeyMapProps;
	/**
	 * 是否只读，用于预览数据
	 */
	readonly?: boolean;
	/**
	 * @deprecated
	 */
	isNativeHive?: boolean;
	/**
	 * 改变表的列的回调函数
	 * @param column 做出改变的列
	 * @param operator 表示当前改变的策略，支持新增、删除、编辑、替换
	 * @param sourceOrTarget 区分改变源表列还是目标表列
	 */
	onColsChanged?: (
		column: IDataColumnsProps | IDataColumnsProps[],
		operator: Valueof<typeof OPERATOR_TYPE>,
		sourceOrTarget: 'source' | 'target',
	) => void;
	/**
	 * 当字段映射关系发生改变的回调函数
	 */
	onLinesChanged?: (lines: IKeyMapProps) => void;
	/**
	 * 下一步或上一步的回调函数
	 * @param next 为 true 时表示下一步，否则为上一步
	 */
	onNext?: (next: boolean) => void;
}

export const isValidFormatType = (type: any) => {
	if (!type) return false;
	const typeStr = type.toUpperCase();
	return typeStr === 'STRING' || typeStr === 'VARCHAR' || typeStr === 'VARCHAR2';
};

function isFieldMatch(source: any, target: any) {
	/**
	 * TODO 目前从接口返回的keymap字段与sourceMap, targetMap中不一致
	 */
	if (isObject(source as any) && isObject(target as any)) {
		const sourceVal = source.key || source.index;
		const tagetVal = target.key || target.index;
		return sourceVal === tagetVal;
	}
	if (isObject(source as any) && !isObject(target as any)) {
		const sourceVal = source.key || source.index;
		return sourceVal === target;
	}
	if (!isObject(source as any) && isObject(target as any)) {
		const targetVal = target.key || target.index;
		return source === targetVal;
	}
	return source === target;
}

/**
 * 获取批量添加的默认值
 */
function getBatIntVal(
	type: DATA_SOURCE_ENUM | undefined,
	typeCol: IDataColumnsProps[] | undefined,
) {
	const isNotHBase = type !== DATA_SOURCE_ENUM.HBASE;
	let initialVal = '';
	if (isNotHBase) {
		typeCol?.forEach((item) => {
			const itemKey = Utils.checkExist(item.key) ? item.key : undefined;
			const field = Utils.checkExist(item.index) ? item.index : itemKey;
			if (field !== undefined) {
				initialVal += `${field}:${item.type},\n`;
			}
		});
	} else {
		typeCol?.forEach((item) => {
			const field = Utils.checkExist(item.key) ? item.key : undefined;
			if (field !== undefined) initialVal += `${item.cf || '-'}:${field}:${item.type},\n`;
		});
	}
	return initialVal;
}

export default function KeyMap({
	sourceMap,
	targetMap,
	userColumns,
	isNativeHive = false,
	readonly,
	onColsChanged,
	onNext,
	onLinesChanged,
}: IKeyMapComponentProps) {
	const [loading, setLoading] = useState(false);
	// 前两步选择的表获取字段
	const [tableColumns, setTableCols] = useState<IKeyMapProps>({
		source: [],
		target: [],
	});
	const [canvasInfo, setCanvasInfo] = useState({
		h: 40, // 字段一行高度
		w: 230, // 字段的宽度
		W: 600, // step容器大小
		padding: 10, // 绘制拖拽点左右边距
	});
	// 连线数据
	const [lines, setLines] = useState<IKeyMapProps>({
		source: sourceMap.column || [],
		target: targetMap.column || [],
	});
	const [btnTypes, setBtnTypes] = useState({ rowMap: false, nameMap: false });
	const [visibleConst, setConstVisible] = useState(false);
	const [keyModal, setKeyModal] = useState<{
		visible: boolean;
		isReader: boolean;
		editField: IDataColumnsProps | undefined;
		source: IDataColumnsProps | undefined;
		operation: Valueof<typeof OPERATOR_TYPE>;
	}>({
		visible: false,
		// 区分源表还是目标表
		isReader: false,
		editField: undefined,
		source: undefined,
		operation: OPERATOR_TYPE.ADD,
	});
	const [batchModal, setBatchModal] = useState<{
		visible: boolean;
		isReader: boolean;
		defaultValue?: string;
	}>({
		visible: false,
		// 区分源表还是目标表
		isReader: false,
		defaultValue: undefined,
	});
	// 列族，只有 HDFS 才有
	const [families, setFamilies] = useState<{ source: string[]; target: string[] }>({
		source: [],
		target: [],
	});
	// step 容器
	const $canvas = useRef<SVGSVGElement>(null);
	// 拖动的线
	const $activeLine = useRef<SVGLineElement>(null);
	const { h, w, W, padding } = canvasInfo;

	const loadColumnFamily = () => {
		if (sourceSrcType === DATA_SOURCE_ENUM.HBASE) {
			Api.getHBaseColumnFamily({
				sourceId: sourceMap.sourceId,
				tableName: sourceMap.table,
			}).then((res) => {
				if (res.code === 1) {
					setFamilies((f) => ({ ...f, source: res.data || [] }));
				}
			});
		}

		if (targetSrcType === DATA_SOURCE_ENUM.HBASE) {
			Api.getHBaseColumnFamily({
				sourceId: targetMap.sourceId,
				tableName: targetMap?.table,
			}).then((res) => {
				if (res.code === 1) {
					setFamilies((f) => ({ ...f, target: res.data || [] }));
				}
			});
		}
	};

	const handleResize = () => {
		setCanvasInfo((f) => ({ ...f, W: getCanvasW() }));
	};

	const handleAddLines = (source: IDataColumnsProps, target: IDataColumnsProps) => {
		setLines((ls) => {
			// avoid to add lines from an already exist source or target
			if (ls.source.some((l) => isFieldMatch(l, source))) return ls;
			if (ls.target.some((l) => isFieldMatch(l, target))) return ls;
			return {
				source: [...ls.source, source],
				target: [...ls.target, target],
			};
		});
	};

	const handleRemoveLines = (source: IDataColumnsProps, target: IDataColumnsProps) => {
		setLines((ls) => {
			const nextSource = ls.source.filter((s) => !isFieldMatch(s, source));
			const nextTarget = ls.target.filter((s) => !isFieldMatch(s, target));

			return {
				source: nextSource,
				target: nextTarget,
			};
		});
	};

	const handleRowMapClick = () => {
		const { rowMap, nameMap } = btnTypes;

		if (!rowMap) {
			const source: IDataColumnsProps[] = [];
			const target: IDataColumnsProps[] = [];
			sourceCol.forEach((o, i) => {
				if (targetCol[i]) {
					source.push(o);
					target.push(targetCol[i]);
				}
			});
			setLines({ source, target });
		} else {
			setLines({ source: [], target: [] });
		}

		setBtnTypes({
			rowMap: !rowMap,
			nameMap: nameMap ? !nameMap : nameMap,
		});
	};

	// 同名映射
	const handleNameMapClick = () => {
		const { nameMap, rowMap } = btnTypes;

		if (!nameMap) {
			const source: IDataColumnsProps[] = [];
			const target: IDataColumnsProps[] = [];
			sourceCol.forEach((o) => {
				const name = o.key.toString().toUpperCase();
				const idx = targetCol.findIndex((col) => {
					const sourceName = col.key.toString().toUpperCase();
					return sourceName === name;
				});
				if (idx !== -1) {
					source.push(o);
					target.push(targetCol[idx]);
				}
			});
			setLines({ source, target });
		} else {
			setLines({ source: [], target: [] });
		}

		setBtnTypes({
			nameMap: !nameMap,
			rowMap: rowMap ? !rowMap : rowMap,
		});
	};

	// 拷贝源表列
	const handleCopySourceCols = () => {
		if (isHdfsType(targetSrcType)) {
			const serverParams: any = {};
			sourceCol.forEach((item) => {
				serverParams[item.key || item.index!] = item.type;
			});
			Api.convertToHiveColumns({
				columns: serverParams,
			}).then((res: any) => {
				if (res.code === 1) {
					const params: any = [];
					Object.getOwnPropertyNames(res.data).forEach((key: any) => {
						params.push({
							key,
							type: res.data[key],
						});
					});
					onColsChanged?.(params, OPERATOR_TYPE.ADD, 'target');
				}
			});
		} else if (sourceCol && sourceCol.length > 0) {
			const params = sourceCol.map((item) => {
				return {
					key: !isUndefined(item.key) ? item.key : item.index!,
					type: item.type,
				};
			});
			onColsChanged?.(params, OPERATOR_TYPE.ADD, 'target');
		}
	};

	// 拷贝目标表列
	const handleCopyTargetCols = () => {
		if (targetCol && targetCol.length > 0) {
			const params = targetCol.map((item, index) => {
				return {
					key: item.key || index.toString(),
					type: item.type,
				};
			});
			onColsChanged?.(params, OPERATOR_TYPE.ADD, 'target');
		}
	};

	const initEditKeyRow = (
		isReader: boolean,
		sourceCol: IDataColumnsProps | undefined,
		field: IDataColumnsProps | undefined,
	) => {
		setKeyModal({
			visible: true,
			isReader,
			editField: field,
			source: sourceCol,
			operation: OPERATOR_TYPE.EDIT,
		});
	};

	const initAddKeyRow = (isReader: boolean) => {
		setKeyModal({
			visible: true,
			isReader,
			editField: undefined,
			source: undefined,
			operation: OPERATOR_TYPE.ADD,
		});
	};

	const handleKeyModalSubmit = (values: IDataColumnsProps) => {
		onColsChanged?.(
			Object.assign(keyModal.editField || {}, values),
			keyModal.operation,
			keyModal.isReader ? 'source' : 'target',
		);
		handleHideKeyModal();
	};

	const handleHideKeyModal = () => {
		setKeyModal({
			visible: false,
			isReader: false,
			operation: OPERATOR_TYPE.ADD,
			editField: undefined,
			source: undefined,
		});
	};

	const handleOpenBatchModal = (isReader: boolean = true) => {
		const srcType = isReader ? sourceSrcType : targetSrcType;
		const cols = isReader ? sourceCol : targetCol;
		setBatchModal({
			visible: true,
			isReader,
			defaultValue: getBatIntVal(srcType, cols),
		});
	};

	// const importSourceFields = () => {
	// 	setBatchModal({
	// 		visible: true,
	// 		isReader: true,
	// 		defaultValue: getBatIntVal(sourceSrcType, sourceCol),
	// 	});
	// };

	// const importFields = () => {
	// 	setBatchModal({
	// 		visible: true,
	// 		isReader: false,
	// 		defaultValue: getBatIntVal(targetSrcType, targetCol),
	// 	});
	// };

	const handleHideBatchModal = () => {
		setBatchModal({
			visible: false,
			isReader: false,
			defaultValue: '',
		});
	};

	// 批量添加源表字段
	const handleBatchAddSourceFields = (batchText: string) => {
		if (!batchText) {
			handleHideBatchModal();
			return;
		}

		const arr = batchText.split(',');
		const params: IDataColumnsProps[] = [];
		switch (sourceSrcType) {
			case DATA_SOURCE_ENUM.FTP:
			case DATA_SOURCE_ENUM.HDFS:
			case DATA_SOURCE_ENUM.S3: {
				for (let i = 0; i < arr.length; i += 1) {
					const item = arr[i].replace(/\n/, '');
					if (item) {
						const map = item.split(':');
						if (map.length < 1) {
							break;
						}
						const key = parseInt(Utils.trim(map[0]), 10);
						const type = map[1] ? Utils.trim(map[1]).toUpperCase() : null;
						if (!Number.isNaN(key) && isNumber(key)) {
							if (type && HDFS_FIELD_TYPES.includes(type)) {
								if (!params.find((pa) => pa.key === key)) {
									params.push({
										key,
										type,
									});
								}
							} else {
								message.error(`索引 ${key} 的数据类型错误！`);
								return;
							}
						} else {
							message.error(`索引名称 ${key} 应该为整数数字！`);
							return;
						}
					}
				}
				break;
			}
			case DATA_SOURCE_ENUM.HBASE: {
				for (let i = 0; i < arr.length; i += 1) {
					const item = arr[i].replace(/\n/, '');
					if (item) {
						const map = item.split(':');
						if (map.length < 2) {
							break;
						}
						const cf = Utils.trim(map[0]);
						const name = Utils.trim(map[1]);
						const type = Utils.trim(map[2]);
						if (!HBASE_FIELD_TYPES.includes(type)) {
							message.error(`字段${name}的数据类型错误！`);
							return;
						}
						params.push({
							cf,
							key: name,
							type,
						});
					}
				}
				break;
			}
			default:
				break;
		}

		onColsChanged?.(params, OPERATOR_TYPE.REPLACE, 'source');
		handleHideBatchModal();
	};

	// 批量添加目标表字段
	const handleBatchAddTargetFields = (batchText: string) => {
		const str = Utils.trim(batchText);
		const arr = str.split(',');

		const params: any = [];

		switch (targetSrcType) {
			case DATA_SOURCE_ENUM.FTP:
			case DATA_SOURCE_ENUM.HDFS:
			case DATA_SOURCE_ENUM.S3: {
				for (let i = 0; i < arr.length; i += 1) {
					const item = arr[i];
					if (item) {
						const map = item.split(':');
						const key = Utils.trim(map[0]);
						const type = Utils.trim(map[1].toUpperCase());
						if (HDFS_FIELD_TYPES.includes(type)) {
							params.push({
								key,
								type,
							});
						} else {
							message.error(`字段${key}的数据类型错误！`);
							return;
						}
					}
				}
				break;
			}
			case DATA_SOURCE_ENUM.HBASE: {
				for (let i = 0; i < arr.length; i += 1) {
					const item = arr[i];
					if (item) {
						const map = item.split(':');
						const cf = Utils.trim(map[0]);
						const name = Utils.trim(map[1]);
						const type = Utils.trim(map[2]);
						if (!HBASE_FIELD_TYPES.includes(type)) {
							message.error(`字段${name}的数据类型错误！`);
							return;
						}
						params.push({
							cf,
							key: name,
							type,
						});
					}
				}
				break;
			}
			default:
				break;
		}
		onColsChanged?.(params, OPERATOR_TYPE.REPLACE, 'target');
		handleHideBatchModal();
	};

	const handleSubmit = () => {
		const { source, target } = lines;
		if (source.length === 0 && target.length === 0) {
			message.error('尚未配置数据同步的字段映射！');
			return;
		}

		const isCarbonDataCheckPartition = () => {
			if (targetSrcType === DATA_SOURCE_ENUM.CARBONDATA && !isNativeHive) {
				const hasPartiton = targetCol.find((col) => col.isPart);
				const keymapPartition = target.find((col) => col.isPart);
				if (hasPartiton && !keymapPartition) {
					message.error('目标字段中的分区字段必须添加映射！');
					return false;
				}
			}
			return true;
		};

		// 如果Carbondata作为目标数据源，且目标字段中有分区字段，分区字段必须添加映射，否则error
		if (!isCarbonDataCheckPartition()) {
			return;
		}

		// 针对Hbase增加keyrow检验项
		if (targetSrcType === DATA_SOURCE_ENUM.HBASE) {
			if (!checkHBaseRowKey()) {
				return;
			}
		}

		onNext?.(true);
	};

	const checkHBaseRowKey = () => {
		const { rowkey } = targetMap;
		const { source } = lines;

		if (rowkey) {
			const arr: any = [];
			if (sourceSrcType === DATA_SOURCE_ENUM.HBASE) {
				const regx = /([\w]+:[\w]+)/g;
				const matchRes = rowkey.match(regx) || '';
				for (let i = 0; i < matchRes.length; i += 1) {
					const val = matchRes[i].split(':');
					if (val && val.length === 2) {
						const res = source.find(
							(item) => item.cf === val[0] && item.key === val[1],
						);
						if (!res) {
							message.error('目标表rowkey在源表中并不存在！');
							return false;
						}
					}
				}
			} else {
				// 正则提取
				const regx = /\$\(([\w]+)\)/g;
				let temp: any;
				// eslint-disable-next-line no-cond-assign
				while ((temp = regx.exec(rowkey)) !== null) {
					arr.push(temp[1]);
				}
				// 验证字段
				for (let i = 0; i < arr.length; i += 1) {
					let res: IDataColumnsProps | undefined;
					if (isObject(source[0])) {
						res = source.find((item) => {
							const name = item.key !== undefined ? item.key : item.index;
							return `${name}` === `${arr[i]}` ? item : undefined;
						});
					} else {
						res = source.find((item) => item === arr[i]);
					}
					if (!res) {
						message.error(`目标表rowkey: ${arr[i]}在源表中并不存在！！`);
						return false;
					}
				}
			}
			return true;
		}

		message.error('目标表rowkey不能为空！');
		return false;
	};

	const keymapHelpModal = () => {
		Modal.info({
			title: '快速配置字段映射',
			content: (
				<div>
					<img
						style={{
							width: '100%',
							height: 400,
						}}
						src="images/keymap_help.jpg"
					/>
				</div>
			),
			width: 850,
			okText: '了解',
		});
	};

	const dragSvg = () => {
		renderDags(
			$canvas.current,
			(tableColumns?.source || []).concat(userColumns?.source || []),
			(tableColumns?.target || []).concat(userColumns?.target || []),
			canvasInfo,
		);
		renderLines($canvas.current, lines, canvasInfo);
		bindEvents(
			$canvas.current,
			$activeLine.current,
			canvasInfo,
			handleAddLines,
			handleRemoveLines,
		);
	};

	const renderSource = () => {
		const colStyle: CSSProperties = {
			left: padding,
			top: padding,
			width: w,
			height: h,
		};

		const renderTableRow = (sourceType?: DATA_SOURCE_ENUM, col?: IDataColumnsProps) => {
			const removeOption = (
				<div
					className="remove-cell"
					onClick={() => col && onColsChanged?.(col, OPERATOR_TYPE.REMOVE, 'source')}
				>
					<Tooltip title="删除当前列">
						<MinusOutlined />
					</Tooltip>
				</div>
			);

			const editOption = (
				<div className="edit-cell" onClick={() => initEditKeyRow(true, undefined, col)}>
					<Tooltip title="编辑当前列">
						<EditOutlined />
					</Tooltip>
				</div>
			);

			const cellOperation = (remove: any, edit: any) => (
				<div>
					{remove}
					{edit}
				</div>
			);

			// eslint-disable-next-line no-nested-ternary
			const typeValue = col
				? col.value
					? `常量(${col.type})`
					: `${col.type ? col.type.toUpperCase() : ''}${
							col.format ? `(${col.format})` : ''
					  }`
				: '';
			const type = col ? <ScrollText value={typeValue} /> : '类型';

			switch (sourceType) {
				case DATA_SOURCE_ENUM.HDFS:
				case DATA_SOURCE_ENUM.S3: {
					const name: any = col ? (
						<ScrollText
							value={
								// eslint-disable-next-line no-nested-ternary
								col.index !== undefined
									? col.index
									: col.value
									? `'${col.key}'`
									: col.key
							}
						/>
					) : (
						'索引位'
					);
					return (
						<div>
							<div className="cell">{name}</div>
							<div className="cell" title={typeValue}>
								{type}
							</div>
							{sourceMap.fileType !== 'orc' ? (
								<div className="cell">
									{col ? cellOperation(removeOption, editOption) : '操作'}
								</div>
							) : (
								''
							)}
						</div>
					);
				}
				case DATA_SOURCE_ENUM.HBASE: {
					const name: any = col ? (
						<ScrollText value={col.value ? `'${col.key}'` : col.key} />
					) : (
						'列名/行健'
					);
					const cf = col ? col.cf : '列族';

					// 仅允许常量删除操作
					const opt =
						col && col.key === 'rowkey'
							? cellOperation(null, editOption)
							: cellOperation(removeOption, editOption);
					return (
						<div className="four-cells">
							<div className="cell" title={cf}>
								{cf || '-'}
							</div>
							<div className="cell">{name}</div>
							<div className="cell" title={typeValue}>
								{type}
							</div>
							<div className="cell">{col ? opt : '操作'}</div>
						</div>
					);
				}
				case DATA_SOURCE_ENUM.MAXCOMPUTE:
				case DATA_SOURCE_ENUM.HIVE1X:
				case DATA_SOURCE_ENUM.HIVE:
				case DATA_SOURCE_ENUM.HIVE3X: {
					const name: any = col ? (
						<ScrollText value={col.value ? `'${col.key}'` : col.key} />
					) : (
						'字段名称'
					);
					// 仅允许常量删除操作
					const opt =
						col && col.value
							? cellOperation(removeOption, editOption)
							: cellOperation(null, editOption);
					return (
						<div>
							<div className="cell">{name}</div>
							<div className="cell" title={typeValue}>
								{type}
							</div>
							<div className="cell">{col ? opt : '操作'}</div>
						</div>
					);
				}
				case DATA_SOURCE_ENUM.IMPALA: {
					const name: any = col ? (
						<ScrollText value={col.value ? `'${col.key}'` : col.key} />
					) : (
						'字段名称'
					);
					// 仅允许常量删除操作
					const opt =
						col && col.value
							? cellOperation(removeOption, editOption)
							: cellOperation(null, editOption);
					return (
						<div>
							<div className="cell">{name}</div>
							<div className="cell" title={typeValue}>
								{type}
								{col && col.isPart && (
									<img
										src="images/primary-key.svg"
										style={{
											float: 'right',
											marginTop: '-26px',
										}}
									/>
								)}
							</div>
							<div className="cell">{col ? opt : '操作'}</div>
						</div>
					);
				}
				case DATA_SOURCE_ENUM.FTP: {
					const name: any = col ? (
						<ScrollText
							value={
								// eslint-disable-next-line no-nested-ternary
								col.index !== undefined
									? col.index
									: col.value
									? `'${col.key}'`
									: col.key
							}
						/>
					) : (
						'字段序号'
					);
					return (
						<div>
							<div className="cell">{name}</div>
							<div className="cell" title={typeValue}>
								{type}
							</div>
							<div className="cell">
								{col ? cellOperation(removeOption, editOption) : '操作'}
							</div>
						</div>
					);
				}
				default: {
					// 常量都允许删除和编辑
					const canFormat = isValidFormatType(col && col.type) || col?.value;
					const opt = (
						<div>
							{col && col.value ? removeOption : ''}
							{canFormat ? editOption : ''}
						</div>
					);
					const name: any = col ? (
						<ScrollText value={col.value ? `'${col.key}'` : col.key} />
					) : (
						'字段名称'
					);
					return (
						<div>
							<div className="cell">{name}</div>
							<div className="cell" title={typeValue}>
								{type}
							</div>
							<div className="cell">{col ? opt : '操作'}</div>
						</div>
					);
				}
			}
		};

		const renderTableFooter = (sourceType?: DATA_SOURCE_ENUM) => {
			if (readonly) return '';
			let footerContent = null;
			const btnAddConst = (
				<span className="col-plugin" onClick={() => setConstVisible(true)}>
					+添加常量
				</span>
			);
			switch (sourceType) {
				case DATA_SOURCE_ENUM.HBASE:
					footerContent = (
						<span>
							<span className="col-plugin" onClick={() => initAddKeyRow(true)}>
								+添加字段
							</span>
							&nbsp;
							<span className="col-plugin" onClick={() => handleOpenBatchModal()}>
								+文本模式
							</span>
						</span>
					);
					break;
				case DATA_SOURCE_ENUM.HDFS:
				case DATA_SOURCE_ENUM.S3: {
					footerContent =
						sourceMap.fileType !== 'orc' ? (
							<span>
								<span className="col-plugin" onClick={() => initAddKeyRow(true)}>
									+添加字段
								</span>
								&nbsp;
								<span className="col-plugin" onClick={() => handleOpenBatchModal()}>
									+文本模式
								</span>
							</span>
						) : null;
					break;
				}
				case DATA_SOURCE_ENUM.FTP: {
					footerContent = (
						<span>
							<span className="col-plugin" onClick={() => initAddKeyRow(true)}>
								+添加字段
							</span>
							&nbsp;
							<span className="col-plugin" onClick={() => handleOpenBatchModal()}>
								+文本模式
							</span>
						</span>
					);
					break;
				}
				default: {
					footerContent = null;
					break;
				}
			}
			return (
				<div
					className="m-col absolute"
					style={{
						left: padding,
						top: padding + h * (sourceCol.length + 1),
						width: w,
						height: h,
						zIndex: 2,
					}}
				>
					{footerContent}
					{btnAddConst}
				</div>
			);
		};

		return (
			<div className="sourceLeft">
				<div className="m-col title absolute" style={colStyle}>
					{renderTableRow(sourceSrcType)}
				</div>
				{sourceCol.map((col, i) => (
					<div
						style={{
							width: w,
							height: h,
							left: padding,
							top: padding + h * (i + 1),
						}}
						className="m-col absolute"
						key={`sourceLeft-${i}`}
					>
						{renderTableRow(sourceSrcType, col)}
					</div>
				))}
				{renderTableFooter(sourceSrcType)}
			</div>
		);
	};

	const renderTarget = () => {
		const colStyle: CSSProperties = {
			left: W - (padding + w),
			top: padding,
			width: w,
			height: h,
		};

		const renderTableRow = (
			targetType?: DATA_SOURCE_ENUM,
			col?: IDataColumnsProps,
			i?: number,
		) => {
			const operations = (
				<div>
					<div
						className="remove-cell"
						onClick={() => col && onColsChanged?.(col, OPERATOR_TYPE.REMOVE, 'target')}
					>
						<Tooltip title="删除当前列">
							<MinusOutlined />
						</Tooltip>
					</div>
					<div
						className="edit-cell"
						onClick={() => i !== undefined && initEditKeyRow(false, sourceCol[i], col)}
					>
						<Tooltip title="编辑当前列">
							<EditOutlined />
						</Tooltip>
					</div>
				</div>
			);
			switch (targetType) {
				case DATA_SOURCE_ENUM.HDFS:
				case DATA_SOURCE_ENUM.S3: {
					const name = col ? <ScrollText value={col.key} /> : '字段名称';
					const type = col ? col.type.toUpperCase() : '类型';
					return (
						<div>
							<div className="cell">{name}</div>
							<div className="cell" title={type}>
								{type}
							</div>
							<div className="cell">{col ? operations : '操作'}</div>
						</div>
					);
				}
				case DATA_SOURCE_ENUM.HBASE: {
					const name = col ? col.cf : '列族';
					const column: any = col ? <ScrollText value={col.key} /> : '列名';
					const type: any = col ? <ScrollText value={col.type.toUpperCase()} /> : '类型';
					return (
						<div className="four-cells">
							<div className="cell">{name}</div>
							<div className="cell" title={column}>
								{column}
							</div>
							<div className="cell" title={type}>
								{type}
							</div>
							<div className="cell">{col ? operations : '操作'}</div>
						</div>
					);
				}
				case DATA_SOURCE_ENUM.FTP: {
					const column = col ? <ScrollText value={col.key} /> : '字段名称';
					const type = col ? col.type.toUpperCase() : '类型';
					return (
						<div>
							<div
								className="cell"
								title={col ? col.key.toString() : (column as string)}
							>
								{column}
							</div>
							<div className="cell" title={type}>
								{type}
							</div>
							<div className="cell">{col ? operations : '操作'}</div>
						</div>
					);
				}
				case DATA_SOURCE_ENUM.IMPALA: {
					const typeText = col
						? `${col.type.toUpperCase()}${col.isPart ? '(分区字段)' : ''}`
						: '类型';
					const fieldName = col ? <ScrollText value={col.key} /> : '字段名称';
					return (
						<div>
							<div
								className="cell"
								title={col ? col.key.toString() : (fieldName as string)}
							>
								{fieldName}
							</div>
							<div className="cell" title={typeText}>
								{typeText}
								{col && col.isPart && (
									<img
										src="images/primary-key.svg"
										style={{
											float: 'right',
											marginTop: '-26px',
										}}
									/>
								)}
							</div>
						</div>
					);
				}
				default: {
					const typeText = col
						? `${col.type.toUpperCase()}${col.isPart ? '(分区字段)' : ''}`
						: '类型';
					const fieldName: any = col ? <ScrollText value={col.key} /> : '字段名称';
					return (
						<div>
							<div className="cell" title={fieldName}>
								{fieldName}
							</div>
							<div className="cell" title={typeText}>
								{typeText}
							</div>
						</div>
					);
				}
			}
		};

		const renderTableFooter = (targetType?: DATA_SOURCE_ENUM) => {
			if (readonly) return '';
			let footerContent = null;
			switch (targetType) {
				case DATA_SOURCE_ENUM.HBASE:
					footerContent = (
						<div>
							<span className="col-plugin" onClick={() => initAddKeyRow(false)}>
								+添加字段
							</span>
							&nbsp;
							<span
								className="col-plugin"
								onClick={() => handleOpenBatchModal(false)}
							>
								+文本模式
							</span>
						</div>
					);
					break;
				case DATA_SOURCE_ENUM.HDFS: {
					footerContent = (
						<div>
							<span className="col-plugin" onClick={() => initAddKeyRow(false)}>
								+添加字段
							</span>
							&nbsp;
							<span
								className="col-plugin"
								onClick={() => handleOpenBatchModal(false)}
							>
								+文本模式
							</span>
						</div>
					);
					break;
				}
				case DATA_SOURCE_ENUM.FTP: {
					footerContent = (
						<div>
							<span className="col-plugin" onClick={() => initAddKeyRow(false)}>
								+添加字段
							</span>
							&nbsp;
							<span
								className="col-plugin"
								onClick={() => handleOpenBatchModal(false)}
							>
								+文本模式
							</span>
						</div>
					);
					break;
				}
				case DATA_SOURCE_ENUM.S3: {
					footerContent = (
						<div>
							<span className="col-plugin" onClick={() => initAddKeyRow(false)}>
								+添加字段
							</span>
							&nbsp;
							<span
								className="col-plugin"
								onClick={() => handleOpenBatchModal(false)}
							>
								+文本模式
							</span>
						</div>
					);
					break;
				}
				default: {
					footerContent = null;
					break;
				}
			}
			return footerContent ? (
				<div
					className="m-col footer absolute"
					style={{
						top: padding + h * (targetCol.length + 1),
						left: W - (padding + w),
						width: w,
						height: h,
						zIndex: 2,
					}}
				>
					{footerContent}
				</div>
			) : (
				''
			);
		};

		return (
			<div className="targetRight">
				<div className="m-col title  absolute" style={colStyle}>
					{renderTableRow(targetSrcType)}
				</div>
				{targetCol.map((col, i) => {
					return (
						<div
							key={`targetRight-${i}`}
							className="m-col absolute"
							style={{
								width: w,
								height: h,
								top: padding + h * (i + 1),
								left: W - (padding + w),
							}}
						>
							{renderTableRow(targetSrcType, col, i)}
						</div>
					);
				})}
				{renderTableFooter(targetSrcType)}
			</div>
		);
	};

	const renderKeyModal = () => {
		const { operation, isReader, visible } = keyModal;
		const dataType = isReader ? sourceSrcType : targetSrcType;

		let title = '添加HDFS字段';
		if (operation === 'add') {
			if (dataType === DATA_SOURCE_ENUM.HBASE) {
				title = '添加HBase字段';
			} else if (dataType === DATA_SOURCE_ENUM.FTP) {
				title = '添加FTP字段';
			} else if (dataType === DATA_SOURCE_ENUM.S3) {
				title = '添加AWS S3字段';
			}
		} else if (operation === 'edit') {
			title = '修改HDFS字段';
			if (dataType === DATA_SOURCE_ENUM.HBASE) {
				title = '修改HBase字段';
			} else if (dataType === DATA_SOURCE_ENUM.FTP) {
				title = '添加FTP字段';
			} else if (dataType === DATA_SOURCE_ENUM.S3) {
				title = '添加AWS S3字段';
			} else {
				title = '修改字段';
			}
		}

		return (
			<KeyModal
				title={title}
				visible={visible}
				keyModal={keyModal}
				dataType={dataType}
				sourceColumnFamily={families.source}
				targetColumnFamily={families.target}
				onOk={handleKeyModalSubmit}
				onCancel={handleHideKeyModal}
			/>
		);
	};

	const renderBatchModal = () => {
		let sPlaceholder;
		let sDesc;
		let tPlaceholder;
		let tDesc;
		switch (sourceSrcType) {
			case DATA_SOURCE_ENUM.FTP:
			case DATA_SOURCE_ENUM.HDFS:
			case DATA_SOURCE_ENUM.S3: {
				sPlaceholder = '0: STRING,\n1: INTEGER,...';
				sDesc = 'index: type, index: type';
				break;
			}
			case DATA_SOURCE_ENUM.HBASE: {
				sPlaceholder = 'cf1: field1: STRING,\ncf1: field2: INTEGER,...';
				sDesc = 'columnFamily: fieldName: type,';
				break;
			}
			default: {
				break;
			}
		}

		switch (targetSrcType) {
			case DATA_SOURCE_ENUM.FTP:
			case DATA_SOURCE_ENUM.HDFS:
			case DATA_SOURCE_ENUM.S3: {
				tPlaceholder = 'field1: STRING,\nfield2: INTEGER,...';
				tDesc = 'fieldName: type, fieldName: type';
				break;
			}
			case DATA_SOURCE_ENUM.HBASE: {
				tPlaceholder = 'cf1: field1: STRING,\ncf2: field2: INTEGER,...';
				tDesc = 'columnFamily: fieldName: type,';
				break;
			}
			default: {
				break;
			}
		}

		return (
			<>
				<BatchModal
					title="批量添加源表字段"
					desc={sDesc}
					columnFamily={families.source}
					sourceType={sourceSrcType}
					placeholder={sPlaceholder}
					defaultValue={batchModal.defaultValue}
					visible={batchModal.visible && batchModal.isReader}
					onOk={handleBatchAddSourceFields}
					onCancel={handleHideBatchModal}
				/>
				<BatchModal
					title="批量添加目标字段"
					desc={tDesc}
					columnFamily={families.target}
					placeholder={tPlaceholder}
					sourceType={targetSrcType}
					defaultValue={batchModal.defaultValue}
					visible={batchModal.visible && !batchModal.isReader}
					onOk={handleBatchAddTargetFields}
					onCancel={handleHideBatchModal}
				/>
			</>
		);
	};

	const getTableCols = () => {
		// ES 数据源：
		// - tableName 字段取自 indexType,
		// - schema 字段取自 index
		const ES_DATASOURCE = [DATA_SOURCE_ENUM.ES, DATA_SOURCE_ENUM.ES6, DATA_SOURCE_ENUM.ES7];
		const sourceSchema = ES_DATASOURCE.includes(sourceMap.type!)
			? sourceMap.index
			: sourceMap.schema;
		const sourceTable = ES_DATASOURCE.includes(sourceMap.type!)
			? sourceMap.indexType
			: sourceMap.table;

		// Hive，Impala 作为结果表时，需要获取分区字段
		const sourceType = sourceMap.type!;
		const sourcePart =
			+sourceType === DATA_SOURCE_ENUM.HIVE1X ||
			+sourceType === DATA_SOURCE_ENUM.HIVE ||
			+sourceType === DATA_SOURCE_ENUM.HIVE3X ||
			+sourceType === DATA_SOURCE_ENUM.SPARKTHRIFT;

		const targetSchema = ES_DATASOURCE.includes(targetMap.type!)
			? targetMap.index
			: targetMap.schema;
		const targetTable = ES_DATASOURCE.includes(targetMap.type!)
			? targetMap.indexType
			: targetMap.table;

		// Hive 作为结果表时，需要获取分区字段
		const targetType = targetMap.type!;
		const targetPart =
			+targetType === DATA_SOURCE_ENUM.HIVE1X ||
			+targetType === DATA_SOURCE_ENUM.HIVE ||
			+targetType === DATA_SOURCE_ENUM.HIVE3X ||
			+targetType === DATA_SOURCE_ENUM.SPARKTHRIFT;

		// table 和 schema 至少有一者必存在
		if (!sourceTable && !sourceSchema) return;
		if (!targetTable && !targetSchema) return;

		setLoading(true);
		Promise.all([
			Api.getOfflineTableColumn({
				sourceId: sourceMap.sourceId,
				schema: sourceSchema,
				tableName: Array.isArray(sourceTable) ? sourceTable[0] : sourceTable,
				isIncludePart: sourcePart,
			}),
			Api.getOfflineTableColumn({
				sourceId: targetMap.sourceId,
				schema: targetSchema,
				tableName: targetTable,
				isIncludePart: targetPart,
			}),
		])
			.then((results) => {
				if (results.every((res) => res.code === 1)) {
					const sourceMapCol = sourceMap.column || [];
					const targetMapCol = targetMap.column || [];

					// TODO: 暂时无法解决没有连线的自定义列或常量保存的问题
					// 取当前选中数据源的表字段和已连线的字段的并集作为 keymap 的数据
					// 因为可能存在常量或自定义列不存在在「数据源的表字段」中，需要从已连线的字段中获取
					const nextSource: IDataColumnsProps[] = results[0].data;
					sourceMapCol.forEach((col) => {
						if (!nextSource.find((s) => s.key === col.key)) {
							nextSource.push(col);
						}
					});

					// same as source
					const nextTarget: IDataColumnsProps[] = results[1].data;
					targetMapCol.forEach((col) => {
						if (!nextTarget.find((s) => s.key === col.key)) {
							nextTarget.push(col);
						}
					});
					setTableCols({ source: nextSource, target: nextTarget });
				}
			})
			.finally(() => {
				setLoading(false);
			});
	};

	useEffect(() => {
		getTableCols();
	}, []);

	useEffect(() => {
		// 设置step容器大小
		setCanvasInfo((f) => ({ ...f, W: getCanvasW() }));
		loadColumnFamily();
	}, []);

	useEffect(() => {
		select($canvas.current).selectAll('.dl, .dr, .lines').remove();
		dragSvg();
	}, [lines, canvasInfo, tableColumns, userColumns]);

	useEffect(() => {
		onLinesChanged?.(lines);
	}, [lines]);

	const sourceCol = useMemo(
		() => (tableColumns?.source || []).concat(userColumns?.source || []),
		[tableColumns, userColumns],
	);
	const targetCol = useMemo(
		() => (tableColumns?.target || []).concat(userColumns?.target || []),
		[tableColumns, userColumns],
	);

	const sourceSrcType = useMemo(() => sourceMap?.type, [sourceMap]);
	const targetSrcType = useMemo(() => targetMap?.type, [targetMap]);

	const H = useMemo(
		() => h * (Math.max(targetCol.length, sourceCol.length) + 1),
		[sourceCol.length, targetCol.length],
	);

	return (
		<Resize onResize={handleResize}>
			<div className="mx-20px">
				<p className="text-xs text-center">
					您要配置来源表与目标表的字段映射关系，通过连线将待同步的字段左右相连，也可以通过同行映射、同名映射批量完成映射
					&nbsp;
					{!lines.source.length && (
						<a onClick={keymapHelpModal}>如何快速配置字段映射？</a>
					)}
				</p>
				{/* {targetSrcType === DATA_SOURCE_ENUM.HBASE ? (
					<Row>
						<Col span={21} style={{ textAlign: 'center' }}>
							<div
								className="m-keymapbox"
								style={{
									width: W + 200,
									height: h - 18,
									marginTop: 18,
									zIndex: 3,
									display: 'inline-block',
								}}
							>
								<div
									className="pa"
									style={{
										left: W - (padding + w) + 100,
									}}
								>
									<span>rowkey</span>:
									{isFocus ? (
										<Input.TextArea
											autoFocus={isFocus}
											style={focusSty}
											defaultValue={get(targetMap, 'type.rowkey', '')}
											placeholder={rowkeyTxt}
											onChange={this.debounceRowkeyChange}
											onFocus={() => {
												this.setState({
													isFocus: true,
												});
											}}
											onBlur={this.handleBlurRowkeyCheck}
											autoSize={{
												minRows: 4,
												maxRows: 12,
											}}
										/>
									) : (
										<Input
											autoFocus={isFocus}
											style={focusSty}
											defaultValue={get(targetMap, 'type.rowkey', '')}
											placeholder={rowkeyTxt}
											onChange={this.debounceRowkeyChange}
											onFocus={() => {
												this.setState({
													isFocus: true,
												});
											}}
											onBlur={this.handleBlurRowkeyCheck}
										/>
									)}
								</div>
							</div>
						</Col>
					</Row>
				) : null} */}
				<Spin spinning={loading}>
					<Row gutter={12}>
						<Col span={readonly ? 24 : 21} className="text-center">
							<div
								className="m-keymapbox"
								style={{
									width: W,
									minHeight: H + 20,
								}}
							>
								{renderSource()}
								{renderTarget()}
								<svg
									ref={$canvas}
									width={W - 30 > w * 2 ? W - w * 2 + 30 : 0}
									height={H}
									className="m-keymapcanvas"
									style={{ left: w, top: padding }}
								>
									<defs>
										<marker
											id="arrow"
											markerUnits="strokeWidth"
											markerWidth="12"
											markerHeight="12"
											viewBox="0 0 12 12"
											refX="6"
											refY="6"
											orient="auto"
										>
											<path
												d="M2,3 L9,6 L2,9 L2,6 L2,3"
												fill="currentColor"
											/>
										</marker>
									</defs>
									<g>
										<line
											ref={$activeLine}
											x1="-10"
											y1="-10"
											x2="-10"
											y2="-10"
											stroke="currentColor"
											strokeWidth="2"
											markerEnd="url(#arrow)"
										/>
									</g>
								</svg>
							</div>
						</Col>
						<Col span={3}>
							{!readonly ? (
								<div className="m-buttons">
									<Button
										type={btnTypes.rowMap ? 'primary' : 'default'}
										onClick={handleRowMapClick}
									>
										{btnTypes.rowMap ? '取消同行映射' : '同行映射'}
									</Button>
									<br />
									<Button
										disabled={isHdfsType(sourceSrcType)}
										type={btnTypes.nameMap ? 'primary' : 'default'}
										onClick={handleNameMapClick}
									>
										{btnTypes.nameMap ? '取消同名映射' : '同名映射'}
									</Button>
									<br />
									{isHdfsType(targetSrcType) ? (
										<>
											<Button onClick={handleCopySourceCols}>
												拷贝源字段
											</Button>
											<br />
										</>
									) : (
										''
									)}
									{isHdfsType(sourceSrcType) ? (
										<Button onClick={handleCopyTargetCols}>拷贝目标字段</Button>
									) : (
										''
									)}
								</div>
							) : null}
						</Col>
					</Row>
				</Spin>
				{renderKeyModal()}
				{renderBatchModal()}
				<ConstModal
					visible={visibleConst}
					onOk={(col) => {
						onColsChanged?.(col, OPERATOR_TYPE.ADD, 'source');
						setConstVisible(false);
					}}
					onCancel={() => setConstVisible(false)}
				/>
				{!readonly && (
					<div className="steps-action" style={{ marginTop: 80 }}>
						<Space>
							<Button onClick={() => onNext?.(false)}>上一步</Button>
							<Button type="primary" onClick={handleSubmit}>
								下一步
							</Button>
						</Space>
					</div>
				)}
			</div>
		</Resize>
	);
}

/**
 * 获取step容器的大小，最小为450，其他情况为panel大小的5/6;
 */
function getCanvasW() {
	let w = 450;
	const canvas = document.querySelector('.dt-datasync-content');
	if (canvas) {
		const newW = (canvas.getBoundingClientRect().width / 6) * 5;
		if (newW > w) w = newW;
	}
	return w;
}

/**
 * 绘制字段旁边的拖拽点
 */
const renderDags = (
	container: SVGSVGElement | null,
	sourceCol: IDataColumnsProps[],
	targetCol: IDataColumnsProps[],
	canvasInfo: { w: number; h: number; W: number; padding: number },
) => {
	const { w, h, W, padding } = canvasInfo;

	select(container)
		.append('g')
		.attr('class', 'dl')
		.selectAll('g')
		.data(sourceCol)
		.enter()
		.append('g')
		.attr('class', 'col-dag-l')
		.append('circle')
		.attr('class', 'dag-circle')
		.attr('cx', () => padding)
		.attr('cy', (_, i) => h * (i + 1.5))
		.attr('r', 5)
		.attr('stroke-width', 2)
		.attr('stroke', '#fff')
		.attr('fill', 'currentColor');

	select(container)
		.append('g')
		.attr('class', 'dr')
		.selectAll('g')
		.data(targetCol)
		.enter()
		.append('g')
		.attr('class', 'col-dag-r')
		.append('circle')
		.attr('class', 'dag-circle')
		/**
		 * W-w*2代表绘制区域的宽度
		 */
		.attr('cx', () => W - w * 2 - padding)
		.attr('cy', (_, i) => h * (i + 1.5))
		.attr('r', 5)
		.attr('stroke-width', 2)
		.attr('stroke', '#fff')
		.attr('fill', 'currentColor');
};

function renderLines(
	container: SVGSVGElement | null,
	keymap: { source: IDataColumnsProps[]; target: IDataColumnsProps[] },
	canvasInfo: { w: number; h: number; W: number; padding: number },
) {
	const { w, h, W, padding } = canvasInfo;
	const { source, target } = keymap;
	const $dagL = select(container).selectAll<SVGGElement, IDataColumnsProps>('.col-dag-l');
	const $dagR = select(container).selectAll<SVGGElement, IDataColumnsProps>('.col-dag-r');

	const posArr: {
		s: { x: number; y: number };
		e: { x: number; y: number };
		dl: IDataColumnsProps;
		dr: IDataColumnsProps;
	}[] = [];

	/**
	 * source中的元素 keyObj 类型：
	 * if(sourceSrcType === 1, 2, 3) string
	 * if( === 6) { index, type }
	 */
	source.forEach((keyObj, ii) => {
		$dagL.each((dl, i) => {
			let sx: number;
			let sy: number;
			let ex: number;
			let ey: number;

			if (isFieldMatch(dl, keyObj)) {
				sx = padding;
				sy = (i + 1.5) * h;

				$dagR.each((dr, j) => {
					/**
					 * target[ii] 类型：
					 * if(targetSrcType === 1, 2, 3) string
					 * if( === 6)  obj{ key, type }
					 */
					if (isFieldMatch(dr, target[ii])) {
						ex = W - w * 2 - padding;
						ey = (j + 1.5) * h;

						posArr.push({
							s: { x: sx, y: sy },
							e: { x: ex, y: ey },
							dl: keyObj,
							dr: target[ii],
						});
					}
				});
			}
		});
	});

	const mapline = select(container)
		.append('g')
		.attr('class', 'lines')
		.selectAll('g')
		.data(posArr)
		.enter()
		.append('g')
		.attr('class', 'mapline');

	mapline
		.append('line')
		.attr('x1', (d) => d.s.x)
		.attr('y1', (d) => d.s.y)
		.attr('x2', (d) => d.e.x)
		.attr('y2', (d) => d.e.y)
		.attr('stroke', 'currentColor')
		.attr('stroke-width', 2)
		.attr('marker-end', 'url(#arrow)');
}

function bindEvents(
	container: SVGSVGElement | null,
	lineContainer: SVGGElement | null,
	canvasInfo: { w: number; h: number; W: number; padding: number },
	onAddKeys?: (source: IDataColumnsProps, target: IDataColumnsProps) => void,
	onDeleteKeys?: (source: IDataColumnsProps, target: IDataColumnsProps) => void,
) {
	const { w, h, W, padding } = canvasInfo;
	const $line = select(lineContainer);
	const $dagL = select(container).selectAll<SVGGElement, IDataColumnsProps>('.col-dag-l');
	let isMouseDown = false;
	let sourceKeyObj: IDataColumnsProps | undefined;
	let targetKeyObj: IDataColumnsProps | undefined;

	$dagL.on('mousedown', (d, i) => {
		const sx = padding;
		const sy = (i + 1.5) * h;
		$line.attr('x1', sx).attr('y1', sy).attr('x2', sx).attr('y2', sy);

		sourceKeyObj = d;
		isMouseDown = true;
	});

	const $canvas = select(container);
	$canvas
		.on('mousemove', () => {
			if (isMouseDown) {
				const xy = mouse($canvas.node()!);
				const [ex, ey] = xy;
				const threholdX = W - w * 2 - padding * 2;

				if (ex < threholdX) {
					$line.attr('x2', xy[0]).attr('y2', xy[1]);
				} else {
					const $y = (Math.floor(ey / h) + 0.5) * h;
					const $x = threholdX + padding;
					$line.attr('x2', $x).attr('y2', $y);
				}
			}
		})
		.on('mouseup', () => {
			if (isMouseDown) {
				const xy = mouse($canvas.node()!);
				const [ex, ey] = xy;
				const threholdX = W - w * 2 - padding * 2;

				if (ex < threholdX) resetActiveLine(lineContainer);
				else {
					const tidx = Math.floor(ey / h) - 1;
					const $dagR = select(container).selectAll('.col-dag-r');

					$dagR.each((d: any, i: any) => {
						if (i === tidx) {
							targetKeyObj = d;
						}
					});
				}
			}
			if (sourceKeyObj && targetKeyObj) {
				/**
				 * 存储连线
				 */
				onAddKeys?.(sourceKeyObj, targetKeyObj);
				resetActiveLine(lineContainer);
			}

			isMouseDown = false;
		});

	$canvas
		.selectAll<
			SVGGElement,
			{
				s: { x: number; y: number };
				e: { x: number; y: number };
				dl: IDataColumnsProps;
				dr: IDataColumnsProps;
			}
		>('.mapline')
		.on('mouseover', (_, i, nodes) => {
			select(nodes[i]).select('line').attr('stroke-width', 3).attr('stroke', '#2491F7');
		})
		.on('mouseout', (_, i, nodes) => {
			select(nodes[i]).select('line').attr('stroke-width', 2).attr('stroke', '#2491F7');
		})
		.on('click', (d) => {
			/**
			 * 删除连线
			 */
			onDeleteKeys?.(d.dl, d.dr);
		});
}

function resetActiveLine(container: SVGGElement | null) {
	select(container).attr('x1', -10).attr('y1', -10).attr('x2', -10).attr('y2', -10);
}
