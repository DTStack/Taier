import * as React from 'react'
import { Form, Select, message, Icon } from 'antd'

import req from '../../../../consts/reqUrls'
import Api from '../../../../api/console'
import UploadFile from './uploadFile'
import { COMPONENT_TYPE_VALUE, VERSION_TYPE, FILE_TYPE,
    CONFIG_FILE_DESC, DEFAULT_COMP_VERSION } from '../const'
import { isOtherVersion, handleComponentConfig } from '../help'

interface IProps {
    comp: any;
    form: any;
    view: boolean;
    saveCompsData: any[];
    versionData: any;
    clusterName: string;
}

interface IState {
    loading: any;
    principals: any[];
}

const FormItem = Form.Item
const Option = Select.Option
export default class FileConfig extends React.PureComponent<IProps, IState> {
    state: IState = {
        loading: {
            [FILE_TYPE.KERNEROS]: false,
            [FILE_TYPE.PARAMES]: false,
            [FILE_TYPE.CONFIGS]: false
        },
        principals: []
    }

    renderCompsVersion = () => {
        const { getFieldDecorator } = this.props.form
        const { versionData, comp, view } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        let version = isOtherVersion(typeCode) ? versionData[VERSION_TYPE[typeCode]] : versionData.hadoopVersion
        let initialValue = isOtherVersion(typeCode) ? DEFAULT_COMP_VERSION[typeCode] : versionData.hadoopVersion[0].value
        return (
            <FormItem
                label="组件版本"
                colon={false}
                key={`${typeCode}.hadoopVersion`}
            >
                {getFieldDecorator(`${typeCode}.hadoopVersion`, {
                    initialValue: comp?.hadoopVersion ?? initialValue
                })(
                    <Select style={{ width: 172 }} disabled={view}>
                        {version.map((ver: any) => {
                            return <Option value={ver.value} key={ver.key}>{ver.key}</Option>
                        })}
                    </Select>
                )}
            </FormItem>
        )
    }

    // 下载配置文件
    downloadFile = (type: number) => {
        const { form, clusterName, comp } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        const version = form.getFieldValue(`${form}.hadoopVersion`) || '';

        const a = document.createElement('a');
        let param = comp?.id ? `?componentId=${comp.id}&` : '?'
        param = param + `type=${type}&componentType=${typeCode}&hadoopVersion=${version}&clusterName=${clusterName}`;
        a.href = `${req.DOWNLOAD_RESOURCE}${param}`;
        a.click();
    }

    uploadFile = (file: any, loadingType: number, callBack: Function) => {
        const { comp, form } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        this.setState((preState) => ({
            loading: {
                ...preState.loading,
                [loadingType]: true
            }
        }))
        Api.uploadResource({
            fileName: file,
            componentType: typeCode
        }).then((res: any) => {
            if (res.code === 1) {
                switch (loadingType) {
                    case FILE_TYPE.KERNEROS:
                        this.getPrincipalsList(file)
                        break
                    case FILE_TYPE.PARAMES:
                        form.setFieldsValue({
                            [typeCode]: {
                                componentConfig: {
                                    ...handleComponentConfig({ componentConfig: res.data[0] }, true)
                                }
                            }
                        })
                        break
                    case FILE_TYPE.CONFIGS:
                        form.setFieldsValue({
                            [typeCode]: {
                                specialConfig: res.data[0]
                            }
                        })
                        break
                }
                callBack && callBack()
                message.success('文件上传成功')
            }
            this.setState((preState) => ({
                loading: {
                    ...preState.loading,
                    [loadingType]: false
                }
            }))
        })
    }

    getPrincipalsList = async (file: any) => {
        const { form, comp } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        const res = await Api.parseKerberos({ fileName: file })
        if (res.code == 1) {
            this.setState({
                principals: res.data ?? []
            })
            form.setFieldsValue({
                [`${typeCode}`]: {
                    principal: res?.data[0] ?? '',
                    principals: res.data
                }
            })
        }
    }

