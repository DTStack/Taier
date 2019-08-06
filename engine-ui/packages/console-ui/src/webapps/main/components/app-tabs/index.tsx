import * as React from 'react'
import { Tabs } from 'antd'
import { cloneDeep } from 'lodash';
const TabPane: any = Tabs.TabPane

export default function AppTabs (props: any) {
    const { apps, content, onPaneChange, activeKey, licenseApps = [] } = props;
    const newApps = cloneDeep(apps);
    let enableApps: any = [];
    for (let i: any = 0; i < newApps.length; i++) {
        for (let j: any = 0; j < licenseApps.length; j++) {
            if (newApps[i].id == licenseApps[j].id && licenseApps[j].isShow) {
                newApps[i].enable = licenseApps[j].isShow;
                enableApps.push(newApps[i]);
            }
        }
    }
    const tabPanes = enableApps.length > 0 && enableApps.map((app: any) => {
        const isShow = !app.disableExt && !app.disableSetting;

        return isShow && (<TabPane tab={app.name} key={app.id} data={app}>
            {content}
        </TabPane>)
    });

    return (
        <div className="m-tabs">
            {
                enableApps.length < 2 ? content
                    : <Tabs
                        animated={false}
                        activeKey={activeKey}
                        onChange={onPaneChange}
                    >
                        {tabPanes}
                    </Tabs>
            }
        </div>
    )
}
