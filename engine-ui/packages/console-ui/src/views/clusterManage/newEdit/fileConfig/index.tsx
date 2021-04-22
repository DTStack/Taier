import * as React from 'react'
import { Form, Select, message, Icon, Cascader,
    notification } from 'antd'

import req from '../../../../consts/reqUrls'
import Api from '../../../../api/console'
import UploadFile from './components/uploadFileBtn'
import KerberosModal from './components/kerberosModal'
import { COMPONENT_TYPE_VALUE, VERSION_TYPE, FILE_TYPE,
    CONFIG_FILE_DESC, DEFAULT_COMP_VERSION } from '../const'
import { isOtherVersion, isSameVersion, handleComponentConfig,
    needZipFile, getOptions, getInitialValue, isMultiVersion,
    isYarn } from '../help'

interface IProps {
    comp: any;
    form: any;
    view: boolean;
    saveCompsData: any[];
    versionData: any;
    clusterInfo: any;
    commVersion?: string;
    handleCompVersion?: Function;
    saveComp: Function;
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
        if (isSameVersion(Number(typeCode))) return
        this.props.form.setFieldsValue({ [`${typeCode}.hadoopVersion`]: version })
    }

    renderCompsVersion = () => {
        const { getFieldDecorator } = this.props.form
        const { versionData, comp, view, commVersion } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        let version = isOtherVersion(typeCode) ? versionData[VERSION_TYPE[typeCode]] : versionData.hadoopVersion
        let initialValue = isOtherVersion(typeCode) ? DEFAULT_COMP_VERSION[typeCode] : [version[0].key, version[0].values[0]?.key]
        initialValue = comp?.hadoopVersion || initialValue
        if (isSameVersion(typeCode)) {
            initialValue = commVersion ? getInitialValue(version, commVersion)
                : (comp?.hadoopVersion ? getInitialValue(version, comp?.hadoopVersion) : initialValue)
        }

        return (
            <>
                <FormItem
                    label="组件版本"
                    colon={false}
                    key={`${typeCode}.hadoopVersion`}
                >
                    {getFieldDecorator(`${typeCode}.hadoopVersionSelect`, {
                        initialValue: initialValue
                    })(
                        isOtherVersion(typeCode) ? (<Select style={{ width: 172 }} disabled={view} onChange={this.handleVersion}>
                            {version.map((ver: any) => {
                                return <Option value={ver.value} key={ver.key}>{ver.key}</Option>
                            })}
                        </Select>) : <Cascader
                            options={getOptions(version)}
                            disabled={view}
                            expandTrigger="click"
                            displayRender={(label) => {
                                return label[label.length - 1];
                            }}
                            onChange={this.handleVersion}
                            style={{ width: '100%' }}
                        />
                    )}
                </FormItem>
                {getFieldDecorator(`${typeCode}.hadoopVersion`, {
                    initialValue: isSameVersion(typeCode) ? (commVersion || comp?.hadoopVersion || version[0].values[0]?.key) : initialValue
                })(<></>)}
            </>
        )
    }

    getPrincipalsList = async (file: any) => {
        const { form, comp } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        const hadoopVersion = comp?.hadoopVersion ?? ''
        const res = await Api.parseKerberos({ fileName: file })
        if (res.code == 1) {
            const principal = {
                principal: res?.data[0] ?? '',
                principals: res.data
            }
            const fieldValue = isMultiVersion(typeCode) ? { [hadoopVersion]: principal } : { ...principal }
            form.setFieldsValue({ [typeCode]: fieldValue })
            this.setState({
                principals: res.data ?? []
            })
        }
    }

    refreshYarnQueue = () => {
        const { clusterName } = this.props.clusterInfo
        Api.refreshQueue({ clusterName }).then((res: any) => {
            if (res.code == 1) {
                const target = res.data.find(v => v.componentTypeCode == COMPONENT_TYPE_VALUE.YARN)
                if (target?.result || res.data.length == 0) {
                    message.success('刷新成功')
                } else {
                    notification['error']({
                        message: '刷新失败',
                        description: `${target.errorMsg}`,
                        style: { wordBreak: 'break-word' }
                    });
                }
            }
        })
    }

    // 下载配置文件
    downloadFile = (type: number) => {
        const { form, clusterInfo, comp } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        let version = form.getFieldValue(typeCode + '.hadoopVersion') || '';
        if (isMultiVersion(typeCode)) version = comp?.hadoopVersion ?? ''

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
        const { comp, form, clusterInfo } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        const hadoopVersion = isMultiVersion(typeCode) ? comp?.hadoopVersion : ''
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
        if (loadingType == FILE_TYPE.KERNEROS) {
            const params = {
                kerberosFile: file,
                clusterId: clusterInfo?.clusterId ?? '',
                componentCode: typeCode,
                componentVersion: hadoopVersion
            }
            res = await Api.uploadKerberos(params)
            this.getPrincipalsList(file)
        } else {
            res = await Api.uploadResource({
                fileName: file,
                componentType: typeCode
            })
        }
        function setValue () {
            const componentConfig = handleComponentConfig({
                componentConfig: res.data[0]
            }, true)
            const fieldValue = isMultiVersion(typeCode)
                ? { [hadoopVersion]: { componentConfig } } : { componentConfig }
            form.setFieldsValue({ [typeCode]: fieldValue })
        }
        if (res.code == 1) {
            switch (loadingType) {
                case FILE_TYPE.KERNEROS: {
                    this.setKrbConfig(res.data)
                    break
                }
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
                    hadoopVersion: comp?.hadoopVersion ?? '',
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
                    {!view && <Icon
                        type="edit"
                        style={{ right: !comp?.id ? 20 : 40 }}
                        onClick={() => this.setState({ visible: true })}
                    />}
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
                    hadoopVersion: comp?.hadoopVersion ?? '',
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
                label={<span>
                    配置文件
                    {isYarn(typeCode) && <a style={{ marginLeft: 66 }} onClick={this.refreshYarnQueue}>刷新队列</a>}
                </span>}
                deleteIcon={true}
                fileInfo={{
                    typeCode,
                    name: 'uploadFileName',
                    value: comp.uploadFileName,
                    desc: CONFIG_FILE_DESC[typeCode],
                    loading: loading[FILE_TYPE.CONFIGS],
                    hadoopVersion: comp?.hadoopVersion ?? '',
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

    renderStorageComponents = () => {
        const { comp, form, saveCompsData, view } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        const hadoopVersion = comp?.hadoopVersion ?? ''
        let formField = typeCode
        if (isMultiVersion(typeCode)) formField = formField + '.' + hadoopVersion
        formField = formField + '.storeType'

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
                key={formField}
            >
                {form.getFieldDecorator(formField, {
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
        let principalsList = principals
        const typeCode = comp?.componentTypeCode ?? ''
        const hadoopVersion = comp?.hadoopVersion ?? ''

        let formField = typeCode
        if (isMultiVersion(typeCode)) formField = formField + '.' + hadoopVersion

        const kerberosFile = form.getFieldValue(formField + '.kerberosFileName') ?? comp?.kerberosFileName

        if (!principals.length && !Array.isArray(comp?.principals) && comp?.principals) {
            principalsList = comp?.principals.split(',')
        }

        if (principalsList?.length == 0 || !kerberosFile) return

        return (
            <FormItem
                label="principal"
                colon={false}
                key={formField + '.principal'}
            >
                {form.getFieldDecorator(formField + '.principal', {
                    initialValue: comp?.principal ?? principals[0] ?? ''
                })(
                    <Select style={{ width: 172 }} disabled={view}>
                        {
                            principalsList.map((ver: any, key) => {
                                return <Option value={ver} key={key}>{ver}</Option>
                            })
                        }
                    </Select>
                )}
                {form.getFieldDecorator(formField + '.principals', {
                    initialValue: comp?.principals ?? ''
                })(
                    <></>
                )}
            </FormItem>
        )
    }

    setKrbConfig = (krbconfig: any) => {
        const { comp, saveComp } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        const hadoopVersion = comp?.hadoopVersion ?? ''
        saveComp({
            mergeKrb5Content: krbconfig,
            componentTypeCode: typeCode,
            hadoopVersion: hadoopVersion
        })
    }

    hanleVisible = (krbconfig: any) => {
        this.setState({ visible: false })
        this.setKrbConfig(krbconfig)
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
                        {this.renderPrincipal()}
                    </>
                )
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
            case COMPONENT_TYPE_VALUE.SFTP:
            case COMPONENT_TYPE_VALUE.NFS: {
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
                        {this.renderStorageComponents()}
                    </>
                )
            }
            case COMPONENT_TYPE_VALUE.IMPALA_SQL:
            case COMPONENT_TYPE_VALUE.HIVE_SERVER:
            case COMPONENT_TYPE_VALUE.SPARK_THRIFT_SERVER: {
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
            case COMPONENT_TYPE_VALUE.SPARK:
            case COMPONENT_TYPE_VALUE.FLINK: {
                return (
                    <>
                        {this.renderKerberosFile()}
                        {this.renderPrincipal()}
                        {this.renderParamsFile()}
                        {this.renderStorageComponents()}
                    </>
                )
            }
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
            default:
                return null;
        }
    }

    render () {
        const { comp } = this.props
        const { visible } = this.state
        return (
            <div className="c-fileConfig__container">
                {this.renderFileConfig()}
                <KerberosModal
                    key={`${visible}`}
                    visible={visible}
                    krbconfig={comp?.mergeKrb5Content || ''}
                    onCancel={this.hanleVisible}
                />
            </div>
        )
    }
}
