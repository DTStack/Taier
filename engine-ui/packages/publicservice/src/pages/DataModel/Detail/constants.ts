const textDefaultPlaceholder = (holder: string) => {
  return (value) => {
    return value ? value : holder;
  };
};

export const holder = textDefaultPlaceholder('--');
