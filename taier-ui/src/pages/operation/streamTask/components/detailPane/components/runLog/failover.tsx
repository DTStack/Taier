import { useEffect,useState } from 'react';

import stream from '@/api';
import Editor from '@/components/editor';
import { IStreamJobProps } from '@/interface';
import { prettierJSONstring } from '@/utils';

interface IProps {
    data: IStreamJobProps | undefined;
    isShow: boolean;
}

export default function Failover({ data }: IProps) {
    const [logInfo, setLogInfo] = useState('');

    const getLog = async () => {
        if (!data?.id) return;
        const res = await stream.getFailoverLogsByTaskId({ taskId: data.id });
        if (res.code == 1) {
            setLogInfo(prettierJSONstring(res.data));
        }
    };

    useEffect(() => {
        getLog();
    });

    return (
        <Editor
            style={{ height: '100%' }}
            sync
            value={logInfo || ''}
            language="jsonlog"
            options={{
                readOnly: true,
                minimap: {
                    enabled: false,
                },
            }}
        />
    );
}
