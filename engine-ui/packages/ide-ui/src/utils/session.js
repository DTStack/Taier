export function setItem(key, value) {
    // eslint-disable-next-line no-undef
    sessionStorage.setItem(key, value);
}

export function getItem(key) {
    // eslint-disable-next-line no-undef
    return sessionStorage.getItem(key);
}

export function clear() {
    // eslint-disable-next-line no-undef
    return sessionStorage.clear();
}
