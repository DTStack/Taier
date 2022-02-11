// eslint-disable-next-line max-classes-per-file
import React from 'react';
import { Form, Icon } from '@ant-design/compatible';
import { connect } from 'react-redux';
import { Input, Select, Button, Table, message, Row, Col, Spin, AutoComplete } from 'antd';
import { isEmpty, debounce, get, isArray } from 'lodash';
import assign from 'object-assign';
import { Utils } from '@dtinsight/dt-utils';
import TableCell from '@/components/tableCell';
import {
	DATA_SOURCE_ENUM,
	DATA_SOURCE_TEXT,
	formItemLayout,
	SUPPROT_SUB_LIBRARY_DB_ARRAY,
	RDB_TYPE_ARRAY,
} from '@/constant';
import { dataSyncAction, sourceMapAction, workbenchAction } from '@/reducer/dataSync/actionType';
import { isRDB, filterValueOption, formJsonValidator } from '@/utils';
import ajax from '@/api';
import { API } from '../../api/dataSource';
import HelpDoc from '../../components/helpDoc';
import singletonNotification from '../notification';

const FormItem = Form.Item;
const { Option } = Select;
const { TextArea } = Input;

class SourceForm extends React.Component<any, any> {
	_isMounted = false;
	isMysqlTable = false;
	constructor(props: any) {
		super(props);
		this.state = {
			tableListMap: {},
			showPreview: false,
			dataSource: [],
			columns: [],
			tablePartitionList: [], // 表分区列表
			incrementColumns: [], // 增量字段
			loading: false, // 请求
			tableListSearch: {},
			schemaList: [], // schema数据
			schemaId: '', // schema id
			fetching: false, // 模糊查询后端接口loading动画
			kingbaseId: '', // schema所属数据源 id
			tableListLoading: false,
			previewPath: '',
		};
	}

	timerID: any;
	formRef: any;

	saveFormRef = (formRef: any) => {
		this.formRef = formRef;
	};

	componentDidMount() {
		this._isMounted = true;
		const { sourceMap, form } = this.props;
		const { sourceList } = sourceMap;
		const dataSourceType = sourceMap.type && sourceMap.type.type;
		const schema =
			(isEmpty(sourceMap)
				? ''
				: sourceMap?.schema
				? sourceMap?.schema
				: sourceMap?.type?.schema) || form.getFieldValue('schema');
		let tableName = '';
		let sourceId = '';
		if (sourceList) {
			for (let i = 0; i < sourceList.length; i++) {
				const source = sourceList[i];
				if (!source.sourceId) {
					return;
				}
				if (
					dataSourceType === DATA_SOURCE_ENUM.POSTGRESQL ||
					dataSourceType === DATA_SOURCE_ENUM.ORACLE
				) {
					this.getSchemaList(source.sourceId);
					schema
						? this.getTableList(source.sourceId, schema)
						: this.getTableList(source.sourceId);
				} else {
					this.getTableList(source.sourceId);
					if (source.tables && i === 0) {
						tableName = source.tables;
						sourceId = source.sourceId;
					}
				}
			}
		}

		if (tableName && sourceId && RDB_TYPE_ARRAY.indexOf(dataSourceType) > -1) {
			this.getCopate(sourceId, tableName);
			this.loadIncrementColumn(tableName);
		}
	}

	componentWillUnmount() {
		this._isMounted = false;
		clearInterval(this.timerID);
	}

	loadIncrementColumn = async (tableName: any, schema?: any) => {
		const { sourceMap, form } = this.props;
		// eslint-disable-next-line
		schema = schema || form.getFieldValue('schema');
		// eslint-disable-next-line @typescript-eslint/no-unused-vars
		const value = {
			sourceId: sourceMap.sourceId,
			tableName,
		};
		// TODO, without increment cols
		// const res = await ajax.getIncrementColumns(
		// 	schema
		// 		? Object.assign(value, {
		// 				schema,
		// 		  })
		// 		: value,
		// );

		// if (res.code === 1) {
		// 	this.setState({
		// 		incrementColumns: res.data || [],
		// 	});
		// }
	};

	getSchemaList = (sourceId: any, schema?: any) => {
		this.setState(
			{
				kingbaseId: sourceId,
			},
			() => {
				API.getAllSchemas({
					sourceId,
					schema,
				}).then((res: any) => {
					if (res.code === 1) {
						this.setState({
							schemaList: res.data || [],
						});
					}
				});
			},
		);
	};

	getTableList = (sourceId: any, schema?: any, str?: any) => {
		const ctx = this;
		const { sourceMap, handleTableCopateChange } = this.props;
		handleTableCopateChange([]);
		if (sourceMap.type && sourceMap.type.type === DATA_SOURCE_ENUM.HDFS) {
			return;
		}

		this.isMysqlTable = sourceMap.type?.type === DATA_SOURCE_ENUM.MYSQL;
		const { tableListSearch, tableListMap } = this.state;
		this.setState(
			{
				showPreview: false,
				tableListMap: {
					...tableListMap,
					[sourceId]: [],
				},
				tableListSearch: {
					...tableListSearch,
					[sourceId]: [],
				},
				schemaId: schema,
				name: str,
				tableListLoading: !str,
				fetching: !!str,
			},
			() => {
				API.getOfflineTableList({
					sourceId,
					schema,
					isSys: false,
					name: str,
					isRead: true,
				})
					.then((res: any) => {
						if (res && res.code === 1) {
							if (ctx._isMounted) {
								const { data = [] } = res;
								let arr = data;
								// 前端本地模糊搜索，最多展示 200 条
								if (data.length && data.length > 200) {
									arr = data.slice(0, 200);
								}
								ctx.setState({
									tableListMap: {
										...tableListMap,
										[sourceId]: res.data || [],
									},
									tableListSearch: {
										...tableListSearch,
										[sourceId]: arr || [],
									},
								});
							}
						}
					})
					.finally(() => {
						this.setState({
							tableListLoading: false,
							fetching: false,
						});
					});
			},
		);
	};

