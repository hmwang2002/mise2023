# 使用 adoptopenjdk 镜像作为基础镜像
FROM openjdk:17

# 设置工作目录
WORKDIR /app

# 将本地的 JAR 文件复制到容器中
COPY post-center.jar /app/post-center.jar
COPY ../application.yml /app/application.yml
COPY ./src/main/resources/application-prod.yml /app/application-prod.yml

# 暴露应用程序的端口（如果应用程序监听特定端口）
EXPOSE 8081

# 启动 Java 应用程序
CMD ["java", "-jar", "post-center.jar", "--spring.config.location=/app/application.yml"]
