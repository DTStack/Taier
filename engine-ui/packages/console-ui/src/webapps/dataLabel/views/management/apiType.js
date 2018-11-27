import React, { Component } from 'react';
import { connect } from 'react-redux'
import { Card, message } from 'antd';
import ApiTypeTree from './apiTypeTree'
import GoBack from 'main/components/go-back'
import { apiMarketActions } from '../../actions/apiMarket';
import { apiManageActions } from '../../actions/apiManage';
import { apiMarketActionType as ACTION_TYPE } from '../../consts/apiMarketActionType';

const mapStateToProps = state => {
    const { user, apiMarket } = state;
    return { apiMarket, user }
};

const mapDispatchToProps = dispatch => ({
    getCatalogue (pid) {
        dispatch(apiMarketActions.getCatalogue(pid));
    },
    deleteCatalogue (pid) {
        return dispatch(apiManageActions.deleteCatalogue({ id: pid }));
    },
    updateCatalogue (pid, nodeName) {
        return dispatch(apiManageActions.updateCatalogue({ id: pid, nodeName }));
    },
    addCatalogue (pid, nodeName) {
        return dispatch(apiManageActions.addCatalogue({ pid, nodeName }));
    },
    addCatalogueEdit (tree) {
        dispatch({
            type: ACTION_TYPE.GET_CATALOGUE,
            payload: tree
        });
    }
});

@connect(mapStateToProps, mapDispatchToProps)
class ApiType extends Component {
    onSelect = (selectedKeys, info) => {
        console.log('selected', selectedKeys, info);
    }
    onCheck = (checkedKeys, info) => {
        console.log('onCheck', checkedKeys, info);
    }
    componentDidMount () {
        this.props.getCatalogue(0);
    }
    deleteCatalogue (pid) {
        this.props.deleteCatalogue(pid)
            .then(
                (res) => {
                    if (res) {
                        message.success('删除成功')
                    }
                    this.props.getCatalogue(0);
                }
            )
    }
    addCatalogue (id, name) {
        this.props.addCatalogue(id, name)
            .then(
                (res) => {
                    if (res) {
                        message.success('新增成功')
                    }
                    this.props.getCatalogue(0);
                }
            )
    }
    updateCatalogue (id, nodeName) {
        this.props.updateCatalogue(id, nodeName)
            .then(
                (res) => {
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
