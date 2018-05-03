import React from 'react';
import { connect } from 'react-redux';
import { Steps, Button, message, Form, Input,
    Row, Col, Icon, Select, Radio, Tooltip, InputNumber } from 'antd';
import assign from 'object-assign';
import { isEqual, throttle, range, isObject } from 'lodash';

import ajax from '../../api';
import { formItemLayout } from '../../comm/const';
import CatalogueTree from './catalogTree';
import LifeCycle from './lifeCycle';

const Step = Steps.Step;
const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;

/**
 * @description step1:基本信息
 * @class BaseForm
 * @extends {React.Component}
 */
class BaseForm extends React.Component {
    
    constructor(props) {
        super(props);

        this.state = {
            type: '1', // 1: 内部表 2:外部表
            dataCatalogue: [],
        };
    }

    componentDidMount() {
        this.loadCatalogue();
    }

    loadCatalogue = () => {
        ajax.getDataCatalogues().then(res => {
            this.setState({
                dataCatalogue: res.data && [res.data],
            })
        })
    }

    lifeCycleChange = (value) => {
        this.props.form.setFieldsValue({'lifeDay': value})
    }

    render() {
        const { getFieldDecorator } = this.props.form;
        const { tableName, desc, delim, location, lifeDay, catalogueId } = this.props;
        const { type, dataCatalogue } = this.state;

        return <Form>
            <FormItem
                {...formItemLayout}
                label="表名"
                hasFeedback
            >
                {getFieldDecorator('tableName', {
                    rules: [{
                        required: true, message: '表名不可为空！',
                    }, {
                        pattern: /^([A-Za-z0-9_]{1,64})$/,
                        message: '表名称只能由字母、数字、下划线组成，且长度不超过64个字符!',
                    }, {
                        validator: this.validateTableName.bind(this)
                    }],
                    validateTrigger: 'onBlur',
                    initialValue: tableName
                })(
                    <Input placeholder="请输入表名" autoComplete="off" />,
                )}
            </FormItem>
            <FormItem
                {...formItemLayout}
                label="分隔符"
                hasFeedback
            >
                {getFieldDecorator('delim', {
                    rules: [],
                    initialValue: delim,
                })(
                    <Input placeholder="分隔符" autoComplete="off" />
                )}
            </FormItem>
            <FormItem
                {...formItemLayout}
                label="类型"
                hasFeedback
            >
                <RadioGroup value={ this.state.type }
                    onChange={ this.handleChange.bind(this) }
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
                        message: '外部表地址不可为空！',
                    }, {
                        validator: this.validateLoc.bind(this)
                    }],
                    initialValue: location,
                    validateTrigger: 'onBlur'
                })(
                    <Input placehoder="外部表地址"/>
                )}
            </FormItem>}
            <FormItem
                {...formItemLayout}
                label="所属类目"
            >
                {getFieldDecorator('catalogueId', {
                    rules: [{
                        required: true,
                        message: '表所在类目不可为空！'
                    }],
                    initialValue: catalogueId || undefined,
                })(
                    <CatalogueTree
                        isPicker
                        isFolderPicker
                        placeholder="请选择类目"
                        treeData={dataCatalogue}
                    />
                )}
            </FormItem>
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
                        required: true, message: '存储格式不可为空！',
                    }],
                    initialValue: 'textfile',
                })(
                    <Select>
                        <Option value="textfile">textfile</Option>
                        <Option value="orc">orc</Option>
                    </Select>
                )}
            </FormItem>
            <FormItem
                {...formItemLayout}
                label="描述"
            >
                {getFieldDecorator('tableDesc', {
                    rules: [{
                        max: 200,
                        message: '描述不得超过200个字符！',
                    }],
                    initialValue: desc
                })(
                    <Input type="textarea" placeholder="描述信息" />
                )}
            </FormItem>
        </Form>
    }

    validateDelim(rule, value, callback) {
        value = value.trim();

        if(value[0] === '\\') {
            if(value.length > 2) {
                callback('分隔符长度只能为1（不包括转义字符"\\"）')
            }
        }
        else {
            if(value.length > 1) {
                callback('分隔符长度只能为1')
            }
        }
        callback();
    }

    validateTableName(rule, value, callback) {
        const ctx = this;
        value ? ajax.checkTableExist({
            tableName: value
        }).then(res => {
            if(res.code === 1) {
                // 转换为小写
                ctx.props.form.setFieldsValue({ tableName: value.toLowerCase() })
                if(res.data) callback('该表已经存在！');
            }
        })
        .then(callback) : callback();
    }

    validateLoc(rule, value, callback) {
        value ? ajax.checkHdfsLocExist({
            hdfsUri: 'hdfs://' + value
        }).then(res => {
            if(res.code === 1) {
                if(!res.data) callback('此目录不存在');
            }
        })
        .then(callback) : callback();
    }

    handleChange(e) {
        const type = e.target.value;

        this.setState({type});
        type === '1' && this.props.resetLoc();
    }
}


