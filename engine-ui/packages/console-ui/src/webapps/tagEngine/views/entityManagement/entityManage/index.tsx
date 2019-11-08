import * as React from 'react';
import { Button, Table, Modal, message as Message } from 'antd';

const confirm = Modal.confirm;
interface IProps {
    history: any;
    changeEntity?: (params: any) => void;
    getEntityList?: (parmas: any) => void;
}
interface IState {
    entityId: string;
    loading: boolean;
    pageNo: number;
    pageSize: number;
    total: number;
    dataSource: any;
    visible: boolean;
}
class EntityManage extends React.PureComponent<IProps, IState> {
    state: IState = {
        entityId: '',
        loading: true,
        pageNo: 1,
        pageSize: 10,
        total: 0,
        visible: false,
        dataSource: []
    };
    componentDidMount () {
        this.loadMainData(false);
    }
    loadMainData (isClear: any) {
        if (isClear) {
        // 清除一些过滤条件
        }
    }
    onTableChange = (pagination: any, filters: any, sorter: any) => {
        const { current, pageSize } = pagination;
        this.setState(
            {
                pageNo: current,
                pageSize: pageSize,
                loading: true
            },
            () => {
                this.loadMainData(false);
            }
        );
    };
    createEntity = () => {

    };
    onHandleSet = (id: any) => {

    }
    onHandleView = (id: any, type: any) => {

    };
    onDelete = (id: any) => {
        confirm({
            title: '删除操作',
            content: '删除功能将删除实体关联的其他信息，需谨慎提示，是否删除该实体？',
            okText: '确定',
            cancelText: '取消',
            onOk () {
                Message.success('成功')
            },
            onCancel () {
                console.log('Cancel');
            }
        });
    };
    onClose = () => {
        this.setState({
            visible: false,
            entityId: ''
        })
    }
    render () {
        const {
            loading,
            dataSource
        } = this.state;
        const columns = [
            {
                title: '实体名称',
                dataIndex: 'entityName',
                key: 'entityName',
                width: 110
            },
            {
                title: '实体编码',
                dataIndex: 'entityCode',
                key: 'entityCode',
                width: 110
            },
            {
                title: '来源索引',
                dataIndex: 'entityIndex',
                key: 'entityIndex',
                width: 110
            },
            {
                title: '实体描述',
                dataIndex: 'description',
                key: 'description',
                width: 200
            },
            {
                title: '创建时间',
                dataIndex: 'createAt',
                key: 'createAt',
                width: 200
            },
            {
                title: '创建人',
                dataIndex: 'creator',
                width: 100,
                key: 'creator'
            },
            {
                title: '操作',
                dataIndex: 'id',
                key: 'id',
                width: 200,
                render: (id: any) => {
                    return (
                        <div className="operate_btn_wrap">
                            <a onClick={() => this.onHandleView(id, 'edit')}>编辑</a>
                        </div>
                    );
                }
            }
        ];
        return (
            <div className="entity-manage">
                <div className="title">
                    <Button type="primary" onClick={this.createEntity}>
                           新增实体
                    </Button>

                </div>
                <div className="entity_manage_content">
                    <Table
                        dataSource={dataSource}
                        columns={columns}
                        loading={loading}
                        rowKey="id"
                        onChange={this.onTableChange}
                        pagination={false}
                    />
                </div>
            </div>
        );
    }
}
export default EntityManage;
