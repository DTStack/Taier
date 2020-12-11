import * as React from 'react'
import { Form, Select, Upload, Button, Icon } from 'antd'
import { COMPONENT_TYPE_VALUE, VERSION_TYPE } from '../const'

interface IProps {
    comp: any;
    form: any;
    versionData: any;
}

const FormItem = Form.Item
const Option = Select.Option
export default class FileConfig extends React.PureComponent<IProps, any> {
    renderCompsVersion = () => {
        const { getFieldDecorator } = this.props.form
        const { versionData, comp } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        let version = []
        let initialValue = ''
        switch (typeCode) {
            case COMPONENT_TYPE_VALUE.FLINK:
            case COMPONENT_TYPE_VALUE.SPARK: {
                version = versionData[VERSION_TYPE[typeCode]]
                initialValue = versionData[VERSION_TYPE[typeCode]][0].value
                break
            }
            case COMPONENT_TYPE_VALUE.SPARK_THRIFT_SERVER:
            case COMPONENT_TYPE_VALUE.HIVE_SERVER: {
                version = versionData[VERSION_TYPE[typeCode]]
                initialValue = versionData[VERSION_TYPE[typeCode]][1].value
                break
            }
            default: {
                version = versionData.hadoopVersion
                initialValue = versionData.hadoopVersion[0].value
                break
            }
        }
        return (
            <FormItem
                label="组件版本"
                colon={false}
                key={`${typeCode}.hadoopVersion`}
            >
                {getFieldDecorator(`${typeCode}.hadoopVersion`, {
                    initialValue: initialValue
                })(
                    <Select style={{ width: 172 }}>
                        {version.map((ver: any) => {
                            return <Option value={ver.value} key={ver.key}>{ver.key}</Option>
                        })}
                    </Select>
                )}
            </FormItem>
        )
    }

    // 参数批量上传文件
    renderParamsFile = (configName: any) => {
        const { comp, form } = this.props
        const typeCode = comp?.componentTypeCode ?? ''
        const uploadParamsProp = {
            name: 'paramsFile',
            accept: '.zip',
            beforeUpload: (file: any) => {
                form.setFieldsValue({
                    [`${typeCode}.paramsFile`]: file
                })
                return false;
            },
            fileList: []
        }
        console.log('ss === ', form.getFieldValue(`${typeCode}.paramsFile`))
        return (
            <FormItem
                label={<span>参数批量上传<span
                    className="c-fileConfig__downloadTemp"
                    // onClick={() => this.downloadFile(DOWNLOAD_TYPE.PARAMES)}
                >
                    {form.getFieldValue(`${typeCode}.paramsFile`) ? '下载参数' : '下载模板'}
                </span></span>}
                colon={false}
            >
                {form.getFieldDecorator(`${typeCode}.paramsFile`)(<div />)}
                <div className="c-fileConfig__config">
                    <Upload {...uploadParamsProp}>
                        <Button style={{ width: 172 }} icon="upload">点击上传</Button>
                    </Upload>
                    <span className="config-desc">zip格式，至少包括yarn-site.xml</span>
                </div>
                {form.getFieldValue(`${typeCode}.paramsFile`) && <span className="config-file">
                    <Icon type="paper-clip" />
                    {form.getFieldValue(`${typeCode}.paramsFile`)?.name}
                    <Icon type="delete" onClick={() => {
                        form.setFieldsValue({
                            [`${typeCode}.paramsFile`]: ''
                        })
                    }} />
                </span>}

            </FormItem>
        )
    }

    renderFileConfig = () => {
        const typeCode = this.props?.comp?.componentTypeCode ?? ''
        switch (typeCode) {
            // case COMPONENT_TYPE_VALUE.ORACLE_SQL:
            // case COMPONENT_TYPE_VALUE.LIBRA_SQL:
            // case COMPONENT_TYPE_VALUE.TIDB_SQL:
            // case COMPONENT_TYPE_VALUE.GREEN_PLUM_SQL:
            // case COMPONENT_TYPE_VALUE.PRESTO_SQL: {
            //     return (
            //         <>
            //             {this.renderParamsFile(typeCode)}
            //             {this.renderStorageComponents(typeCode)}
            //         </>
            //     )
            // }
            // case COMPONENT_TYPE_VALUE.SFTP:
            // case COMPONENT_TYPE_VALUE.NFS: {
            //     return this.renderParamsFile(typeCode)
            // }
            // case COMPONENT_TYPE_VALUE.KUBERNETES: {
            //     return (
            //         <>
            //             {this.renderConfigsFile(typeCode)}
            //             {this.renderKerberosFile(typeCode)}
            //             {this.renderPrincipal(typeCode)}
            //         </>
            //     )
            // }
            case COMPONENT_TYPE_VALUE.IMPALA_SQL:
            case COMPONENT_TYPE_VALUE.HIVE_SERVER:
                return (
                    <>
                        {this.renderCompsVersion()}
                        {/* {this.renderKerberosFile(typeCode)}
                        {this.renderPrincipal(typeCode)}
                        {this.renderParamsFile(typeCode)}
                        {this.renderStorageComponents(typeCode)} */}
                    </>
                )
            // case COMPONENT_TYPE_VALUE.LEARNING:
            // case COMPONENT_TYPE_VALUE.DTYARNSHELL: {
            //     return (
            //         <>
            //             {this.renderKerberosFile(typeCode)}
            //             {this.renderPrincipal(typeCode)}
            //             {this.renderParamsFile(typeCode)}
            //             {this.renderStorageComponents(typeCode)}
            //         </>
            //     )
            // }
            case COMPONENT_TYPE_VALUE.YARN:
            case COMPONENT_TYPE_VALUE.HDFS: {
                return (
                    <>
                        {this.renderCompsVersion()}
                        {/* {this.renderConfigsFile(typeCode)}
                        {this.renderKerberosFile(typeCode)}
                        {this.renderPrincipal(typeCode)} */}
                    </>
                )
            }
            case COMPONENT_TYPE_VALUE.SPARK_THRIFT_SERVER:
            case COMPONENT_TYPE_VALUE.SPARK:
            case COMPONENT_TYPE_VALUE.FLINK: {
                return (
                    <>
                        {this.renderCompsVersion()}
                        {/* {this.renderKerberosFile(typeCode)}
                        {this.renderPrincipal(typeCode)}
                        {this.renderParamsFile(typeCode)}
                        {this.renderStorageComponents(typeCode)} */}
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
