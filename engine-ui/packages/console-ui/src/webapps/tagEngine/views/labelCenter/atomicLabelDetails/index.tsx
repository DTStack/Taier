import * as React from 'react';
import { Tabs, Card, Button, Row, Col, Modal } from 'antd';
import Breadcrumb from '../../../components/breadcrumb';
import { GroupStatus } from '../../../components/status';
import MoveTreeNode from './components/moveTreeNode';
import './style.scss';
import BasicInfo from './components/basicInfo';
import LableRules from './components/labelRules';
const TabPane = Tabs.TabPane;
const confirm = Modal.confirm;
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
    onHandleEdit = () => {
        this.props.router.push('/editAtomicLabel')
    }
    onHandleMove = () => {
        this.setState({
            moveVisible: true
        })
    }
    onHandleCancelMove = (type: 'ok'|'cancel') => {
        this.setState({
            moveVisible: false
        })
    }
    onHandleDelete = () => {
        confirm({
            title: '删除标签',
            content: '删除标签后，无法恢复请谨慎操作！',
            onOk () {
                console.log('OK');
            },
            onCancel () {
                console.log('Cancel');
            }
        });
    }
    render () {
        const { moveVisible } = this.state;
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
                    className="dt-cardTabs"
                    bodyStyle={{ padding: 20 }}
                >
                    <Row>
                        <Col className="left">
                            <div className="title">5大城市20岁以上购买产品的人群</div>
                            <div className="subTitle"><span className="update">最近更新时间：2019-10-10 00:00</span> <GroupStatus value={0}/></div>
                        </Col>
                        <Col className="right">
                            <Button type="primary" className="btn" onClick={this.onHandleEdit}>编辑</Button>
                            <Button type="primary" className="btn" onClick={this.onHandleDelete}>删除</Button>
                            <Button type="primary" className="btn" onClick={this.onHandleMove}>移动至</Button>
                        </Col>
                    </Row>
                    <Row style={{ minHeight: 600 }}>
                        <Tabs
                            defaultActiveKey="1"
                            animated={false}
                            tabBarStyle={{ height: 40 }}
                        >
                            <TabPane tab="基本信息" key="1">
                                <BasicInfo data={[]}/>
                            </TabPane>
                            <TabPane tab="标签规则" key="2">
                                <LableRules data={[]}/>
                            </TabPane>
                        </Tabs>
                    </Row>
                </Card>
                <MoveTreeNode visible={moveVisible} handleOk={() => this.onHandleCancelMove('ok')} handleCancel={() => this.onHandleCancelMove('cancel')}/>
            </div>
        );
    }
}
