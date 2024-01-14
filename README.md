# README

2023年秋南京大学软件学院移动互联网软件工程大作业——PaoPao微社区。

## 移动端

移动端部分使用原生安卓开发，功能如下。

![image-20240114112331974](http://kiyotakawang.oss-cn-hangzhou.aliyuncs.com/img/image-20240114112331974.png)

![image-20240114112232204](http://kiyotakawang.oss-cn-hangzhou.aliyuncs.com/img/image-20240114112232204.png)

![image-20240114112255116](http://kiyotakawang.oss-cn-hangzhou.aliyuncs.com/img/image-20240114112255116.png)

## 后端

### 后端架构

后端架构：基于Spring Cloud Alibaba开发的微服务系统。

![image-20240114112529872](http://kiyotakawang.oss-cn-hangzhou.aliyuncs.com/img/image-20240114112529872.png)

### 数据存储

![image-20240114112619917](http://kiyotakawang.oss-cn-hangzhou.aliyuncs.com/img/image-20240114112619917.png)

### 运行要求

因为项目使用到了阿里云OSS和SMS服务，因此需要向usercenter数据库的aliconfig表中添加自己的阿里云账号的accesskey和access-secret，方可保证成功初始化Aliconfig Bean。

Nacos和sentinel的相关运行jar包或文件请自行下载。运行后端项目前请确保Nacos已经成功启动。

## 大语言模型

本项目提供大语言模型生成帖子功能的实现。如果您需要接入大模型，请自行在mise2023-frontend项目下ui/chat/chatActivity.java文件中添加部署了大语言模型连接服务的服务器地址。

相关python后台代码可以参考mise2023-Article-Generation-backend文件夹下的main.py。

![image-20240114113343307](http://kiyotakawang.oss-cn-hangzhou.aliyuncs.com/img/image-20240114113343307.png)

