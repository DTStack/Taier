import * as React from 'react';
import { connect } from 'react-redux';
import {
    Form,
    Input,
    Select,
    Button,
    Icon,
    Table,
    message,
    Radio,
    Row,
    Col,
    Tooltip,
    Checkbox
} from 'antd';
import { isEmpty, debounce, get, isArray } from 'lodash';
import assign from 'object-assign';

import utils from 'utils';
import { singletonNotification, debounceEventHander, filterValueOption } from 'funcs';
import ajax from '../../../../api';
import {
    sourceMapAction,
    dataSyncAction,
    workbenchAction
} from '../../../../store/modules/offlineTask/actionType';

import HelpDoc from '../../../helpDoc';
import { isRDB, formJsonValidator } from '../../../../comm';

import {
    formItemLayout,
    DATA_SOURCE,
    DATA_SOURCE_TEXT,
    SUPPROT_SUB_LIBRARY_DB_ARRAY,
    RDB_TYPE_ARRAY
} from '../../../../comm/const';

import BatchSelect from './batchSelect';

const FormItem = Form.Item;
const Option: any = Select.Option;
const RadioGroup = Radio.Group;
const TextArea = Input.TextArea;

class SourceForm extends React.Component<any, any> {
    _isMounted = false;
    isMysqlTable = false;
    constructor (props: any) {
        super(props);
        this.state = {
            tableListMap: {},
            showPreview: false,
            dataSource: [],
            columns: [],
            tablePartitionList: [], // 表分区列表
            incrementColumns: [], // 增量字段
            loading: false, // 请求
            isChecked: {} // checkbox默认是否选中
        };
    }
    timerID: any;
    componentDidMount () {
        this._isMounted = true;
        const { sourceMap } = this.props;
        const { sourceList } = sourceMap;
        let tableName = '';
        let sourceId = '';
        if (sourceList) {
            for (let i = 0; i < sourceList.length; i++) {
                let source = sourceList[i];
                if (source.sourceId != null) {
                    this.getTableList(source.sourceId);
                    if (source.tables && i == 0) {
                        tableName = source.tables;
                        sourceId = source.sourceId;
                    }
                }
            }
        }
        if (
            tableName &&
            sourceId &&
            RDB_TYPE_ARRAY.indexOf(sourceMap.type.type) > -1
        ) {
            this.getCopate(sourceId, tableName);
            this.loadIncrementColumn(tableName);
        }
    }

    componentWillUnmount () {
        this._isMounted = false;
        clearInterval(this.timerID);
    }

    loadIncrementColumn = async (tableName: any) => {
        const { sourceMap } = this.props;
        const res = await ajax.getIncrementColumns({
            sourceId: sourceMap.sourceId,
            tableName: tableName
        });

        if (res.code === 1) {
            this.setState({
                incrementColumns: res.data || []
            })
        }
    }

    onIncrementColumnChange = (value: any) => {
        const { assignSourceMap } = this.props;
        assignSourceMap({ increColumn: value });
    }

    getTableList = (sourceId: any) => {
        const ctx = this;
        const { sourceMap } = this.props;
        if (
            sourceMap.type &&
            (sourceMap.type.type === DATA_SOURCE.HDFS ||
                sourceMap.type.type === DATA_SOURCE.FTP)
        ) {
            return;
        }

        this.isMysqlTable = sourceMap.type.type === DATA_SOURCE.MYSQL;

        // 保证不同mySql类型表切换是批量选择出现的数据错误问题
        this.state.isChecked[sourceMap.sourceId] && this.setState((preState: any) => ({ isChecked: { ...preState.isChecked, ...{ [sourceMap.sourceId]: !preState.isChecked[sourceMap.sourceId] } } }));

        this.setState(
            {
                showPreview: false
            },
            () => {
                ajax.getOfflineTableList({
                    sourceId,
                    isSys: false
                }).then((res: any) => {
                    if (res.code === 1) {
                        if (ctx._isMounted) {
                            ctx.setState({
                                tableListMap: {
                                    ...this.state.tableListMap,
                                    [sourceId]: res.data || []
                                }
                            });
                        }
                    }
                });
            }
        );
    };

    getTableColumn = (tableName: any, type: any) => {
        const {
            form,
            sourceMap,
            handleTableColumnChange,
            handleTableCopateChange
        } = this.props;

        if (tableName instanceof Array) {
            tableName = tableName[0];
        }

        if (!tableName) {
            handleTableCopateChange([]);
            // form.resetFields(['splitPK']) //resetFields指的是恢复上一个值
            form.setFields({
                splitPK: {
                    value: ''
                }
            });
            return;
        }

        if (sourceMap.type && sourceMap.type.type === DATA_SOURCE.HBASE) {
            this.setState({
                loading: false
            });
            return;
        }

        if (type && isRDB(type)) {
            this.getCopate(sourceMap.sourceId, tableName);
        }

        ajax.getOfflineTableColumn({
            sourceId: sourceMap.sourceId,
            tableName
        }).then((res: any) => {
            if (res.code === 1) {
                handleTableColumnChange(res.data);
            } else {
                handleTableColumnChange([]);
            }
            this.setState({
                loading: false
            });
        });
    };

    getCopate (sourceId: any, tableName: any) {
        const { handleTableCopateChange } = this.props;
        if (tableName instanceof Array) {
            tableName = tableName[0];
        }
        ajax.getOfflineColumnForSyncopate({
            sourceId,
            tableName
        }).then((res: any) => {
            if (res.code === 1) {
                handleTableCopateChange(res.data);
            } else {
                handleTableCopateChange([]);
            }
        });
    }

    getDataObjById (id: any) {
        const { dataSourceList } = this.props;
        return dataSourceList.filter((src: any) => {
            return src.id == id;
        })[0];
    }

    changeExtSource (key: any, value: any) {
        this.props.changeExtDataSource(this.getDataObjById(value), key);
        this.getTableList(value);
        this.resetTable(`extTable.${key}`);
    }

    changeSource (value: any, option: any) {
        const { handleSourceChange } = this.props;
        setTimeout(() => {
            this.getTableList(value);
        }, 0);
        handleSourceChange(this.getDataObjById(value));
        this.resetTable();
    }

    addDataSource () {
        const key = 'key' + ~~(Math.random() * 10000000);
        this.props.addDataSource(key);
    }

