import * as React from 'react';
import { connect } from 'react-redux';
import { Form, Input, Select, Button, Radio, Modal, Icon, message } from 'antd';
import { isEmpty, debounce, get } from 'lodash';
import assign from 'object-assign';

import utils from 'utils';
import { singletonNotification, filterValueOption } from 'funcs';
import Editor from 'widgets/editor';
import { getProjectTableTypes } from '../../../../store/modules/tableType';
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

import { formJsonValidator } from '../../../../comm'

import HelpDoc from '../../../helpDoc';

import { DDL_IDE_PLACEHOLDER } from '../../../../comm/DDLCommon';

const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;
const TextArea = Input.TextArea;

class TargetForm extends React.Component<any, any> {
    constructor (props: any) {
        super(props);

        this.state = {
            tableList: [],
            visible: false,
            modalLoading: false,
            tablePartitionList: [], // 表分区列表
            loading: false // 请求
        };
    }

    componentDidMount () {
        const { targetMap, getProjectTableTypes, project } = this.props;
        const { sourceId, type } = targetMap;
        const projectId = project && project.id;
        if (projectId) {
            getProjectTableTypes(projectId);
        }
        sourceId && this.getTableList(sourceId);
        if (type) {
            this.checkIsNativeHive(type.tableName);
            // 如果是已经添加过分区信息，则加载其分区列表信息
            if (type.partition) {
                this.getHivePartitions(type.table);
            }
        }
    }

