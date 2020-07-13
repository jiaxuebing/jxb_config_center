package com.jxb.demo.classloader;

/**
 * @desc 自定义类加载器
 */
public class ConfigClassLoader extends ClassLoader{


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }




}
