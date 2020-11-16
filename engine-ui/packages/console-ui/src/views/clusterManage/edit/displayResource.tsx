import * as React from 'react';
import { Form, Select, Icon } from 'antd';
import utils from 'dt-common/src/utils';
import {
    COMPONENT_TYPE_VALUE,
    COMPONEMT_CONFIG_KEY_ENUM
} from '../../../consts';
import Api from '../../../api/console'
import dealData from './dealData';

const FormItem = Form.Item;
const Option = Select.Option;
class DisplayResource extends React.Component<any, any> {
    state: any = {
        compVersion: [],
        saveCompsData: []
    }
    componentDidMount () {
        const { clusterName } = this.props;

        (async () => {
            const res = await Api.getComponentStore({ clusterName })
            if (!res) return
            const { data = [] } = res
            let saveCompsData = []
            data.forEach(item => {
                saveCompsData.push({
                    key: item?.componentTypeCode,
                    value: item?.componentName
                })
            })
            this.setState({
                saveCompsData
            })
        })()
    }
    // 组件配置信息
    getComponentConfig = () => {
        const { components, componentConfig } = this.props;
        const componentTypeCode = components.componentTypeCode;
        return componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]] || {};
    }

    getNoticeContent = (componentTypeCode: number) => {
        switch (componentTypeCode) {
            case COMPONENT_TYPE_VALUE.YARN:
                return 'zip格式，至少包括yarn-site.xml';
            case COMPONENT_TYPE_VALUE.HDFS:
                return 'zip格式，至少包括core-site.xml、hdfs-site.xml、hive-site.xml';
            case COMPONENT_TYPE_VALUE.KUBERNETES:
                return 'zip格式，至少包括kubernetes.config';
            default:
                return null;
        }
    }

    // 配置文件
    renderConfigsFile = (configName: any) => {
        const { getFieldDecorator, components, isView, uploadLoading,
            fileChange, downloadFile } = this.props;
        const config = this.getComponentConfig();
        const { fileName } = config;
        const noticeContent = this.getNoticeContent(components.componentTypeCode);
        return (
            <FormItem
                label="配置文件"
                colon={false}
            >
                {getFieldDecorator(`${configName}.uploadFileName`, {
                    initialValue: components.uploadFileName || ''
                })(
                    <div>
                        {!isView && <label
                            style={{ lineHeight: '32px', textIndent: 'initial', height: 32, width: 172 }}
                            className="ant-btn"
                            htmlFor={`my${configName}File`}
                        >
                            <span>
                                {uploadLoading ? <Icon className="blue-loading" type="loading" style={{ marginRight: 8 }} /> : <Icon type="upload" style={{ marginRight: 8 }} />}
                                上传文件
                            </span>
                        </label>}
                        <input
                            type="file"
                            id={`my${configName}File`}
                            onClick={(e: any) => { e.target.value = null }}
                            onChange={(e: any) => { fileChange(e, components.componentTypeCode) }}
                            accept=".zip"
                            style={{ display: 'none' }}
                        />
                        {!isView && <div className="c-displayResource__notice">{noticeContent}</div>}
                        {fileName && <div className="c-displayResource__downloadFile" style={{ fontSize: 12, color: '#3F87FF' }}>
                            <span>
                                <Icon type="paper-clip" style={{ marginRight: 2, color: '#666666FF' }} />
                                {utils.textOverflowExchange(fileName, 9)}
                            </span>
                            {components.id && <Icon type="download" style={{ color: '#666666FF' }} onClick={() => downloadFile(components, 1)} />}
                        </div>}
                    </div>
                )}
            </FormItem>
        )
    }

    // Hadoop Kerberos认证文件
    renderKerberosFile = (configName: any) => {
        const { getFieldDecorator, components, isView, kerUploadLoading, kerFileChange,
            downloadFile, deleteKerFile } = this.props;
        const config = this.getComponentConfig();
        const { kerFileName } = config;
        return (
            <FormItem
                label="Hadoop Kerberos认证文件"
                colon={false}
            >
                {getFieldDecorator(`${configName}.kerberosFileName`, {
                    initialValue: components.kerberosFileName || ''
                })(
                    <div>
                        {!isView && <label
                            style={{ lineHeight: '32px', textIndent: 'initial', height: 32, width: 172 }}
                            className="ant-btn"
                            htmlFor={`my${configName}KerberosFile`}
                        >
                            <span>
                                {kerUploadLoading ? <Icon className="blue-loading" type="loading" style={{ marginRight: 8 }} /> : <Icon type="upload" style={{ marginRight: 8 }} />}
                                上传文件
                            </span>
                        </label>}
                        <input
                            type="file"
                            id={`my${configName}KerberosFile`}
                            onClick={(e: any) => { e.target.value = null }}
                            onChange={(e: any) => kerFileChange(e, components.componentTypeCode)}
                            accept=".zip"
                            style={{ display: 'none' }}
                        />
                        {!isView && <div className="c-displayResource__notice">仅支持.zip格式</div>}
                        {kerFileName && <div className="c-displayResource__downloadFile" style={{ fontSize: 12, color: '#3F87FF' }}>
                            <span>
                                <Icon type="paper-clip" style={{ marginRight: 2, color: '#666666FF' }} />
                                {utils.textOverflowExchange(kerFileName, 9)}
                            </span>
                            <span>
                                {components.id && <Icon type="download" style={{ color: '#666666FF' }} onClick={() => components.id && downloadFile(components, 0)} />}
                                {!isView && <Icon type="delete" style={{ color: '#666666FF', marginLeft: 6 }} onClick={() => deleteKerFile(components.componentTypeCode)} />}
                            </span>
                        </div>}
                    </div>
                )}
            </FormItem>
        )
    }

    // 参数批量上传文件
    renderParamsFile = (configName: any) => {
        const { getFieldDecorator, components, isView, uploadLoading, downloadFile, paramsfileChange } = this.props;
        const config = this.getComponentConfig();
        const { paramsFileName, loadTemplate = [] } = config;
        const isHaveValue = dealData.checkFormHaveValue(loadTemplate)
        return (
            <FormItem
                label={
                    <span>
                        <span>参数批量上传</span>
                        {
                            paramsFileName || isHaveValue
                                ? <span
                                    className="c-displayResource__downloadTemp"
                                    onClick={() => downloadFile(components, 2)}
                                >
                                    下载参数
                                </span>
                                : <span
                                    className="c-displayResource__downloadTemp"
                                    onClick={() => downloadFile(components, 2)}
                                >
                                    下载模板
                                </span>
                        }
                    </span>
                }
                colon={false}
            >
                {getFieldDecorator(`${configName}.paramsFile`)(
                    <div>
                        {!isView && <label
                            style={{ lineHeight: '32px', textIndent: 'initial', height: 32, width: 172 }}
                            className="ant-btn"
                            htmlFor={`my${configName}PramasFile`}
                        >
                            <span>
                                {uploadLoading ? <Icon className="blue-loading" type="loading" style={{ marginRight: 8 }} /> : <Icon type="upload" style={{ marginRight: 8 }} />}
                                上传文件
                            </span>
                        </label>}
                        <input
                            name="file"
                            type="file"
                            id={`my${configName}PramasFile`}
                            onClick={(e: any) => { e.target.value = null }}
                            onChange={(e: any) => paramsfileChange(e, components.componentTypeCode)}
                            accept=".json"
                            style={{ display: 'none' }}
                        />
                        {!isView && <div className="c-displayResource__notice">仅支持json格式</div>}
                    </div>
                )}
            </FormItem>
        )
    }

    handleCompsVersion = (val: any, componentTypeCode: number) => {
        this.props.handleCommonVersion(val, componentTypeCode)
        this.props.handleCompsVersion(val, componentTypeCode);
    }

    handleSaveCompsData = (val: any, componentTypeCode: number) => {
        this.props.handleSaveCompsData(val, componentTypeCode)
        this.props.handleCompsCompsData(val, componentTypeCode);
    }

    // 组件版本
    renderCompsVersion = (configName: string) => {
        const { versionData, getFieldDecorator, isView, components } = this.props;
        const { commonVersion } = this.props;
        const componentTypeCode = components.componentTypeCode;
        let defaultVersion = 'hadoop2';
        let version = commonVersion || components.hadoopVersion;

        let versionCompsData = versionData.hadoopVersion || [];
        switch (componentTypeCode) {
            case COMPONENT_TYPE_VALUE.FLINK:
                defaultVersion = '180';
                version = components.hadoopVersion || '';
                versionCompsData = versionData.Flink || []
                break;
            case COMPONENT_TYPE_VALUE.SPARK:
                defaultVersion = '210';
                version = components.hadoopVersion || '';
                versionCompsData = versionData.Spark || []
                break;
            case COMPONENT_TYPE_VALUE.SPARK_THRIFT_SERVER:
                defaultVersion = '2.x';
                version = components.hadoopVersion || '';
                versionCompsData = versionData.SparkThrift || []
                break;
            case COMPONENT_TYPE_VALUE.HIVE_SERVER:
                defaultVersion = '2.x';
                version = components.hadoopVersion || '';
                versionCompsData = versionData.HiveServer || []
                break;
            default:
                break;
        }
        return (
            <FormItem
                label="组件版本"
                colon={false}
                key={`${configName}.hadoopVersion`}
            >
                {getFieldDecorator(`${configName}.hadoopVersion`, {
                    initialValue: version || defaultVersion
                })(
                    <Select style={{ width: 172 }} disabled={isView} onChange={(val) => this.handleCompsVersion(val, componentTypeCode)}>
                        {versionCompsData.map((ver: any) => {
                            return <Option value={ver.value} key={ver.key}>{ver.key}</Option>
                        })}
                    </Select>
                )}
            </FormItem>
        )
    }

    // 存储组件
    renderStorageComponents = (configName: string) => {
        const { getFieldDecorator, isView, components } = this.props;
        const componentTypeCode = components.componentTypeCode;
        const { saveCompsData } = this.state
        if (saveCompsData.length === 0) return
        let storeTypeFlag = false
        for (const item in saveCompsData) {
            if (saveCompsData[item].key === COMPONENT_TYPE_VALUE.HDFS) {
                storeTypeFlag = true
                break;
            }
        }
        let storeType = components?.storeType || (storeTypeFlag ? COMPONENT_TYPE_VALUE.HDFS : saveCompsData?.[0]?.key)

        return (
            <FormItem
                label="存储组件"
                colon={false}
                key={`${configName}.storeType`}
            >
                {getFieldDecorator(`${configName}.storeType`, {
                    initialValue: storeType
                })(
                    <Select style={{ width: 172 }} disabled={isView} onChange={(val) => this.handleSaveCompsData(val, componentTypeCode)}>
                        {saveCompsData.map((ver: any) => {
                            return <Option value={ver.key} key={ver.key}>{ver.value}</Option>
                        })}
                    </Select>
                )}
            </FormItem>
        )
    }

    renderDisplayResource = () => {
        const { componentTypeCode = '' } = this.props?.components;
        switch (componentTypeCode) {
            case COMPONENT_TYPE_VALUE.SFTP:
            case COMPONENT_TYPE_VALUE.ORACLE_SQL:
            case COMPONENT_TYPE_VALUE.LIBRA_SQL:
            case COMPONENT_TYPE_VALUE.TIDB_SQL:
            case COMPONENT_TYPE_VALUE.GREEN_PLUM_SQL:
            case COMPONENT_TYPE_VALUE.NFS:
            case COMPONENT_TYPE_VALUE.PRESTO_SQL: {
                return (componentTypeCode !== COMPONENT_TYPE_VALUE.SFTP && componentTypeCode !== COMPONENT_TYPE_VALUE.NFS)
                    ? (
                        <>
                            {this.renderParamsFile(COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode])}
                            {this.renderStorageComponents(COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode])}
                        </>
                    ) : this.renderParamsFile(COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode])
            }
            case COMPONENT_TYPE_VALUE.KUBERNETES: {
                return (
                    <React.Fragment>
                        {this.renderConfigsFile(COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode])}
                        {this.renderKerberosFile(COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode])}
                    </React.Fragment>
                )
            }
            case COMPONENT_TYPE_VALUE.IMPALA_SQL:
            case COMPONENT_TYPE_VALUE.HIVE_SERVER:
                return (
                    <React.Fragment>
                        {this.renderCompsVersion(COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode])}
                        {this.renderKerberosFile(COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode])}
                        {this.renderParamsFile(COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode])}
                        {this.renderStorageComponents(COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode])}
                    </React.Fragment>
                )
            case COMPONENT_TYPE_VALUE.LEARNING:
            case COMPONENT_TYPE_VALUE.DTYARNSHELL: {
                return (
                    <React.Fragment>
                        {this.renderKerberosFile(COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode])}
                        {this.renderParamsFile(COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode])}
                        {this.renderStorageComponents(COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode])}
                    </React.Fragment>
                )
            }
            case COMPONENT_TYPE_VALUE.YARN:
            case COMPONENT_TYPE_VALUE.HDFS: {
                return (
                    <React.Fragment>
                        {this.renderCompsVersion(COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode])}
                        {this.renderConfigsFile(COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode])}
                        {this.renderKerberosFile(COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode])}
                    </React.Fragment>
                )
            }
            case COMPONENT_TYPE_VALUE.SPARK_THRIFT_SERVER:
            case COMPONENT_TYPE_VALUE.SPARK:
            case COMPONENT_TYPE_VALUE.FLINK: {
                return (
                    <React.Fragment>
                        {this.renderCompsVersion(COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode])}
                        {this.renderKerberosFile(COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode])}
                        {this.renderParamsFile(COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode])}
                        {this.renderStorageComponents(COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode])}
                    </React.Fragment>
                )
            }
            default:
                return null;
        }
    }
    render () {
        return (
            <div className="c-displayResource__container">
                {this.renderDisplayResource()}
            </div>
        )
    }
}

export default DisplayResource;
