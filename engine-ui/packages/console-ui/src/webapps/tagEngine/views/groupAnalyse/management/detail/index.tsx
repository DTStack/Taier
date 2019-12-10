import * as React from 'react'
import { get } from 'lodash'

import { Tabs, Card, Button, Row, Col, message } from 'antd';

import Breadcrumb from '../../../../components/breadcrumb';
import { GroupStatus } from '../../../../components/status';
import BasicInfo from './basicInfo';
import SpecimenList from './specimen';
import Portrait from './portrait';

import GroupAPI from '../../../../api/group';
import { IGroup } from '../../../../model/group';
import { Link } from 'react-router';

interface IState {
    loading: boolean;
    queryParams: {
        desc: boolean;
        pageNo: number;
        sorterField: string;
        searchVal: string;
    };
    groupDetail: IGroup;
};

const breadcrumbNameMap = [{
    path: '/groupAnalyse',
    name: '群组管理'
}, {
    path: '/groupAnalyse/detail',
    name: '群组详情'
}];

const TabPane = Tabs.TabPane;

class GroupDetail extends React.Component<any, IState> {
    state: IState = {
        loading: false,
        queryParams: {
            desc: false,
            pageNo: 0,
            sorterField: '',
            searchVal: null
        },
        groupDetail: {}
    }

    componentDidMount () {
        this.loadDetail();
    }

    loadDetail = async () => {
        const { router } = this.props;
        const res = await GroupAPI.getGroup({ groupId: get(router, 'location.query.groupId') });
        this.setState({
            groupDetail: res.data
        })
    }

    onEnableAPI = async () => {
        const { router } = this.props;
        const { groupDetail } = this.state;
        const res = await GroupAPI.openAPI({ groupId: get(router, 'location.query.groupId'), isOpenApi: groupDetail.isOpen ? 0 : 1 });
        if (res.code === 1) {
            const mes = groupDetail.isOpen ? 'ApI 关闭成功' : 'ApI 开启成功'
            message.success(mes)
            this.loadDetail();
        }
    }

    render () {
        const { router } = this.props;
        const { groupDetail = {} } = this.state;
        return (
            <div className="c-groupDetail m-card">
                <Breadcrumb breadcrumbNameMap={breadcrumbNameMap} />
                <Card
                    noHovering
                    bordered={false}
                    className="noBorderBottom"
                    bodyStyle={{ padding: 20 }}
                >
                    <Row>
                        <Col className="left">
                            <h1>{groupDetail.groupName || '-'}</h1>
                            <p className="description"><span style={{ marginRight: 10 }}>最近更新时间：{groupDetail.updateAt}</span> <GroupStatus value={0} /></p>
                        </Col>
                        <Col className="right">
                            <Button type="primary"><Link to={`/groupAnalyse/upload/edit?groupId=${get(router, 'location.query.groupId')}&entityId=${get(router, 'location.query.entityId')}`}>编辑</Link></Button>
                        </Col>
                    </Row>
                    <Row className="c-groupDetail__tabs">
                        <Tabs
                            defaultActiveKey="1"
                            animated={false}
                            tabBarStyle={{ height: 38 }}
                        >
                            <TabPane tab="基本信息" key="basicInfo"><BasicInfo onEnableAPI={this.onEnableAPI} data={groupDetail} /></TabPane>
                            <TabPane tab="样本列表" key="specimenList"><SpecimenList router={router} /></TabPane>
                            <TabPane tab="群组画像" key="portrait"><Portrait router={router} /></TabPane>
                        </Tabs>
                    </Row>
                </Card>
            </div>
        )
    }
}

export default GroupDetail
