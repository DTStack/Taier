import * as React from 'react';
import { message } from 'antd';
import Api from '../../../api/dataModel';

class BasePane extends React.Component<any, any> {
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
        Api.getModels(params).then((res: any) => {
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

    update = (formData: any) => {
        const succCall = (res: any) => {
            if (res.code === 1) {
                this.setState({
                    modalVisible: false
                }, this.loadData)
            }
        }
        if (formData.isEdit) { Api.updateModel(formData).then(succCall); } else { Api.addModel(formData).then(succCall); }
    }

    delete = (data: any) => {
        Api.deleteModel({
            ids: [data.id]
        }).then((res: any) => {
            if (res.code === 1) {
                message.success('删除成功！')
                this.loadData();
            }
        });
    }

    initEdit = (data: any) => {
        this.setState({
            modalData: data,
            modalVisible: true
        })
    }

    initAdd = () => {
        this.setState({ modalVisible: true, modalData: '' });
    }

    handleTableChange = (pagination: any, filters: any, sorter: any) => {
        const params = Object.assign(this.state.params, {
            currentPage: pagination.current
        })
        this.setState(params, this.loadData)
    }
}
export default BasePane;
