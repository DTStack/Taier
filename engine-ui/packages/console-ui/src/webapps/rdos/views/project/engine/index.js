import React, { Component } from 'react';
import { Tabs, Button, Icon } from 'antd';
// import { ENGINE_SOURCE_TYPE } from '../../../comm/const';
// import EngineSourceForm from './form';
import CreateEngineModal from './create';
import Api from '../../../api';

const TabPane = Tabs.TabPane;

class EngineConfig extends Component {
    state = {
        useingEngineData: {},
        useEngineList: [],
        unUseEngineList: [],
        dBList: [],
        modalKey: '',
        createEngineVisible: false,
        formStatus: 'add' // 表单状态， add, edit
    }

    componentDidMount () {
        this.getProUseEngine();
        this.getProjectUnUsedEngine();
        this.getDbList();
    }
    exChangeData = (data = {}) => {
        let useEngineList = []
        for (let key in data) {
            useEngineList.push(Object.assign({}, data[key], {}))
        }
        return useEngineList
    }
    exChangeUnuseData = (data = {}) => {
        console.log('data', data)
        let unUseEngineList = [];
        for (let key in data) {
            unUseEngineList.push({
                name: data[key].engineType,
                value: key
            })
        }
        console.log(unUseEngineList)
        return unUseEngineList;
    }
    async getProUseEngine () {
        const res = await Api.getProjectUsedEngineInfo()
        if (res.code == 1) {
            const useEngineList = this.exChangeData(res.data || {})
            this.setState({
                useEngineList,
                useingEngineData: res.data
            })
        }
    }
    async getProjectUnUsedEngine () {
        const res = await Api.getProjectUnUsedEngine();
        if (res.code == 1) {
            const unUseEngineList = this.exChangeUnuseData()
            this.setState({
                unUseEngineList
            })
        }
    }
    async getDbList () {
        let res = await Api.getRetainDBList();
        if (res.code == 1) {
            this.setState({
                dBList: res.data
            });
        }
    }
    addEngineSource = (sourceFormData, form) => {
        const res = Api.addNewEngine(sourceFormData);
        if (res.code === 1) {
            this.onCancelCreate();
            this.onCancelCreate();
        }
    }
    onCancelCreate = () => { this.setState({ createEngineVisible: false }) }
    onActiveCreate = () => { this.setState({ createEngineVisible: true, modalKey: Math.random() }) }

    renderTitleText (data) {
        switch (data.status) {
            case 0: {
                return (
                    <span>
                        <Icon type="loading" style={{ fontSize: 14, color: '#2491F7', paddingLeft: 16 }} />
                        <span style={{ color: '#999', paddingLeft: '8px' }}>创建中</span>
                    </span>
                )
            }
            case 1: {
                return null
            }
            case 2:
            case 3: {
                return (
                    <span>
                        <Icon type="close-circle" style={{ fontSize: 14, color: '#f00', paddingLeft: 16 }} />
                        <span style={{ color: '#999', paddingLeft: '8px' }}>创建失败</span>
                    </span>
                )
            }
        }
    }
    renderTabPanes = () => {
        const { useEngineList, useingEngineData } = this.state;
        const tabPanes = useEngineList.map(paneItem => {
            return (
                <TabPane
                    tab={
                        <span>
                            {paneItem.engineType}
                            {this.renderTitleText(useingEngineData)}
                        </span>
                    }
                    key={paneItem.engineType}
                    style={{ paddingTop: 20 }}>
                    <section className='engine-wrapper'>
                        <p>{`jdbcUrl：${paneItem.jdbcURL}`}</p>
                        <p>{`defaultFS：${paneItem.defaultFS}`}</p>
                    </section>
                </TabPane>
            )
        })
        return tabPanes;
    }

    render () {
        const { unUseEngineList, dBList } = this.state;
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
                    unUseEngineList={unUseEngineList}
                    dBList={dBList}
                    visible={this.state.createEngineVisible}
                />
            </div>
        )
    }
}

export default EngineConfig
