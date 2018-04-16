import React, { Component } from 'react'
import { connect } from "react-redux";
import { Card, Icon, Row, Col, Button, Modal } from "antd"
import ApplyBox from "../applyBox"
import utils from "utils";
let modal;
class TopCard extends Component {
    state = {
        applyBox: false,
        apply: {
            apiId: "",
            apiName: "",
            desc: ""
        }
    }
    getTopRightButton() {
        const status = this.getValue('applyStatus');
        if (status == 2) {
            return <Button onClick={this.jumpToMine.bind(this)} size="large" type="primary">查看使用情况</Button>

        }
        return <Button onClick={this.doApply.bind(this)} size="large" type="primary">立即订购</Button>

    }
    doApply() {
        this.setState({
            applyBox: true,
            apply: {
                apiId: this.props.appId,
                apiName: this.getApiValue('apiName'),
                desc: this.getApiValue('desc')
            }
        })
    }
    back() {
        this.props.router.replace("/api/market");
    }
    getValue(key) {
        const api = this.props.apiMarket && this.props.apiMarket.apiCallInfo && this.props.apiMarket.apiCallInfo[this.props.apiId];
        if (api) {
            return api[key]
        } else {
            return null;
        }

    }
    getApiValue(key) {
        const api = this.props.apiMarket && this.props.apiMarket.api && this.props.apiMarket.api[this.props.apiId];
        if (api) {
            return api[key]
        } else {
            return null;
        }

    }
    showApplySuccessModal() {
        modal = Modal.success({
            title: '申请提交成功',
            content: (
                <span>您可以在 <a onClick={this.jumpToMine.bind(this)}>我的API</a> 中查看审批进度</span>
            ),
            okText: "确定"
        });
    }

    jumpToMine() {
        modal.destroy();
        this.props.router.push("/api/mine");

    }
    handleOk() {

        this.setState({
            applyBox: false
        });
        this.showApplySuccessModal();
    }
    handleCancel() {
        this.setState({
            applyBox: false
        })
    }
    render() {
        return (
            <Card className="box-1" noHovering>
                <ApplyBox show={this.state.applyBox}
                    successCallBack={this.handleOk.bind(this)}
                    cancelCallback={this.handleCancel.bind(this)}
                    apiId={this.state.apply.apiId}
                    apiName={this.state.apply.apiName}
                    desc={this.state.apply.desc}
                ></ApplyBox>
                <Row className="m-count" style={{ height: "auto" }}>
                    <Col span={1} ><Icon type="left-circle-o" onClick={this.back.bind(this)} style={{ fontSize: 18, cursor: "pointer" }} /></Col>
                    <Col span={12}>
                        <Row className="header-title">
                            {this.getApiValue('apiName')}
                        </Row>
                        <Row className="header-content">
                            {this.getApiValue('desc')}
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
                    <Col span={3}>
                        {this.getTopRightButton()}
                    </Col>
                </Row>

            </Card>
        )
    }
}
export default TopCard;