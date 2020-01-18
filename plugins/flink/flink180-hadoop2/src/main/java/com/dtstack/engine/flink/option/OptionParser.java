package com.dtstack.engine.flink.option;

import com.dtstack.engine.flink.util.FlinkUtil;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class OptionParser {

    public static final String OPTION_SQL = "sql";

    private org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();

    private BasicParser parser = new BasicParser();

    private Options properties = new Options();

    public OptionParser(String[] args) throws Exception {
        initOptions(addOptions(args));
    }

    private CommandLine addOptions(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ParseException {
        Class cla = properties.getClass();
        Field[] fields = cla.getDeclaredFields();
        for(Field field:fields){
            String name = field.getName();
            OptionRequired optionRequired = field.getAnnotation(OptionRequired.class);
            if(optionRequired != null){
                options.addOption(name,optionRequired.hasArg(),optionRequired.description());
            }
        }
        CommandLine cl = parser.parse(options, args);
        return cl;
    }

    private void initOptions(CommandLine cl) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ParseException {
        Class cla = properties.getClass();
        Field[] fields = cla.getDeclaredFields();
        for(Field field:fields){
            String name = field.getName();
            String value = cl.getOptionValue(name);
            OptionRequired optionRequired = field.getAnnotation(OptionRequired.class);
            if(optionRequired != null){
                if(optionRequired.required()&& StringUtils.isBlank(value)){
                    throw new RuntimeException(String.format("parameters of %s is required",name));
                }
            }
            if(StringUtils.isNotBlank(value)){
                field.setAccessible(true);
                field.set(properties,value);
            }
        }
    }

    public Options getOptions(){
        return properties;
    }

    public List<String> getProgramExeArgList() throws Exception {
        Map<String,Object> mapConf = FlinkUtil.ObjectToMap(properties);
        List<String> args = Lists.newArrayList();
        for(Map.Entry<String, Object> one : mapConf.entrySet()){
            String key = one.getKey();
            Object value = one.getValue();
            if(value == null){
                continue;
            }else if(OPTION_SQL.equalsIgnoreCase(key)){
                File file = new File(value.toString());
                FileInputStream in = new FileInputStream(file);
                byte[] filecontent = new byte[(int) file.length()];
                in.read(filecontent);
                String content = new String(filecontent, Charsets.UTF_8.name());
                value = URLEncoder.encode(content, Charsets.UTF_8.name());
            }
            args.add("-" + key);
            args.add(value.toString());
        }
        return args;
    }
}