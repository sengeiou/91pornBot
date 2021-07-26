package com.example.demo.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @program: demo
 * @description:
 * @author: Jia Wei
 * @create: 2021-07-18 08:20
 **/
@Data
@ConfigurationProperties(prefix = "myappconfig")
@Component
public class MyappConfig {

    public static Boolean ON_PROXY;

    public static String FFMPEG_ROOT;

    public static String MP4BOX_ROOT;

    public static String FILE_ROOT;

    public static String CHAT_ID;

    public static String BOT_TOKEN;


    @Value("${myappconfig.mp4box_root}")
    public void setMp4boxRoot(String mp4boxRoot) {
        MP4BOX_ROOT = mp4boxRoot;
    }


    @Value("${myappconfig.bot_token}")
    public void setBotToken(String botToken) {
        BOT_TOKEN = botToken;
    }

    @Value("${myappconfig.chat_id}")
    public void setChatId(String chatId) {
        CHAT_ID = chatId;
    }

    @Value("${myappconfig.fileroot}")
    public void setFileRoot(String fileRoot) {
        FILE_ROOT = fileRoot;
    }

    @Value("${myappconfig.ffmpeg_root}")
    public void setFfmpegRoot(String ffmpegRoot) {
        FFMPEG_ROOT = ffmpegRoot;
    }

    @Value("${myappconfig.proxy_on}")
    public void setOnProxy(Boolean proxy_on) {
        ON_PROXY = proxy_on;
    }

}
