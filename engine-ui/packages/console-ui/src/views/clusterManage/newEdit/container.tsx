import * as React from 'react'

import FileConfig from './fileConfig'
import FormConfig from './formConfig'
import ToolBar from './components/toolbar'

interface IProps {
    comp: any;
    form: any;
    view: boolean;
    saveCompsData: any[];
    versionData: any;
    clusterInfo: any;
    testStatus: any;
    initialCompData?: any[];
    commVersion?: string;
    saveComp: (params: any, type?: string) => void;
    getLoadTemplate: (key?: string, params?: any) => void;
    testConnects: Function;
    handleConfirm: Function;
    handleCompVersion?: Function;
}

export default class Container extends React.Component<IProps, any> {
    render () {
        const { comp, view, versionData, initialCompData, commVersion,
            saveCompsData, clusterInfo, handleCompVersion, saveComp,
            testConnects, handleConfirm } = this.props
        return (
            <>
                <FileConfig
                    comp={comp}
                    view={view}
                    form={this.props.form}
                    versionData={versionData}
                    commVersion={commVersion}
                    saveCompsData={saveCompsData}
                    clusterInfo={clusterInfo}
                    handleCompVersion={handleCompVersion}
                />
                <FormConfig
                    comp={comp}
                    view={view}
                    form={this.props.form}
                />
                {view && <ToolBar
                    comp={comp}
                    clusterInfo={clusterInfo}
                    initialCompData={initialCompData}
                    form={this.props.form}
                    saveComp={saveComp}
                    testConnects={testConnects}
                    handleConfirm={handleConfirm}
                />}
            </>
        )
    }
}
