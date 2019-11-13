import * as React from 'react';
import './style.scss';
import Breadcrumb from '../../../components/breadcrumb';
import ModuleTitle from '../../../components/moduleTitle';
import { Button, Card, Table } from 'antd';

interface IProps {
    location: any;
}

interface IState {
    dictionaryInfor: any;
}

export default class DictionaryDetail extends React.Component<IProps, IState> {
    state: IState = {
        dictionaryInfor: {
            name: '字典名称',
            type: '标签字典',
            creator: '创建人',
            createTime: '2019-12-03 12:23:44',
            des: '描述描述描述',
            refered: '标签名称1；标签名称2；',
            updateTime: '2019-12-03 12:23:44',
            rule: [
                { name: '高中', value: '1' },
                { name: '高中', value: '2' },
                { name: '高中', value: '3' },
                { name: '高中', value: '4' }
            ]
        }
    }

    componentDidMount () {

    }

    renderBaseInfor = () => {
        const { dictionaryInfor } = this.state;
        return (
            <div className="dd-base-infor">
                <table className="review_info">
                    <tr>
                        <td>字典名称</td>
                        <td>{dictionaryInfor.name}</td>
                        <td>字典类型</td>
                        <td>{dictionaryInfor.type}</td>
                    </tr>
                    <tr>
                        <td>创建人</td>
                        <td>{dictionaryInfor.creator}</td>
                        <td>创建时间</td>
                        <td>{dictionaryInfor.createTime}</td>
                    </tr>
                    <tr>
                        <td>字典描述</td>
                        <td>{dictionaryInfor.des}</td>
                        <td>被引用情况</td>
                        <td>{dictionaryInfor.refered}</td>
                    </tr>
                    <tr>
                        <td>最近更新时间</td>
                        <td>{dictionaryInfor.updateTime}</td>
                        <td></td>
                        <td></td>
                    </tr>
                </table>
            </div>
        )
    }

    renderRuleData = () => {
        const { dictionaryInfor: { rule = [] } } = this.state;
        const columns = [
            {
                title: '字典值',
                dataIndex: 'value',
                key: 'value',
                width: 600
            }, {
                title: '字典名称',
                dataIndex: 'name',
                key: 'name'
            }
        ]
        return (
            <div className="tage-dictionary-manage shadow">
                <Card
                    noHovering
                    bordered={false}
                    className="noBorderBottom"
                >
                    <Table
                        rowKey="value"
                        className="dt-ant-table dt-ant-table--border"
                        pagination={false}
                        scroll={{ y: 400 }}
                        columns={columns}
                        dataSource={rule}
                    />
                </Card>
            </div>
        )
    }

    render () {
        const breadcrumbNameMap = [
            {
                path: '/dictionaryManage',
                name: '字典管理'
            },
            {
                path: '',
                name: '字典详情'
            }
        ];
        return (
            <div className="dictionary-detail">
                <Breadcrumb breadcrumbNameMap={breadcrumbNameMap} style={{ marginBottom: 10 }} />
                <ModuleTitle
                    title={'基本信息'}
                    extra={
                        <Button type='primary'>编辑</Button>
                    }
                />
                {this.renderBaseInfor()}
                <ModuleTitle title={'字典规则详情'} />
                {this.renderRuleData()}
            </div>
        )
    }
}