    // Hadoop Kerberos认证文件
    renderKerberosFile = () => {
        const { comp, view } = this.props
        const { loading } = this.state
        const typeCode = comp?.componentTypeCode ?? ''
        return (
            <UploadFile
                label="Hadoop Kerberos认证文件"
                fileInfo={{
                    typeCode,
                    name: 'kerberosFileName',
                    value: comp.kerberosFileName,
                    desc: '仅支持.zip格式',
                    loading: loading[FILE_TYPE.KERNEROS],
                    uploadProps: {
                        name: 'kerberosFile',
                        accept: '.zip',
                        type: FILE_TYPE.KERNEROS
                    }
                }}
                view={view}
                form={this.props.form}
                uploadFile={this.uploadFile}
                icons={comp?.id && <Icon
                    type="download"
                    style={{ right: view ? 0 : 20 }}
                    onClick={() => this.downloadFile(FILE_TYPE.KERNEROS)}
                />}
            />
        )
    }

    // 参数批量上传文件
    renderParamsFile = () => {
        const { comp, view } = this.props
        const { loading } = this.state
        const typeCode = comp?.componentTypeCode ?? ''
        return (
            <UploadFile
                fileInfo={{
                    typeCode: typeCode,
                    name: 'paramsFile',
                    value: comp.paramsFile,
                    desc: '仅支持json格式',
                    loading: loading[FILE_TYPE.PARAMES],
                    uploadProps: {
                        name: 'paramsFile',
                        accept: '.json',
                        type: FILE_TYPE.PARAMES
                    }
                }}
                view={view}
                form={this.props.form}
                uploadFile={this.uploadFile}
                notDesc={true}
                label={
                    <span>
                        参数批量上传
                        <span className="c-fileConfig__downloadTemp"
                            onClick={() => this.downloadFile(FILE_TYPE.PARAMES)}
                        >
                            {comp?.id ? '下载参数' : '下载模板'}
                        </span>
                    </span>
                }
            />
        )
    }

    // 配置文件
    renderConfigsFile = () => {
        const { comp, view } = this.props
        const { loading } = this.state
        const typeCode = comp?.componentTypeCode ?? ''
        return (
            <UploadFile
                label="配置文件"
                deleteIcon={true}
                fileInfo={{
                    typeCode,
                    name: 'uploadFileName',
                    value: comp.uploadFileName,
                    desc: CONFIG_FILE_DESC[typeCode],
                    loading: loading[FILE_TYPE.CONFIGS],
                    uploadProps: {
                        name: 'uploadFileName',
                        accept: '.zip',
                        type: FILE_TYPE.CONFIGS
                    }
                }}
                view={view}
                form={this.props.form}
                uploadFile={this.uploadFile}
                rules={[
                    { required: true, message: `配置文件为空` }
                ]}
                icons={comp?.id && <Icon
                    type="download"
                    style={{ right: view ? 0 : 20 }}
                    onClick={() => this.downloadFile(FILE_TYPE.CONFIGS)}
                />}
            />
        )
    }

    renderStorageComponents = () => {
        const { comp, form, saveCompsData, view } = this.props
        const typeCode = comp?.componentTypeCode ?? ''

        if (saveCompsData.length === 0) return
        let storeTypeFlag = false
        for (const item in saveCompsData) {
            if (saveCompsData[item].key === COMPONENT_TYPE_VALUE.HDFS) {
                storeTypeFlag = true
                break;
            }
        }
        let storeType = comp?.storeType || (storeTypeFlag ? COMPONENT_TYPE_VALUE.HDFS : saveCompsData?.[0]?.key)

        return (
            <FormItem
                label="存储组件"
                colon={false}
                key={`${typeCode}.storeType`}
            >
                {form.getFieldDecorator(`${typeCode}.storeType`, {
                    initialValue: storeType
                })(
                    <Select style={{ width: 172 }} disabled={view}>
                        {saveCompsData.map((ver: any) => {
                            return <Option value={ver.key} key={ver.key}>{ver.value}</Option>
                        })}
                    </Select>
                )}
            </FormItem>
        )
    }

