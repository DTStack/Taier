import * as React from 'react';
import { connect } from 'react-redux';
import {
    Steps, Button, message, Form, Input,
    Row, Col, Icon, Select, Radio, Tooltip, InputNumber
} from 'antd';
import assign from 'object-assign';
import { range, isObject } from 'lodash';
import { browserHistory, hashHistory } from 'react-router'
import EngineSelect from '../../components/engineSelect';
import { getProjectTableTypes } from '../../store/modules/tableType';
import ajax from '../../api/dataManage';
import { formItemLayout, TABLE_TYPE } from '../../comm/const';
import CatalogueTree from './catalogTree';
import LifeCycle from './lifeCycle';

const Step = Steps.Step;
const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;

@(connect((state: any) as any) => {
    return {
        project: state.project,
        projectTableTypes: state.tableTypes.projectTableTypes
    }
}, (dispatch: any) => {
    return {
        getProjectTableTypes: (projectId: any) => {
            dispatch(getProjectTableTypes(projectId))
        }
    }
})

/**
 * @description step1:基本信息
 * @class BaseForm
 * @extends {React.Component}
 */
class BaseForm extends React.Component<any, any> {
    constructor(props: any) {
        super(props);

        this.state = {
            type: props.location ? '2' : '1', // 1: 内部表 2:外部表
            dataCatalogue: [],
            storedType: props.storedType,
            tableType: props.tableType
        };
    }

    componentDidMount () {
        const { getProjectTableTypes, project } = this.props;
        const projectId = project && project.id;
        if (projectId) {
            getProjectTableTypes(projectId);
        }
        this.loadCatalogue();
    }

    loadCatalogue = () => {
        ajax.getDataCatalogues().then((res: any) => {
            this.setState({
                dataCatalogue: res.data && [res.data]
            })
        })
    }

    lifeCycleChange = (value: any) => {
        this.props.form.setFieldsValue({ 'lifeDay': value })
    }

    render () {
        const { getFieldDecorator, getFieldValue } = this.props.form;
        const { tableName, desc, delim, location, lifeDay, catalogueId, projectTableTypes } = this.props;
        const { type, dataCatalogue, storedType, tableType } = this.state;
        const isShowDelim = storedType == 'textfile';
        const isHiveTable = getFieldValue('tableType') == TABLE_TYPE.HIVE;
        return <Form>
            <FormItem
                {...formItemLayout}
                label="表类型"
            >
                {getFieldDecorator('tableType', {
                    rules: [{
                        required: true,
                        message: '表类型不可为空！'
                    }],
                    initialValue: tableType
                })(
                    <EngineSelect
                        allowClear
                        placeholder="表类型"
                        tableTypes={projectTableTypes}
                        onChange={(value: any) => {
                            this.setState({
                                tableType: value
                            })
                        }}
                    />
                )}
            </FormItem>
            <FormItem
                {...formItemLayout}
                label="表名"
                hasFeedback
            >
                {getFieldDecorator('tableName', {
                    rules: [{
                        required: true, message: '表名不可为空！'
                    }, {
                        pattern: /^([A-Za-z0-9_]{1,64})$/,
                        message: '表名称只能由字母、数字、下划线组成，且长度不超过64个字符!'
                    }, {
                        validator: this.validateTableName.bind(this)
                    }],
                    validateTrigger: 'onBlur',
                    initialValue: tableName
                })(
                    <Input placeholder="请输入表名" autoComplete="off" />
                )}
            </FormItem>
            {
                isHiveTable && (
                    <>
                        <FormItem
                            {...formItemLayout}
                            label="类型"
                            hasFeedback
                        >
                            <RadioGroup value={this.state.type}
                                onChange={this.handleChange.bind(this)}
                            >
                                <Radio value={'1'}>内部表</Radio>
                                <Radio value={'2'}>外部表</Radio>
                            </RadioGroup>
                        </FormItem>
                        {type == 2 && <FormItem
                            {...formItemLayout}
                            label="外部表地址"
                            hasFeedback
                        >
                            {getFieldDecorator('location', {
                                rules: [{
                                    required: true,
                                    message: '外部表地址不可为空！'
                                }, {
                                    validator: this.validateLoc.bind(this)
                                }],
                                initialValue: location,
                                validateTrigger: 'onBlur'
                            })(
                                <Input placehoder="外部表地址" />
                            )}
                        </FormItem>}
                    </>
                )
            }
            <FormItem
                {...formItemLayout}
                label="所属类目"
            >
                {getFieldDecorator('catalogueId', {
                    rules: [{
                        required: true,
                        message: '表所在类目不可为空！'
                    }],
                    initialValue: catalogueId || undefined
                })(
                    <CatalogueTree
                        isPicker
                        isFolderPicker
                        placeholder="请选择类目"
                        treeData={dataCatalogue}
                    />
                )}
            </FormItem>
            {
                isHiveTable && (
                    <>
                        <FormItem
                            {...formItemLayout}
                            label="生命周期"
                        >
                            {getFieldDecorator('lifeDay', {
                                rules: [{
                                    required: true,
                                    message: '生命周期不可为空！'
                                }],
                                initialValue: lifeDay || 90
                            })(
                                <LifeCycle
                                    onChange={this.lifeCycleChange}
                                />
                            )}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="存储格式"
                            hasFeedback
                        >
                            {getFieldDecorator('storedType', {
                                rules: [{
                                    required: true, message: '存储格式不可为空！'
                                }],
                                initialValue: storedType
                            })(
                                <Select onChange={(value: any) => {
                                    this.setState({
                                        storedType: value
                                    })
                                }}>
                                    <Option value="textfile">textfile</Option>
                                    <Option value="orc">orc</Option>
                                    <Option value="parquet">parquet</Option>
                                </Select>
                            )}
                        </FormItem>
                    </>
                )
            }
            {isShowDelim && (
                <FormItem
                    {...formItemLayout}
                    label="分隔符"
                    hasFeedback
                >
                    {getFieldDecorator('delim', {
                        rules: [],
                        initialValue: delim
                    })(
                        <Input placeholder="分隔符" autoComplete="off" />
                    )}
                </FormItem>
            )}
            <FormItem
                {...formItemLayout}
                label="描述"
            >
                {getFieldDecorator('tableDesc', {
                    rules: [{
                        max: 200,
                        message: '描述不得超过200个字符！'
                    }],
                    initialValue: desc
                })(
                    <Input type="textarea" placeholder="描述信息" />
                )}
            </FormItem>
        </Form>
    }