	onSearchTable = (str: any, sourceId: any) => {
		const { tableListMap, tableListSearch } = this.state;
		let arr = tableListMap[sourceId].filter((item: any) => item.indexOf(str) !== -1);
		if (arr.length && arr.length > 200) {
			arr = arr.slice(0, 200);
		}
		this.setState({
			tableListSearch: {
				...tableListSearch,
				[sourceId]: arr || [],
			},
		});
	};

	getTableColumn = (tableName: any, type: any) => {
		const { form, sourceMap, handleTableColumnChange, handleTableCopateChange } = this.props;

		if (tableName instanceof Array) {
			tableName = tableName[0];
		}

		const sourceType = get(sourceMap, 'type.type', null);
		const { getFieldValue } = form;
		const schema = getFieldValue('schema');

		if (!tableName) {
			handleTableCopateChange([]);
			form.setFields({
				splitPK: {
					value: '',
				},
			});
			return false;
		}

		if (isRDB(sourceType) || sourceType === DATA_SOURCE_ENUM.POSTGRESQL) {
			this.getCopate(sourceMap.sourceId, tableName);
		}
		// Hive，Impala 作为结果表时，需要获取分区字段
		const includePart =
			+sourceType === DATA_SOURCE_ENUM.HIVE1X ||
			+sourceType === DATA_SOURCE_ENUM.HIVE ||
			+sourceType === DATA_SOURCE_ENUM.HIVE3X ||
			+sourceType === DATA_SOURCE_ENUM.SPARKTHRIFT;

		API.getOfflineTableColumn({
			sourceId: sourceMap.sourceId,
			schema,
			tableName,
			isIncludePart: includePart,
		})
			.then((res) => {
				if (res.code === 1) {
					handleTableColumnChange(res.data);
				} else {
					handleTableColumnChange([]);
				}
			})
			.finally(() => {
				this.setState({
					loading: false,
				});
			});
	};

	getCopate(sourceId: any, tableName: any) {
		const { handleTableCopateChange, form } = this.props;
		const { getFieldValue } = form;
		const schema = getFieldValue('schema');
		if (tableName instanceof Array) {
			tableName = tableName[0];
		}
		API.getOfflineColumnForSyncopate({
			sourceId,
			tableName,
			schema,
		}).then((res: any) => {
			if (res.code === 1) {
				handleTableCopateChange(res.data);
			} else {
				handleTableCopateChange([]);
			}
		});
	}

	getDataObjById(id: any) {
		const { dataSourceList } = this.props;
		const data = dataSourceList.filter((src: any) => {
			return `${src.dataInfoId}` === id;
		})[0];
		return data;
	}

	changeSource(value: any, option: any) {
		const { handleSourceChange } = this.props;
		const { dataType } = option.props;
		setTimeout(() => {
			// KINGBASE/ORACLE需要加schema字段
			(dataType === DATA_SOURCE_ENUM.ORACLE || dataType === DATA_SOURCE_ENUM.POSTGRESQL) &&
				this.getSchemaList(value);

			dataType !== DATA_SOURCE_ENUM.KINGBASE8 &&
				dataType !== DATA_SOURCE_ENUM.S3 &&
				dataType !== DATA_SOURCE_ENUM.ADB_FOR_PG &&
				this.getTableList(value);
		}, 0);

		handleSourceChange(this.getDataObjById(value));
		this.resetTable();
	}

	resetTable(key?: any) {
		const { form } = this.props;
		this.changeTable('');
		// 这边先隐藏结点，然后再reset，再显示。不然会有一个组件自带bug。
		this.setState(
			{
				selectHack: true,
			},
			() => {
				if (key) {
					form.resetFields([key]);
				} else {
					form.resetFields(['table']);
					form.resetFields(['splitPK']);
				}
				this.setState({
					selectHack: false,
				});
			},
		);
	}

	getTableData = (type: any, value: any, sourceKey?: any) => {
		if (value) {
			this.setState({
				loading: true,
			});
			const { form } = this.props;
			const formData = form.getFieldsValue();
			// 加载增量模式字段
			if (this.props.isIncrementMode) {
				this.loadIncrementColumn(value, formData?.schema);
			}
			this.getTableColumn(value, type);
		}
		// 不可简化sourceKey, 在submitForm上对应的不同的逻辑，即第四个参数对应的逻辑不同，在不同场景可能不存在第四个参数，不能简化
		this.submitForm(null, sourceKey, value, sourceKey);
		this.setState({
			showPreview: false,
		});
	};

