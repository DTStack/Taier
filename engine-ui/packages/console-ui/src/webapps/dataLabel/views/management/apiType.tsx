import * as React from 'react';
import { connect } from 'react-redux'
import { Card, message } from 'antd';
import ApiTypeTree from './apiTypeTree'
import GoBack from 'main/components/go-back'
import { apiMarketActions } from '../../actions/apiMarket';
import { apiManageActions } from '../../actions/apiManage';
import { apiMarketActionType as ACTION_TYPE } from '../../consts/apiMarketActionType';

const mapStateToProps = (state: any) => {
    const { user, apiMarket } = state;
    return { apiMarket, user }
};

const mapDispatchToProps = (dispatch: any) => ({
    getCatalogue(pid: any) {
        dispatch(apiMarketActions.getCatalogue(pid));
    },
    deleteCatalogue(pid: any) {
        return dispatch(apiManageActions.deleteCatalogue({ id: pid }));
    },
    updateCatalogue(pid: any, nodeName: any) {
        return dispatch(apiManageActions.updateCatalogue({ id: pid, nodeName }));
    },
    addCatalogue(pid: any, nodeName: any) {
        return dispatch(apiManageActions.addCatalogue({ pid, nodeName }));
    },
    addCatalogueEdit(tree: any) {
        dispatch({
            type: ACTION_TYPE.GET_CATALOGUE,
            payload: tree
        });
    }
});

@(connect(mapStateToProps, mapDispatchToProps) as any)
class ApiType extends React.Component<any, any> {
    onSelect = (selectedKeys: any, info: any) => {
        console.log('selected', selectedKeys, info);
    }
    onCheck = (checkedKeys: any, info: any) => {
        console.log('onCheck', checkedKeys, info);
    }
    componentDidMount () {
        this.props.getCatalogue(0);
    }
    deleteCatalogue(pid: any) {
        this.props.deleteCatalogue(pid)
            .then(
                (res: any) => {
                    if (res) {
                        message.success('删除成功')
                    }
                    this.props.getCatalogue(0);
                }
            )
    }
    addCatalogue(id: any, name: any) {
        this.props.addCatalogue(id, name)
            .then(
                (res: any) => {
                    if (res) {
                        message.success('新增成功')
                    }
                    this.props.getCatalogue(0);
                }
            )
    }
    updateCatalogue(id: any, nodeName: any) {
        this.props.updateCatalogue(id, nodeName)
            .then(
                (res: any) => {
                    if (res) {
                        message.success('更改成功')
                    }
                    this.props.getCatalogue(0);
                }
            )
    }
    render () {
        return (
            <div className="m-card">
                <h1 className="box-title"> <GoBack></GoBack> 类目管理</h1>
                <Card
                    className="box-2 g-datamanage"
                    noHovering
                >
                    <ApiTypeTree getCatalogue={this.props.getCatalogue} addCatalogue={this.addCatalogue.bind(this)} addCatalogueEdit={this.props.addCatalogueEdit} updateCatalogue={this.updateCatalogue.bind(this)} deleteCatalogue={this.deleteCatalogue.bind(this)} tree={this.props.apiMarket.apiCatalogue}></ApiTypeTree>

                </Card>

            </div>
        )
    }
}
export default ApiType;
