import * as React from 'react'
import { connect } from 'react-redux';
import { Card, Input, Cascader, Table, Modal, Tabs, Tooltip, Checkbox } from 'antd'
import { apiMarketActions } from '../../actions/apiMarket';
import utils from 'utils';

import SlidePane from 'widgets/slidePane';
import ApplyBox from '../../components/applyBox'
import Content from '../../components/apiContent'
import { API_TYPE } from '../../consts';

const Search = Input.Search;
let modal: any;

const mapStateToProps = (state: any) => {
    const { user, project, apiMarket } = state;
    return { apiMarket, user, project }
};

const mapDispatchToProps = (dispatch: any) => ({
    getCatalogue (pid: any) {
        dispatch(apiMarketActions.getCatalogue(pid));
    },
    getApiMarketList (params: any) {
        return dispatch(apiMarketActions.getApiMarketList(params));
    },
    getApiDetail (params: any) {
        dispatch(apiMarketActions.getApiDetail(params));
    },
    getApiExtInfo (params: any) {
        dispatch(apiMarketActions.getApiExtInfo(params));
    }
});

@(connect(mapStateToProps, mapDispatchToProps) as any)
class APIMarket extends React.Component<any, any> {
    state: any = {
        searchValue: '',
        pageIndex: 1,
        loading: true,
        applyBox: false,
        apply: {
            apiId: '',
            apiName: '',
            desc: ''
        },
        showCreate: false,
        showModify: false,
        detailRecord: {},
        type1: undefined,
        type2: undefined,
        apiName: '',
        pageSize: 20,
        total: 0,
        sorter: {},
        slidePaneShow: false,
        applyKey: Math.random()
    }
    getMarketApi () {
        this.setState({
            loading: true
        })
        const { showCreate, showModify } = this.state;
        const { user } = this.props;
        const userId = user.id;
        const dic: any = {
            updateTime: 'gmt_modified'
        }
        const orderType: any = {
            'ascend': 'asc',
            'descend': 'desc'
        }

        this.props.getApiMarketList({
            apiName: this.state.searchValue,
            pid: this.state.type1 || -1,
            cid: this.state.type2 || -1,
            currentPage: this.state.pageIndex,
            pageSize: this.state.pageSize,
            orderBy: dic[this.state.sorter.columnKey],
            sort: orderType[this.state.sorter.order],
            modifyUserId: showModify ? userId : null, // 修改人id
            createUserId: showCreate ? userId : null // 创建人人id
        }).then((res: any) => {
            console.log('apigetOver');

            this.setState({
                loading: false,
                total: res.data.totalCount
            })
        }).catch((e: any) => {
            this.setState({
                loading: false
            })
        })
    }
    getCatagoryName (value: any, catagorys?: any) {
        if (!value && value != 0) {
            return null;
        }
        const tree = catagorys || this.props.apiMarket.apiCatalogue;
        let arr: any = [];
        function exchangeTree (data: any) {
            if (!data || data.length < 1) {
                return null;
            }
            for (let i = 0; i < data.length; i++) {
                let item = data[i];
                if (item.api) {
                    continue;
                }
                if (item.id == value) {
                    arr.push(item.catalogueName);
                    return item.catalogueName;
                }
                if (exchangeTree(item.childCatalogue)) {
                    arr.push(item.catalogueName);
                    return item.catalogueName
                }
            }
            return null;
        }
        if (exchangeTree(tree)) {
            return arr.reverse().join(' / ');
        }
        return null;
    }
    getCascaderData () { // 处理级联选择数据
        const cascaderData: any = [];
        const { apiCatalogue } = this.props.apiMarket;
        if (apiCatalogue.length > 0) {
            apiCatalogue.map((v: any) => {
                const option: any = {};
                option.value = option.label = v.catalogueName;
                option.pid = v.id
                if (v.childCatalogue.length > 0) {
                    option.children = [];
                    v.childCatalogue.map((v: any) => {
                        if (v.api) {
                            return;
                        }
                        const childOpt: any = {};
                        childOpt.value = childOpt.label = v.catalogueName;
                        childOpt.cid = v.id;
                        option.children.push(childOpt);
                    })
                    if (option.children.length == 0) {
                        option.children = undefined;
                    }
                }
                cascaderData.push(option)
            })
        }
        return cascaderData;
    }
    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps: any) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.props.getCatalogue(0);
            this.getMarketApi();
        }
    }

    componentDidMount () {
        this.props.getCatalogue(0);
        this.getMarketApi();
    }
    onSourceChange (key: any) {
        this.setState({
            type1: key,
            type2: undefined
        }, () => {
            this.getMarketApi();
        })
    }
    onUserSourceChange (key: any) {
        this.setState({
            type2: key
        }, () => {
            this.getMarketApi();
        })
    }
    handleSearch (value: any) {
        this.setState({
            searchValue: value,
            pageIndex: 1
        }, () => {
            this.getMarketApi();
        }
        )
    }
    onCheckBoxChange (type: string, e: React.ChangeEvent<HTMLInputElement>) {
        this.setState({
            [type]: e.target.checked
        }, () => {
            this.getMarketApi();
        })
    }
    getDealType (type: any) {
        const dic: any = {
            'complete': '已审批',
            'nothing': '申请',
            'applying': '审批中'
        }
        return dic[type || 'nothing']
    }
    deal (record: any) {
        const method = (this as any)['deal' + record.deal]
        if (method) {
            method.call(this, record);
        }
    }
    dealcomplete (record: any) {
        this.props.router.push({
            pathname: '/api/mine/myApi/approved',
            query: {
                apiId: record.key,
                apiName: record.apiName
            }
        });
    }
    dealnothing (record: any) {
        this.setState({
            applyBox: true,
            applyKey: Math.random(),
            apply: {
                apiId: record.key,
                apiName: record.apiName,
                desc: record.description
            }
        })
        console.log('dealnothing', record);
    }
    dealapplying (record: any) {
        this.props.router.push({
            pathname: '/api/mine/myApi/notApproved',
            query: {
                apiId: record.key,
                apiName: record.apiName
            }
        });
    }
    // 表格换页/排序
    onTableChange = (page: any, filter: any, sorter: any) => {
        this.setState({
            pageIndex: page.current,
            sorter: sorter

        }, () => {
            this.getMarketApi();
        });
    }
    openDetail (record: any) {
        const { getApiDetail, getApiExtInfo } = this.props;
        this.setState({
            detailRecord: record,
            slidePaneShow: true
        });
        getApiDetail({ apiId: record.key });
        getApiExtInfo({ apiId: record.key });
    }
    initColumns () {
        return [{
            title: 'API名称',
            dataIndex: 'apiName',
            key: 'apiName',
            render: (text: any, record: any) => {
                return <a onClick={this.openDetail.bind(this, record)} >{text}</a>
            }
        }, {
            title: 'API分类',
            dataIndex: 'cId',
            key: 'cId',
            width: '230px',
            render: (text: any, record: any) => {
                return this.getCatagoryName(text);
            }
        }, {
            title: 'API描述',
            dataIndex: 'description',
            key: 'description',
            width: 300,
            render (text: any) {
                const desc = (text && text.length > 21) ? (<Tooltip title={text}>
                    <span>{`${text.substr(0, 21)}......`}</span>
                </Tooltip>) : text;
                return desc;
            }
        }, {
            title: '累计调用（次）',
            dataIndex: 'callCount',
            key: 'callCount'

        }, {
            title: '最近更新时间',
            dataIndex: 'updateTime',
            key: 'updateTime',
            render (time: any) {
                return utils.formatDateTime(time);
            },
            sorter: true
        }, {
            title: '操作',
            dataIndex: 'deal',
            render: (text: any, record: any) => {
                return <a onClick={this.deal.bind(this, record)}>{this.getDealType(record.deal)}</a>
            }
        }]
    }
    getSource () {
        const errorDic: any = {
            5: 'nothing',
            4: 'complete',
            3: 'complete',
            2: 'nothing',
            1: 'complete',
            0: 'applying',
            '-1': 'nothing'
        }
        const apiList = this.props.apiMarket.apiList;
        let arr: any = [];
        for (let i = 0; i < apiList.length; i++) {
            arr.push({
                key: apiList[i].id,
                apiName: apiList[i].name,
                description: apiList[i].apiDesc,
                callCount: apiList[i].invokeTotal,
                updateTime: apiList[i].gmtModified,
                deal: errorDic[apiList[i].applyStatus],
                cId: apiList[i].cId,
                apiType: apiList[i].apiType
            })
        }
        return arr;
    }
    getPagination () {
        return {
            current: this.state.pageIndex,
            pageSize: this.state.pageSize,
            total: this.state.total
        }
    }

    cascaderOnChange (value: any, data: any) { // API类型改变
        let { type1, type2 } = this.state;
        type1 = data[0] ? data[0].pid : undefined;
        type2 = data[1] ? data[1].cid : undefined;
        this.setState({
            type1,
            type2
        }, () => {
            this.getMarketApi();
        })
    }

    getCardTitle () {
        const cascaderData = this.getCascaderData();
        const { showCreate, showModify } = this.state;
        return (
            <div className="flex font-12">
                <Search
                    placeholder="输入API名称搜索"
                    style={{ width: 150 }}
                    onSearch={this.handleSearch.bind(this)}
                />
                <div className="m-l-8">
                    API分类：
                    <Cascader placeholder="API分类" options={cascaderData} onChange={this.cascaderOnChange.bind(this)} changeOnSelect expandTrigger="hover" />
                </div>
                <Checkbox
                    className="m-l-8"
                    checked={showCreate}
                    onChange={this.onCheckBoxChange.bind(this, 'showCreate')}
                >
                    我创建的
                </Checkbox>
                <Checkbox
                    checked={showModify}
                    onChange={this.onCheckBoxChange.bind(this, 'showModify')}
                >
                    我修改的
                </Checkbox>

            </div>
        )
    }

    jumpToMine () {
        modal.destroy();
        this.props.router.push('/api/mine/myApi');
    }
    showApplySuccessModal () {
        modal = Modal.success({
            title: '申请提交成功',
            content: (
                <span>您可以在 <a onClick={this.jumpToMine.bind(this)}>我的API</a> 中查看审批进度</span>
            ),
            okText: '确定'
        });
    }
    handleOk () {
        this.setState({
            applyBox: false
        });
        this.getMarketApi();
    }
    handleCancel () {
        this.setState({
            applyBox: false
        })
    }
    closeSlide () {
        this.setState({
            slidePaneShow: false
        })
    }

    render () {
        const { apiMarket } = this.props
        const { slidePaneShow, detailRecord, applyKey } = this.state;

        return (
            <div className="api-market">
                <ApplyBox
                    show={this.state.applyBox}
                    successCallBack={this.handleOk.bind(this)}
                    cancelCallback={this.handleCancel.bind(this)}
                    apiId={this.state.apply.apiId}
                    apiName={this.state.apply.apiName}
                    desc={this.state.apply.desc}
                    key={applyKey}
                />
                <h1 className="box-title">Api市场</h1>
                <div style={{ minHeight: 'calc(100% - 65px)' }} className="m-card box-2">
                    <SlidePane
                        className="m-tabs"
                        visible={slidePaneShow}
                        style={{ right: '-20px', width: '80%', minHeight: '600px', height: '100%' }}
                        onClose={this.closeSlide.bind(this)}>
                        <Tabs
                            animated={false}
                            className="l-dt__tabs--scroll"
                        >
                            <Tabs.TabPane tab="API详情" key="callMethod">
                                <div style={{ paddingLeft: '40px', paddingTop: '20px' }}>
                                    <Content
                                        showReqLimit={true}
                                        apiMarket={apiMarket}
                                        apiId={detailRecord.key}
                                        showMarketInfo={true}
                                        showRecord={detailRecord}
                                        isRegister={detailRecord.apiType == API_TYPE.REGISTER}
                                    />
                                </div>
                            </Tabs.TabPane>
                        </Tabs>

                    </SlidePane>
                    <Card
                        bordered={false}
                        noHovering
                        title={this.getCardTitle()}
                    >
                        <Table
                            className="m-table monitor-table"
                            columns={this.initColumns()}
                            loading={this.state.loading}
                            pagination={this.getPagination()}
                            dataSource={this.getSource()}
                            onChange={this.onTableChange}
                        />
                    </Card>
                </div>
            </div>
        )
    }
}

export default APIMarket
