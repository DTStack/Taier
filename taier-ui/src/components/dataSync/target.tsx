/* eslint-disable max-classes-per-file */
import * as React from 'react';
import { connect } from 'react-redux';
import { Form, Icon } from '@ant-design/compatible';
import { Input, Select, Button, Radio, message, Spin, AutoComplete, Modal } from 'antd';
import { isEmpty, debounce, get } from 'lodash';
import assign from 'object-assign';

import { Utils } from '@dtinsight/dt-utils';
import singletonNotification from '../notification';
import { API } from '../../api/dataSource';

import HelpDoc from '../../components/helpDoc';
import {
	DATA_SOURCE_ENUM,
	formItemLayout,
	DATA_SOURCE_TEXT,
	DDL_IDE_PLACEHOLDER,
} from '@/constant';
import {
	dataSyncAction,
	settingAction,
	targetMapAction,
	workbenchAction,
} from '@/reducer/dataSync/actionType';
import { getProjectTableTypes } from '@/reducer/dataSync/tableType';
import { filterValueOption, formJsonValidator } from '@/utils';
import Editor from '@/components/codeEditor';
import './target.scss';

const FormItem = Form.Item;
const { Option } = Select;
const RadioGroup = Radio.Group;
const { TextArea } = Input;

class TargetForm extends React.Component<any, any> {
	constructor(props: any) {
		super(props);

		this.state = {
			tableList: [],
			tableListSearch: [],
			visible: false,
			modalLoading: false,
			tablePartitionList: [], // 表分区列表
			loading: false, // 请求
			isImpalaHiveTable: false, // 是否是impala hive表
			schemaList: [],
			kingbaseId: '',
			tableListLoading: false,
			fetching: false,
			schemaId: '',
		};
	}

	componentDidMount() {
		const { targetMap, getProjectTableTypes, project } = this.props;
		const { sourceId, type = {} } = targetMap;
		const schema = isEmpty(targetMap) ? '' : targetMap?.schema ?? targetMap.type.schema;
		const projectId = project && project.id;
		if (projectId) {
			getProjectTableTypes(projectId);
		}
		if (!sourceId) {
			return;
		}
		if (type.type === DATA_SOURCE_ENUM.POSTGRESQL || type.type === DATA_SOURCE_ENUM.ORACLE) {
			this.getSchemaList(sourceId);
			schema ? this.getTableList(sourceId, schema) : this.getTableList(sourceId);
		} else {
			this.getTableList(sourceId);
		}
		if (type) {
			this.checkIsNativeHive(type.tableName);
			// 如果是已经添加过分区信息，则加载其分区列表信息
			if (type.partition) {
				this.getHivePartitions(type.table);
			}
		}
	}

	getSchemaList = (sourceId: any) => {
		API.getAllSchemas({
			sourceId,
		}).then((res: any) => {
			if (res.code === 1) {
				this.setState({
					schemaList: res.data || [],
					kingbaseId: sourceId,
				});
			}
		});
	};

	getTableList = (sourceId: any, schema?: any, name?: any) => {
		const ctx = this;
		const { targetMap } = this.props;
		// 排除条件
		if (targetMap.type?.type === DATA_SOURCE_ENUM.HDFS) {
			return true;
		}

		ctx.setState(
			{
				tableList: [],
				tableListSearch: [],
				tableListLoading: !name,
				schemaId: schema,
				fetching: !!name,
			},
			() => {
				API.getOfflineTableList({
					sourceId,
					isSys: false,
					schema,
					name,
					isRead: false,
				})
					.then((res: any) => {
						if (res.code === 1) {
							const { data = [] } = res;
							let arr = data;
							if (data.length && data.length > 200) {
								arr = data.slice(0, 200);
							}
							ctx.setState({
								tableList: res.data,
								tableListSearch: arr,
							});
						}
					})
					.finally(() => {
						ctx.setState({
							tableListLoading: false,
							fetching: false,
						});
					});
			},
		);
	};

	onSearchTable = (str: any) => {
		const { tableList } = this.state;
		let arr = tableList.filter((item: any) => item.indexOf(str) !== -1);
		if (arr.length && arr.length > 200) {
			arr = arr.slice(0, 200);
		}
		this.setState({
			tableListSearch: arr,
		});
	};

	/**
	 * 根据数据源id获取数据源信息
	 * @param {*} id
	 */
	getDataObjById(id: any) {
		const { dataSourceList } = this.props;
		return dataSourceList.filter((src: any) => {
			return `${src.dataInfoId}` === id;
		})[0];
	}