/**
 * @description 字段/分区 一行
 * @class RowItem
 * @extends {React.Component}
 */
export class RowItem extends React.Component {

    constructor(props) {
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
    handleChange(selectName, evt) {
        const { data, replaceRow } = this.props;
        let iptName, value;

        if(isObject(evt)) {
            iptName = evt.target.name;
            value = evt.target.value;
        }
        else{
            iptName = selectName;
            value = evt;
        }

        const newData = assign({}, data, { [iptName]: value });
        const TYPE = newData.type.toUpperCase();

        if(TYPE === 'DECIMAL') {
            if(!newData.precision) newData.precision = 10;
            if(!newData.scale) newData.scale = 0;
        }
        if(TYPE === 'CHAR') {
            if(!newData.charLen) newData.charLen = 10;
        }
        if(TYPE === 'VARCHAR') {
            if(!newData.varcharLen) newData.varcharLen = 10;
        }

        if (this.checkParams(newData)) {
            replaceRow(newData);
        }
    }

    checkParams(params) {
        const reg = /^[A-Za-z0-9_]+$/;
        if (params.name) {
            if (!reg.test(params.name)) {
                message.error('字段名称只能由字母、数字、下划线组成！')
                return false;
            }
            if (params.name.length > 20) {
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

    shouldComponentUpdate(nextProps, nextState) {
        return !isEqual(this.props, nextProps);
    }

    render() {
        const { data } = this.props;
        const { editMode } = this.state;
        const { isSaved, isPartition, precision, scale } = data;
        const needExtra = ['DECIMAL', 'VARCHAR', 'CHAR'].indexOf(data.type.toUpperCase()) !== -1;
        const TYPES = isPartition ?
            ['STRING', 'BIGINT'] :
            ["TINYINT", "SMALLINT", "INT", "BIGINT", "BOOLEAN",
            "FLOAT", "DOUBLE", "STRING", "BINARY", "TIMESTAMP",
            "DECIMAL", "DATE", "VARCHAR", "CHAR"
            ];

        return <Row className="row">
            <Col span={4} className="cell">
                <Input name="name" defaultValue={ data.name }
                    autoComplete="off"
                    onChange={ this.handleChange.bind(this, undefined) }
                    disabled={ isSaved }
                />
            </Col>
            <Col span={8} className="cell">
                <Select name="type" defaultValue={ data.type }
                    onChange={ this.handleChange.bind(this, 'type') }
                    style={{ width: needExtra ? '40%' : '80%' }}
                    disabled={ isSaved }
                >
                    {TYPES.map(str => <Option key={str} value={str}>{str}</Option>)}
                </Select>
                { needExtra && this.renderExtra(data.type) }
            </Col>
            <Col span={7} className="cell">
                <Input 
                    name="comment" 
                    defaultValue={ data.comment }
                    autoComplete="off"
                    onChange={ this.handleChange.bind(this, undefined) }
                    disabled={ isSaved && isPartition }
                />
            </Col>
            <Col span={5} className="cell" style={{ paddingTop: 13 }}>
                <a href="javascript:void(0)"
                    disabled={ isSaved }
                    onClick={ () => this.props.moveRow(data.uuid, true) }
                >上移</a>
                <span> | </span>
                <a href="javascript:void(0)"
                    disabled={ isSaved }
                    onClick={ () => this.props.moveRow(data.uuid, false) }
                >下移</a>
                <span> | </span>
                <a href="javascript:void(0)"
                    disabled={ isSaved }
                    onClick={ () => this.props.delRow(data.uuid) }
                >删除</a>
            </Col>
        </Row>
    }

    renderExtra(type) {
        const { data } = this.props;
        const { precision, scale, charLen, varcharLen, isSaved } = data;
        let result = '';

        type = type.toUpperCase();
        switch(type) {
            case 'DECIMAL':
                result = <span className="extra-ipt">
                    <Select name="precision"
                        style={{ marginLeft: '2%', width: '18%' }}
                        value={ `${precision}` || '10'}
                        onChange={ this.handleChange.bind(this, 'precision') }
                        placeholder="precision"
                        disabled={ isSaved }
                    >
                        {range(39).slice(1).map(n => <Option value={`${n}`}
                            key={n}
                        >{n}</Option>)}
                    </Select>
                    <Select name="scale"
                        style={{ marginLeft: '2%', width: '18%' }}
                        value={ `${scale}` || '0'}
                        onChange={ this.handleChange.bind(this, 'scale') }
                        placeholder="scale"
                        disabled={ isSaved }
                    >
                        {range(precision || 10).map(n1 => <Option value={`${n1}`}
                            key={n1}
                        >{n1}</Option>)}
                    </Select>
                    <Tooltip title="type(precision,scale)；precision:数字总长度，最大为38；scale：小数点之后的位数">
                        <Icon type="question-circle-o" style={{ marginLeft: '2%' }}/>
                    </Tooltip>
                </span>
                break;
            case 'CHAR':
                result = <span className="extra-ipt">
                    <InputNumber name="charLen" defaultValue={ charLen || 10 }
                        min={1}
                        max={255}
                        style={{ width: '38%', marginLeft: '2%' }}
                        onChange={ this.handleChange.bind(this, 'charLen') }
                        disabled={ isSaved }
                    />
                    <Tooltip title="type(char)；char的长度为1~255">
                        <Icon type="question-circle-o" style={{ marginLeft: '2%' }}/>
                    </Tooltip>
                </span>
                break;
            case 'VARCHAR':
                result = <span className="extra-ipt">
                    <InputNumber name="varcharLen" defaultValue={ varcharLen || 10 }
                        min={1}
                        max={65535}
                        style={{ width: '38%', marginLeft: '2%' }}
                        onChange={ this.handleChange.bind(this, 'varcharLen') }
                        disabled={ isSaved }
                    />
                    <Tooltip title="type(varchar)；varchar的长度为1~65535">
                        <Icon type="question-circle-o" style={{ marginLeft: '2%' }}/>
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
export class ColumnsPartition extends React.Component {
    constructor(props) {
        super(props);
    }

    addRow(type) {
        this.props.addRow({
            name: '',
            type: 'STRING',
            desc: '',
            uuid: Date.now()
        }, type);
    }

    delRow(type, uuid) {
        this.props.delRow(uuid, type);
    }

    replaceRow(type, newCol) {
        this.props.replaceRow(newCol, type);
    }

    moveRow(type, uuid, isUp) {
        this.props.moveRow(uuid, type, isUp);
    }

    render() {
        const { columns, partition_keys, isEdit } = this.props;

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
                    { columns.map((col, i) => <RowItem
                        data={ col }
                        key={ col.uuid || i }
                        delRow={ this.delRow.bind(this, 1) }
                        replaceRow={ this.replaceRow.bind(this, 1) }
                        moveRow={ this.moveRow.bind(this, 1) }
                    />)}
                </div>
                <div className="fn">
                    <a href="javascript:void(0)" onClick={ this.addRow.bind(this, 1) }>
                        <Icon type="plus-circle-o" /> 新增字段
                    </a>
                </div>
            </div>
            <div className="partition box">
                <h3>分区信息</h3>
                <div className="table">
                    <Row className="title">
                        <Col span={4} className="cell">字段名</Col>
                        <Col span={8} className="cell">类型</Col>
                        <Col span={7} className="cell">注释</Col>
                        <Col span={5} className="cell">操作</Col>
                    </Row>
                    { partition_keys.map((partition, i) => <RowItem
                        data={{...partition, isPartition: true}}
                        key={ partition.uuid || i }
                        delRow={ this.delRow.bind(this, 2) }
                        replaceRow={ this.replaceRow.bind(this, 2) }
                        moveRow={ this.moveRow.bind(this, 2) }
                    />)}
                </div>
                <div className="fn">
                    <a href="javascript:void(0)"
                        disabled={ isEdit }
                        onClick={ this.addRow.bind(this, 2) }>
                        <Icon type="plus-circle-o" /> 新增分区
                    </a>
                </div>
            </div>
        </div>
    }
}


class TableCreator extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            current: 0,

            table: {
                tableName: '',
                desc: '',
                delim: '',
                lifeDay: 90,
                location: undefined, // 存在则为外部表
                columns: [],
                storedType: 'textfile',
                partition_keys: []
            }
        };

        // move up/down
        Array.prototype.__move = function(from, to) {
            this.splice(to, 0, this.splice(from, 1)[0]);
            return this;
        };
    }

    shouldComponentUpdate(nextProps, nextState) {
        let shouldUpdate = false;

        if(this.state.current === 0) {
            if(this.state.type !== nextState.type) shouldUpdate = true;
            else shouldUpdate = this.state.current !== nextState.current;
        }
        else {
            shouldUpdate = true;
        }

        return shouldUpdate;
    }

    next() {
        const { current, table } = this.state;
        const { partition_keys, columns } = table;

        if(current === 0) {
            this.baseForm.validateFields((err, values) => {
                if(!err) {
                    const next = current + 1;
                    this.setState({ current: next });
                }
            });
        }
        else if(current === 1){
            if(partition_keys.length === 0 && columns.length === 0) {
                message.error('请添加字段或分区信息');
            }
            else {
                this.doCreate();
            }
        }
    }

    prev() {
        const current = this.state.current - 1;
        this.setState({ current });
    }

    doCreate() {
        const { table, current } = this.state;
        let { columns, partition_keys } = table;

        columns = this.reduceRowData(columns);
        partition_keys = this.reduceRowData(partition_keys);

        if(partition_keys.length === 0 && columns.length === 0) {
            message.error('字段或分区信息不完整');
        }
        else {
            ajax.createTable(table).then(res => {
                if(res.code === 1) {
                    const next = current + 1;
                    this.setState({ current: next,
                        result: 'success'
                    });
                    setTimeout(() => {
                        this.props.router.push('/data-manage/table');
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
    reduceRowData(arr) {
        return arr.filter(data => {
            return data.name !== '';
        });
    }

    dosth() {
        console.log(arguments);
    }

    /**
     * @description 新曾一行
     * @param {any} data 新数据
     * @param {number} type 1: columns 2: partitions
     * @memberof TableCreator
     */
    addRow(data, type) {

        let { table } = this.state;
        let { columns, partition_keys } = table;

        if(type === 1) {
            columns.push(data);
            table.columns = columns;
        }
        else if(type === 2) {
            partition_keys.push(data);
            table.partition_keys = partition_keys;
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
    delRow(uuid, type) {
        let { table } = this.state;
        let { columns, partition_keys } = table;

        if(type === 1) {
            columns = columns.filter(col => {
                return col.uuid !== uuid
            });
            table.columns = columns;
        }
        else if(type === 2) {
            partition_keys = partition_keys.filter(col => {
                return col.uuid !== uuid
            });
            table.partition_keys = partition_keys;
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
    replaceRow(newCol, type) {
        let { table } = this.state;
        let { columns, partition_keys } = table;
        const { uuid } = newCol;

        if(type === 1) {
            columns = columns.map(col => {
                if(col.uuid === uuid) return newCol;
                else return col;
            });
            table.columns = columns;
        }
        else if(type === 2) {
            partition_keys = partition_keys.map(col => {
                if(col.uuid === uuid) return newCol;
                else return col;
            });
            table.partition_keys = partition_keys;
        }

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
    moveRow(uuid, type, isUp) {
        let { table } = this.state;
        let { columns, partition_keys } = table;
        let from;

        if(type === 1) {
            columns.forEach((col, i) => {
                if(col.uuid === uuid) from = i;
            });
            table.columns = columns.__move(from,  isUp ? from - 1 : from + 1);
        }
        else if(type === 2) {
            partition_keys.forEach((col, i) => {
                if(col.uuid === uuid) from = i;
            });
            table.partition_keys = partition_keys.__move(from, isUp ? from - 1 : from + 1);
        }

        this.setState({
            table
        })
    }

    resetLoc() {
        this.setState(state => {
            let table = assign(state.table, {
                location: undefined
            });
            return assign(state, {table});
        });
    }

    render() {
        const the = this;
        const BaseFormWrapper = Form.create({
            onValuesChange(props, values) {
                the.setState(state => {
                    state.table = assign(state.table, values);
                    return state;
                });
            }
        })(BaseForm);

        const steps = [{
            title: '基本信息',
            content: <BaseFormWrapper
                {...this.state.table}
                ref={ el => this.baseForm = el }
                resetLoc={ this.resetLoc.bind(this) }
            />
        }, {
            title: '字段与分区',
            content: <ColumnsPartition {...this.state.table}
                addRow={ this.addRow.bind(this) }
                delRow={ this.delRow.bind(this) }
                replaceRow={ this.replaceRow.bind(this) }
                moveRow={ this.moveRow.bind(this) }
            />
        }, {
            title: '新建完成',
            content: <div className="m-createresult" style={{textAlign: 'center'}}>
                { this.state.result ? (this.state.result === 'success' ?
                    <div>
                        <h3>
                            <Icon type="check-circle" style={{ color: 'green' }}/> 新建成功!
                        </h3>
                        <p style={{ marginTop: 10 }}><span className="m-countdown" /> 秒后自动返回</p>
                    </div>:
                    <div>
                        <h3>
                            <Icon type="close-circle" style={{ color: 'red' }}/> 新建失败!
                        </h3>
                        <p style={{ color: 'red', marginTop: 10 }}>{this.state.result.message}</p>
                    </div>) : null
                }
            </div>,
        }];

        return <div className="bg-w" style={{ padding: '20px', margin: '20px' }}>
            <Steps current={this.state.current}>
                {steps.map(item => <Step key={item.title} title={item.title} />)}
            </Steps>
            <div className="steps-content">
                {steps[this.state.current].content}
            </div>
            <div className="steps-action">
                <Button style={{ marginRight: 8 }}
                    onClick={ () => this.props.router.push('/data-manage/table') }
                >取消</Button>
                { this.state.current > 0 && this.state.current !== 2 &&
                    <Button style={{ marginRight: 8 }}
                        onClick={() => this.prev()}
                    > 上一步 </Button>
                }
                { this.state.current < steps.length - 1 && <Button type="primary"
                        onClick={ () => this.next() }
                    >{ this.state.current === 1 ? '提交' : '下一步' }</Button>
                }
                { this.state.current === steps.length - 1 && <Button type="primary"
                        onClick={() => this.props.router.push('/data-manage/table')}
                    >返回</Button>
                }
            </div>
        </div>
    }
}

export default TableCreator;