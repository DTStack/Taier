import * as React from 'react';
import { Pagination } from 'antd';
import SpreadSheet from 'widgets/spreadsheet';

class Result extends React.Component<any, any> {
    state: any = {
        pagination: {
            current: 1,
            pageSize: 10
        }
    };
    getPageData (data: any) {
        let result: any = [];
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
                    <SpreadSheet
                        columns={data[0]}
                        data={this.getPageData(showData)}
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
                            onChange={(page: any) => {
                                this.setState({
                                    pagination: {
                                        ...pagination,
                                        current: page
                                    }
                                })
                            }}
                            onShowSizeChange={(current: any, size: any) => {
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