	changeSource(value: any, option: any) {
		const { handleSourceChange } = this.props;
		const { dataType } = option.props;
		setTimeout(() => {
			dataType !== DATA_SOURCE_ENUM.KINGBASE8 &&
				dataType !== DATA_SOURCE_ENUM.S3 &&
				dataType !== DATA_SOURCE_ENUM.ADB_FOR_PG &&
				this.getTableList(value);

			// 有schema才需要获取schemalist
			(dataType === DATA_SOURCE_ENUM.POSTGRESQL || dataType === DATA_SOURCE_ENUM.ORACLE) &&
				this.getSchemaList(value);
		}, 0);
		handleSourceChange(this.getDataObjById(value));
		this.resetTable();
	}

	resetTable() {
		const { form } = this.props;
		this.changeTable('');
		// 这边先隐藏结点，然后再reset，再显示。不然会有一个组件自带bug。
		this.setState(
			{
				selectHack: true,
			},
			() => {
				form.resetFields(['table']);
				this.setState({
					selectHack: false,
				});
			},
		);
	}

	getHivePartitions = (tableName: any) => {
		const { targetMap, handleTargetMapChange } = this.props;

		const { sourceId, type } = targetMap;
		// TODO 这里获取 Hive 分区的条件有点模糊
		if (
			type &&
			(type.type === DATA_SOURCE_ENUM.HIVE1X ||
				type.type === DATA_SOURCE_ENUM.HIVE ||
				type.type === DATA_SOURCE_ENUM.HIVE3X ||
				type.type === DATA_SOURCE_ENUM.SPARKTHRIFT)
		) {
			API.getHivePartitions({
				sourceId: sourceId,
				tableName,
			}).then((res: any) => {
				this.setState({
					tablePartitionList: res.data || [],
				});
				const havePartition = res.data && res.data.length > 0;
				handleTargetMapChange({ havePartition });
			});
		}
	};

	changeTable(type?: any, value?: any) {
		if (value) {
			// Reset partition
			this.props.form.setFieldsValue({ partition: '' });
			const schema = this.props.form.getFieldValue('schema');
			// 获取表列字段
			this.getTableColumn(value, schema);
			// 检测是否有 native hive
			this.checkIsNativeHive(value);
			// 获取 Hive 分区字段
			if (type !== DATA_SOURCE_ENUM.IMPALA) {
				this.getHivePartitions(value);
			}
		}
		this.submitForm();
	}

	getTableColumn = (tableName: any, schema?: any) => {
		const { form, handleTableColumnChange, targetMap } = this.props;
		const sourceId = form.getFieldValue('sourceId');

		this.setState({
			loading: true,
		});
		// Hive 作为结果表时，需要获取分区字段
		const targetType = get(targetMap, 'type.type', null);
		const includePart =
			+targetType === DATA_SOURCE_ENUM.HIVE1X ||
			+targetType === DATA_SOURCE_ENUM.HIVE ||
			+targetType === DATA_SOURCE_ENUM.HIVE3X ||
			+targetType === DATA_SOURCE_ENUM.SPARKTHRIFT;

		API.getOfflineTableColumn({
			sourceId,
			schema,
			tableName,
			isIncludePart: includePart,
		}).then((res: any) => {
			this.setState({
				loading: false,
			});
			if (res.code === 1) {
				handleTableColumnChange(res.data);
			} else {
				handleTableColumnChange([]);
			}
		});
	};

	checkIsNativeHive(tableName: any) {
		const { form } = this.props;
		const sourceId = form.getFieldValue('sourceId');
		if (!tableName || !sourceId) {
			return false;
		}
	}

	submitForm = () => {
		const { form, updateTabAsUnSave, handleTargetMapChange } = this.props;

		setTimeout(() => {
			/**
			 * targetMap
			 */
			let values = form.getFieldsValue();
			const keyAndValues = Object.entries(values);
			/**
			 * 这边将 ·writeMode@hdfs· 类的key全部转化为writeMode
			 * 加上@ 的原因是避免antd相同key引发的bug
			 */
			values = (() => {
				const values: any = {};
				keyAndValues.forEach(([key, value]) => {
					if (key.indexOf('@') > -1) {
						values[key.split('@')[0]] = value;
					} else {
						values[key] = value;
					}
				});
				return values;
			})();
			// 去空格
			if (values.partition) {
				values.partition = Utils.trimAll(values.partition);
			}
			if (values.path) {
				values.path = Utils.trimAll(values.path);
			}
			if (values.fileName) {
				values.fileName = Utils.trimAll(values.fileName);
			}
			if (values.bucket) {
				values.bucket = Utils.trimAll(values.bucket);
			}
			if (values.object) {
				values.object = Utils.trimAll(values.object);
			}
			if (values.fileName) {
				values.fileName = Utils.trimAll(values.fileName);
			}
			if (values.ftpFileName) {
				values.ftpFileName = Utils.trimAll(values.ftpFileName);
			}
			const srcmap = assign(values, {
				src: this.getDataObjById(values.sourceId),
			});

			// 处理数据同步变量
			handleTargetMapChange(srcmap);
			updateTabAsUnSave();
		}, 0);
	};