    validateDelim (rule: any, value: any, callback: any) {
        value = value.trim();
        let error: any;
        if (value[0] === '\\') {
            if (value.length > 2) {
                error = '分隔符长度只能为1（不包括转义字符"\\"）'
                callback(error)
            }
        } else {
            if (value.length > 1) {
                error = '分隔符长度只能为1'
                callback(error)
            }
        }
        callback();
    }

    validateTableName (rule: any, value: any, callback: any) {
        const ctx = this;
        const tableType = ctx.props.form.getFieldValue('tableType')
        if (tableType) {
            value ? ajax.checkTableExist({
                tableName: value,
                tableType
            }).then((res: any) => {
                if (res.code === 1) {
                    // 转换为小写
                    ctx.props.form.setFieldsValue({ tableName: value.toLowerCase() })
                    if (res.data) {
                        const error = '该表已经存在！'
                        callback(error);
                    }
                }
            })
                .then(callback) : callback();
        } else {
            message.error('请先选择表类型！')
        }
    }

    validateLoc (rule: any, value: any, callback: any) {
        value ? ajax.checkHdfsLocExist({
            hdfsUri: 'hdfs://' + value
        }).then((res: any) => {
            if (res.code === 1) {
                if (!res.data) {
                    const error = '此目录不存在'
                    callback(error);
                }
            }
        })
            .then(callback) : callback();
    }

    handleChange(e: any) {
        const type = e.target.value;

        this.setState({ type });
        type === '1' && this.props.resetLoc();
    }
}

/**
 * @description 字段/分区 一行
 * @class RowItem
 * @extends {React.Component}
 */
export class RowItem extends React.Component<any, any> {
    constructor(props: any) {
        super(props);
        this.state = {
            editMode: false
        };
    }

