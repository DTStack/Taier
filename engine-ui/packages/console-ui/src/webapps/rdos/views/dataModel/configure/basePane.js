import React, { Component } from 'react';

import Api from '../../../api/dataModel';

export default class BasePane extends Component {

    state ={
        table: { data: [] },

        loading: false,
        
        modalData: '',
        modalVisible: false,

        params: {
            currentPage: 1,
            type: 1, // 模型层级
        }
    }

    componentDidMount() {
        this.loadData();
    }

    loadData = () => {
        const { params } = this.state;
        this.setState({
            loading: true,
        })
        Api.getModels(params).then(res => {
            if (res.code === 1) {
                this.setState({
                    table: res.data
                })
            }
            this.setState({
                loading: false,
            })
        })
    }

    update = (formData) => {
        Api.updateModel(formData).then(res => {
            if (res.code === 1) {
                this.loadData();
            }
        })
    }

    delete = (data) => {
        const { params } = this.state;
        Api.deleteModel(params).then(res => {
            if (res.code === 1) {
                this.loadData();
            }
        })
    }

    initEdit = (data) => {
        this.setState({
            modalData: data,
            modalVisible: true,
        })
    }

    handleTableChange(pagination, filters, sorter) {
        const params = Object.assign(this.state.params, { 
            currentPage: pagination.current 
        })
        this.setState(params, this.loadData)
    }
}