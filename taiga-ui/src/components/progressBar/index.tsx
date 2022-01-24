import './index.scss';

class ProgressBar {
	private clock: number | null;
	private count: number;
	private className: string;
	private hodor: HTMLDivElement;

	constructor() {
		this.clock = null;
		this.count = 0;
		this.className = 'dtc-progress-progress-bar';
		this.hodor = document.createElement('div');
		this.hodor.className = this.className;
	}

	show() {
		this.count += 1;
		if (!this.hasAdded() && !this.clock) {
			this.clock = window.setTimeout(() => {
				document.body.appendChild(this.hodor);
			}, 200);
		}
	}

	hide() {
		this.count -= 1;
		if (this.count <= 0) {
			if (this.clock) {
				clearTimeout(this.clock);
				this.clock = null;
			}
			if (this.hasAdded()) {
				this.hodor.remove();
			}
		}
	}

	hasAdded() {
		return document.getElementsByClassName(this.className).length > 0;
	}
}
export default new ProgressBar();
