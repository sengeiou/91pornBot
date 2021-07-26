package com.example.demo.utils;

import com.example.demo.JavaScriptInterface;
import org.springframework.core.io.ClassPathResource;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;

/**
 * @program: demo
 * @description:
 * @author: Jia Wei
 * @create: 2021-07-14 17:04
 **/
public class JsUtil {

/*    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println(JsUtil.strencode("%3c%73%6f%75%72%63%65%20%73%72%63%3d%27%68%74%74%70%73%3a%2f%2f%63%63%6e%2e%6b%69%6c%6c%63%6f%76%69%64%32%30%32%31%2e%63%6f%6d%2f%2f%6d%33%75%38%2f%34%39%34%37%38%37%2f%34%39%34%37%38%37%2e%6d%33%75%38%3f%73%74%3d%52%65%35%4e%75%79%61%30%52%67%77%63%4d%67%47%4a%71%63%31%53%5a%41%26%65%3d%31%36%32%36%33%34%39%39%33%36%27%20%74%79%70%65%3d%27%61%70%70%6c%69%63%61%74%69%6f%6e%2f%78%2d%6d%70%65%67%55%52%4c%27%3e"));
    }*/

    public static String strencode(String str1) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        try {
            ClassPathResource resource = new ClassPathResource("md2.js");
            BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), "UTF-8"));
            engine.eval(br);
        } catch (ScriptException e) {
            //忽略js脚本异常
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (engine instanceof Invocable) {
            Invocable invocable = (Invocable) engine;
            JavaScriptInterface executeMethod = invocable.getInterface(JavaScriptInterface.class);
            return executeMethod.strencode2(str1);
        }
        throw new RuntimeException("解密失敗");
    }

}



