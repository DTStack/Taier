import * as React from 'react';
// import EditCell from '../../../../components/editCell';
import EllipsisText from '../../../../components/ellipsisText';

import { Card, Table, Input } from 'antd';
import './style.scss';
import { isEmpty } from 'lodash';

interface IProps {
    dataSource: any;
}

interface IState {
    currentItem: any;
    editInputVal: string;
}

export default class DimensionData extends React.Component<IProps, IState> {
    state: IState = {
        currentItem: {},
        editInputVal: ''
    }

    componentDidMount () {

    }

    handleCNChange = () => {

    }

    setEditItem = (item) => {
        this.setState({
            currentItem: item,
            editInputVal: item.chName
        })
    }

    onChangeEdit = (e: any) => {
        const value = e.target.value;
        this.setState({
            editInputVal: value ? value.slice(0, 20) : ''
        });
    };

    onOkEdit = () => {
        const { editInputVal } = this.state;
        console.log('editInputVal', editInputVal);
        // TODO 更新数据维度中文名
        this.onCancelEdit();
    };

    onCancelEdit = () => {
        this.setState({
            editInputVal: '',
            currentItem: {}
        });
    };

    initColumns = () => {
        const { currentItem, editInputVal } = this.state;
        return [{
            title: '维度名称',
            dataIndex: 'name',
            key: 'name',
            width: 200,
            render: (text: any, record: any) => {
                return (
                    <div className="di-table-name-col">
                        <div className="tag-box">
                            {record.isKey ? <a style={{ cursor: 'default' }}><i className='iconfont iconicon_key'></i></a> : null}
                        </div>
                        <span>{text}</span>
                        {record.isKey ? '(主键)' : ''}
                    </div>
                )
            }
        }, {
            title: '中文名',
            dataIndex: 'chName',
            key: 'chName',
            width: 300,
            render: (text: any, record: any) => {
                return <div className="dd-edit-cell">
                    {!isEmpty(currentItem) && currentItem.id == record.id ? (
                        <div className="edit_input_row">
                            <Input
                                value={editInputVal}
                                className="input"
                                style={{ width: 150, lineHeight: 24, height: 24 }}
                                onChange={this.onChangeEdit}
                            />
                            <a onClick={this.onOkEdit}>完成</a>
                            <a onClick={this.onCancelEdit}>取消</a>
                        </div>
                    ) : (
                        <React.Fragment>
                            <EllipsisText value={text} />
                            <a onClick={this.setEditItem.bind(this, record)}>修改</a>
                        </React.Fragment>
                    )}
                </div>
            }
        }, {
            title: '数据类型',
            dataIndex: 'type',
            key: 'type',
            width: 200
        }, {
            title: '属性值数量',
            dataIndex: 'propertyNum',
            key: 'propertyNum',
            width: 150
        }, {
            title: '多值列',
            dataIndex: 'isMultiply',
            key: 'isMultiply',
            width: 150
        }, {
            title: '关联原子标签',
            dataIndex: 'isRelateLabel',
            key: 'isRelateLabel',
            filters: [
                { text: '否', value: '否' },
                { text: '是', value: '是' }
            ],
            filterMultiple: false,
            onFilter: (value: string, record: any) => {
                return record.isRelateLabel == value;
            }
        }];
    }

    render () {
        const { dataSource = [] } = this.props;

        return (
            <div className="ed-dimension-data shadow">
                <Card
                    noHovering
                    bordered={false}
                    className="noBorderBottom"
                >
                    <div className="total-count-box">
                        <span>共计&nbsp;{dataSource.length}个&nbsp;数据维度</span>
                    </div>
                    <Table
                        rowKey="id"
                        className="dt-ant-table--border"
                        pagination={false}
                        scroll={{ y: 400 }}
                        columns={this.initColumns()}
                        dataSource={dataSource}
                    />
                </Card>
            </div>
        )
    }
}