	changeTable(type?: any, value?: any, sourceKey?: any) {
		const { tableListMap } = this.state;
		const { sourceMap, form } = this.props;
		const targetSource = [DATA_SOURCE_ENUM.POSTGRESQL];
		const schema = form.getFieldValue('schema');
		if (
			targetSource.includes(sourceMap.type?.type) &&
			(!Array.isArray(tableListMap[sourceMap.sourceId]) ||
				tableListMap[sourceMap.sourceId].length === 0 ||
				!tableListMap[sourceMap.sourceId]?.includes(value))
		) {
			form.setFieldsValue({ table: undefined });
			this.getTableList(sourceMap?.sourceId, schema, '');
			return;
		}

		if (value) {
			this.setState({
				loading: true,
			});

			this.getTableColumn(value, type);
			// 如果源为hive, 则加载分区字段
			this.getHivePartions(value);
			// 加载增量模式字段
			if (this.props.isIncrementMode) {
				this.loadIncrementColumn(value, schema);
			}
		}
		// 不可简化sourceKey, 在submitForm上对应的不同的逻辑，即第四个参数对应的逻辑不同，在不同场景可能不存在第四个参数，不能简化
		this.submitForm(null, sourceKey, value, sourceKey);
		this.setState({
			showPreview: false,
		});
	}

	getHivePartions = (tableName: any) => {
		const { sourceMap, form } = this.props;

		if (
			sourceMap.type &&
			sourceMap.type.type !== DATA_SOURCE_ENUM.HIVE &&
			sourceMap.type.type !== DATA_SOURCE_ENUM.HIVE3X &&
			sourceMap.type.type !== DATA_SOURCE_ENUM.SPARKTHRIFT &&
			sourceMap.type.type !== DATA_SOURCE_ENUM.HIVE1X
		) {
			return;
		}
		// Reset partition
		form.setFieldsValue({ partition: '' });
		API.getHivePartitions({
			sourceId: sourceMap.sourceId,
			tableName,
		}).then((res: any) => {
			this.setState({
				tablePartitionList: res.data || [],
			});
		});
	};

	validatePath = (rule: any, value: any, callback: any) => {
		const { handleTableColumnChange, form } = this.props;
		const { getFieldValue } = form;
		const sourceId = getFieldValue('sourceId');
		if (getFieldValue('fileType') === 'orc') {
			API.getOfflineTableColumn({
				sourceId,
				tableName: value,
			}).then((res: any) => {
				if (res.code === 1) {
					handleTableColumnChange(res.data);
					callback();
				}
				/* eslint-disable-next-line */
				callback('该路径无效！');
			});
		} else {
			callback();
		}
	};

	validateChineseCharacter = (data: any) => {
		const reg = /(，|。|；|[\u4e00-\u9fa5]+)/; // 中文字符，中文逗号，句号，分号
		let has = false;
		const fieldsName: any = [];
		if (data.path && reg.test(data.path)) {
			has = true;
			fieldsName.push('路径');
		}
		if (data.fieldDelimiter && reg.test(data.fieldDelimiter)) {
			has = true;
			fieldsName.push('列分隔符');
		}
		if (has) {
			singletonNotification(
				'提示',
				`${fieldsName.join('、')}参数中有包含中文或者中文标点符号！`,
				'warning',
			);
		}
	};

	submitForm(event?: any, sourceKey?: any, value?: any, key?: any) {
		const { form, handleSourceMapChange, sourceMap } = this.props;
		let tempObj: any = {};
		if (key) {
			tempObj = {
				extTable: assign({}, { ...sourceMap.type.extTable }, { [key]: value }),
			};
		} else if (value) {
			tempObj = {
				table: value,
			};
		}

		this.timerID = setTimeout(() => {
			const values = form.getFieldsValue();
			// clean no use property
			for (const key in values) {
				if (values[key] === '') {
					values[key] = undefined;
				}
			}
			// 去空格
			if (values.partition) {
				values.partition = Utils.trimAll(values.partition);
			}
			if (values.path && !isArray(values.path)) {
				values.path = Utils.trimAll(values.path);
			}
			const srcmap = assign(
				{},
				sourceMap.type,
				{ ...values, ...tempObj },
				{
					src: this.getDataObjById(values.sourceId),
				},
			);
			handleSourceMapChange(srcmap, sourceKey);
		}, 0);
		// 需放在定时器外为了保证设置值在getFieldsValue之前
		if (value && key) {
			form.setFieldsValue({ [`extTable.${key}`]: value });
		} else if (value) {
			form.setFieldsValue({ table: value });
		}
	}

	next(cb: any) {
		const { form, sourceMap, saveDataSyncToTab, dataSync, currentTabData } = this.props;

		let validateFields = null;
		if (sourceMap?.type?.type === DATA_SOURCE_ENUM.HDFS) {
			validateFields = ['sourceId', 'path', 'fileType'];
			if (sourceMap.type.fileType === 'text') {
				validateFields.push('encoding');
			}
		}
		const formData = form.getFieldsValue();

		form.validateFieldsAndScroll(validateFields, { force: true }, (err: any, values: any) => {
			if (!err) {
				// 校验中文字符，如果有则发出警告
				this.validateChineseCharacter(formData);
				saveDataSyncToTab({
					id: currentTabData.id,
					data: dataSync,
				});
				/* eslint-disable-next-line */
				cb.call(null, 1);
			}
		});
	}

	getPopupContainer() {
		return this.props.dataSyncRef;
	}

