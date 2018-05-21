import React from 'react';
import { connect } from 'react-redux';
import { Form, Input, Select, Button, Radio } from 'antd';
import { isEmpty } from 'lodash';
import assign from 'object-assign';

import ajax from '../../../../api';
import {
    targetMapAction,
    dataSyncAction,
    workbenchAction
} from '../../../../store/modules/offlineTask/actionType';

import { 
    formItemLayout, 
    dataSourceTypes,
    DATA_SOURCE,
} from '../../../../comm/const';

import HelpDoc from '../../../helpDoc';

import { matchTaskParams } from '../../../../comm';

const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;

class TargetForm extends React.Component{
    constructor(props) {
        super(props);

        this.state = {
            tableList: []
        };
    }

    componentDidMount() {
        const { targetMap } = this.props;
        const { sourceId } = targetMap;

        sourceId && this.getTableList(sourceId);
    }


    getTableList = (sourceId) => {
        const ctx = this
        const { targetMap } = this.props

        // 排除条件
        if (targetMap.type && (
                targetMap.type.type === DATA_SOURCE.HDFS ||
                targetMap.type.type === DATA_SOURCE.FTP )
            ) {
            return true;
        } 

        ctx.setState({
            tableList: []
        }, () => {
            ajax.getOfflineTableList({
                sourceId,
                isSys: false
            }).then(res => {
                if(res.code === 1) {
                    ctx.setState({
                        tableList: res.data
                    });
                }
            });
        })
    }

