# 基于telegramBot、Ffmpeg、MP4Box、UnitPage的爬取视频、发送视频的机器人
91视频爬取


## 特点

破解91视频的播放限制、理论上可以无限下载

切除长视频(4分钟)播放开始的静态帧(10秒)

由于电报Bot单次发送最大50M文件，切割发送视频(MP4Box大法好!!!)


## 环境

jdk1.8

电报机器人申请

MP4Box、Ffmpeg安装。

MP4Box 安装
https://gpac.wp.imt.fr/downloads/
https://github.com/gpac/gpac/wiki/GPAC-Build-Guide-for-Linux


配置在yml

```
        #是否开启代理
        proxy_on: true
        #ffmpeg 路径
        ffmpeg_root: E:\\Utils\\ffmpeg\\bin\\
        #MP4Box 路径
        mp4box_root: E:\\Utils\\GPAC\\mp4box
        #临时文件路径
        fileroot: "F:\\m3u8JavaTest\\"
        #发送telegram目的id
        chat_id: "****"
        bot_token: "************************"

```
