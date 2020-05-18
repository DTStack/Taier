import * as React from 'react';
import {
    Form, Select, Icon
} from 'antd';
import utils from 'dt-common/src/utils';
import {
    COMPONENT_TYPE_VALUE, COMPONEMT_CONFIG_KEYS, COMPONEMT_CONFIG_KEY_ENUM
} from '../../../consts';

const FormItem = Form.Item;
const Option = Select.Option;
class DisplayResource extends React.Component<any, any> {
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
                {getFieldDecorator(`${configName}.file`, {
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
        const { paramsFileName } = config;
        return (
            <FormItem
                label={
                    <span>
                        <span>参数批量上传</span>
                        {
                            paramsFileName
                                ? <span
                                    className="c-displayResource__downloadTemp"
                                    onClick={() => downloadFile(components, 3)}
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
                {getFieldDecorator(`${configName}.file`, null)(
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
                            accept=".zip"
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
        const { getFieldDecorator, components, isView } = this.props;
        return (
            <FormItem
                label="组件版本"
                colon={false}
            >
                {getFieldDecorator(`${configName}.hadoopVersion`, {
                    initialValue: components.hadoopVersion || 'hadoop2'
                })(
                    <Select style={{ width: 172 }} disabled={isView}>
                        <Option value='hadoop2' key='hadoop2'>hadoop2</Option>
                        <Option value='hadoop3' key='hadoop3'>hadoop3</Option>
                        <Option value='HW' key='HW'>HW</Option>
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
            case COMPONENT_TYPE_VALUE.SPARKTHRIFTSERVER:
                return (
                    <React.Fragment>
                        {this.renderKerberosFile(COMPONEMT_CONFIG_KEYS.SPARKTHRIFTSERVER)}
                        {this.renderParamsFile(COMPONEMT_CONFIG_KEYS.SPARKTHRIFTSERVER)}
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
