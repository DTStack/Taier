import * as React from 'react';
import { Table, Popover } from 'antd';
import ConfigDictModal from './configDictModal';
import { get } from 'lodash';
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
    configModalVisble: boolean;
    tagVals: any[];
    configItem: any;
    configModalKey: number;
}

let timer: any = null;

export default class AtomicLabel extends React.Component<IProps, IState> {
    state: IState = {
        configModalVisble: false,
        tagVals: [],
        configItem: {},
        configModalKey: +new Date()
    }

    componentDidMount () {

    }

    componentDidUpdate (preProps: any) {

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
            },
            configModalKey: +new Date()
        })
    }

    handleConfModelOk = (value) => {
        const { configItem } = this.state;
        const { infor } = this.props;
        let newItem = { ...infor[configItem.labelIndex] };
        if (value.way == 'auto') {
            newItem.tagDictParam = {
                name: value.dictSetName,
                dictValueParamList: value.dictSetRule ? value.dictSetRule.map(item => {
                    return { value: item.value, valueName: item.name };
                }) : []
            }
        } else {
            newItem.tagDictId = value.dictRef;
        }
        infor[configItem.labelIndex] = {
            ...newItem
        }
        this.props.handleChange([...infor]);
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
                return record.type == 3 ? '' : <a onClick={this.handleConfig.bind(this, record, index)}>配置字典</a>
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
        const { configModalVisble, configItem, configModalKey } = this.state;
        const { infor } = this.props;
        return (
            <div className="atomic-label">
                <div className="top-box">
                    <div>
                        <span>共计{get(infor, 'length') || 0}个原子标签</span>
                    </div>
                </div>
                <Table
                    rowKey="entityAttr"
                    className="al-table-border"
                    pagination={false}
                    loading={false}
                    columns={this.initColumns()}
                    scroll={{ y: 400 }}
                    dataSource={infor || []}
                />
                <ConfigDictModal
                    visible={configModalVisble}
                    isLabel={true}
                    key={configModalKey}
                    configItem={configItem}
                    onOk={this.handleConfModelOk}
                    onCancel={this.handleConfModelCancel}
                />
            </div>
        )
    }
}