    /**
     * @description
     * @param {any} selectName evt为value类型时用于确定表单name，可选
     * @param {any} evt value/event
     * @memberof RowItem
     */
    handleChange (selectName: any, evt: any) {
        const { data, replaceRow } = this.props;
        let iptName, value;

        if (isObject(evt)) {
            iptName = evt.target.name;
            value = evt.target.value;
        } else {
            iptName = selectName;
            value = evt;
        }
        console.log('input-select', iptName, value);

        const newData = assign({}, data, { [iptName]: value });
        const TYPE = newData.columnType.toUpperCase();

        if (TYPE === 'DECIMAL' || TYPE === 'NUMERIC') {
            if (!newData.precision) newData.precision = 10;
            if (!newData.scale) newData.scale = 0;
        }
        if (TYPE === 'CHAR' || TYPE === 'CHARACTER' || TYPE === 'CHARACTER VARYING') {
            if (!newData.charLen) newData.charLen = 10;
        }
        if (TYPE === 'VARCHAR') {
            if (!newData.varcharLen) newData.varcharLen = 10;
        }

        if (this.checkParams(newData)) {
            replaceRow(newData);
        }
    }

    checkParams(params: any) {
        const reg = /^[A-Za-z0-9_]+$/;
        if (params.columnName) {
            if (!reg.test(params.columnName)) {
                message.error('字段名称只能由字母、数字、下划线组成！')
                return false;
            }
            if (params.columnName.length > 20) {
                message.error('字段名称不可超过20个字符！')
                return false;
            }
        }
        if (params.comment && params.comment.length > 100) {
            message.error('字段备注不可超过100个字符！')
            return false;
        }
        return true;
    }

    render () {
        const { data, tableType } = this.props;
        const { isSaved, isPartition, columnType, columnName, comment } = data;
        const isHiveTable = tableType == TABLE_TYPE.HIVE;
        const needExtra: any = ['DECIMAL', 'VARCHAR', 'CHAR', 'CHARACTER', 'CHARACTER VARYING', 'NUMERIC'].indexOf(columnType.toUpperCase()) !== -1;
        // const needExtra = true;
        const TYPES = isHiveTable
            ? (isPartition ? ['STRING', 'BIGINT'] : ['TINYINT', 'SMALLINT', 'INT', 'BIGINT', 'BOOLEAN',
                'FLOAT', 'DOUBLE', 'STRING', 'BINARY', 'TIMESTAMP',
                'DECIMAL', 'DATE', 'VARCHAR', 'CHAR'
            ])
            : ['SMALLINT', 'INTEGER', 'BIGINT', 'BOOLEAN', 'CHARACTER', 'CHARACTER VARYING',
                'TEXT', 'NUMERIC', 'REAL', 'DOUBLE PRECISION', 'TIMESTAMP', 'DATE'];

        return <Row className="row">
            <Col span={4} className="cell">
                <Input name="columnName" value={columnName}
                    autoComplete="off"
                    onChange={this.handleChange.bind(this, undefined)}
                    disabled={isSaved}
                />
            </Col>
            <Col span={8} className="cell">
                <Select name="columnType" defaultValue={columnType}
                    onChange={this.handleChange.bind(this, 'columnType')}
                    style={{ width: needExtra ? '40%' : '80%' }}
                    disabled={isSaved}
                >
                    {TYPES.map((str: any) => <Option key={str} value={str}>{str}</Option>)}
                </Select>
                {needExtra && this.renderExtra(columnType)}
            </Col>
            <Col span={7} className="cell">
                <Input
                    name="comment"
                    value={comment}
                    autoComplete="off"
                    onChange={this.handleChange.bind(this, undefined)}
                    disabled={isSaved}
                />
            </Col>
            <Col span={5} className="cell" style={{ paddingTop: 13 }}>
                <a href="javascript:void(0)"
                    disabled={isSaved}
                    onClick={() => this.props.moveRow(data.uuid, true)}
                >上移</a>
                <span> | </span>
                <a href="javascript:void(0)"
                    disabled={isSaved}
                    onClick={() => this.props.moveRow(data.uuid, false)}
                >下移</a>
                <span> | </span>
                <a href="javascript:void(0)"
                    disabled={isSaved}
                    onClick={() => this.props.delRow(data.uuid)}
                >删除</a>
            </Col>
        </Row>
    }

