import * as React from 'react';
import { message, Modal } from 'antd'
import { connect } from 'react-redux';
import EnableTable from './buyStateTable/enableTable'
import DisableTable from './buyStateTable/disableTable'
import { apiManageActions } from '../../../../actions/apiManage';
import { mineActions } from '../../../../actions/mine';
const confirm = Modal.confirm;
const mapStateToProps = (state: any) => {
    const { user, apiMarket, apiManage } = state;
    return { apiMarket, apiManage, user }
};

const mapDispatchToProps = (dispatch: any) => ({
    getApiUserApplyList(params: any) {
        return dispatch(
            apiManageActions.getApiUserApplyList(params)
        )
    },
    updateUserApiStatus(params: any) {
        return dispatch(mineActions.updateApplyStatus(params));
    }
});

@(connect(mapStateToProps, mapDispatchToProps) as any)
class BuyManageState extends React.Component<any, any> {
    state: any = {
        data: [],
        total: 0,
        disAbleKey: 'init',
        enAbleKey: 'init',
        loading: false
    }
    componentDidMount () {
        this.tableChange({
            filter: {},
            sortedInfo: {},
            page: 1
        })
    }
    initState () {
        this.setState({
            disAbleKey: Math.random(),
            enAbleKey: Math.random(),
            total: 0,
            dataDisable: [],
            dataEnable: []
        })
    }
    /* eslint-disable-next-line */
    componentWillReceiveProps(nextProps: any) {
        if (this.props.apiId != nextProps.apiId || this.props.disAble != nextProps.disAble) {
            this.initState();
            this.tableChange({
                filter: {},
                sortedInfo: {},
                page: 1
            }, nextProps.apiId)
        }
    }
    tableChange(params: any, apiId: any) {
        const { filter, page } = params;
        let status = filter.status
        if (!status || status.length < 1) {
            status = ['1', '3', '4']
        }
        let requestParams: any = {};
        requestParams.apiId = apiId || this.props.apiId;
        requestParams.pageSize = 10;
        requestParams.currentPage = page
        requestParams.status = status;
        this.getData(requestParams);
    }
    getData(params: any) {
        if (!params) { // 无参数，默认刷新
            params = this.state.requestParams;
        }
        if (!params.apiId) {
            return;
        }
        this.setState({
            loading: true,
            requestParams: params
        })
        this.props.getApiUserApplyList(params)
            .then(
                (res: any) => {
                    this.setState({
                        loading: false
                    })
                    if (res) {
                        this.setState({
                            data: res.data.data,
                            total: res.data.totalCount
                        })
                    }
                }
            )
    }
    // 取消api授权
    cancelApi(applyId: any) {
        confirm({
            title: '确认取消?',
            content: '确认取消授权',
            onOk: () => {
                this.props.updateUserApiStatus({
                    applyId: applyId,
                    useAdmin: true,
                    status: 4
                })
                    .then(
                        (res: any) => {
                            if (res) {
                                message.success('取消成功')
                                this.getData();
                            }
                        }
                    )
            },
            onCancel () {
                console.log('Cancel');
            }
        });
    }
    // 增加api授权
    applyApi(applyId: any) {
        confirm({
            title: '确认授权?',
            content: '确认授权',
            onOk: () => {
                this.props.updateUserApiStatus({
                    applyId: applyId,
                    useAdmin: true,
                    status: 1
                })
                    .then(
                        (res: any) => {
                            if (res) {
                                message.success('授权成功')
                                this.getData();
                            }
                        }
                    )
            },
            onCancel () {
                console.log('Cancel');
            }
        });
    }
    lookAllErrorText () {
        console.log('lookAllErrorText')
    }
    getTable () {
        if (this.props.statusDisAble) {
            return <DisableTable loading={this.state.loading} key={this.state.disAbleKey} total={this.state.total} data={this.state.data} tableChange={this.tableChange.bind(this)}></DisableTable>
        }
        return <EnableTable
            cancelApi={this.cancelApi.bind(this)}
            applyApi={this.applyApi.bind(this)}
            loading={this.state.loading}
            key={this.state.enAbleKey}
            total={this.state.total}
            data={this.state.data}
            tableChange={this.tableChange.bind(this)} ></EnableTable>
    }
    render () {
        return (
            <div>

                {this.getTable()}
            </div>
        )
    }
}
export default BuyManageState;
