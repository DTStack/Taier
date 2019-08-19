import * as React from 'react';
import PropTypes from 'prop-types';

import { Row, Icon } from 'antd';

const Response = (props: any) => {
    const { message, data, warning } = props;
    const warningMsg = warning || '请妥善保存数据库密码，遗失后无法找回，可通过重置密码恢复访问';
    return (
        <div className="response-succ" style={{
            padding: '30px 60px'
        }}>
            <Row className="txt-center">
                <Icon type="check-circle" className="status-icon"/>
                <p className="status-desc">{message}</p>
            </Row>
            <Row className="response-info">
                <Row>数据库标识：{data.name}</Row>
                <Row>JDBC信息：{data.jdbcUrl}</Row>
                <Row>用户名：{data.dbUserName}</Row>
                <Row>密码：{data.dbPwd}</Row>
            </Row>
            <Row className="response-warning">
                <Icon type="exclamation-circle-o" />&nbsp;
                <span>{warningMsg}</span>
            </Row>
        </div>
    )
}

Response.propTypes = {
    message: PropTypes.string,
    data: PropTypes.object
}

export default Response
