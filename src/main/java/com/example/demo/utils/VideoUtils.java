package com.example.demo.utils; /**
* @Author:psw
* @Description:获取视频宽高大小时间工具类
*/

import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

import static com.example.demo.DemoApplication.VIDEO_MP4;
import static com.example.demo.DemoApplication.env;
import static com.example.demo.config.MyappConfig.FFMPEG_ROOT;
import static com.example.demo.config.MyappConfig.MP4BOX_ROOT;

@Slf4j
public class VideoUtils {

    public static it.sauronsoftware.jave.MultimediaInfo getVideoInfo(File source) throws EncoderException {
        Encoder encoder = new Encoder();
        return encoder.getInfo(source);
    }


    public static void convertToMp42(String url, File jpgPath,String fileroot,String filename) throws IOException, InterruptedException {

        FFmpeg  ffmpeg  = new FFmpeg(FFMPEG_ROOT+"ffmpeg");
        FFprobe ffprobe = new FFprobe(FFMPEG_ROOT+"ffprobe");

        //时长 s
        FFmpegProbeResult probe = ffprobe.probe(url);
        FFmpegFormat info = probe.getFormat();
        double duration = info.duration;
        //封面信息保存
        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
        FFmpegBuilder builderJpg = new FFmpegBuilder()
                .overrideOutputFiles(true)
                .setInput(url)
                .addOutput(jpgPath.getAbsolutePath())
                .addExtraArgs("-vframes","1")
                .done();
        FFmpegJob job1 = executor.createJob(builderJpg);
        job1.run();

        //整体下载
        String path = fileroot + filename + VIDEO_MP4;
        FFmpegOutputBuilder fFmpegOutputBuilder = new FFmpegBuilder()
                    .overrideOutputFiles(true)
                    .setInput(url)
                    .addOutput(path)
                    .setFormat("mp4")
                    .addExtraArgs(
                            //编码解码器
                            "-c:v", "libx264",
                            //质量默认23，越小质量越好
                            "-crf", "22",
                            //限制cpu在50%左右
                            "-threads", "2"
                    );

            FFmpegBuilder builder;
            if (duration>240){
                //大于4分钟截取前10秒
                builder = fFmpegOutputBuilder.addExtraArgs("-ss", "00:00:10").done();
            }else {
                builder= fFmpegOutputBuilder.done();
            }
            FFmpegJob job = executor.createJob(builder);
            job.run();
        //视频过大切割
        Map<String, Object> voideMsg = VideoUtils.getVoideMsg(path);
        double size =(double) voideMsg.get("size");
        log.warn("大小："+size+"M");
        if (size>=50){
            //按50M大小分割
            String strCmd = MP4BOX_ROOT+" -splits 51200 "+path;
            Process process = getExec(strCmd);
            new RunThread(process.getInputStream(), "INFO").start();
            new RunThread(process.getErrorStream(),"WARN").start();
            int value = process.waitFor();
            if(value == 0) {
                log.info("分割成功");
            } else {
                log.info("分割失败");
            }
            new File(path).delete();
        }


    }

    private static Process getExec(String strCmd) throws IOException {
        String command;
        if (env.equals("dev")){
            command = "cmd /c ";
        }else {
            command = "";
        }

        return Runtime.getRuntime().exec(command + strCmd);
    }

//    public static void main(String[] args) throws IOException, InterruptedException {
//
//        String strCmd ="E:\\Utils\\GPAC\\"+"mp4box -splits 51200"+ " F:\\m3u8JavaTest\\[原创]6小时车轮战，都累了\\aa.mp4";
//        Process process = Runtime.getRuntime().exec(strCmd);
//        BufferedReader strCon = new BufferedReader(new InputStreamReader(process.getInputStream()));
//        String line;
//        while ((line = strCon.readLine()) != null) {
//            System.out.println("java print:"+line);
//        }
//
//    }


    /**
     * @param path
     * @return Map
     */
    public static Map<String, Object> getVoideMsg(String path){
        
        Map<String, Object> map = new HashMap<String, Object>();
        File file = new File(path);
        Encoder encoder = new Encoder();
        FileChannel fc= null;

        if(file != null){
            try {
                it.sauronsoftware.jave.MultimediaInfo m = encoder.getInfo(file);
                FileInputStream fis = new FileInputStream(file);
                fc= fis.getChannel();
                BigDecimal fileSize = new BigDecimal(fc.size());
                BigDecimal divide = fileSize.divide(new BigDecimal(1048576), 2, RoundingMode.HALF_UP);

                map.put("height", m.getVideo().getSize().getHeight());
                map.put("width", m.getVideo().getSize().getWidth());
                map.put("size", divide.doubleValue());
                map.put("format", m.getFormat());
            }catch (Exception e) {
                e.printStackTrace();
            }finally {
                if (null!=fc){
                    try {
                        fc.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        
        return map;    
    }
}