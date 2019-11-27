import * as React from 'react';
import './style.scss';

interface IProps {
    infor: any;
}

export default class BaseInfor extends React.Component<IProps, any> {
    state: any = {

    }

    componentDidMount () {

    }

    render () {
        const { infor } = this.props;
        return (
            <div className="ed-base-infor">
                <table className="review_info">
                    <tr>
                        <td>实体ID</td>
                        <td>{infor.id}</td>
                        <td>数据源</td>
                        <td>{infor.dataSourceName}</td>
                    </tr>
                    <tr>
                        <td>数据表</td>
                        <td>{infor.dataSourceTable}</td>
                        <td>数据源主键</td>
                        <td>{infor.entityPrimaryKey}{infor.entityPrimaryKeyCn ? `(${infor.entityPrimaryKeyCn})` : ''}</td>
                    </tr>
                    <tr>
                        <td>数据量</td>
                        <td>{infor.dataCount}</td>
                        <td>创建人</td>
                        <td>{infor.createBy}</td>
                    </tr>
                    <tr>
                        <td>创建时间</td>
                        <td>{infor.createAt}</td>
                        <td>实体描述</td>
                        <td>{infor.entityDesc}</td>
                    </tr>
                    <tr>
                        <td>更新时间</td>
                        <td>{infor.updateAt}</td>
                        <td></td>
                        <td></td>
                    </tr>
                </table>
            </div>
        )
    }
}
