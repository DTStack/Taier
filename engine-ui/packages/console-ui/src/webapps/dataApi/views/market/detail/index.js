import React, { Component } from 'react'
import { connect } from 'react-redux';
import { Card, Icon, Row, Col, Button, Table } from 'antd'
import { Link } from 'react-router';
import { apiMarketActions } from '../../../actions/apiMarket';
import Content from '../../../components/apiContent'
import TopCard from './topCard'

const mapStateToProps = state => {
    const { user, apiMarket } = state;
    return { apiMarket, user }
};

const mapDispatchToProps = dispatch => ({
    getApiDetail (apiId) {
        dispatch(
            apiMarketActions.getApiDetail({
                apiId: apiId
            })
        )
    },
    getApiExtInfo (apiId) {
        dispatch(
            apiMarketActions.getApiExtInfo({
                apiId: apiId
            })
        )
    }
});

@connect(mapStateToProps, mapDispatchToProps)
class APIDetail extends Component {
    state = {
        apiId: ''
    }
    componentDidMount () {
        const apiId = this.props.router.params && this.props.router.params.api;
        if (apiId) {
            this.setState({
                apiId: apiId
            }, () => {
                this.props.getApiDetail(apiId);
                this.props.getApiExtInfo(apiId);
            })
        }
    }

    getRequestDataSource () {
        return this.getValue('reqParam') || [];
    }
    getRequestColumns () {
        return [{
            title: '参数名',
            dataIndex: 'paramName',
            key: 'paramName'
        }, {
            title: '数据类型',
            dataIndex: 'paramType',
            key: 'paramType'
        }, {
            title: '是否必填',
            dataIndex: 'required',
            key: 'required',
            render (text) {
                if (text) {
                    return '是'
                }
                return '否'
            }
        }, {
            title: '说明',
            dataIndex: 'desc',
            key: 'desc'
        }];
    }
    getResponseDataSource () {
        return this.getValue('respParam') || [];
    }
    getResponseColumns () {
        return [{
            title: '参数名',
            dataIndex: 'paramName',
            key: 'paramName'
        }, {
            title: '数据类型',
            dataIndex: 'paramType',
            key: 'paramType'
        }, {
            title: '是否必填',
            dataIndex: 'required',
            key: 'required',
            render (text) {
                if (text) {
                    return '是'
                }
                return '否'
            }
        }, {
            title: '说明',
            dataIndex: 'desc',
            key: 'desc'

        }];
    }

    render () {
        const { apiMarket } = this.props;
        const { apiId } = this.state;
        return (
            <div>
                <TopCard {...this.state} {...this.props} ></TopCard>
                <Card className="box-1" noHovering>
                    <Content apiMarket={apiMarket} apiId={apiId} />>
                </Card>
            </div>
        )
    }
}

export default APIDetail;