	render() {
		const { getFieldDecorator } = this.props.form;
		const { sourceMap, dataSourceList, navtoStep, isIncrementMode } = this.props;

		const disablePreview =
			isEmpty(sourceMap) || sourceMap?.type?.type === DATA_SOURCE_ENUM.HDFS;
		const { tableListLoading } = this.state;
		const { getPopupContainer } = this.props;
		const disableFix = { disabled: disablePreview };
		return (
			<div className="g-step1">
				<Spin spinning={tableListLoading}>
					<Form>
						<FormItem {...formItemLayout} label="数据源">
							{getFieldDecorator('sourceId', {
								rules: [
									{
										required: true,
										message: '数据源为必填项',
									},
								],
								initialValue: isEmpty(sourceMap) ? '' : `${sourceMap.sourceId}`,
							})(
								<Select
									getPopupContainer={getPopupContainer}
									showSearch
									onSelect={this.changeSource.bind(this)}
									optionFilterProp="name"
								>
									{dataSourceList.map(
										(src: {
											dataName: string;
											dataTypeCode: DATA_SOURCE_ENUM;
											dataInfoId: string;
										}) => {
											const title = `${src.dataName}（${
												DATA_SOURCE_TEXT[src.dataTypeCode]
											}）`;
											const disableSelect =
												src.dataTypeCode === DATA_SOURCE_ENUM.ES ||
												src.dataTypeCode === DATA_SOURCE_ENUM.REDIS ||
												src.dataTypeCode === DATA_SOURCE_ENUM.MONGODB ||
												// 增量模式需要禁用非关系型数据库
												(isIncrementMode && !isRDB(src.dataTypeCode));

											return (
												<Option
													dataType={src.dataTypeCode}
													key={src.dataInfoId}
													name={src.dataName}
													value={`${src.dataInfoId}`}
													disabled={disableSelect}
												>
													{title}
												</Option>
											);
										},
									)}
								</Select>,
							)}
						</FormItem>
						{this.renderDynamicForm()}
						{!isEmpty(sourceMap) ? (
							<FormItem {...formItemLayout} label={<span>高级配置<HelpDoc doc={'dataSyncExtralConfigHelp'} /></span>}>
								{getFieldDecorator('extralConfig', {
									rules: [
										{
											validator: formJsonValidator,
										},
									],
									initialValue: get(sourceMap, 'extralConfig', ''),
								})(
									<TextArea
										onChange={this.submitForm.bind(this)}
										placeholder={
											'以JSON格式添加高级参数，例如对关系型数据库可配置fetchSize'
										}
										autoSize={{ minRows: 2, maxRows: 6 }}
									/>,
								)}
							</FormItem>
						) : null}
					</Form>
					<div
						className="m-datapreview"
						style={{
							width: '90%',
							margin: '0 auto',
							overflow: 'auto',
							textAlign: 'center',
						}}
					>
						<p style={{ cursor: 'pointer', marginBottom: 10 }}>
							<a
								{...disableFix}
								href="javascript:void(0)"
								onClick={this.loadPreview.bind(this)}
							>
								数据预览
								{this.state.showPreview ? <Icon type="up" /> : <Icon type="down" />}
							</a>
						</p>
						{this.state.showPreview ? (
							<Table
								dataSource={this.state.dataSource}
								columns={this.state.columns}
								scroll={{
									x: this.state.columns.reduce((a: any, b: any) => {
										return a + b.width;
									}, 0),
								}}
								pagination={false}
								bordered={false}
							/>
						) : null}
					</div>
					{!this.props.readonly && (
						<div className="steps-action">
							<Button
								loading={this.state.loading}
								type="primary"
								onClick={() => setTimeout(() => this.next(navtoStep), 600)}
							>
								下一步
							</Button>
						</div>
					)}
				</Spin>
			</div>
		);
	}

	loadPreview() {
		const { showPreview } = this.state;
		const { form } = this.props;
		const sourceId = form.getFieldValue('sourceId');
		const schema = form.getFieldValue('schema');
		let tableName = form.getFieldValue('table');

		if (!sourceId || !tableName) {
			message.error('数据源或表名缺失');
			return;
		}
		if (tableName instanceof Array) {
			tableName = tableName[0];
		}
		if (!showPreview) {
			API.getDataPreview({
				sourceId,
				tableName,
				schema,
			}).then((res: any) => {
				if (res.code === 1) {
					const { columnList, dataList } = res.data;

					const columns = columnList.map((s: any) => {
						return {
							title: s,
							dataIndex: s,
							key: s,
							width: 20 + s.length * 10,
							render: (text: string) => {
								return <TableCell style={{ textIndent: 'none' }} value={text} />;
							},
						};
					});
					const dataSource = dataList.map((arr: any, i: any) => {
						const o: any = {};
						for (let j = 0; j < arr.length; j++) {
							o.key = i;
							o[columnList[j]] = arr[j];
						}
						return o;
					});

					this.setState(
						{
							columns,
							dataSource,
						},
						() => {
							this.setState({
								showPreview: true,
							});
						},
					);
				}
			});
		} else {
			this.setState({
				showPreview: false,
			});
		}
	}

	debounceTableSearch = debounce(this.changeTable, 500, { maxWait: 2000 });

	debounceTableNameSearch = debounce(this.getTableList, 500, {
		maxWait: 2000,
	});

