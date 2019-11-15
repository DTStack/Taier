import * as React from 'react';
import { Tabs, Card, Button, Row, Col } from 'antd';
import Breadcrumb from '../../../components/breadcrumb';
import { GroupStatus } from '../../../components/status';

import './style.scss';
const TabPane = Tabs.TabPane;
interface IProps {
    router?: any;
}
interface IState {
    type: string;
    visible: boolean;
    moveVisible: boolean;
}

export default class AtomicLabelDetails extends React.PureComponent<IProps, IState> {
    state: IState = {
        visible: false,
        moveVisible: false,
        type: '0'
    };
    handleChange = () => {

    }
    render () {
        const breadcrumbNameMap = [
            {
                path: '/labelCenter',
                name: '标签管理'
            },
            {
                path: '/atomicLabelDetails',
                name: '标签详情'
            }
        ];
        return (
            <div className="atomicLabelDetails">
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
                            <TabPane tab="基本信息" key="1">基本信息</TabPane>
                            <TabPane tab="标签规则" key="2">标签规则</TabPane>
                        </Tabs>
                    </Row>
                </Card>
            </div>
        );
    }
}
