import React from 'react';
import { connect } from 'react-redux';
import {
    Form, Input, Select,
    Button, Icon, Table,
    message, Radio
} from 'antd';
import { isEmpty, debounce } from 'lodash';
import assign from 'object-assign';

import ajax from '../../../../api';
import {
    sourceMapAction,
    dataSyncAction,
    workbenchAction
} from '../../../../store/modules/offlineTask/actionType';

import HelpDoc from '../../../helpDoc';
import { matchTaskParams } from '../../../../comm';
import { DatabaseType } from '../../../../components/status';

import {
    formItemLayout,
    dataSourceTypes,
    DATA_SOURCE,
} from '../../../../comm/const';

const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;

class SourceForm extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            tableList: [],
            showPreview: false,
            dataSource: [],
            columns: []
        };
    }

    componentDidMount() {
        const { sourceMap } = this.props;
        const { sourceId } = sourceMap;

        sourceId && this.getTableList(sourceId);
    }

    componentWillUnmount() {
        clearInterval(this.timerID);
    }

    getTableList = (sourceId) => {
        const ctx = this
        const { sourceMap } = this.props
        if (sourceMap.type &&
            (
                sourceMap.type.type === DATA_SOURCE.HDFS ||
                sourceMap.type.type === DATA_SOURCE.FTP
            )
        ) {
            return;
        }

        this.setState({
            tableList: [],
            showPreview: false
        }, () => {
            ajax.getOfflineTableList({
                sourceId,
                isSys: false
            }).then(res => {
                if (res.code === 1) {
                    ctx.setState({
                        tableList: res.data || []
                    });
                }
            });
        });
    }

    getTableColumn(tableName) {
        const { form, handleTableColumnChange } = this.props;
        const { sourceMap } = this.props

        if (sourceMap.type &&
            (sourceMap.type.type === DATA_SOURCE.HBASE)
        ) {
            return;
        }

        const sourceId = form.getFieldValue('sourceId');
        ajax.getOfflineTableColumn({
            sourceId,
            tableName
        }).then(res => {
            if (res.code === 1) {
                handleTableColumnChange(res.data);
            } else {
                handleTableColumnChange([]);
            }
        })
    }

    getDataObjById(id) {
        const { dataSourceList } = this.props;
        return dataSourceList.filter(src => {
            return src.id == id;
        })[0];
    }

    changeSource(value) {
        const { handleSourceChange } = this.props;
        setTimeout(() => {
            this.getTableList(value);
        }, 0);

        handleSourceChange(this.getDataObjById(value));
        this.resetTable();
    }   

    resetTable(){
        const { form } = this.props;
        this.changeTable('');
        //这边先隐藏结点，然后再reset，再显示。不然会有一个组件自带bug。
        this.setState({
            selectHack:true
        },()=>{
            form.resetFields(['table'])
            this.setState({
                selectHack:false
            })
        })
        
    }

    changeTable(value) {
        if(value){
            this.getTableColumn(value);
        }
        this.submitForm();
        this.setState({
            showPreview: false
        })
    }

    validatePath = (rule, value, callback) => {
        const { handleTableColumnChange, form } = this.props;
        const { getFieldValue } = form
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
                callback('该路径无效！')
            })
        } else {
            callback()
        }
    }

    submitForm() {
        const {
            updateTaskFields, form,
            handleSourceMapChange, currentTab,
            sourceMap, taskCustomParams,
        } = this.props;

        this.timerID = setTimeout(() => {
            let values = form.getFieldsValue();

            // clean no use property
            for (let key in values) {
                if (values[key] === '') {
                    delete values[key];
                }
            }

            // where, 获取任务自定义参数
            if (values.where) {
                const taskVariables = matchTaskParams(taskCustomParams, values.where)
                if (taskVariables.length > 0) {
                    updateTaskFields({
                        id: currentTab,
                        taskVariables
                    })
                }
            }

            // 分区，获取任务自定义参数
            if (values.partition) {
                const taskVariables = matchTaskParams(taskCustomParams, values.partition)
                if (taskVariables.length > 0) {
                    updateTaskFields({
                        id: currentTab,
                        taskVariables
                    })
                }
            }

            const srcmap = assign(values, {
                src: this.getDataObjById(values.sourceId)
            });
            handleSourceMapChange(srcmap);
        }, 0);
    }

    next(cb) {
        const { form, handleSourceMapChange, sourceMap } = this.props;
        let validateFields = null;
        if (sourceMap.type && sourceMap.type.type === DATA_SOURCE.HDFS) {
            validateFields = ['sourceId', 'path', 'fileType'];
            if (sourceMap.type.fileType === 'text') {
                validateFields.push('encoding')
            }
        }

        form.validateFieldsAndScroll(validateFields, { force: true }, (err, values) => {
            if (!err) {
                cb.call(null, 1);
            }
        })
    }

    render() {
        const { getFieldDecorator } = this.props.form;
        const {
            sourceMap, dataSourceList,
            navtoStep, isCurrentTabNew,
        } = this.props;

        const disablePreview = isEmpty(sourceMap) ||
              sourceMap.type.type === DATA_SOURCE.HDFS ||
              sourceMap.type.type === DATA_SOURCE.HBASE ||
              sourceMap.type.type === DATA_SOURCE.FTP;

        return <div className="g-step1">
            <Form>
                <FormItem
                    {...formItemLayout}
                    label="数据源"
                >
                    {getFieldDecorator('sourceId', {
                        rules: [{
                            required: true
                        }],
                        initialValue: isEmpty(sourceMap) ? '' : `${sourceMap.sourceId}`
                    })(
                        <Select
                            showSearch
                            onChange={this.changeSource.bind(this)}
                            optionFilterProp="name"
                            disabled={ !isCurrentTabNew }
                        >
                            {dataSourceList.map(src => {
                                return (
                                    <Option
                                        key={src.id}
                                        name={src.dataName}
                                        value={`${src.id}`}
                                        disabled={
                                            src.type === DATA_SOURCE.ES
                                        }
                                    >
                                        {src.dataName}( <DatabaseType value={src.type} /> )
                                    </Option>
                                )
                            })}
                        </Select>
                    )}
                </FormItem>
                {this.renderDynamicForm()}
            </Form>
            <div className="m-datapreview" style={{
                width: '90%',
                margin: '0 auto',
                overflow: 'auto'
            }}>
                <p style={{ cursor: 'pointer', marginBottom: 10 }} >
                    <a 
                        disabled={ disablePreview }
                        href="javascript:void(0)" 
                        onClick={this.loadPreview.bind(this)}
                    >
                        数据预览{this.state.showPreview ? <Icon type="up" /> : <Icon type="down" />}
                    </a>
                </p>
                {this.state.showPreview ?
                    <Table dataSource={this.state.dataSource}
                        columns={this.state.columns}
                        scroll={{ x: 200 * this.state.columns.length }}
                        pagination={false}
                        bordered={false}
                    /> : null
                }
            </div>
            {!this.props.readonly && <div className="steps-action">
                <Button type="primary" onClick={() => this.next(navtoStep)}>下一步</Button>
            </div>}
        </div>
    }

    loadPreview() {
        const { showPreview } = this.state;
        const { form } = this.props;
        const sourceId = form.getFieldValue('sourceId');
        const tableName = form.getFieldValue('table');

        if (!sourceId || !tableName) {
            message.error('数据源或表名缺失');
            return;
        }

        if (!showPreview) {
            ajax.getDataPreview({
                sourceId, tableName
            })
                .then(res => {
                    if (res.code === 1) {
                        const { columnList, dataList } = res.data;

                        let columns = columnList.map(s => {
                            return {
                                title: s,
                                dataIndex: s,
                                key: s
                            }
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
                            columns, dataSource, showPreview: true
                        });
                    }
                })
        }
        else {
            this.setState({
                showPreview: false
            })
        }
    }

    debounceTableSearch = debounce(this.changeTable, 300, { 'maxWait': 2000 })

    renderDynamicForm() {
        const { getFieldDecorator } = this.props.form;
        const { selectHack } = this.state;
        const { sourceMap, dataSourceList, isCurrentTabNew } = this.props;
        const fileType = (sourceMap.type && sourceMap.type.fileType) || 'text';
        let formItem;
        if (isEmpty(sourceMap)) return null;
        switch (sourceMap.type.type) {
            case DATA_SOURCE.MYSQL:
            case DATA_SOURCE.ORACLE:
            case DATA_SOURCE.SQLSERVER: {
                formItem = [
                    !selectHack&&<FormItem
                        {...formItemLayout}
                        label="表名"
                        key="table"
                    >
                        {getFieldDecorator('table', {
                            rules: [{
                                required: true,
                                message: '数据源表为必选项！'
                            }],
                            initialValue: isEmpty(sourceMap) ? '' : sourceMap.type.table
                        })(
                            <Select
                                mode="combobox"
                                showSearch
                                showArrow={true}
                                onChange={this.debounceTableSearch.bind(this)}
                                disabled={!isCurrentTabNew}
                                optionFilterProp="value"
                            >
                                {this.state.tableList.map(table => {
                                    return <Option key={`rdb-${table}`} value={table}>
                                        {table}
                                    </Option>
                                })}
                            </Select>
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="数据过滤"
                        key="where"
                    >
                        {getFieldDecorator('where', {
                            rules: [{
                                max: 1000,
                                message: '过滤语句不可超过1000个字符!',
                            }],
                            initialValue: isEmpty(sourceMap) ? '' : sourceMap.type.where
                        })(
                            <Input
                                type="textarea"
                                placeholder="请参考相关SQL语法填写where过滤语句（不要填写where关键字）。该过滤语句通常用作增量同步"
                                onChange={this.submitForm.bind(this)}
                            ></Input>,
                        )}
                        <HelpDoc doc="dataFilterDoc" />
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="切分键"
                        key="splitPK"
                    >
                        {getFieldDecorator('splitPK', {
                            rules: [],
                            initialValue: isEmpty(sourceMap) ? '' : sourceMap.splitPK
                        })(
                            <Input
                                type="text"
                                placeholder="根据配置的字段进行数据分片，实现并发读取"
                                onChange={this.submitForm.bind(this)}
                            ></Input>
                        )}
                        <HelpDoc doc="switchKey" />
                    </FormItem>
                ];
                break;
            }
            case DATA_SOURCE.MAXCOMPUTE:
            case DATA_SOURCE.HIVE: {// Relational DB
                formItem = [
                    !selectHack&&<FormItem
                        {...formItemLayout}
                        label="表名"
                        key="table"
                    >
                        {getFieldDecorator('table', {
                            rules: [{
                                required: true
                            }],
                            initialValue: isEmpty(sourceMap) ? '' : sourceMap.type.table
                        })(
                            <Select
                                showSearch
                                mode="combobox"
                                onChange={this.debounceTableSearch.bind(this)}
                                disabled={!isCurrentTabNew}
                                optionFilterProp="value"
                            >
                                {this.state.tableList.map(table => {
                                    return <Option key={`rdb-${table}`} value={table}>
                                        {table}
                                    </Option>
                                })}
                            </Select>
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="分区"
                        key="partition"
                    >
                        {getFieldDecorator('partition', {
                            rules: [],
                            initialValue: isEmpty(sourceMap) ? '' : sourceMap.type.partition
                        })(
                            <Input
                                placeholder="请填写分区"
                                placeholder="pt=${bdp.system.bizdate};"
                                onChange={this.submitForm.bind(this)}
                            ></Input>,
                        )}
                        <HelpDoc doc="partitionDesc" />
                    </FormItem>
                ];
                break;
            }
            case DATA_SOURCE.HDFS: // HDFS
                formItem = [
                    <FormItem
                        {...formItemLayout}
                        label="路径"
                        key="path"
                    >
                        {getFieldDecorator('path', {
                            rules: [{
                                required: true,
                                message: '路径不得为空！',
                            }, {
                                max: 200,
                                message: '路径不得超过200个字符！',
                            }, {
                                validator: this.validatePath
                            }],
                            validateTrigger: 'onSubmit',
                            initialValue: isEmpty(sourceMap) ? '' : sourceMap.type.path
                        })(
                            <Input
                                placeholder="例如: /rdos/batch"
                                onChange={this.submitForm.bind(this)} />
                        )}
                        <HelpDoc doc="hdfsPath" />
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
                            initialValue: !sourceMap.type || !sourceMap.type.fileType ? 'text' : sourceMap.type.fileType
                        })(
                            <Select onChange={this.submitForm.bind(this)} >
                                <Option value="orc">orc</Option>
                                <Option value="text">text</Option>
                            </Select>
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="编码"
                        key="encoding"
                        style={{ display: fileType === 'text' ? 'block' : 'none' }}
                    >
                        {getFieldDecorator('encoding', {
                            rules: [{
                                required: true
                            }],
                            initialValue: !sourceMap.type || !sourceMap.type.encoding ? 'utf-8' : sourceMap.type.encoding
                        })(
                            <Select onChange={this.submitForm.bind(this)}>
                                <Option value="utf-8">utf-8</Option>
                                <Option value="gbk">gbk</Option>
                            </Select>
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        style={{ display: fileType === 'text' ? 'block' : 'none' }}
                        label="分隔符"
                        key="fieldDelimiter"
                    >
                        {getFieldDecorator('fieldDelimiter', {
                            rules: [],
                            initialValue: isEmpty(sourceMap) ? ',' : sourceMap.type.fieldDelimiter
                        })(
                            <Input
                                placeholder="若不填写，则默认为\001"
                                onChange={this.submitForm.bind(this)} />
                        )}
                    </FormItem>,
                ];
                break;
            case DATA_SOURCE.HBASE:
                formItem = [
                    !selectHack&&<FormItem
                        {...formItemLayout}
                        label="表名"
                        key="table"
                    >
                        {getFieldDecorator('table', {
                            rules: [{
                                required: true
                            }],
                            initialValue: isEmpty(sourceMap) ? '' : sourceMap.type.table
                        })(
                            <Select
                                showSearch
                                mode="combobox"
                                onChange={this.debounceTableSearch.bind(this)}
                                disabled={!isCurrentTabNew}
                                optionFilterProp="value"
                            >
                                {this.state.tableList.map(table => {
                                    return <Option key={`hbase-${table}`} value={table}>
                                        {table}
                                    </Option>
                                })}
                            </Select>
                        )}
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
                            initialValue: sourceMap.type && sourceMap.type.encoding ? sourceMap.type.encoding : 'utf-8'
                        })(
                            <Select onChange={this.submitForm.bind(this)}>
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
                            initialValue: sourceMap.type && sourceMap.type.startRowkey ? sourceMap.type.startRowkey : ''
                        })(
                            <Input
                                placeholder="startRowkey"
                                onChange={this.submitForm.bind(this)} />
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="结束行健"
                        key="endRowkey"
                    >
                        {getFieldDecorator('endRowkey', {
                            rules: [],
                            initialValue: sourceMap.type && sourceMap.type.endRowkey ? sourceMap.type.endRowkey : ''
                        })(
                            <Input
                                placeholder="endRowkey"
                                onChange={this.submitForm.bind(this)} />
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
                            initialValue: sourceMap.type && sourceMap.type.isBinaryRowkey ? sourceMap.type.isBinaryRowkey : '0'
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
                            initialValue: sourceMap.type && sourceMap.type.scanCacheSize ? sourceMap.type.scanCacheSize : ''
                        })(
                            <Input
                                onChange={this.submitForm.bind(this)}
                                placeholder="请输入大小, 默认为256"
                                type="number"
                                min={0}
                                suffix="行"
                            ></Input>
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="每次RPC请求获取列数"
                        key="scanBatchSize"
                    >
                        {getFieldDecorator('scanBatchSize', {
                            rules: [],
                            initialValue: sourceMap.type && sourceMap.type.scanBatchSize ? sourceMap.type.scanBatchSize : ''
                        })(
                            <Input
                                onChange={this.submitForm.bind(this)}
                                placeholder="请输入大小, 默认为100"
                                type="number"
                                min={0}
                                suffix="列"
                            ></Input>
                        )}
                    </FormItem>
                ]
                break;
            case DATA_SOURCE.FTP: {
                formItem = [
                    <FormItem
                        {...formItemLayout}
                        label="路径"
                        key="path"
                    >
                        {getFieldDecorator('path', {
                            rules: [{
                                required: true,
                                message: '路径不得为空！',
                            }, {
                                max: 200,
                                message: '路径不得超过200个字符！',
                            }, {
                                validator: this.validatePath
                            }],
                            validateTrigger: 'onSubmit',
                            initialValue: isEmpty(sourceMap) ? '' : sourceMap.type.path
                        })(
                            <Input
                                placeholder="例如: /rdos/batch"
                                onChange={this.submitForm.bind(this)} />
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        style={{ display: fileType === 'text' ? 'block' : 'none' }}
                        label="列分隔符"
                        key="fieldDelimiter"
                    >
                        {getFieldDecorator('fieldDelimiter', {
                            rules: [{
                                required: true,
                                message: '分隔符不可为空！',
                            }],
                            initialValue: isEmpty(sourceMap) ? ',' : sourceMap.type.fieldDelimiter
                        })(
                            <Input
                                placeholder="默认值为,"
                                onChange={this.submitForm.bind(this)} />
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="编码"
                        key="encoding"
                        style={{ display: fileType === 'text' ? 'block' : 'none' }}
                    >
                        {getFieldDecorator('encoding', {
                            rules: [{
                                required: true,
                                message: '必须选择一种编码！',
                            }],
                            initialValue: !sourceMap.type || !sourceMap.type.encoding ? 'utf-8' : sourceMap.type.encoding
                        })(
                            <Select onChange={this.submitForm.bind(this)}>
                                <Option value="utf-8">utf-8</Option>
                                <Option value="gbk">gbk</Option>
                            </Select>
                        )}
                    </FormItem>,
                ]
                break;
            }
            default: break;
        }

        return formItem;
    }
}

const SourceFormWrap = Form.create()(SourceForm);

class Source extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return <div>
            <SourceFormWrap {...this.props} />
        </div>
    }
}

const mapState = state => {
    const { dataSync, workbench } = state.offlineTask;
    const { isCurrentTabNew, currentTab } = workbench;

    return {
        isCurrentTabNew,
        currentTab,
        sourceMap: dataSync.sourceMap,
        dataSourceList: dataSync.dataSourceList,
        taskCustomParams: workbench.taskCustomParams,
    };
};
const mapDispatch = dispatch => {
    return {
        handleSourceChange: src => {
            dispatch({
                type: dataSyncAction.RESET_SOURCE_MAP,
            });
            dispatch({
                type: dataSyncAction.RESET_KEYMAP,
            });
            dispatch({
                type: sourceMapAction.DATA_SOURCE_CHANGE,
                payload: src
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },

        handleSourceMapChange: srcmap => {
            dispatch({
                type: sourceMapAction.DATA_SOURCEMAP_CHANGE,
                payload: srcmap
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

        updateTaskFields(params) {
            dispatch({
                type: workbenchAction.SET_TASK_FIELDS_VALUE,
                payload: params
            });
        }
    }
};

export default connect(mapState, mapDispatch)(Source);