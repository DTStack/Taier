import { useMemo, useState } from 'react';
import { Radio } from 'antd';

import Editor from '@/components/editor';
import { CREATE_MODEL_TYPE,TASK_TYPE_ENUM } from '@/constant';
import type { IStreamJobParamsProps } from '@/interface';
import { prettierJSONstring } from '@/utils';

export type IRunCodeDataProps = Pick<
    IStreamJobParamsProps,
    'createModel' | 'taskType' | 'taskParams' | 'id' | 'originSourceType' | 'targetSourceType' | 'sqlText'
> & {
    sourceStr?: string;
    targetStr?: string;
    settingStr?: string;
};
interface IProps {
    data?: IRunCodeDataProps;
}

enum TAB_KEYS {
    CODE = 'code',
    SOURCE = 'source',
    SINK = 'sink',
    SIDE = 'side',
    ENV = 'env',
}

export default function RunCode({ data }: IProps) {
    const [tabKey, setTabKey] = useState<TAB_KEYS>(TAB_KEYS.ENV);

    const renderContent = (key: TAB_KEYS) => {
        switch (key) {
            case TAB_KEYS.CODE:
                return (
                    <Editor
                        sync
                        style={{ height: '100%' }}
                        language={data?.taskType === TASK_TYPE_ENUM.SQL ? 'hivesql' : 'json'}
                        options={{ readOnly: true, minimap: { enabled: false } }}
                        value={prettierJSONstring(data?.sqlText || '')}
                    />
                );
            case TAB_KEYS.SOURCE:
                return (
                    <Editor
                        sync
                        style={{ height: '100%' }}
                        language="sql"
                        options={{ readOnly: true, minimap: { enabled: false } }}
                        value={data?.sourceStr}
                    />
                );
            case TAB_KEYS.SINK:
                return (
                    <Editor
                        sync
                        style={{ height: '100%' }}
                        language="sql"
                        options={{ readOnly: true, minimap: { enabled: false } }}
                        value={data?.targetStr}
                    />
                );
            case TAB_KEYS.SIDE:
                return (
                    <Editor
                        sync
                        style={{ height: '100%' }}
                        language="sql"
                        options={{ readOnly: true, minimap: { enabled: false } }}
                        value={data?.settingStr}
                    />
                );
            case TAB_KEYS.ENV:
                return (
                    <Editor
                        sync
                        style={{ height: '100%' }}
                        language="ini"
                        options={{ readOnly: true, minimap: { enabled: false } }}
                        value={data?.taskParams}
                    />
                );
            default:
                return null;
        }
    };

    const isflinkSql = useMemo(() => data?.taskType === TASK_TYPE_ENUM.SQL, [data]);
    const isGuideMode = useMemo(() => data?.createModel !== CREATE_MODEL_TYPE.SCRIPT, [data]);

    return (
        <div className="m-tabs h-full">
            <Radio.Group style={{ padding: '12px 20px' }} value={tabKey} onChange={(e) => setTabKey(e.target.value)}>
                <Radio.Button value={TAB_KEYS.CODE}>运行代码</Radio.Button>
                {isflinkSql && isGuideMode && (
                    <>
                        <Radio.Button value={TAB_KEYS.SOURCE}>源表</Radio.Button>
                        <Radio.Button value={TAB_KEYS.SINK}>结果表</Radio.Button>
                        <Radio.Button value={TAB_KEYS.SIDE}>维表</Radio.Button>
                    </>
                )}
                <Radio.Button value={TAB_KEYS.ENV}>环境参数</Radio.Button>
            </Radio.Group>
            {renderContent(tabKey)}
        </div>
    );
}
