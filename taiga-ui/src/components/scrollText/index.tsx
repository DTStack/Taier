import React from 'react';
import './index.scss';

interface ScrollTextProps {
	value?: string;
	style?: React.CSSProperties;
	children?: React.ReactNode;
}

const DEFAULT_STYLE: React.CSSProperties = {
	height: '28px',
	margin: '5px 5px 5px 0px',
	width: '100%',
	textAlign: 'left',
	backgroundColor: 'transparent',
	backgroundImage: 'none',
	border: 'none',
};

export default function scrollText(props: ScrollTextProps) {
	const { value, style: propsStyle = {} } = props;

	return (
		<input
			data-testid="test-scroll-text"
			style={{ ...DEFAULT_STYLE, ...propsStyle }}
			title={value}
			readOnly
			className="cell-input"
			value={value}
		/>
	);
}
