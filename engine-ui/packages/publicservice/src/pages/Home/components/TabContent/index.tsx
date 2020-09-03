import * as React from 'react';
const TabContent: React.FC<any> = (props: any) => {
	const [color, setColor] = React.useState('blue');
	const changeColor = (e) => {
		setColor('green');
	};

	return (
		<div>
			<div style={{ color }}>函数式组件</div>
			<div  className="example-button" onClick={changeColor}>
				点击换色{' '}
			</div>
		</div>
	);
};
export default TabContent;
