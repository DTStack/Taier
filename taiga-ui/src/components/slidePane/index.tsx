import React from 'react';
import { DoubleRightOutlined } from '@ant-design/icons';
import classNames from 'classnames';
import './index.scss';

interface SlidePaneProps {
	children: React.ReactNode;
	visible: boolean;
	left?: string | number;
	width?: string | number;
	className?: string;
	style?: React.CSSProperties;
	onClose?: React.MouseEventHandler<HTMLSpanElement>;
	[propName: string]: any;
}

const slidePrefixCls = 'dtc-slide-pane';

export default function SlidePane({
	className,
	visible,
	children,
	onClose,
	style = {},
}: SlidePaneProps) {
	const myStyle: React.CSSProperties = {
		top: 0,
		transform: visible ? undefined : 'translate3d(150%, 0, 0)',
	};

	if (!visible) {
		myStyle.pointerEvents = 'none';
	}

	return (
		<div className={classNames(slidePrefixCls, className)} style={{ ...myStyle, ...style }}>
			<div
				className={`${slidePrefixCls}-conent`}
				data-testid="slidepane_container"
				style={{
					display: visible ? 'block' : 'none',
					height: '100%',
				}}
			>
				{children}
			</div>
			<span
				className={`${slidePrefixCls}-toggle`}
				data-testid="slidepane_action"
				onClick={onClose}
			>
				<DoubleRightOutlined />
			</span>
		</div>
	);
}
