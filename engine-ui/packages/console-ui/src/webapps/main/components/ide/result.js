import React from 'react';
import { Pagination } from 'antd';
import { HotTable } from '@handsontable/react';
import 'handsontable/languages/zh-CN.js';

class Result extends React.Component {
    state = {
        pagination: {
            current: 1,
            pageSize: 10
        }
    };
    tableRef = React.createRef()
    componentDidUpdate (prevProps, prevState) {
        if (prevProps != this.props && this.props.isShow) {
            if (this.tableRef) {
                this.removeRenderClock();
                this._renderColck = setTimeout(() => {
                    this.tableRef.current.hotInstance.render();
                })
            }
        }
    }
    removeRenderClock () {
        if (this._renderColck) {
            clearTimeout(this._renderColck)
        }
    }
    componentWillUnmount (prevProps, prevState) {
        this.removeRenderClock();
    }
    getPageData (data) {
        let result = [];
        if (!data) {
            return result;
        }
        const { pagination } = this.state;
        const { current, pageSize } = pagination;
        const begin = (current - 1) * pageSize;
        const end = begin + pageSize;
        result = data.slice(begin, end);
        return result;
    }
    hotTableCustomRender (instance, td, row, col, prop, value, cellProperties) {
        if (row == 1) {
            td.style.background = '#000';
        }
        return td;
    }
    generateCols (arr) {
        return arr.map((item, index) => {
            return {
                // renderer: this.hotTableCustomRender,
                data: index
            }
        })
    }
    render () {
        const { pagination } = this.state;
        const { extraView, data } = this.props;
        const showData = data.slice(1, data.length);
        const columns = this.generateCols(data[0]);
        return (
            // <Table
            //     rowKey="id"
            //     scroll={{ x: true }}
            //     className="console-table"
            //     bordered
            //     dataSource={showData}
            //     onChange={this.onChange}
            //     columns={columns}
            // />
            <div className='c-ide-result'>
                <div className='c-ide-result__table'>
                    <HotTable
                        ref={this.tableRef}
                        className='o-handsontable-no-border'
                        style={{ width: '100%', height: '100%' }}
                        language='zh-CN'
                        colHeaders={data[0]}
                        columns={columns}
                        data={this.getPageData(showData)}
                        readOnly={true}
                        rowHeaders={true}// 数字行号
                        fillHandle={false}// 拖动复制单元格
                        manualRowResize={true}// 拉伸功能
                        manualColumnResize={true}// 拉伸功能
                        colWidths={200}
                        rowHeights={30}
                        columnHeaderHeight={25}
                        contextMenu={['copy']}
                        stretchH='all' // 填充空白区域
                    />
                </div>
                <div className='c-ide-result__tools'>
                    {extraView}
                    <span className='c-ide-result__tools__pagination'>
                        <Pagination
                            size='small'
                            {...pagination}
                            total={showData.length}
                            showSizeChanger
                            onChange={(page) => {
                                this.setState({
                                    pagination: {
                                        ...pagination,
                                        current: page
                                    }
                                })
                            }}
                            onShowSizeChange={(current, size) => {
                                this.setState({
                                    pagination: {
                                        ...pagination,
                                        pageSize: size,
                                        current: 1
                                    }
                                })
                            }}
                        />
                    </span>
                </div>
            </div>
        );
    }
}
export default Result;
