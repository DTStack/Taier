export function safeGetConsoleTab (origin, tabId) {
    origin[tabId] = origin[tabId] || { data: [], activeKey: null };
    return origin[tabId];
}
