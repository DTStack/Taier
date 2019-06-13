import React, { Component } from 'react';
import { Tabs, Button } from 'antd';

// import EngineSourceForm from './form';
import CreateEngineModal from './create';
import Api from '../../../api';

const TabPane = Tabs.TabPane;

class EngineConfig extends Component {
    state = {
        engineList: [{
            name: 'hadoop',
            value: '1'
        }, {
            name: 'libra',
            value: '2'
        }],
        modalKey: '',
        createEngineVisible: false,
        formStatus: 'add' // 表单状态， add, edit
    }

    componentDidMount () {
        // 先调获取支持引擎的类型以及列表然后搜索
        // this.fetchData();
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
                ctx.setState({ engineList: res.data })
            }
        })
    }

    addOrUpdateEngineSource = (sourceFormData, formObj, callback) => {
        // const ctx = this
        // const { title, formStatus, source } = this.state
        // let reqSource = sourceFormData
        // if (formStatus === 'edit') { // 编辑数据
        //     reqSource = Object.assign(source, sourceFormData)
        // }
        // Api.addOrUpdateSource(reqSource).then((res) => {
        //     if (res.code === 1) {
        //         formObj.resetFields()
        //         message.success(`${title}成功！`)
        //         ctx.setState({
        //             visible: false
        //         })
        //         ctx.fetchData()
        //         if (callback) callback();
        //     }
        // })
    }

    onCancelCreate = () => { this.setState({ createEngineVisible: false }) }
    onActiveCreate = () => { this.setState({ createEngineVisible: true, modalKey: Math.random() }) }

    renderTabPanes = () => {
        const { engineList } = this.state;
        const tabPanes = engineList.map(paneItem => {
            return (
                <TabPane tab={paneItem.name} key={paneItem.value} style={{ paddingTop: 20 }}>
                    <section className='engine-wrapper'>
                        <p>dbcUrl：jdbc:hive2://node005:10000/zhedates</p>
                        <p>defaultFS：hdfs://ns1</p>
                    </section>
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
                    key={this.state.modalKey}
                    visible={this.state.createEngineVisible}
                />
            </div>
        )
    }
}

export default EngineConfig
