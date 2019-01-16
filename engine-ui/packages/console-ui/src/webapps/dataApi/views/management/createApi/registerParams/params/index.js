import React from 'react';

import { Button, Tooltip, Icon } from 'antd';
import ConstColumnModel from '../../../../../model/constColumnModel';
import InputColumnModel from '../../../../../model/inputColumnModel';
import { resolveFormItemKey } from './helper';
import Card from '../card';
import ConstTable from './const';
import InputTable from './input';

class RegisterParams extends React.Component {
    initColumns () {
        return [{
            dataIndex: 'name',
            title: '参数名称'
        }]
    }
    newColumn (type) {
        let { data = {} } = this.props;
        let { inputColumn = [], constColumn = [] } = data;
        let newColumn;
        if (type == 'in') {
            newColumn = new InputColumnModel();
            inputColumn = [
                ...inputColumn,
                newColumn
            ]
        } else if (type == 'const') {
            newColumn = new ConstColumnModel();
            constColumn = [
                ...constColumn,
                newColumn
            ]
        }
        newColumn && this.props.updateData({
            inputColumn,
            constColumn
        });
    }
    updateColumnData (type, values) {
        const keyAndValue = Object.entries(values);
        let { data = {} } = this.props;
        let { inputColumn = [], constColumn = [] } = data;
        let columns;
        let UpdateColumnClass;
        if (type == 'in') {
            inputColumn = [...inputColumn];// shadow copy
            columns = inputColumn;
            UpdateColumnClass = InputColumnModel;
        } else if (type == 'const') {
            columns = constColumn;
            UpdateColumnClass = ConstColumnModel;
        }
        keyAndValue.forEach(([key, value]) => {
            const { id, name } = resolveFormItemKey(key);
            let targetIndex = columns.findIndex((column) => {
                return column.id == id;
            })
            if (targetIndex > -1) {
                columns[targetIndex] = new UpdateColumnClass({
                    ...columns[targetIndex],
                    [name]: value
                })
            }
        });
        this.props.updateData({
            inputColumn,
            constColumn
        });
    }
    render () {
        let { data = {} } = this.props;
        let { inputColumn = [], constColumn = [] } = data;
        return (
            <React.Fragment>
                <Card
                    title='输入参数'
                    extra={(
                        <Button type='primary' onClick={this.newColumn.bind(this, 'in')}>新增参数</Button>
                    )}
                >
                    <InputTable updateColumnData={this.updateColumnData.bind(this, 'in')} data={inputColumn} />
                </Card>
                <Card
                    style={{ marginTop: '40px' }}
                    title={<span>
                        常量参数
                        <Tooltip title="常量参数对用户不可见，用户调用API时无需传入，但后端服务始终接收列表中的常量参数及参数值。">
                            <Icon type="question-circle-o" />
                        </Tooltip>
                    </span>}
                    extra={(
                        <Button type='primary' onClick={this.newColumn.bind(this, 'const')}>新增参数</Button>
                    )}
                >
                    <ConstTable updateColumnData={this.updateColumnData.bind(this, 'const')} data={constColumn} />
                </Card>
            </React.Fragment>
        )
    }
}
export default RegisterParams;
