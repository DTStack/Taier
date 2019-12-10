import * as React from 'react';
import { Tabs, Card, Button, Row, Col, message as Message } from 'antd';
import { hashHistory } from 'react-router';
import Breadcrumb from '../../../components/breadcrumb';
import { GroupStatus } from '../../../components/status';
import MoveTreeNode from './components/moveTreeNode';
import BasicInfo from './components/basicInfo';
import LableRules from './components/labelRules';
import DerivativeRules from './components/derivativeRules';
import DeleteModal from '../../../components/deleteModal'

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
    deleteVisible: boolean;
    canDelete: boolean;
    data: any;
}

export default class LabelDetails extends React.PureComponent<IProps, IState> {
    state: IState = {
        visible: false,
        moveVisible: false,
        tagId: '',
        entityId: '',
        data: {},
        deleteVisible: false,
        canDelete: false
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
        const { tagId, entityId, data } = this.state;
        hashHistory.push({ pathname: data.tagType == '原子标签' ? '/editAtomicLabel' : '/createLabel', query: { tagId, entityId } })
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
        API.canDeleteTag({
            tagId
        }).then((res: any) => {
            const { data, code } = res;
            if (code === 1) {
                this.setState({
                    deleteVisible: true,
                    canDelete: data
                })
            }
        })
    }
    handleDeleteModel = (type: string) => {
        const { tagId } = this.state;
        if (type == 'ok') {
            API.deleteTag({
                tagId
            }).then(res => {
                const { code } = res;
                if (code === 1) {
                    Message.success('删除成功！');
                    this.props.router.goBack();
                }
            })
        }
        this.setState({
            deleteVisible: false,
            canDelete: false
        })
    }
    render () {
        const { moveVisible, data, canDelete, deleteVisible } = this.state;
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
                                <BasicInfo data={data}/>
                            </TabPane>
                            <TabPane tab="标签规则" key="2">
                                {
                                    data.tagType == '原子标签' ? (
                                        <LableRules entityId={entityId} tagId={tagId}/>
                                    ) : (
                                        <DerivativeRules entityId={entityId} tagId={tagId}/>
                                    )
                                }
                            </TabPane>
                        </Tabs>
                    </Row>
                </Card>
                <MoveTreeNode visible={moveVisible} id={ tagId } entityId={entityId} handleOk={() => this.onHandleCancelMove('ok')} handleCancel={() => this.onHandleCancelMove('cancel')}/>
                <DeleteModal
                    title={'删除标签'}
                    content={canDelete ? '删除标签后，无法恢复请谨慎操作！' : '解除当前标签的引用关系后可删除'}
                    visible={deleteVisible}
                    onCancel={this.handleDeleteModel.bind(this, 'cancel')}
                    onOk={this.handleDeleteModel.bind(this, 'ok')}
                    footer={
                        !canDelete ? <Button type="primary" onClick={this.handleDeleteModel.bind(this, 'cancel')}>我知道了</Button> : undefined
                    }
                />
            </div>
        );
    }
}
