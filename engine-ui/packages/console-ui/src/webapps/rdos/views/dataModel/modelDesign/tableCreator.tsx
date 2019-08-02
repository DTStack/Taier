/* eslint-disable @typescript-eslint/camelcase */
import * as React from 'react';
import assign from 'object-assign';
import { connect } from 'react-redux';
import {
    Steps, Button, message, Form, Icon
} from 'antd';

import api from '../../../api/dataManage';
import ajax from '../../../api/dataModel';
import { getProjectTableTypes } from '../../../store/modules/tableType';
import BaseForm from './baseForm';
import ColumnsPartition from './columnsPartition';
const Step = Steps.Step;

@(connect((state: any) => {
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
}) as any)

class TableCreator extends React.Component<any, any> {
    constructor (props: any) {
        super(props);

        this.state = {

            current: 0,

            tableNameRules: [],

            table: {
                tableType: undefined,
                tableName: '',
                tableDesc: '',
                delim: '',
                lifeDay: 90,
                location: undefined, // 存在则为外部表
                columns: [],
                storedType: 'orc',
                subject: '', // 主题域
                grade: '', // 模型层级
                partition_keys: []
            },

            modelLevels: [],
            subjectFields: [],
            incrementCounts: [],
            freshFrequencies: [],
            dataCatalogue: [], // 数据类目
            columnFileds: [] // 指标字段
        };

        // move up/down
        /* eslint-disable */
        (Array.prototype as any).__move = function (from: any, to: any) {
            this.splice(to, 0, this.splice(from, 1)[0]);
            return this;
        };
        /* eslint-enable */
    }
    baseForm: any = React.createRef();
    componentDidMount () {
        const { getProjectTableTypes, project } = this.props;
        const projectId = project && project.id;
        if (projectId) {
            getProjectTableTypes(projectId);
        }
        this.loadTableNameRules();
        this.loadOptionsData();
        this.loadCatalogue();
    }

    shouldComponentUpdate (nextProps: any, nextState: any) {
        let shouldUpdate = false;

        if (this.state.current === 0) {
            if (this.state.type !== nextState.type) shouldUpdate = true;
            else shouldUpdate = this.state.current !== nextState.current;
        } else {
            shouldUpdate = true;
        }

        if (this.state.tableNameRules !== nextState.tableNameRules ||
            this.state.modelLevels !== nextState.modelLevels ||
            this.state.dataCatalogue !== nextState.dataCatalogue
        ) {
            return true;
        }

        return shouldUpdate;
    }

    loadCatalogue = () => {
        api.getDataCatalogues().then((res: any) => {
            this.setState({
                dataCatalogue: res.data && [res.data]
            })
        })
    }

