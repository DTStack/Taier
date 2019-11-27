import * as React from 'react';
import { Table, Popover } from 'antd';
import ConfigDictModal from './configDictModal';
import { isEqual } from 'lodash';
import EditInput from '../../../../components/editInput';
import { API } from '../../../../api/apiMap';
import './style.scss';

interface IProps {
    infor: any[];
    handleChange: any;
    baseInfor: any;
    attrTypeMap: any;
}

interface IState {
    dataSource: any[];
    configModalVisble: boolean;
    total: number;
    tagVals: any[];
    configItem: any;
}

let timer: any = null;

export default class AtomicLabel extends React.Component<IProps, IState> {
    state: IState = {
        dataSource: [],
        configModalVisble: false,
        total: 0,
        tagVals: [],
        configItem: {}
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

    componentWillUnmount () {
        if (timer) {
            clearTimeout(timer);
            timer = null;
        }
    }

    handleConfig = (item, index) => {
        this.setState({
            configModalVisble: true,
            configItem: {
                ...item,
                labelIndex: index
            }
        })
    }

    handleConfModelOk = (value) => {
        console.log('To Deal: ', value);
        // TODO 处理配置弹框中的数据
        this.handleConfModelCancel();
    }

    handleConfModelCancel = () => {
        this.setState({
            configModalVisble: false
        })
        timer = setTimeout(() => {
            this.setState({
                configItem: {}
            })
            clearTimeout(timer);
            timer = null;
        }, 100)
    }

    handleTableChange = (type: string, record: any, index: number, e: any) => {
        const data = [...this.props.infor];
        data[index] = {
            ...record,
            [type]: e.target.value
        }
        this.props.handleChange([...data]);
    }

    handleViewTagVals = (column, visible) => {
        if (visible) {
            this.getColumnVals(column);
        }
    }

    getColumnVals = (column) => {
        const { baseInfor } = this.props;
        API.getColumnVals({
            dataSourceId: baseInfor.dataSourceId,
            index: baseInfor.dataSourceTable,
            column
        }).then((res: any) => {
            const { data = [], code } = res;
            if (code === 1) {
                this.setState({
                    tagVals: data
                });
            }
        })
    }

    renderPopoverContent = () => {
        const { tagVals } = this.state;
        let dataSource = tagVals.map((item, index) => {
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
        const { attrTypeMap = {} } = this.props;
        return [{
            title: '标签名称',
            dataIndex: 'tagName',
            key: 'tagName',
            width: 200,
            render: (text: any, record: any, index: number) => {
                return <EditInput
                    onChange={this.handleTableChange.bind(this, 'tagName', record, index)}
                    value={text}
                    style={{ width: 150 }}
                />
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
            width: 120,
            render: (text: any) => {
                return attrTypeMap[text];
            }
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
                let realContent = this.renderPopoverContent();
                return (
                    <Popover overlayClassName="label-detail-content" onVisibleChange={this.handleViewTagVals.bind(this, record.entityAttr)} placement="rightTop" title={null} content={realContent} trigger="click">
                        <a>预览</a>
                    </Popover>
                );
            }
        }, {
            title: '配置字典',
            dataIndex: 'config',
            key: 'config',
            width: 120,
            render: (text: any, record: any, index: number) => {
                return record.type == 'time' ? '' : <a onClick={this.handleConfig.bind(this, record, index)}>配置字典</a>
            }
        }, {
            title: '标签描述',
            dataIndex: 'tagDesc',
            key: 'tagDesc',
            width: 200,
            render: (text: any, record: any, index: number) => {
                return <EditInput
                    onChange={this.handleTableChange.bind(this, 'tagDesc', record, index)}
                    value={text}
                    style={{ width: 150 }}
                />
            }
        }];
    }

    render () {
        const { dataSource, configModalVisble, total, configItem } = this.state;
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
                    infor={configItem}
                    onOk={this.handleConfModelOk}
                    onCancel={this.handleConfModelCancel}
                />
            </div>
        )
    }
}
