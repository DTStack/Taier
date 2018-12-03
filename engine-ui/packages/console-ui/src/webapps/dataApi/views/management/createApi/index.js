import React, { Component } from 'react';
import { Card, Steps, message, Spin } from 'antd';
import { connect } from 'react-redux'

import GoBack from 'main/components/go-back'

import BasicProperties from './basicProperties'
import ParamsConfig from './paramsConfig'
import Complete from './complete'
import ModeChoose from './modeChoose'
import TestApi from './testApi'
import { apiMarketActions } from '../../../actions/apiMarket';
import { apiManageActions } from '../../../actions/apiManage';
import { dataSourceActions } from '../../../actions/dataSource';
import { apiManageActionType } from '../../../consts/apiManageActionType';
import utils from '../../../../../utils';
import ColumnsModel from '../../../model/columnsModel';

const Step = Steps.Step;

const mapStateToProps = state => {
    const { user, apiMarket, dataSource, apiManage } = state;
    return { apiMarket, user, dataSource, apiManage }
};

const mapDispatchToProps = dispatch => ({
    getCatalogue (pid) {
        return dispatch(apiMarketActions.getCatalogue(pid));
    },
    getDataSourceList (type) {
        return dispatch(apiManageActions.getDataSourceByBaseInfo({ type: type }));
    },
    createApi (params) {
        return dispatch(apiManageActions.createApi(params));
    },
    tablelist (sourceId) {
        return dispatch(apiManageActions.tablelist({ sourceId }));
    },
    tablecolumn (sourceId, tableName) {
        return dispatch(apiManageActions.tablecolumn({ sourceId, tableName }));
    },
    previewData (dataSourceId, tableName) {
        return dispatch(apiManageActions.previewData({ dataSourceId, tableName }));
    },
    saveOrUpdateApiInfo (params) {
        return dispatch(apiManageActions.saveOrUpdateApiInfo(params));
    },
    sqlFormat (sql, type) {
        return dispatch(apiManageActions.sqlFormat({ sql, type }));
    },
    sqlParser (sql, sourceId) {
        return dispatch(apiManageActions.sqlParser({ sql, sourceId }));
    },
    getApiInfo (apiId) {
        return dispatch(apiManageActions.getApiInfo({ apiId }));
    },
    apiTest (params) {
        return dispatch(apiManageActions.apiTest(params));
    },
    getDataSourcesType () {
        return dispatch(dataSourceActions.getDataSourcesType());
    },
    disAbleTipChange () {
        return dispatch({
            type: apiManageActionType.CHANGE_DISABLE_TIP
        })
    }
});

