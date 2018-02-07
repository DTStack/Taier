import React from 'react';
import { connect } from 'react-redux';
import moment from 'moment';
import { cloneDeep } from 'lodash';

import { 
    Row, Col, Table, Button,
    Tabs, Radio, Icon, Input,
} from 'antd';

import ajax from '../../../../api';
import TablePartition from '../../../dataManage/tablePartition';

const TabPane = Tabs.TabPane;
const RadioButton = Radio.Button;
const RadioGroup = Radio.Group

export default class TableInfoPane extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            tableData: '',
            previewData: '',
            tabKey: '',
            filterDropdownVisible: false,
        };
    }

    componentDidMount() {
        const tbId = this.props.tableId
        if (tbId) {
            this.getTable(tbId)
        }
    }

    componentWillReceiveProps(nextProps) {
        const tbId = nextProps.tableId
        if (tbId && tbId !== this.props.tableId ) {
            this.getTable(tbId)
            this.getPreview(this.state.tabKey, tbId)
        }
    }

    reset = () => {
        this.getTable(this.props.tableId)
    }

    getTable(tableId) {
        if (!tableId) return;
        ajax.getTable({ tableId }).then(res => {
            if (res.code === 1) {
                this.setState({
                    tableData: res.data
                });
            }
        });
    }

    getPreview(key, tableId) {
        if (+key === 2 || +key === 3) {
            ajax.previewTable({ tableId: tableId || this.props.tableId }).then(res => {
                if (res.code === 1 && res.data) {
                    this.setState({
                        tabKey: key,
                        previewData: this.formatPreviewData(res.data)
                    });
                }
            });
        }
    }

    formatPreviewData(arr) {
        const cols = arr.shift();

        this.previewCols = cols;
        return arr.map(keyArr => {
            let o = {};
            for (let i = 0; i < keyArr.length; i++) {
                o[cols[i]] = keyArr[i]
            }
            return o
        });
    }

    filterColumnByName = (e) => {
        const searchText = e.target.value
        const tableData = cloneDeep(this.state.tableData)
        const tableColms = [...tableData.column]
        if (searchText) {
            const reg = new RegExp(searchText, 'gi');
            const filteredTables = tableColms.length > 0 && tableColms.filter(col =>{
                return col.name.match(reg);
            })
            if (filteredTables.length > 0) {
                tableData.column = filteredTables
            }
            this.setState({ tableData })
        }
    }

    initColums = () => {
        return [{
            title: '字段名称',
            dataIndex: 'name',
            key: 'name',
            filterIcon: <Icon type="search" />,
            filterDropdownVisible: this.state.filterDropdownVisible,
            filterDropdown:(
                <div className="custom-filter-dropdown">
                    <Input
                        ref={ele => this.searchInput = ele}
                        placeholder="搜索字段"
                        onChange={this.filterColumnByName}
                    />
                </div>
            ),
            onFilterDropdownVisibleChange: (visible) => {
                this.setState({
                    filterDropdownVisible: visible,
                }, () => this.searchInput.focus());
                if (!visible) {
                    this.reset();
                }
            },
        }, {
            title: '类型',
            dataIndex: 'type',
            key: 'type'
        }, {
            title: '注释',
            dataIndex: 'comment',
            key: 'comment',
            render(text) {
                return text
            }
        }];
    }

    render() {
        const { tableData, previewData } = this.state;

        return <div className="g-tableviewer">
            <Tabs 
                type="inline"
                size="small"
                animated={false}
                onChange={this.getPreview.bind(this)}
            >
                <TabPane tab="字段信息" key="1">
                    <div className="box">
                        <Table
                            rowKey="id"
                            columns={this.initColums()}
                            dataSource={tableData.column}
                        />
                    </div>
                </TabPane>
                <TabPane tab="分区信息" key="2">
                    <TablePartition table={tableData && tableData.table} />
                </TabPane>
                <TabPane tab="数据预览" key="3">
                    <div className="box">
                        {previewData ? <Table
                            columns={this.previewCols.map(str => ({
                                title: str,
                                dataIndex: str,
                                key: str
                            }))}
                            rowKey="key"
                            dataSource={previewData}
                            scroll={{ x: 260 }}
                        />
                            :
                            <p style={{
                                marginTop: 20,
                                textAlign: 'center',
                                fontSize: 36,
                                color: '#ddd'
                            }}><Icon type="exclamation-circle-o" /> 此表中没有字段信息 </p>
                        }
                    </div>
                </TabPane>
            </Tabs>
        </div>
    }
}