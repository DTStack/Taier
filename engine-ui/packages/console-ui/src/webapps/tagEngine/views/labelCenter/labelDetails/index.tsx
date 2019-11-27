import * as React from 'react';
import { Tabs, Card, Button, Row, Col, message as Message, Popconfirm } from 'antd';
import Breadcrumb from '../../../components/breadcrumb';
import { GroupStatus } from '../../../components/status';
import MoveTreeNode from './components/moveTreeNode';
import BasicInfo from './components/basicInfo';
import LableRules from './components/labelRules';
import { API } from '../../../api/apiMap';
import './style.scss';

const TabPane = Tabs.TabPane;
interface IProps {
    router?: any;
    location?: any;
}
interface IState {
    tagId: string|number;
    entityId: string|number;
    visible: boolean;
    moveVisible: boolean;
    data: any;
}

export default class LabelDetails extends React.PureComponent<IProps, IState> {
    state: IState = {
        visible: false,
        moveVisible: false,
        tagId: '',
        entityId: '',
        data: {}
    };
    componentDidMount () {
        const { location } = this.props;
        const { tagId, entityId } = location.state;
        this.setState({
            tagId,
            entityId
        })
        this.getTagDetail(tagId);
    }
    getTagDetail = (tagId) => {
        API.getTagDetail({
            tagId
        }).then(res => { // 获取主键列表
            const { code, data } = res;
            if (code == 1) {
                this.setState({ data })
            }
        })
    }
    onHandleEdit = () => {
        const { tagId } = this.state;
        this.props.router.push('/editAtomicLabel', { tagId })
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
        const { tagId } = this.state;
        API.deleteTag({
            tagId: tagId
        }).then(res => {
            const { code } = res;
            if (code) {
                Message.success('删除成功！');
                this.props.router.goBack();
            }
        })
    }
    render () {
        const { moveVisible, data } = this.state;
        const { location } = this.props;
        const { tagId, entityId } = location.state;
        const breadcrumbNameMap = [
            {
                path: '/labelCenter',
                name: '标签管理'
            },
            {
                path: '/labelDetails',
                name: '标签详情'
            }
        ];
        const emptyText = '---'
        return (
            <div className="labelDetails">
                <Breadcrumb breadcrumbNameMap={breadcrumbNameMap} />
                <Card
                    noHovering
                    bordered={false}
                    className="dt-cardTabs"
                    bodyStyle={{ padding: 20 }}
                >
                    <Row>
                        <Col className="left">
                            <div className="title">{data.tagName || emptyText}</div>
                            <div className="subTitle"><span className="update">最近更新时间：{data.updateAt || emptyText}</span> <GroupStatus value={data.tagStatus}/></div>
                        </Col>
                        <Col className="right">
                            <Button type="primary" className="btn" onClick={this.onHandleEdit}>编辑</Button>
                            <Popconfirm placement="top" title="确定删除此标签？" onConfirm={this.onHandleDelete}>
                                <Button type="primary" className="btn">删除</Button>
                            </Popconfirm>

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
                                <BasicInfo data={data}/>
                            </TabPane>
                            <TabPane tab="标签规则" key="2">
                                <LableRules entityId={entityId} tagId={tagId}/>
                            </TabPane>
                        </Tabs>
                    </Row>
                </Card>
                <MoveTreeNode visible={moveVisible} id={ tagId } entityId={entityId} handleOk={() => this.onHandleCancelMove('ok')} handleCancel={() => this.onHandleCancelMove('cancel')}/>
            </div>
        );
    }
}
