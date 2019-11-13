import * as React from 'react';
import Breadcrumb from '../../../../components/breadcrumb';
import ModuleTitle from '../../../../components/moduleTitle';
import BaseInfor from './baseInfor';
import DimensionData from './dimensionData';
import { Button } from 'antd';

interface IProps {
    location: any;
}

interface IState {
    entityInfor: any;
}

export default class EntityDetail extends React.Component<IProps, IState> {
    state: IState = {
        entityInfor: {}
    }

    componentDidMount () {
        this.getEntityData();
    }

    getEntityData = () => {
        this.setState({
            entityInfor: {
                id: 10,
                dataSource: 'xxxxxx',
                table: 'xxxxxx',
                key: 'key',
                keyName: '主键',
                count: 300,
                creator: '某某人',
                createTime: '2019-12-20 12:22:42',
                desc: 'xxxxxx',
                propertyData: [
                    { id: '1', isKey: true, isMultiply: '是', name: 'xxxxxxx', chName: 'xxx1', type: 'char', propertyNum: 200, isRelateLabel: '是' },
                    { id: '2', isKey: false, isMultiply: '否', name: 'xxxxx', chName: 'xxx2', type: 'number', propertyNum: 100, isRelateLabel: '否' },
                    { id: '3', isKey: false, isMultiply: '否', name: 'xxxxx', chName: 'xxx3', type: 'number', propertyNum: 30, isRelateLabel: '是' }
                ]
            }
        })
    }

    render () {
        const { entityInfor } = this.state;
        const breadcrumbNameMap = [
            {
                path: '/entityManage',
                name: '实体管理'
            },
            {
                path: '',
                name: '实体详情'
            }
        ];
        return (
            <div className="entity-detail">
                <Breadcrumb breadcrumbNameMap={breadcrumbNameMap} style={{ marginBottom: 10 }} />
                <ModuleTitle
                    title={'基本信息'}
                    extra={
                        <Button type='primary'>编辑</Button>
                    }
                />
                <BaseInfor infor={entityInfor} />
                <ModuleTitle title={'数据维度'} />
                <DimensionData dataSource={entityInfor.propertyData} />
            </div>
        )
    }
}