    loadTableNameRules = () => {
        ajax.getTableNameRules().then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    tableNameRules: res.data
                })
            }
        })
    }

    loadOptionsData = () => {
        const defaultParams: any = {
            currentPage: 1,
            pageSize: 1000
        }
        const ctx = this;
        // 加载主题选项
        const callSucc = (field: any) => {
            return function (res: any) {
                if (res.code === 1) {
                    ctx.setState({
                        [field]: res.data ? res.data.data : []
                    })
                }
            }
        }
        ajax.getModels(assign({
            type: 1 // 模型层级
        }, defaultParams)).then(callSucc('modelLevels'));

        ajax.getModels(assign({
            type: 2 // 模型层级
        }, defaultParams)).then(callSucc('subjectFields'));

        ajax.getModels(assign({
            type: 3 // 模型层级
        }, defaultParams)).then(callSucc('freshFrequencies'));

        ajax.getModels(assign({
            type: 4 // 模型层级
        }, defaultParams)).then(callSucc('incrementCounts'));

        // 获取指标字段
        ajax.getTablePartitions().then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    columnFileds: res.data || []
                })
            }
        });
    }

    next () {
        const { current, table } = this.state;
        const { partition_keys, columns } = table;// eslint-disable-line

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
            table: Object.assign({}, this.state.table, { // 避免切换表类型，清空columns数据
                columns: []
            })
        });
    }

    doCreate () {
        const { table, current } = this.state;
        let { columns, partition_keys } = table;// eslint-disable-line camelcase
        columns = this.reduceRowData(columns);
        partition_keys = this.reduceRowData(partition_keys);// eslint-disable-line camelcase

        if (partition_keys.length === 0 && columns.length === 0) {
            message.error('字段或分区信息不完整');
        }

        ajax.createTable(table).then((res: any) => {
            if (res.code === 1) {
                const next = current + 1;
                this.setState({ current: next,
                    result: 'success'
                });
                setTimeout(() => {
                    this.props.router.push('/data-model/table');
                }, 3000);
            }
        })
    }

    /**
     * @description 删除不完整的字段/分区信息
     * @param {any} arr
     * @memberof TableCreator
     */
    reduceRowData (arr: any) {
        return arr.filter((data: any) => {
            return data.name !== '';
        });
    }

    /**
     * @description 新曾一行
     * @param {any} data 新数据
     * @param {number} type 1: columns 2: partitions
     * @memberof TableCreator
     */
    addRow (data: any, type: any) {
        let { table } = this.state;
        let { columns, partition_keys } = table; // eslint-disable-line camelcase

        if (type === 1) {
            columns.push(data);
            table.columns = columns;
        } else if (type === 2) {
            partition_keys.push(data);
            table.partition_keys = partition_keys; // eslint-disable-line camelcase
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
        let { columns, partition_keys } = table; // eslint-disable-line camelcase

        if (type === 1) {
            columns = columns.filter((col: any) => {
                return col.uuid !== uuid
            });
            table.columns = columns;
        } else if (type === 2) {
            // eslint-disable-next-line camelcase
            partition_keys = partition_keys.filter((col: any) => {
                return col.uuid !== uuid
            });
            table.partition_keys = partition_keys; // eslint-disable-line camelcase
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
        let { table } = this.state;
        let { columns, partition_keys } = table; // eslint-disable-line camelcase
        const { uuid } = newCol;

        if (type === 1) {
            columns = columns.map((col: any) => {
                if (col.uuid === uuid) return newCol;
                else return col;
            });
            table.columns = columns;
        } else if (type === 2) {
            // eslint-disable-next-line camelcase
            partition_keys = partition_keys.map((col: any) => {
                if (col.uuid === uuid) return newCol;
                else return col;
            });
            table.partition_keys = partition_keys; // eslint-disable-line camelcase
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
    moveRow (uuid: any, type: any, isUp: any) {
        let { table } = this.state;
        let { columns, partition_keys } = table; // eslint-disable-line camelcase
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

    render () {
        const the = this;

        const {
            modelLevels,
            subjectFields,
            incrementCounts,
            freshFrequencies,
            columnFileds, // 指标字段
            dataCatalogue
        } = this.state;

        const BaseFormWrapper = Form.create({
            onValuesChange (props: any, values: any) {
                the.setState((state: any) => {
                    state.table = assign(state.table, values);
                    return state;
                });
            }
        })(BaseForm);

        const steps: any = [{
            title: '基本信息',
            content: <BaseFormWrapper
                {...this.state.table}
                tableNameRules={this.state.tableNameRules}
                modelLevels={modelLevels}
                subjectFields={subjectFields}
                incrementCounts={incrementCounts}
                freshFrequencies={freshFrequencies}
                projectTableTypes={this.props.projectTableTypes}
                dataCatalogue={dataCatalogue}
                ref={ (el: any) => this.baseForm = el }
                resetLoc={ this.resetLoc.bind(this) }
            />
        }, {
            title: '字段与分区',
            content: <ColumnsPartition
                {...this.state.table}
                addRow={ this.addRow.bind(this) }
                delRow={ this.delRow.bind(this) }
                columnFileds={columnFileds}
                replaceRow={ this.replaceRow.bind(this) }
                moveRow={ this.moveRow.bind(this) }
            />
        }, {
            title: '新建完成',
            content: <div className="m-createresult" style={{ textAlign: 'center' }}>
                { this.state.result ? (this.state.result === 'success'
                    ? <div>
                        <h3>
                            <Icon type="check-circle" style={{ color: 'green' }}/> 新建成功!
                        </h3>
                        <p style={{ marginTop: 10 }}><span className="m-countdown" /> 秒后自动返回</p>
                    </div>
                    : <div>
                        <h3>
                            <Icon type="close-circle" style={{ color: 'red' }}/> 新建失败!
                        </h3>
                        <p style={{ color: 'red', marginTop: 10 }}>{this.state.result.message}</p>
                    </div>) : null
                }
            </div>
        }];

        return <div className="bg-w" style={{ padding: '20px', margin: '20px' }}>
            <Steps current={this.state.current}>
                {steps.map((item: any) => <Step key={item.title} title={item.title} />)}
            </Steps>
            <div className="steps-content">
                {steps[this.state.current].content}
            </div>
            <div className="steps-action">
                <Button style={{ marginRight: 8 }}
                    onClick={ () => this.props.router.push('/data-model/table') }
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
                    onClick={() => this.props.router.push('/data-model/table')}
                >返回</Button>
                }
            </div>
        </div>
    }
}

export default TableCreator;