	renderDynamicForm = () => {
		const {
			selectHack,
			tableListMap,
			tableListSearch,
			schemaList,
			kingbaseId,
			schemaId,
			fetching,
		} = this.state;
		const { sourceMap, isIncrementMode, form } = this.props;
		const { getFieldDecorator, getFieldValue } = form;
		const { getPopupContainer } = this.props;
		const fileType = (sourceMap.type && sourceMap.type.fileType) || 'text';
		const haveChineseQuote = !!(
			sourceMap &&
			sourceMap.type &&
			/(‘|’|”|“)/.test(sourceMap.type.where)
		);
		// 非增量模式
		const supportSubLibrary =
			SUPPROT_SUB_LIBRARY_DB_ARRAY.indexOf(
				sourceMap && sourceMap.sourceList && sourceMap.sourceList[0].type,
			) > -1 && !isIncrementMode;
		let formItem: any;

		if (isEmpty(sourceMap)) return null;

		switch (sourceMap?.type?.type) {
			case DATA_SOURCE_ENUM.MYSQL: {
				const tableValue = isEmpty(sourceMap)
					? ''
					: supportSubLibrary
					? sourceMap.sourceList[0].tables
					: sourceMap.type.table;
				formItem = [
					!selectHack ? (
						<div key={DATA_SOURCE_ENUM.MYSQL}>
							<FormItem {...formItemLayout} label={'表名(批量)'} key="rdbtable">
								{getFieldDecorator('table', {
									rules: [
										{
											required: true,
											message: '数据源表为必选项！',
										},
									],
									initialValue: tableValue,
								})(
									<Select
										getPopupContainer={getPopupContainer}
										mode={'multiple'}
										showSearch
										{...{ showArrow: true }}
										onSelect={this.debounceTableSearch.bind(
											this,
											sourceMap.type.type,
										)}
										onChange={(val: any) =>
											this.debounceTableSearch(sourceMap.type.type, val)
										}
										onBlur={() => {
											this.changeTable(sourceMap.type.type);
										}}
										optionFilterProp="value"
										filterOption={false}
										notFoundContent={fetching ? <Spin size="small" /> : null}
										onSearch={(str: any) => {
											this.debounceTableNameSearch(
												sourceMap.sourceId,
												null,
												str,
											);
										}}
									>
										{(tableListSearch[sourceMap.sourceId] || []).map(
											(table: any) => {
												return (
													<Option key={`rdb-${table}`} value={table}>
														{table}
													</Option>
												);
											},
										)}
										{/* <Option key={'rdb-1'} value={1}>
                                            {1}
                                        </Option> */}
									</Select>,
								)}
							</FormItem>
						</div>
					) : null,
					<FormItem
						{...formItemLayout}
						label={
							<span>
								数据过滤
								<HelpDoc doc="dataFilterDoc" />
							</span>
						}
						key="where"
					>
						{getFieldDecorator('where', {
							rules: [
								{
									max: 1000,
									message: '过滤语句不可超过1000个字符!',
								},
							],
							initialValue: isEmpty(sourceMap) ? '' : sourceMap.type.where,
						})(
							<Input.TextArea
								placeholder="请参考相关SQL语法填写where过滤语句（不要填写where关键字）。该过滤语句通常用作增量同步"
								onChange={this.submitForm.bind(this)}
							/>,
						)}
					</FormItem>,
					haveChineseQuote && (
						<Row className="form-item-follow-text">
							<Col
								style={{ textAlign: 'left' }}
								span={formItemLayout.wrapperCol.sm.span}
								offset={formItemLayout.labelCol.sm.span}
							>
								<p className="warning-color">当前输入含有中文引号</p>
							</Col>
						</Row>
					),
					<FormItem
						{...formItemLayout}
						label={
							<span>
								切分键
								<HelpDoc doc="selectKey" />
							</span>
						}
						key="splitPK"
					>
						{getFieldDecorator('splitPK', {
							rules: [],
							initialValue: isEmpty(sourceMap) ? '' : sourceMap.type.splitPK,
						})(
							<Select
								getPopupContainer={getPopupContainer}
								showSearch
								{...{ showArrow: true }}
								allowClear={true}
								onChange={this.submitForm.bind(this)}
							>
								{(
									(sourceMap.copate &&
										sourceMap.copate
											.map((v: any) => v.key)
											.filter(
												(v: any, index: any, self: any) =>
													self.indexOf(v) === index,
											)) ||
									[]
								).map((copateValue: any, index: any) => {
									return (
										<Option key={`copate-${index}`} value={copateValue}>
											{copateValue}
										</Option>
									);
								})}
							</Select>,
						)}
					</FormItem>,
				];
				break;
			}
			case DATA_SOURCE_ENUM.ORACLE: {
				const tableValue = isEmpty(sourceMap) ? '' : sourceMap.type.table;
				formItem = [
					!selectHack ? (
						<div key={DATA_SOURCE_ENUM.ORACLE}>
							<FormItem {...formItemLayout} label="schema" key="schema">
								{getFieldDecorator('schema', {
									rules: [],
									initialValue: isEmpty(sourceMap)
										? ''
										: sourceMap?.schema
										? sourceMap?.schema
										: sourceMap.type.schema,
								})(
									<Select
										showSearch
										{...{ showArrow: true }}
										allowClear={true}
										onChange={(val: any) => {
											this.getTableList(kingbaseId, val);
											form.setFieldsValue({
												table: '',
												syncModel: '',
											});
										}}
									>
										{schemaList.map((copateValue: any, index: any) => {
											return (
												<Option key={`copate-${index}`} value={copateValue}>
													{/* ORACLE数据库单独考虑ROW_NUMBER() 这个函数， 展示去除括号 */}
													{copateValue === 'ROW_NUMBER()'
														? 'ROW_NUMBER'
														: copateValue}
												</Option>
											);
										})}
									</Select>,
								)}
							</FormItem>
							<FormItem {...formItemLayout} label="表名" key="rdbtable">
								{getFieldDecorator('table', {
									rules: [
										{
											required: true,
											message: '数据源表为必选项！',
										},
									],
									initialValue: tableValue,
								})(
									<Select
										disabled={this.isMysqlTable}
										getPopupContainer={getPopupContainer}
										showSearch
										{...{ showArrow: true }}
										onSelect={this.getTableData.bind(this, sourceMap.type.type)}
										notFoundContent={fetching ? <Spin size="small" /> : null}
										filterOption={false}
										onSearch={(val: any) =>
											this.debounceTableNameSearch(kingbaseId, schemaId, val)
										}
									>
										{(tableListMap[sourceMap.sourceId] || []).map(
											(table: any) => {
												return (
													<Option key={`rdb-${table}`} value={table}>
														{table}
													</Option>
												);
											},
										)}
									</Select>,
								)}
							</FormItem>
						</div>
					) : null,
					<FormItem
						{...formItemLayout}
						label={
							<span>
								数据过滤
								<HelpDoc doc="dataFilterDoc" />
							</span>
						}
						key="where"
					>
						{getFieldDecorator('where', {
							rules: [
								{
									max: 1000,
									message: '过滤语句不可超过1000个字符!',
								},
							],
							initialValue: isEmpty(sourceMap) ? '' : sourceMap.type.where,
						})(
							<Input
								type="textarea"
								placeholder="请参考相关SQL语法填写where过滤语句（不要填写where关键字）。该过滤语句通常用作增量同步"
								onChange={this.submitForm.bind(this)}
							/>,
						)}
					</FormItem>,
					haveChineseQuote && (
						<Row className="form-item-follow-text">
							<Col
								style={{ textAlign: 'left' }}
								span={formItemLayout.wrapperCol.sm.span}
								offset={formItemLayout.labelCol.sm.span}
							>
								<p className="warning-color">当前输入含有中文引号</p>
							</Col>
						</Row>
					),
					<FormItem
						{...formItemLayout}
						label={
							<span>
								切分键
								<HelpDoc doc="selectKey" />
							</span>
						}
						key="splitPK"
					>
						{getFieldDecorator('splitPK', {
							rules: [],
							initialValue: isEmpty(sourceMap) ? '' : sourceMap.type.splitPK,
						})(
							<Select
								getPopupContainer={getPopupContainer}
								showSearch
								{...{ showArrow: true }}
								allowClear={true}
								onChange={this.submitForm.bind(this)}
							>
								{(
									(sourceMap.copate &&
										sourceMap.copate
											.map((v: any) => v.key)
											.filter(
												(v: any, index: any, self: any) =>
													self.indexOf(v) === index,
											)) ||
									[]
								).map((copateValue: any, index: any) => {
									return (
										<Option key={`copate-${index}`} value={copateValue}>
											{/* ORACLE数据库单独考虑ROW_NUMBER() 这个函数， 展示去除括号 */}
											{copateValue === 'ROW_NUMBER()'
												? 'ROW_NUMBER'
												: copateValue}
										</Option>
									);
								})}
							</Select>,
						)}
					</FormItem>,
				];
				break;
			}
			case DATA_SOURCE_ENUM.POSTGRESQL: {
				const tableValue = isEmpty(sourceMap)
					? ''
					: supportSubLibrary
					? sourceMap.sourceList[0].tables
					: sourceMap.type.table;
				formItem = [
					!selectHack ? (
						<div key={DATA_SOURCE_ENUM.POSTGRESQL}>
							<FormItem {...formItemLayout} label="schema" key="schema">
								{getFieldDecorator('schema', {
									initialValue: isEmpty(sourceMap)
										? ''
										: sourceMap?.schema
										? sourceMap?.schema
										: sourceMap.type.schema,
								})(
									<Select
										showSearch
										{...{ showArrow: true }}
										allowClear={true}
										onChange={(val: any) => {
											this.getTableList(kingbaseId, val);
											form.setFieldsValue({
												table: '',
												syncModel: '',
												splitPK: undefined,
											});
											this.setState({
												tableListMap: {},
											});
										}}
									>
										{schemaList.map((copateValue: any, index: any) => {
											return (
												<Option key={`copate-${index}`} value={copateValue}>
													{copateValue}
												</Option>
											);
										})}
									</Select>,
								)}
							</FormItem>
							{supportSubLibrary ? (
								<FormItem {...formItemLayout} label="表名" key="rdbtable">
									{getFieldDecorator('table', {
										rules: [
											{
												required: true,
												message: '数据源表为必选项！',
											},
										],
										initialValue: tableValue,
									})(
										<Select
											getPopupContainer={getPopupContainer}
											mode={'multiple'}
											showSearch
											{...{ showArrow: true }}
											optionFilterProp="value"
											onSelect={this.debounceTableSearch.bind(
												this,
												sourceMap.type.type,
											)}
											onSearch={(str: any) =>
												this.onSearchTable(str, sourceMap.sourceId)
											}
										>
											{(
												this.state.tableListMap[sourceMap.sourceId] || []
											).map((table: any) => {
												return (
													<Option key={`rdb-${table}`} value={table}>
														{table}
													</Option>
												);
											})}
										</Select>,
									)}
								</FormItem>
							) : (
								<FormItem {...formItemLayout} label={'表名'} key="rdbtable">
									{getFieldDecorator('table', {
										rules: [
											{
												required: true,
												message: '数据源表为必选项！',
											},
										],
										initialValue: tableValue,
									})(
										<Select
											disabled={this.isMysqlTable}
											getPopupContainer={getPopupContainer}
											showSearch
											onSearch={(str: any) =>
												this.debounceTableNameSearch(
													sourceMap.sourceId,
													getFieldValue('schema'),
													str,
												)
											}
											{...{ showArrow: true }}
											onSelect={this.debounceTableSearch.bind(
												this,
												sourceMap.type.type,
											)}
											optionFilterProp="value"
											filterOption={filterValueOption}
										>
											{getFieldValue('schema') &&
												(
													this.state.tableListMap[sourceMap.sourceId] ||
													[]
												).map((table: any) => {
													return (
														<Option key={`rdb-${table}`} value={table}>
															{table}
														</Option>
													);
												})}
										</Select>,
									)}
								</FormItem>
							)}
						</div>
					) : null,
					<FormItem
						{...formItemLayout}
						label={
							<span>
								数据过滤
								<HelpDoc doc="dataFilterDoc" />
							</span>
						}
						key="where"
					>
						{getFieldDecorator('where', {
							rules: [
								{
									max: 1000,
									message: '过滤语句不可超过1000个字符!',
								},
							],
							initialValue: isEmpty(sourceMap) ? '' : sourceMap.type.where,
						})(
							<Input
								type="textarea"
								placeholder="请参考相关SQL语法填写where过滤语句（不要填写where关键字）。该过滤语句通常用作增量同步"
								onChange={this.submitForm.bind(this)}
							/>,
						)}
					</FormItem>,
					haveChineseQuote && (
						<Row className="form-item-follow-text">
							<Col
								style={{ textAlign: 'left' }}
								span={formItemLayout.wrapperCol.sm.span}
								offset={formItemLayout.labelCol.sm.span}
							>
								<p className="warning-color">当前输入含有中文引号</p>
							</Col>
						</Row>
					),
					<FormItem
						{...formItemLayout}
						label={
							<span>
								切分键
								<HelpDoc doc={'selectKey'} />
							</span>
						}
						key="splitPK"
					>
						{getFieldDecorator('splitPK', {
							rules: [],
							initialValue: isEmpty(sourceMap) ? '' : sourceMap.type.splitPK,
						})(
							<Select
								getPopupContainer={getPopupContainer}
								showSearch
								{...{ showArrow: true }}
								allowClear={true}
								onChange={this.submitForm.bind(this)}
							>
								{(
									(sourceMap.copate &&
										sourceMap.copate
											.map((v: any) => v.key)
											.filter(
												(v: any, index: any, self: any) =>
													self.indexOf(v) === index,
											)) ||
									[]
								).map((copateValue: any, index: any) => {
									return (
										<Option key={`copate-${index}`} value={copateValue}>
											{/* ORACLE数据库单独考虑ROW_NUMBER() 这个函数， 展示去除括号 */}
											{copateValue}
										</Option>
									);
								})}
							</Select>,
						)}
					</FormItem>,
				];
				break;
			}
			case DATA_SOURCE_ENUM.HDFS: {
				// HDFS
				formItem = [
					<FormItem
						{...formItemLayout}
						label={
							<span>
								路径
								<HelpDoc doc="hdfsPath" />
							</span>
						}
						key="path"
					>
						{getFieldDecorator('path', {
							rules: [
								{
									required: true,
									message: '路径不得为空！',
								},
								{
									max: 200,
									message: '路径不得超过200个字符！',
								},
								{
									validator: this.validatePath,
								},
							],
							validateTrigger: 'onSubmit',
							initialValue: isEmpty(sourceMap) ? '' : sourceMap.type.path,
						})(
							<Input
								placeholder="例如: /rdos/batch"
								onChange={this.submitForm.bind(this)}
							/>,
						)}
					</FormItem>,
					<FormItem {...formItemLayout} label="文件类型" key="fileType">
						{getFieldDecorator('fileType', {
							rules: [
								{
									required: true,
								},
							],
							initialValue:
								sourceMap.type && sourceMap.type.fileType
									? sourceMap.type.fileType
									: 'text',
						})(
							<Select
								getPopupContainer={getPopupContainer}
								onChange={this.submitForm.bind(this)}
							>
								<Option value="orc">orc</Option>
								<Option value="text">text</Option>
								<Option value="parquet">parquet</Option>
							</Select>,
						)}
					</FormItem>,
					<FormItem
						{...formItemLayout}
						style={{
							display: fileType === 'text' ? 'block' : 'none',
						}}
						label={
							<span>
								列分隔符
								<HelpDoc doc="splitCharacter" />
							</span>
						}
						key="fieldDelimiter"
					>
						{getFieldDecorator('fieldDelimiter', {
							rules: [],
							initialValue: isEmpty(sourceMap) ? ',' : sourceMap.type.fieldDelimiter,
						})(
							<Input
								/* eslint-disable */
								placeholder="若不填写，则默认为\001"
								/* eslint-disable */
								onChange={this.submitForm.bind(this)}
							/>,
						)}
					</FormItem>,
					<FormItem
						{...formItemLayout}
						label="编码"
						key="encoding"
						style={{
							display: fileType === 'text' ? 'block' : 'none',
						}}
					>
						{getFieldDecorator('encoding', {
							rules: [
								{
									required: true,
								},
							],
							initialValue:
								!sourceMap.type || !sourceMap.type.encoding
									? 'utf-8'
									: sourceMap.type.encoding,
						})(
							<Select
								getPopupContainer={getPopupContainer}
								onChange={this.submitForm.bind(this)}
							>
								<Option value="utf-8">utf-8</Option>
								<Option value="gbk">gbk</Option>
							</Select>,
						)}
					</FormItem>,
				];
				break;
			}
			case DATA_SOURCE_ENUM.HIVE:
			case DATA_SOURCE_ENUM.HIVE1X:
			case DATA_SOURCE_ENUM.HIVE3X:
			case DATA_SOURCE_ENUM.SPARKTHRIFT: {
				// Hive
				formItem = [
					!selectHack && (
						<FormItem {...formItemLayout} label="表名" key="table">
							{getFieldDecorator('table', {
								rules: [
									{
										required: true,
										message: '数据源表为必选项！',
									},
								],
								initialValue: isEmpty(sourceMap) ? '' : sourceMap.type.table,
							})(
								<Select
									getPopupContainer={getPopupContainer}
									showSearch
									onChange={this.debounceTableSearch.bind(this, null)}
									optionFilterProp="value"
									onSearch={(str: any) =>
										this.onSearchTable(str, sourceMap.sourceId)
									}
								>
									{(tableListSearch[sourceMap.sourceId] || []).map(
										(table: any) => {
											return (
												<Option key={`rdb-${table}`} value={table}>
													{table}
												</Option>
											);
										},
									)}
								</Select>,
							)}
						</FormItem>
					),
					<FormItem
						{...formItemLayout}
						label={
							<span>
								分区
								<HelpDoc doc="partitionDesc" />
							</span>
						}
						key="partition"
					>
						{getFieldDecorator('partition', {
							rules: [],
							initialValue: isEmpty(sourceMap) ? '' : sourceMap.type.partition,
						})(
							<AutoComplete
								showSearch
								{...{ showArrow: true }}
								placeholder="请填写分区信息"
								onChange={this.submitForm.bind(this)}
								filterOption={filterValueOption}
							>
								{(this.state.tablePartitionList || []).map((pt: any) => {
									return (
										<AutoComplete.Option key={`rdb-${pt}`} value={pt}>
											{pt}
										</AutoComplete.Option>
									);
								})}
							</AutoComplete>,
						)}
					</FormItem>,
				];
				break;
			}
			default:
				break;
		}
		return formItem;
	};
}

