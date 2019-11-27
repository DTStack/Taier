import * as React from 'react';
import Breadcrumb from '../../../../components/breadcrumb';
import ModuleTitle from '../../../../components/moduleTitle';
import BaseInfor from './baseInfor';
import DimensionData from './dimensionData';
import { hashHistory } from 'react-router';
import { get } from 'lodash';
import { Button } from 'antd';
import { API } from '../../../../api/apiMap';

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
        let id = get(this.props.location, 'state.id');
        if (id) {
            this.setState({
                entityInfor: get(this.props.location, 'state') || {}
            }, () => {
                this.getEntityData();
            })
        }
    }

    getEntityData = () => {
        const { entityInfor } = this.state;
        API.getEntityAttrs({
            entityId: entityInfor.id
        }).then((res: any) => {
            const { data = [], code } = res;
            if (code === 1) {
                this.setState({
                    entityInfor: {
                        ...this.state.entityInfor,
                        propertyData: data
                    }
                });
            }
        })
    }

    handleGotoEdit = () => {
        const { entityInfor } = this.state;
        hashHistory.push({ pathname: '/entityManage/edit', state: { ...entityInfor } })
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
                        <Button type='primary' onClick={this.handleGotoEdit}>编辑</Button>
                    }
                />
                <BaseInfor infor={entityInfor} />
                <ModuleTitle title={'数据维度'} />
                <DimensionData regetData={this.getEntityData} infor={entityInfor} />
            </div>
        )
    }
}
