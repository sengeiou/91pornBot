package com.example.demo.utils;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.example.demo.DemoApplication.*;
import static com.example.demo.config.MyappConfig.ON_PROXY;


/**
 * @program: demo
 * @description:
 * @author: Jia Wei
 * @create: 2021-07-15 15:49
 **/
@Slf4j
public class WebUtil {

    public static HtmlPage UnitPage(String url)  {
        // 屏蔽HtmlUnit等系统 log
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.http.client").setLevel(Level.OFF);
        System.getProperties().put("org.apache.commons.logging.simplelog.defaultlog","fatal");
        Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
        Logger.getLogger("org.apache.http").setLevel(Level.OFF);
        log.info("Loading page now-----------------------------------------------: "+url);

        // HtmlUnit 模拟浏览器
        HtmlPage page = null;
        try {
            WebClient webClient = new WebClient(BrowserVersion.CHROME);
            // 启用JS解释器，默认为true
            webClient.getOptions().setJavaScriptEnabled(false);
            // 禁用css支持
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            // 设置连接超时时间
            webClient.getOptions().setTimeout(100 * 1000);
            if (ON_PROXY){
                ProxyConfig proxyConfig = new ProxyConfig(PROXY_HOST,PROXY_PORT,null);
                webClient.getOptions().setProxyConfig(proxyConfig);
            }
            webClient.addRequestHeader("Accept-Language","zh-cn,zh;q=0.5");
            webClient.addRequestHeader("Connection", "keep-alive");
            page = webClient.getPage(url);
            // 等待js后台执行30秒
            webClient.waitForBackgroundJavaScript(30 * 1000);
        } catch (IOException e) {

        } catch (FailingHttpStatusCodeException e) {

        }


        return page;
    }

}
