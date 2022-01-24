import classNames from 'classnames';
import './index.scss';

enum CIRCLE_TYPES_ENUM {
	running = 'running',
	finished = 'finished',
	stopped = 'stopped',
	frozen = 'frozen',
	fail = 'fail',
	submitting = 'submitting',
	restarting = 'restarting',
	waitSubmit = 'waitSubmit',
}

type CicleType = keyof typeof CIRCLE_TYPES_ENUM;

interface CircleProps {
	type?: CicleType;
	className?: string;
	style?: React.CSSProperties;
	onClick?: () => void;
	children?: React.ReactNode;
}

export default function Circle({ className, type, children, ...other }: CircleProps) {
	const prefixCls = 'dtc-circle';
	const classes = classNames({
		className,
		[`${prefixCls}-default`]: true,
		[`${prefixCls}-${type}`]: type,
	});

	return (
		<div {...other} className={classes}>
			{children || ''}
		</div>
	);
}
