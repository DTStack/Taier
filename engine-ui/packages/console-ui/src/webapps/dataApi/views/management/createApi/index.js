import React, { Component } from 'react';
import { Card, Steps, message, Spin } from 'antd';
import { connect } from 'react-redux'

import GoBack from 'main/components/go-back'

import BasicProperties from './basicProperties'
import ParamsConfig from './params'
import RegisterParams from './registerParams';
import Complete from './complete'
import ModeChoose from './modeChoose'
import TestApi from './testApi'
import { apiMarketActions } from '../../../actions/apiMarket';
import { apiManageActions } from '../../../actions/apiManage';
import { dataSourceActions } from '../../../actions/dataSource';
import { apiManageActionType } from '../../../consts/apiManageActionType';
import utils from '../../../../../utils';
import ColumnsModel from '../../../model/columnsModel';
import InputColumnModel from '../../../model/inputColumnModel';
import ErrorColumnModel from '../../../model/errroColumnModel';
import ConstColumnModel from '../../../model/constColumnModel';

import { API_TYPE } from '../../../consts';

const Step = Steps.Step;

const mapStateToProps = state => {
    const { user, apiMarket, dataSource, apiManage } = state;
    return { apiMarket, user, dataSource, apiManage }
};

const mapDispatchToProps = dispatch => ({
    getCatalogue (pid) {
        return dispatch(apiMarketActions.getCatalogue(pid));
    },
    getSecurityList () {
        return dispatch(apiManageActions.getSecuritySimpleList());
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
    /**
     * 关闭api编辑的提示
     */
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
        registerParams: {},
        complete: {},
        testApi: {},
        mode: undefined,
        loading: false,
        apiEdit: false, // 编辑还是新建
        isSaveResult: false

    }
    // eslint-disable-next-line
    UNSAFE_componentWillMount () {
        const apiId = utils.getParameterByName('apiId');
        if (apiId) {
            this.setState({
                loading: true,
                apiEdit: true
            })
        }
        this.props.getSecurityList();
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
                    if (success && apiId) {
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
    /**
     * 将服务端columns转换为本地需要的格式
     * @param {arr} columns 要转换的columns
     */
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
        const isRegister = utils.getParameterByName('isRegister');
        let newState = {
            loading: false,
            mode: data.paramCfgType,
            basicProperties: {
                APIGroup: this.getInitCatagoryList(data.catalogueId),
                APIName: data.name,
                APIPath: data.apiPath,
                APIdescription: data.apiDesc,
                originalPath: data.originalPath,
                originalHost: data.originalHost,
                callLimit: data.reqLimit,
                method: data.reqType,
                protocol: data.protocol,
                responseType: data.responseType,
                reqType: data.reqType,
                securityGroupIds: data.securityGroupIds
            },
            paramsConfig: {
                dataSourceType: data.dataSourceType,
                dataSrcId: data.dataSrcId,
                tableName: data.tableName,
                resultPage: data.respPageSize,
                resultPageChecked: data.allowPaging,
                sql: data.sql
            },
            registerParams: {},
            testApi: {
                inFields: data.inFields && data.inFields.inFields,
                respJson: data.respJson
            }
        };
        if (isRegister) {
            let inputParam = data.inputParam || [];
            let constParam = inputParam.filter((item) => {
                return item.constant
            })
            inputParam = inputParam.filter((item) => {
                return !item.constant;
            })
            newState.registerParams.successValue = data.successRespJson;
            newState.registerParams.errorValue = data.errorRespJson;
            newState.registerParams.bodyDesc = data.bodyDesc;
            newState.registerParams.inputParam = inputParam.map((item) => {
                return new InputColumnModel(item);
            })
            newState.registerParams.constParam = constParam.map((item) => {
                return new ConstColumnModel(item);
            })
            newState.registerParams.errorCodeList = (data.errorCodeList || []).map((item) => {
                return new ErrorColumnModel(item);
            })
            newState.registerParams.wsdlXml = data.wsdlXml;
            newState.registerParams.saveWsdlXml = data.saveWsdlXml;
        } else {
            newState.paramsConfig = {
                dataSourceType: data.dataSourceType,
                dataSrcId: data.dataSrcId,
                tableName: data.tableName,
                resultPage: data.respPageSize,
                resultPageChecked: data.allowPaging,
                sql: data.sql,
                inputParam: ColumnsModel.exchangeServerParams(data.inputParam),
                outputParam: ColumnsModel.exchangeServerParams(data.outputParam)
            }
        }
        this.setState(newState)
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
    registerParams (data, isStay) {
        isStay = typeof isStay == 'boolean' ? isStay : false;
        let newState = {
            registerParams: data || {}
        }
        if (!isStay) {
            newState.current = 2;
        }
        this.setState(newState)
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

    // next () {
    //     const { key } = steps[this.state.current];
    //     if (this.state[key] && this.state[key].pass) {
    //         const current = this.state.current + 1;
    //         this.setState({ current });
    //     }
    // }

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
    createParamsConfig () {
        const { paramsConfig } = this.state;
        let result = {
            dataSrcId: paramsConfig.dataSrcId,
            tableName: paramsConfig.tableName,
            dataSourceType: paramsConfig.dataSourceType,
            respPageSize: paramsConfig.respPageSize,
            allowPaging: paramsConfig.resultPageChecked ? 1 : 0,
            sql: paramsConfig.sql,
            inputParam: [],
            outputParam: []
        }
        for (let i in paramsConfig.inputParam) {
            let item = paramsConfig.inputParam[i];
            result.inputParam.push({
                fieldName: item.columnName,
                paramName: item.paramsName,
                paramType: item.type,
                operator: item.operator,
                required: item.required,
                desc: item.desc
            })
        }
        for (let i in paramsConfig.outputParam) {
            let item = paramsConfig.outputParam[i];
            result.outputParam.push({
                fieldName: item.columnName,
                paramName: item.paramsName,
                paramType: item.type,
                desc: item.desc
            })
        }
        return result;
    }
    createBasicProperties () {
        const { basicProperties } = this.state;
        let result = {};
        result.name = basicProperties.APIName;// api名字
        result.catalogueId = basicProperties.APIGroup && basicProperties.APIGroup[basicProperties.APIGroup.length - 1];// 分组
        result.apiDesc = basicProperties.APIdescription;// 描述
        result.reqLimit = basicProperties.callLimit;// 调用限制
        result.securityGroupIds = basicProperties.securityGroupIds;// 安全组
        result.apiPath = basicProperties.APIPath;// api路径
        result.originalHost = basicProperties.originalHost;// 后端host
        result.originalPath = basicProperties.originalPath;// 后端path
        result.reqType = basicProperties.method;// http method
        result.protocol = basicProperties.protocol;// 协议
        result.responseType = basicProperties.responseType;// 返回类型
        return result;
    }
    createRegisterParams () {
        const { registerParams } = this.state;
        let result = {
            inputParam: [],
            constParam: []
        }
        result.successRespJson = registerParams.successValue;
        result.errorRespJson = registerParams.errorValue;
        result.errorCodeList = registerParams.errorCodeList;
        result.bodyDesc = registerParams.bodyDesc;
        result.inputParam = (registerParams.inputParam || []).concat((registerParams.constParam || []).map((item) => {
            return {
                ...item,
                constant: true
            }
        }));
        result.wsdlXml = registerParams.saveWsdlXml != null && registerParams.saveWsdlXml === 1 ? registerParams.wsdlXml : null;
        result.saveWsdlXml = registerParams.saveWsdlXml;
        return result;
    }
    createApiServerParams () {
        const { isSaveResult, mode, testApi } = this.state;
        const params = {}
        const isRegister = utils.getParameterByName('isRegister');
        params.id = utils.getParameterByName('apiId')
        params.paramCfgType = mode;// 模式

        let basicPropertiesParams = this.createBasicProperties();
        Object.assign(params, basicPropertiesParams);
        if (isRegister) {
            let paramsConfigParams = this.createRegisterParams();
            Object.assign(params, paramsConfigParams);
        } else {
            let paramsConfigParams = this.createParamsConfig();
            Object.assign(params, paramsConfigParams);
        }

        if (isSaveResult && !isRegister) {
            params.inFields = testApi.inFields;
            params.respJson = testApi.respJson;
        }
        params.apiType = isRegister ? API_TYPE.REGISTER : API_TYPE.NORMAL;
        return params;
    }
    apiTest (values, extValues = {}) {
        let params = this.createApiServerParams();
        const { pageNo, pageSize, ...other } = values;
        params.pageNo = pageNo;
        params.pageSize = pageSize;
        params = { ...params, ...extValues };
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
        this.saveData(type, data, () => {
            this.save(true)
        });
        // this.props.router.goBack();
    }
    saveData (type, data, callback) {
        data = data || {};
        this.setState({
            [type]: data
        }, () => {
            callback && callback();
        })
    }
    getParamsView () {
        const isRegister = utils.getParameterByName('isRegister');
        if (isRegister) {
            return {
                key: 'registerParams',
                title: '参数配置',
                content: RegisterParams
            }
        } else {
            return {
                key: 'paramsConfig',
                title: '参数配置',
                content: ParamsConfig
            }
        }
    }
    render () {
        const {
            mode,
            paramsConfig,
            basicProperties,
            registerParams,
            apiEdit,
            loading,
            isSaveResult
        } = this.state;

        const steps = [
            {
                key: 'basicProperties',
                title: '基本属性',
                content: BasicProperties
            },
            this.getParamsView(),
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
        const isRegister = utils.getParameterByName('isRegister');
        const title = isRegister ? '注册API' : '新建API';
        return (
            <div className="g-datamanage">
                <h1 className="box-title"> <GoBack url="/api/manage"></GoBack> {apiEdit ? '编辑API' : title}</h1>
                {loading ? <div style={{ textAlign: 'center', marginTop: '400px' }}>
                    <Spin size="large" />
                </div>
                    : <Card
                        style={{ padding: '20px', minHeight: 'calc(100% - 65px)' }}
                        className="box-2"
                        noHovering
                    >
                        {
                            (mode || mode == 0 || isRegister) ? (
                                <div>
                                    <Steps current={this.state.current}>
                                        <Step title="基本属性" />
                                        <Step title="参数配置" />
                                        <Step title="完成" />
                                    </Steps>
                                    <Content
                                        isRegister={isRegister}
                                        apiEdit={apiEdit}
                                        dataSourceId={this.state.basicProperties.dataSource}
                                        tableId={this.state.basicProperties.table}
                                        {...this.props}
                                        {...this.state[key]}
                                        registerParams={registerParams}
                                        paramsConfig={paramsConfig}
                                        basicProperties={basicProperties}
                                        reDo={this.reDo.bind(this)}
                                        prev={this.prev.bind(this)}
                                        mode={mode}
                                        isSaveResult={isSaveResult}
                                        saveData={this.saveData.bind(this, key)}
                                        cancelAndSave={this.cancelAndSave.bind(this, key)}
                                        apiTest={this.apiTest.bind(this)}
                                        dataChange={this[key].bind(this)}
                                        saveResult={this.saveResult.bind(this)}
                                        changeRegisterParams={this.registerParams.bind(this)}
                                    ></Content>
                                </div>
                            ) : <ModeChoose chooseMode={this.chooseMode.bind(this)} />}
                    </Card>}
            </div>
        )
    }
}
export default NewApi;
