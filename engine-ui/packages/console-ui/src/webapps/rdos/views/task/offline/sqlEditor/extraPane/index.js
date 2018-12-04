import React from 'react';

import { Input, Spin, Popover } from 'antd';

import TextMark from 'widgets/textMark';
import TableDetail from './tableDetail/index.js';

const Search = Input.Search;

class TableTipExtraPane extends React.Component {
    state = {
        searchValue: undefined,
        visibleMap: {},
        hideList: []
    }
    resolveTableName (tableName) {
        const { tabId } = this.props;
        return tableName + '%' + tabId;
    }
    changeVisible (tableName) {
        const { hideList } = this.state;
        let cloneHideList = [...hideList];
        tableName = this.resolveTableName(tableName);
        const tableIndex = hideList.indexOf(tableName);
        let isHide = tableIndex > -1;
        if (isHide) {
            cloneHideList[tableIndex] = undefined;
            cloneHideList = cloneHideList.filter(Boolean);
            this.setState({
                hideList: cloneHideList
            })
        } else {
            cloneHideList.push(tableName)
            this.setState({
                hideList: cloneHideList
            })
        }
    }
    isShow (tableName) {
        const { hideList } = this.state;
        tableName = this.resolveTableName(tableName);
        const tableIndex = hideList.indexOf(tableName);
        return tableIndex == -1;
    }
    renderTableItem (tableName, columns) {
        const { visibleMap, searchValue } = this.state;
        return <section className="tablePane-table-box">
            <div className="tablePane-table-title">
                <Popover
                    trigger="click"
                    placement="leftBottom"
                    content={<TableDetail
                        close={this.closeTableDetail.bind(this)}
                        tableName={tableName}
                        columns={columns}
                    />}
                    visible={visibleMap[tableName]}
                    onVisibleChange={
                        (value) => {
                            this.setState({
                                visibleMap: {
                                    ...visibleMap,
                                    [tableName]: value
                                }
                            })
                        }
                    }
                >
                    <img className="tablePnae-table-title-icon" src="/public/rdos/img/notice.png" />
                </Popover>
                <TextMark
                    onClick={this.changeVisible.bind(this, tableName)}
                    className="tablePnae-table-title-name"
                    title={tableName}
                    text={tableName}
                    markText={searchValue}
                />
            </div>
            <div style={{ display: this.isShow(tableName) ? 'block' : 'none' }} className="tablePane-table-column-box">
                {columns.map((column) => {
                    return <div key={column.columnName} className="tablePane-table-column">
                        <TextMark
                            className="table-column-name"
                            title={column.columnName}
                            text={column.columnName}
                            markText={searchValue}
                        />
                        <span
                            className="table-column-type"
                            title={column.columnType}
                        >
                            {column.columnType}
                        </span>
                    </div>
                })}
            </div>
        </section>
    }

    renderTables () {
        const { data } = this.props;
        const tableAndColumns = Object.entries(data);

        return <div className="tablePane-tables-box">
            {tableAndColumns.length ? <div>
                <Search
                    className="tablePane-search"
                    placeholder="输入表名/字段名搜索"
                    onChange={this.search.bind(this)}
                />
                {this.filterTable(tableAndColumns).map(([table, columns]) => {
                    return this.renderTableItem(table, columns)
                })}
            </div> : this.renderNone()}
        </div>
    }

    renderNone () {
        return (
            <div className="tablePane-notables">
                暂无表...
            </div>
        )
    }

    renderLoading () {
        return (
            <div className="tablePane-loading">
                <Spin />
            </div>
        )
    }

    closeTableDetail (tableName) {
        this.setState({
            visibleMap: {
                ...this.state.visibleMap,
                [tableName]: false
            }
        })
    }

    filterTable (tableAndColumns) {
        const { searchValue } = this.state;
        if (!searchValue) {
            return tableAndColumns;
        }
        return tableAndColumns.filter(([table, columns]) => {
            const column = columns.find((column) => {
                return column.columnName.indexOf(searchValue) > -1;
            })
            return table.indexOf(searchValue) > -1 || column;
        })
    }

    search (e) {
        this.setState({
            searchValue: e.target.value
        })
    }

    render () {
        console.log('redner')
        const { loading } = this.props;
        return (
            <div className="tablePane-box">
                <p className="tablePane-header">Tables</p>
                {this.renderTables()}
                {loading && this.renderLoading()}
            </div>
        )
    }
}

export default TableTipExtraPane;
