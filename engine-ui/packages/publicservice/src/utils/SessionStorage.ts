class SessionStorage {
  private key: string;
  constructor(key: string) {
    this.key = key;
  }
  public set(value: string): void {
    window.sessionStorage.setItem(this.key, value);
  }
  public get(): string {
    return window.sessionStorage.getItem(this.key);
  }
  public has(): boolean {
    return !!window.sessionStorage.getItem(this.key);
  }
  public destory(): void {
    window.sessionStorage.removeItem(this.key);
  }
}

export default SessionStorage;
