import * as React from 'react';
import { Tabs, Button, Icon, message } from 'antd';
import { connect } from 'react-redux'
import CreateEngineModal from './create';
import Api from '../../../api';
import { ENGINE_TYPE_NAME } from '../../../comm/const';

const TabPane = Tabs.TabPane;
const CREATE_STATUS: any = {
    INIT: 0,
    SUCC: 1
}
class EngineConfig extends React.Component<any, any> {
    state: any = {
        useingEngineData: {},
        useEngineList: [],
        unUseEngineList: [],
        confirmLoading: false,
        dBList: [],
        modalKey: '',
        createEngineVisible: false,
        formStatus: 'add' // 表单状态， add, edit
    }
    timer: any;
    componentDidMount () {
        this.getProUseEngine();
        this.getProjectUnUsedEngine();
        this.getDbList();
    }
    componentWillUnmount () {
        this.timer && clearInterval(this.timer);
    }
    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.getProUseEngine();
            this.getProjectUnUsedEngine();
            this.getDbList();
        }
    }
    exChangeData = (data = {}) => {
        let useEngineList: any = []
        for (let key in data) {
            useEngineList.push(Object.assign({}, (data as any)[key], {}))
        }
        return useEngineList
    }
    exChangeUnuseData = (data = {}) => {
        let unUseEngineList: any = [];
        for (let key in data) {
            unUseEngineList.push({
                name: (data as any)[key].engineType,
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
            const unUseEngineList = this.exChangeUnuseData(res.data)
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
    async checkAddEngineStatus (engineType: any) {
        this.setState({
            confirmLoading: true
        })
        let res = await Api.checkAddEngineStatus({
            engineType
        })
        if (res.code == 1) {
            const data = res.data;
            if (data == CREATE_STATUS.SUCC) {
                this.timer && clearInterval(this.timer);
                this.setState({
                    confirmLoading: false
                })
                message.success('添加引擎成功！');
                this.getProUseEngine();
                this.getProjectUnUsedEngine();
                this.getDbList();
                this.onCancelCreate();
            }
        } else {
            this.setState({
                confirmLoading: false
            })
            message.error('添加引擎失败！')
        }
    }
    async addEngineSource (sourceFormData?: any, form?: any) {
        const res = await Api.addNewEngine(sourceFormData);
        if (res.code === 1) {
            this.timer = setInterval(() => {
                this.checkAddEngineStatus(sourceFormData.engineType)
            }, 1500)
        }
    }
    onCancelCreate = () => { this.setState({ createEngineVisible: false }) }
    onActiveCreate = () => { this.setState({ createEngineVisible: true, modalKey: Math.random() }) }

    renderTitleText (data: any) {
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
        const { useEngineList } = this.state;
        const tabPanes = useEngineList.map((paneItem: any) => {
            return (
                <TabPane
                    tab={
                        <span>
                            {paneItem.engineType}
                        </span>
                    }
                    key={paneItem.engineType}
                    style={{ paddingTop: 20 }}>
                    <section className='engine-wrapper'>
                        <p>{`jdbcUrl：${paneItem.jdbcURL}`}</p>
                        {
                            <p>{paneItem.engineType == ENGINE_TYPE_NAME.HADOOP ? `defaultFS：${paneItem.defaultFS}`
                                : `userName：${paneItem.userName}`}
                            </p>
                        }
                    </section>
                </TabPane>
            )
        })
        return tabPanes;
    }
    addOrUpdateEngineSource = (params: any, form: any) => {
        console.log(params, form)
        this.addEngineSource(params)
    }
    render () {
        const { unUseEngineList, dBList, confirmLoading } = this.state;
        return (
            <div style={{ height: '100%' }}>
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
                <div className="box-2 m-tabs m-card" style={{ height: '100%' }}>
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
                    confirmLoading={confirmLoading}
                    visible={this.state.createEngineVisible}
                />
            </div>
        )
    }
}
export default connect((state: any) => {
    return {
        user: state.user,
        project: state.project
    }
})(EngineConfig)
