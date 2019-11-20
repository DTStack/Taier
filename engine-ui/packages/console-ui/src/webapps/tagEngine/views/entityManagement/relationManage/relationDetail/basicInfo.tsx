import * as React from 'react';

import { Row, Button } from 'antd';

import utils from 'utils';
import { Link } from 'react-router';

interface IProps {
    data: any;
};

class BasicRelationInfo extends React.Component<IProps, any> {
    render () {
        const { data } = this.props;
        return (
            <Row className="row-content">
                <h1 className="row-title">
                    数据库信息
                    <span className="right">
                        <Link to={`/relationManage/edit/${data.id}`}><Button>编辑</Button></Link>
                    </span>
                </h1>
                <table style={{ marginTop: 5 }} className="table-info" width="100%" {...{ width: '100%' }} cellPadding="0" cellSpacing="0">
                    <tbody>
                        <tr>
                            <td>关系名称</td>
                            <td>
                                {data.name || '关系名称'}
                            </td>
                            <td>关系ID</td>
                            <td>{data.id}</td>
                        </tr>
                        <tr>
                            <td>创建人</td>
                            <td>{data.createUserName}</td>
                            <td>创建时间</td>
                            <td>{utils.formatDateTime(data.gmtCreate)}</td>
                        </tr>
                        <tr>
                            <td>数据量</td>
                            <td>{data.count}</td>
                            <td>关系描述</td>
                            <td>{data.desc}</td>
                        </tr>
                    </tbody>
                </table>
            </Row>
        )
    }
}

export default BasicRelationInfo
