import * as React from 'react'
import { Card, Icon, Row, Col, Button } from 'antd'
import { EXCHANGE_ADMIN_API_STATUS } from '../../../consts'
import utils from 'utils';
class ManageTopCard extends React.Component<any, any> {
    getTopRightButton () {
        const key = EXCHANGE_ADMIN_API_STATUS[this.getValue('status')];
        if (key) {
            if (key == 'stop') {
                return (
                    <div>
                        <Button onClick={this.props.openApi} style={{ marginRight: '5px' }} >开启</Button>
                        <Button onClick={this.editApi.bind(this, this.props.apiId)} style={{ marginRight: '5px' }} type="primary">编辑</Button>
                        <Button onClick={this.props.deleteApi} type="danger">删除</Button>
                    </div>
                )
            } else {
                return <Button onClick={this.props.closeApi} size="large" type="danger">禁用</Button>
            }
        }
        return null;
    }

    editApi(id: any) {
        this.props.router.push('/dl/manage/editApi/' + id);
    }
    back () {
        this.props.router.goBack();
    }
    openApiDetail () {
        window.open(`${location.origin + location.pathname}#/dl/market/detail/${this.props.apiId}?isHideBack=true`)
    }
    getValue(key: any) {
        const api = this.props.apiMarket && this.props.apiMarket.apiCallInfo && this.props.apiMarket.apiCallInfo[this.props.apiId];
        if (api) {
            return api[key]
        } else {
            return null;
        }
    }
    render () {
        return (
            <Card className="box-1" noHovering>
                <Row className="m-count" style={{ height: 'auto' }}>
                    <Col span={1} ><Icon type="left-circle-o" onClick={this.back.bind(this)} style={{ fontSize: 18, cursor: 'pointer' }} /></Col>
                    <Col span={10}>
                        <Row className="header-title">
                            {this.getValue('apiName')}
                        </Row>
                        <Row className="header-content">
                            {this.getValue('apiDesc')}
                        </Row>
                        <Row className="header-content">
                            <a onClick={this.openApiDetail.bind(this)}>查看详情</a>
                        </Row>

                    </Col>
                    <Col span={3}>
                        <section className="m-count-section " style={{ width: 150, marginTop: 0 }}>
                            <span className="m-count-title text-left">订购人数</span>
                            <span className="m-count-content font-black text-left">{utils.toQfw(this.getValue('applyNum') || 0)}<span style={{ fontSize: 12 }}></span></span>
                        </section>
                    </Col>
                    <Col span={5}>
                        <section className="m-count-section" style={{ width: 200, marginTop: 0 }}>
                            <span className="m-count-title text-left">累计调用</span>
                            <span className="m-count-content font-black text-left">{utils.toQfw(this.getValue('apiCallNum') || 0)}<span style={{ fontSize: 12 }}></span></span>
                        </section>
                    </Col>
                    <Col span={5}>
                        {this.getTopRightButton()}
                    </Col>
                </Row>

            </Card>
        )
    }
}
export default ManageTopCard;
