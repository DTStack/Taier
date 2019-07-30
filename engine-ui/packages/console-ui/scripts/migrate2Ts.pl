 #!/usr/bin/perl
 # JS 迁移到TS工具
 # 主要是做基本的转换
 # 使用示例： perl -i ./scripts/migrate2Ts.pl ./src/webapps/main/app.tsx 
 # -p 对目标文件每行进行查找和替换， -i表示将替换结果写回文件，-e 整个程序接在命令后面

## 文件名替换
# 所有 import React 的文件转换为 tsx, 否则其他的文件则转换为 ts

undef $/;   # 进入”file-slurp"（文件读取）模式
$text = <>;
# $file1 = $ARGV[0]; # 读取命令行第一个文件
# $text = $file1;

# import React from 'react, 引入方式的文件转换为import * as React from 'react'
$text =~ s/import\s+React(,\s+{\s+Component\s+})?\s+from\s+'react'/import * as React from 'react'/gx;

# import ReactDOM from 'react-dom', 引入方式的文件转换为import * as ReactDOM from 'react-dom'
$text =~ s/import\s+ReactDOM\sfrom\s'react-dom'/import * as ReactDOM from 'react-dom'/gx;

# extends Component 或者extends React.Component 继承替换为 extends React.Component<any, any>
$text =~ s/extends\s+(React\.)?Component(?!<any)/extends React.Component<any, any>/gx;

# 所有Form.create()(\b([a-zA-Z]+)\b) 替换为 Form.create<any>()($1)
$text =~ s/Form.create\(\)/Form.create<any>\(\)/gx;

# varible => 替换为 (variable: any) =>
$text =~ s/\b([a-zA-z]+)\b\s=>/($1: any) =>/gx;

# (var1) => 替换为 (var1: any) =>
$text =~ s/\((\b[a-zA-Z_]+\b(?!:\sany))\)\s+=>/($1: any) =>/gx;

# (var1, var2) => 替换为 (var1: any, var2: any) =>
$text =~ s/\((\b[a-zA-Z_]+\b(?!:\sany)),\s(\b[a-zA-Z_]+\b(?!:\sany))\)\s+=>/($1: any, $2: any) =>/gx;

# 处理[abc (a, b) {}]
$text =~ s/\s+\(([a-zA-z]+(?!:\sany)),\s([a-zA-z]+(?!:\sany))\)\s+{/($1: any, $2: any) {/gx;

# func (varible) {} 替换为 func (variable: any) {}
$text =~ s/(?<!if)(?<!else if)(?<!switch)(?<!while)\s+\(\b([a-zA-z]+)\b(?!:\sany)\)\s+{/($1: any) {/gx;

# state: any = {
$text =~ s/(state)(?!:\sany)\s+=\s+{/$1: any = {/gx;

# 空数组添加any类型声明
$text =~ s/\b([\w]+)(?<!any)\b\s=\s\[\]/$1: any = []/gx;

# 空对象添加any声明
$text =~ s/\b([\w]+)(?<!any)\b\s=\s{}/$1: any = {}/gx;

# 
print $text;







