import * as React from 'react';

export default class BaseTable extends React.Component<any, any> {
    loadData: any;
    state ={
        table: { data: [] } as any,

        loading: false,

        modalData: '',
        modalVisible: false,

        params: {
            currentPage: 1,
            type: 1 // 模型层级
        }
    }

    initEdit = (data: any) => {
        this.setState({
            modalData: data,
            modalVisible: true
        })
    }

    handleTableChange (pagination: any, filters: any, sorter: any) {
        const params = Object.assign(this.state.params, {
            pageIndex: pagination.current
        })
        this.setState(params, this.loadData)
    }
}
