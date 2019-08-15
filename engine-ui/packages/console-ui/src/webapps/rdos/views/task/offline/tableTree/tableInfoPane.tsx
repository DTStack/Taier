import * as React from 'react';

import {
    Table, Tabs, Icon, Input
} from 'antd';

import ajax from '../../../../api/dataManage';
import TableCell from 'widgets/tableCell'
import TablePartition from '../../../dataManage/tablePartition';
import { TABLE_TYPE } from '../../../../comm/const'
const TabPane = Tabs.TabPane;
var COLUMNS: any = []; // columns不变量
export default class TableInfoPane extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            tableData: '',
            previewData: '',
            tabKey: '',
            filterDropdownVisible: false,
            searchText: '',
            column: []
        };
    }
    previewCols: any;
    searchInput: any;
    componentDidMount () {
        const tbId = this.props.tableId
        if (tbId) {
            this.getTable(tbId)
        }
    }

    /* eslint-disable */
    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
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

    getTable(tableId: any) {
        if (!tableId) return;
        ajax.getTable({ tableId }).then((res: any) => {
            if (res.code === 1) {
                const data = res.data;
                const { column = [] } = data;
                COLUMNS = column;
                this.setState({
                    tableData: data,
                    column: column
                });
            }
        });
    }
    getPreview (key: any, tableId: any) {
        if (+key === 3) {
            ajax.previewTable({ tableId: tableId || this.props.tableId }).then((res: any) => {
                if (res.code === 1 && res.data) {
                    this.setState({
                        tabKey: key,
                        previewData: this.formatPreviewData(res.data)
                    });
                }
            });
        }
    }

    formatPreviewData(arr: any) {
        const cols = arr.shift();

        this.previewCols = cols;
        return arr.map((keyArr: any) => {
            let o: any = {};
            for (let i = 0; i < keyArr.length; i++) {
                o[cols[i]] = keyArr[i]
            }
            return o
        });
    }
    changeInputText = (e: any) => {
        this.setState({ searchText: e.target.value })
    }
    filterColumnByName = () => {
        const { searchText } = this.state;
        const reg = new RegExp(searchText, 'gi');
        this.setState({
            filterDropdownVisible: false,
            column: COLUMNS.map((record: any) => {
                const match = record.columnName.match(reg);
                if (!match) {
                    return null;
                }
                return {
                    ...record,
                    columnName: (
                        <span>
                            {record.columnName.split(reg).map((text: any, i: any) => (
                                i > 0 ? [<span key={`${i}`} style={{ color: '#f50' }}>{match[0]}</span>, text] : text
                            ))}
                        </span>
                    ),
                };
            }).filter((record: any) => !!record),
        })
    }

    initColums = () => {
        return [{
            title: '字段名称',
            dataIndex: 'columnName',
            key: 'columnName',
            render (text: any, record: any) {
                const isDensen = record.needMask === 0; // 是否脱敏字段(0脱敏，1未脱敏)
                return isDensen ? `${text}(脱敏)` : text
            },
            filterIcon: <Icon type="search" />,
            filterDropdownVisible: this.state.filterDropdownVisible,
            filterDropdown: (
                <div className="custom-filter-dropdown">
                    <Input
                        ref={(ele: any) => this.searchInput = ele}
                        value={this.state.searchText}
                        placeholder="搜索字段"
                        onChange={this.changeInputText}
                        onPressEnter={this.filterColumnByName}
                    />
                </div>
            ),
            onFilterDropdownVisibleChange: (visible: any) => {
                this.setState({
                    filterDropdownVisible: visible
                }, () => this.searchInput && this.searchInput.focus());
            }
        }, {
            title: '类型',
            dataIndex: 'columnType',
            key: 'columnType'
        }, {
            title: '注释',
            dataIndex: 'comment',
            key: 'comment',
            render(text: any) {
                return text
            }
        }];
    }
    getScrollX(previewCols: any) {
        let l = 500;
        for (let str of previewCols) {
            l = 20 + l + str.length * 10;
        }
        return Math.max(l, 600);
    }
    render () {
        const { tableData, previewData } = this.state;
        const isHiveTable = tableData && tableData.table.tableType == TABLE_TYPE.HIVE;
        const pagination: any = { simple: true, size: 'small' }
        return <div className="g-tableviewer">
            <Tabs
                type="line"
                size="small"
                animated={false}
                onChange={this.getPreview.bind(this)}
            >
                <TabPane tab="字段信息" key="1">
                    <div className="box">
                        <Table
                            className="dt-ant-table dt-ant-table--border"
                            rowKey="id"
                            columns={this.initColums()}
                            dataSource={this.state.column}
                            pagination={pagination}
                        />
                    </div>
                </TabPane>
                {
                    isHiveTable && (
                        <TabPane tab="分区信息" key="2">
                            <TablePartition
                                pagination={pagination}
                                table={tableData && tableData.table}
                            />
                        </TabPane>
                    )
                }
                <TabPane tab="数据预览" key="3">
                    <div className="box">
                        {previewData ? <Table
                            className="dt-ant-table dt-ant-table--border"
                            columns={this.previewCols.map((str: any) => ({
                                width: 20 + str.length * 10,
                                title: str,
                                dataIndex: str,
                                key: str,
                                render(text: any) {
                                    return <TableCell style={{ minWidth: 80 }} value={text} />
                                }
                            }))}
                            rowKey="key"
                            pagination={pagination}
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
