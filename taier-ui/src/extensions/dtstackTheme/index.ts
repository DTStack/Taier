// 读取 package.json 中的内容
import dtstackTheme from './themes/dtstackTheme.json';
import MyTheme from './package.json';

// 读取详细的主题颜色内容
const themes = [dtstackTheme];

const packageThemes = MyTheme.contributes?.themes || [];

MyTheme.contributes.themes = packageThemes.map((theme, index) => {
    // 为每个 theme 添加 id
    const nextTheme = { ...theme, ...themes[index] };
    return nextTheme;
});

// 声明当前主题的唯一 id
(MyTheme as any).id = 'MyTheme';

// 导出 package.json 的内容供 Molecule 使用
export default JSON.parse(JSON.stringify(MyTheme));
