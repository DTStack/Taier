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
    shouldComponentUpdate (nextProps, nextState) {
        // 只有结果id改变，才会更新，防止handsontable在display:none的状态下更新导致渲染错误
        if (this.props.id != nextProps.id || nextState != this.state) {
            return true;
        } else {
            return false;
        }
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
    render () {
        const { pagination } = this.state;
        const { extraView, data } = this.props;
        const showData = data.slice(1, data.length);
        // const columns = this.generateCols(data[0]);
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
                        className='o-handsontable-no-border'
                        style={{ width: '100%', height: '100%' }}
                        language='zh-CN'
                        colHeaders={data[0]}
                        data={this.getPageData(showData)}
                        readOnly={true}
                        rowHeaders={true}// 数字行号
                        fillHandle={false}// 拖动复制单元格
                        manualRowResize={true}// 拉伸功能
                        manualColumnResize={true}// 拉伸功能
                        colWidths={500}
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