    renderExtra(columnType: any) {
        const { data } = this.props;
        const { precision, scale, charLen, varcharLen, isSaved } = data;
        let result = '';

        columnType = columnType.toUpperCase();
        switch (columnType) {
            case 'DECIMAL':
            case 'NUMERIC':
                result = <span className="extra-ipt">
                    <Select name="precision"
                        style={{ marginLeft: '2%', width: '18%' }}
                        value={`${precision}` || '10'}
                        onChange={this.handleChange.bind(this, 'precision')}
                        placeholder="precision"
                        disabled={isSaved}
                    >
                        {range(39).slice(1).map((n: any) => <Option value={`${n}`}
                            key={n}
                        >{n}</Option>)}
                    </Select>
                    <Select name="scale"
                        style={{ marginLeft: '2%', width: '18%' }}
                        value={`${scale}` || '0'}
                        onChange={this.handleChange.bind(this, 'scale')}
                        placeholder="scale"
                        disabled={isSaved}
                    >
                        {range(precision || 10).map((n1: any) => <Option value={`${n1}`}
                            key={n1}
                        >{n1}</Option>)}
                    </Select>
                    <Tooltip title="type(precision,scale)；precision:数字总长度，最大为38；scale：小数点之后的位数">
                        <Icon type="question-circle-o" style={{ marginLeft: '2%' }} />
                    </Tooltip>
                </span>
                break;
            case 'CHAR':
            case 'CHARACTER':
            case 'CHARACTER VARYING':
                result = <span className="extra-ipt">
                    <InputNumber name="charLen" defaultValue={charLen || 10}
                        min={1}
                        max={255}
                        style={{ width: '38%', marginLeft: '2%' }}
                        onChange={this.handleChange.bind(this, 'charLen')}
                        disabled={isSaved}
                    />
                    <Tooltip title="type(char)；char的长度为1~255">
                        <Icon type="question-circle-o" style={{ marginLeft: '2%' }} />
                    </Tooltip>
                </span>
                break;
            case 'VARCHAR':
                result = <span className="extra-ipt">
                    <InputNumber name="varcharLen" defaultValue={varcharLen || 10}
                        min={1}
                        max={65535}
                        style={{ width: '38%', marginLeft: '2%' }}
                        onChange={this.handleChange.bind(this, 'varcharLen')}
                        disabled={isSaved}
                    />
                    <Tooltip title="type(varchar)；varchar的长度为1~65535">
                        <Icon type="question-circle-o" style={{ marginLeft: '2%' }} />
                    </Tooltip>
                </span>
                break;

            default: break;
        }

        return result;
    }
}

/**
 * @description step2:字段与分区
 * @export
 * @class ColumnsPartition
 * @extends {React.Component}
 */
export class ColumnsPartition extends React.Component<any, any> {
    constructor(props: any) {
        super(props);
    }

    addRow(type: any) {
        const isHiveTable = this.props.tableType == TABLE_TYPE.HIVE;
        this.props.addRow({
            columnName: '',
            columnType: isHiveTable ? 'STRING' : 'INTEGER',
            columnDesc: '',
            uuid: Date.now()
        }, type);
    }

    delRow (type: any, uuid: any) {
        this.props.delRow(uuid, type);
    }

    replaceRow (type: any, newCol: any) {
        this.props.replaceRow(newCol, type);
    }

    moveRow (type: any, uuid: any, isUp: any) {
        this.props.moveRow(uuid, type, isUp);
    }

    render () {
        const {
            columns,
            partition_keys,// eslint-disable-line
            isEdit,
            tableType
        } = this.props;
        const isHiveTable = tableType == TABLE_TYPE.HIVE;
        return <div className="m-columnspartition">
            <div className="columns box">
                <h3>字段信息</h3>
                <div className="table">
                    <Row className="title">
                        <Col span={4} className="cell">字段名</Col>
                        <Col span={8} className="cell">类型</Col>
                        <Col span={7} className="cell">注释</Col>
                        <Col span={5} className="cell">操作</Col>
                    </Row>
                    {columns.map((col: any, i: any) => <RowItem
                        data={col}
                        key={col.uuid || i}
                        delRow={this.delRow.bind(this, 1)}
                        replaceRow={this.replaceRow.bind(this, 1)}
                        moveRow={this.moveRow.bind(this, 1)}
                        tableType={tableType}
                    />)}
                </div>
                <div className="fn">
                    <a href="javascript:void(0)" disabled={isEdit} onClick={this.addRow.bind(this, 1)}>
                        <Icon type="plus-circle-o" /> 新增字段
                    </a>
                </div>
            </div>
            {
                isHiveTable && (
                    <div className="partition box">
                        <h3>分区信息</h3>
                        <div className="table">
                            <Row className="title">
                                <Col span={4} className="cell">字段名</Col>
                                <Col span={8} className="cell">类型</Col>
                                <Col span={7} className="cell">注释</Col>
                                <Col span={5} className="cell">操作</Col>
                            </Row>
                            {partition_keys.map((partition: any, i: any) => <RowItem
                                data={{ ...partition, isPartition: true }}
                                key={partition.uuid || i}
                                delRow={this.delRow.bind(this, 2)}
                                replaceRow={this.replaceRow.bind(this, 2)}
                                moveRow={this.moveRow.bind(this, 2)}
                            />)}
                        </div>
                        <div className="fn">
                            <a href="javascript:void(0)"
                                disabled={isEdit}
                                onClick={this.addRow.bind(this, 2)}>
                                <Icon type="plus-circle-o" /> 新增分区字段
                            </a>
                        </div>
                    </div>
                )
            }
        </div>
    }
}