    deleteExtSource (key: any) {
        this.props.deleteDataSource(key);
    }

    resetTable (key?: any) {
        const { form } = this.props;
        this.changeTable('');
        // 这边先隐藏结点，然后再reset，再显示。不然会有一个组件自带bug。
        this.setState(
            {
                selectHack: true
            },
            () => {
                if (key) {
                    form.resetFields([key]);
                } else {
                    form.resetFields(['table']);
                    form.resetFields(['splitPK']);
                }
                this.setState({
                    selectHack: false
                });
            }
        );
    }

    changeTable (type?: any, value?: any, sourceKey?: any) {
        if (value) {
            this.setState({
                loading: true
            });

            this.getTableColumn(value, type);
            // 如果源为hive, 则加载分区字段
            this.getHivePartions(value);

            // 加载增量模式字段
            if (this.props.isIncrementMode) {
                this.loadIncrementColumn(value);
            }
        }
        // 不可简化sourceKey, 在submitForm上对应的不同的逻辑，即第四个参数对应的逻辑不同，在不同场景可能不存在第四个参数，不能简化
        this.submitForm(null, sourceKey, value, sourceKey);
        this.setState({
            showPreview: false
        });
    }

    getHivePartions = (tableName: any) => {
        const {
            sourceMap,
            form
        } = this.props;

        if (sourceMap.type && sourceMap.type.type !== DATA_SOURCE.HIVE_2 && sourceMap.type.type !== DATA_SOURCE.HIVE_1) {
            return;
        }
        // Reset partition
        form.setFieldsValue({ partition: '' });
        ajax.getHivePartitions({
            sourceId: sourceMap.sourceId,
            tableName
        }).then((res: any) => {
            this.setState({
                tablePartitionList: res.data || []
            });
        });
    }

    changeExtTable (key: any, value: any) {
        this.submitForm(null, key);
    }

