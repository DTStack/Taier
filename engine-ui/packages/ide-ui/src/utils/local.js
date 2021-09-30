export function setItem(key, value) {
	// eslint-disable-next-line no-undef
	localStorage.setItem(key, value);
}

export function getItem(key) {
	// eslint-disable-next-line no-undef
	return localStorage.getItem(key);
}

export function clear() {
	// eslint-disable-next-line no-undef
	return localStorage.clear();
}