class TableCreator extends React.Component<any, any> {
    constructor(props: any) {
        super(props);

        this.state = {
            current: 0,

            table: {
                tableType: undefined,
                tableName: '',
                desc: '',
                delim: '',
                lifeDay: 90,
                location: undefined, // 存在则为外部表
                columns: [],
                storedType: 'orc',
                partition_keys: []
            }
        };

        // move up/down
        /* eslint-disable */
        Array.prototype.__move = function (from: any, to: any) {
            this.splice(to, 0, this.splice(from, 1)[0]);
            return this;
        };
        /* eslint-enable */
    }

    shouldComponentUpdate (nextProps: any, nextState: any) {
        let shouldUpdate = false;

        if (this.state.current === 0) {
            if (this.state.type !== nextState.type) shouldUpdate = true;
            else shouldUpdate = this.state.current !== nextState.current;
        } else {
            shouldUpdate = true;
        }

        return shouldUpdate;
    }

    next () {
        const { current, table } = this.state;
        const {
            partition_keys,// eslint-disable-line
            columns
        } = table;

        if (current === 0) {
            this.baseForm.validateFields((err: any, values: any) => {
                if (!err) {
                    const next = current + 1;
                    this.setState({ current: next });
                }
            });
        } else if (current === 1) {
            if (partition_keys.length === 0 && columns.length === 0) {
                message.error('请添加字段或分区信息');
            } else {
                this.doCreate();
            }
        }
    }

    prev () {
        const current = this.state.current - 1;
        this.setState({
            current,
            table: Object.assign({}, this.state.table, {
                columns: []
            })
        });
    }

    doCreate () {
        const { table, current } = this.state;
        let {
            columns,
            partition_keys// eslint-disable-line
        } = table;

        columns = this.reduceRowData(columns);
        partition_keys = this.reduceRowData(partition_keys);// eslint-disable-line

        if (partition_keys.length === 0 && columns.length === 0) {
            message.error('字段或分区信息不完整');
        } else {
            ajax.createTable(table).then((res: any) => {
                if (res.code === 1) {
                    const next = current + 1;
                    this.setState({
                        current: next,
                        result: 'success'
                    });
                    setTimeout(() => {
                        this.goBack();
                    }, 3000);
                }
            })
        }
    }

    /**
     * @description 删除不完整的字段/分区信息
     * @param {any} arr
     * @memberof TableCreator
     */
    reduceRowData(arr: any) {
        return arr.filter((data: any) => {
            return data.name !== '';
        });
    }

    dosth () {
        console.log(arguments);
    }

    /**
     * @description 新曾一行
     * @param {any} data 新数据
     * @param {number} type 1: columns 2: partitions
     * @memberof TableCreator
     */
    addRow (data: any, type: any) {
        let { table } = this.state;
        let {
            columns,
            partition_keys// eslint-disable-line
        } = table;

        if (type === 1) {
            columns.push(data);
            table.columns = columns;
        } else if (type === 2) {
            partition_keys.push(data);
            table.partition_keys = partition_keys;// eslint-disable-line
        }

        this.setState({
            table
        });
    }

    /**
     * @description 删除一行
     * @param {any} uuid
     * @param {number} type type 1: columns 2: partitions
     * @memberof TableCreator
     */
    delRow (uuid: any, type: any) {
        let { table } = this.state;
        let {
            columns,
            partition_keys// eslint-disable-line
        } = table;

        if (type === 1) {
            columns = columns.filter((col: any) => {
                return col.uuid !== uuid
            });
            table.columns = columns;
        } else if (type === 2) {
            /* eslint-disable */
            partition_keys = partition_keys.filter((col: any) => {
                return col.uuid !== uuid
            });
            table.partition_keys = partition_keys;
            /* eslint-enable */
        }

        this.setState({
            table
        });
    }

