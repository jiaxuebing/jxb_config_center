package com.jxb.demo.bytecode;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.reflect.Method;

/**
 * @desc 使用javassist/jdkCompiler
 */
public class DynamicJava {

    /**
     * javassist操作字节码文件
     * 1) 获取类池ClassPool：ClassPool classPool = ClassPool.getDefault();
     * 2) 获取字节码对象CtClass: CtClass ctClass = classPool.getCtClass(className);
     * 3) CtMethod、CtField
     * @param args
     */

    public static void main(String[] args) {
        ClassPool classPool = ClassPool.getDefault();
        try{
            CtClass ctClass = classPool.getCtClass("com.jxb.demo.bytecode.Test");
            CtMethod ctMethod = ctClass.getDeclaredMethod("test");
            ctMethod.insertBefore("System.out.println(\"runing is start\");");
            ctMethod.insertAfter("System.out.println(\"running is end\");");
            ctClass.writeFile();

            Class clazz = ctClass.toClass();
            Method method = clazz.getDeclaredMethod("test");
            method.invoke(clazz.newInstance());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
