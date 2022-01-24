import { assign } from 'lodash';
import { DoubleRightOutlined } from '@ant-design/icons';
import React from 'react';

import classNames from 'classnames';
import './index.scss';

export interface SlidePaneProps {
	children: React.ReactNode;
	visible: boolean;
	left?: string | number;
	width?: string | number;
	className?: string;
	style?: React.CSSProperties;
	onClose?<HTMLSpanElement, MouseEvent>(): void;
	[propName: string]: any;
}

class SlidePane extends React.Component<SlidePaneProps, any> {
	constructor(props: SlidePaneProps) {
		super(props);
	}

	render() {
		const { children, visible, style, className, onClose } = this.props;
		const slidePrefixCls = 'dtc-slide-pane';
		let myStyle: any = {
			top: 0,
			transform: visible ? undefined : 'translate3d(150%, 0, 0)',
		};
		const classes = classNames(slidePrefixCls, className);
		if (!visible) {
			myStyle['pointerEvents'] = 'none';
		}
		if (style) myStyle = assign(myStyle, style);

		return (
			<div className={classes} style={myStyle}>
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
}

export default SlidePane;
