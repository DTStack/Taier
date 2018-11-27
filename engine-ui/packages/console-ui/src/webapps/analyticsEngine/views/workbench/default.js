import React, { Component } from 'react';
import { Row, Col } from 'antd';

class Default extends Component {
    render () {
        const {
            onSQLQuery,
            onCreateTable
        } = this.props;

        const iconBaseUrl = '/public/analyticsEngine/img';

        return (
            <Row
                className="box-card txt-left"
                style={{ paddingTop: '30px' }}
            >
                <Col className="operation-card">
                    <div
                        onClick={() => onSQLQuery()}
                        className="operation-content"
                    >
                        <img
                            src={`${iconBaseUrl}/new_query.png`}
                            className="anticon"
                        />
                        <p className="txt-center operation-title">
                            SQL查询
                        </p>
                    </div>
                </Col>
                <Col className="operation-card">
                    <div
                        onClick={onCreateTable}
                        className="operation-content"
                    >
                        <img
                            src={`${iconBaseUrl}/add_table.png`}
                            className="anticon"
                        />
                        <p className="txt-center operation-title">
                            新建表
                        </p>
                    </div>
                </Col>
            </Row>
        );
    }
}

export default Default;
