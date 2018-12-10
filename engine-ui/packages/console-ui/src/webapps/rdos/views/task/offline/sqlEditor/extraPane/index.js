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
        return <section className="c-tablePane__table">
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
                {columns.map((column) => {
                    return <div key={column.columnName} className="c-tablePane__table__column">
                        <TextMark
                            className="c-table__column__name"
                            title={column.columnName}
                            text={column.columnName}
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
        const { data } = this.props;
        const tableAndColumns = Object.entries(data);

        return <div className="c-tablePane__tables">
            {tableAndColumns.length ? <div>
                <Search
                    className="c-tablePane__search"
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
