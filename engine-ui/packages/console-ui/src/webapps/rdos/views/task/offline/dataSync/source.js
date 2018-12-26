import React from 'react';
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
    Tooltip
} from 'antd';
import { isEmpty, debounce, get, isArray } from 'lodash';
import assign from 'object-assign';

import utils from 'utils';
import { singletonNotification, debounceEventHander } from 'funcs';
import ajax from '../../../../api';
import {
    sourceMapAction,
    dataSyncAction,
    workbenchAction
} from '../../../../store/modules/offlineTask/actionType';

import HelpDoc from '../../../helpDoc';
import { isRDB } from '../../../../comm';

import {
    formItemLayout,
    DATA_SOURCE,
    DATA_SOURCE_TEXT,
    SUPPROT_SUB_LIBRARY_DB_ARRAY,
    RDB_TYPE_ARRAY
} from '../../../../comm/const';

const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;

class SourceForm extends React.Component {
    constructor (props) {
        super(props);

        this.state = {
            tableListMap: {},
            showPreview: false,
            dataSource: [],
            columns: [],
            loading: false // 请求
        };
    }

    componentDidMount () {
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
        }
    }

    componentWillUnmount () {
        clearInterval(this.timerID);
    }

    getTableList = sourceId => {
        const ctx = this;
        const { sourceMap } = this.props;
        if (
            sourceMap.type &&
            (sourceMap.type.type === DATA_SOURCE.HDFS ||
                sourceMap.type.type === DATA_SOURCE.FTP)
        ) {
            return;
        }

        this.setState(
            {
                showPreview: false
            },
            () => {
                ajax.getOfflineTableList({
                    sourceId,
                    isSys: false
                }).then(res => {
                    if (res.code === 1) {
                        ctx.setState({
                            tableListMap: {
                                ...this.state.tableListMap,
                                [sourceId]: res.data || []
                            }
                        });
                    }
                });
            }
        );
    };

    getTableColumn = (tableName, type) => {
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
            return;
        }

        if (type && isRDB(type)) {
            this.getCopate(sourceMap.sourceId, tableName);
        }

        ajax.getOfflineTableColumn({
            sourceId: sourceMap.sourceId,
            tableName
        }).then(res => {
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

    getCopate (sourceId, tableName) {
        const { handleTableCopateChange } = this.props;
        if (tableName instanceof Array) {
            tableName = tableName[0];
        }
        ajax.getOfflineColumnForSyncopate({
            sourceId,
            tableName
        }).then(res => {
            if (res.code === 1) {
                handleTableCopateChange(res.data);
            } else {
                handleTableCopateChange([]);
            }
        });
    }

    getDataObjById (id) {
        const { dataSourceList } = this.props;
        return dataSourceList.filter(src => {
            return src.id == id;
        })[0];
    }

    changeExtSource (key, value) {
        this.props.changeExtDataSource(this.getDataObjById(value), key);
        this.getTableList(value);
        this.resetTable(`extTable.${key}`);
    }

    changeSource (value, option) {
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

    deleteExtSource (key) {
        this.props.deleteDataSource(key);
    }

    resetTable (key) {
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

    changeTable (type, value) {
        if (value) {
            this.setState({
                loading: true
            });
            this.getTableColumn(value, type);
        }
        this.submitForm();
        this.setState({
            showPreview: false
        });
    }
    changeExtTable (key, value) {
        this.submitForm(null, key);
    }

    validatePath = (rule, value, callback) => {
        const { handleTableColumnChange, form } = this.props;
        const { getFieldValue } = form;
        const sourceId = getFieldValue('sourceId');
        if (getFieldValue('fileType') === 'orc') {
            ajax.getOfflineTableColumn({
                sourceId,
                tableName: value
            }).then(res => {
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

    checkSpaceCharacter = (rule, value, callback) => {
        const reg = /^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g;
        if (reg.test(value)) {
            /* eslint-disable-next-line */
            callback('该参数不能包含空格符！')
        }
        callback()
    }

    validateChineseCharacter = data => {
        const reg = /(，|。|；|[\u4e00-\u9fa5]+)/; // 中文字符，中文逗号，句号，分号
        let has = false;
        let fieldsName = [];
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

    onFtpPathChange = (e) => {
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

    onRemoveFtpPath = (index) => {
        const { sourceMap, handleSourceMapChange } = this.props;
        const paths = get(sourceMap, 'type.path', ['']);
        const srcmap = Object.assign({}, sourceMap);
        paths.splice(index, 1);
        srcmap.type.path = paths;
        console.log('onRemoveFtpPath'.srcmap)
        handleSourceMapChange(srcmap);
    }

    submitForm (event, sourceKey) {
        const { form, handleSourceMapChange, sourceMap } = this.props;

        this.timerID = setTimeout(() => {
            let values = form.getFieldsValue();

            // clean no use property
            for (let key in values) {
                if (values[key] === '') {
                    delete values[key];
                }
            }
            // 去空格
            if (values.partition) {
                values.partition = utils.trim(values.partition);
            }
            if (values.path && !isArray(values.path)) {
                values.path = utils.trim(values.path);
            }
            const srcmap = assign({}, sourceMap.type, values, {
                src: this.getDataObjById(values.sourceId)
            });
            handleSourceMapChange(srcmap, sourceKey);
        }, 0);
    }

    next (cb) {
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
            (err, values) => {
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
                                {dataSourceList.map(src => {
                                    let title = `${src.dataName}（${
                                        DATA_SOURCE_TEXT[src.type]
                                    }）`;

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
                            disabled={disablePreview}
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
                                x: this.state.columns.reduce((a, b) => {
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
            }).then(res => {
                if (res.code === 1) {
                    const { columnList, dataList } = res.data;

                    let columns = columnList.map(s => {
                        return {
                            title: s,
                            dataIndex: s,
                            key: s,
                            width: 20 + s.length * 10
                        };
                    });
                    let dataSource = dataList.map((arr, i) => {
                        let o = {};
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
        const { selectHack } = this.state;
        const { sourceMap, dataSourceList } = this.props;
        const { getFieldDecorator } = this.props.form;
        const sourceList = sourceMap.sourceList;

        if (!sourceList) {
            return [];
        }

        return sourceList
            .filter(source => {
                return source.key != 'main';
            })
            .map(source => {
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
                                    source.sourceId == null
                                        ? null
                                        : '' + source.sourceId
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
                                        .filter(dataSource => {
                                            return (
                                                dataSource.type ==
                                                sourceList[0].type
                                            );
                                        })
                                        .map(src => {
                                            let title = `${src.dataName}（${
                                                DATA_SOURCE_TEXT[src.type]
                                            }）`;

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
                                    <Select
                                        mode="tags"
                                        showSearch
                                        showArrow={true}
                                        onChange={this.debounceExtTableSearch.bind(
                                            this,
                                            source.key
                                        )}
                                        optionFilterProp="value"
                                    >
                                        {(
                                            this.state.tableListMap[source.sourceId] || []
                                        ).map(table => {
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
                            </FormItem>
                        )}
                    </div>
                );
            });
    };

    renderDynamicForm = () => {
        const { getFieldDecorator } = this.props.form;
        const { selectHack } = this.state;
        const { sourceMap, isIncrementMode } = this.props;
        const fileType = (sourceMap.type && sourceMap.type.fileType) || 'text';

        const getPopupContainer = this.props.getPopupContainer;
        const haveChineseQuote = !!(sourceMap && sourceMap.type && /(‘|’|”|“)/.test(sourceMap.type.where));

        // 非增量模式
        const supportSubLibrary = SUPPROT_SUB_LIBRARY_DB_ARRAY.indexOf(sourceMap &&
                sourceMap.sourceList &&
                sourceMap.sourceList[0].type
        ) > -1 && !isIncrementMode;

        let formItem;
        if (isEmpty(sourceMap)) return null;

        switch (sourceMap.type.type) {
            case DATA_SOURCE.DB2:
            case DATA_SOURCE.MYSQL:
            case DATA_SOURCE.ORACLE:
            case DATA_SOURCE.SQLSERVER:
            case DATA_SOURCE.POSTGRESQL: {
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
                                    : supportSubLibrary
                                        ? sourceMap.sourceList[0].tables
                                        : sourceMap.type.table
                            })(
                                <Select
                                    getPopupContainer={getPopupContainer}
                                    mode={
                                        supportSubLibrary ? 'tags' : 'combobox'
                                    }
                                    showSearch
                                    showArrow={true}
                                    onBlur={this.debounceTableSearch.bind(
                                        this,
                                        sourceMap.type.type
                                    )}
                                    optionFilterProp="value"
                                >
                                    {(
                                        this.state.tableListMap[sourceMap.sourceId] || []
                                    ).map(table => {
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
                            {supportSubLibrary && (
                                <Tooltip title="此处可以选择多表，请保证它们的表结构一致">
                                    <Icon
                                        className="help-doc"
                                        type="question-circle-o"
                                    />
                                </Tooltip>
                            )}
                        </FormItem>
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
                                showArrow={true}
                                onChange={this.submitForm.bind(this)}
                            >
                                {(
                                    (sourceMap.copate &&
                                            sourceMap.copate
                                                .map(v => v.key)
                                                .filter(
                                                    (v, index, self) =>
                                                        self.indexOf(v) === index
                                                )) ||
                                        []
                                ).map((copateValue, index) => {
                                    return (
                                        <Option
                                            key={`copate-${index}`}
                                            value={copateValue}
                                        >
                                            {copateValue}
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
                                    showArrow={true}
                                    onBlur={this.debounceTableSearch.bind(
                                        this,
                                        sourceMap.type.type
                                    )}
                                    optionFilterProp="value"
                                >
                                    {(
                                        this.state.tableListMap[sourceMap.sourceId] || []
                                    ).map(table => {
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
            case DATA_SOURCE.HIVE: {
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
                                >
                                    {(
                                        this.state.tableListMap[sourceMap.sourceId] || []
                                    ).map(table => {
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
                            rules: [
                                // {
                                //     validator: this.checkSpaceCharacter
                                // }
                            ],
                            initialValue: isEmpty(sourceMap)
                                ? ''
                                : sourceMap.type.partition
                        })(
                            <Input
                                placeholder="请填写分区"
                                /* eslint-disable */
                                placeholder="pt=${bdp.system.bizdate}"
                                onChange={this.submitForm.bind(this)}
                            />
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
                                >
                                    {(
                                        this.state.tableListMap[sourceMap.sourceId] || []
                                    ).map(table => {
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
                                min={0}
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
                                type="number"
                                min={0}
                                suffix="列"
                            />
                        )}
                    </FormItem>
                ];
                break;
            }
            case DATA_SOURCE.FTP: {
                const paths = get(sourceMap, 'type.path', ['']);
                const getItem = (path, index) => {
                    return (
                        <div style={{ paddingBottom: 10, position: 'relative' }} key={`path_${index}`}>
                            <Input
                                className="ant-input-lg"
                                placeholder="例如: /rdos/batch"
                                defaultValue={ path }
                                data-index={index}
                                onChange={this.debounceFtpChange}
                            />
                            { index > 0 ? <Button
                                    onClick={this.onRemoveFtpPath.bind(this, index)}
                                    title="删除当前路径"
                                    shape="circle"
                                    style={removeBtnStyle} 
                                    icon="minus"
                                /> : '' 
                            }
                        </div>
                    )
                }
                const removeBtnStyle = {
                    position: 'absolute',
                    right: '-25px',
                    top: '6px',
                    cursor:' pointer',
                    width: '20px',
                    height: '20px'
                }
                let pathItems = getItem(paths, 0);
                if (isArray(paths)) {
                    pathItems = paths.map && paths.map((path, index) => getItem(path, index));
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
            default: break;
        }
        return formItem;
    };
}

const SourceFormWrap = Form.create()(SourceForm);

class Source extends React.Component {
    constructor (props) {
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

const mapState = state => {
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
const mapDispatch = (dispatch, ownProps) => {
    return {
        addDataSource (key) {
            dispatch({
                type: sourceMapAction.DATA_SOURCE_ADD,
                key: key
            });
        },
        deleteDataSource (key) {
            dispatch({
                type: sourceMapAction.DATA_SOURCE_DELETE,
                key: key
            });
        },
        changeExtDataSource (src, key) {
            dispatch({
                type: sourceMapAction.DATA_SOURCE_CHANGE,
                payload: src,
                key: key
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },
        handleSourceChange: src => {
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

        handleSourceMapChange: (srcmap, key) => {
            dispatch({
                type: sourceMapAction.DATA_SOURCEMAP_CHANGE,
                payload: srcmap,
                key: key || 'main'
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },

        handleTableColumnChange: colData => {
            dispatch({
                type: sourceMapAction.SOURCE_TABLE_COLUMN_CHANGE,
                payload: colData
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },

        handleTableCopateChange: copateData => {
            dispatch({
                type: sourceMapAction.SOURCE_TABLE_COPATE_CHANGE,
                payload: copateData
            });
        },

        updateTaskFields (params) {
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
