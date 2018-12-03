import { Component } from 'react';

export default class BaseTable extends Component {
    state ={
        table: { data: [] },

        loading: false,

        modalData: '',
        modalVisible: false,

        params: {
            currentPage: 1,
            type: 1 // 模型层级
        }
    }

    initEdit = (data) => {
        this.setState({
            modalData: data,
            modalVisible: true
        })
    }

    handleTableChange (pagination, filters, sorter) {
        const params = Object.assign(this.state.params, {
            pageIndex: pagination.current
        })
        this.setState(params, this.loadData)
    }
}
