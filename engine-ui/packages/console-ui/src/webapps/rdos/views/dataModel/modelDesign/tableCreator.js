import React from 'react';
import { connect } from 'react-redux';
import assign from 'object-assign';

import { 
    Steps, Button, message,
    Form, Row, Col, Icon
} from 'antd';

import ajax from '../../../api/dataModel';
import { 
    tableModelRules,
} from '../../../comm/const';

import BaseForm from './baseForm';
import ColumnsPartition from './columnsPartition';

const Step = Steps.Step;

class TableCreator extends React.Component {

    constructor(props) {
        super(props);

        this.state = {

            current: 0,

            tableNameRules: tableModelRules,

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

    componentDidMount() {
        this.loadTableNameRules();
    }

    shouldComponentUpdate(nextProps, nextState) {
        let shouldUpdate = false;

        if (this.state.current === 0) {
            if(this.state.type !== nextState.type) shouldUpdate = true;
            else shouldUpdate = this.state.current !== nextState.current;
        }
        else {
            shouldUpdate = true;
        }

        if (this.state.tableNameRules !== nextState.tableNameRules) {
            return true;
        }

        return shouldUpdate;
    }

    loadTableNameRules = () => {
        ajax.getTableNameRules().then(res => {
            if (res.code === 1) {
                this.setState({
                    tableNameRules: res.data,
                })
            }
        })
    }

    changeRuleValue = (value, index) => {
        const newArrs = [...this.state.tableNameRules];
        newArrs[index].field = value;
        console.log('arguments:', newArrs[index], value, index)
        this.setState({
            tableNameRules: newArrs
        });
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
        const { table, current, tableNameRules } = this.state;
        let { columns, partition_keys } = table;

        columns = this.reduceRowData(columns);
        partition_keys = this.reduceRowData(partition_keys);

        if(partition_keys.length === 0 && columns.length === 0) {
            message.error('字段或分区信息不完整');
        }
        // 利用表名生成表名
        if (tableNameRules.length > 0) {
            table.tableName = tableNameRules.map(rule => rule.name).join('_');
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
                tableNameRules={this.state.tableNameRules}
                changeRuleValue={this.changeRuleValue}
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