import { useState, useEffect } from 'react';
import { IStreamTaskProps } from '@/interface';
import Editor from '@/components/editor';
import stream from '@/api/stream';
import { prettierJSONstring } from '@/utils';

interface IProps {
	data: IStreamTaskProps | undefined;
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
