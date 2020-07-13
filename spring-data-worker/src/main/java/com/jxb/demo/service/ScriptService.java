package com.jxb.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.script.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @desc js脚本引擎服务
 * @author jiaxuebing
 * @date 2020-05-26
 */
@Component
public class ScriptService {

    private static Logger logger = LoggerFactory.getLogger(ScriptService.class);

    //创建引擎管理器
    private static ScriptEngineManager scriptEngineManager = new ScriptEngineManager();

    private ThreadLocal<ScriptEngine> threadLocal = new ThreadLocal<>();

    //创建js脚本引擎,支持多线程并发
    public ScriptEngine createScriptEngine(){
        ScriptEngine scriptEngine = null;
        scriptEngine = threadLocal.get();
        if(scriptEngine == null){
            scriptEngine = scriptEngineManager.getEngineByName("javascript");
            InputStream is = this.getClass().getResourceAsStream("js/boce_interface.js");
            InputStreamReader reader = new InputStreamReader(is);
            try{
                scriptEngine.eval(reader);
            }catch (ScriptException se){

            }finally{
                closeIOStream(reader);
            }
            threadLocal.set(scriptEngine);
        }
        return scriptEngine;
    }

    /**
     * @desc compiledScript适用于单个函数
     */
    public void compileScript(){
        try{
            ScriptEngine scriptEngine = createScriptEngine();
            Compilable compilable = null;
            if(scriptEngine instanceof Compilable){
                compilable = (Compilable) scriptEngine;
            }
            CompiledScript compiledScript = compilable.compile("function addSum(a,b){return a+b;}");
            compiledScript.eval();

        }catch (ScriptException se){

        }catch (Exception e){

        }
    }

    //关闭输入流
    private void closeIOStream(Object stream){
        try{
            if(stream != null){
               if(stream instanceof InputStream){
                   ((InputStream) stream).close();
               }else if(stream instanceof Reader){
                   ((Reader) stream).close();
               }
            }
        }catch (Exception e){
            logger.error("***[ScriptService::ERROR]:",e);
        }
    }


}