@connect(mapStateToProps, mapDispatchToProps)
class NewApi extends Component {
    state = {
        current: 0,
        basicProperties: {},
        paramsConfig: {},
        complete: {},
        testApi: {},
        mode: undefined,
        loading: false,
        apiEdit: false,
        isSaveResult: false,
        InputIsEdit: true,
        OutputIsEdit: true

    }
    componentWillMount () {
        const apiId = utils.getParameterByName('apiId');
        if (apiId) {
            this.setState({
                loading: true,
                apiEdit: true
            })
            this.props.getCatalogue(0)
                .then(
                    (res) => {
                        if (res) {
                            return true;
                        }
                    }
                )
                .then(
                    (success) => {
                        if (success) {
                            this.props.getApiInfo(apiId)
                                .then(
                                    (res) => {
                                        if (res) {
                                            this.setDefault(res.data);
                                        }
                                    }
                                )
                        }
                    }
                );
        }
    }
    changeColumnsEditStatus (input, output) {
        this.setState({
            InputIsEdit: input,
            OutputIsEdit: output
        })
    }
    saveResult (e) {
        this.setState({
            isSaveResult: e.target.checked
        })
    }
    getInitCatagoryList (value, catagorys) {
        if (!value && value != 0) {
            return null;
        }
        const tree = catagorys || this.props.apiMarket.apiCatalogue;
        let arr = [];
        function exchangeTree (data) {
            if (!data || data.length < 1) {
                return null;
            }
            for (let i = 0; i < data.length; i++) {
                let item = data[i];
                if (item.api) {
                    continue;
                }
                if (item.id == value) {
                    arr.push(item.id);
                    return item.id;
                }
                if (exchangeTree(item.childCatalogue)) {
                    arr.push(item.id);
                    return item.id
                }
            }
            return null;
        }
        if (exchangeTree(tree)) {
            return arr.reverse();
        }
        return null;
    }
    exchangeServerParams (columns) {
        if (!columns) {
            return [];
        }
        return columns.map(
            (column) => {
                return new ColumnsModel({
                    key: column.fieldName,
                    type: column.paramType,
                    paramsName: column.paramName,
                    operator: column.operator,
                    desc: column.desc,
                    required: column.required
                })
            }
        );
    }
    setDefault (data) {
        this.setState({
            loading: false,
            mode: data.paramCfgType,
            basicProperties: {
                APIGroup: this.getInitCatagoryList(data.catalogueId),
                APIName: data.name,
                APIPath: data.apiPath,
                APIdescription: data.apiDesc,
                callLimit: data.reqLimit,
                method: data.reqType,
                protocol: data.protocol,
                responseType: data.responseType,
                reqType: data.reqType
            },
            paramsConfig: {
                dataSourceType: data.dataSourceType,
                dataSrcId: data.dataSrcId,
                tableName: data.tableName,
                resultPage: data.respPageSize,
                resultPageChecked: data.allowPaging,
                sql: data.sql,
                inputParam: this.exchangeServerParams(data.inputParam),
                outputParam: this.exchangeServerParams(data.outputParam)
            },
            testApi: {
                inFields: data.inFields && data.inFields.inFields,
                respJson: data.respJson
            }
        })
    }
    basicProperties (data) {
        this.setState({
            basicProperties: data || {},
            current: 1
        })
    }
    chooseMode (mode) {
        this.setState({
            mode: mode
        })
    }
    paramsConfig (data) {
        this.setState({
            paramsConfig: data || {},
            current: 2
        })
    }
    complete (data) {
        this.setState({
            complete: data
        })
    }
    testApi () {
        this.save(true);
    }
    reDo () {
        this.setState({
            current: 0,
            basicProperties: {},
            paramsConfig: {},
            complete: {}
        })
    }

    next () {
        const { key } = steps[this.state.current];
        if (this.state[key] && this.state[key].pass) {
            const current = this.state.current + 1;
            this.setState({ current });
        }
    }

