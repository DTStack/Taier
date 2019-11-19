import * as React from 'react';
import { Table, Icon, message } from 'antd';
import EditInput from '../editInput/index';
import shortid from 'shortid';
import { get } from 'lodash';
import './style.scss';
interface IProps {
    isEdit: boolean;
    value?: any[];
    onChange?: Function;
}

export default class SetDictionary extends React.PureComponent<IProps, any> {
    constructor (props: IProps) {
        super(props);
    }
    input: any
    tableNode: any
    state: any = {

    }

    onAdd = () => {
        const { value = [] } = this.props;
        let len = get(value, 'length');
        if (len && len > 500) {
            message.warning('字典数量最大不超过500！');
        } else {
            this.props.onChange([...value, { name: '', value: '', key: shortid() }]);
        }
    }
    setBottom = () => {
        const tableScroll = document.querySelector('.set-dictionary-table .ant-table-body');
        if (tableScroll) {
            tableScroll.scrollTop = tableScroll.scrollHeight
        }
    }
    componentDidUpdate (prePorps, preState) {
        if (prePorps.value != this.props.value) {
            this.setBottom();
        }
    }
    onClose = (index: number) => {
        const { value = [] } = this.props;
        const newValue = [...value]
        newValue.splice(index, 1);
        this.props.onChange(newValue);
    }
    onChangeInput = (e: any, labelName: string, index: number) => {
        const { value = [] } = this.props;
        value[index][labelName] = e.target.value;
        this.props.onChange(value);
    }
    render () {
        const { isEdit, value = [] } = this.props;
        const columns = [
            {
                title: '字典值',
                dataIndex: 'value',
                key: 'value',
                width: 120,
                render: (value: string, record: any, index: number) => {
                    return isEdit ? <div className="input_wrap"><EditInput key={index} className="input" onChange={(e: any) => this.onChangeInput(e, 'value', index)} value={value} /></div> : value
                }
            },
            {
                title: '名称',
                dataIndex: 'name',
                key: 'name',
                width: 120,
                render: (value: string, record: any, index: number) => {
                    return isEdit ? <div className="input_wrap"><EditInput key={index} className="input" onChange={(e: any) => this.onChangeInput(e, 'name', index)} value={value} /> <Icon onClick={() => this.onClose(index)} className="close" type="close" /></div> : value
                }
            }
        ];
        return (
            <div className="set-dictionary">
                <Table size="middle" scroll={{ y: 200 }} dataSource={value || []} ref={(node) => this.tableNode = node} className="set-dictionary-table" bordered columns={columns} locale={{ 'emptyText': '数据为空' }} pagination={false} />
                {
                    isEdit && <div className="add_dictionary"><Icon className="plus" onClick={this.onAdd} type="plus" /></div>
                }
            </div>
        )
    }
}
