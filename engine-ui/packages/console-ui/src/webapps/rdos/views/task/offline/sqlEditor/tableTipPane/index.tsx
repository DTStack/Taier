import * as React from 'react';

import { Input, Spin, Popover } from 'antd';

import TextMark from 'widgets/textMark';
import TableDetail from './tableDetail/index';

const Search = Input.Search;

class TableTipExtraPane extends React.Component<any, any> {
    state: any = {
        searchValue: undefined,
        visibleMap: {},
        hideList: []
    }
    resolveTableName (tableName: any) {
        const { tabId } = this.props;
        return tableName + '%' + tabId;
    }
    changeVisible (tableName: any) {
        const { hideList } = this.state;
        let cloneHideList: any = [...hideList];
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
    isShow (tableName: any) {
        const { hideList } = this.state;
        tableName = this.resolveTableName(tableName);
        const tableIndex = hideList.indexOf(tableName);
        return tableIndex == -1;
    }
    renderTableItem (tableName: any, columns: any) {
        const { visibleMap, searchValue } = this.state;
        return <section key={`item-${tableName}`} className="c-tablePane__table">
            <div className="c-tablePane__table__title">
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
                        (value: any) => {
                            this.setState({
                                visibleMap: {
                                    ...visibleMap,
                                    [tableName]: value
                                }
                            })
                        }
                    }
                >
                    <img className="c-tablePnae__table__title__icon" src="/public/rdos/img/notice.png" />
                </Popover>
                <TextMark
                    onClick={this.changeVisible.bind(this, tableName)}
                    className="c-tablePnae__table__title__name"
                    title={tableName}
                    text={tableName}
                    markText={searchValue}
                />
            </div>
            <div style={{ display: this.isShow(tableName) ? 'block' : 'none' }} className="c-tablePane__table__columns">
                {columns.map((column: any) => {
                    const isDesen = column.needMask === 0 // 是否脱敏
                    return <div key={column.columnName} className="c-tablePane__table__column">
                        <TextMark
                            className="c-table__column__name"
                            title={column.columnName}
                            text={column.columnName + (isDesen ? '(脱敏)' : column.isPartition ? '(分区)' : '')}
                            markText={searchValue}
                        />
                        <span
                            className="c-table__column__type"
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
        let { data, partition } = this.props;
        partition = partition || {};
        let tableAndColumns = Object.entries(data);
        tableAndColumns = tableAndColumns.map((item: any) => {
            const tableName = item[0];
            const columns = item[1];
            const partitionColumns = partition[tableName];
            if (partitionColumns) {
                return [tableName, partitionColumns.map((column: any) => {
                    return { ...column, isPartition: true };
                }).concat(columns)];
            } else {
                return item
            }
        })
        return <div className="c-tablePane__tables">
            {tableAndColumns.length ? <div>
                <Search
                    className="c-tablePane__search"
                    placeholder="输入表名/字段名搜索"
                    onChange={this.search.bind(this)}
                />
                {this.filterTable(tableAndColumns).map(([ table, columns ]: any) => {
                    return this.renderTableItem(table, columns)
                })}
            </div> : this.renderNone()}
        </div>
    }

    renderNone () {
        return (
            <div className="c-tablePane__notables">
                暂无表...
            </div>
        )
    }

    renderLoading () {
        return (
            <div className="c-tablePane__loading">
                <Spin />
            </div>
        )
    }

    closeTableDetail (tableName: any) {
        this.setState({
            visibleMap: {
                ...this.state.visibleMap,
                [tableName]: false
            }
        })
    }
    /**
     * 根据搜索条件过滤表格字段
     */
    filterTable (tableAndColumns: any) {
        const { searchValue } = this.state;
        if (!searchValue) {
            return tableAndColumns;
        }
        return tableAndColumns.filter(([ table, columns ]: any) => {
            const column = columns.find((column: any) => {
                return column.columnName.indexOf(searchValue) > -1;
            })
            return table.indexOf(searchValue) > -1 || column;
        })
    }

    search (e: any) {
        this.setState({
            searchValue: e.target.value
        })
    }

    render () {
        const { loading } = this.props;
        return (
            <div className="c-tablePane l-tablePane">
                <p className="c-tablePane__header">Tables</p>
                {this.renderTables()}
                {loading && this.renderLoading()}
            </div>
        )
    }
}

export default TableTipExtraPane;
