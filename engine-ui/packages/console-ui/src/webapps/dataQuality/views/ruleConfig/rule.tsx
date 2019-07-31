import * as React from 'react';
import { connect } from 'react-redux';
import { get } from 'lodash';

import RuleForm from './ruleForm';

@(connect((state: any)=> {
    return {
        monitorFunction: get(state, 'ruleConfig.monitorFunction')
    }
}) as any)
class Rule extends React.Component<any, any> {
    constructor(props: any) {
        super(props);
        this.state = {
            copyData: props.data
        }
    }
    getTableFunction () {
        const { monitorFunction } = this.props;
        return monitorFunction.all.filter(
            (item: any) => item.level === 1
        );
    }
    getColumnFunction(columnName: any) {
        const { monitorFunction, tableColumn } = this.props;
        let columnType = get(tableColumn.filter(
            (item: any) => item.key === columnName
        )[0], 'type');
        return columnType ? monitorFunction[columnType] : [];
    }
    typeCheckFunction () {
        const { monitorFunction } = this.props;
        return monitorFunction.format;
    }
    getFunction () {
        const { copyData } = this.state;
        const { type } = this.props;
        switch (type) {
            case 'column': {
                return this.getColumnFunction(copyData.columnName);
            }
            case 'table': {
                return this.getTableFunction();
            }
            case 'typeCheck': {
                return this.typeCheckFunction();
            }
            default: {
                return [];
            }
        }
    }
    onValuesChange = (values: any) => {
        const { type } = this.props;
        let newData: any = { ...this.state.copyData, ...values };
        if (values.hasOwnProperty('columnName')) {
            newData.functionId = null;
            newData.functionName = null;
            newData.isPercentage = false;
            newData.operator = null;
            newData.threshold = null;
        }
        if (values.hasOwnProperty('functionId')) {
            let f = this.getFunction().find((func: any) => {
                return func.id == values.functionId
            });
            newData.functionName = f.nameZc;
            newData.isPercentage = f.isPercent;
            if (type == 'column') {
                newData.operator = null;
                newData.threshold = null;
            }
        }
        this.setState({
            copyData: newData
        })
    }
    onCancel = (id: any) => {
        this.setState({
            copyData: this.props.data
        });
        this.props.onCancel(id);
    }
    onClone = () => {
        this.props.onClone(this.state.copyData);
    }
    render () {
        return (
            <RuleForm
                {...this.props}
                data={this.state.copyData}
                onValuesChange={this.onValuesChange}
                functionList={this.getFunction()}
                onCancel={this.onCancel}
                onClone={this.onClone}
            />
        )
    }
}
export default Rule
