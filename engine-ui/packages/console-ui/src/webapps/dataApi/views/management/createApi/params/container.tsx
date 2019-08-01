import * as React from 'react';
import { cloneDeep } from 'lodash'
import { connect } from 'react-redux';

import { apiManageActions } from '../../../../actions/apiManage';
import { API_MODE } from '../../../../consts'
import { resolveFormItemKey } from './helper';
import WrapColumnsConfigForm from './columnsConfig';

class ColumnsConfigContainer extends React.Component<any, any> {
    state: any = {
        InputSelectedRows: [],
        OutSelectedRows: []
    }
    formRef = React.createRef();
    /**
     * 移除参数
     * @param {*} rows 要去除的参数
     * @param {*} type 输入还是输出
     */
    filterSelectRow (rows: any, type: any) {
        const { InputSelectedRows, OutSelectedRows } = this.state;
        const idArr = rows.map(
            (row: any) => {
                return row.id;
            }
        )
        if (type == 'in') {
            this.setState({
                InputSelectedRows: InputSelectedRows.filter(
                    (row: any) => {
                        return !idArr.includes(row.id);
                    }
                )
            })
        } else if (type == 'out') {
            this.setState({
                OutSelectedRows: OutSelectedRows.filter(
                    (row: any) => {
                        return !idArr.includes(row.id);
                    }
                )
            })
        }
    }
    /**
     * 更新修改内容
     * @param {*} value 改变的字段
     */
    updateValue (value: any) {
        let { InputColumns, OutputColums, updateColumns } = this.props;
        const keyAndValue = Object.entries(value);
        let inColumnsMap: any = {};
        let outColumnsMap: any = {};
        InputColumns = cloneDeep(InputColumns);
        OutputColums = cloneDeep(OutputColums);
        /**
         * 先把修改的信息都汇总一下哈
         */
        for (let i = 0; i < keyAndValue.length; i++) {
            let formItemAndValue = keyAndValue[i];
            let formItemValue = formItemAndValue[1];
            let formItem = resolveFormItemKey(formItemAndValue[0]);
            if (!formItem) {
                continue;
            }
            let id = formItem.id;
            let name = formItem.name;
            let type = formItem.type;
            if (type == 'in') {
                inColumnsMap[id] = {
                    ...inColumnsMap[id],
                    [name]: formItemValue
                }
            } else {
                outColumnsMap[id] = {
                    ...inColumnsMap[id],
                    [name]: formItemValue
                }
            }
        }
        /**
         * 把改变的数据和原始数据合并
         * @param {*} source 原始数据
         * @param {*} target 改变的数据
         */
        function merge (source: any, target: any) {
            source.map(
                (column: any) => {
                    const id = column.id;
                    if (target[id]) {
                        const keys = Object.entries(target[id]);
                        for (let i = 0; i < keys.length; i++) {
                            let fields = keys[i];
                            const key = fields[0];
                            const value = fields[1];
                            column[key] = value;
                        }
                    }
                    return column;
                }
            )
        }
        /**
         * 汇总完了，就开始修改
         */
        merge(InputColumns, inColumnsMap);
        merge(OutputColums, outColumnsMap);
        updateColumns(InputColumns, 'in')
        updateColumns(OutputColums, 'out')
    }
    rowSelection (type: any) {
        const { mode } = this.props;
        if (mode == API_MODE.SQL) {
            return undefined;
        }
        return {
            onChange: (selectedRowKeys: any, selectedRows: any) => {
                switch (type) {
                    case 'in': {
                        this.setState({
                            InputSelectedRows: selectedRows
                        })
                        return;
                    }
                    case 'out': {
                        this.setState({
                            OutSelectedRows: selectedRows
                        })
                    }
                }
            }
        }
    }
    validateFields () {
        return new Promise((resolve: any, reject: any) => {
            (this.formRef.current as any).validateFieldsAndScroll({}, (err: any, values: any) => {
                if (!err) {
                    resolve(true)
                } else {
                    resolve(false)
                }
            })
        })
    }
    render () {
        const {
            mode,
            sqlModeShowChange,
            handleClickCode
        } = this.props;
        const { InputSelectedRows, OutSelectedRows } = this.state;
        const header = mode == API_MODE.SQL
            ? (
                <span>
                    API参数配置
                    <span style={{ float: 'right', marginLeft: '8px', fontSize: '12px', color: '#888' }}>
                        编辑参数
                    </span>
                    <a onClick={() => {
                        handleClickCode();
                        sqlModeShowChange();
                    }} style={{ float: 'right', fontSize: '12px' }}>代码</a>
                </span>
            ) : <p className='middle-title'>API参数配置</p>

        return (
            <div>
                {header}
                <WrapColumnsConfigForm
                    ref={this.formRef as any}
                    {...this.props}
                    {...{
                        OutSelectedRows: OutSelectedRows,
                        rowSelection: this.rowSelection.bind(this),
                        filterSelectRow: this.filterSelectRow.bind(this),
                        updateValue: this.updateValue.bind(this),
                        InputSelectedRows: InputSelectedRows
                    }}
                />
            </div>
        )
    }
}

const mapDispatchToProps = (dispatch: any) => {
    return {
        handleClickCode () {
            dispatch(apiManageActions.clickCode())
        }
    }
}

export default connect(null, mapDispatchToProps, null, { withRef: true })(ColumnsConfigContainer);
