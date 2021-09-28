import React from 'react'
import { Row, Col } from 'antd'

export default () => {
    return (
        <div
            className="box-title graph-info"
            style={{
                bottom: 35,
                height: '40px',
                background: 'transparent',
                border: 0,
                boxShadow: 'none'
            }}>
            <Row type="flex" justify="start">
                <Col span={4} style={{ minWidth: 200 }}>
                    <div className='mxYellow'></div>
                                等待提交/提交中/等待运行
                </Col>
                <Col span={3}>
                    <div className='mxBlue'></div>
                                运行中
                </Col>
                <Col span={3}>
                    <div className='mxGreen'></div>
                                成功
                </Col>
                <Col span={3}>
                    <div className='mxRed'></div>
                                失败
                </Col>
                <Col span={4}>
                    <div className='mxGray'></div>
                                冻结/取消
                </Col>
            </Row>
        </div>
    )
}