const SourceFormWrap = Form.create<any>()(SourceForm);

class Source extends React.Component<any, any> {
	render() {
		return (
			<>
				<SourceFormWrap {...this.props} />
			</>
		);
	}
}

const mapState = (state: any) => {
	const { workbench, dataSync = {} } = state.dataSync;
	const { isCurrentTabNew = {}, currentTab = {}, tabs } = workbench;
	let taskVariables = [];
	try {
		const thisTab = tabs.length > 0 && tabs?.filter((item: any) => item.id === currentTab)[0];
		taskVariables = thisTab.taskVariables || [];
	} catch (error) {}
	return {
		isCurrentTabNew,
		currentTab,
		...dataSync,
		taskVariables,
	};
};
const mapDispatch = (dispatch: any, ownProps: any) => {
	return {
		handleSourceChange: (src: any) => {
			dispatch({
				type: dataSyncAction.RESET_SOURCE_MAP,
			});
			dispatch({
				type: dataSyncAction.RESET_KEYMAP,
			});
			dispatch({
				type: sourceMapAction.DATA_SOURCE_CHANGE,
				payload: src,
			});
			dispatch({
				type: workbenchAction.MAKE_TAB_DIRTY,
			});
		},

		handleSourceMapChange: (srcmap: any, key: any) => {
			dispatch({
				type: sourceMapAction.DATA_SOURCEMAP_CHANGE,
				payload: srcmap,
				key: key || 'main',
			});
			dispatch({
				type: workbenchAction.MAKE_TAB_DIRTY,
			});
		},

		handleTableColumnChange: (colData: any) => {
			dispatch({
				type: dataSyncAction.RESET_KEYMAP,
			});
			dispatch({
				type: sourceMapAction.SOURCE_TABLE_COLUMN_CHANGE,
				payload: colData,
			});
			dispatch({
				type: workbenchAction.MAKE_TAB_DIRTY,
			});
		},

		handleTableCopateChange: (copateData: any) => {
			dispatch({
				type: sourceMapAction.SOURCE_TABLE_COPATE_CHANGE,
				payload: copateData,
			});
		},
		saveDataSyncToTab: (params: any) => {
			dispatch({
				type: workbenchAction.SAVE_DATASYNC_TO_TAB,
				payload: {
					id: params.id,
					data: params.data,
				},
			});
		},
	};
};

export default connect(mapState, mapDispatch)(Source);
