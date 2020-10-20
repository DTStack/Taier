package com.dtstack.engine.alert.factory;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class JarClassLoader {

	protected URLClassLoader getClassLoader(String path) throws MalformedURLException{
		if(StringUtils.isBlank(path)){
			throw new RuntimeException("class jar path is null");
		}

		File file = new File(path);
		if(file.exists() && file.isFile()){
			URL[] urls = new URL[]{file.toURI().toURL()};
			return new URLClassLoader(urls);
		}
		throw new RuntimeException(String.format("%s not found", path));
	}

	public Object getInstance(String jarPath, String className) throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException{
	    URLClassLoader  classLoader = getClassLoader(jarPath);
		Class<?> clazz = classLoader.loadClass(className);
		return clazz.newInstance();
	} 
	
}