    /**
     * @description 修改（置换）一行
     * @param {any} newCol
     * @param {number} type  1: columns 2: partitions
     * @memberof TableCreator
     */
    replaceRow (newCol: any, type: any) {
        /* eslint-disable */
        let { table } = this.state;
        let { columns, partition_keys } = table;
        const { uuid } = newCol;

        if (type === 1) {
            columns = columns.map((col: any) => {
                if (col.uuid === uuid) return newCol;
                else return col;
            });
            table.columns = columns;
        } else if (type === 2) {
            partition_keys = partition_keys.map((col: any) => {
                if (col.uuid === uuid) return newCol;
                else return col;
            });
            table.partition_keys = partition_keys;
        }
        /* eslint-enable */
        this.setState({
            table
        });
    }

    /**
     * @description 向上、下移动
     * @param {any} uuid
     * @param {number} type 1: columns 2: partitions
     * @param {boolean} isUp
     * @memberof TableCreator
     */
    moveRow (uuid: any, type: any, isUp: any) {
        let { table } = this.state;
        /* eslint-disable */
        let { columns, partition_keys } = table;
        let from: any;

        if (type === 1) {
            columns.forEach((col: any, i: any) => {
                if (col.uuid === uuid) from = i;
            });
            table.columns = columns.__move(from, isUp ? from - 1 : from + 1);
        } else if (type === 2) {
            partition_keys.forEach((col: any, i: any) => {
                if (col.uuid === uuid) from = i;
            });
            table.partition_keys = partition_keys.__move(from, isUp ? from - 1 : from + 1);
        }
        /* eslint-enable */
        this.setState({
            table
        })
    }

    resetLoc () {
        this.setState((state: any) => {
            let table = assign(state.table, {
                location: undefined
            });
            return assign(state, { table });
        });
    }

    goBack = () => {
        const { url, history } = this.props
        if (url) {
            if (history) { browserHistory.push(url) } else { hashHistory.push(url) }
        } else {
            browserHistory.go(-1)
        }
    }

    render () {
        const { current } = this.state;
        const the = this;
        const BaseFormWrapper = Form.create({
            onValuesChange (props: any, values: any) {
                const { table } = the.state;
                the.setState({
                    table: {
                        ...table,
                        ...values
                    }
                })
            }
        })(BaseForm);
        console.log('render')
        const steps: any = [{
            title: '基本信息',
            content: <BaseFormWrapper
                {...this.state.table}
                test={this.state.table}
                ref={(el: any) => this.baseForm = el}
                resetLoc={this.resetLoc.bind(this)}
            />
        }, {
            title: '字段与分区',
            content: <ColumnsPartition {...this.state.table}
                addRow={this.addRow.bind(this)}
                delRow={this.delRow.bind(this)}
                replaceRow={this.replaceRow.bind(this)}
                moveRow={this.moveRow.bind(this)}
            />
        }, {
            title: '新建完成',
            content: <div className="m-createresult" style={{ textAlign: 'center' }}>
                {this.state.result ? (this.state.result === 'success'
                    ? <div>
                        <h3>
                            <Icon type="check-circle" style={{ color: 'green' }} /> 新建成功!
                        </h3>
                        <p style={{ marginTop: 10 }}><span className="m-countdown" /> 秒后自动返回</p>
                    </div>
                    : <div>
                        <h3>
                            <Icon type="close-circle" style={{ color: 'red' }} /> 新建失败!
                        </h3>
                        <p style={{ color: 'red', marginTop: 10 }}>{this.state.result.message}</p>
                    </div>) : null
                }
            </div>
        }];

        return <div className="bg-w" style={{ padding: '20px', margin: '20px' }}>
            <Steps current={current}>
                {steps.map((item: any) => <Step key={item.title} title={item.title} />)}
            </Steps>
            <div className="steps-content">
                {steps[current].content}
            </div>
            <div className="steps-action">
                {
                    current != 2 && <Button style={{ marginRight: 8 }}
                        onClick={this.goBack}
                    >取消</Button>
                }
                {current > 0 && current !== 2 &&
                    <Button style={{ marginRight: 8 }}
                        onClick={() => this.prev()}
                    > 上一步 </Button>
                }
                {current < steps.length - 1 && <Button type="primary"
                    onClick={() => this.next()}
                >{current === 1 ? '提交' : '下一步'}</Button>
                }
                {current != 2 && current === steps.length - 1 && <Button type="primary"
                    onClick={this.goBack}
                >返回</Button>
                }
            </div>
        </div>
    }
}

export default TableCreator;
