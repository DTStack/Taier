import * as React from 'react';
import { Col, Row, Radio } from 'antd';

import { connect } from 'react-redux';
import { apiManageActions } from '../../../../../actions/apiManage';
import { mineActions } from '../../../../../actions/mine';
import ManageTopCall from '../topCall'
import ManageCallCountGraph from './callCountGraph';
import ManageCallDealyGraph from './callDelayGraph';

const RadioButton = Radio.Button;
const RadioGroup = Radio.Group;

const GRAPH_TYPE: any = {
    COUNT: 'count',
    DEALY: 'delay'
}

const mapStateToProps = (state: any) => {
    const { user, apiMarket, apiManage } = state;
    return { apiMarket, apiManage, user }
};

const mapDispatchToProps = (dispatch: any) => ({
    getApiCallUserRankList (apiId: any, time: any) {
        return dispatch(
            apiManageActions.getApiCallUserRankList({
                apiId: apiId,
                time: time
            })
        )
    },
    getApiCallInfo (apiId: any, time: any) {
        return dispatch(
            mineActions.getApiCallInfo({
                apiId: apiId,
                time: time,
                useAdmin: true
            })
        )
    }
});

@(connect(mapStateToProps, mapDispatchToProps) as any)
class ApiManageCallState extends React.Component<any, any> {
    state: any = {
        topCallUser: '',
        failPercent: '',
        callCount: '',
        callList: [],
        topCallList: [],
        apiId: '',
        dateType: '',
        graphType: GRAPH_TYPE.COUNT
    }
    componentDidMount () {
        this.setState({
            apiId: this.props.apiId,
            dateType: this.props.dateType

        }, () => {
            this.getApiCallUserRankList();
        })
    }
    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps(nextProps: any) {
        if (this.state.apiId != nextProps.apiId || this.state.dateType != nextProps.dateType) {
            this.setState({
                apiId: nextProps.apiId,
                dateType: nextProps.dateType

            }, () => {
                this.getApiCallUserRankList();
            })
        }
    }
    getApiCallUserRankList () {
        let apiId = this.state.apiId;
        let time = this.state.dateType;

        if (!apiId || !time) {
            return;
        }
        this.props.getApiCallUserRankList(apiId, time)
            .then(
                (res: any) => {
                    if (res) {
                        this.setState({
                            topCallList: res.data
                        })
                    }
                }
            )
    }
    getGraph () {
        const { graphType, apiId, dateType } = this.state;
        switch (graphType) {
            case GRAPH_TYPE.COUNT: {
                return <ManageCallCountGraph
                    key={apiId + '' + dateType}
                    apiId={apiId}
                    dateType={dateType}
                />
            }
            case GRAPH_TYPE.DEALY: {
                return <ManageCallDealyGraph
                    key={apiId + '' + dateType}
                    apiId={apiId}
                    dateType={dateType}
                />
            }
            default: {
                return null;
            }
        }
    }
    render () {
        const { graphType } = this.state;
        return (
            <div style={{ paddingLeft: '20px', paddingRight: '20px' }}>
                <Row>
                    <Col span={16}>
                        <div style={{ paddingTop: '20px' }}>
                            <RadioGroup onChange={(e: any) => {
                                this.setState({
                                    graphType: e.target.value
                                })
                            }} value={graphType}>
                                <RadioButton value={GRAPH_TYPE.COUNT}>调用次数</RadioButton>
                                <RadioButton value={GRAPH_TYPE.DEALY}>调用耗时</RadioButton>
                            </RadioGroup>
                        </div>
                        {this.getGraph()}
                    </Col>
                    <Col span={8} style={{ paddingTop: '15px' }}>
                        <p style={{ fontWeight: 'bold', lineHeight: 1, fontSize: '14px' }}>排行榜</p>
                        <ManageTopCall data={this.state.topCallList}></ManageTopCall>
                    </Col>
                </Row>

            </div>
        )
    }
}
export default ApiManageCallState;
