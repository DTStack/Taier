import React from 'react';
import { cloneDeep } from 'lodash';

import {
    Table, Tabs, Icon, Input
} from 'antd';

import ajax from '../../../../api/dataManage';
import TableCell from 'widgets/tableCell'
import TablePartition from '../../../dataManage/tablePartition';
import { TABLE_TYPE } from '../../../../comm/const'
const TabPane = Tabs.TabPane;

export default class TableInfoPane extends React.Component {
    constructor (props) {
        super(props);
        this.state = {
            tableData: '',
            previewData: '',
            tabKey: '',
            filterDropdownVisible: false
        };
    }

    componentDidMount () {
        const tbId = this.props.tableId
        if (tbId) {
            this.getTable(tbId)
        }
    }

    /* eslint-disable */
    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps (nextProps) {
        const tbId = nextProps.tableId
        if (tbId && tbId !== this.props.tableId) {
            this.getTable(tbId)
            this.getPreview(this.state.tabKey, tbId)
        }
    }
    /* eslint-disable */

    reset = () => {
        this.getTable(this.props.tableId)
    }

    getTable (tableId) {
        if (!tableId) return;
        ajax.getTable({ tableId }).then(res => {
            if (res.code === 1) {
                this.setState({
                    tableData: res.data
                });
            }
        });
    }

    getPreview (key, tableId) {
        if (+key === 3) {
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

    formatPreviewData (arr) {
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
            const filteredTables = tableColms.length > 0 && tableColms.filter(col => {
                return col.columnName.match(reg);
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
            dataIndex: 'columnName',
            key: 'columnName',
            render (text, record) {
                const isDensen = record.needMask === 0; // 是否脱敏字段(0脱敏，1未脱敏)
                return isDensen ? `${text}(脱敏)` : text
            },
            filterIcon: <Icon type="search" />,
            filterDropdownVisible: this.state.filterDropdownVisible,
            filterDropdown: (
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
                    filterDropdownVisible: visible
                }, () => this.searchInput.focus());
                if (!visible) {
                    this.reset();
                }
            }
        }, {
            title: '类型',
            dataIndex: 'columnType',
            key: 'columnType'
        }, {
            title: '注释',
            dataIndex: 'columnDesc',
            key: 'columnDesc',
            render (text) {
                return text
            }
        }];
    }
    getScrollX (previewCols) {
        let l = 500;
        for (let str of previewCols) {
            l = 20 + l + str.length * 10;
        }
        return Math.max(l, 600);
    }
    render () {
        const { tableData, previewData } = this.state;
        const isHiveTable = tableData && tableData.table.tableType == TABLE_TYPE.HIVE;
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
                            rowKey="columnName"
                            columns={this.initColums()}
                            dataSource={tableData.column}
                            pagination={{ simple: true, size: 'small' }}
                        />
                    </div>
                </TabPane>
                {
                    isHiveTable && (
                        <TabPane tab="分区信息" key="2">
                            <TablePartition
                                pagination={{ simple: true, size: 'small' }}
                                table={tableData && tableData.table}
                            />
                        </TabPane>
                    )
                }
                <TabPane tab="数据预览" key="3">
                    <div className="box">
                        {previewData ? <Table
                            columns={this.previewCols.map(str => ({
                                width: 20 + str.length * 10,
                                title: str,
                                dataIndex: str,
                                key: str,
                                render (text) {
                                    return <TableCell style={{ minWidth: 80 }} value={text} />
                                }
                            }))}
                            rowKey="key"
                            pagination={{ simple: true, size: 'small' }}
                            dataSource={previewData}
                            scroll={{ x: this.getScrollX(this.previewCols) }}
                        />
                            : <p style={{
                                marginTop: 20,
                                textAlign: 'center',
                                fontSize: 12,
                                color: '#ddd'
                            }}><Icon type="exclamation-circle-o" /> 此表中没有数据 </p>
                        }
                    </div>
                </TabPane>
            </Tabs>
        </div>
    }
}
