import React from 'react';
import { connect } from 'react-redux';
import { Form, Input, Select, Button, Radio, Modal, Icon } from 'antd';
import { isEmpty, debounce } from 'lodash';
import assign from 'object-assign';

import ajax from '../../../../api';
import {
    targetMapAction,
    dataSyncAction,
    workbenchAction
} from '../../../../store/modules/offlineTask/actionType';

import {
    formItemLayout,
    DATA_SOURCE,
    DATA_SOURCE_TEXT
} from '../../../../comm/const';

import HelpDoc from '../../../helpDoc';
import Editor from 'widgets/editor';

import { DDL_ide_placeholder } from "../../../../comm/DDLCommon";

const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;

class TargetForm extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            tableList: [],
            visible: false,
            modalLoading: false,
            loading: false
        };
    }

    componentDidMount() {
        const { targetMap, isCurrentTabNew } = this.props;
        const { sourceId } = targetMap;

        sourceId && this.getTableList(sourceId);
    }


    getTableList = (sourceId) => {
        const ctx = this
        const { targetMap } = this.props

        // 排除条件
        if (targetMap.type && (
            targetMap.type.type === DATA_SOURCE.HDFS ||
            targetMap.type.type === DATA_SOURCE.FTP)
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
                if (res.code === 1) {
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
            if (res.code === 1) {
                handleTableColumnChange(res.data);
            } else {
                handleTableColumnChange([]);
            }
        })
    }

    /**
     * 根据数据源id获取数据源信息
     * @param {*} id 
     */
    getDataObjById(id) {
        const { dataSourceList } = this.props;
        return dataSourceList.filter(src => {
            return src.id == id;
        })[0];
    }

    changeSource(value) {
        const { handleSourceChange, form } = this.props;
        setTimeout(() => {
            this.getTableList(value);
        }, 0);
        handleSourceChange(this.getDataObjById(value));
        this.resetTable();
    }

    resetTable() {
        const { form } = this.props;
        this.changeTable('');
        //这边先隐藏结点，然后再reset，再显示。不然会有一个组件自带bug。
        this.setState({
            selectHack: true
        }, () => {
            form.resetFields(['table'])
            this.setState({
                selectHack: false
            })
        })

    }

    changeTable(value) {
        if (value) {
            this.getTableColumn(value);
        }
        this.submitForm();
    }

    submitForm = () => {
        const {
            form, handleTargetMapChange,
        } = this.props;

        setTimeout(() => {
            /**
             * targetMap
             */
            let values = form.getFieldsValue();
            const srcmap = assign(values, {
                src: this.getDataObjById(values.sourceId)
            });

            // 处理数据同步变量
            handleTargetMapChange(srcmap);
        }, 0);
    }

    validateChineseCharacter = (rule, value, callback) => {
        const reg = /(，|。|；)/; // 中文逗号，句号，分号
        if (reg.test(value)) {
            callback('参数中不可包含中文标点符号！')
        } else {
            callback();
        }
    }

    prev(cb) {
        cb.call(null, 0);
    }

    next(cb) {
        const { form } = this.props;

        form.validateFields((err, values) => {
            if (!err) {
                cb.call(null, 2);
            }
        })
    }
    handleCancel() {
        this.setState({
            textSql: "",
            visible: false
        })
    }
    createTable() {
        const { textSql } = this.state;
        const { targetMap } = this.props;
        this.setState({
            modalLoading: true
        })
        ajax.createDdlTable({ sql: textSql }).then((res) => {
            this.setState({
                modalLoading: false
            })
            if (res.code === 1) {
                this.getTableList(targetMap.sourceId)
                this.changeTable(res.data.tableName);
                this.props.form.setFieldsValue({ table: res.data.tableName })
                this.setState({
                    visible: false
                })
                message.success('表创建成功!')
            }
        })
    }
    showCreateModal() {
        const { sourceMap } = this.props;
        this.setState({
            loading: true
        })
        const tableName = typeof sourceMap.type.table == "string" ? sourceMap.type.table : sourceMap.type.table[0]
        ajax.getCreateTargetTable({
            originSourceId: sourceMap.sourceId,
            tableName: tableName,
            partition: sourceMap.type.partition,
        })
            .then(
                (res) => {
                    this.setState({
                        loading: false
                    })
                    if (res.code == 1) {
                        this.setState({
                            textSql: res.data,
                            sync: true,
                            visible: true
                        })
                    }
                }
            )
    }
    ddlChange = (newVal) => {
        this.setState({
            textSql: newVal,
            sync: false
        })
    }
    render() {
        const { getFieldDecorator } = this.props.form;
        const { modalLoading } = this.state;
        const {
            targetMap, dataSourceList,
            navtoStep, isCurrentTabNew
        } = this.props;
        const getPopupContainer = this.props.getPopupContainer;
        return <div className="g-step2">
            <Modal className="m-codemodal"
                title={(
                    <span>建表语句</span>
                )}
                confirmLoading={modalLoading}
                maskClosable={false}
                style={{ height: 424 }}
                visible={this.state.visible}
                onCancel={this.handleCancel.bind(this)}
                onOk={this.createTable.bind(this)}
            >
                <Editor language="dtsql" value={this.state.textSql} sync={this.state.sync} placeholder={DDL_ide_placeholder} onChange={this.ddlChange} />
            </Modal>
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
                            getPopupContainer={getPopupContainer}
                            showSearch
                            onChange={this.changeSource.bind(this)}
                            optionFilterProp="name"
                        >
                            {dataSourceList.map(src => {
                                let title = `${src.dataName}（${DATA_SOURCE_TEXT[src.type]}）`;

                                const disableSelect = src.type === DATA_SOURCE.ES ||
                                    src.type === DATA_SOURCE.REDIS ||
                                    src.type === DATA_SOURCE.MONGODB;

                                return <Option
                                    key={src.id}
                                    name={src.dataName}
                                    value={`${src.id}`}
                                    disabled={disableSelect}>
                                    {title}
                                </Option>
                            })}
                        </Select>
                    )}
                </FormItem>
                {this.renderDynamicForm()}
            </Form>
            {!this.props.readonly && <div className="steps-action">
                <Button style={{ marginRight: 8 }} onClick={() => this.prev(navtoStep)}>上一步</Button>
                <Button type="primary" onClick={() => this.next(navtoStep)}>下一步</Button>
            </div>}
        </div>
    }

    debounceTableSearch = debounce(this.changeTable, 600, { 'maxWait': 2000 })

    renderDynamicForm() {
        const { getFieldDecorator } = this.props.form;
        const { selectHack, loading } = this.state

        const { targetMap, dataSourceList, isCurrentTabNew, project, sourceMap } = this.props;
        const sourceType = sourceMap.type && sourceMap.type.type;
        let formItem;
        const getPopupContainer = this.props.getPopupContainer;
        const showCreateTable = (
            sourceType == DATA_SOURCE.MYSQL || sourceType == DATA_SOURCE.ORACLE
            || sourceType == DATA_SOURCE.SQLSERVER || sourceType == DATA_SOURCE.POSTGRESQL
            || sourceType == DATA_SOURCE.MYSQL || sourceType == DATA_SOURCE.MYSQL
            || sourceType == DATA_SOURCE.HIVE || sourceType == DATA_SOURCE.MAXCOMPUTE
        );

        if (isEmpty(targetMap)) return null;

        switch (targetMap.type.type) {
            case DATA_SOURCE.DB2:
            case DATA_SOURCE.MYSQL:
            case DATA_SOURCE.ORACLE:
            case DATA_SOURCE.SQLSERVER:
            case DATA_SOURCE.POSTGRESQL: {
                formItem = [
                    !selectHack && <FormItem
                        {...formItemLayout}
                        label="表名"
                        key="table"
                    >
                        {getFieldDecorator('table', {
                            rules: [{
                                required: true,
                                message: '请选择表',
                            }],
                            initialValue: isEmpty(targetMap) ? '' : targetMap.type.table
                        })(
                            <Select
                                getPopupContainer={getPopupContainer}
                                showSearch
                                mode="combobox"
                                // disabled={ !isCurrentTabNew }
                                optionFilterProp="value"
                                onChange={this.debounceTableSearch.bind(this)}
                            >
                                {this.state.tableList.map(table => {
                                    return <Option
                                        key={`rdb-target-${table}`}
                                        value={table}>
                                        {table}
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
                                onChange={this.submitForm.bind(this)}
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
                                onChange={this.submitForm.bind(this)}
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
                            initialValue: targetMap.type && targetMap.type.writeMode ? targetMap.type.writeMode : 'insert'
                        })(
                            <RadioGroup onChange={this.submitForm.bind(this)}>
                                <Radio value="insert" style={{ float: 'left' }}>
                                    视为脏数据，保留原有数据（Insert Into）
                            </Radio>
                                <Radio value="replace" style={{ float: 'left' }}>
                                    替换原有数据（Replace Into）
                            </Radio>
                            </RadioGroup>
                        )}
                    </FormItem>
                ];
                break;
            }
            case DATA_SOURCE.ANALYSIS: {
                formItem = [
                    !selectHack && <FormItem
                        {...formItemLayout}
                        label="表名"
                        key="table"
                    >
                        {getFieldDecorator('table', {
                            rules: [{
                                required: true,
                                message: '请选择表',
                            }],
                            initialValue: isEmpty(targetMap) ? '' : targetMap.type.table
                        })(
                            <Select
                                getPopupContainer={getPopupContainer}
                                showSearch
                                mode="combobox"
                                // disabled={ !isCurrentTabNew }
                                optionFilterProp="value"
                                onChange={this.debounceTableSearch.bind(this)}
                            >
                                {this.state.tableList.map(table => {
                                    return <Option
                                        key={`rdb-target-${table}`}
                                        value={table}>
                                        {table}
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
                                onChange={this.submitForm.bind(this)}
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
                                onChange={this.submitForm.bind(this)}
                                placeholder="请输入导入数据后执行的SQL脚本"
                                type="textarea"
                            ></Input>
                        )}
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
            }
            case DATA_SOURCE.HIVE:
            case DATA_SOURCE.MAXCOMPUTE: {
                formItem = [
                    !selectHack && <FormItem
                        {...formItemLayout}
                        label="表名"
                        key="table"
                    >
                        {getFieldDecorator('table', {
                            rules: [{
                                required: true,
                                message: '请选择表',
                            }],
                            initialValue: isEmpty(targetMap) ? '' : targetMap.type.table
                        })(
                            <Select
                                getPopupContainer={getPopupContainer}
                                showSearch
                                mode="combobox"
                                onChange={this.debounceTableSearch.bind(this)}
                                optionFilterProp="value"
                            >
                                {this.state.tableList.map(table => {
                                    return <Option
                                        key={`rdb-target-${table}`}
                                        value={table}
                                    >
                                        {table}
                                    </Option>
                                })}
                            </Select>
                        )}
                        {showCreateTable && (loading ? <Icon type="loading" /> : <a
                            style={{ top: "0px", right: "-90px" }}
                            onClick={this.showCreateModal.bind(this)}
                            className="help-doc" >一键生成目标表</a>)}
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
                                placeholder="pt=${bdp.system.bizdate}"
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
            }
            case DATA_SOURCE.HDFS: {
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
                            initialValue: targetMap.type && targetMap.type.fileType ? targetMap.type.fileType : 'orc',
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
                            rules: [{
                                validator: this.validateChineseCharacter
                            }],
                            initialValue: isEmpty(targetMap) ? ',' : targetMap.type.fieldDelimiter
                        })(
                            <Input
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
                        key="writeMode"
                    >
                        {getFieldDecorator('writeMode', {
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
            case DATA_SOURCE.HBASE: {
                formItem = [
                    !selectHack && <FormItem
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
                                getPopupContainer={getPopupContainer}
                                showSearch
                                mode="combobox"
                                onChange={this.debounceTableSearch.bind(this)}
                                // disabled={!isCurrentTabNew}
                                optionFilterProp="value"
                            >
                                {this.state.tableList.map(table => {
                                    return <Option key={`hbase-target-${table}`} value={table}>
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
                            initialValue: targetMap.type && targetMap.type.encoding ? targetMap.type.encoding : 'utf-8'
                        })(
                            <Select getPopupContainer={getPopupContainer} onChange={this.submitForm.bind(this)}>
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
                            <RadioGroup onChange={this.submitForm.bind(this)}>
                                <Radio value="skip" style={{ float: 'left' }}>
                                    SKIP
                            </Radio>
                                <Radio value="empty" style={{ float: 'left' }}>
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
                                onChange={this.submitForm.bind(this)}
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
                            }, {
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
                                onChange={this.submitForm.bind(this)} />
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
                            <Select getPopupContainer={getPopupContainer} onChange={this.submitForm.bind(this)}>
                                <Option value="utf-8">utf-8</Option>
                                <Option value="gbk">gbk</Option>
                            </Select>
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="列分隔符"
                        key="fieldDelimiter"
                    >
                        {getFieldDecorator('fieldDelimiter', {
                            rules: [{
                                validator: this.validateChineseCharacter
                            }],
                            initialValue: targetMap.type&&typeof targetMap.type.fieldDelimiter !="undefined"?targetMap.type.fieldDelimiter:","
                        })(
                            <Input
                                placeholder="若不填写，则默认,"
                                onChange={this.submitForm.bind(this)} />
                        )}
                        <HelpDoc doc="splitCharacter" />
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
                ]
                break;
            }
            default: break;
        }

        return formItem;
    }
}

const TargetFormWrap = Form.create()(TargetForm);

class Target extends React.Component {
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
        project: state.project
    };
};

const mapDispatch = (dispatch, ownProps) => {
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
        },
    };
}

export default connect(mapState, mapDispatch)(Target);