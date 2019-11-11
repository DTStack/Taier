import * as React from 'react';
import { Input, Table } from 'antd';
import ConfigDictModal from './configDictModal';
import PreviewModal from './previewModal';
import EditCell from '../../../../components/editCell';

export default class AtomicLabel extends React.Component<any, any> {
    state: any = {
        dataSource: [
            { labelName: 'xxxxxxx', dimensionName: 'xxx1', type: 'char', labelNum: 200, desc: 'xxxxxxxxxxxxxx' },
            { labelName: 'xxxxx', dimensionName: 'xxx2', type: 'number', labelNum: 100, desc: 'xxxxxxxxxxxxxx' },
            { labelName: 'xxxxx', dimensionName: 'xxx3', type: 'number', labelNum: 30, desc: 'xxxxxxxxxxxxxx' }
        ],
        configModalVisble: false,
        previewModalVisible: false

    }

    componentDidMount () {

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

    handleDescChange = (record: any, e: any) => {
        // TODO 需要限制字数不超过20
    }

    handleLabelNameEdit = (key: any, value: any) => {

    }

    initColumns = () => {
        return [{
            title: '标签名称',
            dataIndex: 'labelName',
            key: 'labelName',
            width: 250,
            render: (text: any, record: any) => {
                return <EditCell
                    keyField="labelName"
                    isView={false}
                    onHandleEdit={this.handleLabelNameEdit}
                    value={text || ''}
                />
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
            render: (text: any, record: any) => {
                return (<Input style={{ width: 150 }} value={text} onChange={this.handleDescChange.bind(this, record)} />)
            }
        }];
    }

    render () {
        const { dataSource, configModalVisble, previewModalVisible } = this.state;
        return (
            <div className="atomic-label">
                <div>
                    <div>
                        <span>共计7个原子标签</span>
                    </div>
                </div>
                <Table
                    rowKey="id"
                    className="dt-ant-table dt-ant-table--border full-screen-table-47"
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
