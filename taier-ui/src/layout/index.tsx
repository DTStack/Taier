import { Provider } from 'react-redux';
import store from '@/store';
import zhCN from 'antd/lib/locale/zh_CN';
import { ConfigProvider } from 'antd';

export default function Layout(props: React.PropsWithChildren<React.ReactNode>) {
	return (
		<ConfigProvider locale={zhCN}>
			<Provider store={store}>{props.children}</Provider>
		</ConfigProvider>
	);
}
