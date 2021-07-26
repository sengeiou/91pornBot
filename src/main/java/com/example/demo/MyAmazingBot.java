package com.example.demo;


import com.example.demo.utils.DealStrSub;
import com.example.demo.utils.JsUtil;
import com.example.demo.utils.VideoUtils;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.MultimediaInfo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.DemoApplication.*;
import static com.example.demo.config.MyappConfig.*;
import static com.example.demo.utils.WebUtil.UnitPage;

/**
 * @author jw131
 */
@Slf4j
public class MyAmazingBot extends TelegramLongPollingBot {

    public MyAmazingBot(DefaultBotOptions options) {
        super(options);
    }

    public void sendVideo(String fileName, String filePath, String JpgPath, String FileRoot, String shoucang,String chatId) throws IOException, EncoderException {
        File mediaFile = new File(filePath);
        InputFile video = new InputFile(mediaFile);
        MultimediaInfo videoInfo = VideoUtils.getVideoInfo(mediaFile);
        int height = videoInfo.getVideo().getSize().getHeight();
        int width = videoInfo.getVideo().getSize().getWidth();
        //秒
        Integer integer = new Integer((int) Math.ceil(videoInfo.getDuration() / 1000));
        InputFile inputFile = new InputFile(new File(JpgPath));
        SendVideo build = SendVideo.builder().
                chatId(CHAT_ID)
                .video(video)
                .duration(integer)
                .height(height)
                .width(width)
                .supportsStreaming(true)
                .thumb(inputFile)
                .caption(fileName + shoucang)
//                    .parseMode("Markdown")
                .build();
        if (chatId!=null){
            build.setChatId(chatId);
        }
        try {
            execute(build);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        } finally {
            FileUtils.deleteDirectory(new File(FileRoot));
        }


    }


    public void sendVideo2( String JpgPath, String FileRoot, String shoucang,String chatId) throws IOException, EncoderException {


        File file = new File(FileRoot);
        File[] filesInit = file.listFiles();
        String exegc = "^.+" + "mp4" + "$";
        //文件名称排序
        List<File> files = Arrays.stream(filesInit).sorted().collect(Collectors.toList());
        for (int i = 0; i < files.size(); i++) {
            String name = files.get(i).getName();
            if(name.matches(exegc)){
                log.info("包含mp4"+name+",发送.....................");
                File mediaFile = new File(FileRoot + name);
                InputFile video = new InputFile(mediaFile);
                MultimediaInfo videoInfo = VideoUtils.getVideoInfo(mediaFile);
                int height = videoInfo.getVideo().getSize().getHeight();
                int width = videoInfo.getVideo().getSize().getWidth();
                //秒
                Integer integer = new Integer((int) Math.ceil(videoInfo.getDuration() / 1000));
                InputFile inputFile = new InputFile(new File(JpgPath));
                SendVideo build = SendVideo.builder().
                        chatId(CHAT_ID)
                        .video(video)
                        .duration(integer)
                        .height(height)
                        .width(width)
                        .supportsStreaming(true)
                        .thumb(inputFile)
                        .caption(name + shoucang)
//                    .parseMode("Markdown")
                        .build();
                if (chatId!=null){
                    build.setChatId(chatId);
                }
                try {
                    execute(build);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            }
        }
        FileUtils.deleteDirectory(new File(FileRoot));

    }


    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {

            String text = update.getMessage().getText();
            SendChatAction sendChatAction = new SendChatAction();
            String chatId = update.getMessage().getChatId().toString();
            sendChatAction.setChatId(chatId);

            if (text.equals("/start")) {
                SendMessage sendMessage = new SendMessage(chatId, "向我发送91视频链接，下载视频,视频最大50M,超过50M分割发送");
                execute(sendMessage);
                sendChatAction.setAction(ActionType.TYPING);
            }  else {
                sendChatAction.setAction(ActionType.RECORDVIDEONOTE);
                String rgex0 = "strencode2\\((.*?)\\)\\)";
                HtmlPage htmlPage = UnitPage(text);
                String soap = htmlPage.asXml();
                DomNode domNode = htmlPage.querySelector(".login_register_header");
                String videoName = domNode.getTextContent();
                //去除html多余空格，换行符
                videoName = videoName.replaceAll("\\s*", "");
                //调用js解码地址
                String strencode = JsUtil.strencode(DealStrSub.getSubUtilSimple(soap, rgex0));
                String rgex = "source src='(.*?)\\?st";
                String realVideoUrl = DealStrSub.getSubUtilSimple(strencode, rgex);
                //真实地址
                log.warn("真实地址：" + realVideoUrl);
                SendMessage sendMessage = new SendMessage(chatId,
                        "正在下载,请等待下载完成.... 视频真实地址（地址为空重新发送链接给我，有时候网络原因获取不到）："
                        + realVideoUrl);
                execute(sendMessage);
                String realPath="";
                if (DemoApplication.env.equals("dev")){
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
                        "*",chatId);
                log.info("已回复");
            }

            try {
                 execute(sendChatAction);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

}
