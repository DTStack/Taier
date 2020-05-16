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
    renderDisplayResource = () => {
        const { components, getFieldDecorator, isView = false,
            fileChange, kerFileChange, paramsfileChange, downloadFile,
            deleteKerFile, uploadLoading, kerUploadLoading, componentConfig } = this.props;
        const componentTypeCode = components.componentTypeCode;
        const config = componentConfig[COMPONEMT_CONFIG_KEY_ENUM[componentTypeCode]] || {};
        // console.log(component.componentTypeCode)
        switch (componentTypeCode) {
            case COMPONENT_TYPE_VALUE.SFTP:
                const { paramsFileName } = config;
                return (
                    <React.Fragment>
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
                            {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.SFTP}.file`, null)(
                                <div>
                                    {!isView && <label
                                        style={{ lineHeight: '32px', textIndent: 'initial', height: 32, width: 172 }}
                                        className="ant-btn"
                                        htmlFor="mySftpFile"
                                    >
                                        <span>
                                            { uploadLoading ? <Icon className="blue-loading" type="loading" style={{ marginRight: 8 }} /> : <Icon type="upload" style={{ marginRight: 8 }} /> }
                                            上传文件
                                        </span>
                                    </label>}
                                    <input
                                        name="file"
                                        type="file"
                                        id="mySftpFile"
                                        onClick={(e: any) => { e.target.value = null }}
                                        onChange={(e: any) => paramsfileChange(e, components.componentTypeCode)}
                                        accept=".zip"
                                        style={{ display: 'none' }}
                                    />
                                    <span style={{ fontSize: 10, color: '#999' }}>仅支持json格式</span>
                                </div>
                            )}
                        </FormItem>
                    </React.Fragment>
                )
            case COMPONENT_TYPE_VALUE.YARN:
                const { fileName, kerFileName } = config;
                return (
                    <React.Fragment>
                        <FormItem
                            label="组件版本"
                            colon={false}
                        >
                            {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.YARN}.hadoopVersion`, {
                                initialValue: components.hadoopVersion || 'hadoop2'
                            })(
                                <Select style={{ width: 172 }} disabled={isView}>
                                    <Option value='hadoop2' key='hadoop2'>hadoop2</Option>
                                    <Option value='hadoop3' key='hadoop3'>hadoop3</Option>
                                    <Option value='HW' key='HW'>HW</Option>
                                </Select>
                            )}
                        </FormItem>
                        <FormItem
                            label="配置文件"
                            colon={false}
                        >
                            {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.YARN}.file`, {
                                initialValue: components.uploadFileName || ''
                            })(
                                <div>
                                    {!isView && <label
                                        style={{ lineHeight: '32px', textIndent: 'initial', height: 32, width: 172 }}
                                        className="ant-btn"
                                        htmlFor="myYarnFile"
                                    >
                                        <span>
                                            { uploadLoading ? <Icon className="blue-loading" type="loading" style={{ marginRight: 8 }} /> : <Icon type="upload" style={{ marginRight: 8 }} /> }
                                            上传文件
                                        </span>
                                    </label>}
                                    <input
                                        type="file"
                                        id="myYarnFile"
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
                        <FormItem
                            label="Hadoop Kerberos认证文件"
                            colon={false}
                        >
                            {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.YARN}.kerberosFileName`, {
                                initialValue: components.kerberosFileName || ''
                            })(
                                <div>
                                    {!isView && <label
                                        style={{ lineHeight: '32px', textIndent: 'initial', height: 32, width: 172 }}
                                        className="ant-btn"
                                        htmlFor="myYarnKerberosFile"
                                    >
                                        <span>
                                            { kerUploadLoading ? <Icon className="blue-loading" type="loading" style={{ marginRight: 8 }} /> : <Icon type="upload" style={{ marginRight: 8 }} /> }
                                            上传文件
                                        </span>
                                    </label>}
                                    <input
                                        type="file"
                                        id="myYarnKerberosFile"
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
