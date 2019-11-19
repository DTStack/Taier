import * as React from 'react';
import { Input, Table, Popover } from 'antd';
import ConfigDictModal from './configDictModal';
import { isEqual } from 'lodash';
// import EditCell from '../../../../components/editCell';
import './style.scss';

interface IProps {
    infor: any[];
    handleChange: any;
}

interface IState {
    dataSource: any[];
    configModalVisble: boolean;
    total: number;
}

export default class AtomicLabel extends React.Component<IProps, IState> {
    state: IState = {
        dataSource: [],
        configModalVisble: false,
        total: 0
    }

    componentDidMount () {
        const { infor } = this.props;
        this.setState({
            dataSource: infor,
            total: infor.length
        })
    }

    componentDidUpdate (preProps: any) {
        const { infor } = this.props;
        if (!isEqual(infor, preProps.infor)) {
            this.setState({
                dataSource: infor,
                total: infor.length
            })
        }
    }

    handleConfig = () => {
        this.setState({
            configModalVisble: true
        })
    }

    handleConfModelOk = () => {
        this.setState({
            configModalVisble: false
        })
    }

    handleConfModelCancel = () => {
        this.setState({
            configModalVisble: false
        })
    }

    handleTableChange = (type: string, record: any, index: number, e: any) => {
        const data = [...this.props.infor];
        data[index] = {
            ...record,
            [type]: e.target.value
        }
        this.props.handleChange([...data]);
    }

    renderPopoverContent = (infor) => {
        let dataSource = infor.map((item, index) => {
            return {
                value: item,
                index
            }
        })
        let columns = [
            {
                title: '部分标签值',
                dataIndex: 'value',
                key: 'value'
            }
        ];
        return (
            <Table
                rowKey="index"
                pagination={false}
                loading={false}
                columns={columns}
                scroll={{ y: 250, x: 120 }}
                dataSource={dataSource}
            />
        )
    }

    initColumns = () => {
        return [{
            title: '标签名称',
            dataIndex: 'labelName',
            key: 'labelName',
            width: 200,
            render: (text: any, record: any, index: number) => {
                // return <EditCell
                //     keyField="labelName"
                //     isView={false}
                //     onHandleEdit={this.handleLabelNameEdit}
                //     value={text || ''}
                // />
                return (<Input style={{ width: 150 }} value={text} onChange={this.handleTableChange.bind(this, 'labelName', record, index)} />)
            }
        }, {
            title: '对应维度',
            dataIndex: 'dimensionName',
            key: 'dimensionName',
            width: 150
        }, {
            title: '数据类型',
            dataIndex: 'type',
            key: 'type',
            width: 120
        }, {
            title: '标签值数量',
            dataIndex: 'labelNum',
            key: 'labelNum',
            width: 120
        }, {
            title: '标签值详情',
            dataIndex: 'labelDetail',
            key: 'labelDetail',
            render: (text: any, record: any) => {
                let labelVal = [111, 222, 333, 444, 666, 777, 888, 99999];
                let realContent = this.renderPopoverContent(labelVal);
                return (
                    <Popover overlayClassName="label-detail-content" placement="rightTop" title={null} content={realContent} trigger="click">
                        <a>预览</a>
                    </Popover>
                );
            }
        }, {
            title: '配置字典',
            dataIndex: 'config',
            key: 'config',
            width: 120,
            render: (text: any, record: any) => {
                return record.type == 'time' ? '' : <a onClick={this.handleConfig}>配置字典</a>
            }
        }, {
            title: '标签描述',
            dataIndex: 'desc',
            key: 'desc',
            width: 200,
            render: (text: any, record: any, index: number) => {
                return (<Input style={{ width: 150 }} value={text} onChange={this.handleTableChange.bind(this, 'desc', record, index)} />)
            }
        }];
    }

    render () {
        const { dataSource, configModalVisble, total } = this.state;
        return (
            <div className="atomic-label">
                <div className="top-box">
                    <div>
                        <span>共计{total}个原子标签</span>
                    </div>
                </div>
                <Table
                    rowKey="id"
                    className="al-table-border"
                    pagination={false}
                    loading={false}
                    columns={this.initColumns()}
                    scroll={{ y: 400 }}
                    dataSource={dataSource}
                />
                <ConfigDictModal
                    visible={configModalVisble}
                    isLabel={true}
                    onOk={this.handleConfModelOk}
                    onCancel={this.handleConfModelCancel}
                />
            </div>
        )
    }
}
