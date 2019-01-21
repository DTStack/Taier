import React, { Component } from 'react';
import { Row, Col } from 'antd';
import { connect } from 'react-redux';
@connect(state => {
    return {
        licenseApps: state.licenseApps
    }
})
class Default extends Component {
    fixArrayIndex = (arr) => {
        let fixArrChildrenApps = [];
        arr.map(item => {
            switch (item.name) {
                case '数据库管理':
                    fixArrChildrenApps[0] = item;
                    break;
                case '表管理':
                    fixArrChildrenApps[1] = item;
                    break;
            }
        })
        return fixArrChildrenApps
    }
    render () {
        const {
            onSQLQuery,
            onCreateTable,
            licenseApps
        } = this.props;

        const iconBaseUrl = '/public/analyticsEngine/img';
        const fixArrChildrenApps = this.fixArrayIndex(licenseApps[2].children);
        const isShowCreateTable = fixArrChildrenApps[1].Show;
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
                { isShowCreateTable ? (
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
                ) : null }
            </Row>
        );
    }
}

export default Default;
