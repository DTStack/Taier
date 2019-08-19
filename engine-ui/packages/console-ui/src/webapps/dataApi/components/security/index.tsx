import * as React from 'react';
import { connect } from 'react-redux';

import LimitModal from './limitModal';
import SecurityModal from './securityModal';
import SecurityDetailModal from '../securityDetailModal';

import { apiMarketActions } from '../../actions/apiMarket';
import { getApiMarketValue } from '../../utils';
import api from '../../api/apiManage'

@(connect((state: any) => {
    return {
        apiMarket: state.apiMarket
    }
}, (dispatch: any) => {
    return {
        getApiDetail (apiId: any) {
            dispatch(
                apiMarketActions.getApiDetail({
                    apiId: apiId
                })
            )
        }
    }
}) as any)
class ApiDetailSecurity extends React.Component<any, any> {
    state: any = {
        isLimitModalVisible: false,
        isSecurityModalVisible: false,
        securityDetailModalVisible: false,
        randomKey: null,
        securityList: []
    }
    componentDidMount () {
        this.fetchData();
    }
    fetchData = () => {
        const { apiId } = this.props;
        if (apiId) {
            this.props.getApiDetail(apiId);
            api.listSecurityGroupByApiId({ apiId }).then((res: any) => {
                if (res.code == 1) {
                    this.setState({
                        securityList: res.data
                    })
                }
            });
        }
    }
    getValue (key: any) {
        const { apiMarket, apiId } = this.props;
        return getApiMarketValue(key, apiMarket, apiId)
    }
    showLimitModal () {
        this.setState({
            isLimitModalVisible: true,
            randomKey: Math.random()
        })
    }
    closeLimitModal () {
        this.setState({
            isLimitModalVisible: false
        })
    }
    showSecurityModal () {
        this.setState({
            isSecurityModalVisible: true,
            randomKey: Math.random()
        })
    }
    closeSecurityModal () {
        this.setState({
            isSecurityModalVisible: false
        })
    }
    renderSecurityList (securityList: any[] = []) {
        return securityList.map((security: any) => {
            return <a onClick={this.showSecurityDetail.bind(this, security)} key={security.id}>{security.name}</a>
        }).reduce((arrs: any, currentArr: any) => {
            if (arrs.length) {
                return arrs.concat(['，', currentArr])
            } else {
                return arrs.concat(currentArr)
            }
        }, [])
    }
    showSecurityDetail (security: any) {
        this.setState({
            securityDetailModalVisible: true,
            securityData: security
        })
    }
    render () {
        const { apiId, disableEdit } = this.props;
        const { isLimitModalVisible, isSecurityModalVisible, randomKey, securityList, securityData, securityDetailModalVisible } = this.state;
        const reqLimit = this.getValue('reqLimit');
        return (
            <div style={{ paddingLeft: 30, marginTop: '20px' }}>
                <h1 className="title-border-l-blue">安全与限制策略</h1>
                <div style={{ marginTop: '10px' }}>
                    <p data-title="调用次数限制：" className="pseudo-title p-line">
                        {reqLimit} 次/秒
                        {!disableEdit && <a style={{ marginLeft: '8px' }} onClick={this.showLimitModal.bind(this)}>修改</a>}
                    </p>
                    <p data-title="安全组：" className="pseudo-title p-line">
                        {this.renderSecurityList(securityList)}
                        {!disableEdit && <a style={{ marginLeft: '8px' }} onClick={this.showSecurityModal.bind(this)}>修改</a>}
                    </p>
                </div>
                <LimitModal
                    key={randomKey}
                    visible={isLimitModalVisible}
                    closeModal={this.closeLimitModal.bind(this)}
                    onOk={this.fetchData}
                    apiId={apiId}
                    data={reqLimit}
                />
                <SecurityModal
                    key={randomKey + 1}
                    visible={isSecurityModalVisible}
                    closeModal={this.closeSecurityModal.bind(this)}
                    onOk={this.fetchData}
                    apiId={apiId}
                    data={securityList}
                />
                <SecurityDetailModal
                    data={securityData}
                    visible={securityDetailModalVisible}
                    closeModal={() => {
                        this.setState({
                            securityDetailModalVisible: false
                        })
                    }}
                />
            </div>
        )
    }
}

export default ApiDetailSecurity;