    getTableList = (sourceId: any) => {
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
            }).then((res: any) => {
                if (res.code === 1) {
                    ctx.setState({
                        tableList: res.data
                    });
                }
            });
        })
    }

    /**
     * 根据数据源id获取数据源信息
     * @param {*} id
     */
    getDataObjById (id: any) {
        const { dataSourceList } = this.props;
        return dataSourceList.filter((src: any) => {
            return src.id == id;
        })[0];
    }

    changeSource (value: any) {
        const { handleSourceChange } = this.props;
        setTimeout(() => {
            this.getTableList(value);
        }, 0);
        handleSourceChange(this.getDataObjById(value));
        this.resetTable();
    }

    resetTable () {
        const { form } = this.props;
        this.changeTable('');
        // 这边先隐藏结点，然后再reset，再显示。不然会有一个组件自带bug。
        this.setState({
            selectHack: true
        }, () => {
            form.resetFields(['table'])
            this.setState({
                selectHack: false
            })
        })
    }

    getHivePartitions = (tableName: any) => {
        const {
            targetMap, handleTargetMapChange
        } = this.props;

        const { sourceId, type } = targetMap;
        // TODO 这里获取 Hive 分区的条件有点模糊
        if (type && (
            type.type === DATA_SOURCE.HIVE_1 ||
            type.type === DATA_SOURCE.HIVE_2 ||
            type.type === DATA_SOURCE.CARBONDATA
        )) {
            ajax.getHivePartitions({
                sourceId: sourceId,
                tableName
            }).then((res: any) => {
                this.setState({
                    tablePartitionList: res.data || []
                });
                const havePartition = res.data && res.data.length > 0;
                handleTargetMapChange({ havePartition });
            });
        }
    }

    changeTable (value: any) {
        if (value) {
            // Reset partition
            this.props.form.setFieldsValue({ partition: '' });
            // 获取表列字段
            this.getTableColumn(value);
            // 检测是否有 native hive
            this.checkIsNativeHive(value);
            // 获取 Hive 分区字段
            this.getHivePartitions(value);
        }
        this.submitForm();
    }

    getTableColumn = (tableName: any) => {
        const { form, handleTableColumnChange, targetMap } = this.props;
        const sourceId = form.getFieldValue('sourceId');

        this.setState({
            loading: true
        })
        // 排除条件
        if (targetMap.type && targetMap.type.type === DATA_SOURCE.HBASE) {
            this.setState({
                loading: false
            })
            return true;
        }

        ajax.getOfflineTableColumn({
            sourceId,
            tableName
        }).then((res: any) => {
            this.setState({
                loading: false
            })
            if (res.code === 1) {
                handleTableColumnChange(res.data);
            } else {
                handleTableColumnChange([]);
            }
        })
    }

    checkIsNativeHive (tableName: any) {
        const { form, targetMap, changeNativeHive } = this.props;
        const sourceId = form.getFieldValue('sourceId');
        if (!tableName || !sourceId) {
            return;
        }
        if (targetMap.type && targetMap.type.type === DATA_SOURCE.CARBONDATA) {
            this.setState({
                loading: true
            })
            ajax.isNativeHive({
                sourceId,
                tableName
            }).then((res: any) => {
                this.setState({
                    loading: false
                })
                if (res.code == 1) {
                    const isNativeHive = res.data;
                    changeNativeHive(isNativeHive);
                }
            })
        }
    }

    submitForm = () => {
        const {
            form, updateTabAsUnSave, handleTargetMapChange
        } = this.props;

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
                let values: any = {};
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
                values.partition = utils.removeAllSpaces(values.partition);
            }
            if (values.path) {
                values.path = utils.removeAllSpaces(values.path);
            }
            if (values.fileName) {
                values.fileName = utils.removeAllSpaces(values.fileName);
            }
            const srcmap = assign(values, {
                src: this.getDataObjById(values.sourceId)
            });

            // 处理数据同步变量
            handleTargetMapChange(srcmap);
            updateTabAsUnSave();
        }, 0);
    }

    validateChineseCharacter = (data: any) => {
        const reg = /(，|。|；|[\u4e00-\u9fa5]+)/; // 中文字符，中文逗号，句号，分号
        let has = false;
        let fieldsName: any = [];
        if (data.path && reg.test(data.path)) {
            has = true;
            fieldsName.push('路径');
        }
        if (data.fileName && reg.test(data.fileName)) {
            has = true;
            fieldsName.push('文件名');
        }
        if (data.fieldDelimiter && reg.test(data.fieldDelimiter)) {
            has = true;
            fieldsName.push('列分隔符');
        }
        if (has) {
            singletonNotification('提示', `${fieldsName.join('、')}参数中有包含中文或者中文标点符号！`, 'warning')
        }
    }

    prev (cb: any) {
        /* eslint-disable-next-line */
        cb.call(null, 0);
    }

    next (cb: any) {
        const { form } = this.props;
        form.validateFields((err: any, values: any) => {
            if (!err) {
                this.validateChineseCharacter(values);
                /* eslint-disable-next-line */
                cb.call(null, 2);
            }
        })
    }
    handleCancel () {
        this.setState({
            textSql: '',
            visible: false
        })
    }
    createTable () {
        const { textSql } = this.state;
        const { targetMap, form } = this.props;
        const tableType = form.getFieldValue('tableType')
        this.setState({
            modalLoading: true
        })
        ajax.createDdlTable({ sql: textSql, sourceId: targetMap.sourceId, tableType }).then((res: any) => {
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
    showCreateModal = () => {
        const { sourceMap, targetMap } = this.props;
        this.setState({
            loading: true
        })
        const tableName = typeof sourceMap.type.table == 'string' ? sourceMap.type.table : sourceMap.type.table[0]
        ajax.getCreateTargetTable({
            originSourceId: sourceMap.sourceId,
            tableName: tableName,
            partition: sourceMap.type.partition,
            targetSourceId: targetMap.sourceId
        })
            .then(
                (res: any) => {
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
    ddlChange = (newVal: any) => {
        this.setState({
            textSql: newVal,
            sync: false
        })
    }
    render () {
        const { getFieldDecorator } = this.props.form;
        const { modalLoading } = this.state;
        const {
            targetMap, dataSourceList, navtoStep, isIncrementMode
        } = this.props;
        const getPopupContainer = this.props.getPopupContainer;
        const dataSourceListFltKylin = dataSourceList && dataSourceList.filter((src: any) => src.type !== DATA_SOURCE.KYLIN);
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
                <Editor language="dtsql" value={this.state.textSql} sync={this.state.sync} placeholder={DDL_IDE_PLACEHOLDER} onChange={this.ddlChange} />
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
                            {dataSourceListFltKylin.map((src: any) => {
                                let title = `${src.dataName}（${(DATA_SOURCE_TEXT as any)[src.type]}）`;

                                /**
                                 * 禁用ES, REDIS, MONGODB,
                                 * 增量模式禁用非 HIVE, HDFS数据源
                                 */
                                const disableSelect = src.type === DATA_SOURCE.ES ||
                                    src.type === DATA_SOURCE.REDIS ||
                                    src.type === DATA_SOURCE.MONGODB ||
                                    (isIncrementMode && (
                                        src.type !== DATA_SOURCE.HIVE_1 &&
                                        src.type !== DATA_SOURCE.HIVE_2 &&
                                        src.type !== DATA_SOURCE.HDFS
                                    ))

                                return <Option
                                    key={src.id}
                                    {...{ name: src.dataName }}
                                    value={`${src.id}`}
                                    disabled={disableSelect}>
                                    {title}
                                </Option>
                            })}
                        </Select>
                    )}
                </FormItem>
                {this.renderDynamicForm()}
                {!isEmpty(targetMap) ? (
                    <FormItem
                        {...formItemLayout}
                        label="高级配置"
                    >
                        {getFieldDecorator('extralConfig', {
                            rules: [{
                                validator: formJsonValidator
                            }],
                            initialValue: get(targetMap, 'extralConfig', '')
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
            {!this.props.readonly && <div className="steps-action">
                <Button style={{ marginRight: 8 }} onClick={() => this.prev(navtoStep)}>上一步</Button>
                <Button
                    type="primary"
                    onClick={() => this.next(navtoStep)}
                    loading={this.state.loading}
                >
                    下一步
                </Button>
            </div>}
        </div>
    }

    debounceTableSearch = debounce(this.changeTable, 600, { 'maxWait': 2000 })

    renderDynamicForm = () => {
        const { getFieldDecorator } = this.props.form;
        const { selectHack, loading } = this.state;

        const { targetMap, sourceMap } = this.props;
        const sourceType = sourceMap.type && sourceMap.type.type;
        const { isNativeHive } = targetMap;
        // 是否拥有分区
        const havePartition = targetMap.type && (!!targetMap.type.partition || targetMap.type.havePartition);
        const isClickHouse = sourceType === DATA_SOURCE.CLICK_HOUSE;
        let formItem: any;
        const getPopupContainer = this.props.getPopupContainer;
        const showCreateTable = (
            sourceType == DATA_SOURCE.MYSQL ||
            sourceType == DATA_SOURCE.POLAR_DB ||
            sourceType == DATA_SOURCE.ORACLE ||
            sourceType == DATA_SOURCE.SQLSERVER ||
            sourceType == DATA_SOURCE.POSTGRESQL ||
            sourceType == DATA_SOURCE.LIBRASQL ||
            sourceType == DATA_SOURCE.DB2 ||
            sourceType == DATA_SOURCE.CLICK_HOUSE ||
            sourceType == DATA_SOURCE.HIVE_2 ||
            sourceType == DATA_SOURCE.HIVE_1 ||
            sourceType == DATA_SOURCE.MAXCOMPUTE
        );

        if (isEmpty(targetMap)) return null;
        switch (targetMap.type.type) {
            case DATA_SOURCE.GBASE:
            case DATA_SOURCE.DB2:
            case DATA_SOURCE.MYSQL:
            case DATA_SOURCE.POLAR_DB:
            case DATA_SOURCE.ORACLE:
            case DATA_SOURCE.CLICK_HOUSE:
            case DATA_SOURCE.SQLSERVER:
            case DATA_SOURCE.POSTGRESQL: {
                let writeModeOptions = [
                    <Option key="writeModeInsert" value="insert">insert into（当主键/约束冲突，报脏数据）</Option>
                ];
                if (!isClickHouse) {
                    writeModeOptions = writeModeOptions.concat([
                        <Option key="writeModeReplace" value="replace">replace into（当主键/约束冲突，先delete再insert，未映射的字段会被映射为NULL）</Option>,
                        <Option key="writeModeUpdate" value="update">on duplicate key update（当主键/约束冲突，update数据，未映射的字段值不变）</Option>
                    ])
                }
                formItem = [
                    !selectHack && <FormItem
                        {...formItemLayout}
                        label="表名"
                        key="table"
                    >
                        {getFieldDecorator('table', {
                            rules: [{
                                required: true,
                                message: '请选择表'
                            }],
                            initialValue: isEmpty(targetMap) ? '' : targetMap.type.table
                        })(
                            <Select
                                getPopupContainer={getPopupContainer}
                                showSearch
                                mode="combobox"
                                optionFilterProp="value"
                                filterOption={filterValueOption}
                                onChange={this.debounceTableSearch.bind(this)}
                            >
                                {this.state.tableList.map((table: any) => {
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
                        key="writeMode-mysql"
                        className="txt-left"
                    >
                        {getFieldDecorator('writeMode@mysql', {
                            rules: [{
                                required: true
                            }],
                            initialValue: targetMap.type && targetMap.type.writeMode ? targetMap.type.writeMode : 'insert'
                        })(
                            <Select onChange={this.submitForm.bind(this)}>
                                { writeModeOptions }
                            </Select>
                        )}
                    </FormItem>
                ];
                break;
            }
            case DATA_SOURCE.CARBONDATA: {
                formItem = [
                    !selectHack && <FormItem
                        {...formItemLayout}
                        label="表名"
                        key="table"
                    >
                        {getFieldDecorator('table', {
                            rules: [{
                                required: true,
                                message: '请选择表'
                            }],
                            initialValue: isEmpty(targetMap) ? '' : targetMap.type.table
                        })(
                            <Select
                                getPopupContainer={getPopupContainer}
                                showSearch
                                mode="combobox"
                                optionFilterProp="value"
                                filterOption={filterValueOption}
                                onChange={this.debounceTableSearch.bind(this)}
                            >
                                {this.state.tableList.map((table: any) => {
                                    return <Option
                                        key={`rdb-target-${table}`}
                                        value={table}>
                                        {table}
                                    </Option>
                                })}
                            </Select>
                        )}
                    </FormItem>,
                    isNativeHive && havePartition ? <FormItem
                        {...formItemLayout}
                        label="分区"
                        key="partition"
                    >
                        {getFieldDecorator('partition', {
                            rules: [{
                                required: true,
                                message: '目标分区为必填项！'
                            }],
                            initialValue: get(targetMap, 'type.partition', '')
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
                    </FormItem> : null,
                    <FormItem
                        {...formItemLayout}
                        label="写入模式"
                        key="writeMode-carbondata"
                        className="txt-left"
                    >
                        {getFieldDecorator('writeMode@carbondata', {
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
            case DATA_SOURCE.KUDU: {
                formItem = [
                    !selectHack && <FormItem
                        {...formItemLayout}
                        label="表名"
                        key="table"
                    >
                        {getFieldDecorator('table', {
                            rules: [{
                                required: true,
                                message: '请选择表'
                            }],
                            initialValue: isEmpty(targetMap) ? '' : targetMap.type.table
                        })(
                            <Select
                                getPopupContainer={getPopupContainer}
                                showSearch
                                mode="combobox"
                                optionFilterProp="value"
                                filterOption={filterValueOption}
                                onChange={this.debounceTableSearch.bind(this)}
                            >
                                {this.state.tableList.map((table: any) => {
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
                        label="写入模式"
                        key="writeMode-kudu"
                        className="txt-left"
                    >
                        {getFieldDecorator('writeMode@kudu', {
                            rules: [{
                                required: true
                            }],
                            initialValue: targetMap.type && targetMap.type.writeMode ? targetMap.type.writeMode : 'replace'
                        })(
                            <RadioGroup onChange={this.submitForm.bind(this)}>
                                <Radio value="insert" style={{ float: 'left' }}>
                                    insert
                                </Radio>
                                <Radio value="update" style={{ float: 'left' }}>
                                    update
                                </Radio>
                                <Radio value="upsert" style={{ float: 'left' }}>
                                    upsert
                                </Radio>
                            </RadioGroup>
                        )}
                    </FormItem>
                ];
                break;
            }
            case DATA_SOURCE.HIVE_1:
            case DATA_SOURCE.HIVE_2:
            case DATA_SOURCE.LIBRASQL:
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
                                message: '请选择表'
                            }],
                            initialValue: isEmpty(targetMap) ? '' : targetMap.type.table
                        })(
                            <Select
                                getPopupContainer={getPopupContainer}
                                showSearch
                                mode="combobox"
                                filterOption={filterValueOption}
                                onChange={this.debounceTableSearch.bind(this)}
                                optionFilterProp="value"
                            >
                                {this.state.tableList.map((table: any) => {
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
                            style={{ top: '0px', right: '-90px' }}
                            onClick={this.showCreateModal.bind(this)}
                            className="help-doc" >一键生成目标表</a>)}
                    </FormItem>,
                    havePartition ? <FormItem
                        {...formItemLayout}
                        label="分区"
                        key="partition"
                    >
                        {getFieldDecorator('partition', {
                            rules: [{
                                required: true,
                                message: '目标分区为必填项！'
                            }],
                            initialValue: get(targetMap, 'type.partition', '')
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
                                    this.state.tablePartitionList.map((pt: any) => {
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
                    </FormItem> : '',
                    <FormItem
                        {...formItemLayout}
                        label="写入模式"
                        key="writeMode-hive"
                        className="txt-left"
                    >
                        {getFieldDecorator('writeMode@hive', {
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
                                /* eslint-disable */
                                placeholder="例如: 目标为hive则 分隔符为\001"
                                /* eslint-disable */
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
                                filterOption={filterValueOption}
                            >
                                {this.state.tableList.map((table: any) => {
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
                                {...{ min: 0 }}
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
                                message: '路径不得为空！'
                            }, {
                                max: 200,
                                message: '路径不得超过200个字符！'
                            }, {
                                // validator: this.validatePath
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
                            rules: [],
                            initialValue: targetMap.type && typeof targetMap.type.fieldDelimiter != 'undefined' ? targetMap.type.fieldDelimiter : ','
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
                        key="writeMode-ftp"
                    >
                        {getFieldDecorator('writeMode@ftp', {
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

const TargetFormWrap = Form.create<any>()(TargetForm);

class Target extends React.Component<any, any> {
    constructor(props: any) {
        super(props);
    }

    render () {
        return <div>
            <TargetFormWrap {...this.props} />
        </div>
    }
}

const mapState = (state: any) => {
    const { workbench, dataSync } = state.offlineTask;
    const { isCurrentTabNew, currentTab } = workbench;
    return {
        currentTab,
        isCurrentTabNew,
        project: state.project,
        sourceMap: dataSync.sourceMap,
        targetMap: dataSync.targetMap,
        dataSourceList: dataSync.dataSourceList,
        projectTableTypes: state.tableTypes.projectTableTypes
    };
};

const mapDispatch = (dispatch: any, ownProps: any) => {
    return {
        handleSourceChange(src: any) {
            dispatch({
                type: dataSyncAction.RESET_TARGET_MAP
            });
            dispatch({
                type: dataSyncAction.RESET_KEYMAP
            });
            dispatch({
                type: targetMapAction.DATA_SOURCE_TARGET_CHANGE,
                payload: src
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },

        handleTargetMapChange(srcmap: any) {
            dispatch({
                type: targetMapAction.DATA_TARGETMAP_CHANGE,
                payload: srcmap
            });
        },
        updateTabAsUnSave () {
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },
        handleTableColumnChange: (colData: any) => {
            dispatch({
                type: dataSyncAction.RESET_KEYMAP
            });
            dispatch({
                type: targetMapAction.TARGET_TABLE_COLUMN_CHANGE,
                payload: colData
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },
        changeNativeHive: (isNativeHive: any) => {
            dispatch({
                type: targetMapAction.CHANGE_NATIVE_HIVE,
                payload: isNativeHive
            });
        },
        updateTaskFields(params: any) {
            dispatch({
                type: workbenchAction.SET_TASK_FIELDS_VALUE,
                payload: params
            });
        },
        getProjectTableTypes: (projectId: any) => {
            dispatch(getProjectTableTypes(projectId))
        }
    };
}

export default connect(mapState, mapDispatch)(Target);
