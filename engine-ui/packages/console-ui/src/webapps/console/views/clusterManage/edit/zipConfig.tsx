import * as React from 'react';
import utils from 'utils';
import { Tooltip, Row, Col } from 'antd';
import { cloneDeep } from 'lodash';
import { formItemLayout } from '../../../consts'

class ZipConfig extends React.Component<any, any> {
    renderZipConfig (type: any) {
        let { zipConfig } = this.props;
        zipConfig = typeof zipConfig == 'string' ? JSON.parse(zipConfig) : zipConfig
        let keyAndValue: any;
        if (type == 'hdfs') {
            // md5zip 界面不显示
            let copyVal = cloneDeep(zipConfig.hadoopConf || {});
            delete (copyVal['md5zip'])
            keyAndValue = Object.entries(copyVal || {})
            utils.sortByCompareFunctions(keyAndValue,
                ([key, value]: any[], [compareKey, compareValue]: any[]) => {
                    if (key == 'fs.defaultFS') {
                        return -1;
                    }
                    if (compareKey == 'fs.defaultFS') {
                        return 1;
                    }
                    return 0;
                },
                ([key, value]: any[], [compareKey, compareValue]: any[]) => {
                    if (key == 'dfs.nameservices') {
                        return -1;
                    }
                    if (compareKey == 'dfs.nameservices') {
                        return 1;
                    }
                    return 0;
                },
                ([key, value]: any[], [compareKey, compareValue]: any[]) => {
                    if (key.indexOf('dfs.ha.namenodes') > -1) {
                        return -1;
                    }
                    if (compareKey.indexOf('dfs.ha.namenodes') > -1) {
                        return 1;
                    }
                    return 0;
                },
                ([key, value]: any[], [compareKey, compareValue]: any[]) => {
                    const checkKey = key.indexOf('dfs.namenode.rpc-address') > -1
                    const checkCompareKey = compareKey.indexOf('dfs.namenode.rpc-address') > -1
                    if (checkKey && checkCompareKey) {
                        return key > compareKey ? 1 : -1
                    } else if (checkKey) {
                        return -1;
                    } else if (checkCompareKey) {
                        return 1;
                    } else {
                        return 0;
                    }
                });
        } else {
            keyAndValue = Object.entries(zipConfig.yarnConf || {})
            utils.sortByCompareFunctions(keyAndValue,
                ([key, value]: any[], [compareKey, compareValue]: any[]) => {
                    if (key == 'yarn.resourcemanager.ha.rm-ids') {
                        return -1;
                    }
                    if (compareKey == 'yarn.resourcemanager.ha.rm-ids') {
                        return 1;
                    }
                    return 0;
                },
                ([key, value]: any[], [compareKey, compareValue]: any[]) => {
                    const checkKey = key.indexOf('yarn.resourcemanager.address') > -1
                    const checkCompareKey = compareKey.indexOf('yarn.resourcemanager.address') > -1
                    if (checkKey && checkCompareKey) {
                        return key > compareKey ? 1 : -1
                    } else if (checkKey) {
                        return -1;
                    } else if (checkCompareKey) {
                        return 1;
                    } else {
                        return 0;
                    }
                },
                ([key, value]: any[], [compareKey, compareValue]: any[]) => {
                    const checkKey = key.indexOf('yarn.resourcemanager.webapp.address') > -1
                    const checkCompareKey = compareKey.indexOf('yarn.resourcemanager.webapp.address') > -1
                    if (checkKey && checkCompareKey) {
                        return key > compareKey ? 1 : -1
                    } else if (checkKey) {
                        return -1;
                    } else if (checkCompareKey) {
                        return 1;
                    } else {
                        return 0;
                    }
                });
        }

        return keyAndValue.map(
            ([key, value]: any[]) => {
                return (<Row key={key} className="zipConfig-item">
                    <Col className="formitem-textname" span={formItemLayout.labelCol.sm.span + 5}>
                        {key.length > 38
                            ? <Tooltip title={key}>{key.substr(0, 38) + '...'}</Tooltip>
                            : key}：
                    </Col>
                    <Col className="formitem-textvalue" span={formItemLayout.wrapperCol.sm.span - 1}>
                        {value}
                    </Col>
                </Row>)
            }
        )
    }
    render () {
        const { zipConfig, singleButton, type } = this.props;
        return (
            zipConfig ? (
                <React.Fragment>
                    <div className="engine-config-content" style={{ width: '750px' }}>
                        {this.renderZipConfig(type)}
                    </div>
                    {singleButton}
                </React.Fragment>
            ) : null
        )
    }
}
export default ZipConfig;
