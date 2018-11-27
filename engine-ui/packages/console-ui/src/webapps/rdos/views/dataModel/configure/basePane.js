import React, { Component } from 'react';
import { message } from 'antd';
import Api from '../../../api/dataModel';

class BasePane extends Component {
    state ={
        table: { data: [] },

        loading: false,

        modalData: '',
        modalVisible: false,

        params: {
            currentPage: 1,
            pageSize: 10,
            type: 1 // 模型层级
        }
    }

    componentDidMount () {
        this.loadData();
    }

    loadData = () => {
        const { params } = this.state;
        this.setState({
            loading: true
        });
        Api.getModels(params).then(res => {
            if (res.code === 1) {
                this.setState({
                    table: res.data
                })
            }
            this.setState({
                loading: false
            })
        });
    }

    update = (formData) => {
        const succCall = (res) => {
            if (res.code === 1) {
                this.setState({
                    modalVisible: false
                }, this.loadData)
            }
        }
        if (formData.isEdit) { Api.updateModel(formData).then(succCall); } else { Api.addModel(formData).then(succCall); }
    }

    delete = (data) => {
        Api.deleteModel({
            ids: [data.id]
        }).then(res => {
            if (res.code === 1) {
                message.success('删除成功！')
                this.loadData();
            }
        });
    }

    initEdit = (data) => {
        this.setState({
            modalData: data,
            modalVisible: true
        })
    }

    initAdd = () => {
        this.setState({ modalVisible: true, modalData: '' });
    }

    handleTableChange = (pagination, filters, sorter) => {
        const params = Object.assign(this.state.params, {
            currentPage: pagination.current
        })
        this.setState(params, this.loadData)
    }
}
export default BasePane;
