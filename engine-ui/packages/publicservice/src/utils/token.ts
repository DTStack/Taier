const TOKEN_KEY = 'token';

interface IToken {
  get: () => string;
  set: (newToken: string) => void;
  delete: () => void;
  has: () => boolean;
}

const getToken = (tokenKey: string):IToken => {
  return {
    get() {
      return window.sessionStorage.getItem(tokenKey);
    },
    set(newToken: string) {
      window.sessionStorage.setItem(tokenKey, newToken);
    },
    delete() {
      window.sessionStorage.removeItem(tokenKey);
    },
    has() {
      return window.sessionStorage.getItem(tokenKey) !== null;
    }
  }
}

export default getToken(TOKEN_KEY);