    prev () {
        const current = this.state.current - 1;
        this.setState({ current });
    }
    save (back) {
        const params = this.createApiServerParams();

        this.props.saveOrUpdateApiInfo(params)
            .then(
                (res) => {
                    if (res) {
                        message.success('保存成功！')
                        if (back) {
                            this.props.router.push('/api/manage');
                        }
                    }
                }
            )
    }
    createApiServerParams () {
        const { isSaveResult } = this.state;
        const params = {}
        params.id = utils.getParameterByName('apiId')
        params.paramCfgType = this.state.mode;// 模式
        params.name = this.state.basicProperties.APIName;// api名字
        params.catalogueId = this.state.basicProperties.APIGroup && this.state.basicProperties.APIGroup[this.state.basicProperties.APIGroup.length - 1];// 分组
        params.apiDesc = this.state.basicProperties.APIdescription;// 描述
        params.dataSrcId = this.state.paramsConfig.dataSrcId;// 数据源
        params.tableName = this.state.paramsConfig.tableName;// 数据表
        params.dataSourceType = this.state.paramsConfig.dataSourceType;// 数据源类型
        params.reqLimit = this.state.basicProperties.callLimit;// 调用限制
        params.apiPath = this.state.basicProperties.APIPath;// api路径
        params.reqType = this.state.basicProperties.method;// http method
        params.protocol = this.state.basicProperties.protocol;// 协议
        params.responseType = this.state.basicProperties.responseType;// 返回类型
        params.respPageSize = this.state.paramsConfig.resultPage;// 分页条数
        params.allowPaging = this.state.paramsConfig.resultPageChecked ? 1 : 0;// 是否分页
        params.inputParam = [];
        params.outputParam = [];
        params.sql = this.state.paramsConfig.sql;// sql
        if (isSaveResult) {
            params.inFields = this.state.testApi.inFields;
            params.respJson = this.state.testApi.respJson;
        }
        const data = this.state.paramsConfig;
        for (let i in data.inputParam) {
            let item = data.inputParam[i];

            params.inputParam.push({
                fieldName: item.columnName,
                paramName: item.paramsName,
                paramType: item.type,
                operator: item.operator,
                required: item.required,
                desc: item.desc
            })
        }
        for (let i in data.outputParam) {
            let item = data.outputParam[i];
            params.outputParam.push({
                fieldName: item.columnName,
                paramName: item.paramsName,
                paramType: item.type,
                desc: item.desc
            })
        }
        return params;
    }
    apiTest (values) {
        const params = this.createApiServerParams();
        const { pageNo, pageSize, ...other } = values;
        params.pageNo = pageNo;
        params.pageSize = pageSize;
        const keys = Object.entries(other);
        const inFields = {};
        keys.map(
            ([key, value]) => {
                inFields[key] = value || undefined;
            }
        )
        params.inFields = inFields;
        this.setState({
            isSaveResult: false
        })
        return this.props.apiTest(params)
            .then(
                (res) => {
                    if (res) {
                        this.setState({
                            testApi: {
                                respJson: res.data,
                                inFields: params
                            }
                        })
                    }
                }
            )
    }
    cancelAndSave (type, data) {
        switch (type) {
        case 'basicProperties': {
            this.setState({
                basicProperties: data || {}
            }, () => {
                this.save(true)
            })
            return;
        }
        case 'paramsConfig': {
            this.setState({
                paramsConfig: data || {}
            }, () => {
                this.save(true)
            })
        }
        case 'complete': {

        }
        }

        // this.props.router.goBack();
    }
    saveData (type, data) {
        switch (type) {
        case 'basicProperties': {
            this.setState({
                basicProperties: data || {}
            })
        }
        case 'paramsConfig': {
            this.setState({
                paramsConfig: data || {}
            })
        }
        case 'complete': {

        }
        }
    }
    render () {
        const { mode, paramsConfig, basicProperties, apiEdit, loading, isSaveResult, InputIsEdit, OutputIsEdit } = this.state;

        const steps = [
            {
                key: 'basicProperties',
                title: '基本属性',
                content: BasicProperties
            },
            {
                key: 'paramsConfig',
                title: '参数配置',
                content: ParamsConfig
            },
            {
                key: 'testApi',
                title: '测试生成',
                content: TestApi
            },
            {
                key: 'complete',
                title: '完成',
                content: Complete
            }
        ]
        const { key, content: Content } = steps[this.state.current];

        return (
            <div className="m-card g-datamanage">
                <h1 className="box-title"> <GoBack url="/api/manage"></GoBack> {apiEdit ? '编辑API' : '新建API'}</h1>
                {loading ? <div style={{ textAlign: 'center', marginTop: '400px' }}>
                    <Spin size="large" />
                </div> : <Card
                    style={{ padding: '20px' }}
                    className="box-2"
                    noHovering
                >
                    {(mode || mode == 0) ? (
                        <div>
                            <Steps current={this.state.current}>
                                <Step title="基本属性" />
                                <Step title="参数配置" />
                                <Step title="完成" />
                            </Steps>
                            <Content
                                apiManage={this.props.apiManage}
                                disAbleTipChange={this.props.disAbleTipChange}
                                apiEdit={apiEdit}
                                dataSourceId={this.state.basicProperties.dataSource}
                                tableId={this.state.basicProperties.table}
                                {...this.props}
                                {...this.state[key]}
                                paramsConfig={paramsConfig}
                                basicProperties={basicProperties}
                                reDo={this.reDo.bind(this)}
                                prev={this.prev.bind(this)}
                                mode={mode}
                                isSaveResult={isSaveResult}
                                InputIsEdit={InputIsEdit}
                                OutputIsEdit={OutputIsEdit}
                                saveData={this.saveData.bind(this, key)}
                                cancelAndSave={this.cancelAndSave.bind(this, key)}
                                apiTest={this.apiTest.bind(this)}
                                dataChange={this[key].bind(this)}
                                saveResult={this.saveResult.bind(this)}
                                changeColumnsEditStatus={this.changeColumnsEditStatus.bind(this)}
                            ></Content>
                        </div>
                    ) : <ModeChoose chooseMode={this.chooseMode.bind(this)} />}
                </Card>}
            </div>
        )
    }
}
export default NewApi;