    renderPrincipal = () => {
        const { comp, form, view } = this.props
        const { principals } = this.state
        const principalsList = !principals.length ? principals : comp.principals
        const typeCode = comp?.componentTypeCode ?? ''

        if (principalsList.length == 0) return

        return (
            <FormItem
                label="principal"
                colon={false}
                key={`${typeCode}.principal`}
            >
                {form.getFieldDecorator(`${typeCode}.principal`, {
                    initialValue: comp?.principal || principals[0] || ''
                })(
                    <Select style={{ width: 172 }} disabled={view}>
                        {
                            principalsList.map((ver: any, key) => {
                                return <Option value={ver} key={key}>{ver}</Option>
                            })
                        }
                    </Select>
                )}
                {form.getFieldDecorator(`${typeCode}.principals`, {
                    initialValue: principals || comp.principals || []
                })(
                    <></>
                )}
            </FormItem>
        )
    }

    renderFileConfig = () => {
        const typeCode = this.props?.comp?.componentTypeCode ?? ''
        switch (typeCode) {
            case COMPONENT_TYPE_VALUE.ORACLE_SQL:
            case COMPONENT_TYPE_VALUE.LIBRA_SQL:
            case COMPONENT_TYPE_VALUE.TIDB_SQL:
            case COMPONENT_TYPE_VALUE.GREEN_PLUM_SQL:
            case COMPONENT_TYPE_VALUE.PRESTO_SQL: {
                return (
                    <>
                        {this.renderParamsFile()}
                        {this.renderStorageComponents()}
                    </>
                )
            }
            case COMPONENT_TYPE_VALUE.SFTP:
            case COMPONENT_TYPE_VALUE.NFS: {
                return this.renderParamsFile()
            }
            case COMPONENT_TYPE_VALUE.KUBERNETES: {
                return (
                    <>
                        {this.renderConfigsFile()}
                        {this.renderKerberosFile()}
                        {this.renderPrincipal()}
                    </>
                )
            }
            case COMPONENT_TYPE_VALUE.IMPALA_SQL:
            case COMPONENT_TYPE_VALUE.HIVE_SERVER:
                return (
                    <>
                        {this.renderCompsVersion()}
                        {this.renderKerberosFile()}
                        {this.renderPrincipal()}
                        {this.renderParamsFile()}
                        {this.renderStorageComponents()}
                    </>
                )
            case COMPONENT_TYPE_VALUE.LEARNING:
            case COMPONENT_TYPE_VALUE.DTYARNSHELL: {
                return (
                    <>
                        {this.renderKerberosFile()}
                        {this.renderPrincipal()}
                        {this.renderParamsFile()}
                        {this.renderStorageComponents()}
                    </>
                )
            }
            case COMPONENT_TYPE_VALUE.YARN:
            case COMPONENT_TYPE_VALUE.HDFS: {
                return (
                    <>
                        {this.renderCompsVersion()}
                        {this.renderConfigsFile()}
                        {this.renderKerberosFile()}
                        {this.renderPrincipal()}
                    </>
                )
            }
            case COMPONENT_TYPE_VALUE.SPARK_THRIFT_SERVER:
            case COMPONENT_TYPE_VALUE.SPARK:
            case COMPONENT_TYPE_VALUE.FLINK: {
                return (
                    <>
                        {this.renderCompsVersion()}
                        {this.renderKerberosFile()}
                        {this.renderPrincipal()}
                        {this.renderParamsFile()}
                        {this.renderStorageComponents()}
                    </>
                )
            }
            default:
                return null;
        }
    }

    render () {
        return (
            <div className="c-fileConfig__container">
                {this.renderFileConfig()}
            </div>
        )
    }
}
