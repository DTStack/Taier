import React, { PropTypes } from 'react';

import { Row, Icon } from 'antd';

const UpdateSucc = props => {
    const { message, data, warning } = props;
    const warningMsg = warning || '请妥善保存数据库密码，遗失后无法找回，可通过重置密码恢复访问';
    return (
        <div className="update-succ" style={{
            padding: '30px 60px'
        }}>
            <Row className="txt-center">
                <Icon type="check-circle" className="status-icon"/>
                <p className="status-desc">{message}</p>
            </Row>
            <Row className="update-info">
                <Row>数据库标识：{data.name}</Row>
                <Row>JDBC信息：{data.jdbc}</Row>
                <Row>用户名：{data.username}</Row>
                <Row>密码：{data.password}</Row>
            </Row>
            <Row className="update-warning">
                <Icon type="exclamation-circle-o" />&nbsp;
                <span>{warningMsg}</span>
            </Row>
        </div>
    )
}

UpdateSucc.propTypes = {
    message: PropTypes.string,
    data: PropTypes.object,
}

export default UpdateSucc