    validatePath = (rule: any, value: any, callback: any) => {
        const { handleTableColumnChange, form } = this.props;
        const { getFieldValue } = form;
        const sourceId = getFieldValue('sourceId');
        if (getFieldValue('fileType') === 'orc') {
            ajax.getOfflineTableColumn({
                sourceId,
                tableName: value
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

    checkSpaceCharacter = (rule: any, value: any, callback: any) => {
        const reg = /^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g;
        if (reg.test(value)) {
            /* eslint-disable-next-line */
            callback('该参数不能包含空格符！')
        }
        callback()
    }

    validateChineseCharacter = (data: any) => {
        const reg = /(，|。|；|[\u4e00-\u9fa5]+)/; // 中文字符，中文逗号，句号，分号
        let has = false;
        let fieldsName: any = [];
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
                'warning'
            );
        }
    };

    onFtpPathChange = (e: any) => {
        const {
            sourceMap, handleSourceMapChange,
            taskCustomParams,
            updateDataSyncVariables
        } = this.props;
        let paths = get(sourceMap, 'type.path', ['']);
        if (!isArray(paths)) {
            paths = [paths];
        }
        const index = parseInt(e.target.getAttribute('data-index'), 10);
        paths[index] = utils.trim(e.target.value);
        const srcmap = Object.assign({}, sourceMap);
        srcmap.type.path = paths;
        handleSourceMapChange(srcmap);
        // 提取路径中的自定义参数
        updateDataSyncVariables(srcmap, null, taskCustomParams)
    }

    debounceFtpChange = debounceEventHander(this.onFtpPathChange, 300, { maxWait: 2000 });

    onAddFtpPath = () => {
        const { sourceMap, handleSourceMapChange } = this.props;
        let paths = get(sourceMap, 'type.path', ['']);
        if (!isArray(paths)) {
            paths = [paths];
        }
        paths.push('');
        const srcmap = Object.assign({}, sourceMap);
        srcmap.type.path = paths;
        handleSourceMapChange(srcmap);
    }

    onRemoveFtpPath = (index: any) => {
        const { sourceMap, handleSourceMapChange } = this.props;
        const paths = get(sourceMap, 'type.path', ['']);
        const srcmap = Object.assign({}, sourceMap);
        paths.splice(index, 1);
        srcmap.type.path = paths;
        handleSourceMapChange(srcmap);
    }

    submitForm (event?: any, sourceKey?: any, value?: any, key?: any) {
        const { form, handleSourceMapChange, sourceMap } = this.props;
        let tempObj: any = {};
        if (key) {
            tempObj = { extTable: assign({}, { ...sourceMap.type.extTable }, { [key]: value }) }
        } else if (value) {
            tempObj = {
                table: value
            }
        }

        this.timerID = setTimeout(() => {
            let values = form.getFieldsValue();
            // clean no use property
            for (let key in values) {
                if (values[key] === '') {
                    values[key] = undefined;
                }
            }
            // 去空格
            if (values.partition) {
                values.partition = utils.removeAllSpaces(values.partition);
            }
            if (values.path && !isArray(values.path)) {
                values.path = utils.removeAllSpaces(values.path);
            }
            const srcmap = assign({}, sourceMap.type, { ...values, ...tempObj }, {
                src: this.getDataObjById(values.sourceId)
            });
            handleSourceMapChange(srcmap, sourceKey);
        }, 0);
        // 需放在定时器外为了保证设置值在getFieldsValue之前
        if (value && key) {
            form.setFieldsValue({ [`extTable.${key}`]: value })
        } else if (value) {
            form.setFieldsValue({ table: value });
        }
    }

    next (cb: any) {
        const { form, sourceMap } = this.props;
        let validateFields = null;
        if (sourceMap.type && sourceMap.type.type === DATA_SOURCE.HDFS) {
            validateFields = ['sourceId', 'path', 'fileType'];
            if (sourceMap.type.fileType === 'text') {
                validateFields.push('encoding');
            }
        }
        const formData = form.getFieldsValue();

        form.validateFieldsAndScroll(
            validateFields,
            { force: true },
            (err: any, values: any) => {
                if (!err) {
                    // 校验中文字符，如果有则发出警告
                    this.validateChineseCharacter(formData);
                    /* eslint-disable-next-line */
                    cb.call(null, 1);
                }
            }
        );
    }
    getPopupContainer () {
        return this.props.dataSyncRef;
    }
    render () {
        const { getFieldDecorator } = this.props.form;
        const {
            sourceMap,
            dataSourceList,
            navtoStep,
            isIncrementMode
        } = this.props;

        const disablePreview =
            isEmpty(sourceMap) ||
            sourceMap.type.type === DATA_SOURCE.HDFS ||
            sourceMap.type.type === DATA_SOURCE.HBASE ||
            sourceMap.type.type === DATA_SOURCE.FTP;

        const getPopupContainer = this.props.getPopupContainer;
        const dataSourceListFltKylin = dataSourceList && dataSourceList.filter((src: any) => src.type !== DATA_SOURCE.KYLIN);
        const disableFix = { disabled: disablePreview }
        return (
            <div className="g-step1">
                <Form>
                    <FormItem {...formItemLayout} label="数据源">
                        {getFieldDecorator('sourceId', {
                            rules: [
                                {
                                    required: true,
                                    message: '数据源为必填项'
                                }
                            ],
                            initialValue: isEmpty(sourceMap)
                                ? ''
                                : `${sourceMap.sourceId}`
                        })(
                            <Select
                                getPopupContainer={getPopupContainer}
                                showSearch
                                onSelect={this.changeSource.bind(this)}
                                optionFilterProp="name"
                            >
                                {dataSourceListFltKylin.map((src: any) => {
                                    let title = `${src.dataName}（${(DATA_SOURCE_TEXT as any)[src.type]}）`;
                                    const disableSelect =
                                        src.type === DATA_SOURCE.ES ||
                                        src.type === DATA_SOURCE.REDIS ||
                                        src.type === DATA_SOURCE.MONGODB ||
                                        // 增量模式需要禁用非关系型数据库
                                        (isIncrementMode && !isRDB(src.type));

                                    return (
                                        <Option
                                            dataType={src.type}
                                            key={src.id}
                                            name={src.dataName}
                                            value={`${src.id}`}
                                            disabled={disableSelect}
                                        >
                                            {title}
                                        </Option>
                                    );
                                })}
                            </Select>
                        )}
                    </FormItem>
                    {this.renderDynamicForm()}
                    {!isEmpty(sourceMap) ? (
                        <FormItem
                            {...formItemLayout}
                            label="高级配置"
                        >
                            {getFieldDecorator('extralConfig', {
                                rules: [{
                                    validator: formJsonValidator
                                }],
                                initialValue: get(sourceMap, 'extralConfig', '')
                            })(
                                <TextArea
                                    onChange={this.submitForm.bind(this)}
                                    placeholder="以JSON格式添加高级参数，例如对关系型数据库可配置fetchSize"
                                    autosize={{ minRows: 2, maxRows: 6 }}
                                />
                            )}
                            <HelpDoc doc='dataSyncExtralConfigHelp' />
                        </FormItem>
                    ) : null}
                </Form>
                <div
                    className="m-datapreview"
                    style={{
                        width: '90%',
                        margin: '0 auto',
                        overflow: 'auto',
                        textAlign: 'center'
                    }}
                >
                    <p style={{ cursor: 'pointer', marginBottom: 10 }}>
                        <a
                            {...disableFix}
                            href="javascript:void(0)"
                            onClick={this.loadPreview.bind(this)}
                        >
                            数据预览
                            {this.state.showPreview ? (
                                <Icon type="up" />
                            ) : (
                                <Icon type="down" />
                            )}
                        </a>
                    </p>
                    {this.state.showPreview ? (
                        <Table
                            dataSource={this.state.dataSource}
                            columns={this.state.columns}
                            scroll={{
                                x: this.state.columns.reduce((a: any, b: any) => {
                                    return a + b.width;
                                }, 0)
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
            </div>
        );
    }

    loadPreview () {
        const { showPreview } = this.state;
        const { form } = this.props;
        const sourceId = form.getFieldValue('sourceId');
        let tableName = form.getFieldValue('table');

        if (!sourceId || !tableName) {
            message.error('数据源或表名缺失');
            return;
        }
        if (tableName instanceof Array) {
            tableName = tableName[0];
        }
        if (!showPreview) {
            ajax.getDataPreview({
                sourceId,
                tableName
            }).then((res: any) => {
                if (res.code === 1) {
                    const { columnList, dataList } = res.data;

                    let columns = columnList.map((s: any) => {
                        return {
                            title: s,
                            dataIndex: s,
                            key: s,
                            width: 20 + s.length * 10
                        };
                    });
                    let dataSource = dataList.map((arr: any, i: any) => {
                        let o: any = {};
                        for (let j = 0; j < arr.length; j++) {
                            o.key = i;
                            o[columnList[j]] = arr[j];
                        }
                        return o;
                    });

                    this.setState({
                        columns,
                        dataSource,
                        showPreview: true
                    });
                }
            });
        } else {
            this.setState({
                showPreview: false
            });
        }
    }

    debounceTableSearch = debounce(this.changeTable, 300, { maxWait: 2000 });
    debounceExtTableSearch = debounce(this.changeExtTable, 300, {
        maxWait: 2000
    });

    renderExtDataSource = () => {
        const { selectHack, isChecked, tableListMap } = this.state;
        const { sourceMap, dataSourceList } = this.props;
        const { getFieldDecorator } = this.props.form;
        const sourceList = sourceMap.sourceList;
        const showArrowFix = { showArrow: true }
        if (!sourceList) {
            return [];
        }
        return sourceList
            .filter((source: any) => {
                return source.key != 'main';
            })
            .map((source: any) => {
                const tableValue = source.sourceId == null
                    ? null
                    : '' + source.sourceId;
                return (
                    <div key={source.key}>
                        <FormItem {...formItemLayout} label="数据源">
                            {getFieldDecorator(`extSourceId.${source.key}`, {
                                rules: [
                                    {
                                        required: true,
                                        message: '数据源为必填项'
                                    }
                                ],
                                initialValue:
                                    tableValue
                            })(
                                <Select
                                    showSearch
                                    onSelect={this.changeExtSource.bind(
                                        this,
                                        source.key
                                    )}
                                    optionFilterProp="name"
                                >
                                    {dataSourceList
                                        .filter((dataSource: any) => {
                                            return (
                                                dataSource.type ==
                                                sourceList[0].type
                                            );
                                        })
                                        .map((src: any) => {
                                            let title = `${src.dataName}（${(DATA_SOURCE_TEXT as any)[src.type]}）`;

                                            const disableSelect =
                                                src.type === DATA_SOURCE.ES ||
                                                src.type ===
                                                DATA_SOURCE.REDIS ||
                                                src.type ===
                                                DATA_SOURCE.MONGODB;

                                            return (
                                                <Option
                                                    dataType={src.type}
                                                    key={src.id}
                                                    name={src.dataName}
                                                    value={`${src.id}`}
                                                    disabled={disableSelect}
                                                >
                                                    {title}
                                                </Option>
                                            );
                                        })}
                                </Select>
                            )}
                            <Icon
                                onClick={this.deleteExtSource.bind(
                                    this,
                                    source.key
                                )}
                                className="help-doc click-icon"
                                type="delete"
                            />
                        </FormItem>
                        {!selectHack && (
                            <div>
                                <FormItem
                                    {...formItemLayout}
                                    label="表名"
                                    key="table"
                                >
                                    {getFieldDecorator(`extTable.${source.key}`, {
                                        rules: [
                                            {
                                                required: true,
                                                message: '数据源表为必选项！'
                                            }
                                        ],
                                        initialValue: source.tables
                                    })(
                                        <Select style={{ 'display': isChecked[`extTable.${source.key}`] ? 'none' : 'block' }}
                                            mode="tags"
                                            showSearch
                                            {...showArrowFix}
                                            onChange={this.debounceExtTableSearch.bind(
                                                this,
                                                source.key
                                            )}
                                            optionFilterProp="value"
                                            filterOption={filterValueOption}
                                        >
                                            {(
                                                this.state.tableListMap[source.sourceId] || []
                                            ).map((table: any) => {
                                                return (
                                                    <Option
                                                        key={`rdb-${table}`}
                                                        value={table}
                                                    >
                                                        {table}
                                                    </Option>
                                                );
                                            })}
                                        </Select>
                                    )}
                                    <Tooltip title="此处可以选择多表，请保证它们的表结构一致">
                                        <Icon
                                            className="help-doc"
                                            type="question-circle-o"
                                        />
                                    </Tooltip>
                                    {
                                        (this.isMysqlTable && isChecked[`extTable.${source.key}`]) ? (
                                            <Row>
                                                <Col>
                                                    <BatchSelect sourceKey={ source.key } sourceMap={ sourceMap } key={ tableValue } tabData={ tableListMap[source.sourceId] } handleSelectFinish={ this.handleSelectFinishFromBatch } />
                                                </Col>
                                            </Row>
                                        ) : null
                                    }
                                </FormItem>
                                {
                                    this.isMysqlTable ? (
                                        <Row className="form-item-follow-text">
                                            <Col
                                                style={{ textAlign: 'right', fontSize: '13PX' }}
                                                span={formItemLayout.wrapperCol.sm.span}
                                                offset={formItemLayout.labelCol.sm.span}
                                            >
                                                {/* 选择一张或多张表，选择多张表时，请保持它们的表结构一致，大批量选择，可以 */}
                                                {/* disabled注意添加数据源之后无数据产生的bug问题 */}
                                                <Checkbox name='isChecked' disabled={ source.sourceId == null } onChange={ () => this.handleCheckboxChange(`extTable.${source.key}`, event) } checked={ isChecked[`extTable.${source.key}`] } >
                                                    <a {...{ disabled: source.sourceId == null }}>
                                                        批量选择
                                                    </a>
                                                </Checkbox>
                                            </Col>
                                        </Row>
                                    ) : null
                                }
                            </div>
                        )}
                    </div>
                );
            });
    };

    renderIncrementColumns = () => {
        const { sourceMap, isIncrementMode } = this.props;
        const { incrementColumns } = this.state;
        const { getFieldDecorator } = this.props.form;
        const columnsOpts = incrementColumns.map((o: any) => <Option key={o.key}>{o.key}（{o.type}）</Option>);
        return isIncrementMode
            ? <FormItem
                {...formItemLayout}
                label="增量标识字段"
                style={{ height: '32px' }}
            >
                {getFieldDecorator('syncModel', {
                    rules: [{
                        required: true,
                        message: '必须选择增量标识字段！'
                    }],
                    initialValue: sourceMap.increColumn || undefined
                })(
                    <Select
                        placeholder="请选择增量标识字段"
                        onChange={this.onIncrementColumnChange}
                    >
                        {columnsOpts}
                    </Select>
                )}
                <HelpDoc doc="incrementColumnHelp" />
            </FormItem>
            : '';
    }

    // sourceKey 用在isChecked中来判断具体是哪一个sourceId已选中
    handleCheckboxChange = (sourceKey: any, event: any) => {
        const { isChecked } = this.state;
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value; // 拿到布尔值
        this.setState({
            isChecked: { ...isChecked, ...{ [sourceKey]: value } }
        });
        // this.props.form.setFieldsValue({ table: this.props.sourceMap.type.table, [`extTable.${sourceKey}`]: this.props.sourceMap.type.extTable[sourceKey] });
    }

    // sourceKey 为需要向redux处理的其他数据源的key值，构造action数据
    // selectKey 为穿梭框选中的数据
    handleSelectFinishFromBatch = (selectKey: any, type: any, sourceKey: any) => {
        if (sourceKey) {
            this.changeTable(type, selectKey, sourceKey);
        } else {
            this.changeTable(type, selectKey);
        }
    }

    renderDynamicForm = () => {
        const { getFieldDecorator } = this.props.form;
        const { selectHack, isChecked, tableListMap } = this.state;
        const { sourceMap, isIncrementMode } = this.props;
        const fileType = (sourceMap.type && sourceMap.type.fileType) || 'text';

        const getPopupContainer = this.props.getPopupContainer;
        const haveChineseQuote = !!(sourceMap && sourceMap.type && /(‘|’|”|“)/.test(sourceMap.type.where));

        // 非增量模式
        const supportSubLibrary = SUPPROT_SUB_LIBRARY_DB_ARRAY.indexOf(sourceMap &&
            sourceMap.sourceList &&
            sourceMap.sourceList[0].type
        ) > -1 && !isIncrementMode;

        let formItem: any;
        if (isEmpty(sourceMap)) return null;
        switch (sourceMap.type.type) {
            case DATA_SOURCE.GBASE:
            case DATA_SOURCE.DB2:
            case DATA_SOURCE.MYSQL:
            case DATA_SOURCE.ORACLE:
            case DATA_SOURCE.CLICK_HOUSE:
            case DATA_SOURCE.SQLSERVER:
            case DATA_SOURCE.LIBRASQL:
            case DATA_SOURCE.POSTGRESQL: {
                const tableValue = isEmpty(sourceMap) ? '' : supportSubLibrary
                    ? sourceMap.sourceList[0].tables
                    : sourceMap.type.table;
                formItem = [
                    !selectHack ? (
                        <div>
                            <FormItem {...formItemLayout} label={ this.isMysqlTable ? '表名(批量)' : '表名' } key="rdbtable">
                                {getFieldDecorator('table', {
                                    rules: [
                                        {
                                            required: true,
                                            message: '数据源表为必选项！'
                                        }
                                    ],
                                    initialValue: tableValue
                                })(
                                    <Select
                                        style={{ 'display': isChecked[ sourceMap.sourceId ] ? 'none' : 'block' }}
                                        disabled={ this.isMysqlTable && isChecked[sourceMap.sourceId] }
                                        getPopupContainer={getPopupContainer}
                                        mode={
                                            supportSubLibrary ? 'tags' : 'combobox'
                                        }
                                        showSearch
                                        {...{ showArrow: true }}
                                        onBlur={this.debounceTableSearch.bind(
                                            this,
                                            sourceMap.type.type
                                        )}
                                        optionFilterProp="value"
                                        filterOption={filterValueOption}
                                    >
                                        {(
                                            this.state.tableListMap[sourceMap.sourceId] || []
                                        ).map((table: any) => {
                                            return (
                                                <Option
                                                    key={`rdb-${table}`}
                                                    value={table}
                                                >
                                                    {table}
                                                </Option>
                                            );
                                        })}
                                    </Select>
                                )}
                                {
                                    (this.isMysqlTable && isChecked[sourceMap.sourceId]) ? (
                                        <Row>
                                            <Col>
                                                <BatchSelect sourceMap={ sourceMap } key={ tableValue } tabData={ tableListMap[sourceMap.sourceId] } handleSelectFinish={ this.handleSelectFinishFromBatch } />
                                            </Col>
                                        </Row>
                                    ) : null
                                }
                                {
                                    isChecked[sourceMap.sourceId] ? null : (
                                        supportSubLibrary && (
                                            <Tooltip title="此处可以选择多表，请保证它们的表结构一致">
                                                <Icon
                                                    className="help-doc"
                                                    type="question-circle-o"
                                                />
                                            </Tooltip>
                                        )
                                    )
                                }
                            </FormItem>
                            {
                                this.isMysqlTable ? (
                                    <Row className="form-item-follow-text">
                                        <Col
                                            style={{ textAlign: 'right', fontSize: '13PX' }}
                                            span={formItemLayout.wrapperCol.sm.span}
                                            offset={formItemLayout.labelCol.sm.span}
                                        >
                                            {/* 选择一张或多张表，选择多张表时，请保持它们的表结构一致，大批量选择，可以 */}
                                            <Checkbox name='isChecked' onChange={ () => { this.handleCheckboxChange(sourceMap.sourceId, event) } } checked={ isChecked[sourceMap.sourceId] } >
                                                <a {...{ disabled: sourceMap.sourceId == null }}>
                                                    批量选择
                                                </a>
                                            </Checkbox>
                                        </Col>
                                    </Row>
                                ) : null
                            }
                        </div>
                    ) : null,
                    ...this.renderExtDataSource(),
                    supportSubLibrary && (
                        <Row className="form-item-follow-text">
                            <Col
                                style={{ textAlign: 'left' }}
                                span={formItemLayout.wrapperCol.sm.span}
                                offset={formItemLayout.labelCol.sm.span}
                            >
                                <a onClick={this.addDataSource.bind(this)}>
                                    添加数据源
                                </a>
                            </Col>
                        </Row>
                    ),
                    this.renderIncrementColumns(),
                    <FormItem {...formItemLayout} label="数据过滤" key="where">
                        {getFieldDecorator('where', {
                            rules: [
                                {
                                    max: 1000,
                                    message: '过滤语句不可超过1000个字符!'
                                }
                            ],
                            initialValue: isEmpty(sourceMap)
                                ? ''
                                : sourceMap.type.where
                        })(
                            <Input
                                type="textarea"
                                placeholder="请参考相关SQL语法填写where过滤语句（不要填写where关键字）。该过滤语句通常用作增量同步"
                                onChange={this.submitForm.bind(this)}
                            />
                        )}
                        <HelpDoc doc="dataFilterDoc" />
                    </FormItem>,
                    haveChineseQuote && (
                        <Row className="form-item-follow-text">
                            <Col
                                style={{ textAlign: 'left' }}
                                span={formItemLayout.wrapperCol.sm.span}
                                offset={formItemLayout.labelCol.sm.span}
                            >
                                <p className="warning-color">
                                    当前输入含有中文引号
                                </p>
                            </Col>
                        </Row>
                    ),
                    <FormItem {...formItemLayout} label="切分键" key="splitPK">
                        {getFieldDecorator('splitPK', {
                            rules: [],
                            initialValue: isEmpty(sourceMap)
                                ? ''
                                : sourceMap.type.splitPK
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
                                                    self.indexOf(v) === index
                                            )) ||
                                    []
                                ).map((copateValue: any, index: any) => {
                                    return (
                                        <Option
                                            key={`copate-${index}`}
                                            value={copateValue}
                                        >
                                            {/* ORACLE数据库单独考虑ROW_NUMBER() 这个函数， 展示去除括号 */}
                                            { (sourceMap.type.type === DATA_SOURCE.ORACLE && copateValue === 'ROW_NUMBER()') ? 'ROW_NUMBER' : copateValue}
                                        </Option>
                                    );
                                })}
                            </Select>
                        )}
                        <HelpDoc doc="selectKey" />
                    </FormItem>
                ];
                break;
            }
            case DATA_SOURCE.CARBONDATA: {
                formItem = [
                    !selectHack ? (
                        <FormItem {...formItemLayout} label="表名" key="table">
                            {getFieldDecorator('table', {
                                rules: [
                                    {
                                        required: true,
                                        message: '数据源表为必选项！'
                                    }
                                ],
                                initialValue: isEmpty(sourceMap)
                                    ? ''
                                    : sourceMap.type.table
                            })(
                                <Select
                                    getPopupContainer={getPopupContainer}
                                    mode={'combobox'}
                                    showSearch
                                    {...{ showArrow: true }}
                                    onBlur={this.debounceTableSearch.bind(
                                        this,
                                        sourceMap.type.type
                                    )}
                                    optionFilterProp="value"
                                    filterOption={filterValueOption}
                                >
                                    {(
                                        this.state.tableListMap[sourceMap.sourceId] || []
                                    ).map((table: any) => {
                                        return (
                                            <Option
                                                key={`carbondata-${table}`}
                                                value={table}
                                            >
                                                {table}
                                            </Option>
                                        );
                                    })}
                                </Select>
                            )}
                        </FormItem>
                    ) : null,
                    ...this.renderExtDataSource(),
                    <FormItem {...formItemLayout} label="数据过滤" key="where">
                        {getFieldDecorator('where', {
                            rules: [
                                {
                                    max: 1000,
                                    message: '过滤语句不可超过1000个字符!'
                                }
                            ],
                            initialValue: isEmpty(sourceMap)
                                ? ''
                                : sourceMap.type.where
                        })(
                            <Input
                                type="textarea"
                                placeholder="请参考相关SQL语法填写where过滤语句（不要填写where关键字）。该过滤语句通常用作增量同步"
                                onChange={this.submitForm.bind(this)}
                            />
                        )}
                        <HelpDoc doc="dataFilterDoc" />
                    </FormItem>,
                    haveChineseQuote && (
                        <Row className="form-item-follow-text">
                            <Col
                                style={{ textAlign: 'left' }}
                                span={formItemLayout.wrapperCol.sm.span}
                                offset={formItemLayout.labelCol.sm.span}
                            >
                                <p className="warning-color">
                                    当前输入含有中文引号
                                </p>
                            </Col>
                        </Row>
                    )
                ];
                break;
            }
            case DATA_SOURCE.MAXCOMPUTE:
            case DATA_SOURCE.HIVE_1:
            case DATA_SOURCE.HIVE_2: {
                // Hive
                formItem = [
                    !selectHack && (
                        <FormItem {...formItemLayout} label="表名" key="table">
                            {getFieldDecorator('table', {
                                rules: [
                                    {
                                        required: true,
                                        message: '数据源表为必选项！'
                                    }
                                ],
                                initialValue: isEmpty(sourceMap)
                                    ? ''
                                    : sourceMap.type.table
                            })(
                                <Select
                                    getPopupContainer={getPopupContainer}
                                    showSearch
                                    mode="combobox"
                                    onBlur={this.debounceTableSearch.bind(
                                        this,
                                        null
                                    )}
                                    optionFilterProp="value"
                                    filterOption={filterValueOption}
                                >
                                    {(
                                        this.state.tableListMap[sourceMap.sourceId] || []
                                    ).map((table: any) => {
                                        return (
                                            <Option
                                                key={`rdb-${table}`}
                                                value={table}
                                            >
                                                {table}
                                            </Option>
                                        );
                                    })}
                                </Select>
                            )}
                        </FormItem>
                    ),
                    <FormItem {...formItemLayout} label="分区" key="partition">
                        {getFieldDecorator('partition', {
                            rules: [],
                            initialValue: isEmpty(sourceMap)
                                ? ''
                                : sourceMap.type.partition
                        })(
                            <Select
                                mode="combobox"
                                showSearch
                                {...{ showArrow: true }}
                                optionFilterProp="value"
                                placeholder="请填写分区信息"
                                onChange={this.submitForm.bind(this)}
                                filterOption={filterValueOption}
                            >
                                {
                                    (this.state.tablePartitionList || []).map((pt: any) => {
                                        return (
                                            <Option
                                                key={`rdb-${pt}`}
                                                value={pt}
                                            >
                                                {pt}
                                            </Option>
                                        );
                                    })}
                            </Select>
                        )}
                        <HelpDoc doc="partitionDesc" />
                    </FormItem>
                ];
                break;
            }
            case DATA_SOURCE.HDFS: {
                // HDFS
                formItem = [
                    <FormItem
                        {...formItemLayout}
                        label="路径"
                        key="path"
                    >
                        {getFieldDecorator('path', {
                            rules: [
                                {
                                    required: true,
                                    message: '路径不得为空！'
                                },
                                {
                                    max: 200,
                                    message: '路径不得超过200个字符！'
                                },
                                {
                                    validator: this.validatePath
                                }
                            ],
                            validateTrigger: 'onSubmit',
                            initialValue: isEmpty(sourceMap)
                                ? ''
                                : sourceMap.type.path
                        })(
                            <Input
                                placeholder="例如: /rdos/batch"
                                onChange={this.submitForm.bind(this)}
                            />
                        )}
                        <HelpDoc doc="hdfsPath" />
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="文件类型"
                        key="fileType"
                    >
                        {getFieldDecorator('fileType', {
                            rules: [
                                {
                                    required: true
                                }
                            ],
                            initialValue:
                                sourceMap.type && sourceMap.type.fileType
                                    ? sourceMap.type.fileType
                                    : 'text'
                        })(
                            <Select
                                getPopupContainer={getPopupContainer}
                                onChange={this.submitForm.bind(this)}
                            >
                                <Option value="orc">orc</Option>
                                <Option value="text">text</Option>
                                <Option value="parquet">parquet</Option>
                            </Select>
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        style={{
                            display: fileType === 'text' ? 'block' : 'none'
                        }}
                        label="列分隔符"
                        key="fieldDelimiter"
                    >
                        {getFieldDecorator('fieldDelimiter', {
                            rules: [],
                            initialValue: isEmpty(sourceMap)
                                ? ','
                                : sourceMap.type.fieldDelimiter
                        })(
                            <Input
                                /* eslint-disable */
                                placeholder="若不填写，则默认为\001"
                                /* eslint-disable */
                                onChange={this.submitForm.bind(this)}
                            />
                        )}
                        <HelpDoc doc="splitCharacter" />
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="编码"
                        key="encoding"
                        style={{
                            display: fileType === 'text' ? 'block' : 'none'
                        }}
                    >
                        {getFieldDecorator('encoding', {
                            rules: [
                                {
                                    required: true
                                }
                            ],
                            initialValue:
                                !sourceMap.type || !sourceMap.type.encoding
                                    ? 'utf-8'
                                    : sourceMap.type.encoding
                        })(
                            <Select
                                getPopupContainer={getPopupContainer}
                                onChange={this.submitForm.bind(this)}
                            >
                                <Option value="utf-8">utf-8</Option>
                                <Option value="gbk">gbk</Option>
                            </Select>
                        )}
                    </FormItem>
                ];
                break;
            }
            case DATA_SOURCE.HBASE: {
                formItem = [
                    !selectHack && (
                        <FormItem {...formItemLayout} label="表名" key="table">
                            {getFieldDecorator('table', {
                                rules: [
                                    {
                                        required: true,
                                        message: '数据源表为必选项！'
                                    }
                                ],
                                initialValue: isEmpty(sourceMap)
                                    ? ''
                                    : sourceMap.type.table
                            })(
                                <Select
                                    getPopupContainer={getPopupContainer}
                                    showSearch
                                    mode="combobox"
                                    onBlur={this.debounceTableSearch.bind(
                                        this,
                                        null
                                    )}
                                    optionFilterProp="value"
                                    filterOption={filterValueOption}
                                >
                                    {(
                                        this.state.tableListMap[sourceMap.sourceId] || []
                                    ).map((table: any) => {
                                        return (
                                            <Option
                                                key={`hbase-${table}`}
                                                value={table}
                                            >
                                                {table}
                                            </Option>
                                        );
                                    })}
                                </Select>
                            )}
                        </FormItem>
                    ),
                    <FormItem {...formItemLayout} label="编码" key="encoding">
                        {getFieldDecorator('encoding', {
                            rules: [
                                {
                                    required: true
                                }
                            ],
                            initialValue:
                                sourceMap.type && sourceMap.type.encoding
                                    ? sourceMap.type.encoding
                                    : 'utf-8'
                        })(
                            <Select
                                getPopupContainer={getPopupContainer}
                                onChange={this.submitForm.bind(this)}
                            >
                                <Option value="utf-8">utf-8</Option>
                                <Option value="gbk">gbk</Option>
                            </Select>
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="开始行健"
                        key="startRowkey"
                    >
                        {getFieldDecorator('startRowkey', {
                            rules: [],
                            initialValue:
                                sourceMap.type && sourceMap.type.startRowkey
                                    ? sourceMap.type.startRowkey
                                    : ''
                        })(
                            <Input
                                placeholder="startRowkey"
                                onChange={this.submitForm.bind(this)}
                            />
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="结束行健"
                        key="endRowkey"
                    >
                        {getFieldDecorator('endRowkey', {
                            rules: [],
                            initialValue:
                                sourceMap.type && sourceMap.type.endRowkey
                                    ? sourceMap.type.endRowkey
                                    : ''
                        })(
                            <Input
                                placeholder="endRowkey"
                                onChange={this.submitForm.bind(this)}
                            />
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        className="txt-left"
                        label="行健二进制转换"
                        key="isBinaryRowkey"
                    >
                        {getFieldDecorator('isBinaryRowkey', {
                            rules: [],
                            initialValue:
                                sourceMap.type && sourceMap.type.isBinaryRowkey
                                    ? sourceMap.type.isBinaryRowkey
                                    : '0'
                        })(
                            <RadioGroup onChange={this.submitForm.bind(this)}>
                                <Radio value="0" style={{ float: 'left' }}>
                                    FALSE
                                </Radio>
                                <Radio value="1" style={{ float: 'left' }}>
                                    TRUE
                                </Radio>
                            </RadioGroup>
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="每次RPC请求获取行数"
                        key="scanCacheSize"
                    >
                        {getFieldDecorator('scanCacheSize', {
                            rules: [],
                            initialValue:
                                sourceMap.type && sourceMap.type.scanCacheSize
                                    ? sourceMap.type.scanCacheSize
                                    : ''
                        })(
                            <Input
                                onChange={this.submitForm.bind(this)}
                                placeholder="请输入大小, 默认为256"
                                type="number"
                                {...{ min: 0 }}
                                suffix="行"
                            />
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="每次RPC请求获取列数"
                        key="scanBatchSize"
                    >
                        {getFieldDecorator('scanBatchSize', {
                            rules: [],
                            initialValue:
                                sourceMap.type && sourceMap.type.scanBatchSize
                                    ? sourceMap.type.scanBatchSize
                                    : ''
                        })(
                            <Input
                                onChange={this.submitForm.bind(this)}
                                placeholder="请输入大小, 默认为100"
                                {...{ min: 0 }}
                                suffix="列"
                            />
                        )}
                    </FormItem>
                ];
                break;
            }
            case DATA_SOURCE.FTP: {
                const paths = get(sourceMap, 'type.path', ['']);
                const getItem = (path: any, index: any) => {
                    return (
                        <div style={{ paddingBottom: 10, position: 'relative' }} key={`path_${index}`}>
                            <Input
                                className="ant-input-lg"
                                placeholder="例如: /rdos/batch"
                                defaultValue={path}
                                data-index={index}
                                onChange={this.debounceFtpChange}
                            />
                            {index > 0 ? <Button
                                onClick={this.onRemoveFtpPath.bind(this, index)}
                                {...{ title: "删除当前路径" }}
                                shape="circle"
                                style={removeBtnStyle}
                                icon="minus"
                            /> : ''
                            }
                        </div>
                    )
                }
                const removeBtnStyle: any = {
                    position: 'absolute',
                    right: '-25px',
                    top: '6px',
                    cursor: ' pointer',
                    width: '20px',
                    height: '20px'
                }
                let pathItems: any = getItem(paths, 0);
                if (isArray(paths)) {
                    pathItems = paths.map && paths.map((path: any, index: any) => getItem(path, index));
                }

                formItem = [
                    <FormItem {...formItemLayout} label="路径" key="path">
                        {pathItems}
                        <div style={{ lineHeight: '12px' }}><a onClick={this.onAddFtpPath}>添加路径</a></div>
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="列分隔符"
                        key="fieldDelimiter"
                    >
                        {getFieldDecorator('fieldDelimiter', {
                            rules: [
                                {
                                    required: true,
                                    message: '分隔符不可为空！'
                                }
                            ],
                            initialValue: isEmpty(sourceMap)
                                ? ','
                                : sourceMap.type.fieldDelimiter
                        })(
                            <Input
                                placeholder="默认值为,"
                                onChange={this.submitForm.bind(this)}
                            />
                        )}
                        <HelpDoc doc="splitCharacter" />
                    </FormItem>,
                    <FormItem {...formItemLayout} label="编码" key="encoding">
                        {getFieldDecorator('encoding', {
                            rules: [
                                {
                                    required: true,
                                    message: '必须选择一种编码！'
                                }
                            ],
                            initialValue:
                                !sourceMap.type || !sourceMap.type.encoding
                                    ? 'utf-8'
                                    : sourceMap.type.encoding
                        })(
                            <Select
                                getPopupContainer={getPopupContainer}
                                onChange={this.submitForm.bind(this)}
                            >
                                <Option value="utf-8">utf-8</Option>
                                <Option value="gbk">gbk</Option>
                            </Select>
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="是否包含表头"
                        key="isFirstLineHeader"
                    >
                        {getFieldDecorator('isFirstLineHeader', {
                            rules: [
                                {
                                    required: true,
                                    message: '必须选择一种编码！'
                                }
                            ],
                            initialValue:
                                !sourceMap.type ||
                                    !sourceMap.type.isFirstLineHeader
                                    ? false
                                    : sourceMap.type.isFirstLineHeader
                        })(
                            <RadioGroup onChange={this.submitForm.bind(this)}>
                                <Radio value={true} style={{ float: 'left' }}>
                                    是
                                </Radio>
                                <Radio value={false} style={{ float: 'left' }}>
                                    否
                                </Radio>
                            </RadioGroup>
                        )}
                    </FormItem>
                ];
                break;
            }
            case DATA_SOURCE.KUDU: {
                formItem = [
                    <FormItem {...formItemLayout} label="表名" key="table">
                        {getFieldDecorator('table', {
                            rules: [
                                {
                                    required: true,
                                    message: '数据源表为必选项！'
                                }
                            ],
                            initialValue: isEmpty(sourceMap)
                                ? ''
                                : sourceMap.type.table
                        })(
                            <Select
                                getPopupContainer={getPopupContainer}
                                showSearch
                                mode="combobox"
                                onBlur={this.debounceTableSearch.bind(
                                    this,
                                    null
                                )}
                                optionFilterProp="value"
                                filterOption={filterValueOption}
                            >
                                {(
                                    this.state.tableListMap[sourceMap.sourceId] || []
                                ).map((table: any) => {
                                    return (
                                        <Option
                                            key={`rdb-${table}`}
                                            value={table}
                                        >
                                            {table}
                                        </Option>
                                    );
                                })}
                            </Select>
                        )}
                    </FormItem>,
                    <FormItem {...formItemLayout} label="数据过滤" key="where">
                        {getFieldDecorator('where', {
                            rules: [
                                {
                                    max: 1000,
                                    message: '过滤语句不可超过1000个字符!'
                                }
                            ],
                            initialValue: isEmpty(sourceMap)
                                ? ''
                                : sourceMap.type.where
                        })(
                            <Input
                                type="textarea"
                                placeholder="填写where过滤条件（不需要填写where关键字），过滤条件只支持=、>、<、>=、<=、and操作符，该过滤语句常用作增量同步"
                                onChange={this.submitForm.bind(this)}
                            />
                        )}
                        <HelpDoc doc="dataFilterDoc" />
                    </FormItem>
                ];
                break;
            }
            default: break;
        }
        return formItem;
    };
}

const SourceFormWrap = Form.create<any>()(SourceForm);

class Source extends React.Component<any, any> {
    constructor(props: any) {
        super(props);
    }

    render () {
        return (
            <div>
                <SourceFormWrap {...this.props} />
            </div>
        );
    }
}

const mapState = (state: any) => {
    const { workbench, dataSync } = state.offlineTask;
    const { isCurrentTabNew, currentTab } = workbench;

    return {
        isCurrentTabNew,
        currentTab,
        sourceMap: dataSync.sourceMap,
        targetMap: dataSync.targetMap,
        dataSourceList: dataSync.dataSourceList
    };
};
const mapDispatch = (dispatch: any, ownProps: any) => {
    return {
        addDataSource(key: any) {
            dispatch({
                type: sourceMapAction.DATA_SOURCE_ADD,
                key: key
            });
        },
        deleteDataSource(key: any) {
            dispatch({
                type: sourceMapAction.DATA_SOURCE_DELETE,
                key: key
            });
        },
        changeExtDataSource (src: any, key: any) {
            dispatch({
                type: sourceMapAction.DATA_SOURCE_CHANGE,
                payload: src,
                key: key
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },
        handleSourceChange: (src: any) => {
            dispatch({
                type: dataSyncAction.RESET_SOURCE_MAP
            });
            dispatch({
                type: dataSyncAction.RESET_KEYMAP
            });
            dispatch({
                type: sourceMapAction.DATA_SOURCE_CHANGE,
                payload: src
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },

        handleSourceMapChange: (srcmap: any, key: any) => {
            dispatch({
                type: sourceMapAction.DATA_SOURCEMAP_CHANGE,
                payload: srcmap,
                key: key || 'main'
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },

        handleTableColumnChange: (colData: any) => {
            dispatch({
                type: dataSyncAction.RESET_KEYMAP
            });
            dispatch({
                type: sourceMapAction.SOURCE_TABLE_COLUMN_CHANGE,
                payload: colData
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },

        handleTableCopateChange: (copateData: any) => {
            dispatch({
                type: sourceMapAction.SOURCE_TABLE_COPATE_CHANGE,
                payload: copateData
            });
        },
        assignSourceMap: (src: any) => {
            dispatch({
                type: sourceMapAction.DATA_SOURCEMAP_UPDATE,
                payload: src
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },
        updateTaskFields(params: any) {
            dispatch({
                type: workbenchAction.SET_TASK_FIELDS_VALUE,
                payload: params
            });
        }
    };
};

export default connect(
    mapState,
    mapDispatch
)(Source);
