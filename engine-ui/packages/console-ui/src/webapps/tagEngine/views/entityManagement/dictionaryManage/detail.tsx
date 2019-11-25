import * as React from 'react';
import './style.scss';
import { hashHistory } from 'react-router';
import Breadcrumb from '../../../components/breadcrumb';
import ModuleTitle from '../../../components/moduleTitle';
import { Button, Card, Table } from 'antd';
import { get } from 'lodash';
import API from '../../../api/entity';

interface IProps {
    location: any;
}

interface IState {
    dictionaryInfor: any;
}

export default class DictionaryDetail extends React.Component<IProps, IState> {
    state: IState = {
        dictionaryInfor: undefined
    }

    componentDidMount () {
        let id = get(this.props.location, 'state.id');
        if (id) {
            this.getDictDetail(id);
        }
    }

    getDictDetail = (id) => {
        API.getDictDetail({
            dictId: id
        }).then((res: any) => {
            const { data = {}, code } = res;
            if (code === 1) {
                this.setState({
                    dictionaryInfor: data
                });
            }
        })
    }

    handleGotoEdit = () => {
        const { dictionaryInfor } = this.state;
        hashHistory.push({ pathname: '/dictionaryManage/edit', state: { ...dictionaryInfor } })
    }

    renderBaseInfor = () => {
        const { dictionaryInfor } = this.state;
        let references = dictionaryInfor.references ? dictionaryInfor.references.reduce((pre, curr, index) => {
            return index ? pre + ', ' + curr.refName : curr.refName;
        }, '') : '';
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
                        <td>{dictionaryInfor.createBy}</td>
                        <td>创建时间</td>
                        <td>{dictionaryInfor.createAt}</td>
                    </tr>
                    <tr>
                        <td>字典描述</td>
                        <td>{dictionaryInfor.desc}</td>
                        <td>被引用情况</td>
                        <td>{references}</td>
                    </tr>
                    <tr>
                        <td>最近更新时间</td>
                        <td>{dictionaryInfor.updateAt}</td>
                        <td></td>
                        <td></td>
                    </tr>
                </table>
            </div>
        )
    }

    renderRuleData = () => {
        const { dictionaryInfor: { dictValueVoList = [] } } = this.state;
        const columns = [
            {
                title: '字典值',
                dataIndex: 'value',
                key: 'value',
                width: 600
            }, {
                title: '字典名称',
                dataIndex: 'valueName',
                key: 'valueName'
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
                        rowKey="id"
                        pagination={false}
                        scroll={{ y: 400 }}
                        columns={columns}
                        dataSource={dictValueVoList}
                    />
                </Card>
            </div>
        )
    }

    render () {
        const { dictionaryInfor } = this.state;
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
                        <Button type='primary' onClick={this.handleGotoEdit}>编辑</Button>
                    }
                />
                {dictionaryInfor && this.renderBaseInfor()}
                <ModuleTitle title={'字典规则详情'} />
                {dictionaryInfor && this.renderRuleData()}
            </div>
        )
    }
}
