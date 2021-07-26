package com.example.demo;

import com.example.demo.config.MyappConfig;
import com.example.demo.utils.DealStrSub;
import com.example.demo.utils.JsUtil;
import com.example.demo.utils.VideoUtils;
import com.gargoylesoftware.htmlunit.html.*;
import it.sauronsoftware.jave.EncoderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import static com.example.demo.config.MyappConfig.*;
import static com.example.demo.utils.WebUtil.UnitPage;

@SpringBootApplication
@Slf4j
@EnableConfigurationProperties({MyappConfig.class})
@EnableScheduling
public class DemoApplication {
    public static String BOT_NAME = "porn_91Bot";
    public static String PROXY_HOST = "127.0.0.1" /* proxy host */;
    public static Integer PROXY_PORT = 10809 /* proxy port */;
    //合并好的文件后缀
    public static String VIDEO_MP4 = ".mp4";
    public static String VIDEO_JPEG = ".jpeg";
    public static MyAmazingBot bot;
    public static String env=null;

    public static void main(String[] args) throws TelegramApiException, ExecutionException, InterruptedException, IOException, EncoderException {
        ConfigurableApplicationContext ctx = SpringApplication.run(DemoApplication.class, args);
        env=ctx.getEnvironment().getActiveProfiles()[0];
        log.info("当前环境：===================" + env);
        log.info("ffmpeg路径：：" + FFMPEG_ROOT);
        initBot();
    }

    /**
     *   每天上午5点15分执行
     */
    @Scheduled(cron = "0 15 5 ? * * ")
    public void scheduledTask() throws TelegramApiException, EncoderException, IOException, ExecutionException, InterruptedException {
        System.out.println("任务执行时间：" + LocalDateTime.now());
        java.util.logging.Logger.getLogger("org.apache.http.wire").setLevel(Level.OFF);
        HtmlPage htmlPage = UnitPage("https://91porn.com/index.php");
        log.info("---------------------------------------------------");
        HtmlPage page = htmlPage.getPage();
        List<Object> byXPath = page.getByXPath("//*[@id=\"wrapper\"]/div[1]/div[2]/div/div/div");
        for (int i = 0, byXPathSize = byXPath.size(); i < byXPathSize; i++) {
            itemDownLoad(byXPath, i);
        }
        log.info("==================所有任务完成!!!!!!!!!!!!!!!!!!!!!!!!!!!!!===============================");
    }


    //任务执行完成，退出
    public static void exitApplication(ConfigurableApplicationContext context) {
        int exitCode = SpringApplication.exit(context, () -> 0);
        System.exit(exitCode);
    }


    private static void initBot() {
        //初始化 BOT
        TelegramBotsApi botsApi = null;
        try {
            botsApi = new TelegramBotsApi(DefaultBotSession.class);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        DefaultBotOptions botOptions = new DefaultBotOptions();
        if (ON_PROXY) {
            log.info("代理开启！！！！！！！！！！！！！！！！！！！");
            botOptions.setProxyHost(PROXY_HOST);
            botOptions.setProxyPort(PROXY_PORT);
            botOptions.setProxyType(DefaultBotOptions.ProxyType.HTTP);
        }
        bot = new MyAmazingBot(botOptions);
        try {
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }


    private static void itemDownLoad(List<Object> byXPath, int i) throws IOException, InterruptedException, ExecutionException, TelegramApiException, EncoderException {
        Object o = byXPath.get(i);
        HtmlDivision item = (HtmlDivision) o;
        DomNodeList<HtmlElement> a = item.getElementsByTagName("a");
        DomNode domNode = item.querySelector(".well a  .video-title");
        String itemStr = item.asXml();
        String rgex1 = "收藏:</span>(.*?)<br/>";
        itemStr = itemStr.replaceAll("\\s*", "");
        String shoucang = DealStrSub.getSubUtilSimple(itemStr, rgex1);


        String href = a.get(0).getAttribute("href");
        String videoName = domNode.getTextContent().replace("[原创]", "");
        log.info(href);
        String rgex0 = "strencode2\\((.*?)\\)\\)";
        String substring1 = DealStrSub.getSubUtilSimple(UnitPage(href).asXml(), rgex0);
        //调用js解码地址
        String strencode = JsUtil.strencode(substring1);
        String rgex = "source src='(.*?)\\?st";
        String realVideoUrl = DealStrSub.getSubUtilSimple(strencode, rgex);
        //真实地址
        log.warn("真实地址：" + realVideoUrl);
        String realPath="";
        if (env.equals("dev")){
             realPath = FILE_ROOT + videoName + "\\";
        }else {
            realPath = FILE_ROOT + videoName + "/";
        }

        File saveDir = new File(FILE_ROOT + videoName);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
         //转mp4
        VideoUtils.convertToMp42(realVideoUrl,
                new File(realPath + videoName + VIDEO_JPEG),
                realPath,videoName);
        

        bot.sendVideo2(realPath + videoName + VIDEO_JPEG,realPath,
                " 收藏: "+ shoucang,null);

    }


}
