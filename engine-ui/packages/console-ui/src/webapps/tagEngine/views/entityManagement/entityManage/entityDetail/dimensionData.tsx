import * as React from 'react';
// import EditCell from '../../../../components/editCell';
import EllipsisText from '../../../../components/ellipsisText';
import { API } from '../../../../api/apiMap';

import { Card, Table, Input, message as Message } from 'antd';
import './style.scss';
import { isEmpty } from 'lodash';

interface IProps {
    infor: any;
    regetData: any;
}

interface IState {
    currentItem: any;
    editInputVal: string;
    attrTypeMap: any;
}

export default class DimensionData extends React.Component<IProps, IState> {
    state: IState = {
        currentItem: {},
        editInputVal: '',
        attrTypeMap: {}
    }

    componentDidMount () {
        this.getLabelType();
    }

    getLabelType = () => {
        API.getLabelType().then((res: any) => {
            const { data = [], code } = res;
            if (code === 1) {
                this.setState({
                    attrTypeMap: data.reduce((pre, curr) => {
                        return {
                            ...pre,
                            [curr.val]: curr.desc
                        }
                    }, {})
                });
            }
        })
    }

    handleCNChange = () => {

    }

    setEditItem = (item) => {
        this.setState({
            currentItem: item,
            editInputVal: item.entityAttrCn
        })
    }

    onChangeEdit = (e: any) => {
        const value = e.target.value;
        this.setState({
            editInputVal: value ? value.slice(0, 20) : ''
        });
    };

    onOkEdit = () => {
        const { editInputVal, currentItem } = this.state;
        API.entityAttrsEdit({
            attrId: currentItem.id,
            entityAttrCn: editInputVal
        }).then((res: any) => {
            const { code } = res;
            if (code === 1) {
                Message.success('修改成功！');
                this.onCancelEdit();
                this.props.regetData();
            }
        })
    };

    onCancelEdit = () => {
        this.setState({
            editInputVal: '',
            currentItem: {}
        });
    };

    initColumns = () => {
        const { currentItem, editInputVal, attrTypeMap } = this.state;
        return [{
            title: '维度名称',
            dataIndex: 'entityAttr',
            key: 'entityAttr',
            width: 200,
            render: (text: any, record: any) => {
                return (
                    <div className="di-table-name-col">
                        <div className="tag-box">
                            {record.isPrimaryKey ? <a style={{ cursor: 'default' }}><i className='iconfont iconicon_key'></i></a> : null}
                        </div>
                        <span>{text}</span>
                        {record.isPrimaryKey ? '(主键)' : ''}
                    </div>
                )
            }
        }, {
            title: '中文名',
            dataIndex: 'entityAttrCn',
            key: 'entityAttrCn',
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
            dataIndex: 'dataType',
            key: 'dataType',
            width: 200,
            render: (text: any) => {
                return attrTypeMap[text];
            }
        }, {
            title: '属性值数量',
            dataIndex: 'tagValueCount',
            key: 'tagValueCount',
            width: 150
        }, {
            title: '多值列',
            dataIndex: 'isMultipleValue',
            key: 'isMultipleValue',
            width: 150,
            render: (text: any) => {
                return text ? '是' : '否'
            }
        }, {
            title: '关联原子标签',
            dataIndex: 'isAtomTag',
            key: 'isAtomTag',
            filters: [
                { text: '否', value: '0' },
                { text: '是', value: '1' }
            ],
            filterMultiple: false,
            onFilter: (value: string, record: any) => {
                return record.isAtomTag == value;
            },
            render: (text: any) => {
                return text ? '是' : '否'
            }
        }];
    }

    render () {
        const { infor: { propertyData = [] } } = this.props;

        return (
            <div className="ed-dimension-data shadow">
                <Card
                    noHovering
                    bordered={false}
                    className="noBorderBottom"
                >
                    <div className="total-count-box">
                        <span>共计&nbsp;{propertyData.length}个&nbsp;数据维度</span>
                    </div>
                    <Table
                        rowKey="id"
                        className="dt-ant-table--border"
                        pagination={false}
                        scroll={{ y: 400 }}
                        columns={this.initColumns()}
                        dataSource={propertyData}
                    />
                </Card>
            </div>
        )
    }
}
