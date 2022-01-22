#!/usr/bin/perl
# ES6 + React + Redux project migrate to Typescrit project.
# This is just a helper script. It can't convert to Typescript project completely, 
# some sitiuations still need you to solve manually.
# Author: xiaowei@dtstack.com
# Example: find src/webapps/**/*.js -exec ./scripts/migrateToTs.pl '{}' \;
# Perl Doc: https://perldoc.perl.org/perlreref.html

use strict;
use warnings;

my $src = $ARGV[0];
my $des = $src;
my $text = "";

sub renameFile {
    if ($src =~ m/jsx?$/) {
        print "Starting to rename file $src.\n";

        open(SRC, "<", $src) or die $!; # open the file, for read

        while(<SRC>){
            $text .= $_; 
        }
        if ($text =~ m/React/g) {# This is a React file
            print "This is React Component file.\n";
            $des =~ s/(.*).jsx?/$1.tsx/;
        } else {# Rename to ts
            print "Just a normal Javascript file.\n";
            $des =~ s/(.*).js/$1.ts/;
        }
        print "Renaming $src to $des\n";
        system "mv $src $des";
    }
    close(SRC);
}

sub convertToTyping {

    open(DES, "<", $des) or die $!; # open the destination file, for read

    if ($text eq "") {
        while(<DES>){
            $text .= $_; 
        }
    }

    # import React from 'react, 引入方式的文件转换为import * as React from 'react'
    $text =~ s/import\s+React(,\s+{\s+Component\s+})?\s+from\s+['"]react['"]/import * as React from 'react'/gx;

    # import ReactDOM from 'react-dom', 引入方式的文件转换为import * as ReactDOM from 'react-dom'
    $text =~ s/import\s+ReactDOM\sfrom\s['"]react-dom['"]/import * as ReactDOM from 'react-dom'/gx;

    # extends Component 或者extends React.Component 继承替换为 extends React.Component<any, any>
    $text =~ s/extends\s+(React\.)?Component(?!<any)/extends React.Component<any, any>/gx;
    $text =~ s/extends\s+(React\.)?PureComponent(?!<any)/extends React.PureComponent<any, any>/gx;

    # 所有Form.create()(\b([\w]+)\b) 替换为 Form.create<any>()($1)
    $text =~ s/Form.create\(\)/Form.create<any>\(\)/gx;

    # varible => 替换为 (variable: any) =>
    $text =~ s/\b([\w]+)\b\s=>/($1: any) =>/gx;

    # (var1) => 替换为 (var1: any) =>
    $text =~ s/\((\b[\w]+\b(?!:\sany))\)\s+=>/($1: any) =>/gx;

    # (var1, var2) => 替换为 (var1: any, var2: any) =>
    $text =~ s/\(([\w]+(?!:\sany)),\s*([\w]+(?!:\sany))\)\s*=>/($1: any, $2: any) =>/gx;

   # (var1, var2, var3) => 替换为 (var1: any, var2: any, var3: any) =>
    $text =~ s/\(([\w]+(?!:\sany)),\s*([\w]+(?!:\sany)),\s*([\w]+(?!:\sany))\)\s*=>/($1: any, $2: any, $3: any) =>/gx;
    $text =~ s/\(([\w]+(?!:\sany)),\s*([\w]+(?!:\sany)),\s*([\w]+(?!:\sany)),\s*([\w]+(?!:\sany))\)\s*=>/($1: any, $2: any, $3: any, $4: any) =>/gx;
    $text =~ s/\(([\w]+(?!:\sany)),\s*([\w]+(?!:\sany)),\s*([\w]+(?!:\sany)),\s*([\w]+(?!:\sany)),\s*([\w]+(?!:\sany))\)\s*=>/($1: any, $2: any, $3: any, $4: any, $5: any) =>/gx;

    # 处理[abc (a, b) {}]
    $text =~ s/\(([\w]+(?!:\sany)),\s*([\w]+(?!:\sany))\)\s+{/($1: any, $2: any) {/gx;
    
    # 处理[abc (a, b, c) {}]
    $text =~ s/\(([\w]+(?!:\sany)),\s*([\w]+(?!:\sany)),\s*([\w]+(?!:\sany))\)\s+{/($1: any, $2: any, $3: any) {/gx;
    $text =~ s/\(([\w]+(?!:\sany)),\s*([\w]+(?!:\sany)),\s*([\w]+(?!:\sany)),\s*([\w]+(?!:\sany))\)\s+{/($1: any, $2: any, $3: any, $4: any) {/gx;
    $text =~ s/\(([\w]+(?!:\sany)),\s*([\w]+(?!:\sany)),\s*([\w]+(?!:\sany)),\s*([\w]+(?!:\sany)),\s*([\w]+(?!:\sany))\)\s+{/($1: any, $2: any, $3: any, $4: any, $5: any) {/gx;

    # func (varible) {} 替换为 func (variable: any) {}
    $text =~ s/(?<!if)(?<!else if)(?<!switch)(?<!catch)(?<!while)(?<!\s)\s?\(([\w]+)(?!:\sany)\)\s+{/($1: any) {/gx;

    # state: any = {
    $text =~ s/(?<!\.)(state)(?!:\sany)\s+=\s+{/$1: any = {/gx;

    # 数组添加any类型声明
    $text =~ s/(let|const|var)\s+([\w]+)(?!:any)\s=\s\[/$1 $2: any = [/gx;

    # 对象添加any声明
    $text =~ s/(let|const|var)\s+([\w]+)(?<!any)\s=\s{/$1 $2: any = {/gx;

    # 未声明类型的变量
    $text =~ s/(let|const|var)\s+([\w]+)(?=;|\n|$)/$1 $2: any/gx;

    # TODO 对象多行匹配
    # Typing for Redux
    # 转换@connect(mapStateToProps, mapDispatchToProps)
    $text =~ s/\@connect\((.*)\)/@(connect($1) as any)/gx;
    # params keyword action add typing
    $text =~ s/action\)/action: any)/gx;

    open(DES, ">", $des) or die $!; # open the destination file, for write

    print DES $text; # Write typing content into file.

    close(DES);

    print "Typing file $des successfully!\n";
}

renameFile();
convertToTyping();