	validateChineseCharacter = (data: any) => {
		const reg = /(，|。|；|[\u4e00-\u9fa5]+)/; // 中文字符，中文逗号，句号，分号
		let has = false;
		const fieldsName: any = [];
		if (data.path && reg.test(data.path)) {
			has = true;
			fieldsName.push('路径');
		}
		if (data.fileName && reg.test(data.fileName)) {
			has = true;
			fieldsName.push('文件名');
		}
		if (data.ftpFileName && reg.test(data.ftpFileName)) {
			has = true;
			fieldsName.push('文件名');
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

	prev(cb: any) {
		/* eslint-disable-next-line */
		cb.call(null, 0);
	}

	next(cb: any) {
		const { form, currentTabData, saveDataSyncToTab, dataSync } = this.props;
		form.validateFields((err: any, values: any) => {
			if (!err) {
				saveDataSyncToTab({
					id: currentTabData.id,
					data: dataSync,
				});
				this.validateChineseCharacter(values);
				/* eslint-disable-next-line */
				cb.call(null, 2);
			}
		});
	}

	createTable() {
		const { textSql } = this.state;
		const { targetMap, form } = this.props;
		const tableType = form.getFieldValue('tableType');
		const dataSourceType = targetMap.type && targetMap.type.type;
		this.setState({
			modalLoading: true,
		});
		API.createDdlTable({
			sql: textSql,
			sourceId: targetMap.sourceId,
			tableType,
		}).then((res: any) => {
			this.setState({
				modalLoading: false,
			});
			if (res.code === 1) {
				this.getTableList(targetMap.sourceId);
				this.changeTable(dataSourceType, res.data);
				this.props.form.setFieldsValue({ table: res.data });
				this.setState({
					visible: false,
				});
				message.success('表创建成功!');
			}
		});
	}

	showCreateModal = () => {
		const { sourceMap, targetMap } = this.props;
		const schema = this.props.form.getFieldValue('schema');
		this.setState({
			loading: true,
		});
		const tableName =
			typeof sourceMap.type.table === 'string'
				? sourceMap.type.table
				: sourceMap.type.table && sourceMap.type.table[0];
		const targetTableName =
			typeof targetMap.type.table === 'string'
				? targetMap.type.table
				: targetMap.type.table && targetMap.type.table[0];
		API.getCreateTargetTable({
			originSourceId: sourceMap.sourceId,
			tableName,
			partition: sourceMap.type.partition,
			targetSourceId: targetMap.sourceId,
			originSchema: sourceMap?.type?.schema || null,
			targetSchema: schema || null,
		}).then((res: any) => {
			this.setState({
				loading: false,
			});
			if (res.code === 1) {
				let textSql = res.data;
				if (targetTableName) {
					const reg = /create\s+table\s+`(.*)`\s*\(/i;
					textSql = res.data.replace(
						reg,
						function (match: any, p1: any, offset: any, string: string) {
							return match.replace(p1, targetTableName);
						},
					);
				}
				this.setState({
					textSql: textSql,
					sync: true,
					visible: true,
				});
			}
		});
	};

	checkData = (value: any) => {
		const { tableListSearch } = this.state;
		const { setFieldsValue } = this.props.form;

		if (!tableListSearch.includes(value)) {
			setFieldsValue({ table: undefined });
		}
	};

	renderTableList = (taskType: any) => {
		const { tableListSearch } = this.state;
		return tableListSearch.map((table: any) => {
			return (
				<Option key={`rdb-target-${table}`} value={table}>
					{table}
				</Option>
			);
		});
	};

	handleCancel() {
		this.setState({
			textSql: '',
			visible: false,
		});
	}

	ddlChange = (_: any, newVal: string) => {
		this.setState({
			textSql: newVal,
			sync: false,
		});
	};

	render() {
		const { getFieldDecorator } = this.props.form;
		const { tableListLoading } = this.state;
		const { targetMap, dataSourceList, navtoStep, isIncrementMode, modalLoading } = this.props;
		const { getPopupContainer } = this.props;
		const mode =
			targetMap.type && targetMap.type.type === DATA_SOURCE_ENUM.IMPALA ? 'sql' : 'dtsql';

		return (
			<Spin spinning={tableListLoading}>
				<div className="g-step2">
					<Modal
						className="m-codemodal"
						title={<span>建表语句</span>}
						confirmLoading={modalLoading}
						maskClosable={false}
						style={{ height: 424 }}
						visible={this.state.visible}
						onCancel={this.handleCancel.bind(this)}
						onOk={this.createTable.bind(this)}
					>
						<Editor
							language={mode}
							value={this.state.textSql}
							sync={this.state.sync}
							placeholder={DDL_IDE_PLACEHOLDER}
							onChange={this.ddlChange}
						/>
					</Modal>
					<Form>
						<FormItem {...formItemLayout} label="数据同步目标">
							{getFieldDecorator('sourceId', {
								rules: [
									{
										required: true,
									},
								],
								initialValue: isEmpty(targetMap) ? '' : `${targetMap.sourceId}`,
							})(
								<Select
									getPopupContainer={getPopupContainer}
									showSearch
									onSelect={(value, options) => {
										this.setState(
											{
												tableList: [],
												tableListSearch: [],
											},
											() => {
												this.changeSource(value, options);
											},
										);
									}}
									optionFilterProp="name"
								>
									{dataSourceList.map(
										(src: {
											dataTypeCode: DATA_SOURCE_ENUM;
											dataName: string;
											dataInfoId: number;
										}) => {
											const title = `${src.dataName}（${
												DATA_SOURCE_TEXT[src.dataTypeCode]
											}）`;

											// 暂时支持以下类型的数据源
											const tmpSupportDataSource = [
												DATA_SOURCE_ENUM.MYSQL,
												DATA_SOURCE_ENUM.ORACLE,
												DATA_SOURCE_ENUM.POSTGRESQL,
												DATA_SOURCE_ENUM.HDFS,
												DATA_SOURCE_ENUM.HIVE,
												DATA_SOURCE_ENUM.HIVE1X,
												DATA_SOURCE_ENUM.HIVE3X,
												DATA_SOURCE_ENUM.SPARKTHRIFT,
											];

											/**
											 * 禁用ES, REDIS, MONGODB,
											 * 增量模式禁用非 HIVE, HDFS数据源
											 */
											const disableSelect =
												!tmpSupportDataSource.includes(src.dataTypeCode) ||
												src.dataTypeCode === DATA_SOURCE_ENUM.ES ||
												src.dataTypeCode === DATA_SOURCE_ENUM.REDIS ||
												src.dataTypeCode === DATA_SOURCE_ENUM.MONGODB ||
												(isIncrementMode &&
													src.dataTypeCode !== DATA_SOURCE_ENUM.HIVE1X &&
													src.dataTypeCode !== DATA_SOURCE_ENUM.HIVE &&
													src.dataTypeCode !== DATA_SOURCE_ENUM.HIVE3X &&
													src.dataTypeCode !==
														DATA_SOURCE_ENUM.SPARKTHRIFT &&
													src.dataTypeCode !== DATA_SOURCE_ENUM.HDFS);

											return (
												<Option
													key={src.dataInfoId}
													{...{
														name: src.dataName,
														dataType: src.dataTypeCode,
													}}
													disabled={disableSelect}
													value={`${src.dataInfoId}`}
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
						{!isEmpty(targetMap) ? (
							<FormItem
								{...formItemLayout}
								label={
									<span>
										高级配置
										<HelpDoc doc={'dataSyncExtralConfigHelp'} />
									</span>
								}
							>
								{getFieldDecorator('extralConfig', {
									rules: [
										{
											validator: formJsonValidator,
										},
									],
									initialValue: get(targetMap, 'extralConfig', ''),
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
					{!this.props.readonly && (
						<div className="steps-action">
							<Button style={{ marginRight: 8 }} onClick={() => this.prev(navtoStep)}>
								上一步
							</Button>
							<Button
								type="primary"
								onClick={() => this.next(navtoStep)}
								loading={this.state.loading}
							>
								下一步
							</Button>
						</div>
					)}
				</div>
			</Spin>
		);
	}

	debounceTableSearch = debounce(this.changeTable, 600, { maxWait: 2000 });
	debounceTableNameSearch = debounce(this.getTableList, 500, {
		maxWait: 2000,
	});

	renderDynamicForm = () => {
		const {
			selectHack,
			loading,
			schemaList,
			kingbaseId,
			tableListSearch = ['111'],
			schemaId,
			tableList,
			fetching,
		} = this.state;

		const { targetMap, sourceMap, form } = this.props;
		const { getFieldDecorator } = form;
		const sourceType = sourceMap.type && sourceMap.type.type;
		const targetType = targetMap?.type?.type;
		// 是否拥有分区
		let formItem: any;
		const havePartition =
			targetMap.type && (!!targetMap.type.partition || targetMap.type.havePartition);
		const { getPopupContainer } = this.props;
		const showCreateTable =
			targetType === DATA_SOURCE_ENUM.HIVE ||
			targetType === DATA_SOURCE_ENUM.HIVE3X ||
			targetType === DATA_SOURCE_ENUM.SPARKTHRIFT ||
			targetType === DATA_SOURCE_ENUM.HIVE ||
			targetType === DATA_SOURCE_ENUM.POSTGRESQL ||
			targetType === DATA_SOURCE_ENUM.MYSQL;
		const showCreateTableSource =
			sourceType === DATA_SOURCE_ENUM.MYSQL ||
			sourceType === DATA_SOURCE_ENUM.ORACLE ||
			sourceType === DATA_SOURCE_ENUM.SQLSERVER ||
			sourceType === DATA_SOURCE_ENUM.POSTGRESQL ||
			sourceType === DATA_SOURCE_ENUM.HIVE ||
			sourceType === DATA_SOURCE_ENUM.HIVE3X ||
			sourceType === DATA_SOURCE_ENUM.SPARKTHRIFT ||
			sourceType === DATA_SOURCE_ENUM.HIVE;

		const oneKeyCreateTable =
			showCreateTable &&
			showCreateTableSource &&
			(loading ? (
				<Icon type="loading" />
			) : (
				<a
					style={{ top: '0px', right: '-103px' }}
					onClick={this.showCreateModal.bind(this)}
					className="help-doc"
				>
					一键生成目标表
				</a>
			));

		if (isEmpty(targetMap)) return null;
		switch (targetMap.type.type) {
			case DATA_SOURCE_ENUM.MYSQL: {
				formItem = [
					!selectHack && (
						<FormItem {...formItemLayout} label="表名" key="table">
							{getFieldDecorator('table', {
								rules: [
									{
										required: true,
										message: '请选择表',
									},
								],
								initialValue: isEmpty(targetMap) ? '' : targetMap.type.table,
							})(
								<Select
									getPopupContainer={getPopupContainer}
									showSearch
									optionFilterProp="value"
									filterOption={false}
									onSearch={(str: any) => {
										this.debounceTableNameSearch(targetMap.sourceId, null, str);
									}}
									onSelect={this.debounceTableSearch.bind(this, null)}
									notFoundContent={fetching ? <Spin size="small" /> : null}
								>
									{tableListSearch.map((table: any) => {
										return (
											<Option key={`rdb-target-${table}`} value={table}>
												{table}
											</Option>
										);
									})}
									{/* <Option key={'rdb-1'} value={1}>
                                        {1}
                                    </Option> */}
								</Select>,
							)}
							{oneKeyCreateTable}
						</FormItem>
					),
					<FormItem {...formItemLayout} label="导入前准备语句" key="preSql">
						{getFieldDecorator('preSql', {
							rules: [],
							initialValue: isEmpty(targetMap) ? '' : targetMap.type.preSql,
						})(
							<Input.TextArea
								onChange={this.submitForm.bind(this)}
								placeholder="请输入导入数据前执行的SQL脚本"
							/>,
						)}
					</FormItem>,
					<FormItem {...formItemLayout} label="导入后准备语句" key="postSql">
						{getFieldDecorator('postSql', {
							rules: [],
							initialValue: isEmpty(targetMap) ? '' : targetMap.type.postSql,
						})(
							<Input.TextArea
								onChange={this.submitForm.bind(this)}
								placeholder="请输入导入数据后执行的SQL脚本"
							/>,
						)}
					</FormItem>,
					<FormItem
						{...formItemLayout}
						label={'主键冲突'}
						key="writeMode-mysql"
						className="txt-left"
					>
						{getFieldDecorator('writeMode@mysql', {
							rules: [
								{
									required: true,
								},
							],
							initialValue:
								targetMap.type && targetMap.type.writeMode
									? targetMap.type.writeMode
									: 'insert',
						})(
							<Select onChange={this.submitForm.bind(this)}>
								<Option key="writeModeInsert" value="insert">
									insert into（当主键/约束冲突，报脏数据）
								</Option>
								<Option key="writeModeReplace" value="replace">
									replace
									into（当主键/约束冲突，先delete再insert，未映射的字段会被映射为NULL）
								</Option>
								<Option key="writeModeUpdate" value="update">
									on duplicate key
									update（当主键/约束冲突，update数据，未映射的字段值不变）
								</Option>
							</Select>,
						)}
					</FormItem>,
				];
				break;
			}
			case DATA_SOURCE_ENUM.ORACLE: {
				formItem = [
					<FormItem {...formItemLayout} label="schema" key="schema">
						{getFieldDecorator('schema', {
							rules: [],
							initialValue: isEmpty(targetMap) ? '' : targetMap.type.schema,
						})(
							<Select
								showSearch
								{...{ showArrow: true }}
								allowClear={true}
								onChange={(val: any) => {
									this.getTableList(kingbaseId, val);
									form.setFieldsValue({ table: '' });
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
					</FormItem>,
					!selectHack && (
						<FormItem {...formItemLayout} label="表名" key="table">
							{getFieldDecorator('table', {
								rules: [
									{
										required: true,
										message: '请选择表',
									},
								],
								initialValue: isEmpty(targetMap) ? '' : targetMap.type.table,
							})(
								<Select
									getPopupContainer={getPopupContainer}
									showSearch
									optionFilterProp="value"
									filterOption={false}
									onSearch={(val: any) =>
										this.debounceTableNameSearch(kingbaseId, schemaId, val)
									}
									notFoundContent={fetching ? <Spin size="small" /> : null}
									onSelect={this.debounceTableSearch.bind(this, null)}
								>
									{tableList.map((table: any) => {
										return (
											<Option key={`rdb-target-${table}`} value={table}>
												{table}
											</Option>
										);
									})}
								</Select>,
							)}
							{oneKeyCreateTable}
						</FormItem>
					),
					<FormItem {...formItemLayout} label="导入前准备语句" key="preSql">
						{getFieldDecorator('preSql', {
							rules: [],
							initialValue: isEmpty(targetMap) ? '' : targetMap.type.preSql,
						})(
							<Input
								onChange={this.submitForm.bind(this)}
								placeholder="请输入导入数据前执行的SQL脚本"
								type="textarea"
							></Input>,
						)}
					</FormItem>,
					<FormItem {...formItemLayout} label="导入后准备语句" key="postSql">
						{getFieldDecorator('postSql', {
							rules: [],
							initialValue: isEmpty(targetMap) ? '' : targetMap.type.postSql,
						})(
							<Input
								onChange={this.submitForm.bind(this)}
								placeholder="请输入导入数据后执行的SQL脚本"
								type="textarea"
							></Input>,
						)}
					</FormItem>,
					<FormItem
						{...formItemLayout}
						label={'主键冲突'}
						key="writeMode-mysql"
						className="txt-left"
					>
						{getFieldDecorator('writeMode@mysql', {
							rules: [
								{
									required: true,
								},
							],
							initialValue:
								targetMap.type && targetMap.type.writeMode
									? targetMap.type.writeMode
									: 'insert',
						})(
							<Select onChange={this.submitForm.bind(this)}>
								<Option key="writeModeInsert" value="insert">
									insert into（当主键/约束冲突，报脏数据）
								</Option>
								<Option key="writeModeReplace" value="replace">
									replace
									into（当主键/约束冲突，先delete再insert，未映射的字段会被映射为NULL）
								</Option>
								<Option key="writeModeUpdate" value="update">
									on duplicate key
									update（当主键/约束冲突，update数据，未映射的字段值不变）
								</Option>
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
				formItem = [
					!selectHack && (
						<FormItem {...formItemLayout} label="表名" key="table">
							{getFieldDecorator('table', {
								rules: [
									{
										required: true,
										message: '请选择表',
									},
								],
								initialValue: isEmpty(targetMap) ? '' : targetMap.type.table,
							})(
								<Select
									getPopupContainer={getPopupContainer}
									showSearch
									// onBlur={(e) => {console.log(e.target.value);this.checkData(e.target.value)}}
									onSearch={(str) => {
										this.onSearchTable(str);
									}}
									onSelect={this.debounceTableSearch.bind(this, null)}
									notFoundContent={fetching ? <Spin size="small" /> : null}
									optionFilterProp="value"
								>
									{tableListSearch.map((table: any) => {
										return (
											<Option key={`rdb-target-${table}`} value={table}>
												{table}
											</Option>
										);
									})}
								</Select>,
							)}
							{oneKeyCreateTable}
						</FormItem>
					),
					havePartition ? (
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
								rules: [
									{
										required: true,
										message: '目标分区为必填项！',
									},
								],
								initialValue: get(targetMap, 'type.partition', ''),
							})(
								<AutoComplete
									showSearch
									{...{ showArrow: true }}
									placeholder="请填写分区信息"
									onChange={this.submitForm.bind(this)}
									filterOption={filterValueOption}
								>
									{this.state.tablePartitionList.map((pt: any) => {
										return (
											<AutoComplete.Option key={`rdb-${pt}`} value={pt}>
												{pt}
											</AutoComplete.Option>
										);
									})}
								</AutoComplete>,
							)}
						</FormItem>
					) : (
						''
					),
					<FormItem
						{...formItemLayout}
						label="写入模式"
						key="writeMode-hive"
						className="txt-left"
					>
						{getFieldDecorator('writeMode@hive', {
							rules: [
								{
									required: true,
								},
							],
							initialValue:
								targetMap.type && targetMap.type.writeMode
									? targetMap.type.writeMode
									: 'replace',
						})(
							<RadioGroup onChange={this.submitForm.bind(this)}>
								<Radio value="replace" style={{ float: 'left' }}>
									覆盖（Insert Overwrite）
								</Radio>
								<Radio value="insert" style={{ float: 'left' }}>
									追加（Insert Into）
								</Radio>
							</RadioGroup>,
						)}
					</FormItem>,
				];
				break;
			}
			case DATA_SOURCE_ENUM.POSTGRESQL: {
				formItem = [
					<FormItem {...formItemLayout} label="schema" key="schema">
						{getFieldDecorator('schema', {
							initialValue: isEmpty(targetMap)
								? ''
								: targetMap?.schema
								? targetMap?.schema
								: targetMap.type.schema,
						})(
							<Select
								showSearch
								{...{ showArrow: true }}
								allowClear={true}
								onChange={(val: any) => {
									val && this.getTableList(kingbaseId, val);
									form.setFieldsValue({
										table: '',
										splitPK: undefined,
									});
									this.setState({
										tableListSearch: [],
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
					</FormItem>,
					!selectHack && (
						<FormItem {...formItemLayout} label="表名" key="table">
							{getFieldDecorator('table', {
								rules: [
									{
										required: true,
										message: '请选择表',
									},
								],
								initialValue: isEmpty(targetMap) ? '' : targetMap.type.table,
							})(
								<Select
									getPopupContainer={getPopupContainer}
									showSearch
									onSearch={(val: any) =>
										this.debounceTableNameSearch(
											targetMap.sourceId,
											form.getFieldValue('schema'),
											val,
										)
									}
									notFoundContent={fetching ? <Spin size="small" /> : null}
									onSelect={this.debounceTableSearch.bind(this, null)}
								>
									{this.renderTableList(targetType)}
								</Select>,
							)}
							{oneKeyCreateTable}
						</FormItem>
					),
					<FormItem {...formItemLayout} label="导入前准备语句" key="preSql">
						{getFieldDecorator('preSql', {
							rules: [],
							initialValue: isEmpty(targetMap) ? '' : targetMap.type.preSql,
						})(
							<Input
								onChange={this.submitForm.bind(this)}
								placeholder="请输入导入数据前执行的SQL脚本"
								type="textarea"
							></Input>,
						)}
					</FormItem>,
					<FormItem {...formItemLayout} label="导入后准备语句" key="postSql">
						{getFieldDecorator('postSql', {
							rules: [],
							initialValue: isEmpty(targetMap) ? '' : targetMap.type.postSql,
						})(
							<Input
								onChange={this.submitForm.bind(this)}
								placeholder="请输入导入数据后执行的SQL脚本"
								type="textarea"
							></Input>,
						)}
					</FormItem>,
					<FormItem
						{...formItemLayout}
						label={'主键冲突'}
						key="writeMode-mysql"
						className="txt-left"
					>
						{getFieldDecorator('writeMode@mysql', {
							rules: [
								{
									required: true,
								},
							],
							initialValue:
								targetMap.type && targetMap.type.writeMode
									? targetMap.type.writeMode
									: 'insert',
						})(
							<Select onChange={this.submitForm.bind(this)}>
								<Option key="writeModeInsert" value="insert">
									insert into（当主键/约束冲突，报脏数据）
								</Option>
								<Option key="writeModeReplace" value="replace">
									replace
									into（当主键/约束冲突，先delete再insert，未映射的字段会被映射为NULL）
								</Option>
								<Option key="writeModeUpdate" value="update">
									on duplicate key
									update（当主键/约束冲突，update数据，未映射的字段值不变）
								</Option>
							</Select>,
						)}
					</FormItem>,
				];
				break;
			}
			case DATA_SOURCE_ENUM.HDFS: {
                formItem = [
                    <FormItem
                        {...formItemLayout}
                        label="路径"
                        key="path"
                    >
                        {getFieldDecorator('path', {
                            rules: [{
                                required: true
                            }],
                            initialValue: isEmpty(targetMap) ? '' : targetMap.type.path
                        })(
                            <Input
                                placeholder="例如: /app/batch"
                                onChange={
                                    debounce(this.submitForm, 600, { 'maxWait': 2000 })
                                } />
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="文件名"
                        key="fileName"
                    >
                        {getFieldDecorator('fileName', {
                            rules: [{
                                required: true
                            }],
                            initialValue: isEmpty(targetMap) ? '' : targetMap.type.fileName
                        })(
                            <Input onChange={this.submitForm.bind(this)} />
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="文件类型"
                        key="fileType"
                    >
                        {getFieldDecorator('fileType', {
                            rules: [{
                                required: true
                            }],
                            initialValue: targetMap.type && targetMap.type.fileType ? targetMap.type.fileType : 'orc'
                        })(
                            <Select getPopupContainer={getPopupContainer} onChange={this.submitForm.bind(this)} >
                                <Option value="orc">orc</Option>
                                <Option value="text">text</Option>
                                <Option value="parquet">parquet</Option>
                            </Select>
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="列分隔符"
                        key="fieldDelimiter"
                    >
                        {getFieldDecorator('fieldDelimiter', {
                            rules: [],
                            initialValue: isEmpty(targetMap) ? ',' : targetMap.type.fieldDelimiter
                        })(
                            <Input
                                // eslint-disable-next-line no-octal-escape
                                placeholder="例如: 目标为hive则 分隔符为\001"
                                onChange={this.submitForm.bind(this)} />
                        )}
                        <HelpDoc doc="splitCharacter" />
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="编码"
                        key="encoding"
                    >
                        {getFieldDecorator('encoding', {
                            rules: [{
                                required: true
                            }],
                            initialValue: isEmpty(targetMap) || !targetMap.type.encoding ? 'utf-8' : targetMap.type.encoding
                        })(
                            <Select getPopupContainer={getPopupContainer} onChange={this.submitForm.bind(this)}>
                                <Option value="utf-8">utf-8</Option>
                                <Option value="gbk">gbk</Option>
                            </Select>
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="写入模式"
                        className="txt-left"
                        key="writeMode-hdfs"
                    >
                        {getFieldDecorator('writeMode@hdfs', {
                            rules: [{
                                required: true
                            }],
                            initialValue: targetMap.type && targetMap.type.writeMode ? targetMap.type.writeMode : 'APPEND'
                        })(
                            <RadioGroup onChange={this.submitForm.bind(this)}>
                                <Radio value="NONCONFLICT" style={{ float: 'left' }}>
                                    覆盖（Insert Overwrite）
                                </Radio>
                                <Radio value="APPEND" style={{ float: 'left' }}>
                                    追加（Insert Into）
                                </Radio>
                            </RadioGroup>
                        )}
                    </FormItem>
                ];
                break;
            }
			default:
				break;
		}

		return formItem;
	};
}

const TargetFormWrap = Form.create<any>()(TargetForm);

class Target extends React.Component<any, any> {
	render() {
		return (
			<div>
				<TargetFormWrap {...this.props} />
			</div>
		);
	}
}

const mapState = (state: any) => {
	const { workbench, dataSync } = state.dataSync;
	const { isCurrentTabNew, currentTab } = workbench;
	return {
		currentTab,
		isCurrentTabNew,
		project: state.project,
		projectTableTypes: state.tableTypes?.projectTableTypes || [],
		...dataSync,
	};
};

const mapDispatch = (dispatch: any, ownProps: any) => {
	return {
		handleSourceChange(src: any) {
			dispatch({
				type: dataSyncAction.RESET_TARGET_MAP,
			});
			dispatch({
				type: settingAction.INIT_CHANNEL_SETTING,
			});
			dispatch({
				type: dataSyncAction.RESET_KEYMAP,
			});
			dispatch({
				type: targetMapAction.DATA_SOURCE_TARGET_CHANGE,
				payload: src,
			});
			dispatch({
				type: workbenchAction.MAKE_TAB_DIRTY,
			});
		},

		handleTargetMapChange(srcmap: any) {
			dispatch({
				type: targetMapAction.DATA_TARGETMAP_CHANGE,
				payload: srcmap,
			});
		},
		updateTabAsUnSave() {
			dispatch({
				type: workbenchAction.MAKE_TAB_DIRTY,
			});
		},
		handleTableColumnChange: (colData: any) => {
			dispatch({
				type: dataSyncAction.RESET_KEYMAP,
			});
			dispatch({
				type: targetMapAction.TARGET_TABLE_COLUMN_CHANGE,
				payload: colData,
			});
			dispatch({
				type: workbenchAction.MAKE_TAB_DIRTY,
			});
		},
		getProjectTableTypes: (projectId: any) => {
			dispatch(getProjectTableTypes(projectId));
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

export default connect(mapState, mapDispatch)(Target);
