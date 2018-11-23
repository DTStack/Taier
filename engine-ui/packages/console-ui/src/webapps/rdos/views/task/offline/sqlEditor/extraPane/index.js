import React from "react";

import { Input, Icon, Spin, Popover } from "antd";

import TableDetail from "./tableDetail/index.js";

const Search = Input.Search;

class TableTipExtraPane extends React.Component {
    state = {
        searchValue: undefined,
        visibleMap: {}
    }

    renderTableItem(tableName, columns) {
        const { visibleMap } = this.state;
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
                <span className="tablePnae-table-title-name">{tableName}</span>
            </div>
            <div className="tablePane-table-column-box">
                {columns.map((column) => {
                    return <div className="tablePane-table-column">
                        <span className="table-column-name">{column.columnName}</span>
                        <span className="table-column-type">{column.columnType}</span>
                    </div>
                })}
            </div>
        </section>
    }

    renderTables() {
        const { data } = this.props;
        const tableAndColumns = Object.entries(data);

        return <div className="tablePane-tables-box">
            {tableAndColumns.length ? <div>
                <Search
                    className="tablePane-search"
                    placeholder="输入表名搜索"
                    onChange={this.search.bind(this)}
                />
                {this.filterTable(tableAndColumns).map(([table, columns]) => {
                    return this.renderTableItem(table, columns)
                })}
            </div> : this.renderNone()}
        </div>
    }

    renderNone() {
        return (
            <div className="tablePane-notables">
                暂无表...
            </div>
        )
    }

    renderLoading() {
        return (
            <div className="tablePane-loading">
                <Spin />
            </div>
        )
    }

    closeTableDetail(tableName) {
        this.setState({
            visibleMap: {
                ...this.state.visibleMap,
                [tableName]: false
            }
        })
    }

    filterTable(tableAndColumns){
        const {searchValue} = this.state;
        if(!searchValue){
            return tableAndColumns;
        }
       return tableAndColumns.filter(([table,columns])=>{
            return table.indexOf(searchValue)>-1;
       })
    }

    search(e) {
        this.setState({
            searchValue: e.target.value
        })
    }

    render() {
        console.log("redner")
        const { loading } = this.props;
        return (
            <div className="tablePane-box">
                <h className="tablePane-header">Tables</h>
                {this.renderTables()}
                {loading && this.renderLoading()}
            </div>
        )
    }
}

export default TableTipExtraPane;