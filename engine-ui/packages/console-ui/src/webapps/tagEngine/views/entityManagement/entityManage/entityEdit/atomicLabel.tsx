import * as React from 'react';
import { Input, Table } from 'antd';
import ConfigDictModal from './configDictModal';
import PreviewModal from './previewModal';
import { isEqual } from 'lodash';
// import EditCell from '../../../../components/editCell';
import './style.scss';

interface Iprops {
    infor: any[];
    handleChange: any;
}

interface IState {
    dataSource: any[];
    configModalVisble: boolean;
    previewModalVisible: boolean;
    total: number;
}

export default class AtomicLabel extends React.Component<Iprops, IState> {
    state: IState = {
        dataSource: [],
        configModalVisble: false,
        previewModalVisible: false,
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

    handlePreview = () => {
        this.setState({
            previewModalVisible: true
        })
    }

    handlePrevModelOk = () => {
        this.setState({
            previewModalVisible: false
        })
    }

    handlePrevModelCancel = () => {
        this.setState({
            previewModalVisible: false
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

    initColumns = () => {
        return [{
            title: '标签名称',
            dataIndex: 'labelName',
            key: 'labelName',
            width: 250,
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
            width: 200
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
            width: 120,
            render: (text: any, record: any) => {
                return <a onClick={this.handlePreview}>预览</a>
            }
        }, {
            title: '配置字典',
            dataIndex: 'config',
            key: 'config',
            width: 120,
            render: (text: any, record: any) => {
                return <a onClick={this.handleConfig}>配置字典</a>
            }
        }, {
            title: '标签描述',
            dataIndex: 'desc',
            key: 'desc',
            // width: 200,
            render: (text: any, record: any, index: number) => {
                return (<Input style={{ width: 150 }} value={text} onChange={this.handleTableChange.bind(this, 'desc', record, index)} />)
            }
        }];
    }

    render () {
        const { dataSource, configModalVisble, previewModalVisible, total } = this.state;
        return (
            <div className="atomic-label">
                <div className="top-box">
                    <div>
                        <span>共计{total}个原子标签</span>
                    </div>
                </div>
                <Table
                    rowKey="id"
                    className="dt-ant-table dt-ant-table--border"
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
                <PreviewModal
                    infor={'1,3,4,5,6,...'}
                    visible={previewModalVisible}
                    onOk={this.handlePrevModelOk}
                    onCancel={this.handlePrevModelCancel}
                />
            </div>
        )
    }
}
