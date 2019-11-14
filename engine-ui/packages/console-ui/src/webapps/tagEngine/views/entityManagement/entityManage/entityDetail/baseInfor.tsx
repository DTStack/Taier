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
                        <td>{infor.dataSource}</td>
                    </tr>
                    <tr>
                        <td>数据表</td>
                        <td>{infor.table}</td>
                        <td>数据源主键</td>
                        <td>{infor.key}({infor.keyName})</td>
                    </tr>
                    <tr>
                        <td>数据量</td>
                        <td>{infor.count}</td>
                        <td>创建人</td>
                        <td>{infor.creator}</td>
                    </tr>
                    <tr>
                        <td>创建时间</td>
                        <td>{infor.createTime}</td>
                        <td>实体描述</td>
                        <td>{infor.desc}</td>
                    </tr>
                </table>
            </div>
        )
    }
}
