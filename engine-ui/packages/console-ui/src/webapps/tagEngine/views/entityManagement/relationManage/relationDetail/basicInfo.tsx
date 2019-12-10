import * as React from 'react';

import { Row, Button } from 'antd';

import utils from 'utils';
import { Link } from 'react-router';
import { IRelation } from '../../../../model/relation';

interface IProps {
    data: IRelation;
};

class BasicRelationInfo extends React.Component<IProps, any> {
    render () {
        const { data } = this.props;
        return (
            <Row className="row-content">
                <h1 className="row-title">
                    数据库信息
                    <span className="right">
                        <Link to={`/relationManage/edit?relationId=${data.id}`}><Button>编辑</Button></Link>
                    </span>
                </h1>
                <table style={{ marginTop: 5 }} className="table-info" width="100%" {...{ width: '100%' }} cellPadding="0" cellSpacing="0">
                    <tbody>
                        <tr>
                            <td>关系名称</td>
                            <td>
                                {data.relationName || '关系名称'}
                            </td>
                            <td>关系ID</td>
                            <td>{data.id}</td>
                        </tr>
                        <tr>
                            <td>创建人</td>
                            <td>{data.createBy}</td>
                            <td>创建时间</td>
                            <td>{utils.formatDateTime(data.createAt)}</td>
                        </tr>
                        <tr>
                            <td>关系描述</td>
                            <td>{data.relationDesc}</td>
                        </tr>
                    </tbody>
                </table>
            </Row>
        )
    }
}

export default BasicRelationInfo
