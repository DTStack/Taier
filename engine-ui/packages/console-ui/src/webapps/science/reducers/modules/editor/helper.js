export function safeGetConsoleTab (origin, tabId) {
    origin[tabId] = origin[tabId] || { data: [] };
    return origin[tabId];
}
