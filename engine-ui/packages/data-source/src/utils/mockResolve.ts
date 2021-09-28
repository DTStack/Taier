type MockResolveType = <T>(value: T) => Promise<T>;

const mockResolve: MockResolveType = (value) =>
  new Promise((resolve) => {
    setTimeout(() => {
      return resolve(value);
    }, 500);
  });

export default mockResolve;