    getTableColumn = (tableName) => {
        const { form, handleTableColumnChange, targetMap } = this.props;
        const sourceId = form.getFieldValue('sourceId');

        // 排除条件
        if (targetMap.type && targetMap.type.type === DATA_SOURCE.HBASE) {
            return true;
        } 

        ajax.getOfflineTableColumn({
            sourceId,
            tableName
        }).then(res => {
            if(res.code === 1) {
                handleTableColumnChange(res.data);
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
        setTimeout(()=> {
            this.getTableList(value);
        }, 0);
        handleSourceChange(this.getDataObjById(value));
    }

    changeTable(value) {
        this.getTableColumn(value);
        this.submitForm();
    }

    submitForm() {
        const { 
            taskCustomParams, updateTaskFields,
            form, handleTargetMapChange, currentTab, 
        } = this.props;

        setTimeout(() => {
            let values = form.getFieldsValue();

            const sqlText = `${values.preSql} ${values.postSql}`
            // 获取任务自定义参数
            if (sqlText) {
                const taskVariables = matchTaskParams(taskCustomParams, sqlText)
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
            handleTargetMapChange(srcmap);
        }, 0);
    }

    prev(cb) {
        cb.call(null, 0);
    }

    next(cb) {
        const { form, handleTargetMapChange } = this.props;
        form.validateFields((err, values) => {
            if(!err) {
                cb.call(null, 2);
            }
        })
    }

    render() {
        const { getFieldDecorator } = this.props.form;

        const {
            targetMap, sourceMap, dataSourceList,
            navtoStep, isCurrentTabNew 
        } = this.props;

        return <div className="g-step2">
            <Form>
                <FormItem
                    {...formItemLayout}
                    label="数据同步目标"
                >
                {getFieldDecorator('sourceId', {
                    rules: [{
                        required: true
                    }],
                    initialValue: isEmpty(targetMap) ? '' : `${targetMap.sourceId}`
                })(
                    <Select
                        showSearch
                        onChange={ this.changeSource.bind(this) }
                        disabled={ !isCurrentTabNew }
                        optionFilterProp="name"
                    >
                        {dataSourceList.map(src => {
                            return <Option key={ src.id } 
                                name={src.dataName}
                                value={ `${src.id}` }>
                                { src.dataName }( { dataSourceTypes[src.type] } )
                            </Option>
                        })}
                    </Select>
                )}
                </FormItem>
                { this.renderDynamicForm() }
            </Form>
            {!this.props.readonly && <div className="steps-action">
                <Button style={{ marginRight: 8 }} onClick={() => this.prev(navtoStep)}>上一步</Button>
                <Button type="primary" onClick={() => this.next(navtoStep)}>下一步</Button>
            </div>}
        </div>
    }

    renderDynamicForm() {
        const { getFieldDecorator } = this.props.form;

        const { targetMap, dataSourceList, isCurrentTabNew } = this.props;
        let formItem;

        if(isEmpty(targetMap)) return null;

        switch(targetMap.type.type) {
            case DATA_SOURCE.MYSQL:
            case DATA_SOURCE.ORACLE:
            case DATA_SOURCE.SQLSERVER:
                formItem = [
                    <FormItem
                        {...formItemLayout}
                        label="表名"
                        key="table"
                    >
                    {getFieldDecorator('table', {
                        rules: [{
                            required: true
                        }],
                        initialValue: isEmpty(targetMap) ? '' : targetMap.type.table
                    })(
                        <Select
                            showSearch
                            onChange={ this.changeTable.bind(this) }
                            disabled={ !isCurrentTabNew }
                            optionFilterProp="value"
                        >
                            {this.state.tableList.map(table => {
                                return <Option 
                                    key={ `rdb-target-${table}` } 
                                    value={ table }>
                              main/consts      { table }
                                </Option>
                            })}
                        </Select>
                    )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="导入前准备语句"
                        key="preSql"
                    >
                    {getFieldDecorator('preSql', {
                        rules: [],
                        initialValue: isEmpty(targetMap) ? '' : targetMap.type.preSql
                    })(
                        <Input
                            onChange={ this.submitForm.bind(this) }
                            placeholder="请输入导入数据前执行的SQL脚本"
                            type="textarea"
                        ></Input>
                    )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="导入后准备语句"
                        key="postSql"
                    >
                    {getFieldDecorator('postSql', {
                        rules: [],
                        initialValue: isEmpty(targetMap) ? '' : targetMap.type.postSql
                    })(
                        <Input
                            onChange={ this.submitForm.bind(this) }
                            placeholder="请输入导入数据后执行的SQL脚本"
                            type="textarea"
                        ></Input>
                    )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="主键冲突"
                        key="writeMode"
                        className="txt-left"
                    >
                    {getFieldDecorator('writeMode', {
                        rules: [{
                            required: true
                        }],
                        initialValue: targetMap.type && targetMap.type.writeMode ? targetMap.type.writeMode : 'replace'
                    })(
                        <RadioGroup onChange={ this.submitForm.bind(this) }>
                            <Radio value="replace" style={{float: 'left'}}>
                                替换原有数据（Replace Into）
                            </Radio>
                            <Radio value="insert" style={{float: 'left'}}>
                                视为脏数据，保留原有数据（Insert Into）
                            </Radio>
                        </RadioGroup>
                    )}
                    </FormItem>
                ];
                break;
            case DATA_SOURCE.HIVE:
                formItem = [
                    <FormItem
                        {...formItemLayout}
                        label="表名"
                        key="table"
                    >
                        {getFieldDecorator('table', {
                            rules: [{
                                required: true
                            }],
                            initialValue: isEmpty(targetMap) ? '' : targetMap.type.table
                        })(
                            <Select
                                showSearch
                                onChange={this.changeTable.bind(this)}
                                disabled={!isCurrentTabNew}
                                optionFilterProp="value"
                            >
                                {this.state.tableList.map(table => {
                                    return <Option key={`rdb-target-${table}`} value={table}>
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
                            initialValue: isEmpty(targetMap) ? '' : targetMap.type.partition
                        })(
                            <Input
                                onChange={this.submitForm.bind(this)}
                                placeholder="pt=${bdp.system.bizdate};"
                            ></Input>
                            )}
                            <HelpDoc doc="partitionDesc" />
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="写入模式"
                        key="writeMode"
                        className="txt-left"
                    >
                        {getFieldDecorator('writeMode', {
                            rules: [{
                                required: true
                            }],
                            initialValue: targetMap.type && targetMap.type.writeMode ? targetMap.type.writeMode : 'replace'
                        })(
                            <RadioGroup onChange={this.submitForm.bind(this)}>
                                <Radio value="replace" style={{ float: 'left' }}>
                                    覆盖（Insert Overwrite）
                                </Radio>
                                <Radio value="insert" style={{ float: 'left' }}>
                                    追加（Insert Into）
                                </Radio>
                            </RadioGroup>
                        )}
                    </FormItem>
                ];
                break;
            case DATA_SOURCE.HDFS:
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
                            placeholder="例如: /rdos/batch"
                            onChange={ this.submitForm.bind(this) } />
                    )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="分隔符"
                        key="fieldDelimiter"
                    >
                    {getFieldDecorator('fieldDelimiter', {
                        rules: [],
                        initialValue: isEmpty(targetMap) ? ',' : targetMap.type.fieldDelimiter
                    })(
                        <Input
                            placeholder="例如: 目标为hive则 分隔符为\001"
                            onChange={ this.submitForm.bind(this) } />
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
                        initialValue: isEmpty(targetMap) || !targetMap.type.encoding ? 'utf-8' : targetMap.type.encoding
                    })(
                        <Select onChange={ this.submitForm.bind(this) }>
                            <Option value="utf-8">utf-8</Option>
                            <Option value="gbk">gbk</Option>
                        </Select>
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
                        <Input onChange={ this.submitForm.bind(this) } />
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
                        initialValue: !targetMap.type || !targetMap.type.fileType  ? 'orc' : targetMap.type.fileType
                    })(
                        <Select  onChange={ this.submitForm.bind(this) } >
                            <Option value="orc">orc</Option>
                            <Option value="text">text</Option>
                        </Select>
                    )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="写入模式"
                        className="txt-left"
                        key="writeMode"
                    >
                    {getFieldDecorator('writeMode', {
                        rules: [{
                            required: true
                        }],
                        initialValue: targetMap.type && targetMap.type.writeMode ? targetMap.type.writeMode : 'APPEND'
                    })(
                        <RadioGroup onChange={ this.submitForm.bind(this) }>
                            <Radio value="APPEND" style={{float: 'left'}}>
                                追加新数据
                            </Radio>
                            <Radio value="NONCONFLICT" style={{float: 'left'}}>
                                覆盖老数据
                            </Radio>
                        </RadioGroup>
                    )}
                    </FormItem>
                ];
                break;
            case DATA_SOURCE.HBASE: {
                formItem = [
                    <FormItem
                        {...formItemLayout}
                        label="表名"
                        key="table"
                    >
                    {getFieldDecorator('table', {
                        rules: [{
                            required: true
                        }],
                        initialValue: targetMap.type && targetMap.type.table ? targetMap.type.table : ''
                    })(
                        <Select
                            showSearch
                            onChange={ this.changeTable.bind(this) }
                            disabled={ !isCurrentTabNew }
                            optionFilterProp="value"
                        >
                            {this.state.tableList.map(table => {
                                return <Option key={ `hbase-target-${table}` } value={ table }>
                                    { table }
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
                        initialValue: targetMap.type && targetMap.type.encoding ? targetMap.type.encoding : 'utf-8' 
                    })(
                        <Select onChange={ this.submitForm.bind(this) }>
                            <Option value="utf-8">utf-8</Option>
                            <Option value="gbk">gbk</Option>
                        </Select>
                    )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="读取为空时的处理方式"
                        key="nullMode"
                        className="txt-left"
                    >
                        {getFieldDecorator('nullMode', {
                        rules: [{
                            required: true,
                            message: '请选择为空时的处理方式!'
                        }],
                        initialValue: targetMap.type && targetMap.type.nullMode ? targetMap.type.nullMode : 'skip' 
                    })(
                        <RadioGroup onChange={ this.submitForm.bind(this) }>
                            <Radio value="skip" style={{float: 'left'}}>
                                SKIP
                            </Radio>
                            <Radio value="empty" style={{float: 'left'}}>
                                EMPTY
                            </Radio>
                        </RadioGroup>
                    )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="写入缓存大小"
                        key="writeBufferSize"
                    >
                    {getFieldDecorator('writeBufferSize', {
                        rules: [],
                        initialValue: isEmpty(targetMap) ? '' : targetMap.type.writeBufferSize
                    })(
                        <Input
                            onChange={ this.submitForm.bind(this) }
                            placeholder="请输入缓存大小"
                            type="number"
                            min={0}
                            suffix="KB"
                        ></Input>
                    )}
                    </FormItem>
                ]
                break;
            }
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
                        },{
                            max: 200,
                            message: '路径不得超过200个字符！',
                        }, {
                            validator: this.validatePath
                        }],
                        validateTrigger: 'onSubmit',
                        initialValue: isEmpty(targetMap) ? '' : targetMap.type.path
                    })(
                        <Input
                            placeholder="例如: /rdos/batch"
                            onChange={ this.submitForm.bind(this) } />
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
                            initialValue: !targetMap.type || !targetMap.type.encoding ? 'utf-8' : targetMap.type.encoding
                        })(
                            <Select onChange={ this.submitForm.bind(this) }>
                                <Option value="utf-8">utf-8</Option>
                                <Option value="gbk">gbk</Option>
                            </Select>
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="分隔符"
                        key="fieldDelimiter"
                    >
                    {getFieldDecorator('fieldDelimiter', {
                        rules: [],
                        initialValue: isEmpty(targetMap) ? ',' : targetMap.type.fieldDelimiter
                    })(
                        <Input
                            placeholder="若不填写，则默认为\001"
                            onChange={ this.submitForm.bind(this) } />
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

const TargetFormWrap = Form.create()(TargetForm);

class Target extends React.Component{
    constructor(props) {
        super(props);
    }

    render() {
        return <div>
            <TargetFormWrap {...this.props} />
        </div>
    }
}

const mapState = state => {
    const { dataSync, workbench } = state.offlineTask;
    const { isCurrentTabNew, currentTab } = workbench;

    return {
        currentTab,
        isCurrentTabNew,
        targetMap: dataSync.targetMap,
        sourceMap: dataSync.sourceMap,
        dataSourceList: dataSync.dataSourceList,
        taskCustomParams: workbench.taskCustomParams,
    };
};

const mapDispatch = dispatch => {
    return {
        handleSourceChange(src) {
            dispatch({
                type: dataSyncAction.RESET_TARGET_MAP,
            });
            dispatch({
                type: dataSyncAction.RESET_KEYMAP,
            });
            dispatch({
                type: targetMapAction.DATA_SOURCE_TARGET_CHANGE,
                payload: src
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },

        handleTargetMapChange(srcmap) {
            
            dispatch({
                type: targetMapAction.DATA_TARGETMAP_CHANGE,
                payload: srcmap
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },

        handleTableColumnChange: colData => {
            dispatch({
                type: targetMapAction.TARGET_TABLE_COLUMN_CHANGE,
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
    };
}

export default connect(mapState, mapDispatch)(Target);