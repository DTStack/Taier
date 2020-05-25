import * as React from 'react';
import {
    Form, Select, Icon
} from 'antd';
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

    // 配置文件
    renderConfigsFile = (configName: any) => {
        const { getFieldDecorator, components, isView, uploadLoading,
            fileChange, downloadFile } = this.props;
        const config = this.getComponentConfig();
        const { fileName } = config;
        const labelName = configName === COMPONEMT_CONFIG_KEYS.KUBERNETES ? 'Hadoop Kerberos认证文件' : '配置文件'
        return (
            <FormItem
                label={labelName}
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
                        <span style={{ fontSize: 10, color: '#999' }}>仅支持.zip格式，至少包括yarn-site.xml</span>
                        { fileName && <div className="c-displayResource__downloadFile" onClick={() => components.id && downloadFile(components, 1)} style={{ fontSize: 12, color: '#3F87FF' }}>
                            <span>
                                <Icon type="paper-clip" style={{ marginRight: 2, color: '#666666FF' }} />
                                {utils.textOverflowExchange(fileName, 16)}
                            </span>
                            <Icon type="download" style={{ color: '#666666FF' }} />
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
                        <div style={{ fontSize: 10, color: '#999' }}>仅支持.zip格式</div>
                        { kerFileName && <div className="c-displayResource__downloadFile" style={{ fontSize: 12, color: '#3F87FF' }}>
                            <span>
                                <Icon type="paper-clip" style={{ marginRight: 2, color: '#666666FF' }} />
                                {utils.textOverflowExchange(kerFileName, 12)}
                            </span>
                            <span>
                                <Icon type="download" style={{ color: '#666666FF' }} onClick={() => components.id && downloadFile(components, 0)} />
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
                        <span style={{ fontSize: 10, color: '#999' }}>仅支持json格式</span>
                    </div>
                )}
            </FormItem>
        )
    }

    // 组件版本
    renderCompVersion = (configName: any) => {
        const { getFieldDecorator, getFieldValue, components, isView } = this.props;
        const componentTypeCode = components.componentTypeCode;
        let connectHadoopVersion: any = '';
        if (componentTypeCode === COMPONENT_TYPE_VALUE.YARN) {
            connectHadoopVersion = getFieldValue(`${COMPONEMT_CONFIG_KEYS.HDFS}.hadoopVersion`) || components.hadoopVersion
        }
        if (componentTypeCode === COMPONENT_TYPE_VALUE.HDFS) {
            connectHadoopVersion = getFieldValue(`${COMPONEMT_CONFIG_KEYS.YARN}.hadoopVersion`) || components.hadoopVersion
        }
        // console.log('connectHadoopVersion=====ssss===',componentTypeCode, connectHadoopVersion)
        return (
            <FormItem
                label="组件版本"
                colon={false}
            >
                {getFieldDecorator(`${configName}.hadoopVersion`, {
                    initialValue: connectHadoopVersion || 'hadoop2'
                })(
                    <Select style={{ width: 172 }} disabled={isView} onChange={(val) => this.handleCompsVersion(val, componentTypeCode)}>
                        <Option value='hadoop2' key='hadoop2'>hadoop2</Option>
                        <Option value='hadoop3' key='hadoop3'>hadoop3</Option>
                        <Option value='HW' key='HW'>HW</Option>
                    </Select>
                )}
            </FormItem>
        )
    }

    handleFlinkSparkVersion = (val: any, key: number) => {
        this.props.handleFlinkSparkVersion(key, val);
    }

    handleCompsVersion = (val: any, key: number) => {
        this.props.handleCompsVersion(key, val);
    }

    // flink组件版本
    renderFilkCompVersion = (configName: any) => {
        // const { compVersion } = this.state;
        const { getFieldDecorator, components, isView } = this.props;
        return (
            <FormItem
                label="组件版本"
                colon={false}
            >
                {getFieldDecorator(`${configName}.hadoopVersion`, {
                    initialValue: components.hadoopVersion || '180'
                })(
                    <Select style={{ width: 172 }} disabled={isView} onChange={(val) => this.handleFlinkSparkVersion(val, COMPONENT_TYPE_VALUE.FLINK)} >
                        <Option value='140' key='1.4'>1.4</Option>
                        <Option value='150' key='1.5'>1.5</Option>
                        <Option value='180' key='1.8'>1.8</Option>
                    </Select>
                )}
            </FormItem>
        )
    }
    // spark组件版本
    renderSparkCompVersion = (configName: any) => {
        const { getFieldDecorator, components, isView } = this.props;
        return (
            <FormItem
                label="组件版本"
                colon={false}
            >
                {getFieldDecorator(`${configName}.hadoopVersion`, {
                    initialValue: components.hadoopVersion || '2.1.x'
                })(
                    <Select style={{ width: 172 }} disabled={isView} onChange={(val) => this.handleFlinkSparkVersion(val, COMPONENT_TYPE_VALUE.SPARK)}>
                        <Option value='2.1.x' key='2.1.X'>2.1.X</Option>
                        <Option value='2.3.x' key='2.3.X'>2.3.X</Option>
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
                        {this.renderCompVersion(COMPONEMT_CONFIG_KEYS.YARN)}
                        {this.renderConfigsFile(COMPONEMT_CONFIG_KEYS.YARN)}
                        {this.renderKerberosFile(COMPONEMT_CONFIG_KEYS.YARN)}
                    </React.Fragment>
                )
            case COMPONENT_TYPE_VALUE.KUBERNETES:
                return this.renderConfigsFile(COMPONEMT_CONFIG_KEYS.KUBERNETES)
            case COMPONENT_TYPE_VALUE.HDFS:
                return (
                    <React.Fragment>
                        {this.renderCompVersion(COMPONEMT_CONFIG_KEYS.HDFS)}
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
                        {this.renderSparkCompVersion(COMPONEMT_CONFIG_KEYS.SPARK)}
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
                        {this.renderFilkCompVersion(COMPONEMT_CONFIG_KEYS.FLINK)}
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
