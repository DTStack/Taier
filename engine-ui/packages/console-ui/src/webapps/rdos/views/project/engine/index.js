import React, { Component } from 'react';
import { Tabs, Button, message } from 'antd';

import EngineSourceForm from './form';
import CreateEngineModal from './create';
import Api from '../../../api';

const TabPane = Tabs.TabPane;

class EngineConfig extends Component {
    state = {
        data: [],
        createEngineVisible: false,
        formStatus: 'add' // 表单状态， add, edit
    }

    componentDidMount () {
        this.fetchData();
    }

    fetchData = () => {
        const ctx = this
        const { params } = this.state;
        this.setState({ loading: true })
        const reqParams = Object.assign({
            pageSize: 10,
            currentPage: 1
        }, params)
        Api.queryEngineSource(reqParams).then((res) => {
            this.setState({
                loading: false
            })
            if (res.code === 1) {
                ctx.setState({ data: res.data })
            }
        })
    }

    addOrUpdateEngineSource = (sourceFormData, formObj, callback) => {
        const ctx = this
        const { title, formStatus, source } = this.state
        let reqSource = sourceFormData
        if (formStatus === 'edit') { // 编辑数据
            reqSource = Object.assign(source, sourceFormData)
        }
        Api.addOrUpdateSource(reqSource).then((res) => {
            if (res.code === 1) {
                formObj.resetFields()
                message.success(`${title}成功！`)
                ctx.setState({
                    visible: false
                })
                ctx.fetchData()
                if (callback) callback();
            }
        })
    }

    testConnection = (source) => { // 测试数据源连通性
        Api.testDSConnection(source).then((res) => {
            if (res.code === 1 && res.data) {
                message.success('数据源连接正常！')
            } else if (res.code === 1 && !res.data) {
                message.error('数据源连接异常')
            }
        })
    }

    onCancelCreate = () => { this.setState({ createEngineVisible: false }) }
    onActiveCreate = () => { this.setState({ createEngineVisible: true }) }

    renderTabPanes = () => {
        const { data } = this.state;
        const tabPanes = data.map(paneItem => {
            return (
                <TabPane tab={paneItem.name} key={paneItem.id} style={{ paddingTop: 20 }}>
                    <EngineSourceForm
                        formMode="edit"
                        sourceData={paneItem}
                        testConnection={this.testConnection}
                        handOk={this.addOrUpdateEngineSource}
                    />
                </TabPane>
            )
        })
        return tabPanes;
    }

    render () {
        return (
            <div>
                <h1 className="box-title">
                    计算引擎配置
                    <Button
                        type="primary"
                        style={{ marginTop: '8.5px', float: 'right' }}
                        onClick={this.onActiveCreate}
                    >
                            添加计算引擎
                    </Button>
                </h1>
                <div className="box-2 m-tabs m-card">
                    <Tabs
                        animated={false}
                        style={{ height: 'auto', minHeight: 'calc(100% - 40px)' }}
                    >
                        { this.renderTabPanes() }
                    </Tabs>
                </div>
                <CreateEngineModal
                    onOk={this.addOrUpdateEngineSource}
                    onCancel={this.onCancelCreate}
                    visible={this.state.createEngineVisible}
                />
            </div>
        )
    }
}

export default EngineConfig
