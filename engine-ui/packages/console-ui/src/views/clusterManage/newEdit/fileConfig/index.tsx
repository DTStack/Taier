import * as React from 'react'
import { Form, Select, message, Icon } from 'antd'

import req from '../../../../consts/reqUrls'
import Api from '../../../../api/console'
import UploadFile from './components/uploadFileBtn'
import { COMPONENT_TYPE_VALUE, VERSION_TYPE, FILE_TYPE,
    CONFIG_FILE_DESC, DEFAULT_COMP_VERSION } from '../const'
import { isOtherVersion, isSameVersion, handleComponentConfig, needZipFile } from '../help'

interface IProps {
    comp: any;
    form: any;
    view: boolean;
    commVersion: string;
    saveCompsData: any[];
    versionData: any;
    clusterInfo: any;
    handleCompVersion: Function;
}

interface IState {
    loading: any;
    visible: boolean;
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
        visible: false,
        principals: []
    }

    /** hdfs 和 yarn 组件版本一致，version提取至上层 */
    handleVersion = (version: any) => {
        const { comp, handleCompVersion } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        handleCompVersion(typeCode, version)
    }

    renderCompsVersion = () => {
        const { getFieldDecorator } = this.props.form
        const { versionData, comp, view, commVersion } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        let version = isOtherVersion(typeCode) ? versionData[VERSION_TYPE[typeCode]] : versionData.hadoopVersion
        let initialValue = isOtherVersion(typeCode) ? DEFAULT_COMP_VERSION[typeCode] : versionData.hadoopVersion[0].value
        initialValue = comp?.hadoopVersion || initialValue
        if (isSameVersion(typeCode)) initialValue = commVersion || initialValue

        return (
            <FormItem
                label="组件版本"
                colon={false}
                key={`${typeCode}.hadoopVersion`}
            >
                {getFieldDecorator(`${typeCode}.hadoopVersion`, {
                    initialValue: initialValue
                })(
                    <Select style={{ width: 172 }} disabled={view} onChange={this.handleVersion}>
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
        const { form, clusterInfo, comp } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        const version = form.getFieldValue(typeCode + '.hadoopVersion') || '';

        const a = document.createElement('a')
        let param = comp?.id ? (`?componentId=${comp.id}&`) : '?'
        param = param + `type=${type}&componentType=${typeCode}&hadoopVersion=${version}&clusterName=${clusterInfo?.clusterName}`;
        a.href = `${req.DOWNLOAD_RESOURCE}${param}`;
        a.click();
    }

    validateFileType = (val: string) => {
        const result = /\.(zip)$/.test(val.toLocaleLowerCase())
        if (val && !result) {
            message.warning('配置文件只能是zip文件!');
        }
        return result
    }

    uploadFile = async (file: any, loadingType: number, callBack: Function) => {
        const { comp, form } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        this.setState((preState) => ({
            loading: {
                ...preState.loading,
                [loadingType]: true
            }
        }))
        let res: any
        if (needZipFile(loadingType) && !this.validateFileType(file?.name)) {
            this.setState((preState) => ({
                loading: {
                    ...preState.loading,
                    [loadingType]: false
                }
            }))
            return;
        }
        res = await Api.uploadResource({
            fileName: file,
            componentType: typeCode
        })
        function setValue () {
            form.setFieldsValue({
                [typeCode]: {
                    componentConfig: {
                        ...handleComponentConfig({
                            componentConfig: res.data[0]
                        }, true)
                    }
                }
            })
        }
        if (res.code == 1) {
            switch (loadingType) {
                case FILE_TYPE.PARAMES:
                    setValue()
                    setValue()
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
    }

    deleteKerFile = () => {
        const { comp } = this.props
        if (!comp.id) return
        Api.closeKerberos({
            componentId: comp.id
        })
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
                icons={<>
                    {comp?.id && <Icon
                        type="download"
                        style={{ right: view ? 0 : 20 }}
                        onClick={() => this.downloadFile(FILE_TYPE.KERNEROS)}
                    />}
                </>}
                deleteFile={this.deleteKerFile}
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
                    style={{ right: 0 }}
                    onClick={() => this.downloadFile(FILE_TYPE.CONFIGS)}
                />}
            />
        )
    }

    renderFileConfig = () => {
        const typeCode = this.props?.comp?.componentTypeCode ?? ''
        switch (typeCode) {
            case COMPONENT_TYPE_VALUE.YARN:
            case COMPONENT_TYPE_VALUE.HDFS: {
                return (
                    <>
                        {this.renderCompsVersion()}
                        {this.renderConfigsFile()}
                        {this.renderKerberosFile()}
                    </>
                )
            }
            case COMPONENT_TYPE_VALUE.KUBERNETES: {
                return (
                    <>
                        {this.renderConfigsFile()}
                        {this.renderKerberosFile()}
                    </>
                )
            }
            case COMPONENT_TYPE_VALUE.SFTP: {
                return this.renderParamsFile()
            }
            case COMPONENT_TYPE_VALUE.ORACLE_SQL:
            case COMPONENT_TYPE_VALUE.LIBRA_SQL:
            case COMPONENT_TYPE_VALUE.TIDB_SQL:
            case COMPONENT_TYPE_VALUE.GREEN_PLUM_SQL:
            case COMPONENT_TYPE_VALUE.PRESTO_SQL: {
                return (
                    <>
                        {this.renderParamsFile()}
                    </>
                )
            }
            case COMPONENT_TYPE_VALUE.IMPALA_SQL:
            case COMPONENT_TYPE_VALUE.HIVE_SERVER:
            case COMPONENT_TYPE_VALUE.SPARK_THRIFT_SERVER:
            case COMPONENT_TYPE_VALUE.SPARK:
            case COMPONENT_TYPE_VALUE.FLINK:
                return (
                    <>
                        {this.renderCompsVersion()}
                        {this.renderKerberosFile()}
                        {this.renderParamsFile()}
                    </>
                )
            case COMPONENT_TYPE_VALUE.LEARNING:
            case COMPONENT_TYPE_VALUE.DTYARNSHELL: {
                return (
                    <>
                        {this.renderKerberosFile()}
                        {this.renderParamsFile()}
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
