import * as React from 'react'

import { Tabs, Card, Button, Row, Col } from 'antd';

import Breadcrumb from '../../../../components/breadcrumb';
import { GroupStatus } from '../../../../components/status';
import BasicInfo from './basicInfo';
import SpecimenList from './specimen';
import Portrait from './portrait';

import GroupAPI from '../../../../api/group';

interface IState {
    loading: boolean;
    queryParams: {
        desc: boolean;
        pageNo: number;
        sorterField: string;
        searchVal: string;
    };
    groupDetail: any;
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
        const res = await GroupAPI.getGroups();
        this.setState({
            groupDetail: res
        })
    }

    render () {
        const { groupDetail } = this.state;
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
                            <h1>5大城市20岁以上购买产品的人群</h1>
                            <p><span>最近更新时间：2019-10-10 00:00</span> <GroupStatus value={0}/></p>
                        </Col>
                        <Col className="right">
                            <Button type="primary" style={{ marginRight: 20 }}>生成 API</Button>
                            <Button type="primary">编辑</Button>
                        </Col>
                    </Row>
                    <Row style={{ minHeight: 600 }}>
                        <Tabs
                            defaultActiveKey="1"
                            animated={false}
                            tabBarStyle={{ height: 40 }}
                        >
                            <TabPane tab="Tab 1" key="1"><BasicInfo data={groupDetail}/></TabPane>
                            <TabPane tab="Tab 2" key="2"><SpecimenList /></TabPane>
                            <TabPane tab="Tab 3" key="3"><Portrait /></TabPane>
                        </Tabs>
                    </Row>
                </Card>
            </div>
        )
    }
}

export default GroupDetail
