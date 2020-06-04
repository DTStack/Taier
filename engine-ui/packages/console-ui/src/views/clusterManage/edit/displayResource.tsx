import * as React from 'react';
import { Form, Select, Icon } from 'antd';
import utils from 'dt-common/src/utils';
import {
    COMPONENT_TYPE_VALUE, COMPONEMT_CONFIG_KEYS, COMPONEMT_CONFIG_KEY_ENUM
} from '../../../consts';
import dealData from './dealData';

const FormItem = Form.Item;
const Option = Select.Option;
class DisplayResource extends React.Component<any, any> {
    state: any = {
        compVersion: []
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
                                { uploadLoading ? <Icon className="blue-loading" type="loading" style={{ marginRight: 8 }} /> : <Icon type="upload" style={{ marginRight: 8 }} /> }
                                上传文件
                            </span>
                        </label>}
                        <input
                            type="file"
                            id={`my${configName}File`}
                            onClick={(e: any) => { e.target.value = null }}
                            onChange={(e: any) => fileChange(e, components.componentTypeCode)}
                            accept=".zip"
                            style={{ display: 'none' }}
                        />
                        { !isView && <div className="c-displayResource__notice">{noticeContent}</div> }
                        { fileName && <div className="c-displayResource__downloadFile" style={{ fontSize: 12, color: '#3F87FF' }}>
                            <span>
                                <Icon type="paper-clip" style={{ marginRight: 2, color: '#666666FF' }} />
                                {utils.textOverflowExchange(fileName, 9)}
                            </span>
                            { components.id && <Icon type="download" style={{ color: '#666666FF' }} onClick={() => downloadFile(components, 1)} /> }
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
                                { kerUploadLoading ? <Icon className="blue-loading" type="loading" style={{ marginRight: 8 }} /> : <Icon type="upload" style={{ marginRight: 8 }} /> }
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
                        { !isView && <div className="c-displayResource__notice">仅支持.zip格式</div> }
                        { kerFileName && <div className="c-displayResource__downloadFile" style={{ fontSize: 12, color: '#3F87FF' }}>
                            <span>
                                <Icon type="paper-clip" style={{ marginRight: 2, color: '#666666FF' }} />
                                {utils.textOverflowExchange(kerFileName, 9)}
                            </span>
                            <span>
                                { components.id && <Icon type="download" style={{ color: '#666666FF' }} onClick={() => components.id && downloadFile(components, 0)} /> }
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
                {getFieldDecorator(`${configName}.paramsFile`, null)(
                    <div>
                        {!isView && <label
                            style={{ lineHeight: '32px', textIndent: 'initial', height: 32, width: 172 }}
                            className="ant-btn"
                            htmlFor={`my${configName}PramasFile`}
                        >
                            <span>
                                { uploadLoading ? <Icon className="blue-loading" type="loading" style={{ marginRight: 8 }} /> : <Icon type="upload" style={{ marginRight: 8 }} /> }
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
                        {!isView && <div className="c-displayResource__notice">仅支持json格式</div> }
                    </div>
                )}
            </FormItem>
        )
    }

    handleCompsVersion = (val: any, componentTypeCode: number) => {
        this.props.handleCommonVersion(val, componentTypeCode)
        this.props.handleCompsVersion(val, componentTypeCode);
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
            default:
                break;
        }
        // console.log('version===========ssss', version)
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

    renderDisplayResource = () => {
        const { components } = this.props;
        const componentTypeCode = components.componentTypeCode;
        switch (componentTypeCode) {
            case COMPONENT_TYPE_VALUE.SFTP:
                return this.renderParamsFile(COMPONEMT_CONFIG_KEYS.SFTP)
            case COMPONENT_TYPE_VALUE.YARN:
                return (
                    <React.Fragment>
                        {this.renderCompsVersion(COMPONEMT_CONFIG_KEYS.YARN)}
                        {this.renderConfigsFile(COMPONEMT_CONFIG_KEYS.YARN)}
                        {this.renderKerberosFile(COMPONEMT_CONFIG_KEYS.YARN)}
                    </React.Fragment>
                )
            case COMPONENT_TYPE_VALUE.KUBERNETES:
                return (
                    <React.Fragment>
                        {this.renderConfigsFile(COMPONEMT_CONFIG_KEYS.KUBERNETES)}
                        {this.renderKerberosFile(COMPONEMT_CONFIG_KEYS.KUBERNETES)}
                    </React.Fragment>
                )
            case COMPONENT_TYPE_VALUE.HDFS:
                return (
                    <React.Fragment>
                        {this.renderCompsVersion(COMPONEMT_CONFIG_KEYS.HDFS)}
                        {this.renderConfigsFile(COMPONEMT_CONFIG_KEYS.HDFS)}
                        {this.renderKerberosFile(COMPONEMT_CONFIG_KEYS.HDFS)}
                    </React.Fragment>
                )
            case COMPONENT_TYPE_VALUE.TIDB_SQL:
                return this.renderParamsFile(COMPONEMT_CONFIG_KEYS.TIDB_SQL)
            case COMPONENT_TYPE_VALUE.LIBRA_SQL:
                return this.renderParamsFile(COMPONEMT_CONFIG_KEYS.LIBRA_SQL)
            case COMPONENT_TYPE_VALUE.ORACLE_SQL:
                return this.renderParamsFile(COMPONEMT_CONFIG_KEYS.ORACLE_SQL)
            case COMPONENT_TYPE_VALUE.IMPALA_SQL:
                return (
                    <React.Fragment>
                        {this.renderKerberosFile(COMPONEMT_CONFIG_KEYS.IMPALA_SQL)}
                        {this.renderParamsFile(COMPONEMT_CONFIG_KEYS.IMPALA_SQL)}
                    </React.Fragment>
                )
            case COMPONENT_TYPE_VALUE.SPARK_THRIFT_SERVER:
                return (
                    <React.Fragment>
                        {this.renderKerberosFile(COMPONEMT_CONFIG_KEYS.SPARK_THRIFT_SERVER)}
                        {this.renderParamsFile(COMPONEMT_CONFIG_KEYS.SPARK_THRIFT_SERVER)}
                    </React.Fragment>
                )
            case COMPONENT_TYPE_VALUE.SPARK:
                return (
                    <React.Fragment>
                        {this.renderCompsVersion(COMPONEMT_CONFIG_KEYS.SPARK)}
                        {this.renderKerberosFile(COMPONEMT_CONFIG_KEYS.SPARK)}
                        {this.renderParamsFile(COMPONEMT_CONFIG_KEYS.SPARK)}
                    </React.Fragment>
                )
            case COMPONENT_TYPE_VALUE.HIVE_SERVER:
                return (
                    <React.Fragment>
                        {this.renderKerberosFile(COMPONEMT_CONFIG_KEYS.HIVE_SERVER)}
                        {this.renderParamsFile(COMPONEMT_CONFIG_KEYS.HIVE_SERVER)}
                    </React.Fragment>
                )
            case COMPONENT_TYPE_VALUE.LEARNING:
                return (
                    <React.Fragment>
                        {this.renderKerberosFile(COMPONEMT_CONFIG_KEYS.LEARNING)}
                        {this.renderParamsFile(COMPONEMT_CONFIG_KEYS.LEARNING)}
                    </React.Fragment>
                )
            case COMPONENT_TYPE_VALUE.FLINK:
                return (
                    <React.Fragment>
                        {this.renderCompsVersion(COMPONEMT_CONFIG_KEYS.FLINK)}
                        {this.renderKerberosFile(COMPONEMT_CONFIG_KEYS.FLINK)}
                        {this.renderParamsFile(COMPONEMT_CONFIG_KEYS.FLINK)}
                    </React.Fragment>
                )
            case COMPONENT_TYPE_VALUE.DTYARNSHELL:
                return (
                    <React.Fragment>
                        {this.renderKerberosFile(COMPONEMT_CONFIG_KEYS.DTYARNSHELL)}
                        {this.renderParamsFile(COMPONEMT_CONFIG_KEYS.DTYARNSHELL)}
                    </React.Fragment>
                )
            default:
                return '';
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
